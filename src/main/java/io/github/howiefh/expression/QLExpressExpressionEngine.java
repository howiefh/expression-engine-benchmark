/*
 * @(#)QLExpressExpressionEngine 1.0 2023/9/7
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

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

import java.util.Map;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class QLExpressExpressionEngine implements ExpressionEngine {
    static final String QL_EXPRESS_ENGINE = "QLExpress";

    private final ExpressRunner expressRunner;
    public QLExpressExpressionEngine() {
        expressRunner = new ExpressRunner();
    }

    /**
     * 类型
     *
     * @return {@link String}
     */
    @Override
    public String getType() {
        return QL_EXPRESS_ENGINE;
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
            DefaultContext<String, Object> context = new DefaultContext<>();
            context.putAll(param);
            return expressRunner.execute(expressionString, context, null, true, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
