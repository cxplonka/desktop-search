/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.image;

import com.semantic.util.FileUtil;
import com.semantic.util.swing.ColorUtil;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;
import org.apache.sanselan.util.IOUtils;

/**
 * http://download.oracle.com/javase/1.4.2/docs/guide/imageio/spec/apps.fm3.html
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ImageUtil {

    private static final Set<String> extMap = new HashSet<String>();

    static {
        /* current readers */
        for (String ext : ImageIO.getReaderFormatNames()) {
            extMap.add(ext.toLowerCase());
        }
    }

    public static boolean isSupportedExt(File file) {
        return isSupportedExt(FileUtil.getFileExtension(file));
    }

    public static boolean isSupportedExt(String ext) {
        if (ext != null) {
            return extMap.contains(ext.toLowerCase());
        }
        return false;
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

    public static int rgbMean(BufferedImage image) {
        int r = 0, g = 0, b = 0;
        int size = image.getWidth() * image.getHeight();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                r += ColorUtil.getRed(rgb);
                g += ColorUtil.getGreen(rgb);
                b += ColorUtil.getBlue(rgb);
            }
        }
        return ColorUtil.convert(0, r /= size, g /= size, b /= size);
    }

    public static int[] analyzeRGB(File file) throws IOException {
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(file);

            Iterator readers = ImageIO.getImageReaders(iis);
            ImageReader reader = (ImageReader) readers.next();
            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(iis, true);

            int[] ret = new int[4 * 3];
            /* */
            int imageIndex = 0;
            int w = reader.getWidth(imageIndex);
            int h = reader.getHeight(imageIndex);
            /* subsample 4% of the size */
            int subW = (int) (w * 0.04);
            int subH = (int) (h * 0.04);
            /* first region */
            Rectangle region = new Rectangle(0, 0, w / 2, h / 2);
            param.setSourceRegion(region);
            param.setSourceSubsampling(subW, subH, 0, 0);
            /* read first region(north west) and use the buffer for all other parts too */
            BufferedImage bi = reader.read(imageIndex, param);
            /* use same subbuffer for all other calls */
            param.setDestination(bi);
            int rgb = rgbMean(bi);
            ret[0] = ColorUtil.getRed(rgb);
            ret[1] = ColorUtil.getGreen(rgb);
            ret[2] = ColorUtil.getBlue(rgb);
            /* region north east */
            region.setBounds(w / 2, 0, w / 2, h / 2);
            param.setSourceRegion(region);
            reader.read(imageIndex, param);
            /* sum up */
            rgb = rgbMean(bi);
            ret[3] = ColorUtil.getRed(rgb);
            ret[4] = ColorUtil.getGreen(rgb);
            ret[5] = ColorUtil.getBlue(rgb);
            /* region south west */
            region.setBounds(0, h / 2, w / 2, h / 2);
            param.setSourceRegion(region);
            reader.read(imageIndex, param);
            /* sum up */
            rgb = rgbMean(bi);
            ret[6] = ColorUtil.getRed(rgb);
            ret[7] = ColorUtil.getGreen(rgb);
            ret[8] = ColorUtil.getBlue(rgb);
            /* region south east */
            region.setBounds(w / 2, h / 2, w / 2, h / 2);
            param.setSourceRegion(region);
            reader.read(imageIndex, param);
            /* sum up */
            rgb = rgbMean(bi);
            ret[9] = ColorUtil.getRed(rgb);
            ret[10] = ColorUtil.getGreen(rgb);
            ret[11] = ColorUtil.getBlue(rgb);

            return ret;
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (iis != null) {
                iis.close();
            }
        }
    }

    public static int analyzeRGBSubSampling(File file) throws IOException {
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(file);

            Iterator readers = ImageIO.getImageReaders(iis);
            ImageReader reader = (ImageReader) readers.next();
            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(iis, true);
            /* */
            int imageIndex = 0;
            int w = reader.getWidth(imageIndex);
            int h = reader.getHeight(imageIndex);
            /* use subsampling, every 4% */
            int subW = (int) (w * 0.04);
            int subH = (int) (h * 0.04);
            param.setSourceSubsampling(subW, subH, 0, 0);

            return rgbMean(reader.read(imageIndex, param));
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public static byte[] extractEXIFThumbnailData(IImageMetadata exif) throws ImageReadException, IOException {
        if (exif != null && exif instanceof JpegImageMetadata) {
            ArrayList dirs = ((JpegImageMetadata) exif).getExif().getDirectories();
            for (Object obj : dirs) {
                TiffImageMetadata.Directory dir = (TiffImageMetadata.Directory) obj;
                byte[] data = null;
                if (dir.getJpegImageData() != null) {
                    data = dir.getJpegImageData().data;
                }
                if (data != null) {
                    return data;
                }
            }
        }
        return null;
    }

    public static TiffField readExifField(IImageMetadata metadata, TagInfo tag) {
        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMeta = (JpegImageMetadata) metadata;
            return jpegMeta.findEXIFValue(tag);
        }
        return null;
    }

    public static TiffOutputSet updateMetadata(IImageMetadata metadata, TagInfo tag, String value) throws Exception {
        TiffOutputSet outputSet = null;
        if (metadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            TiffImageMetadata exifMetadata = jpegMetadata.getExif();
            if (exifMetadata != null) {
                outputSet = exifMetadata.getOutputSet();
                if (outputSet == null) {
                    outputSet = new TiffOutputSet();
                } else {
                    /* check if field already EXISTS - if so remove */
                    TiffOutputField field = outputSet.findField(tag);
                    if (field != null) {
                        outputSet.removeField(tag);
                    }
                }
            }
        } else {
            outputSet = new TiffOutputSet();
        }
        /* add field */
        TiffOutputField newField = new TiffOutputField(
                tag,
                TiffFieldTypeConstants.FIELD_TYPE_ASCII,
                value.length(),
                value.getBytes());
        TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
        exifDirectory.add(newField);
        return outputSet;
    }

    public static void syncMetadata(File source, File destination, TiffOutputSet outputSet) throws IOException {
        OutputStream ostream = null;
        try {
            if (source.equals(destination)) {
                /* first write to temp file and then copy */
                File tempFile = new File(System.getProperty("java.io.tmpdir") + "/sync.tmp");
                ostream = new FileOutputStream(tempFile);
                ostream = new BufferedOutputStream(ostream);
                new ExifRewriter().updateExifMetadataLossless(source, ostream, outputSet);
                /* override */
                IOUtils.copyFileNio(tempFile, destination);
                tempFile.delete();
            } else {
                ostream = new FileOutputStream(destination);
                ostream = new BufferedOutputStream(ostream);
                new ExifRewriter().updateExifMetadataLossless(source, ostream, outputSet);
            }
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            FileUtil.quiteClose(ostream);
        }
    }

    public static void writeJpeg(BufferedImage image, File destFile, float quality)
            throws IOException {
        ImageWriter writer = null;
        FileImageOutputStream output = null;
        try {
            writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            output = new FileImageOutputStream(destFile);
            writer.setOutput(output);
            IIOImage iioImage = new IIOImage(image, null, null);
            writer.write(null, iioImage, param);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (writer != null) {
                writer.dispose();
            }
            if (output != null) {
                output.close();
            }
        }
    }
}