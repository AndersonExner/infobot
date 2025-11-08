package com.example.infobot.web;

import com.example.infobot.service.IngestionService;
import com.example.infobot.service.QaService;
import com.example.infobot.web.dto.ChatRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RagController {

    private final IngestionService ingestionService;
    private final QaService qaService;

    public RagController(IngestionService ingestionService, QaService qaService) {
        this.ingestionService = ingestionService;
        this.qaService = qaService;
    }

    @PostMapping("/ingest")
    public Map<String, Object> ingest() throws Exception {
        int count = ingestionService.ingestAll();
        return Map.of(
                "indexed", count,
                "collection", "in-memory"
        );
    }

    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ask(@RequestBody ChatRequest request) {
        String answer = qaService.ask(request.getQuestion());
        return Map.of("answer", answer);
    }
}
