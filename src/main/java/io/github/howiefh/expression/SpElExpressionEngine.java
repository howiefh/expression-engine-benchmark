/*
 * @(#)SpElExpressionEngine 1.0 2023/9/8
 *
 * Copyright 2023 Feng Hao.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.howiefh.expression;

import io.github.howiefh.support.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class SpElExpressionEngine implements ExpressionEngine{

    static final String SP_EL_ENGINE = "SpEl";
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>(256);

    private final StandardEvaluationContext context;

    private final ExpressionParser expressionParser;

    public SpElExpressionEngine() {
        expressionParser = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, null));
        context = new StandardEvaluationContext();
        context.addPropertyAccessor(new MapAccessor());
    }

    /**
     * 类型
     *
     * @return {@link String}
     */
    @Override
    public String getType() {
        return SP_EL_ENGINE;
    }

    /**
     * 直接执行表达式
     *
     * @param expressionString 表达式
     * @param param      数据
     * @return 表达式的值
     */
    @Override
    public Object execute(String expressionString, Map<String, Object> param) {
        context.setRootObject(param);
        Expression expression = getCompiledExpression(expressionString, expressionString);
        return expression.getValue(context);
    }

    private Expression getCompiledExpression(final String cacheKey, final String expression) {
        Expression compiledExpression = expressionCache.get(cacheKey);
        if (compiledExpression != null) {
            return compiledExpression;
        }
        compiledExpression = expressionParser.parseExpression(expression);
        expressionCache.put(cacheKey, compiledExpression);
        return compiledExpression;
    }
}
