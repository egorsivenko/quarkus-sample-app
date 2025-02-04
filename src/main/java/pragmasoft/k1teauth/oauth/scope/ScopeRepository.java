package pragmasoft.k1teauth.oauth.scope;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface ScopeRepository extends CrudRepository<Scope, String> {
}
