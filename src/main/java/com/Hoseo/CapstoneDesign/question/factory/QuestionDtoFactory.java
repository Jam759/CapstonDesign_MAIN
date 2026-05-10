package com.Hoseo.CapstoneDesign.question.factory;

import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionSummaryResponse;
import com.Hoseo.CapstoneDesign.question.entity.Answer;
import com.Hoseo.CapstoneDesign.question.entity.Question;

import java.util.List;

public class QuestionDtoFactory {

    private QuestionDtoFactory() {}

    public static QuestionDetailResponse toDetailResponse(Question question, List<AnswerResponse> answers) {
        return new QuestionDetailResponse(
                question.getQuestionId(),
                question.getTitle(),
                question.getWriter().getServiceNickname() != null ? question.getWriter().getServiceNickname() : "unknown",
                question.getCreatedAt(),
                question.getContent(),
                question.getTags(),
                question.getViews(),
                answers
        );
    }

    // 목록 조회용 DTO 변환 메서드 (답변 개수를 함께 받습니다)
    public static QuestionSummaryResponse toSummaryResponse(Question question, int repliesCount) {
        return new QuestionSummaryResponse(
                question.getQuestionId(),
                question.getTitle(),
                question.getWriter().getServiceNickname() != null ? question.getWriter().getServiceNickname() : "unknown",
                question.getCreatedAt(),
                repliesCount,
                question.getViews(),
                question.getTags()
        );
    }

    // 답변 엔티티를 응답 DTO로 변환합니다.
    public static AnswerResponse toAnswerResponse(Answer answer) {
        return new AnswerResponse(
                answer.getAnswerId(),
                answer.getWriter().getServiceNickname() != null ? answer.getWriter().getServiceNickname() : "unknown",
                answer.getCreatedAt(),
                false,
                answer.getContent()
        );
    }
}