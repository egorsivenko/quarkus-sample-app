package pragmasoft.k1teauth.oauth.scope;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface ScopeRepository extends PageableRepository<Scope, String> {
}
