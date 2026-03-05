package com.Hoseo.CapstoneDesign.security.entity;

import com.Hoseo.CapstoneDesign.global.entity.LifecycleTimestampEntity;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE refresh_token SET deleted_at = now() WHERE id = ?")
public class RefreshToken extends LifecycleTimestampEntity {

    @Id
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;// jti

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime revokedAt;

    private LocalDateTime usedAt;

    @Column(name = "replaced_by_token_id", columnDefinition = "BINARY(16)")
    private UUID replacedByTokenId;

    @Column(name = "family_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID familyId;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void markUsed(LocalDateTime now, UUID replacedByTokenId) {
        this.usedAt = now;
        this.replacedByTokenId = replacedByTokenId;
    }

    public void revoke(LocalDateTime now) {
        this.revokedAt = now;
    }

}
