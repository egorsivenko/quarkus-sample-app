@import io.micronaut.views.fields.FormGenerator
@import io.micronaut.views.fields.messages.Message
@import pragmasoft.k1teauth.oauth.scope.Scope
@import pragmasoft.k1teauth.oauth.scope.form.DeleteScopeForm
@import java.util.List

@param List<Scope> scopes
@param FormGenerator formGenerator

@template.layout(title = "OAuth scopes",
scripts = @`
    <script src="https://unpkg.com/@hotwired/turbo@8.0.11/dist/turbo.es2017-umd.js"></script>
`,
body = @`
    <h3>OAuth scopes</h3>
    <a href="/profile" role="button" class="outline contrast">Back</a>
    <a href="/oauth2/scopes/new" role="button" class="outline contrast">New scope</a>
    <table>
        @if(scopes.isEmpty())
            <caption>
                <p>No scopes yet</p>
            </caption>
        @endif
        <thead>
        <tr>
            <th scope="col">Name</th>
            <th scope="col">Description</th>
            <th scope="col">Audience</th>
            <th scope="col">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for(var scope : scopes)
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