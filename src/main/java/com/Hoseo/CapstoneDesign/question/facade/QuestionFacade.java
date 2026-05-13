package com.Hoseo.CapstoneDesign.question.facade;

import com.Hoseo.CapstoneDesign.question.dto.request.AnswerCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionUpdateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionFacade {
    // 조회 메서드 선언
    Page<QuestionSummaryResponse> getQuestions(Pageable pageable);
    QuestionDetailResponse getQuestion(Long questionId);

    QuestionDetailResponse createQuestion(Long userId, QuestionCreateRequest request);
    AnswerResponse createAnswer(Long userId, Long questionId, AnswerCreateRequest request);

    // 내 질문 목록, 수정, 삭제 메서드 선언
    List<QuestionSummaryResponse> getMyQuestions(Long userId);
    QuestionDetailResponse updateQuestion(Long userId, Long questionId, QuestionUpdateRequest request);
    void deleteQuestion(Long userId, Long questionId);
}