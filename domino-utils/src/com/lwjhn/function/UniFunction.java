package com.lwjhn.function;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface UniFunction<T, R> extends Function<T, R>, Serializable {
}
