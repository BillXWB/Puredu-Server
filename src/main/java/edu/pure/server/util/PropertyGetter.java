package edu.pure.server.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
public class PropertyGetter<T, R> implements Function<T, R> {
    private String property;

    @SuppressWarnings("unchecked")
    @SneakyThrows({NoSuchFieldException.class,
                   IllegalAccessException.class,
                   InvocationTargetException.class})
    @Override
    public R apply(final @NotNull T arg) {
        final PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(arg.getClass(),
                                                                              this.getProperty());
        if (descriptor == null) {
            throw new NoSuchFieldException(this.getProperty());
        }
        final Method getter = descriptor.getReadMethod();
        return (R) getter.invoke(arg);
    }
}
