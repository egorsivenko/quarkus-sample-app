@import java.util.List
@import io.micronaut.views.fields.Form
@import io.micronaut.views.fields.messages.Message

@param String siteKey
@param List<Message> errors
@param Form form

@template.layout(title = "Forgot password",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
scripts = @`
    <script src="https://challenges.cloudflare.com/turnstile/v0/api.js" async defer></script>
    <script src="https://unpkg.com/@hotwired/turbo@8.0.11/dist/turbo.es2017-umd.js"></script>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Forgot password</h1>
                @template.fieldset.errors(errors = errors)
                @template.fieldset.form(form = form, useCaptcha = true, siteKey = siteKey)
                <a href="/auth/login" role="button" class="outline contrast wide">Back</a>
            </div>
        </article>
    </main>
`)
