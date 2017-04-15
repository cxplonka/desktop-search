/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import com.semantic.util.AffineUtils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class KenBurnsEffect extends JFrame {

    private AffineTransform _current = new AffineTransform();
    private AffineTransform _image = new AffineTransform();
    /* */
    private float _alpha = 1;
    private int _from = 0, _to = 1;
    private JPanel drawPanel;
    private BufferedImage[] _images;

    public KenBurnsEffect() {
        super();
//        setUndecorated(true);
//        setRootPane(new JRootPane());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        try {
            _images = new BufferedImage[]{
                ImageIO.read(new File("c:/1.jpg")),
                ImageIO.read(new File("c:/2.jpg")),
                ImageIO.read(new File("c:/3.jpg")),
                ImageIO.read(new File("c:/4.jpg"))
            };
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        /* */
        add(drawPanel = new JPanel(new BorderLayout()) {

            AlphaComposite _blend = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                
                Rectangle2D bounds = getBounds();

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                /* view transform */
                Composite oldComposite = g2d.getComposite();
                AffineTransform oldTransform = g2d.getTransform();
                _image.setTransform(oldTransform);
                _image.concatenate(_current);
                g2d.setTransform(_image);
                /* from image */
                AffineUtils.createTransformToFitBounds(
                        new Rectangle2D.Double(0, 0,
                        _images[_from].getWidth(), _images[_from].getHeight()),
                        bounds, true, _image);
                _image.concatenate(AffineTransform.getTranslateInstance(
                        -(1 - _alpha) * _images[_from].getWidth() * 0.1,
                        (1 - _alpha) * _images[_from].getHeight() * 0.1));
                g2d.setComposite(_blend.derive(_alpha));
                g2d.drawImage(_images[_from], _image, null);
                /* to image */
                AffineUtils.createTransformToFitBounds(
                        new Rectangle2D.Double(0, 0,
                        _images[_to].getWidth(), _images[_to].getHeight()),
                        bounds, true, _image);
                _image.concatenate(AffineTransform.getTranslateInstance(
                        -(_alpha) * _images[_to].getWidth() * 0.1,
                        (_alpha) * _images[_to].getHeight() * 0.1));
                g2d.setComposite(_blend.derive(1 - _alpha));
                g2d.drawImage(_images[_to], _image, null);
                /* */
                g2d.setTransform(oldTransform);
                g2d.setComposite(oldComposite);

                String dateString = "13. April 2002";
                Rectangle2D sBounds = g2d.getFontMetrics().getStringBounds(dateString, g);
                
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(dateString,
                        (int) (bounds.getWidth() - sBounds.getWidth()) - 10,
                        (int) (bounds.getHeight()) - 10);

            }
        }, BorderLayout.CENTER);
        drawPanel.setBackground(Color.BLACK);
        drawPanel.setFont(new Font("Times", Font.BOLD, 23));
        add(new JButton(new InterpolateAction()), BorderLayout.SOUTH);
    }

    class InterpolateAction extends AbstractAction implements TimingTarget {

        boolean running = false;
        Animator animator;
        AffineTransform source = new AffineTransform();
        AffineTransform destination = new AffineTransform();
        AffineTransform transform = new AffineTransform();
        AffineUtils util = new AffineUtils();
        boolean zoomin = true;

        public InterpolateAction() {
            super("start");
            animator = new Animator(20000, this);
            animator.setRepeatBehavior(Animator.RepeatBehavior.LOOP);
            animator.setRepeatCount(Animator.INFINITE);
            animator.setAcceleration(0.5f);
            animator.setDeceleration(0.5f);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (running) {
                animator.stop();
            } else {
                animator.start();
            }
        }

        @Override
        public void timingEvent(float f) {
            _alpha = 1 - f;
            util.interpolate(source, destination, _current, f);
            repaint();
        }

        @Override
        public void begin() {
            putValue(NAME, "stop");
            running = true;
            /* viewport to image transformation */
            transform();
        }

        private void transform() {
            /* draw bounds of the panel */
            Rectangle2D drawBounds = drawPanel.getBounds();
            /* transform full image bounds to draw bounds */
            Rectangle2D imageBounds = new Rectangle2D.Double(0, 0, _images[_to].getWidth(), _images[_to].getHeight());
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
                w *= 0.65;
                h *= 0.65;
            } else {
                w *= 0.75;
                h *= 0.75;
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
            putValue(NAME, "start");
        }

        @Override
        public void repeat() {
            _from = (_from + 1) % _images.length;
            _to = (_from + 1) % _images.length;
            /* */
            source.setTransform(destination);
            /* viewport to image transformation */
            transform();
        }
    }

    public static void main(String[] arg) throws Exception {
//        UIManager.setLookAndFeel(new SyntheticaSimple2DLookAndFeel());

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                KenBurnsEffect test = new KenBurnsEffect();
                test.setSize(1000, 800);
                test.setVisible(true);
            }
        });
    }
}