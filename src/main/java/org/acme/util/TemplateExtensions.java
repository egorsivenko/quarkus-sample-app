package org.acme.util;

import io.quarkus.qute.TemplateExtension;

import java.util.Collection;

@TemplateExtension
public class TemplateExtensions {

    private TemplateExtensions() {
        throw new IllegalStateException("Utility class");
    }

    public static String join(Collection<String> elements, String delimiter) {
        return String.join(delimiter, elements);
    }
}
