package pragmasoft.k1teauth.oauth.scope.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Serdeable
public class EditScopeForm extends AddScopeForm {

    @NotBlank
    @Size(min = 3, max = 255)
    private String previousName;

    public EditScopeForm() {}

    @Creator
    public EditScopeForm(String name, String description, String audience, String previousName) {
        super(name, description, audience);
        this.previousName = previousName;
    }

    public boolean isNameChanged() {
        return !Objects.equals(name, previousName);
    }

    public String getPreviousName() {
        return previousName;
    }

    public void setPreviousName(String previousName) {
        this.previousName = previousName;
    }
}
