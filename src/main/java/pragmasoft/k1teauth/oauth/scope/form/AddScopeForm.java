package pragmasoft.k1teauth.oauth.scope.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pragmasoft.k1teauth.oauth.scope.Scope;

@Serdeable
public class AddScopeForm {

  @NotBlank
  @Size(min = 3, max = 255)
  protected String name;

  @NotBlank
  @Size(min = 3, max = 255)
  protected String description;

  @NotBlank
  @InputUrl
  @Size(max = 255)
  protected String audience;

  public AddScopeForm() {}

  @Creator
  public AddScopeForm(String name, String description, String audience) {
    this.name = name;
    this.description = description;
    this.audience = audience;
  }

  public Scope mapToScope() {
    return new Scope(name, description, audience);
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
}
