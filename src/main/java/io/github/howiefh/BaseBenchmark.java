/*
 * @(#)BaseBenchmark 1.0 2023/9/12
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

import io.github.howiefh.expression.ExpressionEngine;
import io.github.howiefh.expression.ExpressionEngineFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author fenghao
 * @version 1.0
 * @since 1.0
 */
@BenchmarkMode({Mode.AverageTime})
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 10, time = 5)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BaseBenchmark {

    public static void run(String filename, String...includes) throws RunnerException {
        ChainedOptionsBuilder optionsBuilder = new OptionsBuilder()
                .result(filename)
                .resultFormat(ResultFormatType.JSON);
        for (String reg : includes) {
            optionsBuilder.include(reg);
        }
        Options opt = optionsBuilder.build();
        new Runner(opt).run();
    }


    @State(Scope.Benchmark)
    public static class BenchmarkData {

        public ExpressionEngine mvel = ExpressionEngineFactory.create(ExpressionEngineFactory.MVEL);
        public ExpressionEngine aviator = ExpressionEngineFactory.create(ExpressionEngineFactory.AVIATOR);
        public ExpressionEngine qlExpress = ExpressionEngineFactory.create(ExpressionEngineFactory.QL_EXPRESS);
        public ExpressionEngine spEl = ExpressionEngineFactory.create(ExpressionEngineFactory.SP_EL);
        public ExpressionEngine ognl = ExpressionEngineFactory.create(ExpressionEngineFactory.OGNL);
        public ExpressionEngine jexl = ExpressionEngineFactory.create(ExpressionEngineFactory.JEXL);
        public ExpressionEngine juel = ExpressionEngineFactory.create(ExpressionEngineFactory.JUEL);
        public ExpressionEngine janino = ExpressionEngineFactory.create(ExpressionEngineFactory.JANINO);
    }

}
