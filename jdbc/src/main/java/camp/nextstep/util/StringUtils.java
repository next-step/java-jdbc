package camp.nextstep.util;

import java.util.Arrays;

public class StringUtils {

    private static final String STRING_SPLIT_VALUE = "";

    private StringUtils() {
        throw new AssertionError();
    }

    public static long countContainSequence(String value, CharSequence sequence) {
        return Arrays.stream(value.split(STRING_SPLIT_VALUE))
                .filter(it -> it.contentEquals(sequence))
                .count();
    }
}
