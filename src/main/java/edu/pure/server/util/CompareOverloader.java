package edu.pure.server.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@Getter
@AllArgsConstructor
public class CompareOverloader<T, U extends Comparator<? super T>>
        implements Comparable<CompareOverloader<T, U>> {
    private final T value;
    private final U comparator;

    @Override
    public int compareTo(final @NotNull CompareOverloader<T, U> other) {
        return this.comparator.compare(this.getValue(), other.getValue());
    }
}
