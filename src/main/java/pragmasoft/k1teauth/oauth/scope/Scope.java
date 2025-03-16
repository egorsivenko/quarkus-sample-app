package pragmasoft.k1teauth.oauth.scope;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Serdeable
@Entity
@Table(name = "scopes")
public class Scope {

    @Id
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String audience;

    public Scope() {}

    public Scope(String name, String description, String audience) {
        this.name = name;
        this.description = description;
        this.audience = audience;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
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
