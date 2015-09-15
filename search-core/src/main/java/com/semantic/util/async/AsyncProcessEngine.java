package com.semantic.util.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * http://martinzoldano.blogspot.de/2010/07/asynchronous-processing-via-annotation.html
 * http://www.java-forum.org/codeschnipsel-u-projekte/133866-asynccache-asynchron-nachladender-cache.html
 */
public class AsyncProcessEngine {

    private static final AsyncProcessEngine instance = new AsyncProcessEngine(); //eagerly
    private static final Logger log = Logger.getLogger(AsyncProcessEngine.class.getName());
    private final ConcurrentHashMap<String, Method> methodCache;
    private final ExecutorService execService;

    private AsyncProcessEngine() {
        execService = Executors.newCachedThreadPool();
        methodCache = new ConcurrentHashMap<String, Method>();
        log.info("-=[ AsyncProcessor singleton ]=-");
    }

    public static AsyncProcessEngine getInstance() {
        return instance;
    }

    public static void shutdown() {
        instance.methodCache.clear();
        instance.execService.shutdown();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    public <T> T createAsyncProxyProcess(final Object target, final Class<T> expectedType) {
        return expectedType.cast(
                Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {

                    @Override
                    public Object invoke(final Object proxy,
                            final Method method,
                            final Object[] args) throws Throwable {

                        final Method implMethod = target.getClass().
                                getMethod(method.getName(), method.getParameterTypes());

                        if (!implMethod.isAnnotationPresent(AsynchProcess.class)) {
                            return method.invoke(target, args); // filter
                        }

                        log.info(String.format(
                                "%s method introspection for: %s", target.toString(), implMethod.toGenericString()));

                        instance.execService.submit(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        Method onSuccess = null;
                                        Method onFailure = null;
                                        try {

                                            final AsynchProcess async = implMethod.getAnnotation(AsynchProcess.class);
                                            final String keySuccess = target.getClass().getName() + target.hashCode() + async.onSuccess();
                                            final String keyFailure = target.getClass().getName() + target.hashCode() + async.onFailure();

                                            if (!methodCache.containsKey(keySuccess) || !methodCache.containsKey(keyFailure)) {
                                                try {
                                                    onSuccess = target.getClass().getMethod(async.onSuccess(), new Class<?>[]{Object.class});
                                                    onFailure = target.getClass().getMethod(async.onFailure(), new Class<?>[]{Throwable.class});
                                                } catch (final Exception e) {
                                                }
                                                if (onSuccess == null || onFailure == null) {
//                        			 log.severe(target.getClass().getName()+" MUST impl: {} + {}",async.onSuccess(),async.onFailure());
                                                    return;
                                                } else {
                                                    methodCache.put(keySuccess, onSuccess);
                                                    methodCache.put(keyFailure, onFailure);
//                        			 log.debug("async methods cached: {} {}" , keySuccess , keyFailure );	                        				
                                                }
                                            } else {
                                                onSuccess = methodCache.get(keySuccess);
                                                onFailure = methodCache.get(keyFailure);
//                        		  log.debug("async methods retreived from cache: {} {}" , keySuccess , keyFailure );
                                            }

                                            //log.debug("found methods impl: {} + {}",onSuccess.toGenericString(),onFailure.toGenericString());
                                            //log.debug("Thread.name {} id {} ",Thread.currentThread().getName(),Thread.currentThread().getId());

                                            ////////////////////////////////////////////////////////////////                                
                                            try {
                                                final long start = System.currentTimeMillis();
                                                ////////////////////////////////////////////////////////////
                                                //invoke target method and pass ret val into success usrImpl
                                                onSuccess.invoke(target, method.invoke(target, args));
                                                ////////////////////////////////////////////////////////////                                	
//                                	log.debug("{}() [ tm.ms: {} ]",
//                                		method.getName() , (System.currentTimeMillis()-start));
                                            } catch (final Throwable t) {
                                                //////////////////////////////////////////
                                                onFailure.invoke(target, t.getCause());
                                                //////////////////////////////////////////									
                                            }
                                            ////////////////////////////////////////////////////////////////

                                        } catch (final Throwable e) {
//								log.error("woha@run()",e);
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        return null;
                    }
                }));
    }
}
