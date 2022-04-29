package com.lwjhn.function;

import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface VoiSupplier<T> extends Supplier<T>, Serializable {
}
