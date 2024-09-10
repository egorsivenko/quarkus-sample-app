package org.acme.auth;

import io.quarkus.arc.Arc;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import lombok.experimental.UtilityClass;
import org.acme.user.UserRole;

@UtilityClass
@TemplateExtension(namespace = "user")
public class AuthExtensions {

    public static String email() {
        try (var instance = Arc.container().instance(CurrentIdentityAssociation.class)) {
            return instance.get().getIdentity().getPrincipal().getName();
        }
    }

    public static boolean isAdmin() {
        try (var instance = Arc.container().instance(CurrentIdentityAssociation.class)) {
            return instance.get().getIdentity().getRoles().contains(UserRole.ADMIN.toString());
        }
    }
}
