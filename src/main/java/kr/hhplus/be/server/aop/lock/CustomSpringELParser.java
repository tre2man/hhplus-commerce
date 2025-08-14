package kr.hhplus.be.server.aop.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.stream.Collectors;

public class CustomSpringELParser {
    private CustomSpringELParser() {
    }

    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
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
                .collect(Collectors.toList());
        } else if (result instanceof String) {
            return List.of(result.toString());
        } else {
            return List.of();
        }
    }
}
