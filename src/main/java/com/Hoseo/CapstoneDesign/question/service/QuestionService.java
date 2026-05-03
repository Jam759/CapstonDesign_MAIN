package com.Hoseo.CapstoneDesign.question.service;

import com.Hoseo.CapstoneDesign.question.entity.Question;
import com.Hoseo.CapstoneDesign.question.exception.QuestionErrorCode;
import com.Hoseo.CapstoneDesign.question.exception.QuestionException;
import com.Hoseo.CapstoneDesign.question.factory.QuestionEntityFactory;
import com.Hoseo.CapstoneDesign.question.repository.QuestionRepository;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional
    public Question createQuestion(Users writer, String title, String content, List<String> tags) {
        Question question = QuestionEntityFactory.createQuestion(title, content, tags, writer);
        return questionRepository.save(question);
    }

    // 모든 질문글 조회 (최신순)
    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        return questionRepository.findAllByOrderByCreatedAtDesc();
    }

    // 질문 단건 조회 (내부 로직 공통 사용)
    @Transactional(readOnly = true)
    public Question getQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(QuestionErrorCode.QUESTION_NOT_FOUND));
    }

    // 질문 단건 조회 및 조회수 증가 (상세 페이지 접근 시 사용)
    @Transactional
    public Question getQuestionWithViews(Long questionId) {
        Question question = getQuestion(questionId);
        question.addViewCount();
        return question;
    }

    // 질문 수정 로직
    @Transactional
    public Question updateQuestion(Long questionId, Long requestUserId, String title, String content, List<String> tags) {
        Question question = getQuestion(questionId);

        // 권한 검증: 글 작성자의 ID와 요청한 유저의 ID가 일치하는지 확인
        if (!question.getWriter().getUserId().equals(requestUserId)) {
            throw new QuestionException(QuestionErrorCode.QUESTION_ACCESS_DENIED);
        }

        // Entity에 만들어둔 메서드 호출 (JPA Dirty Checking으로 자동 UPDATE 처리됨)
        question.updateQuestion(title, content, tags);
        return question;
    }

    // 질문 삭제 로직
    @Transactional
    public void deleteQuestion(Long questionId, Long requestUserId) {
        Question question = getQuestion(questionId);

        // 권한 검증
        if (!question.getWriter().getUserId().equals(requestUserId)) {
            throw new QuestionException(QuestionErrorCode.QUESTION_ACCESS_DENIED);
        }

        // @SQLDelete가 적용되어 있으므로 실제 데이터는 남고 deleted_at만 업데이트됨
        questionRepository.delete(question);
    }
}