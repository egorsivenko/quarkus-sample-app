@import java.lang.Iterable

@param String csrfToken
@param Iterable<String> errors

@template.layout(title = "Add new scope",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Add new scope</h1>
                <form action="/oauth2/scopes/new" method="post" autocomplete="off">
                    @if(errors != null)
                        @for(var error : errors)
                            <p class="error">${error}</p>
                        @endfor
                    @endif
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <label for="name">Name</label>
                    <input type="text" id="name" name="name" minlength="3" maxlength="255" placeholder="Name" required autofocus/>
                    <label for="description">Description</label>
                    <input type="text" id="description" name="description" minlength="3" maxlength="255" placeholder="Description" required/>
                    <label for="audience">Audience</label>
                    <input type="url" id="audience" name="audience" maxlength="255" placeholder="Audience" required/>
                    <button type="submit" class="contrast">Create scope</button>
                    <a href="/oauth2/scopes" role="button" class="outline contrast wide">Cancel</a>
                </form>
            </div>
        </article>
    </main>
`)
