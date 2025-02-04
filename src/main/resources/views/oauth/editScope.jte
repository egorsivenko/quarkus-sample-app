@import java.lang.Iterable
@import pragmasoft.k1teauth.oauth.scope.Scope

@param String csrfToken
@param Scope scope
@param Iterable<String> errors

@template.layout(title = "Edit scope",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Edit scope</h1>
                <form action="/oauth2/scopes/edit" method="post" autocomplete="off">
                    @if(errors != null)
                        @for(var error : errors)
                            <p class="error">${error}</p>
                        @endfor
                    @endif
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="previousName" value="${scope.getName()}" />
                    <label for="name">Name</label>
                    <input type="text" id="name" name="name" minlength="3" maxlength="255" value="${scope.getName()}"
                           placeholder="Name" required autofocus />
                    <label for="description">Description</label>
                    <input type="text" id="description" name="description" minlength="3" maxlength="255" value="${scope.getDescription()}"
                           placeholder="Description" required />
                    <label for="audience">Audience</label>
                    <input type="url" id="audience" name="audience" maxlength="255" value="${scope.getAudience()}"
                           placeholder="Audience" required />
                    <button type="submit" class="contrast">Save changes</button>
                    <a href="/oauth2/scopes" role="button" class="outline contrast wide">Cancel</a>
                </form>
            </div>
        </article>
    </main>
`)
