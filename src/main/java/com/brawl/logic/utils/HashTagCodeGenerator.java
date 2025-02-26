package com.brawl.logic.utils;

public class HashTagCodeGenerator {

    // HASH_TAG, CONVERSION_CHARS
    private static final LogicLongToCodeConverterUtil longToCodeConverterUtil = new LogicLongToCodeConverterUtil("#",
            "0289PYLQGRJCUV".toCharArray());

    public static String toCode(int hi, int lo) {
        return longToCodeConverterUtil.toCode(hi, lo);
    }

    public static long toId(String tag) {
        return longToCodeConverterUtil.toId(tag);
    }

}