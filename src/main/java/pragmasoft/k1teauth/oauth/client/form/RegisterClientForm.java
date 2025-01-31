package pragmasoft.k1teauth.oauth.client.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Serdeable
public class RegisterClientForm {

    @NotBlank
    @Size(min = 3, max = 100)
    private String clientName;

    @NotBlank
    private String callbackUrls;

    @NotNull
    private List<String> scopes;

    public RegisterClientForm() {}

    @Creator
    public RegisterClientForm(String clientName, String callbackUrls, List<String> scopes) {
        this.clientName = clientName;
        this.callbackUrls = callbackUrls;
        this.scopes = scopes;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCallbackUrls() {
        return callbackUrls;
    }

    public void setCallbackUrls(String callbackUrls) {
        this.callbackUrls = callbackUrls;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
