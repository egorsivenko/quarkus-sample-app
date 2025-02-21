@import pragmasoft.k1teauth.oauth.scope.Scope
@import java.util.List

@param String csrfToken
@param List<Scope> scopes

@template.layout(title = "OAuth scopes",
styles = @`
    <link rel="stylesheet" href="/css/extra-styles.css"/>
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
                    <form action="/oauth2/scopes/delete" method="post"
                          onsubmit="return confirm('Are you sure you want to delete this scope?');">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="name" value="${scope.getName()}"/>
                        <button type="submit" class="outline">Delete</button>
                    </form>
                    <a href="/oauth2/scopes/edit?name=${scope.getName()}" role="button" class="outline wide">Edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)