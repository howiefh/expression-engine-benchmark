/*
 * @(#)JaninoExpressionEngine 1.0 2023/9/11
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

import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class JaninoExpressionEngine implements ExpressionEngine {
    static final String JANINO_ENGINE = "Janino";

    // If you want to compile many expressions at the same time, you have the option to cook an array of expressions in one IExpressionEvaluator by using the following methods:
    // setMethodNames(String[])
    // setParameters(String[][], Class[][])
    // setExpressionTypes(Class[])
    // setStaticMethod(boolean[])
    // setThrownExceptions(Class[][])
    // cook(String[], Reader[])
    // evaluate(int, Object[])
    private final Map<String, IExpressionEvaluator> expressionCache = new ConcurrentHashMap<>(256);

    public JaninoExpressionEngine() {
    }

    /**
     * 类型
     *
     * @return {@link String}
     */
    @Override
    public String getType() {
        return JANINO_ENGINE;
    }

    /**
     * 执行表达式
     *
     * @param expressionString 表达式
     * @param param            数据
     * @return 表达式的值
     */
    @Override
    public Object execute(String expressionString, Map<String, Object> param) {
        try {
            List<String> parameterNames = new ArrayList<>(param.size());
            List<Class<?>> parameterTypes = new ArrayList<>(param.size());
            List<Object> parameterValues = new ArrayList<>(param.size());
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                if (entry.getValue() != null) {
                    parameterNames.add(entry.getKey());
                    parameterValues.add(entry.getValue());
                    parameterTypes.add(entry.getValue().getClass());
                }
            }

            IExpressionEvaluator expressionEvaluator = getCompiledExpression(expressionString, expressionString, parameterNames, parameterTypes);
            // Eventually we evaluate the expression - and that goes super-fast.
            return expressionEvaluator.evaluate(parameterValues.toArray(new Object[0]));
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private IExpressionEvaluator getCompiledExpression(final String cacheKey, final String expression, List<String> parameterNames, List<Class<?>> parameterTypes) {
        try {
            IExpressionEvaluator expressionEvaluator = expressionCache.get(cacheKey);
            if (expressionEvaluator != null) {
                return expressionEvaluator;
            }
            expressionEvaluator = CompilerFactoryFactory
                    .getDefaultCompilerFactory(JaninoExpressionEngine.class.getClassLoader())
                    .newExpressionEvaluator();
            expressionEvaluator.setParameters(parameterNames.toArray(new String[0]), parameterTypes.toArray(new Class[0]));
            expressionEvaluator.setExpressionType(Object.class);
            // And now we "cook" (scan, parse, compile and load) the fabulous expression.
            expressionEvaluator.cook(expression);
            expressionCache.put(cacheKey, expressionEvaluator);
            return expressionEvaluator;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
