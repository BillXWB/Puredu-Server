package edu.pure.server.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ApiResponse<T> {
    private final T data;
    private final String message;

    @Contract(" -> new")
    public static @NotNull ApiResponse<?> success() {
        return ApiResponse.success(null);
    }

    @Contract("_ -> new")
    public static <T> @NotNull ApiResponse<T> success(final T data) {
        return new ApiResponse<>(data, "");
    }

    @Contract("_ -> new")
    public static @NotNull ApiResponse<?> failure(final @NotNull String message) {
        return new ApiResponse<>(null, message);
    }
}
