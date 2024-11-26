/*
 * Copyright 2015 Fabian Hueske / Vasia Kalavri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.streamingwithflink.chapter1;

import io.github.streamingwithflink.util.*;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class BloombergForexReadings {

    /**
     * main() defines and executes the DataStream program.
     *
     * @param args program arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // set up the streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // use event time for the application
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // configure watermark interval
        env.getConfig().setAutoWatermarkInterval(1000L);

        // ingest sensor stream
        DataStream<BloombergFXRateReading> fxMarketData = env
            // BloombergFXSource generates readings
            .addSource(new BloombergFXSource())
            // assign timestamps and watermarks which are required for event time
            .assignTimestampsAndWatermarks(new BloombergFXRateAssigner());

        SingleOutputStreamOperator<BloombergFXRateReading> avgTemp = fxMarketData
            // organize stream by currency pair
            .keyBy(r -> r.pair)
            // group readings in 1 second windows
            .timeWindow(Time.seconds(1))
            // compute average bid and ask using a user-defined function
            .apply(new BidAskAverager());

        // print result stream to standard out
        avgTemp.print();

        // execute application
        env.execute("Compute average FX Market currencies pairs bid and ask values over 1 sec.");
    }

    /**
     *  User-defined WindowFunction to compute the average temperature of SensorReadings
     */
    public static class BidAskAverager implements WindowFunction<BloombergFXRateReading, BloombergFXRateReading, String, TimeWindow> {

        /**
         * apply() is invoked once for each window.
         *
         * @param pair the key (pair) of the window
         * @param window meta data for the window
         * @param input an iterable over the collected sensor readings that were assigned to the window
         * @param out a collector to emit results from the function
         */
        @Override
        public void apply(String pair, TimeWindow window, Iterable<BloombergFXRateReading> input, Collector<BloombergFXRateReading> out) {

            String baseCurrency = pair.substring(0,2);
            String quoteCurrency = pair.substring(4);
            // compute the average temperature
            int cnt = 0;
            double sumBid = 0.0;double sumAsk = 0.0;
            for (BloombergFXRateReading r : input) {
                cnt++;
                sumBid += r.bid;
                sumAsk += r.ask;
            }
            double avgBid = sumBid / cnt;
            double avgAsk = sumAsk / cnt;

            // emit a BloombergFXRateReading with the average temperature
            out.collect(new BloombergFXRateReading(pair, baseCurrency, quoteCurrency, avgAsk, avgBid, window.getEnd()));
        }
    }
}
