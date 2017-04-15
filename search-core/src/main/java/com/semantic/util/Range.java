/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class Range {

    private double lower;
    private double upper;

    public Range() {
    }

    public Range(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public Range(double... values) {
        lower = Float.MAX_VALUE;
        upper = -Float.MAX_VALUE;
        /* create range from values */
        for (double value : values) {
            add(value);
        }
    }

    public double getUpperBound() {
        return upper;
    }

    public void setUpperBound(double upper) {
        this.upper = upper;
    }

    public double getLowerBound() {
        return lower;
    }

    public void setLowerBound(double lower) {
        this.lower = lower;
    }

    public void set(Range range) {
        this.lower = range.getLowerBound();
        this.upper = range.getUpperBound();
    }

    public void setBounds(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * transform the fraction back to the range bounds
     * @param fraction normalized value [0..1]
     * @return
     */
    public double invert(double fraction) {
        return lower + fraction * (upper - lower);
    }

    /**
     * normalize the value between the upper and lower
     * range [0..1]
     * @param value
     * @return
     */
    public double normalize(double value) {
        return (value - lower) / (upper - lower);
    }

    /**
     * if the value is greater or lower then the current bounds,
     * then we take the value
     * @param value
     * @return
     */
    public void add(double value) {
        if (value < lower) {
            lower = value;
        }
        /* so we have valid bounds when we initilize with invalid values */
        if (value > upper) {
            upper = value;
        }
    }

    public void add(Range range) {
        add(range.getLowerBound());
        add(range.getUpperBound());
    }

    public double getWidth() {
        return (upper - lower);
    }

    public double[] toDouble() {
        return new double[]{lower, upper};
    }

    /**
     * transform the normalized values back to the range values
     * @param range value range
     * @param fractions normalized values[0..1]
     * @return
     */
    public double[] toValues(float[] fractions) {
        double[] ret = new double[fractions.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = invert(fractions[i]);
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Range other = (Range) obj;
        if (this.lower != other.lower) {
            return false;
        }
        if (this.upper != other.upper) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.lower)
                ^ (Double.doubleToLongBits(this.lower) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.upper)
                ^ (Double.doubleToLongBits(this.upper) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("lower: %s upper: %s", lower, upper);
    }
}
