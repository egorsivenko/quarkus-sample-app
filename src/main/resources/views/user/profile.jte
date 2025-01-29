@import pragmasoft.k1teauth.user.User

@param String csrfToken
@param User user

@template.layout(title = "Profile",
styles = @`
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <h4>Hello, ${user.getFullName()} &#x1F44B;</h4>
    <h4>Email: ${user.getEmail()}</h4>
    <a href="/profile/change-password" role="button" class="outline contrast">Change password</a>
    @if (user.isAdmin())
        <hr>
        <a href="/admin/users-list" role="button" class="outline contrast">Users list</a>
    @endif
    <form action="/logout" class="logout-form" method="post">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <button type="submit" class="outline">Logout</button>
    </form>
`)