package pragmasoft.k1teauth.oauth.code;

import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@Singleton
public class AuthCodeCleanupScheduler {

    private final AuthCodeRepository authCodeRepository;

    public AuthCodeCleanupScheduler(AuthCodeRepository authCodeRepository) {
        this.authCodeRepository = authCodeRepository;
    }

    @Scheduled(fixedDelay = "10m", initialDelay = "1m")
    @Transactional
    void cleanupExpiredAuthCodes() {
        authCodeRepository.deleteByExpiresAtLessThan(LocalDateTime.now());
    }
}
