@import pragmasoft.k1teauth.oauth.dto.ConsentForm

@param String csrfToken
@param ConsentForm form

@template.layout(title = "Access permission",
body = @`
    <main class="container">
        <article>
            <div>
                <h3><b>${form.getClientName()}</b> is requesting the following permissions:</h3>
                <ul>
                    @for(var scopeDescription : form.getScopeDescriptions().split("\n"))
                        <li>${scopeDescription}</li>
                    @endfor
                </ul>
                <h4>Do you want to continue?</h4>
                <form action="/oauth2/consent" method="post" autocomplete="off" data-turbo="false">
                    <input type="hidden" name="userGaveConsent" value="true"/>
                    <input type="hidden" name="callbackUrl" value="${form.getCallbackUrl()}"/>
                    <input type="hidden" name="clientName" value="${form.getClientName()}"/>
                    <input type="hidden" name="userId" value="${form.getUserId().toString()}"/>
                    <input type="hidden" name="state" value="${form.getState()}"/>
                    <input type="hidden" name="scopeNames" value="${form.getScopeNames()}"/>
                    <input type="hidden" name="codeChallenge" value="${form.getCodeChallenge()}"/>
                    <input type="hidden" name="codeChallengeMethod" value="${form.getCodeChallengeMethod()}"/>
                    <input type="hidden" name="nonce" value="${form.getNonce()}"/>
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <button type="submit" class="outline">Yes</button>
                </form>
                <form action="/oauth2/consent" method="post" autocomplete="off" data-turbo="false">
                    <input type="hidden" name="userGaveConsent" value="false"/>
                    <input type="hidden" name="callbackUrl" value="${form.getCallbackUrl()}"/>
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <button type="submit" class="outline">No</button>
                </form>
            </div>
        </article>
    </main>
`)
