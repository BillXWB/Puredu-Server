package edu.pure.server.opedukg.service;

import edu.pure.server.opedukg.payload.OpedukgResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
class OpedukgClientLoggedIn extends OpedukgClient {
    private static final Logger logger = LoggerFactory.getLogger(OpedukgClientLoggedIn.class);

    private final LoginService loginService;

    @Value("${opedukg-client.login-service.phone-number}")
    private String phoneNumber;

    @Value("${opedukg-client.login-service.password}")
    private String password;

    private String id;

    @Override
    <T extends OpedukgResponse<?>>
    T get(final String url, final Class<T> responseType,
          final @NotNull Map<String, String> params_) {
        final Map<String, String> params = this.withId(params_);
        return this.doOrLoginAndRedo(() -> super.get(url, responseType, params));
    }

    @Override
    <T extends OpedukgResponse<?>>
    T post(final String url, final Class<T> responseType,
           final @NotNull Map<String, String> params_) {
        final Map<String, String> params = this.withId(params_);
        return this.doOrLoginAndRedo(() -> super.post(url, responseType, params));
    }

    private Map<String, String> withId(final @NotNull Map<String, String> params) {
        return Stream.concat(Map.of("id", this.id).entrySet().stream(),
                             params.entrySet().stream())
                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private <T extends OpedukgResponse<?>>
    T doOrLoginAndRedo(final @NotNull Supplier<T> responseSupplier) {
        T response = responseSupplier.get();
        if (response.getCode() == -1) {
            this.login();
            response = responseSupplier.get();
        }
        return response;
    }

    @PostConstruct
    private void login() {
        this.id = this.loginService.login(this.phoneNumber, this.password);
        OpedukgClientLoggedIn.logger.info("Logged in OpenEduKG with id: " + this.id);
    }
}
