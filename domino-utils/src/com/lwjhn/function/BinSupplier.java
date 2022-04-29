package com.lwjhn.function;

import java.io.Serializable;

@FunctionalInterface
public interface BinSupplier<T,R> extends Serializable {
    void set(T t, R r);
}
