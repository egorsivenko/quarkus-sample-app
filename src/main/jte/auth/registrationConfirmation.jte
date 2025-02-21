@import java.util.UUID

@param UUID userId

@template.layout(title = "Confirm email",
styles = @`
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <h4>You need to confirm your email address.</h4>
    <h5>Email with confirmation link was sent, please check your inbox.</h5>

    <p id="countdown"></p>
    <form action="/auth/resend-registration-email" class="resend-email-form" method="post">
        <input type="hidden" name="userId" value="${userId.toString()}">
        <button type="submit" id="resendButton">Resend Email</button>
    </form>
    <a href="/auth/login" role="button" class="secondary">Cancel</a>

    <script type="text/javascript" src="/js/countdown.js"></script>
`)
