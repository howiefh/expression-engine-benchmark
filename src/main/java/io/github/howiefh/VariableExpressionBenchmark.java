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

import io.github.howiefh.data.Customer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class VariableExpressionBenchmark extends BaseBenchmark {
    static final String BASIC_VARIABLE_EXPRESSION = "pi * d + b - (1000 - d * b / pi) / (pi + 99 - i * d) - i * pi * d / b";
    static final String BIG_DECIMAL_VARIABLE_EXPRESSION = "piDecimal * dDecimal + bDecimal - (1000 - dDecimal * bDecimal / piDecimal) / (piDecimal + 99 - iDecimal * dDecimal) - iDecimal * piDecimal * dDecimal / bDecimal";
    static final String BIG_DECIMAL_VARIABLE_EXPRESSION_JANINO = "piDecimal.multiply(dDecimal).add(bDecimal).subtract(new java.math.BigDecimal(\"1000\").subtract(dDecimal.multiply(bDecimal).divide(piDecimal, java.math.MathContext.DECIMAL128)).divide(piDecimal.add(new java.math.BigDecimal(\"99\")).subtract(iDecimal.multiply(dDecimal)), java.math.MathContext.DECIMAL128)).subtract(iDecimal.multiply(piDecimal).multiply(dDecimal).divide(bDecimal, java.math.MathContext.DECIMAL128))";
//    static final String BIG_DECIMAL_VARIABLE_EXPRESSION_JANINO = "piDecimal.multiply(dDecimal).add(bDecimal).subtract(new java.math.BigDecimal(\"1000\").subtract(dDecimal.multiply(bDecimal).divide(piDecimal, java.math.RoundingMode.HALF_EVEN)).divide(piDecimal.add(new java.math.BigDecimal(\"99\")).subtract(iDecimal.multiply(dDecimal)), java.math.RoundingMode.HALF_EVEN)).subtract(iDecimal.multiply(piDecimal).multiply(dDecimal).divide(bDecimal, java.math.RoundingMode.HALF_EVEN))";

    static final String BASIC_CONDITION_VARIABLE_EXPRESSION = "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 == i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
    static final String BASIC_CONDITION_VARIABLE_EXPRESSION_QL_EXPRESS = "i * pi + (d * b - 199) / (1 - d * pi) - (int)(2 + 100 - i / pi) % 99 == i * pi + (d * b - 199) / (1 - d * pi) - (int)(2 + 100 - i / pi) % 99";
    static final String CONDITION_VARIABLE_EXPRESSION = "(clientVersion == '1.9.0' || clientVersion == '1.9.1' || clientVersion == '1.9.2') && deviceType == 'Xiaomi' && weight >= 4 && osVersion == 'Android 9.0' && osType == 'Android' && clientIp != null && requestTime <= now&& customer.grade > 1 && customer.age > 18";

    static final String CONDITION_VARIABLE_EXPRESSION_AVIATOR = "(clientVersion == '1.9.0' || clientVersion == '1.9.1' || clientVersion == '1.9.2') && deviceType == 'Xiaomi' && weight >= 4 && osVersion == 'Android 9.0' && osType == 'Android' && clientIp != nil && requestTime <= now&& customer.grade > 1 && customer.age > 18";
    static final String CONDITION_VARIABLE_EXPRESSION_JANINO = "(clientVersion == \"1.9.0\" || clientVersion == \"1.9.1\" || clientVersion == \"1.9.2\") && deviceType == \"Xiaomi\" && weight >= 4 && osVersion == \"Android 9.0\" && osType == \"Android\" && clientIp != null && requestTime.compareTo(now) <= 0&& customer.getGrade() > 1 && customer.getAge() > 18";

    private static Map<String, Object> getContextMap() {
        Map<String, Object> context = new HashMap<>();
        context.put("i", 100);
        context.put("pi", 3.14F);
        context.put("d", -3.9D);
        // byte 时 mvel报错 internal error: 113
        // https://github.com/mvel/mvel/blob/85125e1af5f3ec32990b1768b8656bfcf450b489/src/main/java/org/mvel2/math/MathProcessor.java#L132
        // context.put("b", (byte) 4);
        context.put("b", 4);

        context.put("iDecimal", new BigDecimal("100"));
        context.put("piDecimal", new BigDecimal("3.14"));
        context.put("dDecimal", new BigDecimal("-3.9"));
        context.put("bDecimal", new BigDecimal("4"));

        context.put("clientVersion", "1.9.0");
        context.put("deviceType", "Xiaomi");
        context.put("weight", 5);
        context.put("osVersion", "Android 9.0");
        context.put("osType", "Android");
        context.put("clientIp", "127.0.0.1");
        context.put("requestTime", "2020-01-01T12:57:52");
        context.put("now", LocalDateTime.now().toString());
        context.put("customer", new Customer(2, 20));
        return context;
    }

    @State(Scope.Benchmark)
    public static class BaseExpressionBenchmarkData {
        public Map<String, Object> context = getContextMap();
    }


    @State(Scope.Benchmark)
    public static class ExpressionBenchmarkData extends BaseExpressionBenchmarkData {

        @Param({BASIC_VARIABLE_EXPRESSION, BIG_DECIMAL_VARIABLE_EXPRESSION, BASIC_CONDITION_VARIABLE_EXPRESSION, CONDITION_VARIABLE_EXPRESSION})
        public String expression;
    }

    @State(Scope.Benchmark)
    public static class QlExpressExpressionBenchmarkData extends BaseExpressionBenchmarkData {

        @Param({BASIC_VARIABLE_EXPRESSION, BIG_DECIMAL_VARIABLE_EXPRESSION, BASIC_CONDITION_VARIABLE_EXPRESSION_QL_EXPRESS, CONDITION_VARIABLE_EXPRESSION})
        public String expression;
    }

    @State(Scope.Benchmark)
    public static class JaninoExpressionBenchmarkData extends BaseExpressionBenchmarkData {

        @Param({BASIC_VARIABLE_EXPRESSION, BIG_DECIMAL_VARIABLE_EXPRESSION_JANINO, BASIC_CONDITION_VARIABLE_EXPRESSION, CONDITION_VARIABLE_EXPRESSION_JANINO})
        public String expression;
    }

    @State(Scope.Benchmark)
    public static class AviatorExpressionBenchmarkData extends BaseExpressionBenchmarkData {

        @Param({BASIC_VARIABLE_EXPRESSION, BIG_DECIMAL_VARIABLE_EXPRESSION, BASIC_CONDITION_VARIABLE_EXPRESSION, CONDITION_VARIABLE_EXPRESSION_AVIATOR})
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
    public Object testQlExpress(BenchmarkData data, QlExpressExpressionBenchmarkData expressionData) {
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
    public Object testJUEL(BenchmarkData data, ExpressionBenchmarkData expressionData) {
        return data.juel.execute(expressionData.expression, expressionData.context);
    }

    @Benchmark
    public Object testJanino(BenchmarkData data, JaninoExpressionBenchmarkData expressionData) {
        return data.janino.execute(expressionData.expression, expressionData.context);
    }

    public static void main(String[] args) throws RunnerException {
        run("VariableExpressionBenchmark.json", VariableExpressionBenchmark.class.getSimpleName());
    }
}
