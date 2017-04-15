/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.plugin;

import com.semantic.util.Disposable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SimpleLookup implements Disposable {

    private static final Logger log = Logger.getLogger(SimpleLookup.class.getName());
    /* save one plugin reference per class */
    private final Map<Class, Collection> services = new HashMap<Class, Collection>();
    private final Map<Class, ServiceState> serviceStates = new HashMap<Class, ServiceState>();
    /* simple wrapped classloader for detection not loaded classes */
    private final SimpleServiceClassLoader classLoader = new SimpleServiceClassLoader(
            Thread.currentThread().getContextClassLoader());
    
    public Map<Class, ServiceState> getServiceStates(){
        return Collections.unmodifiableMap(serviceStates);
    }
    
    public ServiceState getServiceState(Class clazz) {
        ServiceState ret = serviceStates.get(clazz);
        if (ret == null) {
            serviceStates.put(clazz, ret = new ServiceState());
        }
        return ret;
    }

    public void setServiceState(Class clazz, Throwable ex) {
        ServiceState ret = serviceStates.get(clazz);
        /* combine throwables */
        if (ret != null && !ret.isLoaded()) {
            ret.setThrowable(new Throwable(ex));
        } else {
            serviceStates.put(clazz, new ServiceState(ex));
        }        
    }

    /**
     * return the first service
     * @param <T>
     * @param clazz
     * @return
     */
    public <T> T instance(Class<T> clazz) {
        Collection ret = services.get(clazz);
        /* try to load all provider */
        if (ret == null) {
            ret = allInstances(clazz);
        }
        return (T) (ret != null && !ret.isEmpty() ? ((List) ret).get(0) : null);
    }

    /**
     * try to find the service, if not definition the try to load
     * the service and cache it in the lookup
     */
    public <T> Collection<T> allInstances(Class<T> clazz) {
        Collection ret = services.get(clazz);
        /* try to load service */
        if (ret == null) {
            ServiceLoader<T> ldr = ServiceLoader.load(clazz, classLoader);
            services.put(clazz, ret = new ArrayList());
            /* */
            Iterator<T> it = ldr.iterator();
            while (it.hasNext()) {
                T provider;
                try {
                    provider = it.next();
                } catch (Throwable e) {
                    /* try to cache the reason for fail of loading, can be version problems */
                    try {
                        setServiceState(classLoader.loadClass(classLoader.currentClassName), e);
                    } catch (ClassNotFoundException ex) {
                    }
                    log.log(Level.INFO, "Can not load Service!", e);
                    continue;
                }
                /* cache the provider allInstances */
                Collection<T> service = services.get(provider.getClass());
                if (service == null) {
                    /* service in special category */
                    services.put(provider.getClass(), service = new ArrayList<T>());
                }
                /* special service category */
                service.add(provider);
                /* register the service in the common category */
                ret.add(provider);
                /* update service state to ok */
                serviceStates.put(provider.getClass(), new ServiceState()); 
                /* */
                log.info(String.format("load and cache service - %s", provider.getClass()));
            }
        }
        return ret;
    }

    @Override
    public void dispose() {
        services.clear();
        serviceStates.clear();
    }

    class SimpleServiceClassLoader extends ClassLoader {

        String currentClassName;

        public SimpleServiceClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return super.loadClass(currentClassName = name);
        }
    }
}