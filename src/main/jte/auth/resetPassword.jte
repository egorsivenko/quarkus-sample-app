@import java.util.UUID
@import java.lang.Iterable

@param UUID userId
@param Iterable<String> errors

@template.layout(title = "Reset password",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Reset password</h1>
                <form action="/auth/reset-password" method="post" autocomplete="off">
                    @if(errors != null)
                        @for(var error : errors)
                            <p class="error">${error}</p>
                        @endfor
                    @endif
                    <input type="hidden" name="userId" value="${userId.toString()}"/>
                    <label for="password">New password</label>
                    <input type="password" id="password" name="password" minlength="6" maxlength="50" placeholder="New password" required/>
                    <input type="password" id="confirm-password" name="confirmPassword" minlength="6" maxlength="50"
                           placeholder="Confirm new password" required aria-describedby="invalid-helper-confirm"/>
                    <small id="invalid-helper-confirm">Passwords don’t match.</small>
                    <button type="submit" class="contrast" id="submit-button">Reset password</button>
                    <a href="/auth/forgot-password" role="button" class="outline contrast wide">Cancel</a>
                </form>
            </div>
        </article>
    </main>

    <script type="text/javascript" src="/js/validation.js"></script>
`)
