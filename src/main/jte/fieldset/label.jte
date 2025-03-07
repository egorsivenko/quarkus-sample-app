@import io.micronaut.views.fields.messages.Message
@param String id
@param Message el
<label for="${id}" class="form-label">${el.defaultMessage()}</label>
