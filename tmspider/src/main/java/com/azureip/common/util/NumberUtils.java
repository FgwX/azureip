package com.azureip.common.util;

import java.math.BigDecimal;

public class NumberUtils {
    private final static double MIN_POSITIVE_VALUE = 0.0000000000001;

    public static double convertToDouble(final String number) {
        if (isEmpty(number)) {
            return 0.0;
        }
        try {
            final BigDecimal bigDecimal = new BigDecimal(number);
            return bigDecimal.doubleValue();
        } catch (final Exception e) {
            return 0.0;
        }
    }

    public static long convertToLong(final String number) {
        if (isEmpty(number)) {
            return 0;
        }
        try {
            // 针对"1.00"这种不规则字符串,用 Long.parseLong 转化异常,故用 BigDecimal 转换!
            final BigDecimal bigDecimal = new BigDecimal(number);
            return bigDecimal.longValue();
        } catch (final Exception e) {
            return 0;
        }
    }

    public static int convertToInteger(final String number) {
        if (isEmpty(number)) {
            return 0;
        }
        try {
            // 针对"1.00"这种不规则字符串,用 Integer.parseInt 转化异常,故用 BigDecimal 转换!
            final BigDecimal bigDecimal = new BigDecimal(number);
            return bigDecimal.intValue();
        } catch (final Exception e) {
            return 0;
        }
    }

    public static String longToString(final Long number) {
        try {
            return Long.toString(number);
        } catch (final Exception e) {
            return "";
        }
    }

    public static String doubleToString(final Double number) {
        try {
            return Double.toString(number);
        } catch (final Exception e) {
            return "";
        }
    }

    public static double doubleSubtract(final String number1, final String number2) {
        if (number1 == null || number1.isEmpty() || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.subtract(bigDecimal2).doubleValue();
    }

    public static double doubleSubtract(final Double number1, final Double number2) {
        if (number1 == null || number2 == null) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.subtract(bigDecimal2).doubleValue();
    }

    public static double doubleSubtract(final Double number1, final String number2) {
        if (number1 == null || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.subtract(bigDecimal2).doubleValue();
    }

    public static double doubleSubtract(final String number1, final Double number2) {
        if (number1 == null || number1.isEmpty() || number2 == null) {
            return 0.0;
        }
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.subtract(bigDecimal2).doubleValue();
    }

    public static double doubleAdd(final String number1, final String number2) {
        if (number1 == null || number1.isEmpty() || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.add(bigDecimal2).doubleValue();
    }

    public static double doubleAdd(final Double number1, final Double number2) {
        if (number1 == null || number2 == null) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.add(bigDecimal2).doubleValue();
    }

    public static double doubleAdd(final Double number1, final String number2) {
        if (number1 == null || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.add(bigDecimal2).doubleValue();
    }

    public static double doubleAdd(final String number1, final Double number2) {
        if (number1 == null || number1.isEmpty() || number2 == null) {
            return 0.0;
        }
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.add(bigDecimal2).doubleValue();
    }

    public static double doubleMultiply(final String number1, final String number2, final int scale, final int mode) {
        if (number1 == null || number1.isEmpty() || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.multiply(bigDecimal2).setScale(scale, mode).doubleValue();
    }

    public static double doubleMultiply(final Double number1, final Double number2, final int scale, final int mode) {
        if (number1 == null || number2 == null) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.multiply(bigDecimal2).setScale(scale, mode).doubleValue();
    }

    public static double doubleMultiply(final Double number1, final String number2, final int scale, final int mode) {
        if (number1 == null || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.multiply(bigDecimal2).setScale(scale, mode).doubleValue();
    }

    public static double doubleMultiply(final String number1, final Double number2, final int scale, final int mode) {
        if (number1 == null || number1.isEmpty() || number2 == null) {
            return 0.0;
        }
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.multiply(bigDecimal2).setScale(scale, mode).doubleValue();
    }

    public static double doubleMultiply(final String number1, final String number2) {
        if (number1 == null || number1.isEmpty() || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.multiply(bigDecimal2).doubleValue();
    }

    public static double doubleMultiply(final Double number1, final Double number2) {
        if (number1 == null || number2 == null) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.multiply(bigDecimal2).doubleValue();
    }

    public static double doubleMultiply(final Double number1, final String number2) {
        if (number1 == null || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.multiply(bigDecimal2).doubleValue();
    }

    public static double doubleMultiply(final String number1, final Double number2) {
        if (number1 == null || number1.isEmpty() || number2 == null) {
            return 0.0;
        }
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.multiply(bigDecimal2).doubleValue();
    }

    public static double doubleDiv(final String number1, final String number2, final int scale) {
        if (number1 == null || number1.isEmpty() || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final Double divNumber = convertToDouble(number2);
        /* 判断除数是否为0 */
        if (Math.abs(divNumber) < MIN_POSITIVE_VALUE) {
            return 0.0;
        }
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double doubleDiv(final Double number1, final Double number2, final int scale) {
        if (number1 == null || number2 == null) {
            return 0.0;
        }
        /* 判断除数是否为0 */
        if (Math.abs(number2) < MIN_POSITIVE_VALUE) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double doubleDiv(final Double number1, final String number2, final int scale) {
        if (number1 == null || number2 == null || number2.isEmpty()) {
            return 0.0;
        }
        final Double divNumber = convertToDouble(number2);
        /* 判断除数是否为0 */
        if (Math.abs(divNumber) < MIN_POSITIVE_VALUE) {
            return 0.0;
        }
        final String convertNum1 = doubleToString(number1);
        final BigDecimal bigDecimal1 = new BigDecimal(convertNum1);
        final BigDecimal bigDecimal2 = new BigDecimal(number2);
        return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double doubleDiv(final String number1, final Double number2, final int scale) {
        if (number1 == null || number1.isEmpty() || number2 == null) {
            return 0.0;
        }
        /* 判断除数是否为0 */
        if (Math.abs(number2) < MIN_POSITIVE_VALUE) {
            return 0.0;
        }
        final String convertNum2 = doubleToString(number2);
        final BigDecimal bigDecimal1 = new BigDecimal(number1);
        final BigDecimal bigDecimal2 = new BigDecimal(convertNum2);
        return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static boolean doubleEqualZero(final Double data) {
        return data == null || Math.abs(data) < MIN_POSITIVE_VALUE;
    }

    public static Double roundDouble(final Double number, final int scale) {
        if (number == null) {
            return null;
        }
        final String format = "%." + scale + "f";
        return Double.parseDouble(String.format(format, number));
    }

    public static void main(final String... arg) {
        System.out.println(doubleMultiply(889D, 0.3D, 0, BigDecimal.ROUND_HALF_DOWN));
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
