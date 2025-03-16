@import java.util.List
@import io.micronaut.views.fields.Form
@import io.micronaut.views.fields.messages.Message

@param String siteKey
@param List<Message> errors
@param Form form

@template.layout(title = "Forgot password",
scripts = @`
    <script src="https://challenges.cloudflare.com/turnstile/v0/api.js" async defer></script>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Forgot password</h1>
                @template.fieldset.errors(errors = errors)
                @template.fieldset.form(form = form, useCaptcha = true, siteKey = siteKey, useTurboDrive = false)
                <a href="/auth/login" role="button" class="outline contrast wide" data-turbo="false">Back</a>
            </div>
        </article>
    </main>
`)
