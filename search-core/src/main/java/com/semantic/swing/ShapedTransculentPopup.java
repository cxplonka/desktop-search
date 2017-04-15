/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing;

import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.ResizableWindow;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ShapedTransculentPopup extends JidePopup {

    public static enum Triangle {

        TOP;
    };
    Triangle trianglePosition = Triangle.TOP;
    //
    private Shape shape;
    private static Method opacity;
    private static Method windowShape;

    static {
        try {
            Class awt = Class.forName("com.sun.awt.AWTUtilities");
            opacity = awt.getMethod("setWindowOpacity", new Class[]{Window.class, float.class});
            windowShape = awt.getMethod("setWindowShape", new Class[]{Window.class, Shape.class});
        } catch (Exception ex) {
            Logger.getLogger(ShapedTransculentPopup.class.getName()).log(Level.FINE,
                    "No ShapedTransculent Popups supported in this Enviroment!");
        }
    }

    public ShapedTransculentPopup() {
        super();
        setPopupBorder(BorderFactory.createEmptyBorder());
        setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));
    }

    public static JidePopup createPopup() {
        JidePopup ret;
        try {
            Class.forName("com.sun.awt.AWTUtilities");
            ret = new ShapedTransculentPopup();
        } catch (ClassNotFoundException ex) {
            ret = new JidePopup();
        }
        return ret;
    }

    public void setTrianglePosition(Triangle trianglePosition) {
        this.trianglePosition = trianglePosition;
    }

    public Triangle getTrianglePosition() {
        return trianglePosition;
    }

    @Override
    protected ResizableWindow createHeavyweightPopupContainer(Component owner) {
        ResizableWindow container;

        Component topLevelAncestor = getTopLevelAncestor(owner);
        if (topLevelAncestor instanceof Frame) {
            container = new ResizableWindow((Frame) topLevelAncestor);
        } else if (topLevelAncestor instanceof Window) {
            container = new ResizableWindow((Window) topLevelAncestor);
        } else {
            Frame frame = getFrame(owner);
            container = new ResizableWindow(frame);
        }
        container.getContentPane().add(this);

        try {
            opacity.invoke(null, new Object[]{container, 0.9f});
        } catch (Exception ex) {
            //not supported
        }

        return container;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (shape != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(shape);
        }
    }

    @Override
    protected void internalShowPopup(int cx, int cy, Component arg2) {
        super.internalShowPopup(cx, cy, arg2);

        Point point = new Point(cx, cy);
        SwingUtilities.convertPointToScreen(point, arg2);

        /* dimension of window with component */
        int ww = _window.getWidth();
        int wh = _window.getHeight();

        Insets is = getInsets();
        /* create our shaped popup */
        Area content = new Area(new Rectangle2D.Double(
                0, is.top - 1,
                ww,
                wh - is.top));
        /* we say that our triangle is in the bottom right side */
        Rectangle bounds = content.getBounds();
        /* center */
        point.x += arg2.getWidth() / 2 - bounds.getWidth() / 2;
        _window.setLocation(point);

        Path2D triangle = new Path2D.Double();
        double x;

        switch (trianglePosition) {
            case TOP:
                double tw = is.top * 2;
                x = bounds.getCenterX() - tw / 2;
                triangle.moveTo(x, is.top);
                triangle.lineTo(x + tw, is.top);
                triangle.lineTo(x + tw / 2, 0);
                triangle.closePath();
                break;
        }

        Area all = new Area(triangle);
        all.add(content);

        shape = all;
        try {
            windowShape.invoke(null, new Object[]{_window, shape});
        } catch (Exception ex) {
            //not supported
        }
    }
}
