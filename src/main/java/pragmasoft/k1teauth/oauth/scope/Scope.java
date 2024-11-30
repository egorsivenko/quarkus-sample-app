package pragmasoft.k1teauth.oauth.scope;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(
        name = "scopes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "audience"})
)
public class Scope extends PanacheEntityBase {

    @Id
    public String name;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false)
    public String audience;

    public Scope() {
    }

    public Scope(String name, String description, String audience) {
        this.name = name;
        this.description = description;
        this.audience = audience;
    }

    public static Optional<Scope> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Scope scope)) return false;
        return Objects.equals(name, scope.name)
                && Objects.equals(description, scope.description)
                && Objects.equals(audience, scope.audience);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, audience);
    }
}
