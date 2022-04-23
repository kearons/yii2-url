package pers.yiiurl.common;

public class StringUtils {
    public static String CamelToId(String name) {
        return CamelToId(name, "_");
    }

    public static String CamelToId(String name, String separator) {
        String regex =  "(?<=[a-z0-9])[A-Z]";
        return name.trim().replaceAll(regex, separator + "$0").toLowerCase();
    }
}
