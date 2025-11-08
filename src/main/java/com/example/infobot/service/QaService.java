package com.example.infobot.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QaService {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;

    @Value("${rag.topK}")
    private int topK;

    @Value("${rag.minScore}")
    private double minScore;

    public QaService(ChatLanguageModel chatModel,
                     EmbeddingModel embeddingModel,
                     EmbeddingStore<TextSegment> store) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.store = store;
    }

    public String ask(String question) {
        // 1) Gera embedding da pergunta
        Embedding queryEmbedding = embeddingModel.embed(question).content();

        // 2) Busca os trechos mais relevantes
        // InMemoryEmbeddingStore (versão nova) retorna List<EmbeddingMatch<T>>
        List<EmbeddingMatch<TextSegment>> matches = store.findRelevant(queryEmbedding, topK)
                .stream()
                .filter(m -> m.score() == null || m.score() >= minScore)
                .toList();

        if (matches.isEmpty()) {
            return "Não encontrei informações relacionadas a essa pergunta nos PDFs indexados.";
        }

        // 3) Monta o contexto com fontes
        String context = matches.stream()
                .map(m -> {
                    TextSegment segment = m.embedded();
                    String source = segment.metadata("source");
                    String page = segment.metadata("page");
                    StringBuilder sb = new StringBuilder();

                    if (source != null) {
                        sb.append("Fonte: ").append(source);
                        if (page != null) {
                            sb.append(" (página ").append(page).append(")");
                        }
                        sb.append("\n");
                    }

                    sb.append(segment.text());
                    return sb.toString();
                })
                .collect(Collectors.joining("\n---\n"));

        // 4) Prompt sem text block (pra não dar erro de aspas)
        String prompt =
                "Responda APENAS com base no CONTEXTO abaixo.\n" +
                        "Se a resposta não estiver clara no contexto, diga que não encontrou no material.\n\n" +
                        "Pergunta:\n" +
                        question + "\n\n" +
                        "CONTEXTO:\n" +
                        context + "\n\n" +
                        "Requisitos:\n" +
                        "- Responda em português.\n" +
                        "- Seja objetivo.\n" +
                        "- Sempre cite as fontes (arquivo e página) usadas.";

        // 5) Gera resposta usando o LLM
        return chatModel.generate(prompt);
    }
}
