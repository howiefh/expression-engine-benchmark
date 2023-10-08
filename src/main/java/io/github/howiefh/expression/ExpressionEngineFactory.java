/*
 * @(#)ExpressionEngineFactory 1.0 2023/9/8
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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class ExpressionEngineFactory {
    public static final String MVEL = MVELExpressionEngine.MVEL_ENGINE;
    public static final String AVIATOR = AviatorExpressionEngine.AVIATOR_ENGINE;
    public static final String QL_EXPRESS = QLExpressExpressionEngine.QL_EXPRESS_ENGINE;
    public static final String SP_EL = SpElExpressionEngine.SP_EL_ENGINE;
    public static final String OGNL = OGNLExpressionEngine.OGNL_ENGINE;
    public static final String JEXL = JEXLExpressionEngine.JEXL_ENGINE;
    public static final String JUEL = JUELExpressionEngine.JUEL_ENGINE;
    public static final String JANINO = JaninoExpressionEngine.JANINO_ENGINE;

    public static final Set<String> ALL = new LinkedHashSet<>();
    static {
        ALL.add(MVEL);
        ALL.add(AVIATOR);
        ALL.add(QL_EXPRESS);
        ALL.add(SP_EL);
        ALL.add(OGNL);
        ALL.add(JEXL);
        ALL.add(JUEL);
        ALL.add(JANINO);
    }

    public static ExpressionEngine create(String type) {
        switch (type) {
            case MVEL:
                return new MVELExpressionEngine();
            case AVIATOR:
                return new AviatorExpressionEngine();
            case QL_EXPRESS:
                return new QLExpressExpressionEngine();
            case SP_EL:
                return new SpElExpressionEngine();
            case OGNL:
                return new OGNLExpressionEngine();
            case JEXL:
                return new JEXLExpressionEngine();
            case JUEL:
                return new JUELExpressionEngine();
            case JANINO:
                return new JaninoExpressionEngine();
            default:
                throw new IllegalArgumentException("不存在 " + type);
        }
    }
}
