@import io.micronaut.data.model.Page
@import io.micronaut.views.fields.FormGenerator
@import io.micronaut.views.fields.messages.Message
@import pragmasoft.k1teauth.oauth.consent.Consent
@import pragmasoft.k1teauth.oauth.consent.form.RevokeConsentForm
@import java.time.format.DateTimeFormatter
@import java.util.Locale

@param Page<Consent> page
@param FormGenerator formGenerator

@template.layout(title = "Granted consents",
body = @`
    <h3>Granted consents</h3>
    <a href="/profile" role="button" class="outline contrast">Back</a>
    <table>
        @template.caption(path = "/granted-consents", page = page, emptyContentMessage = "You have not granted any consent")
        <thead>
        <tr>
            <th scope="col">Application name</th>
            <th scope="col">Permissions</th>
            <th scope="col">Grant date</th>
            <th scope="col">Actions</th>
        </tr>
        </thead>
        <tbody>
        @for(var consent : page.getContent())
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