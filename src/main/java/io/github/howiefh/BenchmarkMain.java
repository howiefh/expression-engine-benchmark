/*
 * @(#)BenchmarkMain 1.0 2023/9/7
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
import io.github.howiefh.expression.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
public class BenchmarkMain {

    public static void main(String[] args) {
        int times = 100_0000;
        testLiteral(times);
        testVariableExpression(times);
        testMethod(times);
    }

    public static void testLiteral(int times) {
        System.out.println("literal expression:");
        testLiteralArithmetic(times);
        testLiteralNestedTernaryArithmetic(times);
    }

    public static void testLiteralArithmetic(int times) {
        System.out.println("literal arithmetic expression");
        for (String type : ExpressionEngineFactory.ALL) {
            // MVEL, JUEL 计算(600 - 3 * 15) / (((68 - 9) - 3) * 2 - 100) 除法时转为了Double类型，计算结果和其他不一致
            // MVEL
            // https://github.com/mvel/mvel/blob/85125e1af5f3ec32990b1768b8656bfcf450b489/src/main/java/org/mvel2/math/MathProcessor.java#L81
            // JUEL
            // https://github.com/beckchr/juel/blob/b9eb92d8dd72f3b5114903bf46af8eacaed3daf5/modules/impl/src/main/java/de/odysseus/el/misc/NumberOperations.java#L127
            // https://github.com/beckchr/juel/blob/b9eb92d8dd72f3b5114903bf46af8eacaed3daf5/modules/impl/src/main/java/de/odysseus/el/misc/TypeConverterImpl.java#L120
            String expression = "1000 + 100.0 * 99 - (600 - 3 * 15) / (((68 - 9) - 3) * 2 - 100) + 10000 % 7 * 71";
            benchmark(ExpressionEngineFactory.create(type), expression, Collections.emptyMap(), times);
        }
        System.out.println();
    }

    private static void testLiteralNestedTernaryArithmetic(int times) {
        System.out.println("literal nested ternary arithmetic expression");
        for (String type : ExpressionEngineFactory.ALL) {
            String expression = "6.7 - 100 > 39.6 ? 5 == 5 ? 4 + 5 : 6 - 1 : !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3";
            if (ExpressionEngineFactory.QL_EXPRESS.equals(type)) {
                // 不加括号QLExpress报错 但是加上后MVEL报错
                expression = "6.7 - 100 > 39.6 ? (5 == 5 ? 4 + 5 : 6 - 1) : (!(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3)";
            }
            benchmark(ExpressionEngineFactory.create(type), expression, Collections.emptyMap(), times);
        }
        System.out.println();
    }


    public static void testVariableExpression(int times) {
        System.out.println("including variable expression:");
        testBasicArithmeticVariableExpression(times);
        testBigDecimalVariableExpression(times);
        testBasicConditionVariableExpression(times);
        testConditionVariableExpression(times);
    }

    public static void testBasicArithmeticVariableExpression(int times) {
        System.out.println("including basic variable expression");
        for (String type : ExpressionEngineFactory.ALL) {
            // Aviator,OGNL,JUEL 由于计算pi + 99将pi float转为double精度与其他计算结果不一致
            // Aviator 仅支持double浮点数， float会转为double，整数则都会转为long，并按 long->BitInteger->BigDecimal->double顺序转换.注意如果BigDecimal和double参与计算时会转为double
            // https://github.com/killme2008/aviatorscript/blob/20a3c7f432446861da28a67d5fdee7d959fcc05c/src/main/java/com/googlecode/aviator/runtime/type/AviatorNumber.java#L103
            // https://github.com/killme2008/aviatorscript/blob/20a3c7f432446861da28a67d5fdee7d959fcc05c/src/main/java/com/googlecode/aviator/runtime/type/AviatorNumber.java#L77
            // OGNL
            // https://github.com/orphan-oss/ognl/blob/2f44ece85ddfe3c5f906680fdc49534e09ba050a/src/main/java/ognl/OgnlOps.java#L759
            // https://github.com/orphan-oss/ognl/blob/2f44ece85ddfe3c5f906680fdc49534e09ba050a/src/main/java/ognl/OgnlOps.java#L217
            // JUEL
            // https://github.com/beckchr/juel/blob/b9eb92d8dd72f3b5114903bf46af8eacaed3daf5/modules/impl/src/main/java/de/odysseus/el/misc/NumberOperations.java#L74
            // https://github.com/beckchr/juel/blob/b9eb92d8dd72f3b5114903bf46af8eacaed3daf5/modules/impl/src/main/java/de/odysseus/el/misc/TypeConverterImpl.java#L120
            // JEXL 计算结果恰好与 JavaScript 计算结果一致d * b / pi 中将pi float转为double时用了不同的方式
            // Double.parseDouble(String.valueOf(val)); 其他如转换使用 ((Number)val).doubleValue()
            // https://github.com/apache/commons-jexl/blob/b5eb38c762e0fb866a13dd0b29fd44ab98c1dabd/src/main/java/org/apache/commons/jexl3/JexlArithmetic.java#L1313
            // https://github.com/apache/commons-jexl/blob/b5eb38c762e0fb866a13dd0b29fd44ab98c1dabd/src/main/java/org/apache/commons/jexl3/JexlArithmetic.java#L734
            String expression = "pi * d + b - (1000 - d * b / pi) / (pi + 99 - i * d) - i * pi * d / b";
            benchmark(ExpressionEngineFactory.create(type), expression, getArithmeticContextMap(), times);
        }
        System.out.println();
    }

    private static void testBigDecimalVariableExpression(int times) {
        System.out.println("including BigDecimal variable expression");
        for (String type : ExpressionEngineFactory.ALL) {
            // MVEL,OGNL,JEXL支持BigDecimal字面量 1000B Aviator支持字面量1000M
            // MVEL,Aviator,JEXL MathContext 使用 MathContext.DECIMAL128 执行结果相同
            // MVEL 用/做除法时无法自定义 MathContext 被除数为BigDecimal时除数会转为BigDecimal
            // java.math.BigDecimal.divide(java.math.BigDecimal, java.math.MathContext)
            // https://github.com/mvel/mvel/blob/85125e1af5f3ec32990b1768b8656bfcf450b489/src/main/java/org/mvel2/math/MathProcessor.java#L135
            // Aviator 设定运算精度可以通过 Options.MATH_CONTEXT 选项，默认是 MathContext.DECIMAL128。被除数为BigDecimal除数不是Double时除数转为BigDecimal，否则被除数转为Double
            // java.math.BigDecimal.divide(java.math.BigDecimal, java.math.MathContext)
            // 对于货币计算，或者科学数值计算等场景，需要极高的精度，这种情况下你可以通过设置下列两个选项：
            // ● Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL 强制将所有浮点数（包括科学计数法）都解析为 decimal 类型。
            // ● Options.ALWAYS_PARSE_INTEGRAL_NUMBER_INTO_DECIMAL 将所有整数解析为 decimal 类型。
            // 来强制将脚本中出现的字面量数字都解析为 decimal 类型参与高精度运算，但是从外部传入的变量需要用户自行保证。
            // https://github.com/killme2008/aviatorscript/blob/20a3c7f432446861da28a67d5fdee7d959fcc05c/src/main/java/com/googlecode/aviator/runtime/type/AviatorDecimal.java#L86
            // JEXL new JexlBuilder().arithmetic(new JexlArithmetic(true, MathContext.DECIMAL128, Integer.MIN_VALUE)) 可以设置 MathContext 有操作数为BigDecimal会转换其他操作数为BigDecimal (java.math.BigDecimal.divide(java.math.BigDecimal, java.math.MathContext))
            // https://github.com/apache/commons-jexl/blob/b5eb38c762e0fb866a13dd0b29fd44ab98c1dabd/src/main/java/org/apache/commons/jexl3/JexlArithmetic.java#L1308
            // SpEl 使用操作数的最大scale，RoundingMode.HALF_EVEN 有操作数为BigDecimal会转换其他操作数为BigDecimal (java.math.BigDecimal.divide(java.math.BigDecimal, int, java.math.RoundingMode))
            // https://github.com/spring-projects/spring-framework/blob/19588d413d0d2d4e7244be1e331ffa66443c69c4/spring-expression/src/main/java/org/springframework/expression/spel/ast/OpDivide.java#L58
            // OGNL 使用不指定scale的，RoundingMode.HALF_EVEN 除法 有操作数为BigDecimal会转换其他操作数为BigDecimal (java.math.BigDecimal.divide(java.math.BigDecimal, int))
            // https://github.com/orphan-oss/ognl/blob/2f44ece85ddfe3c5f906680fdc49534e09ba050a/src/main/java/ognl/OgnlOps.java#L810
            // JUEL 不支持字面量的BigDecimal 也不支持通过构造方法创建BigDecimal 但是有操作数为BigDecimal会转换其他操作数为BigDecimal
            // 用不指定scale，RoundingMode.HALF_UP的除法 (java.math.BigDecimal.divide(java.math.BigDecimal, int))
            // https://github.com/beckchr/juel/blob/b9eb92d8dd72f3b5114903bf46af8eacaed3daf5/modules/impl/src/main/java/de/odysseus/el/misc/NumberOperations.java#L125
            // QLExpress
            // 高精度计算在会计财务中非常重要，类似汇金的系统中，会有很多BigDecimal转换代码。而使用QLExpress，你只要关注数学公式本身 订单总价 = 单价 * 数量 + 首重价格 + （ 总重量 - 首重） * 续重单价 ，然后设置这个属性即可，所有的中间运算过程都会保证不丢失精度。
            // 可以使用 new ExpressRunner(true, false); 第一个参数isPrecise传入true指定scale 此时除法scale为10
            // 第一个参数isPrecise传入false时，即使传入参数为BigDecimal，此时用不指定scale，RoundingMode.HALF_UP的除法(java.math.BigDecimal.divide(java.math.BigDecimal, int))会与isPrecise为true时计算结果不一致 (java.math.BigDecimal.divide(java.math.BigDecimal, int, java.math.RoundingMode))
            // 有操作数为BigDecimal会转换其他操作数为BigDecimal
            // https://github.com/alibaba/QLExpress/blob/4004bef0aa78c9026c6cbafa731f1789eb4ff37b/src/main/java/com/ql/util/express/OperatorOfNumber.java#L376
            // https://github.com/alibaba/QLExpress/blob/4004bef0aa78c9026c6cbafa731f1789eb4ff37b/src/main/java/com/ql/util/express/OperatorOfNumber.java#L440
            // https://github.com/alibaba/QLExpress/blob/4004bef0aa78c9026c6cbafa731f1789eb4ff37b/src/main/java/com/ql/util/express/OperatorOfNumber.java#L345
            String expression = "pi * d + b - (1000 - d * b / pi) / (pi + 99 - i * d) - i * pi * d / b";
            if (ExpressionEngineFactory.JANINO.equals(type)) {
//                expression = "pi.multiply(d).add(b).subtract(new java.math.BigDecimal(\"1000\").subtract(d.multiply(b).divide(pi, java.math.MathContext.DECIMAL128)).divide(pi.add(new java.math.BigDecimal(\"99\")).subtract(i.multiply(d)), java.math.MathContext.DECIMAL128)).subtract(i.multiply(pi).multiply(d).divide(b, java.math.MathContext.DECIMAL128))";
                expression = "pi.multiply(d).add(b).subtract(new java.math.BigDecimal(\"1000\").subtract(d.multiply(b).divide(pi, java.math.RoundingMode.HALF_EVEN)).divide(pi.add(new java.math.BigDecimal(\"99\")).subtract(i.multiply(d)), java.math.RoundingMode.HALF_EVEN)).subtract(i.multiply(pi).multiply(d).divide(b, java.math.RoundingMode.HALF_EVEN))";
            }
            benchmark(ExpressionEngineFactory.create(type), expression, getBigDecimalArithmeticContextMap(), times);
        }
        System.out.println();
    }

    private static void testBasicConditionVariableExpression(int times) {
        System.out.println("including basic condition variable expression");
        for (String type : ExpressionEngineFactory.ALL) {
            String expression = "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 == i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
            if (ExpressionEngineFactory.QL_EXPRESS.equals(type)) {
                // 不转换类型会抛出不支持的对象执行了"mod"操作
                expression = "i * pi + (d * b - 199) / (1 - d * pi) - (int)(2 + 100 - i / pi) % 99 == i * pi + (d * b - 199) / (1 - d * pi) - (int)(2 + 100 - i / pi) % 99";
            }
            benchmark(ExpressionEngineFactory.create(type), expression, getArithmeticContextMap(), times);
        }
        System.out.println();
    }

    private static void testConditionVariableExpression(int times) {
        System.out.println("including condition variable expression");
        for (String type : ExpressionEngineFactory.ALL) {
            String expression = "(clientVersion == '1.9.0' || clientVersion == '1.9.1' || clientVersion == '1.9.2') && deviceType == 'Xiaomi' && weight >= 4 && osVersion == 'Android 9.0' && osType == 'Android' && clientIp != null && requestTime <= now&& customer.grade > 1 && customer.age > 18";
            if (ExpressionEngineFactory.AVIATOR.equals(type)) {
                expression = "(clientVersion == '1.9.0' || clientVersion == '1.9.1' || clientVersion == '1.9.2') && deviceType == 'Xiaomi' && weight >= 4 && osVersion == 'Android 9.0' && osType == 'Android' && clientIp != nil && requestTime <= now&& customer.grade > 1 && customer.age > 18";
            } else if (ExpressionEngineFactory.JANINO.equals(type)) {
                expression = "(clientVersion == \"1.9.0\" || clientVersion == \"1.9.1\" || clientVersion == \"1.9.2\") && deviceType == \"Xiaomi\" && weight >= 4 && osVersion == \"Android 9.0\" && osType == \"Android\" && clientIp != null && requestTime.compareTo(now) <= 0&& customer.getGrade() > 1 && customer.getAge() > 18";
            }
            benchmark(ExpressionEngineFactory.create(type), expression, getConditionContextMap(), times);
        }
        System.out.println();
    }

    private static void testMethod(int times) {
        System.out.println("including method expression:");
        testConstructorMethod(times);
        testSubstringMethod(times);
        testSeriesSubstringMethod(times);
    }


    private static void testConstructorMethod(int times) {
        System.out.println("including constructor method expression");
        for (String type : ExpressionEngineFactory.ALL) {
            String expression = "new java.util.Date()";
            if (ExpressionEngineFactory.JUEL.equals(type)) {
                // 不能直接new 需要加 context.setFunction("fn", "date", JUELExpressionEngine.class.getMethod("now"));
                expression = "fn:date()";
            }
            benchmark(ExpressionEngineFactory.create(type), expression, Collections.emptyMap(), times);
        }
        System.out.println();
    }

    private static void testSubstringMethod(int times) {
        System.out.println("including substring method expression");
        for (String type : ExpressionEngineFactory.ALL) {
            String expression = "s.substring(b.d)";
            if (ExpressionEngineFactory.AVIATOR.equals(type)) {
                expression = "string.substring(s, b.d)";
            } else if (ExpressionEngineFactory.JANINO.equals(type)) {
                // 需要转换类型否则报错
                expression = "s.substring((Integer)b.get(\"d\"))";
            }

            benchmark(ExpressionEngineFactory.create(type), expression, getMethodContextMap(), times);
        }
        System.out.println();
    }

    private static void testSeriesSubstringMethod(int times) {
        System.out.println("including series substring method expression");
        for (String type : ExpressionEngineFactory.ALL) {
            String expression = "s.substring(b.d).substring(a, b.c.e)";
            if (ExpressionEngineFactory.AVIATOR.equals(type)) {
                expression = "string.substring(string.substring(s, b.d), a, b.c.e)";
            } else if (ExpressionEngineFactory.JANINO.equals(type)) {
                expression = "s.substring((Integer)b.get(\"d\")).substring(a, (Integer)((java.util.Map)b.get(\"c\")).get(\"e\"))";
            }
            benchmark(ExpressionEngineFactory.create(type), expression, getMethodContextMap(), times);
        }
        System.out.println();
    }

    private static Map<String, Object> getArithmeticContextMap() {
        Map<String, Object> context = new HashMap<>();
        context.put("i", 100);
        context.put("pi", 3.14F);
        context.put("d", -3.9D);
        // byte 时 mvel报错 internal error: 113
        // https://github.com/mvel/mvel/blob/85125e1af5f3ec32990b1768b8656bfcf450b489/src/main/java/org/mvel2/math/MathProcessor.java#L132
        // context.put("b", (byte) 4);
        context.put("b", 4);
        return context;
    }

    private static Map<String, Object> getBigDecimalArithmeticContextMap() {
        Map<String, Object> context = new HashMap<>();
        context.put("i", new BigDecimal("100"));
        context.put("pi", new BigDecimal("3.14"));
        context.put("d", new BigDecimal("-3.9"));
        context.put("b", new BigDecimal("4"));
        return context;
    }

    private static Map<String, Object> getConditionContextMap() {
        Map<String, Object> context = new HashMap<>();
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

    private static Map<String, Object> getMethodContextMap() {
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

    private static void benchmark(ExpressionEngine expressionEngine, String expression, Map<String, Object> context, int times) {
        Object result = expressionEngine.execute(expression, context);
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            expressionEngine.execute(expression, context);
        }
        System.out.println("type: " + expressionEngine.getType() + " time: " + (System.currentTimeMillis() - start) + " result: " + result + " (" + (result != null ? result.getClass() : null) + ")");
    }

}
