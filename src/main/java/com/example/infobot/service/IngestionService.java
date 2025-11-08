package com.example.infobot.service;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IngestionService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;

    @Value("${rag.docsDir}")
    private String docsDir;

    @Value("${rag.chunkSize}")
    private int chunkSize;

    @Value("${rag.chunkOverlap}")
    private int chunkOverlap;

    public IngestionService(EmbeddingModel embeddingModel,
                            EmbeddingStore<TextSegment> store) {
        this.embeddingModel = embeddingModel;
        this.store = store;
    }

    /** Lê todos os PDFs da pasta configurada e indexa. */
    public int ingestAll() throws Exception {
        File dir = new File(docsDir);
        dir.mkdirs();

        File[] pdfs = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfs == null || pdfs.length == 0) {
            return 0;
        }

        int indexed = 0;
        for (File pdf : pdfs) {
            indexed += ingestPdf(pdf);
        }
        return indexed;
    }

    /** Lê e indexa um único PDF. */
    private int ingestPdf(File pdf) throws Exception {
        int added = 0;

        try (PDDocument doc = PDDocument.load(pdf)) {
            int pages = doc.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();

            for (int p = 1; p <= pages; p++) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String text = stripper.getText(doc);

                if (text == null || text.trim().isEmpty()) continue;

                String normalized = text.replaceAll("\\s+", " ").trim();
                List<String> chunks = split(normalized, chunkSize, chunkOverlap);

                for (String chunk : chunks) {
                    Embedding embedding = embeddingModel.embed(chunk).content();

                    Metadata metadata = Metadata.from(Map.of(
                            "source", pdf.getName(),
                            "page", String.valueOf(p)
                    ));

                    TextSegment segment = TextSegment.from(chunk, metadata);

                    store.add(embedding, segment);
                    added++;
                }
            }
        }

        return added;
    }

    /** Quebra o texto em chunks com sobreposição. */
    private static List<String> split(String text, int size, int overlap) {
        List<String> parts = new ArrayList<>();
        int len = text.length();
        int i = 0;

        while (i < len) {
            int end = Math.min(len, i + size);
            parts.add(text.substring(i, end));
            if (end == len) break;
            i = Math.max(0, end - overlap);
        }

        return parts;
    }
}
