/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.lazy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DefaultLazyList<T> implements LazyList<T> {

    private static final int PENDING_TIME = 250;
    /* small cache for loaded elements */
    private Map<Integer, T> indexCache = Collections.synchronizedMap(new HashMap<Integer, T>());
    /* data provider */
    private final LazyListService<T> lazyListService;
    /* current page size for fetching elements */
    private final int pageSize;
    private final boolean fetchLastPageOnly = true;
    /* */
    private transient EventListenerList listeners;
    private final List<IndexRange> pendingRequests;
    private final PendingRequestHandler pendingRequestsHandler;
    private IndexRange currentRequest;

    public DefaultLazyList(LazyListService<T> lazyListService, int pageSize) {
        this.lazyListService = lazyListService;
        this.pageSize = pageSize;
        /* start request handler for fetching asynchronous data */
        pendingRequests = Collections.synchronizedList(new ArrayList<IndexRange>());
        pendingRequestsHandler = new PendingRequestHandler();
        pendingRequestsHandler.start();
    }

    @Override
    public void addOnLoadListener(OnLoadListener listener) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(OnLoadListener.class, listener);
    }

    @Override
    public void removeOnLoadListener(OnLoadListener listener) {
        if (listeners != null) {
            listeners.remove(OnLoadListener.class, listener);
        }
    }

    protected void fireChanged(OnLoadEvent event) {
        if (listeners != null) {
            for (OnLoadListener l : listeners.getListeners(OnLoadListener.class)) {
                l.elementLoaded(event);
            }
        }
    }

    @Override
    public boolean isLoaded(int index) {
        return indexCache.containsKey(index);
    }

    @Override
    public void getAsynchronous(int index) {
        /* already in cache!? */
        if (!isLoaded(index)) {
            if (!isRequested(index)) {
                pendingRequests.add(getStartEndFromIndex(index));
            }
            synchronized (pendingRequestsHandler) {
                pendingRequestsHandler.notify();
            }
        }
    }

    protected boolean isRequested(int index) {
        return isInPendingRequests(index) || (currentRequest != null && currentRequest.contains(index));
    }

    protected boolean isInPendingRequests(int index) {
        synchronized (pendingRequests) {
            for (IndexRange startEnd : pendingRequests) {
                if (startEnd.contains(index)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected IndexRange getStartEndFromPage(int page) {
        int startElement = page * pageSize;
        int endElement = Math.min(size(), startElement + pageSize);
        return new IndexRange(startElement, endElement);
    }

    protected int getPage(int index) {
        return index / pageSize;
    }

    protected IndexRange getStartEndFromIndex(int index) {
        return getStartEndFromPage(getPage(index));
    }

    @Override
    public void close() {
        pendingRequestsHandler.terminate();
        indexCache.clear();
    }

    @Override
    public int size() {
        return lazyListService.getSize();
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

    public void fetchData(IndexRange startEnd) {
        /* 
         * load data from the service in save into the cache, maybe clear cache after
         * an limit
         */
        T[] data = lazyListService.getData(startEnd.getStart(), startEnd.getEnd());
        for (int i = startEnd.getStart(); i < startEnd.getEnd() && data.length > 0; i++) {
            indexCache.put(i, data[i - startEnd.getStart()]);
        }
        fireChanged(new OnLoadEvent(this, startEnd));
    }

    @Override
    public T get(int index) {
        if (!isLoaded(index)) {
            fetchData(getStartEndFromIndex(index));
        }
        return (T) indexCache.get(index);
    }

    @Override
    public void clear() {
        pendingRequests.clear();
        currentRequest = null;
        indexCache.clear();
    }

    public class PendingRequestHandler extends Thread {

        private boolean terminate;

        public PendingRequestHandler() {
            terminate = false;
        }

        public synchronized void terminate() {
            terminate = true;
            notify();
        }

        @Override
        public void run() {
            while (!terminate) {
                while (!pendingRequests.isEmpty()) {
                    if (fetchLastPageOnly) {
                        currentRequest = pendingRequests.get(pendingRequests.size() - 1);
                        pendingRequests.clear();
                    } else {
                        currentRequest = pendingRequests.remove(0);
                    }
                    fetchData(currentRequest);
                    try {
                        /* wait a little bit, before next data (user can scroll very fast) */
                        Thread.sleep(PENDING_TIME);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}