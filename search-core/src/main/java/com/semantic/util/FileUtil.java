/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

import java.io.*;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FileUtil {

    /**
     * http://programming.guide/java/formatting-byte-size-to-human-readable-format.html
     * @param bytes
     * @param si
     * @return
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getName(String path) {
        int index = path.lastIndexOf(File.separatorChar);
        if (index < 0) {
            return path;
        }
        return path.substring(index + 1);
    }

    public static void quiteClose(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            /* Ignore */
        }
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        int idx = name.lastIndexOf('.');
        if (!file.isDirectory() && idx != -1) {
            return name.substring(idx + 1, name.length()).toLowerCase();
        }
        return "";
    }

    public static void copy(File from, File to) throws IOException {
        if (!from.exists()) {
            throw new IOException("Source not exists.");
        }
        /* */
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(from);
            /* create destination file */
            out = new FileOutputStream(to);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            quiteClose(in);
            quiteClose(out);
        }
    }

    public static void copyFile(File from, File toDir) throws IOException {
        if (!toDir.isDirectory() || !from.exists()) {
            throw new IOException("Destination is no Directory or source not exists.");
        }
        /* */
        copy(from, new File(toDir, from.getName()));
    }

    public static String readFile(File file) throws IOException {
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fstream.read(bytes);
            fstream.close();
            return new String(bytes);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            quiteClose(fstream);
        }
    }
}
