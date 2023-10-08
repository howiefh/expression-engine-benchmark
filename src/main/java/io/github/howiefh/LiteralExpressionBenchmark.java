/*
 * @(#)LiteralExpressionBenchmark 1.0 2023/9/12
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
package io.github.howiefh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;


import java.util.Collections;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class LiteralExpressionBenchmark extends BaseBenchmark {
    static final String LITERAL_ARITHMETIC_EXPRESSION = "1000 + 100.0 * 99 - (600 - 3 * 15) / (((68 - 9) - 3) * 2 - 100) + 10000 % 7 * 71";
    static final String LITERAL_NESTED_TERNARY_ARITHMETIC_EXPRESSION = "6.7 - 100 > 39.6 ? 5 == 5 ? 4 + 5 : 6 - 1 : !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3";
    static final String LITERAL_NESTED_TERNARY_ARITHMETIC_EXPRESSION_QL_EXPRESS = "6.7 - 100 > 39.6 ? (5 == 5 ? 4 + 5 : 6 - 1) : (!(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3)";

    @State(Scope.Benchmark)
    public static class ExpressionBenchmarkData {

        @Param({LITERAL_ARITHMETIC_EXPRESSION, LITERAL_NESTED_TERNARY_ARITHMETIC_EXPRESSION})
        public String expression;
    }

    @State(Scope.Benchmark)
    public static class QlExpressExpressionBenchmarkData {

        @Param({LITERAL_ARITHMETIC_EXPRESSION, LITERAL_NESTED_TERNARY_ARITHMETIC_EXPRESSION_QL_EXPRESS})
        public String expression;
    }

    @Benchmark
    public Object testMVEL(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.mvel.execute(expressionData.expression, Collections.emptyMap());
    }

    @Benchmark
    public Object testAviator(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.aviator.execute(expressionData.expression, Collections.emptyMap());
    }

    @Benchmark
    public Object testQlExpress(BenchmarkData data, QlExpressExpressionBenchmarkData expressionData) {
        return data.qlExpress.execute(expressionData.expression, Collections.emptyMap());
    }

    @Benchmark
    public Object testSpEl(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.spEl.execute(expressionData.expression, Collections.emptyMap());
    }

    @Benchmark
    public Object testOGNL(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.ognl.execute(expressionData.expression, Collections.emptyMap());
    }

    @Benchmark
    public Object testJEXL(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.jexl.execute(expressionData.expression, Collections.emptyMap());
    }

    @Benchmark
    public Object testJUEL(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.juel.execute(expressionData.expression, Collections.emptyMap());
    }

    @Benchmark
    public Object testJanino(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.janino.execute(expressionData.expression, Collections.emptyMap());
    }

    public static void main(String[] args) throws RunnerException {
        run("LiteralExpressionBenchmark.json", LiteralExpressionBenchmark.class.getSimpleName());
    }
}
