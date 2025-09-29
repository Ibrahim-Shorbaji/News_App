package com.global.news_app.payload.response;

import java.time.LocalDate;
import java.util.Set;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private Set<String> roles;

    public UserResponse(Long id, String username, String email,
                        LocalDate dateOfBirth, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.roles = roles;
    }

    // Getters only (read-only response)
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Set<String> getRoles() { return roles; }
}
