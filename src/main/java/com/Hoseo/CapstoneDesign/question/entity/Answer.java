package com.Hoseo.CapstoneDesign.question.entity;

import com.Hoseo.CapstoneDesign.global.entity.LifecycleTimestampEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 안전한 엔티티 생성을 위한 접근 제어
@SQLDelete(sql = "UPDATE answer SET deleted_at = now() WHERE answer_id = ?") // 소프트 딜리트 적용
@SQLRestriction("deleted_at is null") // 삭제된 답변은 화면에 나오지 않도록 필터링
public class Answer extends LifecycleTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id", nullable = false)
    private Long answerId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 프론트엔드의 마크다운 답변 텍스트가 저장됩니다.

    // 1. 소속된 질문 정보: 이 답변이 어떤 질문에 달려있는지 연결합니다. (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // 2. 답변 작성자 정보: 기존 Users 테이블을 참조합니다. (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Users writer;

    // --- 객체 수정을 위한 비즈니스 메서드 ---

    // 답변 내용을 수정할 때 호출하는 메서드
    public void updateContent(String content) {
        this.content = content;
    }
}