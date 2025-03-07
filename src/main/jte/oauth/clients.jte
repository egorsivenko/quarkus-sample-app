@import io.micronaut.views.fields.FormGenerator
@import io.micronaut.views.fields.messages.Message
@import pragmasoft.k1teauth.oauth.client.OAuthClient
@import pragmasoft.k1teauth.oauth.client.form.DeleteClientForm
@import java.util.List

@param List<OAuthClient> clients
@param FormGenerator formGenerator

@template.layout(title = "OAuth Clients",
scripts = @`
    <script src="https://unpkg.com/@hotwired/turbo@8.0.11/dist/turbo.es2017-umd.js"></script>
`,
body = @`
    <h3>OAuth Clients</h3>
    <a href="/profile" role="button" class="outline contrast">Back</a>
    <a href="/oauth2/clients/new" role="button" class="outline contrast">New OAuth Client</a>
    <table>
        @if(clients.isEmpty())
            <caption>
                <p>No OAuth clients yet</p>
            </caption>
        @endif
        <thead>
        <tr>
            <th scope="col">Client ID</th>
            <th scope="col">Client Secret</th>
            <th scope="col">Client Name</th>
            <th scope="col">Callback URLs</th>
            <th scope="col">Scopes</th>
            <th scope="col">Confidential</th>
            <th scope="col">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for(var client : clients)
            <tr>
                <th scope="row">${client.getClientId()}</th>
                <td>
                    @if(client.getClientSecret() != null)
                        ${client.getClientSecret()}
                    @else
                        <i>Public client</i>
                    @endif
                </td>
                <td>${client.getName()}</td>
                <td>${String.join(", ", client.getCallbackUrls())}</td>
                <td>
                    @for(var scope : client.getScopes())
                        <div>${scope.getName()}</div>
                    @endfor
                </td>
                <td>${client.isConfidential()}</td>
                <td>
                    @template.fieldset.form(form = formGenerator.generate("/oauth2/clients/delete", new DeleteClientForm(client.getClientId()), Message.of("Delete")))
                    <a href="/oauth2/clients/edit?clientId=${client.getClientId()}" role="button" class="outline wide">Edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)