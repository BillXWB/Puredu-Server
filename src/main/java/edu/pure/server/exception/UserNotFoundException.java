package edu.pure.server.exception;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {
    private UserNotFoundException(final String key, final Object value) {
        super(String.format("User not found with %s: %s", key, value));
    }

    @Contract("_ -> new")
    public static @NotNull UserNotFoundException withId(final Long id) {
        return new UserNotFoundException("id", id);
    }

    @Contract("_ -> new")
    public static @NotNull UserNotFoundException withUsername(final String username) {
        return new UserNotFoundException("username", username);
    }
}
