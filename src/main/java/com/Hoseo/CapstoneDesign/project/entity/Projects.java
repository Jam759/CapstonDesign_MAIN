package com.Hoseo.CapstoneDesign.project.entity;

import com.Hoseo.CapstoneDesign.global.entity.LifecycleTimestampEntity;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectMemberType;
import com.Hoseo.CapstoneDesign.project.entity.enums.ProjectType;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE projects SET deleted_at = now() WHERE user_id = ?")
public class Projects extends LifecycleTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @JoinColumn(name = "owner_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    @Column(name = "repo_url", nullable = false, columnDefinition = "TEXT")
    private String repoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false, length = 20)
    private ProjectMemberType memberType;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "goal", length = 255)
    private String goal;

    @Column(name = "language", length = 255)
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type", nullable = false, length = 30)
    private ProjectType projectType;

}
