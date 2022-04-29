package com.lwjhn.function;

@FunctionalInterface
public interface BiConsumer<T, X, Y> {
    void accept(T t, X x, Y y);
}