@import pragmasoft.k1teauth.user.User
@import java.time.format.DateTimeFormatter
@import java.util.List

@param String csrfToken
@param List<User> users

@template.layout(title = "Users list",
styles = @`
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <h3>Users list</h3>
    <a href="/profile" role="button" class="outline contrast">Back</a>
    <table>
        <thead>
        <tr>
            <th scope="col">Full name</th>
            <th scope="col">Email</th>
            <th scope="col">Role</th>
            <th scope="col">Verified</th>
            <th scope="col">Registration date</th>
            <th scope="col">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for(var user : users)
            <tr>
                <th scope="row">${user.getFullName()}</th>
                <td>${user.getEmail()}</td>
                <td>${user.getRole()}</td>
                <td>${user.isVerified()}</td>
                <td>${user.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))}</td>
                <td>
                    @if(!user.isAdmin())
                        <form action="/admin/delete-user" method="post"
                              onsubmit="return confirm('Are you sure you want to delete this user?');">
                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                            <input type="hidden" name="id" value="${user.getId().toString()}"/>
                            <button type="submit" class="outline">Delete</button>
                        </form>
                        <a href="/admin/edit-user?id=${user.getId().toString()}" role="button" class="outline wide">Edit</a>
                    @else
                        <i>Administrator</i>
                    @endif
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)