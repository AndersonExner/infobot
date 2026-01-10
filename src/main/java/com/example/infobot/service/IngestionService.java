package com.example.infobot.service;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IngestionService {

    private static final Logger log =
            LoggerFactory.getLogger(IngestionService.class);

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

    /** Lê todos os PDFs da pasta configurada e indexa apenas os novos/alterados. */
    public int ingestAll() throws Exception {
        File dir = new File(docsDir);
        dir.mkdirs();

        File[] pdfs = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfs == null || pdfs.length == 0) {
            log.info("📂 Nenhum PDF encontrado em {}", dir.getAbsolutePath());
            return 0;
        }

        int indexed = 0;
        for (File pdf : pdfs) {
            indexed += ingestPdfIfNeeded(pdf);
        }

        log.info("✅ Ingestão finalizada. Total de chunks adicionados: {}", indexed);
        return indexed;
    }

    /** Ingerir PDF apenas se ainda não foi indexado (por hash). */
    private int ingestPdfIfNeeded(File pdf) throws Exception {
        String hash = checksum(pdf);

        // Observação:
        // Se seu EmbeddingStore não suporta busca por metadata,
        // isso ainda funciona como base para futura evolução.
        log.info("📄 Processando PDF: {} (hash={})", pdf.getName(), hash);

        return ingestPdf(pdf, hash);
    }

    /** Lê e indexa um único PDF. */
    private int ingestPdf(File pdf, String hash) throws Exception {
        int added = 0;

        try (PDDocument doc = PDDocument.load(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int pages = doc.getNumberOfPages();

            for (int p = 1; p <= pages; p++) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String text = stripper.getText(doc);

                if (text == null || text.trim().isEmpty()) continue;

                String normalized = text.replaceAll("\\s+", " ").trim();
                List<String> chunks = split(normalized, chunkSize, chunkOverlap);

                for (String chunk : chunks) {
                    Embedding embedding =
                            embeddingModel.embed(chunk).content();

                    Metadata metadata = Metadata.from(Map.of(
                            "source", pdf.getName(),
                            "page", String.valueOf(p),
                            "hash", hash
                    ));

                    TextSegment segment =
                            TextSegment.from(chunk, metadata);

                    store.add(embedding, segment);
                    added++;
                }
            }
        }

        log.info("➕ {} chunks adicionados para {}", added, pdf.getName());
        return added;
    }

    /** Calcula hash MD5 do arquivo (controle de reindexação). */
    private static String checksum(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                md.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(md.digest());
    }

    /** Chunking por palavras com overlap (melhor para RAG). */
    private static List<String> split(String text, int size, int overlap) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (current.length() + word.length() > size) {
                chunks.add(current.toString().trim());

                String overlapText = current.substring(
                        Math.max(0, current.length() - overlap)
                );
                current = new StringBuilder(overlapText);
            }

            current.append(word).append(" ");
        }

        if (!current.isEmpty()) {
            chunks.add(current.toString().trim());
        }

        return chunks;
    }
}
