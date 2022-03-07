package com.swe573.socialhub.dto;

public class JwtDto {
    public JwtDto(String jwt) {
        this.jwt = jwt;
    }
    private String jwt;

    public String getJwt() {
        return jwt;
    }
}
