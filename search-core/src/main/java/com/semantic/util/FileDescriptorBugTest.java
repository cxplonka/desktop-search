/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

import java.io.File;
import java.io.FileInputStream;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FileDescriptorBugTest {

    public static void main(String[] arg) {
        /* only on network devices after ~ 1014 files on win 7 (samba) */
        Files.walkTree(new File("g:/G R A P H I C S"), new VisitorPattern<File>() {

            int c = 0;

            @Override
            public boolean visit(File node) {
                if (!node.isDirectory()) {
                    System.out.println(node);
                    FileInputStream stream = null;
                    try {
                        stream = new FileInputStream(node);
                        c++;
                    } catch (Exception e) {
                        /* java.io.IOException: Too many open files - windows bug!? */
                        e.printStackTrace();
                        /* */
                        System.out.println(c);
                        System.exit(1);
                    } finally {
                        FileUtil.quiteClose(stream);
                    }
                }
                return true;
            }
        });
    }
}