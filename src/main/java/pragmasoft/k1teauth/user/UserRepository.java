package pragmasoft.k1teauth.user;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends PageableRepository<User, UUID> {

    Optional<User> findByEmail(String email);
}
