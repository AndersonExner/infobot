package com.example.infobot.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcrConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract t = new Tesseract();
        t.setDatapath("C:/Users/anderson.exner/AppData/Local/Programs/Tesseract-OCR/tessdata");
        t.setLanguage("por+eng");
        t.setPageSegMode(6);

        return t;
    }
}
