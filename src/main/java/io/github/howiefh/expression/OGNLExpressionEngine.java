/*
 * @(#)OGNLExpressionEngine 1.0 2023/9/8
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

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class OGNLExpressionEngine implements ExpressionEngine {
    static final String OGNL_ENGINE = "OGNL";
    private final Map<String, Object> expressionCache = new ConcurrentHashMap<>();

    /**
     * 类型
     *
     * @return {@link String}
     */
    @Override
    public String getType() {
        return OGNL_ENGINE;
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
        try {
            OgnlContext ognlContext = Ognl.createDefaultContext(param);
            Object expression = getCompiledExpression(expressionString, expressionString);
            return Ognl.getValue(expression, ognlContext, param);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取缓存的编译后的MVEL脚本/表达式
     */
    private Object getCompiledExpression(final String cacheKey, final String expression) throws OgnlException {
        Object compiledExpression = expressionCache.get(cacheKey);
        if (compiledExpression != null) {
            return compiledExpression;
        }
        compiledExpression = Ognl.parseExpression(expression);
        expressionCache.put(cacheKey, compiledExpression);
        return compiledExpression;
    }
}
