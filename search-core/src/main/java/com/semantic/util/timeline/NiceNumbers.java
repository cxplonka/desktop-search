/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.timeline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * by graphic gems
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class NiceNumbers {

    public static double nicenum(double value, boolean round) {
        double expv = Math.floor(Math.log10(value));
        double f = value / (Math.pow(10, expv));
        double nf;

        if (round) {
            if (f < 1.5) {
                nf = 1;
            } else if (f < 3) {
                nf = 2;
            } else if (f < 7) {
                nf = 5;
            } else {
                nf = 10;
            }
        } else {
            if (f <= 1) {
                nf = 1;
            } else if (f <= 2) {
                nf = 2;
            } else if (f <= 5) {
                nf = 5;
            } else {
                nf = 10;
            }
        }

        return nf * Math.pow(10, expv);
    }

    protected static double[] ticks(double min, double max, int ticks) {
        double range = nicenum(max - min, false);
        double d = nicenum(range / (ticks - 1), true);
        double[] ret = new double[3];

        ret[0] = Math.floor(min / d) * d;
        ret[1] = Math.ceil(max / d) * d;
        ret[2] = d;

        return ret;
    }

    public static double[] tickValues(double min, double max, double step_size) {
        double[] ret = new double[(int) ((max - min) / step_size)];

        double d = nicenum((max - min) / (ret.length), true);
        double nice_stepssize = NiceNumbers.nicenum(step_size, false);

        double value = Math.floor(min / d) * d;
        double nfrac = Math.max(-Math.floor(Math.log(nice_stepssize)), 0);
        int idx = 0;

        while (value <= max) {
            if (!(value < min || value > max) && idx < ret.length) {
                ret[idx++] = round(value, (int) nfrac);
            }
            value += nice_stepssize;
        }

        return Arrays.copyOfRange(ret, 0, idx);
    }

    public static double round(double value, int place) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return value;
        }

        BigDecimal in = new BigDecimal(value);
        return in.setScale(place, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static double[] tickValues(double min, double max, int max_ticks) {
        double[] ret = ticks(min, max, max_ticks);
        double nfrac = Math.max(-Math.floor(Math.log(ret[2])), 0);
        double[] ticks = new double[Math.abs(max_ticks * 2)];
        int count = 0;

        for (double x = ret[0]; x < ret[1] + 0.5 * ret[2]; x += ret[2]) {
            double value = round(x, (int) nfrac);
            if (value >= min && value <= max) {
                ticks[count++] = value;
            }
        }

        return Arrays.copyOfRange(ticks, 0, count);
    }
}