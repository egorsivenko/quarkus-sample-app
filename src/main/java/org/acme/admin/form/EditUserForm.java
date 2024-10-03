package org.acme.admin.form;

import org.jboss.resteasy.reactive.RestForm;

import java.util.UUID;

public class EditUserForm {

    @RestForm UUID id;
    @RestForm String fullName;
    @RestForm String email;
    @RestForm String role;

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}