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

import java.util.List;

@Facade
@RequiredArgsConstructor
public class QuestionFacadeImpl implements QuestionFacade {

    private final UserService userService;
    private final QuestionService questionService;
    private final ImageService imageService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    // 전체 질문 목록 조회 (더미 제거)
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

    // 질문 상세 단건 조회 (더미 제거 및 조회수 증가 포함)
    @Override
    @Transactional(readOnly = false) // 조회수를 증가시키므로 읽기 전용 해제
    public QuestionDetailResponse getQuestion(Long questionId) {
        Question question = questionService.getQuestionWithViews(questionId);

        List<AnswerResponse> answers = answerRepository.findAllByQuestion(question).stream()
                .map(QuestionDtoFactory::toAnswerResponse)
                .toList();

        return QuestionDtoFactory.toDetailResponse(question, answers);
    }

    @Override
    @Transactional(readOnly = false)
    public QuestionDetailResponse createQuestion(Long userId, QuestionCreateRequest request) {
        Users writer = userService.getReferenceById(userId);

        Question savedQuestion = questionService.createQuestion(
                writer, request.title(), request.content(), request.tags()
        );

        if (request.imageIds() != null && !request.imageIds().isEmpty()) {
            imageService.attachImagesToTarget(request.imageIds(), savedQuestion.getQuestionId(), "QUESTION");
        }

        return QuestionDtoFactory.toDetailResponse(savedQuestion, List.of());
    }

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

    @Override
    @Transactional(readOnly = false)
    public QuestionDetailResponse updateQuestion(Long userId, Long questionId, QuestionUpdateRequest request) {
        Question updatedQuestion = questionService.updateQuestion(questionId, userId, request.title(), request.content(), request.tags());

        if (request.imageIds() != null && !request.imageIds().isEmpty()) {
            imageService.attachImagesToTarget(request.imageIds(), updatedQuestion.getQuestionId(), "QUESTION");
        }

        return QuestionDtoFactory.toDetailResponse(updatedQuestion, List.of());
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteQuestion(Long userId, Long questionId) {
        questionService.deleteQuestion(questionId, userId);
    }
}