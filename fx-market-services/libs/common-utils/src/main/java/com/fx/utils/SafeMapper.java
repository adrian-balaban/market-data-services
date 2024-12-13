package com.fx.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

// Do not use method reference as input Supplier!
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SafeMapper {
    private static final String NULL = "null";

    public static <T> T nullSafe(Supplier<T> source) {
        try {
            return source.get();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static <T> T nullSafeArray(Supplier<T> source) {
        try {
            return source.get();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return null;
        }
    }
}
