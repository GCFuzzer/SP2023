/*
 * Copyright (c) 2017-2018 The Regents of the University of California
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.berkeley.cs.jqf.fuzz.gc;

import java.io.File;
import java.time.Duration;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import org.junit.runner.Result;

/**
 * Entry point for fuzzing with GCFuzz.
 *
 * @author Sicong Cao
 */
public class GCFuzzDriver {

    public static void main(String[] args) {
        if (args.length < 2){
            System.err.println("Usage: java " + GCFuzzDriver.class + " TEST_CLASS TEST_METHOD [OUTPUT_DIR [SEED_DIR | SEED_FILES...]]");
            System.exit(1);
        }

        String testClassName  = args[0];
        String testMethodName = args[1];
        String targetChain = "<java.util.PriorityQueue: void readObject(java.io.ObjectInputStream)>--><java.util.PriorityQueue: void heapify()>--><java.util.PriorityQueue: void siftDown(int,java.lang.Object)>--><java.util.PriorityQueue: void siftDownUsingComparator(int,java.lang.Object)>--><org.apache.commons.collections4.comparators.TransformingComparator: int compare(java.lang.Object,java.lang.Object)>--><org.apache.commons.collections4.functors.InvokerTransformer: java.lang.Object transform(java.lang.Object)>--><java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>";
        String outputDirectoryName = args.length > 2 ? args[2] : "fuzz-results";
        File outputDirectory = new File(outputDirectoryName);
        File[] seedFiles = null;
        if (args.length > 3) {
            seedFiles = new File[args.length-3];
            for (int i = 3; i < args.length; i++) {
                seedFiles[i-3] = new File(args[i]);
            }
        }

        try {
            // Load the guidance
            String title = testClassName+"#"+testMethodName;
            GCFuzzGuidance guidance = null;

            //for each chain, we limit the validation time to 100 seconds
            if (seedFiles == null) {
                guidance = new GCFuzzGuidance(title, targetChain, Duration.ofSeconds(10000), outputDirectory);
            } else if (seedFiles.length == 1 && seedFiles[0].isDirectory()) {
                guidance = new GCFuzzGuidance(title, targetChain, Duration.ofSeconds(100), outputDirectory, seedFiles[0]);
            } else {
                guidance = new GCFuzzGuidance(title, targetChain, Duration.ofSeconds(100), outputDirectory, seedFiles);
            }


            // Run the Junit test
            Result res = GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
            if (Boolean.getBoolean("jqf.logDistance")) {
                System.out.printf("Minimum distance is %.2f",
                        guidance.getMinDistance());
            }
            if (Boolean.getBoolean("jqf.ei.EXIT_ON_CRASH") && !res.wasSuccessful()) {
                System.exit(3);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }

    }
}
