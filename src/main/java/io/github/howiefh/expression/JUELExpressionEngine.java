/*
 * @(#)JUELExpressionEngine 1.0 2023/9/8
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

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class JUELExpressionEngine implements ExpressionEngine {
    static final String JUEL_ENGINE = "JUEL";

    private final ExpressionFactory factory;

    public JUELExpressionEngine() {
        Properties properties = new Properties();
        properties.put("javax.el.cacheSize", "5000");
        factory = new ExpressionFactoryImpl(properties);
    }

    /**
     * 类型
     *
     * @return {@link String}
     */
    @Override
    public String getType() {
        return JUEL_ENGINE;
    }

    /**
     * 直接执行表达式
     *
     * @param expressionString 表达式
     * @param param            数据
     * @return 表达式的值
     */
    @Override
    public Object execute(String expressionString, Map<String, Object> param) {
        SimpleContext context = new SimpleContext();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (entry.getValue() != null) {
                context.setVariable(entry.getKey(), factory.createValueExpression(entry.getValue(), entry.getValue().getClass()));
            }
        }
        try {
            context.setFunction("fn", "date", JUELExpressionEngine.class.getMethod("now"));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        expressionString = "${" + expressionString + "}";
        ValueExpression expression = factory.createValueExpression(context, expressionString, Object.class);
        return expression.getValue(context);
    }

    public static Date now() {
        return new Date();
    }


}
