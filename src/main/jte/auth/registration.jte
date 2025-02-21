@import java.lang.Iterable

@param String siteKey
@param Iterable<String> errors

@template.layout(title = "Registration",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
scripts = @`
    <script src="https://challenges.cloudflare.com/turnstile/v0/api.js" async defer></script>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Registration</h1>
                <form action="/auth/registration" method="post" autocomplete="off">
                    @if(errors != null)
                        @for(var error : errors)
                            <p class="error">${error}</p>
                        @endfor
                    @endif
                    <input type="text" id="full-name" name="fullName" minlength="4" maxlength="50" placeholder="Full name" required autofocus/>
                    <input type="email" id="email" name="email" maxlength="50" placeholder="Email" required/>
                    <input type="password" id="password" name="password" minlength="6" maxlength="50" placeholder="Password" required/>
                    <input type="password" id="confirm-password" name="confirmPassword" minlength="6" maxlength="50" required
                           placeholder="Confirm password" aria-describedby="invalid-helper-confirm"/>
                    <small id="invalid-helper-confirm">Passwords don’t match.</small>
                    <div class="cf-turnstile" data-sitekey="${siteKey}" data-language="en-US"></div>
                    <button type="submit" class="contrast" id="submit-button">Register</button>
                    <label align="center">
                        Already have an account? <a href="/auth/login">Login</a>
                    </label>
                </form>
            </div>
        </article>
    </main>

    <script type="text/javascript" src="/js/validation.js"></script>
`)
