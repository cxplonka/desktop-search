/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.list;

import com.semantic.ApplicationContext;
import com.semantic.lucene.IndexManager;
import com.semantic.lucene.fields.ContentField;
import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.swing.UIDefaults;
import static com.semantic.swing.grid.DefaultDocumentGridCellRenderer.MIMETYPE;
import com.semantic.thumbnail.ThumbnailManager;
import com.semantic.util.AffineUtils;
import com.semantic.util.FileUtil;
import com.semantic.util.image.ImageUtil;
import com.semantic.util.image.TextureManager;
import com.semantic.util.test.topterms.TermHightlight;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import javax.swing.*;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DefaultListDocumentRenderer extends JLabel implements ListCellRenderer<Document> {

    private BufferedImage image;
    private final BufferedImage loading;
    private boolean isSelected = false;
    private final AffineTransform imageTransform = new AffineTransform();
    private final Rectangle2D from = new Rectangle2D.Double();
    private final Rectangle2D to = new Rectangle2D.Double(5, 5, 60, 60);
    private Document curDocument;
    private boolean labelSet = false;
    /* */
    private final Color selectionFrom = new Color(46, 92, 177);
    private final Color selectionTo = new Color(115, 169, 252);
    private final Color unSelectionFrom = UIManager.getColor(UIDefaults.LIST_UNSELECTION_FROM);
    private final Color unSelectionTo = UIManager.getColor(UIDefaults.LIST_UNSELECTION_TO);
    private final Color lineColor = new Color(50, 52, 55, 200);

    public DefaultListDocumentRenderer() {
        super();
//        setVerticalAlignment(BOTTOM);
        setBorder(BorderFactory.createEmptyBorder(5, 70, 5, 5));
        loading = TextureManager.def().loadImage("128x128/loading.png");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Document doc, int index, boolean isSelected, boolean cellHasFocus) {
        /* data is fetching */
        this.isSelected = isSelected;
        this.curDocument = null;
        image = loading;
        /* handle document classes */
        if (doc != null) {
            this.curDocument = doc;
            /* check for image document */
            if (ImageUtil.isSupportedExt(doc.get(FileExtField.NAME))) {
                /* complete filename */
                String fileName = doc.get(FileNameField.NAME);
                /* try to get thumbnail image from texture cache, if not manager will create
                 * asynchron */
                File thumbFile = new File(ThumbnailManager.def().generateThumbName(new File(fileName)));
                if (thumbFile.exists() && thumbFile.canRead()) {
                    image = TextureManager.def().loadImage(thumbFile);
                }
            } else {
                /* no preview found, mime type */
                String type = MIMETYPE.get(doc.get(MimeTypeField.NAME));
                if (type == null) {
                    image = TextureManager.def().loadImage("128x128/mime_unknown.png");
                } else {
                    image = TextureManager.def().loadImage(type);
                }
            }
            /* fast workaround */
            String result = null;
            labelSet = false;
            setText(null);
            /* highlight query */
            ApplicationContext ctx = ApplicationContext.instance();
            try {
                /* needs to be cached - because every draw call perform this */
                if (curDocument.getField(ContentField.NAME) != null) {
                    result = TermHightlight.hightlight(
                            ctx.get(ApplicationContext.QUERY_MANAGER).getCurrentQuery(),
                            ContentField.NAME,
                            curDocument.get(ContentField.NAME),
                            ctx.get(IndexManager.LUCENE_MANAGER).getIndexWriter().getAnalyzer());
                }
            } catch (Exception ex) {
            } finally {
                if (result != null) {
                    labelSet = true;
                    setText(result);
                }
            }
        }
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (isSelected) {
            GradientPaint gradient = new GradientPaint(0, 0, selectionFrom, 0, getHeight(),
                    selectionTo, true);
            g2d.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(0, 0, unSelectionFrom, 0, getHeight(),
                    unSelectionTo, true);
            g2d.setPaint(gradient);
        }
        g2d.fillRect(0, 0, getWidth(), getHeight());
        /* scale down */
        if (image != null) {
            from.setRect(0, 0, image.getWidth(), image.getHeight());
            if (from.getWidth() < to.getWidth() && from.getHeight() < to.getHeight()) {
                AffineUtils.createCenterTransform(from, to, imageTransform);
            } else {
                AffineUtils.createTransformToFitBounds(from, to, true, imageTransform);
            }
            g2d.drawImage(image, imageTransform, this);
        }

        g2d.setColor(lineColor);
        g2d.drawLine(0, 0, getWidth(), 0);

        if (curDocument != null) {
            g2d.setFont(getFont().deriveFont(Font.BOLD));
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(getTitle(curDocument), 70, 20);
            if (!labelSet) {
                g2d.drawString(curDocument.get(MimeTypeField.NAME), 70, 34);
                if (curDocument.getField(LastModifiedField.NAME) != null) {
                    g2d.drawString(new Date(
                            curDocument.getField(LastModifiedField.NAME).numericValue()
                                    .longValue()).toString(), 70, 48);
                }
            }
        }
        /* */
        super.paintComponent(g);
    }

    public String getTitle(Document doc) {
        return FileUtil.getName(doc.get(FileNameField.NAME));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 70);
    }
}
