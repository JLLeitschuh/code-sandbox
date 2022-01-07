package org.jlleitschuh.sandbox;

import java.util.function.Function;
import java.util.regex.Pattern;

public final class JvmAdditionalOptionsCleaner {

    private static final Function<String, Function<String, String>> BLANK_REPLACING_BUILDER = regex -> string ->
        Pattern.compile(regex).matcher(string).replaceAll("");

    private static final Function<String, String> DBL_QUOTED_ON_OUT_OF_MEMORY = BLANK_REPLACING_BUILDER.apply("-XX:OnOutOfMemoryError=\"(?:[^\"\\\\]|\\\\.)*\"\\s*");
    private static final Function<String, String> SNG_QUOTED_ON_OUT_OF_MEMORY = BLANK_REPLACING_BUILDER.apply("-XX:OnOutOfMemoryError='(?:[^'\\\\]|\\\\.)*'\\s*");
    private static final Function<String, String> DBL_QUOTED_ON_ERROR = BLANK_REPLACING_BUILDER.apply("-XX:OnError=\"(?:[^\"\\\\]|\\\\.)*\"\\s*");
    private static final Function<String, String> SNG_QUOTED_ON_ERROR = BLANK_REPLACING_BUILDER.apply("-XX:OnError='(?:[^'\\\\]|\\\\.)*'\\s*");

    // By the time it gets here, there should be no more OOOM or OE arguments.  But there will be if the attacker omitted the end quote
    // This will remove any that are left, and probably leave us with invalid (but safe) arguments
    private static final Function<String, String> NUCLEAR_OOME_CLEANER = BLANK_REPLACING_BUILDER.apply("-XX:OnOutOfMemoryError.*");
    private static final Function<String, String> NUCLEAR_OE_CLEANER = BLANK_REPLACING_BUILDER.apply("-XX:OnError.*");


    private static final Function<String, String> JVM_ADDITIONAL_OPTIONS_FILTER =
        DBL_QUOTED_ON_OUT_OF_MEMORY
            .andThen(SNG_QUOTED_ON_OUT_OF_MEMORY)
            .andThen(DBL_QUOTED_ON_ERROR)
            .andThen(SNG_QUOTED_ON_ERROR)
            .andThen(NUCLEAR_OOME_CLEANER)
            .andThen(NUCLEAR_OE_CLEANER);

    /**
     * Removes -XX:OnError and -XX:OnOutOfMemoryError arguments from the JVM args as
     * these represent a security risk (as they can run arbitrary shell calls)
     *
     * @param additionalOptions the original additional jvm options
     * @return a cleaned version with the insecure arguments removed
     */
    public static String clean(String additionalOptions) {
        return JVM_ADDITIONAL_OPTIONS_FILTER.apply(additionalOptions).trim();
    }

}
