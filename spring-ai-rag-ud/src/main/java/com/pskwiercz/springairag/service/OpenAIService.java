package com.pskwiercz.springairag.service;

import com.pskwiercz.springairag.model.Answer;
import com.pskwiercz.springairag.model.Question;

public interface OpenAIService {
    Answer getAnswer(Question question);
}
