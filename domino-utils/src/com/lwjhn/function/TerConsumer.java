package com.lwjhn.function;

@FunctionalInterface
public interface TerConsumer<T, X, Y, Z> {
    void accept(T t, X x, Y y, Z z);
}