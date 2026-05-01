package com.Hoseo.CapstoneDesign.question.facade;

import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;

public interface QuestionFacade {
    QuestionDetailResponse createQuestion(Long userId, QuestionCreateRequest request);
}