/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.image;

import com.semantic.util.SoftHashMap;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * softreference texture cache.
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class TextureManager {

    private static final Logger log = Logger.getLogger(TextureManager.class.getName());
    /* default icon */
    public static final String DEFAULT_ICON = "default_image.png";
    protected final BufferedImage defaultImage;
    private final String base;
    /*  
     * simple map with weak reference to the images, we should use
     * softreference's
     */
    private Map<String, BufferedImage> imageCache;
    private static TextureManager singleton;

    public static TextureManager def() {
        if (singleton == null) {
            singleton = new TextureManager("/com/semantic/resource/");
        }
        return singleton;
    }

    public TextureManager(String base) {
        super();
        this.base = base;
        /* create cache */
        if (imageCache == null) {
            imageCache = Collections.synchronizedMap(
                    new SoftHashMap<String, BufferedImage>(10000));
        }
        /* load default image */
        defaultImage = loadImage(DEFAULT_ICON);
    }

    public BufferedImage loadImage(String resourceAndBase) {
        BufferedImage ret = imageCache.get(resourceAndBase);
        if (ret == null) {
            ret = loadImage(resourceAndBase, TextureManager.class.getResource(
                    String.format("%s%s", base, resourceAndBase)));
        }
        return ret;
    }

    public BufferedImage loadImage(URL url) {
        return loadImage(url.toExternalForm(), url);
    }

    public BufferedImage loadImage(File file) {
        try {
            return loadImage(file.getAbsolutePath(), file.toURI().toURL());
        } catch (MalformedURLException ex) {
            log.log(Level.WARNING, "Failed to load texture.", ex);
        }
        return defaultImage;
    }

    public void removeImage(URL url) {
        removeImage(url.toExternalForm());
    }

    public void removeImage(BufferedImage image) {
        if (imageCache != null) {
            String key = null;
            for (Map.Entry<String, BufferedImage> entry : imageCache.entrySet()) {
                if (entry.getValue() != null && entry.getValue() == image) {
                    key = entry.getKey();
                    break;
                }
            }
            if (key != null) {
                removeImage(key);
            }
        }
    }

    /* push to a executor */
    public BufferedImage loadImage(String textureKey, URL url) {
        /* lookup for cached image */
        BufferedImage ret = imageCache.get(textureKey);
        if (ret == null) {
            log.fine(String.format("load and cache texture [%s]", url));
            try {
                imageCache.put(textureKey, ret = ImageIO.read(url));
                return ret;
            } catch (Exception ex) {
                log.log(Level.WARNING, String.format("Failed to load texture. [%s]",
                        url), ex);
            }
        }
        return ret == null ? defaultImage : ret;
    }

    public void removeImage(String textureKey) {
        if (imageCache != null) {
            BufferedImage value = imageCache.remove(textureKey);
            if (value != null) {
                log.fine(String.format("image removed from cache [%s]", textureKey));
            }
        }
    }
}