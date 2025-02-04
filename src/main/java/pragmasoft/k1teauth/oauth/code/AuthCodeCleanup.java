package pragmasoft.k1teauth.oauth.code;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthCodeCleanup {

    @Scheduled(every = "5m")
    @Transactional
    void removeExpiredAuthCodes() {
        AuthCode.delete("expiresAt < ?1", LocalDateTime.now());
    }
}
