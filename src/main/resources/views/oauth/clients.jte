@import pragmasoft.k1teauth.oauth.client.OAuthClient
@import java.util.List

@param String csrfToken
@param List<OAuthClient> clients

@template.layout(title = "OAuth Clients",
styles = @`
    <link rel="stylesheet" href="/css/extra-styles.css"/>
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
            <th scope="col">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for(var client : clients)
            <tr>
                <th scope="row">${client.getClientId()}</th>
                <td>${client.getClientSecret()}</td>
                <td>${client.getName()}</td>
                <td>${String.join(", ", client.getCallbackUrls())}</td>
                <td>
                    @for(var scope : client.getScopes())
                        <div>${scope.getName()}</div>
                    @endfor
                </td>
                <td>
                    <form action="/oauth2/clients/delete" method="POST"
                          onsubmit="return confirm('Are you sure you want to delete this OAuth client?');">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="clientId" value="${client.getClientId()}"/>
                        <button type="submit" class="outline">Delete</button>
                    </form>
                    <a href="/oauth2/clients/edit?clientId=${client.getClientId()}" role="button" class="outline wide">Edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)