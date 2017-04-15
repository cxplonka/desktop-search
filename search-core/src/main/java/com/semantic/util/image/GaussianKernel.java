/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.image;

import java.awt.image.Kernel;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class GaussianKernel extends Kernel {

    public GaussianKernel(final int blurRadius) {
        super((2 * blurRadius) + 1, (2 * blurRadius) + 1, createKernel(blurRadius));
    }

    private static float[] createKernel(final int r) {
        int w = (2 * r) + 1;
        float[] kernel = new float[w * w];
        double m = 2.0d * Math.pow((r / 3.0d), 2);
        double n = Math.PI * m;

        double sum = 0.0d;
        for (int x = 0; x < w; x++) {
            int xr2 = (x - r) * (x - r);
            for (int y = 0; y < w; y++) {
                int yr2 = (y - r) * (y - r);
                kernel[x * w + y] = (float) (Math.pow(Math.E, -(yr2 + xr2) / m) / n);
                sum += kernel[x * w + y];
            }
        }

        for (int i = kernel.length - 1; i >= 0; i--) {
            kernel[i] /= sum;
        }
        return kernel;
    }
}