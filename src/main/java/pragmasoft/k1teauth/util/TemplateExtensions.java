package pragmasoft.k1teauth.util;

import io.quarkus.qute.TemplateExtension;
import pragmasoft.k1teauth.oauth.scope.Scope;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@TemplateExtension
public class TemplateExtensions {

    private TemplateExtensions() {
        throw new IllegalStateException("Utility class");
    }

    public static String join(Collection<String> elements, String delimiter) {
        return String.join(delimiter, elements);
    }

    public static String mapToString(Set<Scope> scopes) {
        return scopes.stream()
                .map(scope -> scope.name)
                .collect(Collectors.joining(" "));
    }
}
