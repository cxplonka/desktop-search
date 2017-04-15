/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DocumentPropertyView.java
 *
 * Created on 28.10.2011, 10:09:25
 */
package com.semantic.swing;

import com.semantic.ApplicationContext;
import com.semantic.lucene.IndexManager;
import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.lucene.handler.LuceneFileHandler;
import static com.semantic.lucene.handler.LuceneFileHandler.*;
import com.semantic.swing.grid.DefaultDocumentGridCellRenderer;
import com.semantic.swing.table.DocumentTableModel;
import com.semantic.util.AffineUtils;
import com.semantic.util.image.ImageUtil;
import com.semantic.util.image.TextureManager;
import com.semantic.util.swing.OptimizedShadowPathEffect;
import com.semantic.util.swing.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.apache.lucene.document.Document;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DocumentPropertyView extends javax.swing.JPanel {

    private static final Logger log = Logger.getLogger(DocumentPropertyView.class.getName());
//    private PreviewComponent _previewComponent = new PreviewComponent();
//    private SwingWorker _currentWorker;
    /* current selected documents */
    private Document[] documents;
    private static final TagInfo COMMENT = ExifTagConstants.EXIF_TAG_USER_COMMENT;

    /** Creates new form DocumentPropertyView */
    public DocumentPropertyView() {
        initComponents();
        initOwnComponents();
    }

    private void initOwnComponents() {
//        add(_previewComponent = new PreviewComponent(), BorderLayout.CENTER);        
        add(infoScroll, BorderLayout.CENTER);
        documentTable.setModel(new DocumentTableModel());
        /* info view */
        infoScroll.setBorder(UIManager.getBorder(UIDefaults.INFO_SCROLLPANE_BORDER));
        infoScroll.setMinimumSize(new Dimension(0, 0));
        keywordingBox.addBox("Keywording", keywordPanel);
        keywordingBox.addBox("GPS", gpsPanel);
        /* apply */
        keyWordArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        sync();
                        break;
                }
            }
        });
        setSelectedDocuments(null);
    }

    public JPanel getDummyPanel() {
        return dummyPanel;
    }

    public void setSelectedDocuments(Document[] documents) {
        keyWordArea.setText(null);
        latField.setText(null);
        lonField.setText(null);
        boolean enable = true;
        this.documents = documents;
        Document first = null;
        try {
            /* clear table view */
            if (documents != null && documents.length == 1) {
                first = documents[0];
                String mime = first.get(MimeTypeField.NAME);
                if (mime.equals("image/jpeg")) {
                    IImageMetadata metadata = Sanselan.getMetadata(
                            new File(first.get(FileNameField.NAME)));
                    /* user comment tag */
                    TiffField field = ImageUtil.readExifField(metadata, COMMENT);
                    if (field != null) {
                        keyWordArea.setText(field.getStringValue().trim());
                    }
                    /*gps directory */
                    if (metadata instanceof JpegImageMetadata) {
                        TiffImageMetadata exif = ((JpegImageMetadata) metadata).getExif();
                        if (exif != null) {
                            TiffImageMetadata.GPSInfo gps = exif.getGPS();
                            if (gps != null) {
                                latField.setText(Double.toString(gps.getLatitudeAsDegreesNorth()));
                                lonField.setText(Double.toString(gps.getLongitudeAsDegreesEast()));
                            }
                        }
                    }
                } else {
                    enable = false;
                }
                /* load first document for preview */
//                if (_currentWorker != null) {
//                    _currentWorker.cancel(true);
//                }
//                _currentWorker = new ImageLoadWorker(documents[0]);
//                _currentWorker.execute();
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "can not read metadata.", e);
            enable = false;
        } finally {
            ((DocumentTableModel) documentTable.getModel()).setDocument(first);
            SwingUtils.enable(boxPanel, enable);
        }
    }

    private void sync() {
        if (documents != null) {
            for (Document doc : documents) {
                File file = new File(doc.get(FileNameField.NAME));
                try {
                    String mime = doc.get(MimeTypeField.NAME);
                    if (mime.equals("image/jpeg")) {
                        IImageMetadata metadata = Sanselan.getMetadata(file);
                        TiffOutputSet outputSet = ImageUtil.updateMetadata(metadata, COMMENT, keyWordArea.getText());
                        /* gps info */
                        try {
                            if (!latField.getText().isEmpty() && !lonField.getText().isEmpty()) {
                                double lat = Double.parseDouble(latField.getText());
                                double lon = Double.parseDouble(lonField.getText());
                                outputSet.setGPSInDegrees(lon, lat);
                            }
                        } catch (Exception e) {
                            log.log(Level.WARNING, String.format("can not create gps tag for image[%s].",
                                    file), e);
                        }
                        ImageUtil.syncMetadata(file, file, outputSet);
                        /* reindex file */
                        IndexManager index = ApplicationContext.instance().get(
                                IndexManager.LUCENE_MANAGER);
                        index.entryModified(file);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARNING, String.format("can not create metadata for image[%s].",
                            file), ex);
                }
            }
            /* update table model */
            setSelectedDocuments(documents);
        }
    }

    class PreviewComponent extends JComponent {

        BufferedImage image;
        private final OptimizedShadowPathEffect _effect = new OptimizedShadowPathEffect();
        private final AffineTransform _transfrom = new AffineTransform();
        private final Rectangle2D _fromBounds = new Rectangle2D.Double();
        private final Rectangle2D _toBounds = new Rectangle2D.Double();
        private static final int INSET = 8;

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (image != null) {
                Graphics2D g2d = (Graphics2D) g;
                _fromBounds.setFrame(0, 0, image.getWidth(), image.getHeight());
                _toBounds.setFrame(INSET, INSET, getWidth() - 2 * INSET, getHeight() - 2 * INSET);
                AffineUtils.createTransformToFitBounds(_fromBounds, _toBounds, true, _transfrom);
                /* transfrom bounds into current transform context */
                _fromBounds.setFrame(new Path2D.Double(_fromBounds).createTransformedShape(
                        _transfrom).getBounds2D());

                _effect.imageShadow(g2d, _fromBounds);
                g2d.drawImage(image, _transfrom, null);
            }
        }
    }

    class ImageLoadWorker extends SwingWorker<BufferedImage, Void> {

        private final Document doc;

        public ImageLoadWorker(Document doc) {
            this.doc = doc;
        }

        @Override
        protected BufferedImage doInBackground() throws Exception {
            BufferedImage bi;
            /* load current document */
            File file = new File(doc.get(FileNameField.NAME));
            /* check for image document */
            if (ImageUtil.isSupportedExt(doc.get(FileExtField.NAME))) {
                /* load complete image */
                bi = ImageIO.read(file);
            } else {
                /* no preview found, mime type */
                String type = DefaultDocumentGridCellRenderer.MIMETYPE.get(doc.get(
                        MimeTypeField.NAME));
                if (type == null) {
                    bi = TextureManager.def().loadImage("128x128/mime_unknown.png");
                } else {
                    bi = TextureManager.def().loadImage(type);
                }
            }
            fileLabel.setText(file.getName());
            return bi;
        }

        @Override
        protected void done() {
            try {
                super.done();
//                _previewComponent.image = get();
//                _previewComponent.repaint();
            } catch (Exception e) {
            }
        }
    }

    /** This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        keywordPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        keyWordArea = new javax.swing.JTextArea();
        gpsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        latField = new javax.swing.JTextField();
        lonField = new javax.swing.JTextField();
        infoScroll = new javax.swing.JScrollPane();
        documentTable = new javax.swing.JTable();
        dummyPanel = new javax.swing.JPanel();
        fileLabel = new javax.swing.JLabel();
        boxPanel = new javax.swing.JPanel();
        keywordingBox = new com.semantic.util.swing.JStackedBox();

        keywordPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 5));

        jLabel1.setText("Keyword Tags:");

        keyWordArea.setColumns(20);
        keyWordArea.setRows(5);
        jScrollPane1.setViewportView(keyWordArea);

        javax.swing.GroupLayout keywordPanelLayout = new javax.swing.GroupLayout(keywordPanel);
        keywordPanel.setLayout(keywordPanelLayout);
        keywordPanelLayout.setHorizontalGroup(
            keywordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(keywordPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(keywordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        keywordPanelLayout.setVerticalGroup(
            keywordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(keywordPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gpsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 5));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("lat:");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("lon:");

        javax.swing.GroupLayout gpsPanelLayout = new javax.swing.GroupLayout(gpsPanel);
        gpsPanel.setLayout(gpsPanelLayout);
        gpsPanelLayout.setHorizontalGroup(
            gpsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gpsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gpsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(gpsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(latField, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                    .addComponent(lonField, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                .addContainerGap())
        );
        gpsPanelLayout.setVerticalGroup(
            gpsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gpsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gpsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(latField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(gpsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lonField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        infoScroll.setBorder(null);
        infoScroll.setMinimumSize(new java.awt.Dimension(0, 0));
        infoScroll.setViewportView(documentTable);

        setLayout(new java.awt.BorderLayout());

        dummyPanel.setLayout(new java.awt.BorderLayout());

        fileLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        fileLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dummyPanel.add(fileLabel, java.awt.BorderLayout.CENTER);

        add(dummyPanel, java.awt.BorderLayout.NORTH);

        boxPanel.setLayout(new java.awt.BorderLayout());
        boxPanel.add(keywordingBox, java.awt.BorderLayout.CENTER);

        add(boxPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel boxPanel;
    private javax.swing.JTable documentTable;
    private javax.swing.JPanel dummyPanel;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JPanel gpsPanel;
    private javax.swing.JScrollPane infoScroll;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea keyWordArea;
    private javax.swing.JPanel keywordPanel;
    private com.semantic.util.swing.JStackedBox keywordingBox;
    private javax.swing.JTextField latField;
    private javax.swing.JTextField lonField;
    // End of variables declaration//GEN-END:variables
}