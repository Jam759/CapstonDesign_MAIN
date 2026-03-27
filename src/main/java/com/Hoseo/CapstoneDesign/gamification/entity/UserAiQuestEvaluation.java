package com.Hoseo.CapstoneDesign.gamification.entity;

import com.Hoseo.CapstoneDesign.analysis.entity.AnalysisJob;
import com.Hoseo.CapstoneDesign.gamification.entity.enums.AiQuestEvaluationResult;
import com.Hoseo.CapstoneDesign.global.entity.CreatableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAiQuestEvaluation extends CreatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_ai_quest_evaluation_id", nullable = false)
    private Long userAiQuestEvaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ai_quest_id", nullable = false)
    private UserAiQuest userAiQuest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_job_id")
    private AnalysisJob analysisJob;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_result", nullable = false, length = 30)
    private AiQuestEvaluationResult evaluationResult;

    @Column(name = "confidence_score", precision = 5, scale = 4)
    private BigDecimal confidenceScore;

    @Column(name = "reason", length = 2000)
    private String reason;

    @Column(name = "progress_note", length = 2000)
    private String progressNote;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

}
