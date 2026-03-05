package com.Hoseo.CapstoneDesign.global.util;

public class EncodeUtil {

    public static String encodeSpacesAsPercent20(String s) {
        return s == null ? "" : s.replace(" ", "%20");
    }

}
