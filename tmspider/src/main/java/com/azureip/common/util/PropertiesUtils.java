package com.azureip.common.util;

import java.util.Properties;

public class PropertiesUtils {

    private static Properties customProps;

    public static Integer getIntValue(String key, Integer def) {
        String value;
        if (def != null) {
            value = customProps.getProperty(key, String.valueOf(def));
        } else {
            value = customProps.getProperty(key);
        }
        Integer integer;
        try {
            integer = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            integer = 0;
        }
        return integer;
    }
}
