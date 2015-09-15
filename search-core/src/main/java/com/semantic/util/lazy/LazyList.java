/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.lazy;

import java.util.List;

/**
 * https://www.hicc.hs-heilbronn.de/de/Lazy_Loading_von_Tabellendaten_mit_Swing
 * @author Daniel Pfeifer
 * @param <T>
 */
public interface LazyList<T> extends List<T> {

    public void addOnLoadListener(OnLoadListener listener);

    public void removeOnLoadListener(OnLoadListener listener);

    public boolean isLoaded(int index);

    public void getAsynchronous(int index);

    public void close();
}
