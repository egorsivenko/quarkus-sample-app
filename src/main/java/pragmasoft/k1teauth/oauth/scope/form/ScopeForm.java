package pragmasoft.k1teauth.oauth.scope.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.jboss.resteasy.reactive.RestForm;
import pragmasoft.k1teauth.oauth.scope.Scope;

import java.util.Objects;

public class ScopeForm {

    @RestForm
    @NotBlank
    @Size(min = 3, max = 255)
    String name;

    @RestForm
    @NotBlank
    @Size(min = 3, max = 255)
    String description;

    @RestForm
    @NotBlank
    @URL
    @Size(max = 255)
    String audience;

    @RestForm
    String previousName;

    public String getName() {
        return name;
    }

    public String getPreviousName() {
        return previousName;
    }

    public boolean isNameChanged() {
        return !Objects.equals(name, previousName);
    }

    public Scope mapToScope() {
        return new Scope(name, description, audience);
    }
}
