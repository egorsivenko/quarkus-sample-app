package pragmasoft.k1teauth.oauth.consent;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsentRepository extends CrudRepository<Consent, UUID> {

    List<Consent> findAllByResourceOwner(User resourceOwner);

    Optional<Consent> findByResourceOwnerAndClient(User resourceOwner, OAuthClient client);
}
