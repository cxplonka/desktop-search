/**
 * Copyright (c) 2007-2009, JAGaToo Project Group all rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the 'Xith3D Project Group' nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 */
package com.semantic.util.image;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

/**
 * This is a BufferedImage extension, that uses a DataBuffer, that stores its
 * data directly in a ByteBuffer.<br>
 * This allows for minimum memory usage, since no data is stored more than once.
 *
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class DirectBufferedImage extends BufferedImage {

    public enum Type {

        DIRECT_RGB,
        DIRECT_RGBA,
        DIRECT_TWO_BYTES,
        DIRECT_ONE_BYTE;
    }
    private final Type directType;
    private int numBytes;
    //private byte[] data;

    /*
    public void setDirectType( Type directType )
    {
    this.directType = directType;
    }
     */
    public final Type getDirectType() {
        return (directType);
    }

    public final int getNumBytes() {
        return (numBytes);
    }

    public final ByteBuffer getByteBuffer() {
        final DirectDataBufferByte dataBuffer = (DirectDataBufferByte) getRaster().getDataBuffer();
        return (dataBuffer.getByteBuffer());
    }

    private static class DirectWritableRaster extends WritableRaster {
        /*
        private final int scanLine;
        private final int bytesPerPixel;
        private final ByteBuffer bb;
         */

        private static SampleModel createSampleModel(int width, int height, int bytesPerPixel, int[] bandOffsets) {
            PixelInterleavedSampleModel csm =
                    new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
                    width, height,
                    bytesPerPixel,
                    width * bytesPerPixel,
                    bandOffsets);

            return (csm);
        }

        public DirectWritableRaster(int width, int height, int bytesPerPixel, int[] bandOffsets, DirectDataBufferByte dataBuffer) {
            super(createSampleModel(width, height, bytesPerPixel, bandOffsets), dataBuffer, new java.awt.Point(0, 0));

            /*
            this.scanLine = width * bytesPerPixel;
            this.bytesPerPixel = bytesPerPixel;
            this.bb = dataBuffer.getByteBuffer();
             */
        }
    }

    private DirectBufferedImage(Type type/*, byte[] buffer*/, ColorModel model, WritableRaster raster, boolean rasterPremultiplied) {
        super(model, raster, rasterPremultiplied, null);

        this.directType = type;

        this.numBytes = raster.getDataBuffer().getSize();
        //this.data = buffer;
    }

    private static int[] createBandOffsets(int bytesPerPixel) {
        int[] bandOffsets = new int[bytesPerPixel];
        for (int i = 0; i < bandOffsets.length; i++) {
            bandOffsets[i] = i;
        }
        return (bandOffsets);
    }

    private static int[] createNumBitsArray(int bytesPerPixel) {
        int[] numBits = new int[bytesPerPixel];
        for (int i = 0; i < numBits.length; i++) {
            numBits[i] = 8;
        }
        return (numBits);
    }

    /**
     * Creates a buffered image which is backed by a NIO byte buffer
     */
    public static DirectBufferedImage makeDirectImageRGBA(int width, int height, int[] bandOffsets, ByteBuffer bb) {
        final int pixelSize = bb.limit() * 8 / (width * height);
        final int bytesPerPixel = pixelSize / 8;

        //int[] bandOffsets = createBandOffsets( bytesPerPixel );
        if (bandOffsets == null) {
            bandOffsets = new int[]{3, 2, 1, 0};
        }

        // create the backing store
        DirectDataBufferByte buffer = new DirectDataBufferByte(bb);

        // build the raster with 4 bytes per pixel

        WritableRaster newRaster = new DirectWritableRaster(width, height, bytesPerPixel, bandOffsets, buffer);

        // create a standard sRGB color space
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        // create a color model which has three 8 bit values for RGB
        int[] nBits = createNumBitsArray(bytesPerPixel);
        ColorModel cm = new ComponentColorModel(cs, nBits, true, false, Transparency.TRANSLUCENT, 0);

        // create the buffered image

        DirectBufferedImage newImage = new DirectBufferedImage(Type.DIRECT_RGBA/*, bb*/, cm, newRaster, false);

        return (newImage);
    }

    /**
     * Creates a buffered image which is backed by a NIO byte buffer
     */
    public static DirectBufferedImage makeDirectImageRGBA(int width, int height, int pixelSize) {
        final int bytesPerPixel = pixelSize / 8;

        //int[] bandOffsets = createBandOffsets( bytesPerPixel );
        int[] bandOffsets = {3, 2, 1, 0};

        // create the backing store

        //byte[] bb = new byte[ width * height * bytesPerPixel ];

        // create a data buffer wrapping the byte array

        //DataBufferByte buffer = new DataBufferByte( width * height * bytesPerPixel );
        DirectDataBufferByte buffer = new DirectDataBufferByte(width * height * bytesPerPixel);

        // build the raster with 4 bytes per pixel

        //WritableRaster newRaster = java.awt.image.Raster.createInterleavedRaster( buffer, width, height, width * bytesPerPixel, bytesPerPixel, bandOffsets, null );
        //WritableRaster newRaster = java.awt.image.Raster.createInterleavedRaster( 0, width, height, width * bytesPerPixel, bytesPerPixel, bandOffsets, null );

        //WritableRaster newRaster = new ByteBufferInterleavedRaster( DirectWritableRaster.createSampleModel( width, height, bytesPerPixel, bandOffsets ), new java.awt.Point( 0, 0 ) );

        //SampleModel sm = new PixelInterleavedSampleModel( DataBuffer.TYPE_BYTE, width, height, bytesPerPixel, width * bytesPerPixel, new int[] { 0 } );
        //WritableRaster newRaster = Raster.createWritableRaster( sm, buffer, null );
        WritableRaster newRaster = new DirectWritableRaster(width, height, bytesPerPixel, bandOffsets, buffer);

        // create a standard sRGB color space
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        // create a color model which has three 8 bit values for RGB
        int[] nBits = createNumBitsArray(bytesPerPixel);
        ColorModel cm = new ComponentColorModel(cs, nBits, true, false, Transparency.TRANSLUCENT, 0);

        // create the buffered image

        DirectBufferedImage newImage = new DirectBufferedImage(Type.DIRECT_RGBA/*, bb*/, cm, newRaster, false);

        return (newImage);
    }

    /**
     * Creates a buffered image which is backed by a NIO byte buffer
     */
    public static DirectBufferedImage makeDirectImageRGBA(int width, int height) {
        return (makeDirectImageRGBA(width, height, 32));
    }

    /**
     * Creates a buffered image which is backed by a NIO byte buffer
     */
    public static DirectBufferedImage makeDirectImageRGB(int width, int height, int[] bandOffsets, ByteBuffer bb) {
        final int pixelSize = bb.limit() * 8 / (width * height);
        final int bytesPerPixel = pixelSize / 8;

        if (bandOffsets == null) {
            bandOffsets = createBandOffsets(bytesPerPixel);
        }

        // create the backing store

        // create a data buffer wrapping the byte array
        DirectDataBufferByte buffer = new DirectDataBufferByte(bb);

        // build the raster with 3 bytes per pixel
        WritableRaster newRaster = new DirectWritableRaster(width, height, bytesPerPixel, bandOffsets, buffer);

        // create a standard sRGB color space
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        // create a color model which has three 8 bit values for RGB
        int[] nBits = createNumBitsArray(bytesPerPixel);
        ColorModel cm = new ComponentColorModel(cs, nBits, false, false, Transparency.OPAQUE, 0);

        // create the buffered image
        DirectBufferedImage newImage = new DirectBufferedImage(Type.DIRECT_RGB/*, bb*/, cm, newRaster, false);

        return (newImage);
    }

    /**
     * Creates a buffered image which is backed by a NIO byte buffer
     */
    public static DirectBufferedImage makeDirectImageRGB(int width, int height, int pixelSize) {
        final int bytesPerPixel = pixelSize / 8;
        // create a standard sRGB color space
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        // create a color model which has three 8 bit values for RGB
        int[] nBits = createNumBitsArray(bytesPerPixel);
        ColorModel cm = new ComponentColorModel(cs, nBits, false, false, Transparency.OPAQUE, 0);

        return makeDirectImage(width, height, cm);
    }

    private static ColorModel defaultRGB() {
        final int bytesPerPixel = 24 / 8;
        // create a standard sRGB color space
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        // create a color model which has three 8 bit values for RGB
        int[] nBits = createNumBitsArray(bytesPerPixel);
        ColorModel cm = new ComponentColorModel(cs, nBits, false, false, Transparency.OPAQUE, 0);
        return cm;
    }

    public static DirectBufferedImage makeDirectImage(int width, int height, ColorModel cm) {
        final int bytesPerPixel = cm.getPixelSize() / 8;

        int[] bandOffsets = createBandOffsets(bytesPerPixel);
        // create a data buffer wrapping the byte array
        DirectDataBufferByte buffer = new DirectDataBufferByte(width * height * bytesPerPixel);
        // build the raster with 3 bytes per pixel
        WritableRaster newRaster = new DirectWritableRaster(width, height, bytesPerPixel, bandOffsets, buffer);

        // create the buffered image
        DirectBufferedImage newImage = null;
        if (cm.hasAlpha()) {
            newImage = new DirectBufferedImage(Type.DIRECT_RGB/*, bb*/, cm, newRaster, false);
        } else {
            newImage = new DirectBufferedImage(Type.DIRECT_RGBA/*, bb*/, cm, newRaster, false);
        }

        return newImage;
    }

    /**
     * Creates a buffered image which is backed by a NIO byte buffer
     */
    public static DirectBufferedImage makeDirectImageRGB(int width, int height) {
        return (makeDirectImageRGB(width, height, 24));
    }

    /**
     * Takes the source buffered image and converts it to a buffered image which
     * is backed by a direct byte buffer
     *
     * @param source
     * @return the DirectBufferedImage
     */
    public static DirectBufferedImage makeDirectImageRGB(BufferedImage source) {
        DirectBufferedImage dest = makeDirectImageRGB(source.getWidth(), source.getHeight());
        source.copyData(dest.getRaster());

        return (dest);
    }

    public static DirectBufferedImage makeDirectImageTwoBytes(int width, int height, int pixelSize) {
        final int bytesPerPixel = pixelSize / 8;

        int[] bandOffsets = createBandOffsets(bytesPerPixel);


        // create a data buffer wrapping the byte array
        DirectDataBufferByte buffer = new DirectDataBufferByte(width * height * bytesPerPixel);

        // build the raster with 2 bytes per pixel
        WritableRaster newRaster = new DirectWritableRaster(width, height, bytesPerPixel, bandOffsets, buffer);

        // create a standard sRGB color space
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY); // FIXME: We actually need two bytes!!!

        // create a color model which has two 8 bit values for luminance and alpha
        int[] nBits = createNumBitsArray(bytesPerPixel);
        ColorModel cm = new ComponentColorModel(cs, nBits, false, false, Transparency.OPAQUE, 0);

        // create the buffered image
        DirectBufferedImage newImage = new DirectBufferedImage(Type.DIRECT_ONE_BYTE/*, bb*/, cm, newRaster, false);

        return (newImage);
    }

    /**
     * Creates a buffered image which is backed by a NIO byte buffer
     */
    public static DirectBufferedImage makeDirectImageTwoBytes(int width, int height) {
        return (makeDirectImageRGBA(width, height, 16));
    }

    public static DirectBufferedImage makeDirectImageOneByte(int width, int height) {
        final int bytesPerPixel = 1;

        int[] bandOffsets = createBandOffsets(bytesPerPixel);

        // create the backing store
        //byte bb[] = new byte[ width * height * bytesPerPixel ];

        // create a data buffer wrapping the byte array
        //DataBuffer buffer = new DataBufferByte( bb, width * height * bytesPerPixel );
        DirectDataBufferByte buffer = new DirectDataBufferByte(width * height * bytesPerPixel);

        // build the raster with 1 byte per pixel
        //WritableRaster newRaster = Raster.createInterleavedRaster( buffer, width, height, width * bytesPerPixel,  bytesPerPixel, bandOffsets, null );
        WritableRaster newRaster = new DirectWritableRaster(width, height, bytesPerPixel, bandOffsets, buffer);

        // create a standard sRGB color space
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);

        // create a color model which has one 8 bit value for GRAY
        int[] nBits = createNumBitsArray(bytesPerPixel);
        ColorModel cm = new ComponentColorModel(cs, nBits, false, false, Transparency.OPAQUE, 0);

        // create the buffered image
        DirectBufferedImage newImage = new DirectBufferedImage(Type.DIRECT_ONE_BYTE/*, bb*/, cm, newRaster, false);

        return (newImage);
    }

    /**
     * takes the source buffered image and converts it to a buffered image which
     * is backed by a direct byte buffer
     *
     * @param source
     */
    public static DirectBufferedImage makeDirectImageRGBA(BufferedImage source) {
        DirectBufferedImage dest = makeDirectImageRGBA(source.getWidth(), source.getHeight());
        source.copyData(dest.getRaster());

        return (dest);
    }

    public static DirectBufferedImage make(Type type, int width, int height) {
        switch (type) {
            case DIRECT_RGBA:
                return (makeDirectImageRGBA(width, height));

            case DIRECT_RGB:
                return (makeDirectImageRGB(width, height));

            case DIRECT_TWO_BYTES:
                return (makeDirectImageTwoBytes(width, height));

            case DIRECT_ONE_BYTE:
                return (makeDirectImageOneByte(width, height));
        }

        throw new Error("Unknown direct image type " + type);
    }

    private static DirectBufferedImage convertViaDrawing(BufferedImage source, DirectBufferedImage dest) {
        Graphics2D g = (Graphics2D) dest.getGraphics();
        g.drawImage(source, 0, 0, dest.getWidth(), dest.getHeight(), null);

        return (dest);
    }

    public static DirectBufferedImage make(BufferedImage bi, boolean allowAlpha) {
        boolean hasAlpha = bi.getColorModel().hasAlpha() && !bi.getColorModel().isAlphaPremultiplied();

        if (hasAlpha && allowAlpha) {
            return (convertViaDrawing(bi, makeDirectImageRGBA(bi.getWidth(), bi.getHeight())));
        }

        return (convertViaDrawing(bi, makeDirectImageRGB(bi.getWidth(), bi.getHeight())));
    }

    public static DirectBufferedImage make(BufferedImage bi) {
        return (make(bi, true));
    }

    /**
     * reads in an image using image io. It then detects if this is a RGBA or
     * RGB image and converts it to the appropriate direct image. Unfortunly
     * this does mean we are loading a buffered image which is thrown away, but
     * there is no help for that currently.
     *
     * @param in
     * @parma allowAlpha
     *
     * @throws java.io.IOException
     */
    public static DirectBufferedImage loadDirectImage(InputStream in, boolean allowAlpha) throws IOException {
        /* not read indirect over ImageIO buffered image, much more performed */
        ImageInputStream imStream = ImageIO.createImageInputStream(in);
        Iterator<ImageReader> it_readers = ImageIO.getImageReaders(imStream);
        ImageReader reader = null;
        /* */
        try {
            /* no readers detected */
            if (!it_readers.hasNext()) {
                throw new IIOException("No Image reader found.");
            }
            /* take the first one */
            reader = (ImageReader) it_readers.next();
            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(imStream, true, true);
            /* check image type */
            int width = reader.getWidth(0);
            int height = reader.getHeight(0);
            ImageTypeSpecifier imageType = reader.getRawImageType(0);
            /* try to load colormodel */
            ColorModel cm = null;
            if (imageType != null) {
                cm = imageType.getColorModel();
            } else {
                cm = defaultRGB();
            }            
            /* create our native back buffered array */
            DirectBufferedImage backBuffer = makeDirectImage(width, height, cm);
            /* set reader backbuffer */
            param.setDestination(backBuffer);
            reader.read(0, param);

            return backBuffer;
        } finally {
            if (reader != null) {
                reader.dispose();
            }
            imStream.close();
        }
    }

    /**
     * reads in an image using image io. It then detects if this is a RGBA or
     * RGB image and converts it to the appropriate direct image. Unfortunly
     * this does mean we are loading a buffered image which is thrown away, but
     * there is no help for that currently.
     *
     * @param in
     *
     * @throws java.io.IOException
     */
    public static DirectBufferedImage loadDirectImage(InputStream in) throws IOException {
        return (loadDirectImage(in, true));
    }

    /**
     * reads in an image using image io. It then detects if this is a RGBA or
     * RGB image and converts it to the appropriate direct image. Unfortunly
     * this does mean we are loading a buffered image which is thrown away, but
     * there is no help for that currently.
     *
     * @param url
     * @param allowAlpha
     *
     * @throws java.io.IOException
     */
    public static DirectBufferedImage loadDirectImage(URL url, boolean allowAlpha) throws IOException {
        return (loadDirectImage(url.openStream(), allowAlpha));
    }

    public static BufferedImage loadDirectImage(URL url) throws java.io.IOException {
        return (loadDirectImage(url, true));
    }

    /**
     * reads in an image using image io. It then detects if this is a RGBA or
     * RGB image and converts it to the appropriate direct image. Unfortunly
     * this does mean we are loading a buffered image which is thrown away, but
     * there is no help for that currently.
     *
     * @param file
     * @param allowAlpha
     *
     * @throws java.io.IOException
     */
    public static DirectBufferedImage loadDirectImage(File file, boolean allowAlpha) throws IOException {
        return (loadDirectImage(new FileInputStream(file), allowAlpha));
    }

    /**
     * reads in an image using image io. It then detects if this is a RGBA or
     * RGB image and converts it to the appropriate direct image. Unfortunly
     * this does mean we are loading a buffered image which is thrown away, but
     * there is no help for that currently.
     *
     * @param file
     *
     * @throws java.io.IOException
     */
    public static DirectBufferedImage loadDirectImage(File file) throws IOException {
        return (loadDirectImage(file, true));
    }

    /**
     * reads in an image using image io. It then detects if this is a RGBA or
     * RGB image and converts it to the appropriate direct image. Unfortunly
     * this does mean we are loading a buffered image which is thrown away, but
     * there is no help for that currently.
     *
     * @param name
     *
     * @throws java.io.IOException
     */
    public static DirectBufferedImage loadDirectImage(String name, boolean allowAlpha) throws IOException {
        return (loadDirectImage(new File(name), allowAlpha));
    }

    public static DirectBufferedImage loadDirectImage(String name) throws IOException {
        return (loadDirectImage(name, true));
    }
}