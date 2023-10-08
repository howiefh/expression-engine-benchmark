/*
 * @(#)JEXLExpressionEngine 1.0 2023/9/10
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

import org.apache.commons.jexl3.*;
import org.apache.commons.jexl3.introspection.JexlPermissions;

import java.util.Map;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class JEXLExpressionEngine implements ExpressionEngine {
    static final String JEXL_ENGINE = "JEXL";
    private final JexlEngine jexlEngine;

    public JEXLExpressionEngine() {
        JexlFeatures features = new JexlFeatures();
        JexlPermissions permissions = JexlPermissions.RESTRICTED.compose("io.github.howiefh.*");
        jexlEngine = new JexlBuilder().permissions(permissions).features(features).cache(256).cacheThreshold(10000).create();
    }


    /**
     * 类型
     *
     * @return {@link String}
     */
    @Override
    public String getType() {
        return JEXL_ENGINE;
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
        JexlExpression expression = jexlEngine.createExpression(expressionString);
        JexlContext context = new MapContext(param);
        return expression.evaluate(context);
    }
}
