@import java.util.List
@import io.micronaut.views.fields.Form
@import io.micronaut.views.fields.messages.Message

@param Form form
@param List<Message> errors

@template.layout(title = "Edit user",
body = @`
    <main class="container">
        <article>
            <div>
                <h1>Edit user</h1>
                @template.fieldset.errors(errors = errors)
                @template.fieldset.form(form = form)
                <a href="/admin/users-list" role="button" class="outline contrast wide">Cancel</a>
            </div>
        </article>
    </main>
`)
