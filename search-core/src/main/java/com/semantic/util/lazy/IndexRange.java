/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.lazy;

/**
 *
 * @author Daniel Pfeifer
 */
public class IndexRange {

    private final int start;
    private final int end;

    public IndexRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public boolean contains(int index) {
        return start <= index && index < end;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }
}