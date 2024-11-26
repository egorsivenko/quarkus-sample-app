package pragmasoft.k1teauth.user.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import pragmasoft.k1teauth.user.User;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PanacheUserRepository implements PanacheRepositoryBase<User, UUID> {

    public Optional<User> findByEmailOptional(String email) {
        return find("email", email).firstResultOptional();
    }
}
