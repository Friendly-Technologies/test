package com.friendly.services.infrastructure.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NumberConversionRegistry {
    private static final Map<Class<?>, Function<Object, Long>> convertersToLong = new HashMap<>();
    private static final Map<Class<?>, Function<Object, Integer>> convertersToInt = new HashMap<>();

    static {
        registerLongConverter(Integer.class, obj -> ((Integer) obj).longValue());
        registerLongConverter(Long.class, Long.class::cast);
        registerLongConverter(Short.class, obj -> ((Short) obj).longValue());
        registerLongConverter(Byte.class, obj -> ((Byte) obj).longValue());
        registerLongConverter(Float.class, obj -> ((Float) obj).longValue());
        registerLongConverter(Double.class, obj -> ((Double) obj).longValue());
        registerLongConverter(BigInteger.class, obj -> ((BigInteger) obj).longValue());
        registerLongConverter(BigDecimal.class, obj -> ((BigDecimal) obj).longValue());

        registerIntConverter(Integer.class, obj -> (Integer) obj);
        registerIntConverter(Long.class, obj -> ((Long) obj).intValue());
        registerIntConverter(Short.class, obj -> ((Short) obj).intValue());
        registerIntConverter(Byte.class, obj -> ((Byte) obj).intValue());
        registerIntConverter(Float.class, obj -> ((Float) obj).intValue());
        registerIntConverter(Double.class, obj -> ((Double) obj).intValue());
        registerIntConverter(BigInteger.class, obj -> ((BigInteger) obj).intValue());
        registerIntConverter(BigDecimal.class, obj -> ((BigDecimal) obj).intValue());
    }

    public static void registerLongConverter(Class<?> clazz, Function<Object, Long> converter) {
        convertersToLong.put(clazz, converter);
    }

    public static void registerIntConverter(Class<?> clazz, Function<Object, Integer> converter) {
        convertersToInt.put(clazz, converter);
    }

    public static long convertToLong(Object obj) {
        return convertersToLong.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(obj))
                .map(entry -> entry.getValue().apply(obj))
                .findFirst()
                .orElse(0L);
    }

    public static int convertToInt(Object obj) {
        return convertersToInt.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(obj))
                .map(entry -> entry.getValue().apply(obj))
                .findFirst()
                .orElse(0);
    }
}
