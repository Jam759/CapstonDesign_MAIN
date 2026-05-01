package com.Hoseo.CapstoneDesign.question.service;

import com.Hoseo.CapstoneDesign.question.entity.Question;
import com.Hoseo.CapstoneDesign.question.factory.QuestionEntityFactory;
import com.Hoseo.CapstoneDesign.question.repository.QuestionRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional
    public Question createQuestion(Users writer, String title, String content, List<String> tags) {
        Question question = QuestionEntityFactory.createQuestion(title, content, tags, writer);
        return questionRepository.save(question);
    }
}