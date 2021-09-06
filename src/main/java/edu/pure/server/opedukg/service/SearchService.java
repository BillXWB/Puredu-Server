package edu.pure.server.opedukg.service;

import edu.pure.server.opedukg.entity.KnowledgeBaseEntity;
import edu.pure.server.opedukg.entity.SearchResult;
import edu.pure.server.opedukg.payload.OpedukgResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SearchService {
    private static final String URL = "/api/typeOpen/open/instanceList";

    private final OpedukgClientLoggedIn client;

    public List<SearchResult> search(final String course, final String keyword) {
        final OpedukgResponse<List<Data>> response = this.client.get(SearchService.URL,
                                                                     Response.class,
                                                                     Map.of("course", course,
                                                                            "searchKey", keyword));
        if (response.getData() == null) {
            return List.of();
        }
        return response.getData().stream()
                       .map(d -> new SearchResult(new KnowledgeBaseEntity(d.getLabel(), d.getUri()),
                                                  d.getCategory())
                       ).collect(Collectors.toList());
    }

    private static class Response extends OpedukgResponse<List<Data>> {}

    @Getter
    private static class Data {
        private String label;
        private String category;
        private String uri;
    }
}
