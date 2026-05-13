package com.Hoseo.CapstoneDesign.question.repository;

import com.Hoseo.CapstoneDesign.question.entity.Question;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 모든 질문을 최신순으로 조회합니다. (전체 목록 조회용)
    List<Question> findAllByOrderByCreatedAtDesc();

    // 모든 질문을 최신순으로 페이징 조회합니다.
    Page<Question> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 특정 작성자가 쓴 질문글을 생성일 기준 내림차순(최신순)으로 가져옵니다.
    List<Question> findAllByWriterOrderByCreatedAtDesc(Users writer);
}