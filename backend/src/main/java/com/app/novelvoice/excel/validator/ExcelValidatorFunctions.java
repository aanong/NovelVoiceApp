package com.app.novelvoice.excel.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Excel验证函数库
 * 提供SpEL表达式中使用的验证方法
 */
@Component("excelValidator")
public class ExcelValidatorFunctions {
    
    // 邮箱正则
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    // 手机号正则(中国大陆)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$");
    
    // 身份证正则(简单校验)
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    
    /**
     * 判断值是否非空
     * @param val 值
     * @return 是否非空
     */
    public boolean notBlank(Object val) {
        if (val == null) {
            return false;
        }
        return StringUtils.hasText(val.toString());
    }
    
    /**
     * 判断值是否为空
     * @param val 值
     * @return 是否为空
     */
    public boolean empty(Object val) {
        if (val == null) {
            return true;
        }
        return !StringUtils.hasText(val.toString());
    }
    
    /**
     * 判断字符串长度是否小于指定值
     * @param val 值
     * @param maxLength 最大长度
     * @return 是否满足条件
     */
    public boolean lengthLessThan(Object val, int maxLength) {
        if (val == null) {
            return true;
        }
        return val.toString().length() < maxLength;
    }
    
    /**
     * 判断字符串长度是否小于等于指定值
     * @param val 值
     * @param maxLength 最大长度
     * @return 是否满足条件
     */
    public boolean lengthLessOrEquals(Object val, int maxLength) {
        if (val == null) {
            return true;
        }
        return val.toString().length() <= maxLength;
    }
    
    /**
     * 判断字符串长度是否在指定范围内
     * @param val 值
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是否满足条件
     */
    public boolean lengthBetween(Object val, int minLength, int maxLength) {
        if (val == null) {
            return minLength == 0;
        }
        int len = val.toString().length();
        return len >= minLength && len <= maxLength;
    }
    
    /**
     * 判断值是否在指定选项中
     * @param val 值
     * @param options 选项列表
     * @return 是否满足条件
     */
    public boolean options(Object val, String... options) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        Set<String> optionSet = new HashSet<>(Arrays.asList(options));
        return optionSet.contains(val.toString().trim());
    }
    
    /**
     * 验证日期格式
     * @param val 值
     * @param pattern 日期格式
     * @return 是否满足条件
     */
    public boolean dateFormat(Object val, String pattern) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            sdf.parse(val.toString().trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * 验证小数精度
     * @param val 值
     * @param scale 小数位数
     * @return 是否满足条件
     */
    public boolean doubleWithScale(Object val, int scale) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            BigDecimal bd = new BigDecimal(val.toString().trim());
            return bd.scale() <= scale;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证double值大于指定值
     * @param val 值
     * @param min 最小值
     * @return 是否满足条件
     */
    public boolean doubleGreaterThan(Object val, double min) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            double d = Double.parseDouble(val.toString().trim());
            return d > min;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证double值大于等于指定值
     * @param val 值
     * @param min 最小值
     * @return 是否满足条件
     */
    public boolean doubleGreaterThanOrEquals(Object val, double min) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            double d = Double.parseDouble(val.toString().trim());
            return d >= min;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证double值在指定范围内
     * @param val 值
     * @param min 最小值
     * @param max 最大值
     * @return 是否满足条件
     */
    public boolean doubleBetween(Object val, double min, double max) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            double d = Double.parseDouble(val.toString().trim());
            return d >= min && d <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证long值大于指定值
     * @param val 值
     * @param min 最小值
     * @return 是否满足条件
     */
    public boolean longGreaterThan(Object val, long min) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            long l = Long.parseLong(val.toString().trim());
            return l > min;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证long值大于等于指定值
     * @param val 值
     * @param min 最小值
     * @return 是否满足条件
     */
    public boolean longGreaterThanOrEquals(Object val, long min) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            long l = Long.parseLong(val.toString().trim());
            return l >= min;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证long值在指定范围内
     * @param val 值
     * @param min 最小值
     * @param max 最大值
     * @return 是否满足条件
     */
    public boolean longBetween(Object val, long min, long max) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            long l = Long.parseLong(val.toString().trim());
            return l >= min && l <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证整数值
     * @param val 值
     * @return 是否为有效整数
     */
    public boolean isInteger(Object val) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            Integer.parseInt(val.toString().trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证数值
     * @param val 值
     * @return 是否为有效数值
     */
    public boolean isNumber(Object val) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        try {
            new BigDecimal(val.toString().trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证邮箱格式
     * @param val 值
     * @return 是否为有效邮箱
     */
    public boolean isEmail(Object val) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        return EMAIL_PATTERN.matcher(val.toString().trim()).matches();
    }
    
    /**
     * 验证手机号格式(中国大陆)
     * @param val 值
     * @return 是否为有效手机号
     */
    public boolean isPhone(Object val) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        return PHONE_PATTERN.matcher(val.toString().trim()).matches();
    }
    
    /**
     * 验证身份证格式
     * @param val 值
     * @return 是否为有效身份证号
     */
    public boolean isIdCard(Object val) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(val.toString().trim()).matches();
    }
    
    /**
     * 正则匹配验证
     * @param val 值
     * @param regex 正则表达式
     * @return 是否匹配
     */
    public boolean matches(Object val, String regex) {
        if (val == null || !StringUtils.hasText(val.toString())) {
            return false;
        }
        return Pattern.matches(regex, val.toString());
    }
}
