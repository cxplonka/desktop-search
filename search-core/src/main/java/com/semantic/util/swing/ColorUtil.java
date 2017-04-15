/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ColorUtil {
    
    public static int convert(int a, int r, int g, int b) {
        return (((a & 0xFF) << 24)
                | ((r & 0xFF) << 16)
                | ((g & 0xFF) << 8)
                | ((b & 0xFF)));
    }

    public static int getAlpha(int argb) {
        return (argb >> 24) & 0xFF;
    }

    public static int getRed(int argb) {
        return (argb >> 16) & 0xFF;
    }

    public static int getGreen(int argb) {
        return (argb >> 8) & 0xFF;
    }

    public static int getBlue(int argb) {
        return argb & 0xFF;
    }
}