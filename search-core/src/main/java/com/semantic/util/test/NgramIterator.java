/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class NgramIterator implements Iterator<String> {

    String str;
    int pos = 0, n, tmp, newpos;
    char[] ngram;

    public NgramIterator(int n, String str) {
        this.n = n;
        this.str = str;
        this.ngram = new char[n];
    }

    @Override
    public boolean hasNext() {
        tmp = pos;
        if (n + tmp >= str.length()) {
            return false;
        }
        /* search for ngram */
        for (int i = 0; i < n && i + tmp < str.length();) {
            char c = str.charAt(i + tmp);
            ngram[i] = ' ';
            if (!Character.isLetter(c)) {
                i = 0;
                tmp++;
            } else {
                ngram[i++] = c;
            }
        }
        newpos = tmp;
        return !contains(ngram, ' ');
    }

    private boolean contains(char[] array, char c) {
        for (char cc : array) {
            if (cc == c) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String next() {
        /* current postion not valid from check */
        if (newpos != pos) {
            pos = newpos;
        }
        /* next postion */
        pos++;
        return new String(ngram);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        String txt = "the Three-dimensional (3D) interactive modeling with the IGMAS ".toLowerCase();

        Map<String, Integer> occurrence = new HashMap<String, Integer>();

        for (Iterator<String> it = new NgramIterator(3, txt); it.hasNext();) {
            String ngram = it.next();
//            System.out.println(ngram);
            int count = 0;
            if (occurrence.containsKey(ngram)) {
                count = occurrence.get(ngram);
            }
            occurrence.put(ngram, ++count);
        }

        for (Map.Entry<String, Integer> entry : occurrence.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}