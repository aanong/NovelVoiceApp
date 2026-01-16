package com.gmrfid.excel.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Excel表达式验证器
 * 支持SpEL表达式验证单元格数据
 */
@Slf4j
@Component
public class ExcelExpressionValidator {

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 验证值是否满足表达式
     * 
     * @param value      待验证的�?
     * @param expression 验证表达�?
     * @return 是否通过验证
     */
    public boolean validate(String value, String expression) {
        if (!StringUtils.hasText(expression)) {
            return true;
        }

        try {
            EvaluationContext context = createContext(value);
            Expression exp = parser.parseExpression(expression);
            Boolean result = exp.getValue(context, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            log.warn("表达式验证失�? expression={}, value={}, error={}", expression, value, e.getMessage());
            return false;
        }
    }

    /**
     * 创建表达式上下文,注册验证函数
     */
    private EvaluationContext createContext(String value) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 设置值变�?
        context.setVariable("val", value);

        // 注册验证函数
        try {
            context.registerFunction("notBlank",
                    ExcelValidatorFunctions.class.getMethod("notBlank", String.class));
            context.registerFunction("empty",
                    ExcelValidatorFunctions.class.getMethod("empty", String.class));
            context.registerFunction("lengthLessThan",
                    ExcelValidatorFunctions.class.getMethod("lengthLessThan", String.class, int.class));
            context.registerFunction("lengthGreaterThan",
                    ExcelValidatorFunctions.class.getMethod("lengthGreaterThan", String.class, int.class));
            context.registerFunction("lengthEquals",
                    ExcelValidatorFunctions.class.getMethod("lengthEquals", String.class, int.class));
            context.registerFunction("lengthBetween",
                    ExcelValidatorFunctions.class.getMethod("lengthBetween", String.class, int.class, int.class));
            context.registerFunction("options",
                    ExcelValidatorFunctions.class.getMethod("options", String.class, String[].class));
            context.registerFunction("dateFormat",
                    ExcelValidatorFunctions.class.getMethod("dateFormat", String.class, String.class));
            context.registerFunction("regex",
                    ExcelValidatorFunctions.class.getMethod("regex", String.class, String.class));
            context.registerFunction("email",
                    ExcelValidatorFunctions.class.getMethod("email", String.class));
            context.registerFunction("phone",
                    ExcelValidatorFunctions.class.getMethod("phone", String.class));
            context.registerFunction("idCard",
                    ExcelValidatorFunctions.class.getMethod("idCard", String.class));
            context.registerFunction("doubleWithScale",
                    ExcelValidatorFunctions.class.getMethod("doubleWithScale", String.class, int.class));
            context.registerFunction("doubleGreaterThan",
                    ExcelValidatorFunctions.class.getMethod("doubleGreaterThan", String.class, double.class));
            context.registerFunction("doubleGreaterThanOrEquals",
                    ExcelValidatorFunctions.class.getMethod("doubleGreaterThanOrEquals", String.class, double.class));
            context.registerFunction("doubleLessThan",
                    ExcelValidatorFunctions.class.getMethod("doubleLessThan", String.class, double.class));
            context.registerFunction("doubleBetween",
                    ExcelValidatorFunctions.class.getMethod("doubleBetween", String.class, double.class, double.class));
            context.registerFunction("longGreaterThan",
                    ExcelValidatorFunctions.class.getMethod("longGreaterThan", String.class, long.class));
            context.registerFunction("longGreaterThanOrEquals",
                    ExcelValidatorFunctions.class.getMethod("longGreaterThanOrEquals", String.class, long.class));
            context.registerFunction("longLessThan",
                    ExcelValidatorFunctions.class.getMethod("longLessThan", String.class, long.class));
            context.registerFunction("longBetween",
                    ExcelValidatorFunctions.class.getMethod("longBetween", String.class, long.class, long.class));
            context.registerFunction("intGreaterThan",
                    ExcelValidatorFunctions.class.getMethod("intGreaterThan", String.class, int.class));
            context.registerFunction("intLessThan",
                    ExcelValidatorFunctions.class.getMethod("intLessThan", String.class, int.class));
            context.registerFunction("intBetween",
                    ExcelValidatorFunctions.class.getMethod("intBetween", String.class, int.class, int.class));
        } catch (NoSuchMethodException e) {
            log.error("注册验证函数失败", e);
        }

        return context;
    }
}
