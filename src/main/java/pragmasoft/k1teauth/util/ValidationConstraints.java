package pragmasoft.k1teauth.util;

public final class ValidationConstraints {

    private ValidationConstraints() {
        throw new IllegalStateException("Utility class");
    }

    public static final String FULL_NAME_SIZE_MESSAGE = "Full name length must be between {min} and {max} characters.";
    public static final String EMAIL_SIZE_MESSAGE = "Email length must be between {min} and {max} characters.";
    public static final String PASSWORD_SIZE_MESSAGE = "Password length must be between {min} and {max} characters.";
    public static final String CLIENT_NAME_SIZE_MESSAGE = "Client name length must be between {min} and {max} characters.";

    public static final String FULL_NAME_NOT_BLANK_MESSAGE = "Full name may not be blank.";
    public static final String EMAIL_NOT_BLANK_MESSAGE = "Email may not be blank.";
    public static final String PASSWORD_NOT_BLANK_MESSAGE = "Password may not be blank.";
    public static final String ROLE_NOT_BLANK_MESSAGE = "User role may not be blank.";
    public static final String CLIENT_NAME_NOT_BLANK_MESSAGE = "Client name may not be blank.";
    public static final String CALLBACK_URL_NOT_BLANK_MESSAGE = "Callback URL may not be blank.";

    public static final String URL_FORMAT_MESSAGE = "Invalid URL format.";
    public static final String URL_MAX_LENGTH_MESSAGE = "URL length must be less than or equal to {max} characters.";
}
