package pragmasoft.k1teauth.user;

public enum UserRole {
    ADMIN, USER;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
