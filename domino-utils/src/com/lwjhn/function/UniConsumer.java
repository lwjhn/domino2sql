package com.lwjhn.function;

@FunctionalInterface
public interface UniConsumer<T, X> {
    void accept(T t, X x);
}