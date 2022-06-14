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

import java.util.*;

import distanceCalculator.CallGraphLoader.LoadCallGraphFromChain;
import distanceCalculator.ControlFlowGraphGenerator.InsnBlock;
import edu.berkeley.cs.jqf.instrument.tracing.events.*;


/**
 * Utility class to collect method and basic block distance
 * @author Sicong Cao
 */
public class Distance implements TraceEventVisitor {

    /** The size of the coverage map. */
    private final int COVERAGE_MAP_SIZE = (1 << 12) - 1; // Minus one to reduce collisions

    /** The coverage counts for each edge. */
    private final DistanceCounter distanceCounter = new NonInfiniteCachingDistanceCounter(COVERAGE_MAP_SIZE);

    public LoadCallGraphFromChain graph;

    /** Creates a new distance map. */
    public Distance() {

    }

    /**
     * Returns the size of the coverage map.
     *
     * @return the size of the coverage map
     */
    public int size() {
        return COVERAGE_MAP_SIZE;
    }

    public void setGraph(LoadCallGraphFromChain graph){
        this.graph = graph;
    }

    public LoadCallGraphFromChain getGraph() {
        return graph;
    }
    /**
     * Updates distance information based on emitted event.
     *
     * <p>This method updates its internal counters for branch and
     * call events.</p>
     *
     * @param e the event to be processed
     */
    public void handleEvent(TraceEvent e) {
        e.applyVisitor(this);
    }

    @Override
    public void visitCallEvent(CallEvent e) {
        String signature = e.getContainingClass().split("/")[e.getContainingClass().split("/").length-1]+ "." +e.getContainingMethodName();
        Map<InsnBlock, String> blockMethodMap;
        blockMethodMap = graph.getBlockMethodMap();
        if (blockMethodMap.containsValue(signature)) {
            for (InsnBlock ib: blockMethodMap.keySet()) {
                // judge current BlockEvent emitted from which method
                if (blockMethodMap.get(ib).equals(signature)) {
                    for (String line: ib.lines) {
                        if (line.startsWith("ldc " + e.getIid()) && ib.getDistance()!=Double.MAX_VALUE){
                            distanceCounter.distanceIncrement(e.getIid(), ib.getDistance());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visitBlockEvent(BlockEvent e) {
        String signature = e.getContainingClass().split("/")[e.getContainingClass().split("/").length-1]+ "." +e.getContainingMethodName();
        Map<InsnBlock, String> blockMethodMap;
        blockMethodMap = graph.getBlockMethodMap();
        if (blockMethodMap.containsValue(signature)) {
            for (InsnBlock ib: blockMethodMap.keySet()) {
                // judge current BlockEvent emitted from which method
                if (blockMethodMap.get(ib).equals(signature)) {
                    for (String line: ib.lines) {
                        if (line.startsWith("ldc " + e.getIid()) && ib.getDistance()!=Double.MAX_VALUE){
                            distanceCounter.distanceIncrement(e.getIid(), ib.getDistance());
                        }
                    }
                }
            }
        }
    }

    public int getZeroCount() {
        return distanceCounter.getZeroSize();
    }

    public int getExecutedBlocks() {
        return distanceCounter.getNonInfiniteSize();
    }

    public Collection<Integer> getExecutedBlockIds() {
        return distanceCounter.getNonInfiniteIndices();
    }

    /**
     * @return the distance of current running seed
     */
    public double getDistance() {
        double totalDistance = 0.0;
        double seedDistance;
        for (int idx: this.distanceCounter.getNonInfiniteIndices()) {
            totalDistance += distanceCounter.getAtIndex(idx);
        }
        seedDistance = totalDistance / distanceCounter.getNonInfiniteSize();
        return seedDistance;
    }

    /**
     * Returns a set of edges in this coverage that don't exist in baseline
     *
     * @param baseline the baseline coverage
     * @return the set of edges that do not exist in {@code baseline}
     */
    public Collection<?> computeNewBlock(Distance baseline) {
        Collection<Integer> newBlock= new ArrayList<>();
        for (int idx : this.distanceCounter.getNonInfiniteIndices()) {
            if (baseline.distanceCounter.getAtIndex(idx) == Double.MAX_VALUE) {
                newBlock.add(idx);
            }
        }
        return newBlock;
    }

    /**
     * Clears the coverage map.
     */
    public void clear() {
        this.distanceCounter.clear();
    }

    /**
     * Updates this coverage with bits from the parameter.
     *
     * @param that the run coverage whose bits to OR
     *
     * @return <code>true</code> iff <code>that</code> is not a subset
     *         of <code>this</code>, causing <code>this</code> to change.
     */
    public boolean updateBits(Distance that) {
        boolean changed = false;
        if (that.distanceCounter.hasNonInfinites()) {
            for (int idx = 0; idx < COVERAGE_MAP_SIZE; idx++) {
                double before = this.distanceCounter.getAtIndex(idx);
                double after = that.distanceCounter.getAtIndex(idx);
                if (after != before) {
                    this.distanceCounter.setAtIndex(idx, after);
                    changed = true;
                }
            }
        }
        return changed;
    }

    /** Returns a hash code of the edge counts in the coverage map. */
    @Override
    public int hashCode() {
        return Arrays.hashCode(distanceCounter.counts);
    }

}
