package com.brawl.logic.utils;

public class LogicLongToCodeConverterUtil {

    private char chars[];
    private String startChar;

    public LogicLongToCodeConverterUtil(String startChar, char[] chars) {
        this.chars = chars;
        this.startChar = startChar;
    }

    public String toCode(int HigherInt, int LowerInt) {
        return convert((long) HigherInt | ((long) LowerInt << 8));
    }

    public String convert(long a1) {
        StringBuilder sb = new StringBuilder();
        int v9 = 1;
        do {
            sb.append(chars[(int) (a1 % chars.length)]);
            a1 /= chars.length;
        } while (v9-- + 0xB > 0 && a1 != 0);
        sb.append(startChar);
        sb.reverse();
        return sb.toString();
    }

    public long toId(String tag) {
        if (!tag.startsWith("#"))
            throw new IllegalArgumentException("invalid tag!");

        long id = 0;

        tag = tag.substring(1);
        for (int i = 0; i < tag.length(); i++) {
            char c = tag.charAt(i);

            int charIdx;
            for (charIdx = 0; charIdx < chars.length; charIdx++)
                if (chars[charIdx] == c)
                    break;

            id *= chars.length;
            id += charIdx;
        }

        return id;
    }

}