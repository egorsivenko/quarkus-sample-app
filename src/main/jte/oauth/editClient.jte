@import java.lang.Iterable
@import java.util.List
@import pragmasoft.k1teauth.oauth.client.OAuthClient
@import pragmasoft.k1teauth.oauth.scope.Scope

@param String csrfToken
@param OAuthClient client
@param List<Scope> scopes
@param Iterable<String> errors

@template.layout(title = "Edit OAuth Client",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Edit OAuth Client</h1>
                <form action="/oauth2/clients/edit" method="post" autocomplete="off">
                    @if(errors != null)
                        @for(var error : errors)
                            <p class="error">${error}</p>
                        @endfor
                    @endif
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="clientId" value="${client.getClientId()}" />
                    <input type="hidden" name="isConfidential" value="${client.isConfidential()}" />
                    <label for="clientName">Client name</label>
                    <input type="text" id="clientName" name="clientName" minlength="3" maxlength="100" value="${client.getName()}"
                           placeholder="Client name" required autofocus />
                    <label for="callbackUrls">Callback URLs (comma-separated list)</label>
                    <input type="text" id="callbackUrls" name="callbackUrls" maxlength="1000" value="${String.join(", ", client.getCallbackUrls())}"
                           placeholder="Callback URL" required />
                    <label for="scopes">Select scopes</label>
                    <input type="text" id="scopeSearch" aria-label="scopeSearch" placeholder="Search scopes..." />
                    <select name="scopes" id="scopes" multiple size="6" required>
                        @for(var scope : scopes)
                            @if(client.getScopes().contains(scope))
                                <option selected value="${scope.getName()}">${scope.getName()}</option>
                            @else
                                <option value="${scope.getName()}">${scope.getName()}</option>
                            @endif
                        @endfor
                    </select>
                    <button type="submit" class="contrast">Save changes</button>
                    <a href="/oauth2/clients" role="button" class="outline contrast wide">Cancel</a>
                </form>
            </div>
        </article>
    </main>

    <script type="text/javascript" src="/js/scopes.js"></script>
`)
