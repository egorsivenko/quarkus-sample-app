package pragmasoft.k1teauth.oauth.client;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;

@Repository
public interface OAuthClientRepository extends PageableRepository<OAuthClient, String> {

    Optional<OAuthClient> findByName(String name);
}
