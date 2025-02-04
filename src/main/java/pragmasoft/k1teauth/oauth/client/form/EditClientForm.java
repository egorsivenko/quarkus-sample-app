package pragmasoft.k1teauth.oauth.client.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Serdeable
public class EditClientForm extends RegisterClientForm {

    @NotBlank
    private String clientId;

    public EditClientForm() {}

    @Creator
    public EditClientForm(String clientName, String callbackUrls, List<String> scopes, String clientId) {
        super(clientName, callbackUrls, scopes);
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
