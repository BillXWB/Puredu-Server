package edu.pure.server.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PageFactory {
    public static <T> @NotNull Page<T> fromList(final @NotNull List<T> list,
                                                final @NotNull Pageable pageable) {
        final Comparator<? super T> comparator = pageable.getSort().stream()
                                                         .map(Orders::toComparator)
                                                         .reduce(Comparator::thenComparing)
                                                         .orElse(new IdentityComparator<>());
        final var begin = (int) Math.min(pageable.getOffset(), list.size());
        final var end = (int) Math.min(pageable.getOffset() + pageable.getPageSize(), list.size());
        final List<T> content = list.stream()
                                    .sorted(comparator)
                                    .collect(Collectors.toList())
                                    .subList(begin, end);
        return new PageImpl<>(content, pageable, list.size());
    }
}
