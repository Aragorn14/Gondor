package com.scube.Gondor.Util;

/**
 * Created by vashoka on 7/25/15.
 */
public class StringUtil {

    public static String PascalCase(String input) {

        String[] words = input.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(words[0].substring(0, 1).toUpperCase() + words[0].substring(1));
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(words[i].substring(0, 1).toUpperCase() + words[i].substring(1));
            }
        }

        return sb.toString();
    }
}

