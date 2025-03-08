@import io.micronaut.views.fields.Form

@param String siteKey
@param boolean failed
@param Form form

@template.layout(title = "Login",
scripts = @`
    <script src="https://challenges.cloudflare.com/turnstile/v0/api.js" async defer></script>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Login</h1>
                @if(failed)
                    <p class="invalid-feedback">Invalid Credentials</p>
                @endif
                @template.fieldset.form(form = form, useCaptcha = true, siteKey = siteKey, useTurboDrive = false)
                <label align="center">
                    <a href="/auth/forgot-password">Forgot Password?</a><br>
                    Don't have an account? <a href="/auth/registration">Register</a>
                </label>
            </div>
        </article>
    </main>
`)