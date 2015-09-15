/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.lazy;

/**
 *
 * @author Daniel Pfeifer
 */
public interface LazyListService<T> {

    public int getSize();

    public T[] getData(int startElement, int endElement);

    public void set(int position, T element);

    public void add(int position, T element);

    public void remove(int position);

    public void clear();
}
