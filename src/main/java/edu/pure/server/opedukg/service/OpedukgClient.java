package edu.pure.server.opedukg.service;

import edu.pure.server.opedukg.payload.OpedukgResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Component
class OpedukgClient {
    private static final String ROOT_URI = "http://open.edukg.cn/opedukg";

    private final RestTemplate client =
            new RestTemplateBuilder().rootUri(OpedukgClient.ROOT_URI).build();

    <T extends OpedukgResponse<?>>
    T get(final String url_, final Class<T> responseType,
          final @NotNull Map<String, String> params_) {
        final Map<String, List<String>> params
                = params_.entrySet().stream()
                         .collect(Collectors.toMap(Map.Entry::getKey,
                                                   entry -> List.of(entry.getValue())));
        final String url = UriComponentsBuilder.fromPath(url_)
                                               .queryParams(new MultiValueMapAdapter<>(params))
                                               .toUriString();
        return this.client.getForObject(url, responseType, params);
    }

    <T extends OpedukgResponse<?>>
    T post(final String url, final Class<T> responseType,
           final @NotNull Map<String, String> params) {
        return this.client.postForObject(url, params, responseType);
    }
}
