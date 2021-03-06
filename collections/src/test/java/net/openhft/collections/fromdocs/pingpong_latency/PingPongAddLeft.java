/*
 * Copyright 2014 Higher Frequency Trading
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.collections.fromdocs.pingpong_latency;

import net.openhft.affinity.AffinitySupport;
import net.openhft.collections.SharedHashMap;
import net.openhft.collections.SharedHashMapBuilder;
import net.openhft.collections.fromdocs.BondVOInterface;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static net.openhft.lang.model.DataValueClasses.newDirectReference;

public class PingPongAddLeft {
    public static void main(String... ignored) throws IOException {
        SharedHashMap<String, BondVOInterface> shm = PingPongCASLeft.acquireSHM();

        playPingPong(shm, +1, true, "PingPongAddLEFT");
    }

    static void playPingPong(SharedHashMap<String, BondVOInterface> shm, double add, boolean setFirst, final String desc) {
        BondVOInterface bond1 = newDirectReference(BondVOInterface.class);
        BondVOInterface bond2 = newDirectReference(BondVOInterface.class);
        BondVOInterface bond3 = newDirectReference(BondVOInterface.class);
        BondVOInterface bond4 = newDirectReference(BondVOInterface.class);

        shm.acquireUsing("369604101", bond1);
        shm.acquireUsing("369604102", bond2);
        shm.acquireUsing("369604103", bond3);
        shm.acquireUsing("369604104", bond4);
        System.out.printf("\n\n" + desc + ": Timing off-heap operations on /dev/shm/RDR_DIM_Mock\n");
        if (setFirst) {
            bond1.setCoupon(add);
            bond2.setCoupon(add);
            bond3.setCoupon(add);
            bond4.setCoupon(add);
        }
        int timeToCallNanoTime = 30;
        int runs = 1000000;
        long[] timings = new long[runs];
        for (int j = 0; j < 100; j++) {
            for (int i = 0; i < runs; i++) {
                long _start = System.nanoTime();
                bond1.addAtomicCoupon(add);
                bond2.addAtomicCoupon(add);
                bond3.addAtomicCoupon(add);
                bond4.addAtomicCoupon(add);
                timings[i] = (System.nanoTime() - _start - timeToCallNanoTime) / 4;
            }
            Arrays.sort(timings);
            System.out.printf("#%d:  atomic add coupon 50/90/99%%tile was %,d / %,d / %,d%n",
                    j, timings[runs / 2], timings[runs * 9 / 10], timings[runs * 99 / 100]);
        }
    }
}