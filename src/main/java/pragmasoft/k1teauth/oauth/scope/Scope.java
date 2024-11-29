package pragmasoft.k1teauth.oauth.scope;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
}
