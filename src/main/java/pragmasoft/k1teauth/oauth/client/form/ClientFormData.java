package pragmasoft.k1teauth.oauth.client.form;

import java.util.List;

public interface ClientFormData {

    String clientName();

    String callbackUrls();

    List<String> scopes();

    boolean isConfidential();
}
