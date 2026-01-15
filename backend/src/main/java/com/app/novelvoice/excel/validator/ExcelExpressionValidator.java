package com.app.novelvoice.excel.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.BeanFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Excel表达式验证器
 * 使用SpEL解析和执行验证表达式
 */
@Component
public class ExcelExpressionValidator {
    
    @Autowired
    private BeanFactory beanFactory;
    
    // SpEL表达式解析器
    private final ExpressionParser parser = new SpelExpressionParser();
    
    // 表达式缓存
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
    
    // 基础评估上下文
    private StandardEvaluationContext baseContext;
    
    @PostConstruct
    public void init() {
        baseContext = new StandardEvaluationContext();
        baseContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        
        // 注册验证函数到上下文中
        // 使用 #functionName 方式调用
        try {
            baseContext.registerFunction("notBlank", 
                    ExcelValidatorFunctions.class.getMethod("notBlank", Object.class));
            baseContext.registerFunction("empty", 
                    ExcelValidatorFunctions.class.getMethod("empty", Object.class));
            baseContext.registerFunction("lengthLessThan", 
                    ExcelValidatorFunctions.class.getMethod("lengthLessThan", Object.class, int.class));
            baseContext.registerFunction("lengthLessOrEquals", 
                    ExcelValidatorFunctions.class.getMethod("lengthLessOrEquals", Object.class, int.class));
            baseContext.registerFunction("lengthBetween", 
                    ExcelValidatorFunctions.class.getMethod("lengthBetween", Object.class, int.class, int.class));
            baseContext.registerFunction("options", 
                    ExcelValidatorFunctions.class.getMethod("options", Object.class, String[].class));
            baseContext.registerFunction("dateFormat", 
                    ExcelValidatorFunctions.class.getMethod("dateFormat", Object.class, String.class));
            baseContext.registerFunction("doubleWithScale", 
                    ExcelValidatorFunctions.class.getMethod("doubleWithScale", Object.class, int.class));
            baseContext.registerFunction("doubleGreaterThan", 
                    ExcelValidatorFunctions.class.getMethod("doubleGreaterThan", Object.class, double.class));
            baseContext.registerFunction("doubleGreaterThanOrEquals", 
                    ExcelValidatorFunctions.class.getMethod("doubleGreaterThanOrEquals", Object.class, double.class));
            baseContext.registerFunction("doubleBetween", 
                    ExcelValidatorFunctions.class.getMethod("doubleBetween", Object.class, double.class, double.class));
            baseContext.registerFunction("longGreaterThan", 
                    ExcelValidatorFunctions.class.getMethod("longGreaterThan", Object.class, long.class));
            baseContext.registerFunction("longGreaterThanOrEquals", 
                    ExcelValidatorFunctions.class.getMethod("longGreaterThanOrEquals", Object.class, long.class));
            baseContext.registerFunction("longBetween", 
                    ExcelValidatorFunctions.class.getMethod("longBetween", Object.class, long.class, long.class));
            baseContext.registerFunction("isInteger", 
                    ExcelValidatorFunctions.class.getMethod("isInteger", Object.class));
            baseContext.registerFunction("isNumber", 
                    ExcelValidatorFunctions.class.getMethod("isNumber", Object.class));
            baseContext.registerFunction("isEmail", 
                    ExcelValidatorFunctions.class.getMethod("isEmail", Object.class));
            baseContext.registerFunction("isPhone", 
                    ExcelValidatorFunctions.class.getMethod("isPhone", Object.class));
            baseContext.registerFunction("isIdCard", 
                    ExcelValidatorFunctions.class.getMethod("isIdCard", Object.class));
            baseContext.registerFunction("matches", 
                    ExcelValidatorFunctions.class.getMethod("matches", Object.class, String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("初始化验证函数失败", e);
        }
    }
    
    /**
     * 验证单个值
     * @param value 要验证的值
     * @param expression 验证表达式
     * @return 验证结果
     */
    public boolean validate(Object value, String expression) {
        if (!StringUtils.hasText(expression)) {
            return true;
        }
        
        try {
            // 从缓存获取或解析表达式
            Expression exp = expressionCache.computeIfAbsent(expression, parser::parseExpression);
            
            // 创建新的评估上下文,设置变量
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setBeanResolver(baseContext.getBeanResolver());
            
            // 复制注册的函数
            copyFunctions(context);
            
            // 设置 #val 变量
            context.setVariable("val", value);
            
            // 执行表达式
            Boolean result = exp.getValue(context, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            // 表达式执行异常,视为验证失败
            return false;
        }
    }
    
    /**
     * 验证并返回详细结果
     * @param value 要验证的值
     * @param expression 验证表达式
     * @param fieldName 字段名(用于错误信息)
     * @return 验证结果
     */
    public ValidationResult validateWithDetail(Object value, String expression, String fieldName) {
        ValidationResult result = new ValidationResult();
        result.setFieldName(fieldName);
        result.setValue(value);
        
        if (!StringUtils.hasText(expression)) {
            result.setValid(true);
            return result;
        }
        
        try {
            Expression exp = expressionCache.computeIfAbsent(expression, parser::parseExpression);
            
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setBeanResolver(baseContext.getBeanResolver());
            copyFunctions(context);
            context.setVariable("val", value);
            
            Boolean valid = exp.getValue(context, Boolean.class);
            result.setValid(valid != null && valid);
            
            if (!result.isValid()) {
                result.setErrorMessage(String.format("字段[%s]验证失败,值[%s]不满足规则[%s]", 
                        fieldName, value, expression));
            }
        } catch (Exception e) {
            result.setValid(false);
            result.setErrorMessage(String.format("字段[%s]验证表达式执行异常: %s", fieldName, e.getMessage()));
        }
        
        return result;
    }
    
    /**
     * 复制函数注册到新上下文
     */
    private void copyFunctions(StandardEvaluationContext targetContext) {
        try {
            targetContext.registerFunction("notBlank", 
                    ExcelValidatorFunctions.class.getMethod("notBlank", Object.class));
            targetContext.registerFunction("empty", 
                    ExcelValidatorFunctions.class.getMethod("empty", Object.class));
            targetContext.registerFunction("lengthLessThan", 
                    ExcelValidatorFunctions.class.getMethod("lengthLessThan", Object.class, int.class));
            targetContext.registerFunction("lengthLessOrEquals", 
                    ExcelValidatorFunctions.class.getMethod("lengthLessOrEquals", Object.class, int.class));
            targetContext.registerFunction("lengthBetween", 
                    ExcelValidatorFunctions.class.getMethod("lengthBetween", Object.class, int.class, int.class));
            targetContext.registerFunction("options", 
                    ExcelValidatorFunctions.class.getMethod("options", Object.class, String[].class));
            targetContext.registerFunction("dateFormat", 
                    ExcelValidatorFunctions.class.getMethod("dateFormat", Object.class, String.class));
            targetContext.registerFunction("doubleWithScale", 
                    ExcelValidatorFunctions.class.getMethod("doubleWithScale", Object.class, int.class));
            targetContext.registerFunction("doubleGreaterThan", 
                    ExcelValidatorFunctions.class.getMethod("doubleGreaterThan", Object.class, double.class));
            targetContext.registerFunction("doubleGreaterThanOrEquals", 
                    ExcelValidatorFunctions.class.getMethod("doubleGreaterThanOrEquals", Object.class, double.class));
            targetContext.registerFunction("doubleBetween", 
                    ExcelValidatorFunctions.class.getMethod("doubleBetween", Object.class, double.class, double.class));
            targetContext.registerFunction("longGreaterThan", 
                    ExcelValidatorFunctions.class.getMethod("longGreaterThan", Object.class, long.class));
            targetContext.registerFunction("longGreaterThanOrEquals", 
                    ExcelValidatorFunctions.class.getMethod("longGreaterThanOrEquals", Object.class, long.class));
            targetContext.registerFunction("longBetween", 
                    ExcelValidatorFunctions.class.getMethod("longBetween", Object.class, long.class, long.class));
            targetContext.registerFunction("isInteger", 
                    ExcelValidatorFunctions.class.getMethod("isInteger", Object.class));
            targetContext.registerFunction("isNumber", 
                    ExcelValidatorFunctions.class.getMethod("isNumber", Object.class));
            targetContext.registerFunction("isEmail", 
                    ExcelValidatorFunctions.class.getMethod("isEmail", Object.class));
            targetContext.registerFunction("isPhone", 
                    ExcelValidatorFunctions.class.getMethod("isPhone", Object.class));
            targetContext.registerFunction("isIdCard", 
                    ExcelValidatorFunctions.class.getMethod("isIdCard", Object.class));
            targetContext.registerFunction("matches", 
                    ExcelValidatorFunctions.class.getMethod("matches", Object.class, String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("复制验证函数失败", e);
        }
    }
    
    /**
     * 清除表达式缓存
     */
    public void clearCache() {
        expressionCache.clear();
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private String fieldName;
        private Object value;
        private boolean valid;
        private String errorMessage;
        
        public String getFieldName() {
            return fieldName;
        }
        
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
        
        public Object getValue() {
            return value;
        }
        
        public void setValue(Object value) {
            this.value = value;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
