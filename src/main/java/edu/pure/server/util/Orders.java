package edu.pure.server.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;

import java.util.Comparator;

class Orders {
    private static final String DEFAULT = "default";

    static <T> Comparator<T> toComparator(final Sort.@NotNull Order order) {
        Comparator<T> comparator;
        if (order.getProperty().equals(Orders.DEFAULT)) {
            comparator = new IdentityComparator<>();
        } else {
            comparator = Comparator.comparing(new PropertyGetter<>(order.getProperty()));
        }
        if (order.isDescending()) {
            comparator = comparator.reversed();
        }
        return comparator;
    }
}
