package com.Hoseo.CapstoneDesign.question.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.image.service.ImageService;
import com.Hoseo.CapstoneDesign.question.dto.request.AnswerCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionUpdateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionSummaryResponse;
import com.Hoseo.CapstoneDesign.question.entity.Answer;
import com.Hoseo.CapstoneDesign.question.entity.Question;
import com.Hoseo.CapstoneDesign.question.exception.QuestionErrorCode;
import com.Hoseo.CapstoneDesign.question.exception.QuestionException;
import com.Hoseo.CapstoneDesign.question.factory.QuestionDtoFactory;
import com.Hoseo.CapstoneDesign.question.factory.QuestionEntityFactory;
import com.Hoseo.CapstoneDesign.question.repository.AnswerRepository;
import com.Hoseo.CapstoneDesign.question.repository.QuestionRepository;
import com.Hoseo.CapstoneDesign.question.service.QuestionService;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import com.Hoseo.CapstoneDesign.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Facade
@RequiredArgsConstructor
public class QuestionFacadeImpl implements QuestionFacade {

    private final UserService userService;
    private final QuestionService questionService;
    private final ImageService imageService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    // 전체 질문 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<QuestionSummaryResponse> getQuestions() {
        return questionService.getAllQuestions().stream()
                .map(q -> {
                    int repliesCount = answerRepository.countByQuestion(q);
                    return QuestionDtoFactory.toSummaryResponse(q, repliesCount);
                })
                .toList();
    }

    // 질문 상세 단건 조회
    @Override
    @Transactional(readOnly = false)
    public QuestionDetailResponse getQuestion(Long questionId) {
        Question question = questionService.getQuestionWithViews(questionId);

        List<AnswerResponse> answers = answerRepository.findAllByQuestion(question).stream()
                .map(QuestionDtoFactory::toAnswerResponse)
                .toList();

        return QuestionDtoFactory.toDetailResponse(question, answers);
    }

    // 새로운 질문을 생성하고, 본문 내에 포함된 마크다운 이미지 ID를 파싱하여 매핑합니다.
    @Override
    @Transactional(readOnly = false)
    public QuestionDetailResponse createQuestion(Long userId, QuestionCreateRequest request) {
        Users writer = userService.getReferenceById(userId);

        Question savedQuestion = questionService.createQuestion(
                writer, request.title(), request.content(), request.tags()
        );

        List<Long> extractedImageIds = extractImageIdsFromContent(request.content());
        if (!extractedImageIds.isEmpty()) {
            imageService.attachImagesToTarget(extractedImageIds, savedQuestion.getQuestionId(), "QUESTION");
        }

        return QuestionDtoFactory.toDetailResponse(savedQuestion, List.of());
    }

    // 특정 질문에 대한 새로운 답변(댓글)을 생성합니다.
    @Override
    @Transactional(readOnly = false)
    public AnswerResponse createAnswer(Long userId, Long questionId, AnswerCreateRequest request) {
        Users writer = userService.getReferenceById(userId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));

        Answer answer = QuestionEntityFactory.createAnswer(request.content(), question, writer);
        Answer savedAnswer = answerRepository.save(answer);

        return new AnswerResponse(
                savedAnswer.getAnswerId(),
                writer.getServiceNickname(),
                savedAnswer.getCreatedAt(),
                false,
                savedAnswer.getContent()
        );
    }

    // 현재 로그인한 사용자가 작성한 질문 목록만 최신순으로 조회합니다.
    @Override
    @Transactional(readOnly = true)
    public List<QuestionSummaryResponse> getMyQuestions(Long userId) {
        Users writer = userService.getReferenceById(userId);

        List<Question> myQuestions = questionRepository.findAllByWriterOrderByCreatedAtDesc(writer);

        return myQuestions.stream()
                .map(q -> {
                    int repliesCount = answerRepository.countByQuestion(q);
                    return QuestionDtoFactory.toSummaryResponse(q, repliesCount);
                })
                .toList();
    }

    // 작성한 기존 질문의 내용(제목, 본문, 태그)을 수정하고 이미지 ID를 재매핑합니다.
    @Override
    @Transactional(readOnly = false)
    public QuestionDetailResponse updateQuestion(Long userId, Long questionId, QuestionUpdateRequest request) {
        Question updatedQuestion = questionService.updateQuestion(questionId, userId, request.title(), request.content(), request.tags());

        List<Long> extractedImageIds = extractImageIdsFromContent(request.content());
        if (!extractedImageIds.isEmpty()) {
            imageService.attachImagesToTarget(extractedImageIds, updatedQuestion.getQuestionId(), "QUESTION");
        }

        return QuestionDtoFactory.toDetailResponse(updatedQuestion, List.of());
    }

    // 본인이 작성한 특정 질문을 삭제합니다.
    @Override
    @Transactional(readOnly = false)
    public void deleteQuestion(Long userId, Long questionId) {
        questionService.deleteQuestion(questionId, userId);
    }

    // 본문에서 정규식을 사용해 ![imageId:101] 패턴의 ID 값을 추출하는 프라이빗 메서드
    private List<Long> extractImageIdsFromContent(String content) {
        List<Long> imageIds = new ArrayList<>();
        if (content == null || content.isBlank()) {
            return imageIds;
        }

        Pattern pattern = Pattern.compile("!\\[imageId:(\\d+)\\]");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            imageIds.add(Long.parseLong(matcher.group(1)));
        }

        return imageIds;
    }
}