package pragmasoft.k1teauth.oauth.consent;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsentRepository extends PageableRepository<Consent, UUID> {

    Page<Consent> findAllByResourceOwner(User resourceOwner, Pageable pageable);

    Optional<Consent> findByResourceOwnerAndClient(User resourceOwner, OAuthClient client);
}
