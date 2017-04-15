/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <K>
 * @param <V>
 */
public class MultiMap<K, V> {

    private final Map<K, Collection<V>> map;
    private int valueCount;

    public MultiMap() {
        map = new HashMap<K, Collection<V>>();
        valueCount = 0;
    }

    public Collection<V> get(K key) {
        Collection<V> values = map.get(key);
        if (values == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableCollection(map.get(key));
    }

    public int size() {
        return valueCount();
    }

    public int keyCount() {
        return map.size();
    }

    public int valueCount() {
        return valueCount;
    }

    public void put(K key, V value) {
        if (key != null) {
            Collection<V> values = map.get(key);
            if (values == null) {
                values = new ArrayList<V>();
                map.put(key, values);
            }
            if (values.add(value)) {
                valueCount++;
            }
        }
    }

    public Collection<V> remove(K key) {
        final Collection<V> removed = map.remove(key);
        if (removed == null) {
            return null;
        }
        valueCount -= removed.size();
        return Collections.unmodifiableCollection(removed);
    }

    public boolean remove(K key, V value) {
        final Collection<V> values = map.get(key);
        if (values == null) {
            return false;
        }
        if (!values.remove(value)) {
            return false;
        }
        if (values.isEmpty()) {
            map.remove(key);
        }
        valueCount--;
        return true;
    }

    public void clear() {
        map.clear();
        valueCount = 0;
    }

    public boolean isEmpty() {
        return valueCount == 0;
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(V value) {
        for (Collection<V> values : map.values()) {
            if (values.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        final Collection<V> allValues = new ArrayList<V>(valueCount());
        for (Collection<V> values : map.values()) {
            allValues.addAll(values);
        }
        return Collections.unmodifiableCollection(allValues);
    }

    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return map.entrySet();
    }
}
