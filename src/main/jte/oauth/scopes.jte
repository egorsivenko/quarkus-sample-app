@import io.micronaut.data.model.Page
@import io.micronaut.views.fields.FormGenerator
@import io.micronaut.views.fields.messages.Message
@import pragmasoft.k1teauth.oauth.scope.Scope
@import pragmasoft.k1teauth.oauth.scope.form.DeleteScopeForm

@param Page<Scope> page
@param FormGenerator formGenerator

@template.layout(title = "OAuth scopes",
body = @`
    <h3>OAuth scopes</h3>
    <a href="/profile" role="button" class="outline contrast">Back</a>
    <a href="/oauth2/scopes/new" role="button" class="outline contrast">New scope</a>
    <table>
        @template.caption(path = "/oauth2/scopes", page = page, emptyContentMessage = "No scopes yet")
        <thead>
        <tr>
            <th scope="col">Name</th>
            <th scope="col">Description</th>
            <th scope="col">Audience</th>
            <th scope="col">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for(var scope : page.getContent())
            <tr>
                <th scope="row">${scope.getName()}</th>
                <td>${scope.getDescription()}</td>
                <td>${scope.getAudience()}</td>
                <td>
                    @template.fieldset.form(form = formGenerator.generate("/oauth2/scopes/delete", new DeleteScopeForm(scope.getName()), Message.of("Delete")))
                    <a href="/oauth2/scopes/edit?name=${scope.getName()}" role="button" class="outline wide">Edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)