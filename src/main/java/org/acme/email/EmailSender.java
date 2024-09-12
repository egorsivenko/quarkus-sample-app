package org.acme.email;

@FunctionalInterface
public interface EmailSender {

    void send(String to, String link);
}
