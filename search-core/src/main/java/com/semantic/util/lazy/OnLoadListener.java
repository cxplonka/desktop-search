/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.lazy;

import java.util.EventListener;

/**
 *
 * @author Daniel Pfeifer
 */
public interface OnLoadListener extends EventListener {

    public void elementLoaded(OnLoadEvent event);
}