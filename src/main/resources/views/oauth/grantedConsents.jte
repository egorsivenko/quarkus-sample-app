@import pragmasoft.k1teauth.oauth.consent.Consent
@import java.time.format.DateTimeFormatter
@import java.util.List
@import java.util.Locale

@param String csrfToken
@param List<Consent> consents

@template.layout(title = "Granted consents",
styles = @`
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
body = @`
    <h3>Granted consents</h3>
    <a href="/profile" role="button" class="outline contrast">Back</a>
    <table>
        @if(consents.isEmpty())
            <caption>
                <p>You have not granted any consent</p>
            </caption>
        @endif
        <thead>
        <tr>
            <th scope="col">Application name</th>
            <th scope="col">Permissions</th>
            <th scope="col">Grant date</th>
            <th scope="col">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for(var consent : consents)
            <tr>
                <th scope="row">${consent.getClient().getName()}</th>
                <td>
                    @for(var scope : consent.getScopes())
                        <div>${scope.getDescription()}</div>
                    @endfor
                </td>
                <td>${consent.getGrantedAt().format(DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.ENGLISH))}</td>
                <td>
                    <form action="/granted-consents/revoke" method="post"
                          onsubmit="return confirm('Are you sure you want to revoke this consent?');">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="id" value="${consent.getId().toString()}"/>
                        <button type="submit" class="outline">Revoke</button>
                    </form>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)