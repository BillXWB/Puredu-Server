package edu.pure.server.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageFactory {
    public static <T> @NotNull Page<T> fromList(final @NotNull List<T> list,
                                                final @NotNull Pageable pageable) {
        final var begin = (int) Math.min(pageable.getOffset(), list.size());
        final var end = (int) Math.min(pageable.getOffset() + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(begin, end), pageable, list.size());
    }
}
