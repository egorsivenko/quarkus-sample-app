@import java.lang.Iterable

@param String siteKey
@param Iterable<String> errors

@template.layout(title = "Forgot password",
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
                <h1>Forgot password</h1>
                <form action="/auth/forgot-password" method="post" autocomplete="off">
                    @if(errors != null)
                        @for(var error : errors)
                            <p class="error">${error}</p>
                        @endfor
                    @endif
                    <label for="email">Account email</label>
                    <input type="email" id="email" name="email" maxlength="50" placeholder="Email" autofocus required/>
                    <div class="cf-turnstile" data-sitekey="${siteKey}" data-language="en-US"></div>
                    <button type="submit" class="contrast">Proceed</button>
                    <a href="/auth/login" role="button" class="outline contrast wide">Back</a>
                </form>
            </div>
        </article>
    </main>
`)
