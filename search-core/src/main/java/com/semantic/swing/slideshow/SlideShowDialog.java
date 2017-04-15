/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.slideshow;

import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.lucene.fields.image.CommentField;
import com.semantic.util.AffineUtils;
import com.semantic.util.image.ImageUtil;
import com.semantic.util.image.TextureManager;
import com.semantic.util.lazy.LazyList;
import com.semantic.util.swing.SwingUtils;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import org.apache.lucene.document.Document;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.interpolation.SplineInterpolator;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SlideShowDialog extends JDialog {

    private final AffineTransform _current = new AffineTransform();
    private final AffineTransform _image = new AffineTransform();
    private float _alpha = 1;
    private Date _date;
    private String _comment;
    private int _from = 0;
    private JPanel drawPanel;
    private BufferedImage from, to;
    private final InterpolateAction action = new InterpolateAction();
    private LazyList<Document> lazyList;
    private static final BufferedImage _logo = TextureManager.def().loadImage("igmas_logo.png");
    private static final Color _textColor = new Color(130, 157, 25);

    public SlideShowDialog(Frame parent) {
        super(parent);
        setUndecorated(true);
        /* override because of synthetica look and feel */
        setRootPane(new JRootPane());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        setModal(true);
        setLayout(new BorderLayout());
        /* */
        initOwnComponents();
    }

    public void setModel(LazyList<Document> lazyList) {
        this.lazyList = lazyList;
        _from = 0;
    }

    private void initOwnComponents() {
        add(drawPanel = new JPanel(new BorderLayout()) {

            AlphaComposite _blend = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Rectangle bounds = getBounds();

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                /* view transform */
                Composite oldComposite = g2d.getComposite();
                AffineTransform oldTransform = g2d.getTransform();
                _image.setTransform(oldTransform);
                _image.concatenate(_current);
                g2d.setTransform(_current);
                /* from image */
                if (from != null) {
                    /* transformation to center bounds in panel coordinate system */
                    AffineUtils.createTransformToFitBounds(
                            new Rectangle2D.Double(0, 0,
                                    from.getWidth(), from.getHeight()),
                            bounds, true, _image);
                    /* translation effect */
                    _image.concatenate(AffineTransform.getTranslateInstance(
                            -(1 - _alpha) * from.getWidth() * 0.1,
                            (1 - _alpha) * from.getHeight() * 0.1));
                    /* smooth alpha interpolation */
                    g2d.setComposite(_blend.derive(_alpha));
                    g2d.drawImage(from, _image, null);
                }
                /* to image */
                if (to != null) {
                    /* transformation to center bounds in panel coordinate system */
                    AffineUtils.createTransformToFitBounds(
                            new Rectangle2D.Double(0, 0,
                                    to.getWidth(), to.getHeight()),
                            bounds, true, _image);
                    /* translation effect */
                    _image.concatenate(AffineTransform.getTranslateInstance(
                            -(_alpha) * to.getWidth() * 0.1,
                            (_alpha) * to.getHeight() * 0.1));
                    /* smooth alpha interpolation */
                    g2d.setComposite(_blend.derive(1 - _alpha));
                    g2d.drawImage(to, _image, null);
                }
                /* */
                g2d.setTransform(oldTransform);
                g2d.setComposite(oldComposite);
//                if (_date != null) {
//                    String dateString = _date.toString();
//                    Rectangle2D sBounds = g2d.getFontMetrics().getStringBounds(dateString, g);
//                    g2d.setColor(_textColor);
//                    g2d.drawString(dateString,
//                            (int) (bounds.getWidth() - sBounds.getWidth()) - 10,
//                            (int) (bounds.getHeight()) - 10);
//                }
                // comment
                if (_comment != null) {
                    g2d.setColor(_textColor);
                    g2d.drawString(_comment, 10,
                            (int) (bounds.getHeight()) - 10);
                }
                // igmas logo
                g2d.drawImage(_logo, null, 10, 10);
            }
        }, BorderLayout.CENTER);
        drawPanel.setBackground(Color.BLACK);
        drawPanel.setFont(new Font("Times", Font.BOLD, 48));
        SwingUtils.registerKeyBoardAction(drawPanel, new CloseDialogAction());        
    }

    @Override
    public void setVisible(boolean bln) {
        Rectangle bounds = getGraphicsConfiguration().getBounds();
        setSize(bounds.width, bounds.height);
        if (bln) {
            action.start();
        } else {
            action.stop();
        }
        /* */
        super.setVisible(bln);
    }

    class InterpolateAction implements TimingTarget {

        private boolean running = false;
        boolean zoomin = true;
        private final Animator animator;
        private final AffineTransform source = new AffineTransform();
        private final AffineTransform destination = new AffineTransform();
        private final AffineTransform transform = new AffineTransform();
        private final AffineUtils util = new AffineUtils();

        public InterpolateAction() {
            animator = new Animator(10000, this);
            animator.setRepeatBehavior(Animator.RepeatBehavior.LOOP);
            animator.setInterpolator(new SplineInterpolator(0.77f, 0, 1f, 0f));
            animator.setRepeatCount(Animator.INFINITE);
            animator.setAcceleration(0.5f);
            animator.setDeceleration(0.5f);
        }

        public void start() {
            if (!running) {
                animator.start();
            }
        }

        @javax.annotation.PreDestroy
        public void stop() {
            if (running) {
                animator.stop();
            }
        }

        @Override
        public void timingEvent(float f) {
            _alpha = 1 - f;
            util.interpolate(source, destination, _current, f);
            repaint();
        }

        private BufferedImage fetchImage() {
            BufferedImage ret = null;
            for (int i = 0; i < lazyList.size(); i++) {
                Document doc = lazyList.get(_from);
                /* check for image document */
                if (ImageUtil.isSupportedExt(doc.get(FileExtField.NAME))) {
                    /* user comment */
                    _comment = doc.get(CommentField.NAME);
                    /* complete filename */
                    String fileName = doc.get(FileNameField.NAME);
                    try {
                        ret = ImageIO.read(new File(fileName));
                        _date = new Date(
                                doc.getField(LastModifiedField.NAME).numericValue().longValue());
                        break;
                    } catch (IOException ex) {
                        Logger.getLogger(SlideShowDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                _from = (_from + 1) % lazyList.size();
            }
            _from = (_from + 1) % lazyList.size();
            return ret;
        }

        @Override
        public void begin() {
            running = true;
            from = fetchImage();
            to = fetchImage();
            /* viewport to image transformation */
            if (to != null) {
                transform();
            }
        }

        private void transform() {
            /* draw bounds of the panel */
            Rectangle2D drawBounds = drawPanel.getBounds();
            /* transform full image bounds to draw bounds */
            Rectangle2D imageBounds = new Rectangle2D.Double(0, 0, to.getWidth(), to.getHeight());
            AffineUtils.createTransformToFitBounds(
                    imageBounds,
                    drawBounds, true, transform);
            AffineUtils.transform(imageBounds, transform);
            /* */
            double min = Math.min(imageBounds.getWidth(), imageBounds.getHeight());
            double x_scale = min / imageBounds.getWidth();
            double y_scale = min / imageBounds.getHeight();
            double w = imageBounds.getWidth() * x_scale;
            double h = imageBounds.getHeight() * y_scale;
            if (zoomin) {
                w *= 0.75;
                h *= 0.75;
            } else {
                w *= 0.9;
                h *= 0.9;
            }
            zoomin = !zoomin;
            /* */
            Rectangle2D deviceBounds = new Rectangle2D.Double(
                    imageBounds.getCenterX() - w / 2,
                    imageBounds.getCenterY() - h / 2, w, h);
            /* */
            AffineUtils.createTransformToFitBounds(
                    deviceBounds,
                    drawBounds, true, destination);
        }

        @Override
        public void end() {
            running = false;
        }

        @Override
        public void repeat() {
            from = to;
            to = fetchImage();
            /* viewport to image transformation */
            source.setTransform(destination);
            if (to != null) {
                transform();
            }
        }
    }
}
