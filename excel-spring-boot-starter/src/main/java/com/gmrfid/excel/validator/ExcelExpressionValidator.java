package com.gmrfid.excel.validator;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SpEL表达式验证器
 */
@Component
public class ExcelExpressionValidator {

        private final ExpressionParser parser = new SpelExpressionParser();
        private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
        private final Method[] validatorMethods;

        public ExcelExpressionValidator() {
                // 获取所有预定义的验证函数
                this.validatorMethods = ExcelValidatorFunctions.class.getDeclaredMethods();
        }

        /**
         * 执行验证表达式
         * 
         * @param expressionStr SpEL表达式
         * @param value         当前单元格的值 (#val)
         * @param row           当前整行数据 (#row)
         * @return 验证是否通过
         */
        public boolean validate(String expressionStr, Object value, Map<String, Object> row) {
                try {
                        Expression expression = expressionCache.computeIfAbsent(expressionStr, parser::parseExpression);

                        StandardEvaluationContext context = new StandardEvaluationContext();

                        // 1. 注册变量
                        context.setVariable("val", value);
                        context.setVariable("row", row);

                        // 2. 注册预定义函数
                        for (Method method : validatorMethods) {
                                context.registerFunction(method.getName(), method);
                        }

                        Boolean result = expression.getValue(context, Boolean.class);
                        return result != null && result;
                } catch (Exception e) {
                        return false;
                }
        }
}
