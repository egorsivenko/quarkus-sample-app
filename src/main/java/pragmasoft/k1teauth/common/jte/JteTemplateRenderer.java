package pragmasoft.k1teauth.common.jte;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;
import jakarta.inject.Singleton;

import java.util.Map;

@Singleton
public final class JteTemplateRenderer {

    private final TemplateEngine templateEngine = TemplateEngine.createPrecompiled(ContentType.Html);

    public String render(String templateName, Object model) {
        TemplateOutput output = new StringOutput();
        templateEngine.render(templateName, model, output);
        return output.toString();
    }

    public String render(String templateName, Map<String, Object> model) {
        TemplateOutput output = new StringOutput();
        templateEngine.render(templateName, model, output);
        return output.toString();
    }
}
