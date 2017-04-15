/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.layerui;

import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.swing.grid.DefaultDocumentGridCellRenderer;
import com.semantic.swing.grid.LazyDocumentListModel;
import com.semantic.swing.list.DocumentPreviewRenderer;
import com.semantic.util.AffineUtils;
import com.semantic.util.image.ImageUtil;
import com.semantic.util.image.TextureManager;
import com.semantic.util.lazy.LazyList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.lucene.document.Document;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ImageLayerUI extends AbstractLayerUI<JComponent> {

    private JPanel _glassPane = new JPanel(new BorderLayout());
    private JList _previewList = new JList();
    private JScrollPane _scroll;
    /* */
    private boolean blocked = true;
    private BufferedImage image;
    private Rectangle2D uiBounds = new Rectangle2D.Double();
    private Rectangle2D imageBounds = new Rectangle2D.Double();
    private AffineTransform imageTransform = new AffineTransform();
    private AffineTransform uiTransform = new AffineTransform();
    /* ui stuff */
    private double minScale = 1;
    private double maxScale = 10;
    private boolean drag = false;
    private boolean rotate = false;
    private Point lastPoint = new Point();
    private JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(100, 100)) {
        @Override
        protected void frameChanged() {
            setDirty(true);
        }
    };
    private LazyList<Document> lazyList;
    private int currentDocument = 0;
    private SwingWorker currentWorker;
    /* button bounds */
    private Point tmp = new Point();
    private BufferedImage close;
    private BufferedImage left;
    private BufferedImage right;
    private BufferedImage prev;
    private BufferedImage next;
    private Rectangle closeRec = new Rectangle();
    private Rectangle leftRec = new Rectangle();
    private Rectangle rightRec = new Rectangle();
    private Rectangle prevRec = new Rectangle();
    private Rectangle nextRec = new Rectangle();

    public ImageLayerUI() {
        super();
        setEnabled(false);
        /* */
        _scroll = new JScrollPane(_previewList,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        _scroll.setBorder(null);
        _previewList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadDocument(_previewList.getSelectedIndex());
                }
            }
        });
        _previewList.setCellRenderer(new DocumentPreviewRenderer(new Dimension(80, 80)));
        _previewList.setVisibleRowCount(1);
        _previewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _previewList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        _scroll.setViewportView(_previewList);
        _glassPane.add(_scroll, BorderLayout.NORTH);
        _glassPane.setOpaque(false);
        /* */
        close = TextureManager.def().loadImage("cross-icon.png");
        left = TextureManager.def().loadImage("rotate_left.png");
        right = TextureManager.def().loadImage("rotate_right.png");
        prev = TextureManager.def().loadImage("prev_icon.png");
        next = TextureManager.def().loadImage("next_icon.png");
        /* */
        busyLabel.setSize(busyLabel.getPreferredSize());
    }

    @Override
    public void installUI(final JComponent c) {
        super.installUI(c);
        final JXLayer layer = (JXLayer) c;
        layer.setGlassPane(_glassPane);
    }

    @Override
    public void uninstallUI(final JComponent c) {
        super.uninstallUI(c);
        final JXLayer layer = (JXLayer) c;
        layer.setGlassPane(null);
    }

    @Override
    public void setEnabled(boolean bln) {
        super.setEnabled(bln);
        /* clear reference */
        if (!bln) {
            drag = false;
            rotate = false;
            image = null;
            currentWorker = null;
        }
    }

    public void setCurrentList(LazyList<Document> list, int index) {
        this.lazyList = list;
        _previewList.setModel(new LazyDocumentListModel(list));
        loadDocument(index);
    }

    @Override
    protected void paintLayer(Graphics2D gd, JXLayer<JComponent> jxl) {
        super.paintLayer(gd, jxl);
        /* */
        int ys = _scroll.getHeight();
        int w = jxl.getWidth();
        int h = jxl.getHeight() - ys;
        gd.setColor(new Color(0, 0, 0, 128));
        gd.fillRect(0, ys, w, h);
        gd.setClip(0, ys, w, h);
        /* */
        if (image != null) {
            gd.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            /* */
            uiBounds.setRect(w * 0.05, ys + h * 0.05,
                    w - 2 * w * 0.05,
                    h - 2 * h * 0.05);
            imageBounds.setRect(0, 0, image.getWidth(), image.getHeight());
            /* only center */
            if (imageBounds.getWidth() < uiBounds.getWidth() && imageBounds.getHeight() < uiBounds.getHeight()) {
                AffineUtils.createCenterTransform(imageBounds, uiBounds, imageTransform);
            } else {
                AffineUtils.createTransformToFitBounds(imageBounds, uiBounds, true, imageTransform);
            }
            imageTransform.concatenate(uiTransform);
            gd.drawImage(image, imageTransform, jxl);
        } else {
            double x = w / 2 - busyLabel.getWidth() / 2;
            double y = h / 2 - busyLabel.getHeight() / 2;
            y += ys;
            gd.translate(x, y);
            busyLabel.paint(gd);
            gd.translate(-x, -y);
        }
        /* close icon */
        closeRec.setRect(w - close.getWidth() - 5, 5 + ys, close.getWidth(), close.getHeight());
        gd.drawImage(close, null, closeRec.x, closeRec.y);
        /* rotate right icon */
        rightRec.setRect(w / 2 - right.getWidth(), h - right.getHeight() - 5 + ys, right.getWidth(), right.getHeight());
        gd.drawImage(right, null, rightRec.x, rightRec.y);
        /* rotate left icon */
        leftRec.setRect(w / 2, h - left.getHeight() - 5 + ys, left.getWidth(), left.getHeight());
        gd.drawImage(left, null, leftRec.x, leftRec.y);
        /* prev icon */
        prevRec.setRect(rightRec.getX() - prev.getWidth(), h - prev.getHeight() - 3 + ys,
                prev.getWidth(), prev.getHeight());
        gd.drawImage(prev, null, prevRec.x, prevRec.y);
        /* next icon */
        nextRec.setRect(leftRec.getMaxX(), h - next.getHeight() - 3 + ys,
                next.getWidth(), next.getHeight());
        gd.drawImage(next, null, nextRec.x, nextRec.y);
    }

    @Override
    protected void processKeyEvent(KeyEvent e, JXLayer<JComponent> l) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    setEnabled(false);
                    break;
                case KeyEvent.VK_LEFT:
                    loadDocument(currentDocument = currentDocument - 1 < 0
                            ? lazyList.size() - 1 : currentDocument - 1);
                    break;
                case KeyEvent.VK_RIGHT:
                    loadDocument(currentDocument = currentDocument + 1
                            >= lazyList.size() ? 0 : currentDocument + 1);
                    break;
            }
        }
        if (blocked) {
            e.consume();
        }
    }

    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e, JXLayer<JComponent> l) {
        /* transform into jxlayer coordinate system */
        Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);

        if (!_scroll.contains(point)) {
            double scaleDelta = (1.0 + (0.1 * -e.getWheelRotation()));
            double currentScale = uiTransform.getScaleX();
            double newScale = currentScale * scaleDelta;
            if (newScale < minScale) {
                scaleDelta = minScale / currentScale;
            }
            if (maxScale < 0 || newScale > maxScale) {
                scaleDelta = maxScale / currentScale;
            }

            try {
                /* transform into current image coordinate system */
                imageTransform.inverseTransform(point, point);
            } catch (NoninvertibleTransformException ex) {
            }
            /* scale about transformed image point */
            AffineUtils.scaleAboutPoint(scaleDelta, scaleDelta, point.getX(), point.getY(), uiTransform);
            setDirty(true);
            /* consume event */
            if (blocked) {
                e.consume();
            }
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JXLayer<JComponent> l) {
        try {
            Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
            if (!_scroll.contains(point)) {
                switch (e.getID()) {
                    case MouseEvent.MOUSE_DRAGGED:
                        /* transform into image coordinate system */
                        tmp.setLocation(point);
                        imageTransform.inverseTransform(tmp, tmp);
                        imageTransform.inverseTransform(lastPoint, lastPoint);
                        /* drag */
                        if (drag) {
                            int dx = (int) (tmp.x - lastPoint.x);
                            int dy = (int) (tmp.y - lastPoint.y);
                            uiTransform.translate(dx, dy);
                            setDirty(true);
                        }
                        /* rotate */
                        if (rotate) {
                            double cx = imageBounds.getCenterX();
                            double cy = imageBounds.getCenterY();
                            uiTransform.rotate(
                                    Math.atan2(tmp.y - cy, tmp.x - cx)
                                    - Math.atan2(lastPoint.y - cy, lastPoint.x - cx),
                                    cx, cy);
                            setDirty(true);
                        }
                        lastPoint.setLocation(point);
                        break;
                }
                if (blocked) {
                    e.consume();
                }
            }
        } catch (NoninvertibleTransformException ex) {
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JXLayer<JComponent> l) {
        Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
        lastPoint.setLocation(point);
        /* */
        switch (e.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                if (SwingUtilities.isLeftMouseButton(e)) {
                    drag = true;
                    /* close layerui */
                    if (closeRec.contains(point)) {
                        setEnabled(false);
                    }
                    /* rotate left */
                    if (leftRec.contains(point)) {
                        double cx = imageBounds.getCenterX();
                        double cy = imageBounds.getCenterY();
                        uiTransform.rotate(Math.toRadians(-90), cx, cy);
                        setDirty(true);
                    }
                    /* rotate right */
                    if (rightRec.contains(point)) {
                        double cx = imageBounds.getCenterX();
                        double cy = imageBounds.getCenterY();
                        uiTransform.rotate(Math.toRadians(90), cx, cy);
                        setDirty(true);
                    }
                    /* prev document */
                    if (prevRec.contains(point)) {
                        loadDocument(currentDocument = currentDocument - 1 < 0
                                ? lazyList.size() - 1 : currentDocument - 1);
                    }
                    /* next document */
                    if (nextRec.contains(point)) {
                        loadDocument(currentDocument = currentDocument + 1
                                >= lazyList.size() ? 0 : currentDocument + 1);
                    }
                }
                /* let's rotate a little bit */
                if (SwingUtilities.isRightMouseButton(e)) {
                    rotate = true;
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                drag = false;
                rotate = false;
                break;
        }
        /* consume, like everybody :) */
        if (blocked && !_scroll.contains(point)) {
            e.consume();
        }
    }

    private void loadDocument(int idx) {
        currentDocument = idx;
        if (currentWorker != null) {
            currentWorker.cancel(true);
        }
        uiTransform.setToIdentity();
        image = null;
        currentWorker = new ImageLoadWorker(idx);
        currentWorker.execute();
        _previewList.setSelectedIndex(idx);
        _previewList.ensureIndexIsVisible(idx);
        setDirty(true);
    }

    class ImageLoadWorker extends SwingWorker<BufferedImage, Void> {

        private int idx;

        public ImageLoadWorker(int idx) {
            this.idx = idx;
            busyLabel.setBusy(true);
        }

        @Override
        protected BufferedImage doInBackground() throws Exception {
            BufferedImage bi = null;
            /* load current document */
            if (!lazyList.isEmpty()) {
                /* load */
                Document doc = lazyList.get(idx);
                /* check for image document */
                if (ImageUtil.isSupportedExt(doc.get(FileExtField.NAME))) {
                    /* load complete image */
                    bi = ImageIO.read(new File(doc.get(FileNameField.NAME)));
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
            }
            return bi;
        }

        @Override
        protected void done() {
            try {
                super.done();
                image = get();
                busyLabel.setBusy(false);
                setDirty(true);
            } catch (Exception e) {
            }
        }
    }
}