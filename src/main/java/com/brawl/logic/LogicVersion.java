package com.brawl.logic;

import lombok.Getter;
import lombok.Setter;

public class LogicVersion {

    @Getter
    @Setter
    private static int majorVersion, minorVersion, buildVersion;

    public static Boolean isDev() {
        return true;
    }

    public static Boolean isProd() {
        return !isDev();
    }

    public static String getEnvironment() {
        return isProd() ? "prod" : "dev";
    }

}