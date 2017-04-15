/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

import java.io.File;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class Files {

    public static void walkTree(File directory, VisitorPattern<File> visitor) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!visitor.visit(file)) {
                    return;
                }
                /* go deeper */
                if (file.isDirectory()) {
                    walkTree(file, visitor);
                }
            }
        }
    }
}
