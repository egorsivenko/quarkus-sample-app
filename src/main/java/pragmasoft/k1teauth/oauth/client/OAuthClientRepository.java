package pragmasoft.k1teauth.oauth.client;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface OAuthClientRepository extends CrudRepository<OAuthClient, String> {

    Optional<OAuthClient> findByName(String name);
}
