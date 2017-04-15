/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.semantic.util.timeline;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TickMarker {

    private double value;
    private Shape marker;
    private Color color;    
    private boolean visible = true;
    private Point2D _intersection = new Point2D.Double();
    private AffineTransform _transform;

    /**
     *
     * @param value - normalized value from 0...1
     * @param marker - the marker will be translate to (0,0)
     * so begin drawing from there
     */
    public TickMarker(double value, Shape marker, Color color) {
        this.value = value;
        this.marker = marker;
        this.color = color;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * normalized value from 0...1
     * @return 
     */
    public double getValue() {
        return value;
    }

    public void setMarker(Shape marker) {
        this.marker = marker;
    }

    public Shape getMarker() {
        return marker;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setTransform(AffineTransform _transform) {
        if(this._transform == null){
            this._transform = new AffineTransform();
        }
        this._transform.setTransform(_transform);
    }

    public boolean contains(Point2D point){
        _intersection.setLocation(point);
        if(this._transform != null){
            try {
                _transform.inverseTransform(_intersection, _intersection);
//                System.out.println(point);
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(TickMarker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return marker.contains(_intersection);
    }
}