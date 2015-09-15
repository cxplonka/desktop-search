/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.lazy;

import java.util.EventObject;

/**
 *
 * @author Daniel Pfeifer
 */
public class OnLoadEvent extends EventObject {

    private final IndexRange interval;

    public OnLoadEvent(Object source, IndexRange interval) {
        super(source);
        this.interval = interval;
    }

    public IndexRange getIndexInterval() {
        return interval;
    }
}
