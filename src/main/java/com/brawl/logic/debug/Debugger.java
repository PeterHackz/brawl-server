package com.brawl.logic.debug;

public class Debugger {

    // 2 - verbose
    // 1 - show warns + errors
    // 0 - info
    // -1 - nothing
    // set it to 0 in production
    private static final int DEBUG_LEVEL = 2;

    private static final String INFO = "\033[96;1m[INFO] %s\033[0m\n";
    private static final String WARNING = "\033[93;1m[WARNING] %s\033[0m\n";
    private static final String ERROR = "\033[91;1m[ERROR] %s\033[0m\n";
    private static final String VERBOSE = "\033[95;1m[VERBOSE] %s\033[0m\n";

    public static void info(String string, Object... objects) {
        if (DEBUG_LEVEL >= 0)
            info(String.format(string, objects));
    }

    public static void warn(String string, Object... objects) {
        if (DEBUG_LEVEL >= 1)
            warn(String.format(string, objects));
    }

    public static void error(String string, Object... objects) {
        if (DEBUG_LEVEL >= 1)
            error(String.format(string, objects));
    }

    public static void verbose(String string, Object... objects) {
        if (DEBUG_LEVEL >= 2)
            verbose(String.format(string, objects));
    }

    public static void fatal(String string, Object... objects) {
        fatal(String.format(string, objects));
    }

    public static void fatal(String string) {
        error(string);
        System.exit(0);
    }

    public static int getDebugLevel() {
        return DEBUG_LEVEL;
    }

    public static boolean isVerbose() {
        return DEBUG_LEVEL >= 2;
    }

    private static void info(String string) {
        System.out.printf(INFO, string);
    }

    private static void warn(String string) {
        System.out.printf(WARNING, string);
    }

    private static void error(String string) {
        System.out.printf(ERROR, string);
    }

    private static void verbose(String string) {
        System.out.printf(VERBOSE, string);
    }
}
