package com.Hoseo.CapstoneDesign.question.repository;

import com.Hoseo.CapstoneDesign.question.entity.Answer;
import com.Hoseo.CapstoneDesign.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    // 특정 질문글에 달린 답변(댓글)의 총 개수를 셉니다.
    int countByQuestion(Question question);

    // 특정 질문글에 달린 모든 답변을 조회합니다. (상세 조회용)
    List<Answer> findAllByQuestion(Question question);
}