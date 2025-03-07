@import java.util.List
@import io.micronaut.views.fields.Form
@import io.micronaut.views.fields.messages.Message

@param Form form
@param List<Message> errors

@template.layout(title = "Register new OAuth Client",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
scripts = @`
    <script src="https://unpkg.com/@hotwired/turbo@8.0.11/dist/turbo.es2017-umd.js"></script>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Register new OAuth Client</h1>
                @template.fieldset.errors(errors = errors)
                @template.fieldset.form(form = form, multipleSelect = true)
                <a href="/oauth2/clients" role="button" class="outline contrast wide">Cancel</a>
            </div>
        </article>
    </main>

    <script type="text/javascript" src="/js/scopes.js"></script>
`)
