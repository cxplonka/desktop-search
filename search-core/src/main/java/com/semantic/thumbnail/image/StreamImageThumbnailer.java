/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.thumbnail.image;

import com.bric.image.pixel.GenericImageSinglePassIterator;
import com.semantic.thumbnail.Thumbnailer;
import com.semantic.util.image.ImageUtil;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.apache.sanselan.Sanselan;

/**
 * http://javagraphics.blogspot.com/2011/05/images-scaling-jpegs-and-pngs.html
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class StreamImageThumbnailer implements Thumbnailer {

    private Dimension maxSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    private ImageIOThumbnailer fallback = new ImageIOThumbnailer();

    @Override
    public void generateThumbnail(File input, File output, String mimeType) throws RuntimeException {
        try {
            BufferedImage subBuffer = null;
            /* try to find and extract embedded thumbnail in jpg files */
            if (mimeType.equals("image/jpeg")) {
                byte[] thumbData = ImageUtil.extractEXIFThumbnailData(Sanselan.getMetadata(input));
                /* */
                if (thumbData != null) {
                    subBuffer = ImageUtil.toBufferedImage(Toolkit.getDefaultToolkit().createImage(
                            thumbData));
                }
            }
            /* do generic generation for thumbnail */
            if (subBuffer == null) {
                subBuffer = GenericImageSinglePassIterator.createScaledImage(
                        input.toURI().toURL(), maxSize);
            }
            /* save thumbnail, if we have an alpha channel then save as png */
            ImageIO.write(subBuffer, ImageUtil.hasAlpha(subBuffer) ? "png" : "jpg", output);
        } catch (Exception e) {
            /* try imageio fallback */
            fallback.generateThumbnail(input, output, mimeType);
        }
    }

    @Override
    public String[] getMimeTypes() {
        return ImageIO.getReaderMIMETypes();
    }
}