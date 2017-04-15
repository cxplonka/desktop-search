/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.plugin;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ServiceState {

    private Throwable throwable;    

    public ServiceState() {
    }
    
    public ServiceState(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
    
    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isLoaded() {
        return throwable == null ? true : false;
    }
}
