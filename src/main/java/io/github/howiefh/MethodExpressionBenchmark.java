/*
 * @(#)VariableExpressionBenchmark 1.0 2023/9/12
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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class MethodExpressionBenchmark extends BaseBenchmark {
    static final String CONSTRUCTOR_EXPRESSION = "new java.util.Date()";
    static final String CONSTRUCTOR_EXPRESSION_JUEL = "fn:date()";
    static final String SUBSTRING_EXPRESSION = "s.substring(b.d)";
    static final String SUBSTRING_EXPRESSION_AVIATOR = "string.substring(s, b.d)";
    static final String SUBSTRING_EXPRESSION_JANINO = "s.substring((Integer)b.get(\"d\"))";
    static final String TWO_SUBSTRINGS_EXPRESSION = "s.substring(b.d).substring(a, b.c.e)";
    static final String TWO_SUBSTRINGS_EXPRESSION_AVIATOR = "string.substring(string.substring(s, b.d), a, b.c.e)";
    static final String TWO_SUBSTRINGS_EXPRESSION_JANINO = "s.substring((Integer)b.get(\"d\")).substring(a, (Integer)((java.util.Map)b.get(\"c\")).get(\"e\"))";

    public static Map<String, Object> getContextMap() {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> env1 = new HashMap<>();
        Map<String, Object> env2 = new HashMap<>();
        context.put("s", "hello world");
        context.put("a", 1);
        context.put("b", env1);
        env1.put("c", env2);
        env1.put("d", 5);
        env2.put("e", 4);
        return context;
    }

    @State(Scope.Benchmark)
    public static class BaseExpressionBenchmarkData {
        public Map<String, Object> context = getContextMap();
    }

    @State(Scope.Benchmark)
    public static class ExpressionBenchmarkData extends BaseExpressionBenchmarkData {

        @Param({CONSTRUCTOR_EXPRESSION, SUBSTRING_EXPRESSION, TWO_SUBSTRINGS_EXPRESSION})
        public String expression;

    }

    @State(Scope.Benchmark)
    public static class JUELExpressionBenchmarkData extends BaseExpressionBenchmarkData {

        @Param({CONSTRUCTOR_EXPRESSION_JUEL, SUBSTRING_EXPRESSION, TWO_SUBSTRINGS_EXPRESSION})
        public String expression;
    }

    @State(Scope.Benchmark)
    public static class JaninoExpressionBenchmarkData extends BaseExpressionBenchmarkData {

        @Param({CONSTRUCTOR_EXPRESSION, SUBSTRING_EXPRESSION_JANINO, TWO_SUBSTRINGS_EXPRESSION_JANINO})
        public String expression;
    }

    @State(Scope.Benchmark)
    public static class AviatorExpressionBenchmarkData extends BaseExpressionBenchmarkData {

        @Param({CONSTRUCTOR_EXPRESSION, SUBSTRING_EXPRESSION_AVIATOR, TWO_SUBSTRINGS_EXPRESSION_AVIATOR})
        public String expression;
    }

    @Benchmark
    public Object testMVEL(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.mvel.execute(expressionData.expression, expressionData.context);
    }

    @Benchmark
    public Object testAviator(BenchmarkData data, AviatorExpressionBenchmarkData expressionData) {
        return data.aviator.execute(expressionData.expression, expressionData.context);
    }

    @Benchmark
    public Object testQlExpress(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.qlExpress.execute(expressionData.expression, expressionData.context);
    }

    @Benchmark
    public Object testSpEl(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.spEl.execute(expressionData.expression, expressionData.context);
    }

    @Benchmark
    public Object testOGNL(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.ognl.execute(expressionData.expression, expressionData.context);
    }

    @Benchmark
    public Object testJEXL(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.jexl.execute(expressionData.expression, expressionData.context);
    }

    @Benchmark
    public Object testJUEL(BenchmarkData data, JUELExpressionBenchmarkData expressionData) {
        return data.juel.execute(expressionData.expression, expressionData.context);
    }

    @Benchmark
    public Object testJanino(BenchmarkData data, JaninoExpressionBenchmarkData expressionData) {
        return data.janino.execute(expressionData.expression, expressionData.context);
    }

    public static void main(String[] args) throws RunnerException {
        run("MethodExpressionBenchmark.json", MethodExpressionBenchmark.class.getSimpleName());
    }
}
