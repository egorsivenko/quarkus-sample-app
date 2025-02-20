@import pragmasoft.k1teauth.oauth.scope.Scope
@import java.lang.Iterable
@import java.util.List

@param String csrfToken
@param List<Scope> scopes
@param Iterable<String> errors

@template.layout(title = "Register new OAuth Client",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Register new OAuth Client</h1>
                <form action="/oauth2/clients/new" method="post" autocomplete="off">
                    @if(errors != null)
                        @for(var error : errors)
                            <p class="error">${error}</p>
                        @endfor
                    @endif
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <label for="clientName">Client name</label>
                    <input type="text" id="clientName" name="clientName" minlength="3" maxlength="100"
                           placeholder="Client name" required autofocus />
                    <label for="callbackUrls">Callback URLs (comma-separated list)</label>
                    <input type="text" id="callbackUrls" name="callbackUrls" maxlength="1000"
                           placeholder="Callback URLs" required />
                    <label for="scopes">Select scopes</label>
                    <input type="text" id="scopeSearch" aria-label="scopeSearch" placeholder="Search scopes..." />
                    <select name="scopes" id="scopes" multiple size="6" required>
                        @for(var scope : scopes)
                            <option value="${scope.getName()}">${scope.getName()}</option>
                        @endfor
                    </select>
                    <fieldset>
                        <label>
                            <input type="checkbox" id="isConfidential" name="isConfidential" value="true" />
                            Confidential client
                        </label>
                    </fieldset>
                    <button type="submit" class="contrast">Create OAuth Client</button>
                    <a href="/oauth2/clients" role="button" class="outline contrast wide">Cancel</a>
                </form>
            </div>
        </article>
    </main>

    <script type="text/javascript" src="/js/scopes.js"></script>
`)
