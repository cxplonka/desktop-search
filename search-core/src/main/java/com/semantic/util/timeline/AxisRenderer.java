/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.timeline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class AxisRenderer extends Line2D.Double {

    private Font font = new Font(Font.DIALOG, Font.PLAIN, 10);
    private Point2D tmp = new Point2D.Double();
    private AffineTransform transform = new AffineTransform();
    /* range from */
    private double min = 0;
    private double max = 1;
    private boolean flipAxis = false;
    /* tick length and scalefactor for the label value */
    private int tick_length = 3;
    private double scaleFactor = 1;
    /* axis label, normal cache and tick shape */
    private Line2D tick = new Line2D.Double();
    private Point2D normal;
    private String label = null;
    private boolean visible = true;
    /* tick markers support */
    private List<TickMarker> markers;

    public AxisRenderer() {
        super();
    }

    public AxisRenderer(String label) {
        super();
        this.label = label;
    }

    public AxisRenderer(String label, double min, double max) {
        this(label);
        this.min = min;
        this.max = max;
    }

    public AxisRenderer(String label, boolean flipAxis) {
        this(label);
        this.flipAxis = flipAxis;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setMinimum(double min) {
        this.min = min;
    }

    public double getMinimum() {
        return min;
    }

    public void setMaximum(double max) {
        this.max = max;
    }

    public void setRange(double lower, double upper){
        this.min = lower;
        this.max = upper;
    }
    
    public double getMaximum() {
        return max;
    }

    public void setFlipAxis(boolean flipAxis) {
        this.flipAxis = flipAxis;
    }

    public boolean isFlipAxis() {
        return flipAxis;
    }

    public double getAngle() {
        return Math.atan2(y2 - y1, x2 - x1);
    }

    public void addTickMarker(TickMarker marker) {
        if (markers == null) {
            markers = new ArrayList<TickMarker>();
        }
        markers.add(marker);
    }

    public TickMarker getTickMarker(int idx) {
        if (markers != null) {
            return markers.get(idx);
        }
        return null;
    }

    public double guessDrawHeight() {
        Rectangle2D b = font.getStringBounds(
                String.valueOf(NiceNumbers.nicenum(max * scaleFactor, false)),
                new FontRenderContext(null, false, false));
        double w = Math.max(b.getWidth(), b.getHeight());
        return 3 * b.getHeight();
    }

    /**
     * 
     * @param value - between lower and upper bound
     * @return 
     */
    public String toLabel(double value){
        return String.valueOf(value);
    }
    
    public double paint(Graphics2D g2d) {
        /* nothing todo */
        if (!visible) {
            return 0;
        }
        /* begin drawing */
        g2d.setColor(Color.BLACK);
        g2d.draw(this);
        /* apply the scale factor for the value range */
        double minvalue = min * scaleFactor;
        double maxvalue = max * scaleFactor;
        double max_cursor = 0;
        boolean vertical = false;
        /* own font rendering */
        g2d.setFont(font);
        /* reference bounds of 1 value for calculation */
        Rectangle2D r = font.getStringBounds(
                String.valueOf(NiceNumbers.nicenum(maxvalue, false)),
                g2d.getFontRenderContext());
        /**
         * if the angle is near vertical the we take the height for the
         * calculation of thick sizes
         */
        double tickSize = r.getWidth() * 1.7;
        if (Math.abs(getAngle()) > 1.5 && Math.abs(getAngle()) < 1.6) {
            tickSize = r.getHeight() * 1.7;
            vertical = true;
        }
        /**
         * calculate how much ticks we can draw for this axis line
         */
        tmp.setLocation(x1, y1);
        int max_ticks = (int) Math.abs(Math.round(tmp.distance(x2, y2) / tickSize));
        /** calculate the normalvector of the line */
        normal = lineNormal(this.x1, this.y1, this.x2, this.y2, normal);
        /** calculate the tickmarks for the line axis */
        double[] values = NiceNumbers.tickValues(minvalue, maxvalue, max_ticks);
        double norm = 1 / (maxvalue - minvalue);
        /**
         * not draw the labels from p1 to p2, if true draw the labels from p2 to
         * p1
         */
        double angle = getAngle();
        if (flipAxis) {
            normal.setLocation(normal.getX() * -1, normal.getY() * -1);
            angle = Math.atan2(y1 - y2, x1 - x2);
        }
        /* save current transformation context */
        AffineTransform save = g2d.getTransform();
        transform.setTransform(save);
        /* draw all tick values */
        for (int i = 0; i < values.length; i++) {
            String tickMark = toLabel(values[i]);
            if(tickMark == null) continue;
            /* bounds of our tick label */
            r = font.getStringBounds(
                    tickMark,
                    g2d.getFontRenderContext());
            //
            double t = (values[i] - minvalue) * norm;
            /* calcualte the next positon of the tick mark */
            lerp(x1, y1, x2, y2, t, tmp);
            /* extend the normal vector */
            double nx = normal.getX() * -tick_length;
            double ny = normal.getY() * -tick_length;
            /* calculate transformation and draw tickmark */
            transform.translate(tmp.getX(), tmp.getY());
            g2d.setTransform(transform);
            tick.setLine(0, 0, nx, ny);
            g2d.draw(tick);
            transform.translate(-tmp.getX(), -tmp.getY());
            /** calculate string postion and draw string */
            double scale = vertical ? r.getWidth() : r.getHeight();
            nx = normal.getX() * -scale;
            ny = normal.getY() * -scale;
            //
            if (scale > max_cursor) {
                max_cursor = scale;
            }
            //
            tmp.setLocation(tmp.getX() + nx, tmp.getY() + ny);
            /* calculate transformation and draw ticklabel */
            transform.translate(tmp.getX() - r.getCenterX(),
                    tmp.getY() - r.getCenterY());
            /* transform to tick coordinate system and draw tick label */
            g2d.setTransform(transform);
            g2d.drawString(tickMark, 0, 0);
            /* transform back to the old coordinate system */
            transform.translate(-(tmp.getX() - r.getCenterX()),
                    -(tmp.getY() - r.getCenterY()));
        }
        /* draw the tickmarkers */
        if (markers != null) {
            for (TickMarker marker : markers) {
                double t = marker.getValue();
                /* value not in range, so not draw */
                if (marker.isVisible() && t >= 0 && t <= 1) {
                    /* calcualte the next positon of the tick mark */
                    lerp(x1, y1, x2, y2, t, tmp);
                    /* calculate transformation and draw tickmark */
                    transform.translate(tmp.getX(), tmp.getY());
                    transform.rotate(angle);
                    /* apply transformation matrix and draw marker */
                    g2d.setTransform(transform);
                    g2d.setPaint(marker.getColor());
                    g2d.fill(marker.getMarker());
                    g2d.setPaint(Color.BLACK);
                    g2d.draw(marker.getMarker());
                    /* update transform */
                    marker.setTransform(transform);
                    /* translate back to the old center */
                    transform.rotate(-angle);
                    transform.translate(-tmp.getX(), -tmp.getY());
                }
            }
        }
        /* current bounds of the label */
        if (label != null) {
            r = font.getStringBounds(
                    label,
                    g2d.getFontRenderContext());
            /* line center */
            lerp(x1, y1, x2, y2, 0.5, tmp);
            /** calculate the translation vector component */
            double nx = normal.getX() * (max_cursor * -2);
            double ny = normal.getY() * (max_cursor * -2);
            tmp.setLocation(tmp.getX() + nx, tmp.getY() + ny);
            //
            transform.translate(tmp.getX() - r.getCenterX(),
                    tmp.getY() - r.getCenterY());
            /** rotate the label, only rotate the vertical labels */
            if (vertical) {
                transform.translate(r.getCenterX(), r.getCenterY());
                transform.rotate(angle);
                transform.translate(-r.getCenterX(), -r.getCenterY());
            }
            /* apply transform and draw label */
            g2d.setTransform(transform);
            g2d.drawString(label, 0, 0);
        }
        /* restore the old transformation context */
        g2d.setTransform(save);
        /* bounds of our line shape  */
        Rectangle2D bounds = getBounds2D();
        /* return pseudo draw height */
        return tmp.distance(bounds.getCenterX(), bounds.getCenterY()) + r.getHeight();
    }

    public static Point2D lineNormal(double x1, double y1, double x2, double y2, Point2D normal) {
        if (normal == null) {
            normal = new Point2D.Double();
        }
        normal.setLocation(x2 - x1, y2 - y1);
        normal.setLocation(-normal.getY(), normal.getX());
        normalize(normal);
        return normal;
    }

    public static Point2D lerp(double x0, double y0, double x1, double y1, double t, Point2D point) {
        if (point == null) {
            point = new Point2D.Double();
        }
        point.setLocation((1.0 - t) * x0 + t * x1,
                (1.0 - t) * y0 + t * y1);
        return point;
    }

    public static void normalize(Point2D point) {
        double norm = 1.0 / Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY());
        point.setLocation(point.getX() * norm, point.getY() * norm);
    }
}