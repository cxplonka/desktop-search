/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class AffineUtils {

    private static Point2D p1 = new Point2D.Double();
    private static Point2D p2 = new Point2D.Double();
    private double[] src_matrix = new double[6];
    private double[] dst_matrix = new double[6];

    public static void scaleAboutPoint(double sx, double sy, double x, double y, AffineTransform transform) {
        transform.translate(x, y);
        transform.scale(sx, sy);
        transform.translate(-x, -y);
    }

    public static void transform(Rectangle2D bounds, AffineTransform t) {
        p1.setLocation(bounds.getMinX(), bounds.getMinY());
        p2.setLocation(bounds.getMaxX(), bounds.getMaxY());
        t.transform(p1, p1);
        t.transform(p2, p2);
        bounds.setRect(p1.getX(), p1.getY(), p2.getX() - p1.getX(),
                p2.getY() - p1.getY());
    }

    public static AffineTransform createCenterTransform(Rectangle2D from, Rectangle2D to, AffineTransform transform) {
        if (transform == null) {
            transform = new AffineTransform();
        }
        transform.setToIdentity();

        transform.setToTranslation(to.getCenterX() - from.getCenterX(),
                to.getCenterY() - from.getCenterY());

        return transform;
    }

    /**
     * will scale and center
     *
     * @param from
     * @param to
     * @param keepAspectRation
     * @param transform
     * @return
     */
    public static AffineTransform createTransformToFitBounds(Rectangle2D from, Rectangle2D to, boolean keepAspectRation, AffineTransform transform) {
        if (transform == null) {
            transform = new AffineTransform();
        }
        transform.setToIdentity();

        transform.setToTranslation(to.getCenterX() - from.getCenterX(),
                to.getCenterY() - from.getCenterY());

        double x_scale = to.getWidth() / from.getWidth();
        double y_scale = to.getHeight() / from.getHeight();

        if (keepAspectRation) {
            double s = Math.min(x_scale, y_scale);
            x_scale = s;
            y_scale = s;
        }

        transform.translate(from.getCenterX(), from.getCenterY());
        transform.scale(x_scale, y_scale);
        transform.translate(-from.getCenterX(), -from.getCenterY());

        return transform;
    }

    /**
     * will scale
     *
     * @param from
     * @param to
     * @param keepAspectRation
     * @param transform
     * @return
     */
    public static AffineTransform createScaleTransfrom(Rectangle2D from, Rectangle2D to, boolean keepAspectRation, AffineTransform transform) {
        if (transform == null) {
            transform = new AffineTransform();
        }
        transform.setToIdentity();

        double x_scale = to.getWidth() / from.getWidth();
        double y_scale = to.getHeight() / from.getHeight();

        if (keepAspectRation) {
            double s = Math.min(x_scale, y_scale);
            x_scale = s;
            y_scale = s;
        }
        transform.scale(x_scale, y_scale);

        return transform;
    }

    public void interpolate(AffineTransform source, AffineTransform destination, AffineTransform transform, double delta) {        
        source.getMatrix(src_matrix);
        destination.getMatrix(dst_matrix);

        transform.setTransform(src_matrix[0] + (delta * (dst_matrix[0] - src_matrix[0])),
                src_matrix[1] + (delta * (dst_matrix[1] - src_matrix[1])),
                src_matrix[2] + (delta * (dst_matrix[2] - src_matrix[2])),
                src_matrix[3] + (delta * (dst_matrix[3] - src_matrix[3])),
                src_matrix[4] + (delta * (dst_matrix[4] - src_matrix[4])),
                src_matrix[5] + (delta * (dst_matrix[5] - src_matrix[5])));
    }
}