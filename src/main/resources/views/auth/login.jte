@param String siteKey
@param boolean failed

@template.layout(title = "Login",
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
                <h1>Login</h1>
                <form action="/login" method="post" autocomplete="off">
                    @if(failed)
                        <p class="error">Invalid Credentials</p>
                    @endif
                    <input type="email" name="username" maxlength="50" placeholder="Email" autofocus required/>
                    <input type="password" name="password" maxlength="50" placeholder="Password" required/>
                    <a href="/auth/forgot-password">Forgot Password?</a>
                    <div class="cf-turnstile" data-sitekey="${siteKey}" data-language="en-US"></div>
                    <button type="submit" class="contrast">Login</button>
                    <label align="center">
                        Don't have an account? <a href="/auth/registration">Register</a>
                    </label>
                </form>
            </div>
        </article>
    </main>
`)