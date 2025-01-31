package pragmasoft.k1teauth.oauth.code;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import pragmasoft.k1teauth.oauth.consent.Consent;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuthCodeRepository extends CrudRepository<AuthCode, String> {

    Optional<AuthCode> findByConsent(Consent consent);

    void deleteByExpiresAtLessThan(LocalDateTime dateTime);
}
