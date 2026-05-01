package com.Hoseo.CapstoneDesign.question.factory;

import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
import com.Hoseo.CapstoneDesign.question.entity.Question;

import java.util.List;

public class QuestionDtoFactory {

    private QuestionDtoFactory() {}

    // 엔티티를 프론트엔드 반환용 DTO로 변환합니다.
    public static QuestionDetailResponse toDetailResponse(Question question, List<AnswerResponse> answers) {
        return new QuestionDetailResponse(
                question.getQuestionId(),
                question.getTitle(),
                question.getWriter().getIdentityId().toString(), // 임시로 IdentityId 사용 (추후 닉네임 등으로 변경 가능)
                question.getCreatedAt(),
                question.getContent(),
                question.getTags(),
                question.getViews(),
                answers
        );
    }
}