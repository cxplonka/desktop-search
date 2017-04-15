/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import com.semantic.util.FileUtil;
import com.semantic.util.image.ImageUtil;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

/**
 * http://download.oracle.com/javase/1.4.2/docs/guide/imageio/spec/apps.fm3.html
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SubImageTest extends JFrame {

    private BufferedImage bi = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);

    public SubImageTest() throws HeadlessException {
        super("test");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        add(new JPanel() {

            @Override
            public void paint(Graphics grphcs) {
                super.paint(grphcs);
                grphcs.drawImage(bi, 0, 0, this);
            }
        });
        init();
    }

    private void init() {
        try {
            File file = new File("c:/test/test.jpg");

            ImageInfo i = Sanselan.getImageInfo(file);
//            System.out.println(i);

            System.out.println(Runtime.getRuntime().freeMemory());

            IImageMetadata metadata = Sanselan.getMetadata(file);
//            System.out.println(metadata);

            if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

                byte[] thumbDate = getEXIFThumbnailData(jpegMetadata.getExif());
                if (thumbDate != null) {
                    bi = ImageUtil.toBufferedImage(Toolkit.getDefaultToolkit().createImage(
                            thumbDate));
                }


                TiffImageMetadata exifMetadata = jpegMetadata.getExif();
                if (exifMetadata != null) {

                    TiffOutputSet outputSet = exifMetadata.getOutputSet();
                    if (outputSet != null) {
                        // check if field already EXISTS - if so remove
                        TiffOutputField imageHistoryPre = outputSet.findField(TiffConstants.EXIF_TAG_USER_COMMENT);
                        if (imageHistoryPre != null) {
                            outputSet.removeField(TiffConstants.EXIF_TAG_USER_COMMENT);
                        }
                        // add field
                        OutputStream ostream = null;
                        try {
                            String fieldData = "some comments for my field metadata christian plonka michael r "
                                    + "alvers lili sigfried oleg heinrich berta vogel";
                            TiffOutputField imageHistory = new TiffOutputField(
                                    ExifTagConstants.EXIF_TAG_USER_COMMENT,
                                    TiffFieldTypeConstants.FIELD_TYPE_ASCII,
                                    fieldData.length(),
                                    fieldData.getBytes());
                            TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
                            exifDirectory.add(imageHistory);

                            File o = new File("c:/test/test1.jpg");
                            
                            ostream = new FileOutputStream(o);
                            ostream = new BufferedOutputStream(ostream);

                            new ExifRewriter().updateExifMetadataLossless(file,
                                    ostream, outputSet);
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            FileUtil.quiteClose(ostream);
                        }

                    }

//                    try {
//                        TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
//                        if (null != gpsInfo) {
//                            double longitude = gpsInfo.getLongitudeAsDegreesEast();
//                            double latitude = gpsInfo.getLatitudeAsDegreesNorth();
//                            System.out.println("    "
//                                    + "GPS Description: " + gpsInfo);
//                            System.out.println("    "
//                                    + "GPS Longitude (Degrees East): "
//                                    + longitude);
//                            System.out.println("    "
//                                    + "GPS Latitude (Degrees North): "
//                                    + latitude);
//                        }
//                    } catch (ImageReadException e) {
//                        e.printStackTrace();
//                    }
                }

            }

            System.out.println(Runtime.getRuntime().freeMemory());
        } catch (Exception ex) {
            Logger.getLogger(SubImageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] getEXIFThumbnailData(TiffImageMetadata exif) throws ImageReadException, IOException {
        ArrayList dirs = exif.getDirectories();
        for (int i = 0; i < dirs.size(); i++) {
            TiffImageMetadata.Directory dir = (TiffImageMetadata.Directory) dirs.get(i);

            byte[] data = null;
            if (dir.getJpegImageData() != null) {
                data = dir.getJpegImageData().data;
            }
            // Support other image formats here.

            if (data != null) {
                return data;
            }
        }
        return null;
    }
    
    public static void print(String file){
        try {
            IImageMetadata metadata = Sanselan.getMetadata(new File(file));
            
            for(Object item : metadata.getItems()){
                System.out.println(item);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(SubImageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] arg) {
        print("c:/1.tif");
//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                SubImageTest test = new SubImageTest();
//                test.setVisible(true);
//            }
//        });
    }
}