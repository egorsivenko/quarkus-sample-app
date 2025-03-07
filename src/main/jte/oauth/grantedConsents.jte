@import io.micronaut.views.fields.FormGenerator
@import io.micronaut.views.fields.messages.Message
@import pragmasoft.k1teauth.oauth.consent.Consent
@import pragmasoft.k1teauth.oauth.consent.form.RevokeConsentForm
@import java.time.format.DateTimeFormatter
@import java.util.List
@import java.util.Locale

@param List<Consent> consents
@param FormGenerator formGenerator

@template.layout(title = "Granted consents",
styles = @`
    <link rel="stylesheet" href="/css/extra-styles.css"/>
`,
scripts = @`
    <script src="https://unpkg.com/@hotwired/turbo@8.0.11/dist/turbo.es2017-umd.js"></script>
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
                    @template.fieldset.form(form = formGenerator.generate("/granted-consents/revoke", new RevokeConsentForm(consent.getId()), Message.of("Revoke")))
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)