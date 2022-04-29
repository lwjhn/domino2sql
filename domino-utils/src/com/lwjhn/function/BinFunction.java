package com.lwjhn.function;

import java.io.Serializable;
import java.util.function.BiFunction;

@FunctionalInterface
public interface BinFunction<T, U, R> extends BiFunction<T, U, R>, Serializable {
}
