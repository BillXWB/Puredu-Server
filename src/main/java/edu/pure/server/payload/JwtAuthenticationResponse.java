package edu.pure.server.payload;

import lombok.Getter;

@Getter
public class JwtAuthenticationResponse {
    private final String tokenType = "Bearer";
    private final String accessToken;

    public JwtAuthenticationResponse(final String accessToken) {
        this.accessToken = accessToken;
    }
}
