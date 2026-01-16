package com.gmrfid.excel.validator;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Excel验证函数集合
 * 用于SpEL表达式中调用
 */
public class ExcelValidatorFunctions {

    // ==================== 字符串验�?====================

    /**
     * 非空验证
     */
    public static boolean notBlank(String value) {
        return StringUtils.hasText(value);
    }

    /**
     * 空值验�?允许为空)
     */
    public static boolean empty(String value) {
        return !StringUtils.hasText(value);
    }

    /**
     * 长度小于
     */
    public static boolean lengthLessThan(String value, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        return value.length() < maxLength;
    }

    /**
     * 长度大于
     */
    public static boolean lengthGreaterThan(String value, int minLength) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        return value.length() > minLength;
    }

    /**
     * 长度等于
     */
    public static boolean lengthEquals(String value, int length) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        return value.length() == length;
    }

    /**
     * 长度范围
     */
    public static boolean lengthBetween(String value, int minLength, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        int len = value.length();
        return len >= minLength && len <= maxLength;
    }

    /**
     * 选项验证
     */
    public static boolean options(String value, String... options) {
        if (!StringUtils.hasText(value)) {
            return true; // 空值通过
        }
        for (String option : options) {
            if (value.equals(option)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 日期验证 ====================

    /**
     * 日期格式验证
     */
    public static boolean dateFormat(String value, String format) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(value.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // ==================== 正则验证 ====================

    /**
     * 正则表达式验�?
     */
    public static boolean regex(String value, String pattern) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        return Pattern.matches(pattern, value);
    }

    /**
     * 邮箱验证
     */
    public static boolean email(String value) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailPattern, value);
    }

    /**
     * 手机号验�?中国大陆)
     */
    public static boolean phone(String value) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        String phonePattern = "^1[3-9]\\d{9}$";
        return Pattern.matches(phonePattern, value);
    }

    /**
     * 身份证号验证(中国大陆)
     */
    public static boolean idCard(String value) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        // 18位身份证
        String idCardPattern = "^\\d{17}[\\dXx]$";
        return Pattern.matches(idCardPattern, value);
    }

    // ==================== 数字验证 ====================

    /**
     * 小数位数验证
     */
    public static boolean doubleWithScale(String value, int scale) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            String trimmed = value.trim();
            int dotIndex = trimmed.indexOf('.');
            if (dotIndex == -1) {
                return true; // 整数OK
            }
            int actualScale = trimmed.length() - dotIndex - 1;
            Double.parseDouble(trimmed); // 验证是否有效数字
            return actualScale <= scale;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 大于(double)
     */
    public static boolean doubleGreaterThan(String value, double threshold) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            return Double.parseDouble(value.trim()) > threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 大于等于(double)
     */
    public static boolean doubleGreaterThanOrEquals(String value, double threshold) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            return Double.parseDouble(value.trim()) >= threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 小于(double)
     */
    public static boolean doubleLessThan(String value, double threshold) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            return Double.parseDouble(value.trim()) < threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 范围(double)
     */
    public static boolean doubleBetween(String value, double min, double max) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            double d = Double.parseDouble(value.trim());
            return d >= min && d <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 大于(long)
     */
    public static boolean longGreaterThan(String value, long threshold) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            return Long.parseLong(value.trim()) > threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 大于等于(long)
     */
    public static boolean longGreaterThanOrEquals(String value, long threshold) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            return Long.parseLong(value.trim()) >= threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 小于(long)
     */
    public static boolean longLessThan(String value, long threshold) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            return Long.parseLong(value.trim()) < threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 范围(long)
     */
    public static boolean longBetween(String value, long min, long max) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            long l = Long.parseLong(value.trim());
            return l >= min && l <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 大于(int)
     */
    public static boolean intGreaterThan(String value, int threshold) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            return Integer.parseInt(value.trim()) > threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 小于(int)
     */
    public static boolean intLessThan(String value, int threshold) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            return Integer.parseInt(value.trim()) < threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 范围(int)
     */
    public static boolean intBetween(String value, int min, int max) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        try {
            int i = Integer.parseInt(value.trim());
            return i >= min && i <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
