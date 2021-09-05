package edu.pure.server.opedukg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.pure.server.util.ChineseAlphabeticalComparator;
import edu.pure.server.util.CompareOverloader;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "entity")
public class SearchResult {
    private final KnowledgeBaseEntity entity;
    private final String category;

    @JsonIgnore
    public CompareOverloader<String, ChineseAlphabeticalComparator> getAlphabet() {
        return new CompareOverloader<>(this.getEntity().getName(),
                                       new ChineseAlphabeticalComparator());
    }

    @JsonIgnore
    public int getLength() {
        return this.getEntity().getName().length();
    }
}
