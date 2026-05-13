package com.Hoseo.CapstoneDesign.image.entity;

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
@Table(name = "image")
// JpaRepository의 delete() 호출 시 실제 DELETE 쿼리 대신 아래 UPDATE 쿼리가 실행됩니다. (소프트 삭제)
@SQLDelete(sql = "UPDATE image SET deleted_at = CURRENT_TIMESTAMP WHERE image_id = ?")
// JpaRepository로 조회할 때 항상 'deleted_at IS NULL'인 데이터만 가져오도록 전역 필터를 겁니다.
@SQLRestriction("deleted_at IS NULL")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends LifecycleTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private Users uploader;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "target_type_cmd_id", length = 50)
    private String targetTypeCmdId;

    @Column(name = "origin_img_name", nullable = false, length = 255)
    private String originImgName;

    @Column(name = "upload_img_name", nullable = false, length = 255)
    private String uploadImgName;

    @Column(name = "img_path", nullable = false, length = 255)
    private String imgPath;

    @Column(name = "img_extension_cmd_id", length = 50)
    private String imgExtensionCmdId;

    @Column(name = "created_by")
    private Long createdBy;

    // 비즈니스 메서드: 최종 글 작성 시 타겟(게시글 등)과 이미지를 연결
    public void attachToTarget(Long targetId, String targetTypeCmdId) {
        this.targetId = targetId;
        this.targetTypeCmdId = targetTypeCmdId;
    }
}