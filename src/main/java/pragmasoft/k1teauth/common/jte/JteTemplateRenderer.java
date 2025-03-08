package pragmasoft.k1teauth.common.jte;

import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import jakarta.inject.Singleton;

import java.nio.file.Path;
import java.util.Map;

@Singleton
public final class JteTemplateRenderer {

    private final CodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src/main/jte"));
    private final TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);

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
