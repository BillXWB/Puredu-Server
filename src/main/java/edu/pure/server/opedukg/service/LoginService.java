package edu.pure.server.opedukg.service;

import edu.pure.server.opedukg.payload.OpedukgResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
class LoginService {
    private static final String URL = "/api/typeAuth/user/login";

    private final OpedukgClient client;

    String login(final String phoneNumber, final String password) {
        final Response response = this.client.post(LoginService.URL, Response.class,
                                                   Map.of("phone", phoneNumber,
                                                          "password", password));
        return response.getId();
    }

    @Getter
    private static class Response extends OpedukgResponse<String> {
        private String id;
    }
}
