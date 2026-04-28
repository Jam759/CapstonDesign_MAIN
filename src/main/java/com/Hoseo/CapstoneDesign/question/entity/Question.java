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

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근을 막아 객체 생성을 안전하게 통제합니다.
@SQLDelete(sql = "UPDATE question SET deleted_at = now() WHERE question_id = ?") // 삭제 시 실제 데이터 대신 삭제 시간을 기록합니다.
@SQLRestriction("deleted_at is null") // 조회 시 삭제되지 않은 데이터만 자동으로 필터링합니다.
public class Question extends LifecycleTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(nullable = false, length = 255)
    private String title; // 프론트엔드에서 넘어올 질문 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 마크다운 텍스트와 이미지 링크가 함께 저장되는 본문

    @Builder.Default
    @Column(nullable = false)
    private int views = 0; // 기본 조회수는 0으로 세팅합니다.

    // 1. 태그 목록: 프론트엔드의 배열 데이터를 위해 별도의 테이블(question_tags)을 자동 생성해 관리합니다.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "question_tags",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "tag_name")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // 2. 작성자 정보: 기존에 친구가 만들어둔 Users 테이블의 데이터를 참조합니다. (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Users writer;

    // 3. 답변 목록 (새로 추가됨): 질문 상세 조회를 위해 이 질문에 달린 답변들을 리스트로 묶어줍니다. (일대다 관계)
    // mappedBy = "question"은 Answer 엔티티의 question 필드가 관계의 주인임을 의미합니다.
    // cascade = CascadeType.ALL은 질문이 지워지면 답변도 함께 지워지도록 설정합니다.
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

    // --- 객체 수정을 위한 비즈니스 메서드 (친구의 규칙에 따라 Setter 대신 사용) ---

    // 질문 제목, 내용, 태그를 한 번에 수정하는 메서드
    public void updateQuestion(String title, String content, List<String> tags) {
        this.title = title;
        this.content = content;
        this.tags = tags;
    }

    // 질문을 읽을 때마다 조회수를 1씩 증가시키는 메서드
    public void addViewCount() {
        this.views++;
    }
}