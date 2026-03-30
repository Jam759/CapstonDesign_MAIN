package com.Hoseo.CapstoneDesign.github.mapper;

import com.Hoseo.CapstoneDesign.github.dto.query.GitHubWebhookValidationQueryResult;
import com.Hoseo.CapstoneDesign.github.dto.query.UserGitHubInstallationLinkQueryResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GitHubMapper {

    /**
     * 사용자와 installationRepository 간의 연결 관계를 검증한다.
     *
     * @param userId 사용자 ID
     * @param installationRepositoryId installation_repository 식별자
     * @return 조건을 만족하면 연결 정보 반환, 없으면 null
     */
    UserGitHubInstallationLinkQueryResult findUserLinkedInstallationRepository(
            @Param("userId") Long userId,
            @Param("installationRepositoryId") Long installationRepositoryId
    );

    /**
     * 웹훅 sender 검증 + 프로젝트 연결 검증 통합 쿼리
     *
     * 검증 내용:
     * - installation 존재
     * - installation → repository 연결
     * - repository → project 연결
     * - project → project member 존재
     * - project member가 가진 installation 중 sender account_id 존재
     */
    GitHubWebhookValidationQueryResult findWebhookValidation(
            @Param("installationId") Long installationId,
            @Param("installationRepositoryId") Long installationRepositoryId,
            @Param("senderAccountId") Long senderAccountId
    );

}
