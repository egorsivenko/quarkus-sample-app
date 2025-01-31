@import pragmasoft.k1teauth.oauth.scope.Scope
@import java.util.Set
@import java.util.UUID
@import java.util.stream.Collectors

@param String csrfToken
@param String clientId
@param String clientName
@param String callbackUrl
@param String state
@param UUID userId
@param Set<Scope> scopes
@param String codeChallenge
@param String codeChallengeMethod

@template.layout(title = "Access permission",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h3><b>${clientName}</b> is requesting the following permissions:</h3>
                <ul>
                    @for(var scope : scopes)
                        <li>${scope.getDescription()}</li>
                    @endfor
                </ul>
                <h4>Do you want to continue?</h4>
                <form action="/oauth2/consent" method="post" autocomplete="off">
                    <input type="hidden" name="userGaveConsent" value="true"/>
                    <input type="hidden" name="callbackUrl" value="${callbackUrl}"/>
                    <input type="hidden" name="clientId" value="${clientId}"/>
                    <input type="hidden" name="userId" value="${userId.toString()}"/>
                    <input type="hidden" name="state" value="${state}"/>
                    <input type="hidden" name="scopes"
                           value="${scopes.stream().map(Scope::getName).collect(Collectors.joining(" "))}"/>
                    <input type="hidden" name="codeChallenge" value="${codeChallenge}"/>
                    <input type="hidden" name="codeChallengeMethod" value="${codeChallengeMethod}"/>
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <button type="submit" class="outline">Yes</button>
                </form>
                <form action="/oauth2/consent" method="post" autocomplete="off">
                    <input type="hidden" name="userGaveConsent" value="false"/>
                    <input type="hidden" name="callbackUrl" value="${callbackUrl}"/>
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <button type="submit" class="outline">No</button>
                </form>
            </div>
        </article>
    </main>
`)
