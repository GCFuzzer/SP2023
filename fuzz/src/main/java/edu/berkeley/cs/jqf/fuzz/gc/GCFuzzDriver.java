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

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import org.junit.runner.Result;

/**
 * Entry point for fuzzing with GCFuzz.
 *
 * @author Sicong Cao
 */
public class GCFuzzDriver {

    public static void main(String[] args) throws IOException {
        if (args.length < 3){
            System.err.println("Usage: java " + GCFuzzDriver.class + " TEST_CLASS TEST_METHOD TEST_CHAINS [OUTPUT_DIR [SEED_DIR | SEED_FILES...]]");
            System.exit(1);
        }

        String testClassName  = args[0];
        String testMethodName = args[1];
        // String targetChain = "<java.util.PriorityQueue: void readObject(java.io.ObjectInputStream)>--><java.util.PriorityQueue: void heapify()>--><java.util.PriorityQueue: void siftDown(int,java.lang.Object)>--><java.util.PriorityQueue: void siftDownUsingComparator(int,java.lang.Object)>--><org.apache.commons.collections4.comparators.TransformingComparator: int compare(java.lang.Object,java.lang.Object)>--><java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>";
        String filePath = args[2];
        String outputDirectoryName = args.length > 3 ? args[3] : "fuzz-results";
        File outputDirectory = new File(outputDirectoryName);
        File[] seedFiles = null;
        if (args.length > 4) {
            seedFiles = new File[args.length-4];
            for (int i = 4; i < args.length; i++) {
                seedFiles[i-4] = new File(args[i]);
            }
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File Not Exist: " + filePath);
        }
        List<String> lines = Files.readAllLines(file.toPath());

        try {
            // Load the guidance
            String title = testClassName+"#"+testMethodName;
            GCFuzzGuidance guidance = null;
            FileWriter fw = null;
            List<String> targetClass= new ArrayList<>();
            File fileWriter = new File("./examples/src/test/resources/dictionaries/CUT.dict");
            Writer out = new FileWriter(fileWriter);
            BufferedWriter bw= new BufferedWriter(out);

            // validate each chain
            for(String line : lines){
                String[] MethodList = line.split("-->");
                for (String signature: MethodList) {
                    targetClass.add(signature.substring(1,signature.length()-1).split(":")[0]);
                }
                LinkedHashSet<String> hashSet = new LinkedHashSet<>(targetClass);
                ArrayList<String> listWithoutDuplicates = new ArrayList<>(hashSet);
                for (String s: listWithoutDuplicates) {
                    if (s.equals("java.lang.reflect.Method")) {
                        continue;
                    }
                    bw.write(s);
                    bw.newLine();
                    bw.flush();
                }
                bw.close();

                if (seedFiles == null) {
                    guidance = new GCFuzzGuidance(title, line, Duration.ofSeconds(100000), outputDirectory);
                } else if (seedFiles.length == 1 && seedFiles[0].isDirectory()) {
                    guidance = new GCFuzzGuidance(title, line, Duration.ofSeconds(100), outputDirectory, seedFiles[0]);
                } else {
                    guidance = new GCFuzzGuidance(title, line, Duration.ofSeconds(100), outputDirectory, seedFiles);
                }

                // Run the Junit test
                Result res = GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
                // TODO: define "jqf.ei.EXIT_ON_TIMEOUT"
                if (Boolean.getBoolean("jqf.ei.EXIT_ON_TIMEOUT")) {
                    continue;
                }
                if (Boolean.getBoolean("jqf.ei.EXIT_ON_CRASH") && !res.wasSuccessful()) {
                    System.exit(3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }

    }
}
