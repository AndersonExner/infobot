🤖 InfoBot — Local RAG com Spring Boot, Ollama e OCR

(Português / English)

🇧🇷 Descrição

O InfoBot é um projeto de estudo que implementa um agente de IA local baseado em RAG (Retrieval-Augmented Generation), capaz de:

📄 Ler arquivos PDF

🖼️ Extrair texto de imagens via OCR (Tesseract)

🧠 Gerar embeddings localmente

💬 Responder perguntas com base no conteúdo indexado

💡 Tudo roda 100% offline, sem dependência de APIs externas ou serviços pagos.

Desenvolvido com Java 17, Spring Boot, LangChain4j e Ollama.

🇺🇸 Description

InfoBot is a study project that implements a fully local AI agent using RAG (Retrieval-Augmented Generation). It can:

📄 Read and index PDF files

🖼️ Extract text from images using OCR (Tesseract)

🧠 Generate embeddings locally

💬 Answer questions based on indexed document content

💡 Runs entirely offline, with no external APIs or paid services required.

Built with Java 17, Spring Boot, LangChain4j, and Ollama.

🚀 Funcionalidades / Features

📄 Indexação automática de PDFs ao iniciar a aplicação

🖼️ OCR de imagens dentro dos PDFs (prints de tela incluídos)

🧠 Embeddings com nomic-embed-text

🔍 Busca semântica com recuperação Top-K

💬 Geração de respostas com modelo LLM local

📚 Citação automática das fontes (arquivo e página)

🎨 Interface React com:

Efeito "digitando..."

Renderização progressiva da resposta

Scroll automático

Layout fixo estilo chat

🧱 Tecnologias / Tech Stack
Componente	Descrição
Java 17	Linguagem principal
Spring Boot 3.x	API REST
LangChain4j	Integração com LLMs
Ollama	Execução local dos modelos
Apache PDFBox	Extração de texto de PDF
Tesseract OCR	Extração de texto de imagens
React + TypeScript	Interface de chat
InMemoryEmbeddingStore	Armazenamento vetorial em memória
⚙️ Requisitos

Java 17+

Maven 3.9+

Node 18+ (para o frontend)

Ollama instalado

Tesseract OCR instalado

📦 Modelos necessários
ollama pull mistral:7b-instruct-q4_K_M
ollama pull nomic-embed-text

🧠 Arquitetura Simplificada
PDF
├─ Texto extraído (PDFBox)
├─ Texto extraído de imagens (Tesseract OCR)
↓
Chunking
↓
Embeddings (Ollama)
↓
Embedding Store
↓
Busca semântica (Top-K)
↓
Geração de resposta (LLM local)