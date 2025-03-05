@import io.micronaut.views.fields.Form
@param Form form
@param Boolean useTurboDrive = true
@param boolean useCaptcha = false
@param String siteKey = null

<form action="${form.action()}" method="${form.method()}" enctype="${form.enctype()}" autocomplete="off"
      data-turbo="${useTurboDrive.toString()}">
    @template.fieldset.fieldset(el = form.fieldset(), useCaptcha = useCaptcha, siteKey = siteKey)
</form>
