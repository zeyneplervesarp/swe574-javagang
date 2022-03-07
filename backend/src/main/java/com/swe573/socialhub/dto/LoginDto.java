package com.swe573.socialhub.dto;

public class LoginDto {
    private String password;
    private String username;

    public LoginDto(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
