package com.Hoseo.CapstoneDesign.question.factory;

import com.Hoseo.CapstoneDesign.question.entity.Answer;
import com.Hoseo.CapstoneDesign.question.entity.Question;
import com.Hoseo.CapstoneDesign.user.entity.Users;

import java.util.List;

public class QuestionEntityFactory {

    private QuestionEntityFactory() {}

    public static Question createQuestion(String title, String content, List<String> tags, Users writer) {
        return Question.builder()
                .title(title)
                .content(content)
                .tags(tags)
                .writer(writer)
                .build();
    }

    public static Answer createAnswer(String content, Question question, Users writer) {
        return Answer.builder()
                .content(content)
                .question(question)
                .writer(writer)
                .build();
    }
}