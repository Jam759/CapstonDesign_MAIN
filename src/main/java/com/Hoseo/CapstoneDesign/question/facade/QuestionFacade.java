package com.Hoseo.CapstoneDesign.question.facade;

import com.Hoseo.CapstoneDesign.question.dto.request.AnswerCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;

public interface QuestionFacade {
    QuestionDetailResponse createQuestion(Long userId, QuestionCreateRequest request);

    // 답변 생성 메서드 추가
    AnswerResponse createAnswer(Long userId, Long questionId, AnswerCreateRequest request);
}