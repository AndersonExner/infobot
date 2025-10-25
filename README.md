# 🤖 InfoBot — RAG com Spring Boot e Ollama
*(Português / English)*

---

## 🇧🇷 Descrição

O **InfoBot** é um projeto de estudo que implementa um agente de **IA local** baseado em **RAG** (*Retrieval-Augmented Generation*), capaz de **ler arquivos PDF** e **responder perguntas** com base no conteúdo desses documentos.

> 💡 Tudo roda **localmente**, sem depender de APIs externas ou serviços pagos.  
> Desenvolvido com **Java 17**, **Spring Boot**, **LangChain4j** e **Ollama**.

---

## EN Description

**InfoBot** is a study project that implements a local **AI agent** using **RAG** (*Retrieval-Augmented Generation*), capable of **reading PDF files** and **answering questions** based on their content.

> 💡 Runs entirely **offline**, with no external APIs or paid services required.  
> Built with **Java 17**, **Spring Boot**, **LangChain4j**, and **Ollama**.

---

## 🚀 Funcionalidades / Features

- 📄 Leitura e indexação de arquivos PDF
- 🧠 Geração de *embeddings* com `nomic-embed-text` (Ollama)
- 🔍 Busca semântica e recuperação de contexto relevante
- 💬 Geração de respostas contextuais com `llama3.1`
- 📚 Citação automática das fontes (arquivo e página)

---

## 🧱 Tecnologias / Tech Stack

| Componente | Descrição / Description |
|-------------|-------------------------|
| **Java 17** | Linguagem principal / Main language |
| **Spring Boot 3.3.4** | Framework web |
| **LangChain4j** | Integração com LLMs e embeddings |
| **Ollama** | Execução local dos modelos |
| **Apache PDFBox** | Leitura e extração de texto de PDFs |
| **InMemoryEmbeddingStore** | Armazenamento vetorial simples (memória) |

---

## ⚙️ Requisitos / Requirements

- **Java 17+** (Eclipse Temurin recomendado)
- **Maven 3.9+**
- **Ollama** instalado e rodando localmente

### Modelos necessários / Required models
```bash
ollama pull llama3.1
ollama pull nomic-embed-text
