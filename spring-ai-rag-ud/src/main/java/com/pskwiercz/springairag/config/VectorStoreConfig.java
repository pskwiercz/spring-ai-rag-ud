package com.pskwiercz.springairag.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
public class VectorStoreConfig {
    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel, VectorStoreProperties vectorStoreProperties) {
        var store = new SimpleVectorStore(embeddingModel);
        File vectorStoreFile = new File(vectorStoreProperties.getVectorStorePath());

        if (vectorStoreFile.exists()) {
            store.load(vectorStoreFile);
        } else {
            log.debug("Creating a new vector store");
            vectorStoreProperties.getDocumentsToDownload().forEach(document -> {
                log.debug("Downloading: " + document.getFilename());
                TikaDocumentReader reader = new TikaDocumentReader(document);
                List<Document> documents = reader.get();
                TextSplitter textSplitter = new TokenTextSplitter();
                store.add(textSplitter.apply(documents));
                store.save(vectorStoreFile);
            });
        }
        return store;
    }
}
