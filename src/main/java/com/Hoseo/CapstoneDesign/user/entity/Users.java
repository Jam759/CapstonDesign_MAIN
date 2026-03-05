package com.Hoseo.CapstoneDesign.user.entity;

import com.Hoseo.CapstoneDesign.global.entity.LifecycleTimestampEntity;
import com.Hoseo.CapstoneDesign.user.entity.enums.OauthType;
import com.Hoseo.CapstoneDesign.user.entity.enums.SystemRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET deleted_at = now() WHERE user_id = ?")
public class Users extends LifecycleTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "identity_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID identityId; // UUIDv7 저장

    @Column(name = "service_nickname", length = 100)
    private String serviceNickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_role", nullable = false)
    private SystemRole systemRole; // ADMIN, USER

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_type", nullable = false)
    private OauthType oauthType; // GitHub

    @Column(name = "oauth_provider_id", nullable = false, length = 255)
    private String oauthProviderId;

    @Column(name = "oauth_nickname", nullable = false, length = 255)
    private String oauthNickname;

    public void updateOauthNickname(String oauthNickname) {
        this.oauthNickname = oauthNickname;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Users)) return false;
        Users other = (Users) obj;
        return this.userId.equals(other.userId)
                && this.identityId.equals(other.identityId);
    }

}
