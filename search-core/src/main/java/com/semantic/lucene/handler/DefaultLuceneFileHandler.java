/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.handler;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DefaultLuceneFileHandler extends LuceneFileHandler {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"*"};
    }
}