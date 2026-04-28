package com.Hoseo.CapstoneDesign.question.repository;

import com.Hoseo.CapstoneDesign.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// JpaRepository<관리할 엔티티 클래스, 그 엔티티의 PK 데이터 타입> 을 상속받습니다.
public interface QuestionRepository extends JpaRepository<Question, Long> {
    //조회순으로 질문 찾을때 추가 예정
}