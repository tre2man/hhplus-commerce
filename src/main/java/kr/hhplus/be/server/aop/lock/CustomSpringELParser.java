package kr.hhplus.be.server.aop.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

public class CustomSpringELParser {
    private CustomSpringELParser() {
    }
    
    public static List<String> getMultipleKeys(String[] parameterNames, Object[] args, String keyExpression) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        Object result = parser.parseExpression(keyExpression).getValue(context, Object.class);
        
        if (result instanceof List) {
            return ((List<?>) result).stream()
                .map(Object::toString)
                .toList();
        } else if (result instanceof String) {
            return List.of(result.toString());
        } else {
            return List.of();
        }
    }
}
