package pragmasoft.k1teauth.oauth.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

public class SignInForm {

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String email;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String password;

    @RestForm
    @NotNull
    String clientId;

    @RestForm
    @NotNull
    String clientName;

    @RestForm
    @NotNull
    String callbackUrl;

    @RestForm
    @NotNull
    String state;

    @RestForm
    @NotNull
    String scopes;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getState() {
        return state;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }
}
