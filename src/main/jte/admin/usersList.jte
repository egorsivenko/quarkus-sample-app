@import io.micronaut.views.fields.FormGenerator
@import io.micronaut.views.fields.messages.Message
@import pragmasoft.k1teauth.admin.form.DeleteUserForm
@import pragmasoft.k1teauth.user.User
@import java.time.format.DateTimeFormatter
@import java.util.List

@param List<User> users
@param FormGenerator formGenerator

@template.layout(title = "Users list",
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
                        @template.fieldset.form(form = formGenerator.generate("/admin/delete-user", new DeleteUserForm(user.getId()), Message.of("Delete")))
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