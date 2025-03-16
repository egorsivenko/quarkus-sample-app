@import java.util.List
@import io.micronaut.views.fields.Form
@import io.micronaut.views.fields.messages.Message

@param Form form
@param List<Message> errors

@template.layout(title = "Edit scope",
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Edit scope</h1>
                @template.fieldset.errors(errors = errors)
                @template.fieldset.form(form = form)
                <a href="/oauth2/scopes" role="button" class="outline contrast wide">Cancel</a>
            </div>
        </article>
    </main>
`)
