/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.thumbnail.image;

import com.semantic.thumbnail.Thumbnailer;
import com.semantic.util.AffineUtils;
import com.semantic.util.image.ImageUtil;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * 
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ImageIOThumbnailer implements Thumbnailer {

    private Point2D size = new Point2D.Double();
    private Rectangle2D from = new Rectangle2D.Double();
    private Rectangle2D to = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    private AffineTransform imageTransform = new AffineTransform();

    @Override
    public void generateThumbnail(File input, File output, String mimeType) throws RuntimeException {
        /* read image into direct backed buffer */        
        try {
            BufferedImage img = ImageIO.read(input);
            /* create transformation matrix */
            from.setRect(0, 0, img.getWidth(), img.getHeight());
            /* only copy if smaller */
            if (from.getWidth() < DEFAULT_WIDTH && from.getHeight() < DEFAULT_HEIGHT) {
                ImageIO.write(img, ImageUtil.hasAlpha(img) ? "png" : "jpg", output);
            } else {
                AffineUtils.createScaleTransfrom(from, to, true, imageTransform);
                /* transform source bounds and save only the real image part, save space */
                size.setLocation(from.getMaxX(), from.getMaxY());
                imageTransform.transform(size, size);
                /* create image subbuffer */
                BufferedImage subBuffer = new BufferedImage(
                        (int) size.getX(), (int) size.getY(), BufferedImage.TYPE_3BYTE_BGR);
                /* transform into thumbnail buffer */
                Graphics2D g2d = subBuffer.createGraphics();
                try {
                    /* smooth interpolation, scale more then 50% = bad quality */
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(img, imageTransform, null);
                } finally {
                    g2d.dispose();
                }
                /* save thumbnail, if we have an alpha channel then save as png */
                ImageIO.write(subBuffer, ImageUtil.hasAlpha(subBuffer) ? "png" : "jpg", output);
            }
        } catch (Exception e) {            
            throw new RuntimeException("can not generate thumbnail!", e);
        }
    }

    @Override
    public String[] getMimeTypes() {
        return ImageIO.getReaderMIMETypes();
    }
}