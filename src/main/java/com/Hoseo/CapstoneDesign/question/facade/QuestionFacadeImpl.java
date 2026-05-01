package com.Hoseo.CapstoneDesign.question.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.image.service.ImageService;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
import com.Hoseo.CapstoneDesign.question.entity.Question;
import com.Hoseo.CapstoneDesign.question.factory.QuestionDtoFactory;
import com.Hoseo.CapstoneDesign.question.service.QuestionService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class QuestionFacadeImpl implements QuestionFacade {

    private final UserService userService;
    private final QuestionService questionService;
    private final ImageService imageService; // 이미지 도메인 연결

    @Override
    @Transactional(readOnly = false)
    public QuestionDetailResponse createQuestion(Long userId, QuestionCreateRequest request) {
        // 1. 유저 정보 조회
        Users writer = userService.getReferenceById(userId);

        // 2. 질문글 생성 및 저장
        Question savedQuestion = questionService.createQuestion(
                writer, request.title(), request.content(), request.tags()
        );

        // 3. 업로드된 이미지들이 있다면, 이 질문글의 소속으로 확정 짓기 (핵심 결합 로직)
        if (request.imageIds() != null && !request.imageIds().isEmpty()) {
            imageService.attachImagesToTarget(request.imageIds(), savedQuestion.getQuestionId(), "QUESTION");
        }

        // 4. 저장된 엔티티를 DTO로 변환하여 컨트롤러로 반환
        return QuestionDtoFactory.toDetailResponse(savedQuestion, List.of());
    }
}