package edu.pure.server.opedukg.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class OpedukgAnswer implements Comparable<OpedukgAnswer> {
    private final KnowledgeBaseEntity entity;
    private final EntityProperty property;
    private double score;

    @Override
    public int compareTo(final @NotNull OpedukgAnswer other) {
        return Double.compare(other.getScore(), this.getScore());
    }
}
