package com.example.infobot;

import com.example.infobot.service.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class PdfIngestionRunner implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(PdfIngestionRunner.class);

    private final IngestionService ingestionService;

    @Value("${rag.ingest-on-startup:true}")
    private boolean ingestOnStartup;

    public PdfIngestionRunner(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!ingestOnStartup) {
            log.info("⏭️ Ingestão automática de PDFs desativada");
            return;
        }

        log.info("🔄 Iniciando ingestão automática de PDFs...");
        int count = ingestionService.ingestAll();
        log.info("✅ Ingestão automática concluída. Chunks indexados: {}", count);
    }
}
