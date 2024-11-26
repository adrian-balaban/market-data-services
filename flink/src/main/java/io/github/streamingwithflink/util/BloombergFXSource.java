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
package io.github.streamingwithflink.util;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Flink SourceFunction to generate SensorReadings with random temperature values.
 *
 * Each parallel instance of the source simulates 10 sensors which emit one sensor reading every 100 ms.
 *
 * Note: This is a simple data-generating source function that does not checkpoint its state.
 * In case of a failure, the source does not replay any data.
 */
public class BloombergFXSource extends RichParallelSourceFunction<BloombergFXRateReading> {

    private final long SLEEP_MILLIS = 100;
    // flag indicating whether source is still running
    private boolean running = true;

    /** run() continuously emits SensorReadings by emitting them through the SourceContext. */
    @Override
    public void run(SourceContext<BloombergFXRateReading> srcCtx) throws Exception {


        // initialize random number generator
        Random rand = new Random();
        // look up index of this parallel task
        int taskIdx = this.getRuntimeContext().getIndexOfThisSubtask();

        // initialize sensor ids and temperatures
        final List<BloombergFXRateReading> fxRates = new ArrayList<>();
        fxRates.add(new BloombergFXRateReading("USD/JPY", "USD", "JPY", 110.45, 108.45, 0));
        fxRates.add(new BloombergFXRateReading("EUR/USD", "EUR", "USD", 1.1357, 1.1337, 0));

        while (running) {

            // get current time
            long curTime = Calendar.getInstance().getTimeInMillis();

            // emit readings
            fxRates.forEach(rate -> {
                rate.setAsk(formatToFourDecimals(rate.getAsk() + (Math.random() - 0.5) * 0.001));
                rate.setBid(formatToFourDecimals(rate.getBid() + (Math.random() - 0.5) * 0.001));
                rate.setTimestamp(curTime);
                // emit reading
                srcCtx.collect(rate);
            });

            // wait for 100 ms
            Thread.sleep(SLEEP_MILLIS);
        }
    }

    private double formatToFourDecimals(double num) {
        return Math.round(num * 10000.0) / 10000.0;
    }

    /** Cancels this SourceFunction. */
    @Override
    public void cancel() {
        this.running = false;
    }
}
