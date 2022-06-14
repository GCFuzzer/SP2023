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
package edu.berkeley.cs.jqf.fuzz.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An extension of {@link Counter} that caches which entries
 * are non-zero.
 *
 * <p>This version of counter is used by classes such as
 * {@link Coverage}, which require frequent queries of
 * the non-zero locations.</p>
 */
public class NonInfiniteCachingDistanceCounter extends DistanceCounter {

    private int nonInfiniteCount;

    private final double infiniteNumber = Double.MAX_VALUE;

    private Collection<Integer> nonInfiniteIndices;

    public NonInfiniteCachingDistanceCounter(int size) {
        super(size);
        this.nonInfiniteCount = 0;
        this.nonInfiniteIndices = new ArrayList<>();
    }

    @Override
    public void clear() {
        for (int idx : nonInfiniteIndices) {
            counts[idx] = infiniteNumber;
        }
        this.nonInfiniteCount = 0;
        this.nonInfiniteIndices.clear();
    }

    @Override
    public double incrementAtIndex(int index, double value) {
        double newValue = super.incrementAtIndex(index, value);
        // A count becomes non-zero if it was incremented to delta
        if (newValue == value) {
            nonInfiniteIndices.add(index);
            nonInfiniteCount++;
        }
        return newValue;
    }

    @Override
    public int getNonInfiniteSize() {
        return nonInfiniteCount;
    }

    @Override
    public boolean hasNonInfinites(){
        return nonInfiniteCount > 0;
    }

    @Override
    public Collection<Integer> getNonInfiniteIndices() {
        return nonInfiniteIndices;
    }

    @Override
    public Collection<Double> getNonInfiniteValues() {
        List<Double> values = new ArrayList<>(size /2);
        for (int idx : nonInfiniteIndices) {
            double count = counts[idx];
            assert (count != infiniteNumber);
            values.add(count);
        }
        return values;
    }

    @Override
    public void setAtIndex(int index, double newValue) {
        double oldValue = counts[index];
        super.setAtIndex(index, newValue);
        if (oldValue == infiniteNumber && newValue != infiniteNumber) {
            nonInfiniteIndices.add(index);
            nonInfiniteCount++;
        }
    }


}
