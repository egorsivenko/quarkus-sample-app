package pragmasoft.k1teauth.email.smtp;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("smtp")
public class SmtpConfig {

    private boolean auth;
    private StarttlsConfig starttls;
    private SessionConfig session;
    private String host;
    private int port;

    @ConfigurationProperties("starttls")
    public static class StarttlsConfig {

        private boolean enable;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    @ConfigurationProperties("session")
    public static class SessionConfig {

        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public StarttlsConfig getStarttls() {
        return starttls;
    }

    public void setStarttls(StarttlsConfig starttls) {
        this.starttls = starttls;
    }

    public SessionConfig getSession() {
        return session;
    }

    public void setSession(SessionConfig session) {
        this.session = session;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
