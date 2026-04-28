package com.Hoseo.CapstoneDesign.question.repository;

import com.Hoseo.CapstoneDesign.question.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    //CRUD 자동 매핑
}