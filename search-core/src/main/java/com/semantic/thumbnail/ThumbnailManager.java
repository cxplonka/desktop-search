/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.thumbnail;

import com.semantic.ApplicationContext;
import com.semantic.thumbnail.image.StreamImageThumbnailer;
import com.semantic.util.StringUtils;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class ThumbnailManager implements Runnable {

    protected static final Logger log = Logger.getLogger(ThumbnailManager.class.getName());
    /* fetch/create asynchronous workQueue */
    private Thread thumbThread;
    private File baseDir;
    /* the files for create a thumbnails */
    private BlockingQueue<File> workQueue = new LinkedBlockingQueue<File>();
    private BlockingQueue<File> resultQueue = new LinkedBlockingQueue<File>();
    private static final int MAX_RESULTS = 20;
    private boolean keepRunning = true;    
    private long sleepMilisecondOnEmpty = 20;
    /* */
    private Map<String, Thumbnailer> generator = new HashMap<String, Thumbnailer>();
    /* */
    private static final int CORES = Runtime.getRuntime().availableProcessors();    
    private ExecutorService executor = Executors.newFixedThreadPool(CORES);
    /* */
    private EventListenerList listeners;
    private static ThumbnailManager singleton;
    /* */
    private MessageDigest md5;

    public static ThumbnailManager def() {
        if (singleton == null) {
            singleton = new ThumbnailManager(new File(ApplicationContext.ISEARCH_HOME + "/.thumbnails"));
        }
        return singleton;
    }

    public ThumbnailManager(File baseDir) {
        this.baseDir = baseDir;
        init();
    }

    private void init() {
        try {
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            md5 = MessageDigest.getInstance("MD5");
            /* default image thumbnailer */
            registerThumbnailer(new StreamImageThumbnailer());
            start();
        } catch (NoSuchAlgorithmException ex) {
            log.log(Level.SEVERE, "can not start thumbnail manager!", ex);
        }
    }

    public void addThumbnailLoadListener(ThumbnailLoadListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(ThumbnailLoadListener.class, l);
    }

    public void removeThumbnailLoadListener(ThumbnailLoadListener l) {
        if (listeners == null) {
            listeners.remove(ThumbnailLoadListener.class, l);
        }
    }

    protected void fireChanged(ThumbnailLoadEvent event) {
        if (listeners != null) {
            for (ThumbnailLoadListener l : listeners.getListeners(ThumbnailLoadListener.class)) {
                l.thumbnailLoaded(event);
            }
        }
    }

    public synchronized void registerThumbnailer(Thumbnailer thumbnailer) {
        for (String type : thumbnailer.getMimeTypes()) {
            generator.put(type, thumbnailer);
        }
    }

    public synchronized void removeThumbnailer(Thumbnailer thumbnailer) {
        for (String type : thumbnailer.getMimeTypes()) {
            generator.remove(type);
        }
    }

    public void generateThumbnails(File... files) {
        workQueue.addAll(Arrays.asList(files));
    }

    public synchronized String generateThumbName(File file) {
        md5.reset();
        md5.update(file.getAbsolutePath().getBytes());
        return String.format("%s/%s.thumb", baseDir.getAbsoluteFile(),
                StringUtils.getHexString(md5.digest()));
    }

    public void start() {
        if (thumbThread == null) {
            thumbThread = new Thread(this, "ThumbnailGenerator");
            thumbThread.start();
            /* init some thumbnail tasks */
            for (int i = 0; i < CORES; i++) {                
                executor.submit(new ThumbnailerTask(this, workQueue, resultQueue));
            }
        }
    }    

    @Override
    public void run() {
        while (keepRunning) {
            try {
                /* fire away the results */
                if (resultQueue.size() > MAX_RESULTS || workQueue.isEmpty()) {
                    if (!resultQueue.isEmpty()) {
                        File[] files = resultQueue.toArray(new File[resultQueue.size()]);                        
                        resultQueue.clear();
                        fireChanged(new ThumbnailLoadEvent(this, files));
                    }
                }                
                /* Nothing in queue so lets wait */
                Thread.sleep(sleepMilisecondOnEmpty);
            } catch (Exception e) {
                log.log(Level.WARNING, "problem occur", e);
            }
        }        
    }

    public void stop() {
        this.keepRunning = false;
        /* shutdown */
        List<Runnable> runnables = executor.shutdownNow();
        for(Runnable runnable : runnables){
            if(runnable instanceof ThumbnailerTask){
                ((ThumbnailerTask)runnable).stop();
            }
        }
    }
}