package fr.boul2gom.cerberus.api.utils;

import java.util.function.Function;

public class EnumUtils {

    public static <T extends Enum<T>, V> T parse(V wanted, Function<T, V> parser, T defaultValue, Class<T> clazz) {
        for (final T value : clazz.getEnumConstants()) {
            if (parser.apply(value).equals(wanted)) {
                return value;
            }
        }

        return defaultValue;
    }

    public static <T extends Enum<T>, V> T parse(V wanted, Function<T, V> parser, T defaultValue) {
        return EnumUtils.parse(wanted, parser, defaultValue, defaultValue.getDeclaringClass());
    }
}
