/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.grid;

import java.awt.Stroke;
import com.semantic.util.image.ImageUtil;
import java.awt.image.BufferedImage;
import com.guigarage.jgrid.JGrid;
import com.guigarage.jgrid.renderer.GridCellRenderer;
import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.MimeTypeField;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import com.semantic.thumbnail.ThumbnailManager;
import com.semantic.util.AffineUtils;
import com.semantic.util.FileUtil;
import com.semantic.util.image.TextureManager;
import com.semantic.util.swing.OptimizedShadowPathEffect;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import org.apache.lucene.document.Document;
import static com.semantic.lucene.handler.ImageLuceneFileHandler.*;
import java.awt.RenderingHints;

/**
 * for bs based icons FileSystemView.getFileSystemView().getSystemIcon();
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DefaultDocumentGridCellRenderer extends JComponent implements GridCellRenderer {

    /**
     * mimetype - image resource mapping
     */
    public static final Map<String, String> MIMETYPE = new HashMap<String, String>();

    static {
        for (String type : convertMimeTypes(TEXT_MIME_TYPES)) {
            MIMETYPE.put(type, "128x128/mime_text.png");
        }
        for (String type : convertMimeTypes(IMAGE_MIME_TYPES)) {
            MIMETYPE.put(type, "128x128/mime_image.png");
        }
    }
    private boolean drawShadow = true;
    private boolean drawDescription = true;
    /* */
    private final AffineTransform imageTransform = new AffineTransform();
    private final Rectangle2D imageBounds = new Rectangle2D.Double();
    private final Rectangle2D uiBounds = new Rectangle2D.Double();
    private BufferedImage image;
    private final BufferedImage loading;
    /* shadow path effect */
    private final OptimizedShadowPathEffect effect = new OptimizedShadowPathEffect();
    private final JLabel desc = new JLabel();
    /* selected stuff */
    private boolean selected = false;
    private boolean applyShadow = true;
    private final Color selectionColor = new Color(46, 92, 177);
    private final Stroke selectionStroke = new BasicStroke(3f);
    private final Stroke smallStroke = new BasicStroke(0f);

    public DefaultDocumentGridCellRenderer() {
        super();
        loading = TextureManager.def().loadImage("128x128/loading.png");
        effect.setOffset(new Point2D.Double(2, 2));
        effect.setBrushColor(Color.GRAY);
        effect.setEffectWidth(14);
    }

    public void setDrawDescription(boolean drawDescription) {
        this.drawDescription = drawDescription;
    }

    public void setDrawShadow(boolean drawShadow) {
        this.drawShadow = drawShadow;
    }

    public boolean isDrawShadow() {
        return drawShadow;
    }

    public boolean isDrawDescription() {
        return drawDescription;
    }

    private void layoutLabel(JLabel label, int maxWidth) {
        FontMetrics metrics = label.getFontMetrics(label.getFont());
        int w = metrics.stringWidth(label.getText()) + 5;
        w = w > maxWidth ? maxWidth : w;
        label.setSize(w, metrics.getHeight());
    }

    @Override
    public Component getGridCellRendererComponent(JGrid grid, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        /* data is fetching */
        image = loading;
        this.selected = isSelected;
        /* handle document classes */
        if (value instanceof Document) {
            Document doc = (Document) value;
            /* complete filename */
            String fileName = doc.get(FileNameField.NAME);
            /* init description and layout */
            desc.setText(FileUtil.getName(fileName));
            layoutLabel(desc, grid.getFixedCellDimension());
            /* check for image document */
            if (ImageUtil.isSupportedExt(doc.get(FileExtField.NAME))) {
                applyShadow = true;
                /* try to get thumbnail image from texture cache, if not manager will create
                 * asynchron */
                File thumbFile = new File(ThumbnailManager.def().generateThumbName(new File(fileName)));
                if (thumbFile.exists() && thumbFile.canRead()) {
                    image = TextureManager.def().loadImage(thumbFile);
                }
            } else {
                applyShadow = false;
                /* no preview found, mime type */
                String type = MIMETYPE.get(doc.get(MimeTypeField.NAME));
                if (type == null) {
                    image = TextureManager.def().loadImage("128x128/mime_unknown.png");
                } else {
                    image = TextureManager.def().loadImage(type);
                }
            }
        }
        return this;
    }

    private void calculate(Rectangle2D bounds) {
        int tOffset = effect.getEffectWidth();
        tOffset += drawDescription ? desc.getHeight() : 0;
        bounds.setRect(getX() + effect.getEffectWidth() / 2,
                getY() + effect.getEffectWidth() / 2,
                getWidth() - effect.getEffectWidth(),
                getHeight() - tOffset);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            /* create transform, scale into component bounds - effect bounds */
            imageBounds.setRect(0, 0, image.getWidth(), image.getHeight());
            calculate(uiBounds);
            /* only center */
            if (imageBounds.getWidth() < uiBounds.getWidth() && imageBounds.getHeight() < uiBounds.getHeight()) {
                AffineUtils.createCenterTransform(imageBounds, uiBounds, imageTransform);
            } else {
                AffineUtils.createTransformToFitBounds(imageBounds, uiBounds, true, imageTransform);
            }
            /* apply path effect on image bounds */
            AffineUtils.transform(imageBounds, imageTransform);
            if (drawShadow && applyShadow) {
                effect.imageShadow(g2d, imageBounds);
            }
            /* draw transformed */
            g2d.drawImage(image, imageTransform, null);
            /* cell selected */
            if (selected) {
                g.setColor(selectionColor);
                g2d.setStroke(selectionStroke);
                g2d.draw(imageBounds);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(smallStroke);
                g2d.draw(imageBounds);
            }
            /* text layout */
            if (drawDescription) {
                int x = (int) (imageBounds.getCenterX() - desc.getWidth() / 2);
                int y = (int) imageBounds.getMaxY() + 3;
                g2d.setColor(Color.BLACK);
                /* scale and draw */
                g2d.translate(x, y);
                desc.paint(g);
                g2d.translate(-x, -y);
            }
        }
    }
}
