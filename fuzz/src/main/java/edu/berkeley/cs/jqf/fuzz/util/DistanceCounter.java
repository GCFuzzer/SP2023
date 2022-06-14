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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Sicong Cao
 */
public class DistanceCounter {

    /** The size of the counter map. */
    protected final int size;

    /** Map for index-distance
     * index: exercised basic blocks.
     * value: distance for each basic blocks */
    protected final double[] counts;

    private final double infiniteNumber = Double.MAX_VALUE;

    /**
     * Creates a new counter with given size.
     *
     * @param size the fixed-number of elements in the hashtable.
     */
    public DistanceCounter(int size) {
        this.size = size;
        this.counts = new double[size];
        Arrays.fill(this.counts, Double.MAX_VALUE);
    }

    /**
     * Returns the size of this counter.
     *
     * @return the size of this counter
     */
    public int size() {
        return this.size;
    }

    /**
     * Clears the counter by setting all values to zero.
     */
    public void clear() {
        Arrays.fill(this.counts, Double.MAX_VALUE);
    }

    private int idx(int key) {
        return Hashing.hash(key, size);
    }

    //TODO: may be collision
    protected double incrementAtIndex(int index, double value) {
        return (this.counts[index] = value);
    }

    /**
     * Increments the count at the given key.
     *
     * <p>Note that the key is hashed and therefore the count
     * to increment may be shared with another key that hashes
     * to the same value. </p>
     *
     * @param key the key whose count to increment
     * @return the new value after incrementing the count
     */
    public double distanceIncrement(int key, double value) {
        return incrementAtIndex(idx(key), value);
    }

    /**
     * Returns the number of indices with non-zero counts.
     *
     * @return the number of indices with non-zero counts
     */
    public int getNonInfiniteSize() {
        int size = 0;
        for (int i = 0; i < counts.length; i++) {
            double count = counts[i];
            if (count != infiniteNumber) {
                size++;
            }
        }
        return size;
    }

    public int getZeroSize() {
        int size = 0;
        for (int i = 0; i < counts.length; i++) {
            double count = counts[i];
            if (count == 1.0) {
                size++;
            }
        }
        return size;
    }


    /**
     * Checks if all indices have zero counts
     * and returns a boolean as result.
     *
     * @return {@code true} if some indices have non-zero counts, otherwise {@code false}.
     */
    public boolean hasNonInfinites(){
        if (counts.length> 0){
            for (int i = 0; i < counts.length; i++) {
                double count = counts[i];
                if (count != infiniteNumber) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a set of indices at which the count is "infinite".
     *
     * <p>Note that, by doing this, "zero" represents the sink method is invoked.</p>
     *
     * @return a set of indices at which the count is infinite
     */
    public Collection<Integer> getNonInfiniteIndices() {
        List<Integer> indices = new ArrayList<>(size /2);
        for (int i = 0; i < counts.length; i++) {
            double count = counts[i];
            if (count != Double.MAX_VALUE) {
                indices.add(i);
            }
        }
        return indices;
    }
    /**
     * Returns a set of non-zero count values in this counter.
     *
     * @return a set of non-zero count values in this counter.
     */
    public Collection<Double> getNonInfiniteValues() {
        List<Double> values = new ArrayList<>(size /2);
        for (int i = 0; i < counts.length; i++) {
            double count = counts[i];
            if (count != infiniteNumber) {
                values.add(count);
            }
        }
        return values;
    }

    /**
     * Retrieves a value for a given key.
     *
     * <p>The key is first hashed to retrieve a value from
     * the counter, and hence the result is modulo collisions.</p>
     *
     * @param key the key to query
     * @return the count for the index corresponding to this key
     */
    public double get(int key) {
        return this.counts[idx(key)];
    }

    public double getAtIndex(int idx) {
        return this.counts[idx];
    }

    public void setAtIndex(int idx, double value) {
        this.counts[idx] = value;
    }
}
