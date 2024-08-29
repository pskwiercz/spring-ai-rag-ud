package com.pskwiercz.springairag.service;

import com.pskwiercz.springairag.model.Answer;
import com.pskwiercz.springairag.model.Question;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    final ChatClient chatClient;
    final SimpleVectorStore vectorStore;

    @Value("classpath:/templates/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    @Value("classpath:/templates/rag-prompt-template-meta.st")
    private Resource ragPromptTemplateMeta;

    public OpenAIServiceImpl(ChatClient.Builder chatClient, SimpleVectorStore vectorStore) {
        this.chatClient = chatClient.build();
        this.vectorStore = vectorStore;
    }

    @Override
    public Answer getAnswer(Question question) {
        List<Document> documents = vectorStore
                .similaritySearch(SearchRequest.query(question.question()).withTopK(5));

        List<String> contentList = documents.stream().map(Document::getContent).toList();
        contentList.forEach(System.out::println);

        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplateMeta);
        Prompt prompt = promptTemplate.create(Map.of("input", question.question(), "documents",
                String.join("\n", contentList)));

        return new Answer(chatClient.prompt(prompt).call().content());
    }
}
