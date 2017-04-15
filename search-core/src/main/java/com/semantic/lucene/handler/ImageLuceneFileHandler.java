/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.handler;

import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.lucene.fields.image.AspectRatioField;
import com.semantic.lucene.fields.image.BitsPerPixelField;
import com.semantic.lucene.fields.image.CommentField;
import com.semantic.lucene.fields.image.ExifDateField;
import com.semantic.lucene.fields.image.ExifMakeField;
import com.semantic.lucene.fields.image.ExifModelField;
import com.semantic.lucene.fields.image.ImageHeightField;
import com.semantic.lucene.fields.image.ImageWidthField;
import com.semantic.lucene.fields.image.LatField;
import com.semantic.lucene.fields.image.LonField;
import com.semantic.lucene.fields.image.PixelSizeField;
import com.semantic.util.DateUtil;
import com.semantic.util.image.ImageUtil;
import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;

/**
 * exif overview - http://www.media.mit.edu/pia/Research/deepview/exif.html
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ImageLuceneFileHandler extends LuceneFileHandler {

    private boolean classifyImage = false;
    /**
     * FIELD_RGB_CLASSIFICATION in red channel mean value (datatype - string)
     */
    public static final String COLORMEAN = "image_mean_color_channel_vector";

    @Override
    public Document handleDocument(IndexWriter indexWriter, IndexState state, File file) throws Exception {
        Document doc = super.handleDocument(indexWriter, state, file);
        /* create/update document */
        if (doc != null && file.length() > 0) {
            /* ultra fast header analysis, with fileinputstream - too many filedescripor
             * exeption on samba network */
            try {
                ImageInfo imageInfo = Sanselan.getImageInfo(file);
                /* default image meta informations */
                int w = imageInfo.getWidth();
                int h = imageInfo.getHeight();
                /* image width */
                get(ImageWidthField.NAME).add(doc, w);
                /* image height */
                get(ImageHeightField.NAME).add(doc, h);
                /* bits per pixel */
                get(BitsPerPixelField.NAME).add(doc, imageInfo.getBitsPerPixel());
                /* complete size */
                get(PixelSizeField.NAME).add(doc, w * h);
                /* image aspect ratio */
                get(AspectRatioField.NAME).add(doc, w / (float) h);
                /* try to extract metadata from jpeg files */
                String mime = imageInfo.getMimeType();
                /* override because they are different content types than extensions */
                doc.removeFields(MimeTypeField.NAME);
                get(MimeTypeField.NAME).add(doc, mime);
                /* special jpeg handling */
                if (mime != null && mime.equals("image/jpeg")) {
                    IImageMetadata metadata = Sanselan.getMetadata(file);
                    if (metadata instanceof JpegImageMetadata) {
                        JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                        /* create date of the image */
                        TiffField field = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_CREATE_DATE);
                        if (field != null) {
                            Date date = DateUtil.parseEXIFFormat(field.getStringValue());
                            /* else unknown format */
                            if (date != null) {
                                /* override file last modified date */
                                doc.removeFields(LastModifiedField.NAME);
                                get(LastModifiedField.NAME).add(doc, date.getTime());
                            }
                        }
                        /* user comment tag */
                        field = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_USER_COMMENT);
                        if (field != null) {
                            get(CommentField.NAME).add(doc, field.getStringValue().trim());
                        }
                        /* make tag */
                        field = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_MAKE);
                        if (field != null) {
                            get(ExifMakeField.NAME).add(doc, field.getStringValue().trim());
                        }
                        /* model tag */
                        field = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_MODEL);
                        if (field != null) {
                            get(ExifModelField.NAME).add(doc, field.getStringValue().trim());
                        }
                        /* date time tag */
                        field = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_CREATE_DATE);
                        if (field != null) {
                            Date date = DateUtil.parseEXIFFormat(field.getStringValue());
                            /* else unknown format */
                            if (date != null) {
                                get(ExifDateField.NAME).add(doc, date.getTime());
                            }
                        }
                        /* try to find gps informations */
                        TiffImageMetadata exifMetadata = jpegMetadata.getExif();
                        if (exifMetadata != null) {
                            try {
                                TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                                if (null != gpsInfo) {
                                    get(LatField.NAME).add(doc, gpsInfo.getLatitudeAsDegreesNorth());
                                    get(LonField.NAME).add(doc, gpsInfo.getLongitudeAsDegreesEast());
//                                    doc.add(new NumericField(FIELD_EXIF_GPS_IMG_DIRECTION, Field.Store.YES,
//                                    true).setIntValue(gps.getRational(
//                                    GpsDirectory.TAG_GPS_IMG_DIRECTION).intValue()));
//                                    System.out.println(exifMetadata.findField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION));
                                }
                            } catch (ImageReadException e) {
                            }
                        }
                    }
                }
                /* run some classification */
                if (classifyImage) {
                    int[] v = ImageUtil.analyzeRGB(file);
                    for (int i = 0; i < v.length; i++) {
                        doc.add(new IntPoint(COLORMEAN + i, v[i]));
                    }
                }
            } catch (Throwable e) {
                /* only log */
                log.log(Level.INFO, "can not extract image informations!", e);
            }
        }
        return doc;
    }

    public void setClassifyImage(boolean classifyImage) {
        this.classifyImage = classifyImage;
    }

    public boolean isClassifyImage() {
        return classifyImage;
    }

    @Override
    public String[] getFileExtensions() {
        return ImageIO.getReaderFormatNames();
    }
}
