package edu.pure.server.opedukg.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
class OpedukgClient {
    private static final String ROOT_URI = "http://open.edukg.cn/opedukg";

    private final RestTemplate client =
            new RestTemplateBuilder().rootUri(OpedukgClient.ROOT_URI).build();

    @Value("${opedukg-service.id}")
    private String id;

    <T> T get(final String url, final Class<T> responseType, Map<String, String> params) {
        params = Stream.concat(Map.of("id", this.id).entrySet().stream(),
                               params.entrySet().stream())
                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this.getWithoutId(url, responseType, params);
    }

    private <T> T getWithoutId(String url, final Class<T> responseType,
                               final @NotNull Map<String, String> params_) {
        final Map<String, List<String>> params
                = params_.entrySet().stream()
                         .collect(Collectors.toMap(Map.Entry::getKey,
                                                   entry -> List.of(entry.getValue())));
        url = UriComponentsBuilder.fromPath(url)
                                  .queryParams(new MultiValueMapAdapter<>(params))
                                  .toUriString();
        return this.client.getForObject(url, responseType, params);
    }

    <T> T post(final String url, final Class<T> responseType, Map<String, String> params) {
        params = Stream.concat(Map.of("id", this.id).entrySet().stream(),
                               params.entrySet().stream())
                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this.postWithoutId(url, responseType, params);
    }

    <T> T postWithoutId(final String url, final Class<T> responseType,
                        final Map<String, String> params) {
        return this.client.postForObject(url, params, responseType);
    }
}
