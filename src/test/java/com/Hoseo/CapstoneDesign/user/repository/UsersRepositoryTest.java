package com.Hoseo.CapstoneDesign.user.repository;

import com.Hoseo.CapstoneDesign.support.builder.UsersTestBuilder;
import com.Hoseo.CapstoneDesign.user.entity.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class UsersRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UsersRepositoryTest.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Test
    @DisplayName("identityId로 사용자 조회가 가능하다")
    void findByIdentityIdSuccess() {
        Users user = usersRepository.save(UsersTestBuilder.defaultUser().build());

        Optional<Users> result = usersRepository.findByIdentityId(user.getIdentityId());

        assertThat(result).isPresent();
        assertThat(result.get().getIdentityId()).isEqualTo(user.getIdentityId());
        log.info("[TEST] repository findByIdentityId success validated");
    }

    @Test
    @DisplayName("identityId 중복 저장 시 예외가 발생한다")
    void duplicateIdentityIdFail() {
        UUID duplicatedIdentity = UUID.randomUUID();
        usersRepository.save(UsersTestBuilder.defaultUser().identityId(duplicatedIdentity).build());
        assertThatThrownBy(() -> usersRepository.save(UsersTestBuilder.defaultUser()
                        .identityId(duplicatedIdentity)
                        .oauthProviderId("another-provider")
                        .build()))
                .isInstanceOf(DataIntegrityViolationException.class);

        log.info("[TEST] duplicate identity constraint validated");
    }

    @Test
    @DisplayName("delete 호출 시 soft delete 컬럼 deleted_at 이 채워진다")
    void softDeleteSetsDeletedAt() {
        Users user = usersRepository.save(UsersTestBuilder.defaultUser().build());
        usersRepository.flush();

        usersRepository.delete(user);
        usersRepository.flush();
        entityManager.clear();

        Object deletedAt = entityManager.createNativeQuery("select deleted_at from users where user_id = :userId")
                .setParameter("userId", user.getUserId())
                .getSingleResult();

        assertThat(deletedAt).isInstanceOf(Timestamp.class);
        assertThat(((Timestamp) deletedAt).toLocalDateTime()).isNotNull();
        log.info("[TEST] soft delete SQLDelete behavior validated");
    }
}
