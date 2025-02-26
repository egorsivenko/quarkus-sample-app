@import java.util.List
@import io.micronaut.views.fields.messages.Message
@param String name = null
@param List<Message> errors
@if(errors != null)
    @for(var error : errors)
        <p id="${name}ValidationServerFeedback" class="invalid-feedback">${error.defaultMessage()}</p>
    @endfor
@endif
