/*
 * To change this template, choose Tools | Templates
 * and open the template file the editor.
 */
package com.semantic.thumbnail;

import com.semantic.lucene.handler.LuceneFileHandler;
import com.semantic.thumbnail.image.StreamImageThumbnailer;
import com.semantic.util.image.TextureManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ThumbnailerTask implements Runnable {

    private static final Logger log = Logger.getLogger(ThumbnailerTask.class.getName());
    /* */
    private boolean keepRunning = true;
    private static final long sleepMilisecondOnEmpty = 20;
    /* */
    private BlockingQueue<File> workQueue;
    private BlockingQueue<File> resultQueue;
    private BufferedImage errorThumb = TextureManager.def().loadImage("thumbnail_warning.png");
    private ThumbnailManager manager;
    /* */
    private Thumbnailer thumbnailer = new StreamImageThumbnailer();

    public ThumbnailerTask(ThumbnailManager manager, BlockingQueue<File> workQueue, BlockingQueue<File> resultQueue) {
        this.manager = manager;
        this.workQueue = workQueue;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        while (keepRunning) {
            File file = workQueue.poll();            
            try {
                /* null if nothing is in the queue */
                if (file != null) {                    
                    File out = new File(manager.generateThumbName(file));
                    if (!out.exists() && file.canRead()) {
                        /* warning lower case */
                        String type = LuceneFileHandler.MIME_MAP.getContentType(
                                file.getAbsolutePath().toLowerCase());
                        if (type.startsWith("image")) {
                            thumbnailer.generateThumbnail(file, out, type);
                            /* push to queue */
                            resultQueue.put(out);
                            /* */
                            log.info(String.format("thumbnail created for [%s]", file));
                        }
                    }
                } else {
                    /* Nothing in queue so lets wait */
                    Thread.sleep(sleepMilisecondOnEmpty);
                }
            } catch (Exception e) {
                try {
                    /* use default error image as thumb preview */
                    File out = new File(manager.generateThumbName(file));
                    ImageIO.write(errorThumb, "png", out);
                    resultQueue.put(out);
                } catch (Exception ex) {
                }
                /* */
                log.log(Level.WARNING, "problem occur", e);
            }
        }
    }

    public void stop() {
        this.keepRunning = false;
    }
}