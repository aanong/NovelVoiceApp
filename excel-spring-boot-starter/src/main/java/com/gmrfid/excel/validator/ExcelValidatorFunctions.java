package com.gmrfid.excel.validator;

import org.springframework.util.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Excel数据验证预定义函数集
 * 供SpEL表达式中调用，例如: #notBlank(#val)
 */
public class ExcelValidatorFunctions {

    public static boolean notBlank(Object val) {
        if (val == null)
            return false;
        return StringUtils.hasText(String.valueOf(val));
    }

    public static boolean empty(Object val) {
        if (val == null)
            return true;
        return !StringUtils.hasText(String.valueOf(val));
    }

    public static boolean lengthEquals(Object val, int len) {
        if (val == null)
            return false;
        return String.valueOf(val).length() == len;
    }

    public static boolean lengthLessThan(Object val, int max) {
        if (val == null)
            return true;
        return String.valueOf(val).length() < max;
    }

    public static boolean lengthBetween(Object val, int min, int max) {
        if (val == null)
            return false;
        int len = String.valueOf(val).length();
        return len >= min && len <= max;
    }

    public static boolean options(Object val, String... options) {
        if (val == null)
            return false;
        String strVal = String.valueOf(val);
        return Arrays.asList(options).contains(strVal);
    }

    public static boolean dateFormat(Object val, String format) {
        if (val == null)
            return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(String.valueOf(val));
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean regex(Object val, String regex) {
        if (val == null)
            return false;
        return Pattern.matches(regex, String.valueOf(val));
    }

    /**
     * 常用正则表达式快捷验证
     */
    public static boolean email(Object val) {
        return regex(val, "^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean phone(Object val) {
        return regex(val, "^1[3-9]\\d{9}$");
    }

    public static boolean idCard(Object val) {
        return regex(val, "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)");
    }

    /**
     * 数值相关验证
     */
    public static boolean doubleWithScale(Object val, int scale) {
        if (val == null)
            return false;
        try {
            String str = String.valueOf(val);
            if (!str.contains("."))
                return true;
            return str.split("\\.")[1].length() <= scale;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean doubleGreaterThan(Object val, double limit) {
        try {
            return Double.parseDouble(String.valueOf(val)) > limit;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean doubleLessThan(Object val, double limit) {
        try {
            return Double.parseDouble(String.valueOf(val)) < limit;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean doubleBetween(Object val, double min, double max) {
        try {
            double d = Double.parseDouble(String.valueOf(val));
            return d >= min && d <= max;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean longGreaterThan(Object val, long limit) {
        try {
            return Long.parseLong(String.valueOf(val)) > limit;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean longBetween(Object val, long min, long max) {
        try {
            long l = Long.parseLong(String.valueOf(val));
            return l >= min && l <= max;
        } catch (Exception e) {
            return false;
        }
    }
}
