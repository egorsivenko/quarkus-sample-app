package pragmasoft.k1teauth.util;

public final class EmailValidator {

    private EmailValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValid(String email) {
        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');

        if (atIndex <= 0 || atIndex != email.lastIndexOf('@') || atIndex == email.length() - 1) {
            return false;
        }
        return dotIndex > atIndex + 1 && dotIndex != email.length() - 1;
    }
}
