package com.Hoseo.CapstoneDesign.question.facade;

import com.Hoseo.CapstoneDesign.global.annotation.Facade;
import com.Hoseo.CapstoneDesign.image.service.ImageService;
import com.Hoseo.CapstoneDesign.question.dto.request.AnswerCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.request.QuestionCreateRequest;
import com.Hoseo.CapstoneDesign.question.dto.response.AnswerResponse;
import com.Hoseo.CapstoneDesign.question.dto.response.QuestionDetailResponse;
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
    private final ImageService imageService; // 이미지 도메인 연결

    // 답변 생성을 위해 필요한 Repository 두 개를 추가로 주입받습니다.
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    @Transactional(readOnly = false) // 데이터 변경(insert)이 일어나므로 트랜잭션을 엽니다.
    public QuestionDetailResponse createQuestion(Long userId, QuestionCreateRequest request) {
        // 1. 유저 정보 조회 (data.sql 유저 혹은 실제 가입 유저 모두 호환)
        Users writer = userService.getReferenceById(userId);

        // 2. 질문글 생성 및 DB 저장 (Service 계층에 위임)
        Question savedQuestion = questionService.createQuestion(
                writer, request.title(), request.content(), request.tags()
        );

        // 3. 업로드된 임시 이미지들이 있다면, 이 질문글의 소속(QUESTION)으로 확정 짓기
        if (request.imageIds() != null && !request.imageIds().isEmpty()) {
            imageService.attachImagesToTarget(request.imageIds(), savedQuestion.getQuestionId(), "QUESTION");
        }

        // 4. 저장된 엔티티를 프론트엔드가 요구하는 DTO 규격으로 변환하여 반환
        return QuestionDtoFactory.toDetailResponse(savedQuestion, List.of());
    }

    // 👇 [추가된 부분] 인터페이스에서 약속한 답변 생성 메서드를 구현합니다.
    @Override
    @Transactional(readOnly = false) // 답변 데이터 삽입을 위한 트랜잭션 설정
    public AnswerResponse createAnswer(Long userId, Long questionId, AnswerCreateRequest request) {

        // 1. 토큰을 통해 추출된 유저 ID로 실제 유저 엔티티를 가져옵니다.
        // (프록시 객체로 가져와 성능을 최적화합니다.)
        Users writer = userService.getReferenceById(userId);

        // 2. 답변을 달 대상 '질문글'이 DB에 실제로 존재하는지 확인합니다.
        // 만약 존재하지 않으면 우리가 만든 Custom Exception(404 Not Found)을 던집니다.
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));

        // 3. Factory 클래스를 활용해 새로운 답변(Answer) 엔티티 객체를 조립합니다.
        Answer answer = QuestionEntityFactory.createAnswer(request.content(), question, writer);

        // 4. 조립된 답변 엔티티를 데이터베이스에 실제로 저장(INSERT)합니다.
        Answer savedAnswer = answerRepository.save(answer);

        // 5. 프론트엔드에 돌려줄 응답 DTO를 생성하여 반환합니다.
        // 더 이상 임시 값(1L, "service-user")이 아닌 실제 DB ID와 유저의 닉네임을 담아줍니다.
        return new AnswerResponse(
                savedAnswer.getAnswerId(),    // DB가 방금 자동 생성(Auto-Increment)한 답변 번호
                writer.getServiceNickname(), // 실제 로그인한 유저의 서비스 닉네임 (예: '이종엽')
                savedAnswer.getCreatedAt(),  // 답변이 작성된 실제 시간
                false,                       // 초기 채택(Best) 상태는 무조건 false로 고정
                savedAnswer.getContent()     // 방금 작성한 답변 본문
        );
    }
}