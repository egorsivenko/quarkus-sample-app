@import java.lang.Iterable
@import pragmasoft.k1teauth.user.User
@import pragmasoft.k1teauth.user.User.Role

@param String csrfToken
@param User user
@param Role[] roles
@param Iterable<String> errors

@template.layout(title = "Edit user",
styles = @`
    <link rel="stylesheet" href="/css/styles.css"/>
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Edit user</h1>
                <form action="/admin/edit-user" method="post" autocomplete="off">
                    @if(errors != null)
                        @for(var error : errors)
                            <p class="error">${error}</p>
                        @endfor
                    @endif
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="id" value="${user.getId().toString()}" />
                    <label for="fullName">Full name</label>
                    <input type="text" id="fullName" name="fullName" minlength="1" maxlength="50" value="${user.getFullName()}"
                           placeholder="Full name"  autofocus />
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" maxlength="50" value="${user.getEmail()}"
                           placeholder="Email" required />
                    <label for="role">Role</label>
                    <select id="role" name="role" required>
                        <option value="${user.getRole()}" selected>${user.getRole()}</option>
                        @for(var role : roles)
                            @if(role != user.getRole())
                                <option value="${role}">${role}</option>
                            @endif
                        @endfor
                    </select>
                    <button type="submit" class="contrast">Edit user</button>
                    <a href="/admin/users-list" role="button" class="outline contrast wide">Cancel</a>
                </form>
            </div>
        </article>
    </main>
`)
