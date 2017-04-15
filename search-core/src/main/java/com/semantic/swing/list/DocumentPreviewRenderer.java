/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.list;

import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.thumbnail.ThumbnailManager;
import com.semantic.util.AffineUtils;
import com.semantic.util.image.ImageUtil;
import com.semantic.util.image.TextureManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DocumentPreviewRenderer extends JComponent implements ListCellRenderer {

    private BufferedImage image;
    private BufferedImage loading;
    private boolean isSelected = false;
    private AffineTransform imageTransform = new AffineTransform();
    private Rectangle2D from = new Rectangle2D.Double();
    private Rectangle2D to = new Rectangle2D.Double(5, 5, 60, 60);    
    /* */
    private Color selectionFrom = new Color(46, 92, 177);
    private Color selectionTo = new Color(115, 169, 252);
    private int _gap = 3;
    private Dimension prefered;

    public DocumentPreviewRenderer(Dimension prefered) {
        super();
        setLayout(new BorderLayout());
        setBackground(new JPanel().getBackground());
        loading = TextureManager.def().loadImage("128x128/loading.png");
        to.setRect(_gap, _gap, prefered.width - 2 * _gap, prefered.height - 2 * _gap);
        this.prefered = new Dimension(prefered);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        /* data is fetching */
        this.isSelected = isSelected;        
        image = loading;
        /* handle document classes */
        if (value instanceof Document) {
            Document doc = (Document) value;            
            /* check for image document */
            if (ImageUtil.isSupportedExt(doc.get(FileExtField.NAME))) {
                /* complete filename */
                String fileName = doc.get(FileNameField.NAME);
                /* try to get thumbnail image from texture cache, if not manager will create asynchron */
                File thumbFile = new File(ThumbnailManager.def().generateThumbName(new File(fileName)));
                if (thumbFile.exists() && thumbFile.canRead()) {
                    image = TextureManager.def().loadImage(thumbFile);
                }
            } else {
                /* no preview found, mime type */
                image = TextureManager.def().loadImage("128x128/mime_unknown.png");
            }
        }
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        /* */
        Graphics2D g2 = (Graphics2D) g;
        if (isSelected) {
            GradientPaint gradient = new GradientPaint(0, 0, selectionFrom, 0, getHeight(),
                    selectionTo, true);
            g2.setPaint(gradient);
        } else {            
            g2.setPaint(getBackground());
        }
        g2.fillRect(0, 0, getWidth(), getHeight());
        /* scale down */
        from.setRect(0, 0, image.getWidth(), image.getHeight());
        if (from.getWidth() < to.getWidth() && from.getHeight() < to.getHeight()) {
            AffineUtils.createCenterTransform(from, to, imageTransform);
        } else {
            AffineUtils.createTransformToFitBounds(from, to, true, imageTransform);
        }
        g2.drawImage(image, imageTransform, this);
    }

    @Override
    public Dimension getPreferredSize() {
        return prefered;
    }
}