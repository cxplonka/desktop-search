/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import com.semantic.util.AffineUtils;
import com.semantic.util.image.GaussianKernel;
import com.semantic.util.image.TextureManager;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class OptimizedShadowPathEffect {

    private BufferedImage _clipImage = null;
    private final ConvolveOp blurKernel = new ConvolveOp(new GaussianKernel(4));
    /* shadow effect images */
    private final BufferedImage leftDown = TextureManager.def().loadImage("shadow/shadow_left_down.png");
    private final BufferedImage rightDown = TextureManager.def().loadImage("shadow/shadow_right_down.png");
    private final BufferedImage rightUp = TextureManager.def().loadImage("shadow/shadow_right_up.png");
    private final BufferedImage leftUp = TextureManager.def().loadImage("shadow/shadow_left_up.png");
    private final BufferedImage trackDown = TextureManager.def().loadImage("shadow/shadow_track_down.png");
    private final BufferedImage trackRight = TextureManager.def().loadImage("shadow/shadow_track_right.png");
    private final BufferedImage trackUp = TextureManager.def().loadImage("shadow/shadow_track_up.png");
    private final BufferedImage trackLeft = TextureManager.def().loadImage("shadow/shadow_track_left.png");
    private final AffineTransform transform = new AffineTransform();
    private final Rectangle2D from = new Rectangle2D.Double();
    private final Rectangle2D to = new Rectangle2D.Double();

    public OptimizedShadowPathEffect() {
        setBrushColor(Color.BLACK);
        setBrushSteps(5);
        setEffectWidth(8);
        setRenderInsideShape(false);
        setOffset(new Point(0, 0));
        setShouldFillShape(false);
        setShapeMasked(true);
    }

    public void imageShadow(Graphics2D g, Shape clipShape) {
        Rectangle tmp = clipShape.getBounds();
        double mx = tmp.getMaxX();
        double my = tmp.getMaxY();
        double ew = mx - (tmp.getX() + leftDown.getWidth());
        double eh = my - (tmp.getY() + rightUp.getHeight());
        /* track image templates are 1 pixel bigger because of 1pixel transformation error */
        if (ew > 0 && eh > 0) {
            /* draw corners, left up */
            transform.setToTranslation(tmp.getX() - leftUp.getWidth() / 2, tmp.getY() - leftUp.getHeight() / 2);
            g.drawImage(leftUp, transform, null);
            /* right up */
            transform.setToTranslation(mx - rightUp.getWidth() / 2, tmp.getY() - rightUp.getHeight() / 2);
            g.drawImage(rightUp, transform, null);
            /* right down */
            transform.setToTranslation(mx - rightDown.getWidth() / 2, my - rightDown.getHeight() / 2);
            g.drawImage(rightDown, transform, null);
            /* left down */
            transform.setToTranslation(tmp.getX() - leftDown.getWidth() / 2, my - leftDown.getHeight() / 2);
            g.drawImage(leftDown, transform, null);
            /* scale and draw edges, down track */
            from.setFrame(0, 0, trackDown.getWidth(), trackDown.getHeight());
            to.setFrame(tmp.x + leftDown.getWidth() / 2, my - 1, tmp.width - 2 * leftDown.getWidth() / 2, trackDown.getHeight());
            g.drawImage(trackDown, AffineUtils.createTransformToFitBounds(from, to, false, transform), null);
            /* right track */
            from.setFrame(0, 0, trackRight.getWidth(), trackRight.getHeight());
            to.setFrame(mx - 1, tmp.y + rightUp.getHeight() / 2, trackRight.getWidth(), tmp.getHeight() - rightUp.getHeight());
            g.drawImage(trackRight, AffineUtils.createTransformToFitBounds(from, to, false, transform), null);
            /* left track */
            from.setFrame(0, 0, trackLeft.getWidth(), trackLeft.getHeight());
            to.setFrame(tmp.x - trackLeft.getWidth() + 1, tmp.y + leftUp.getHeight() / 2, trackLeft.getWidth(), tmp.getHeight() - leftUp.getHeight());
            g.drawImage(trackLeft, AffineUtils.createTransformToFitBounds(from, to, false, transform), null);
            /* up track */
            from.setFrame(0, 0, trackUp.getWidth(), trackUp.getHeight());
            to.setFrame(tmp.x + leftUp.getWidth() / 2, tmp.y - trackUp.getHeight() + 1, tmp.width - 2 * leftUp.getWidth() / 2, trackUp.getHeight());
            g.drawImage(trackUp, AffineUtils.createTransformToFitBounds(from, to, false, transform), null);
        }
    }

    public void blurShadow(Graphics2D g, Shape clipShape) {
        // create a rect to hold the bounds
        Rectangle tmp = clipShape.getBounds();
        int width = tmp.x + tmp.width;
        int height = tmp.y + tmp.height;
        tmp.setRect(0, 0, width + getEffectWidth() * 2 + 1,
                height + getEffectWidth() * 2 + 1);

        // Apply the border glow effect        
        BufferedImage clipImage = getClipImage(tmp);
        Graphics2D g2dImage = clipImage.createGraphics();
        try {
            /* clear the buffer */
            g2dImage.setComposite(AlphaComposite.Clear);
            g2dImage.fillRect(0, 0, clipImage.getWidth(), clipImage.getHeight());
            /* translate with offset */
            g2dImage.translate(getEffectWidth() - getOffset().getX(),
                    getEffectWidth() - getOffset().getY());
            /* */
            g2dImage.setComposite(AlphaComposite.Src);
            g2dImage.setPaint(getBrushColor());
            g2dImage.translate(offset.getX(), offset.getY());
            g2dImage.fill(clipShape);
            g2dImage.translate(-offset.getX(), -offset.getY());
        } finally {
            /* draw the final image */
            g2dImage.dispose();
        }
        g.drawImage(clipImage, blurKernel, -getEffectWidth() + (int) getOffset().getX(),
                -getEffectWidth() + (int) getOffset().getY());
    }

    public void apply(Graphics2D g, Shape clipShape, int width, int height) {
        // create a rect to hold the bounds
        Rectangle tmp = clipShape.getBounds();
        width = (int) tmp.getMaxX();
        height = (int) tmp.getMaxY();
        tmp.setRect(0, 0, width + getEffectWidth() * 2 + 1,
                height + getEffectWidth() * 2 + 1);

        // Apply the border glow effect
        if (isShapeMasked()) {
            BufferedImage clipImage = getClipImage(tmp);
            Graphics2D g2 = clipImage.createGraphics();
            try {
                /* clear the buffer */
                g2.setPaint(Color.BLACK);
                g2.setComposite(AlphaComposite.Clear);
                g2.fillRect(0, 0, clipImage.getWidth(), clipImage.getHeight());

                g2.translate(getEffectWidth() - getOffset().getX(),
                        getEffectWidth() - getOffset().getY());
                paintBorderGlow(g2, clipShape, width, height);

                /* clip out the parts we don't want */
                g2.setComposite(AlphaComposite.Clear);
                g2.setColor(Color.WHITE);
                if (isRenderInsideShape()) {
                    /* clip the outside */
                    Area area = new Area(tmp);
                    area.subtract(new Area(clipShape));
                    g2.fill(area);
                } else {
                    /* clip the inside */
                    g2.fill(clipShape);
                }
            } finally {
                /* draw the final image */
                g2.dispose();
            }
            g.drawImage(clipImage, -getEffectWidth() + (int) getOffset().getX(), -getEffectWidth() + (int) getOffset().getY(), null);
        } else {
            paintBorderGlow(g, clipShape, width, height);
        }
    }

    private BufferedImage getClipImage(final Rectangle effectBounds) {
        /* 
         * only create new temp buffer if the effectBounds become greate the the 
         * current backed clip image
         */
        if (_clipImage == null
                || _clipImage.getWidth() < effectBounds.width
                || _clipImage.getHeight() < effectBounds.height) {
            /* */
            int w = effectBounds.width;
            int h = effectBounds.height;
            if (_clipImage != null) {
                w = Math.max(_clipImage.getWidth(), w);
                h = Math.max(_clipImage.getHeight(), h);
            }
            _clipImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
        return _clipImage;
    }

    /**
     * Paints the border glow
     * @param g2dImage
     * @param clipShape
     * @param width
     * @param height
     */
    protected void paintBorderGlow(Graphics2D g2,
            Shape clipShape, int width, int height) {
        int steps = getBrushSteps();

        boolean inside = isRenderInsideShape();

        g2.setPaint(getBrushColor());
        g2.translate(offset.getX(), offset.getY());

        if (isShouldFillShape()) {
            // set the inside/outside mode
            if (inside) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
                Area a1 = new Area(new Rectangle(
                        (int) -offset.getX() - 20,
                        (int) -offset.getY() - 20,
                        width + 40, height + 40));
                Area a2 = new Area(clipShape);
                a1.subtract(a2);
                g2.fill(a1);
            } else {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 1f));
                g2.fill(clipShape);
            }
        }

        float brushAlpha = 1f / steps;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, brushAlpha));

        // draw the effect
        for (float i = 0; i < steps; i = i + 1f) {
            float brushWidth = i * effectWidth / steps;
            g2.setStroke(new BasicStroke(brushWidth,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            g2.draw(clipShape);
        }
        g2.translate(-offset.getX(), -offset.getY());

    }
    /**
     * Holds value of property brushColor.
     */
    private Color brushColor;
    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    /**
     * Getter for property brushColor.
     * @return Value of property brushColor.
     */
    public Color getBrushColor() {
        return this.brushColor;
    }

    /**
     * Setter for property brushColor.
     * @param brushColor New value of property brushColor.
     */
    public void setBrushColor(Color brushColor) {
        Color oldBrushColor = this.brushColor;
        this.brushColor = brushColor;
        propertyChangeSupport.firePropertyChange("brushColor", oldBrushColor, brushColor);
    }
    /**
     * Holds value of property brushSteps.
     */
    private int brushSteps;

    /**
     * Getter for property brushSteps.
     * @return Value of property brushSteps.
     */
    public int getBrushSteps() {
        return this.brushSteps;
    }

    /**
     * Setter for property brushSteps.
     * @param brushSteps New value of property brushSteps.
     */
    public void setBrushSteps(int brushSteps) {
        int oldBrushSteps = this.brushSteps;
        this.brushSteps = brushSteps;
        propertyChangeSupport.firePropertyChange("brushSteps", oldBrushSteps, brushSteps);
    }
    /**
     * Holds value of property effectWidth.
     */
    private int effectWidth;

    /**
     * Getter for property effectWidth.
     * @return Value of property effectWidth.
     */
    public int getEffectWidth() {
        return this.effectWidth;
    }

    /**
     * Setter for property effectWidth.
     * @param effectWidth New value of property effectWidth.
     */
    public void setEffectWidth(int effectWidth) {
        int oldEffectWidth = this.effectWidth;
        this.effectWidth = effectWidth;
        propertyChangeSupport.firePropertyChange("effectWidth", oldEffectWidth, effectWidth);
    }
    /**
     * Holds value of property renderInsideShape.
     */
    private boolean renderInsideShape;

    /**
     * Getter for property renderInsideShape.
     * @return Value of property renderInsideShape.
     */
    public boolean isRenderInsideShape() {
        return this.renderInsideShape;
    }

    /**
     * Setter for property renderInsideShape.
     * @param renderInsideShape New value of property renderInsideShape.
     */
    public void setRenderInsideShape(boolean renderInsideShape) {
        boolean oldRenderInsideShape = this.renderInsideShape;
        this.renderInsideShape = renderInsideShape;
        propertyChangeSupport.firePropertyChange("renderInsideShape", oldRenderInsideShape, renderInsideShape);
    }
    /**
     * Holds value of property offset.
     */
    private Point2D offset;

    /**
     * Getter for property offset.
     * @return Value of property offset.
     */
    public Point2D getOffset() {
        return this.offset;
    }

    /**
     * Setter for property offset.
     * @param offset New value of property offset.
     */
    public void setOffset(Point2D offset) {
        Point2D oldOffset = this.offset;
        this.offset = offset;
        propertyChangeSupport.firePropertyChange("offset", oldOffset, offset);
    }
    /**
     * Holds value of property shouldFillShape.
     */
    private boolean shouldFillShape;

    /**
     * Getter for property shouldFillShape.
     * @return Value of property shouldFillShape.
     */
    public boolean isShouldFillShape() {
        return this.shouldFillShape;
    }

    /**
     * Setter for property shouldFillShape.
     * @param shouldFillShape New value of property shouldFillShape.
     */
    public void setShouldFillShape(boolean shouldFillShape) {
        boolean oldShouldFillShape = this.shouldFillShape;
        this.shouldFillShape = shouldFillShape;
        propertyChangeSupport.firePropertyChange("shouldFillShape", oldShouldFillShape, shouldFillShape);
    }
    /**
     * Holds value of property shapeMasked.
     */
    private boolean shapeMasked;

    /**
     * Getter for property shapeMasked.
     * @return Value of property shapeMasked.
     */
    public boolean isShapeMasked() {
        return this.shapeMasked;
    }

    /**
     * Setter for property shapeMasked.
     * @param shapeMasked New value of property shapeMasked.
     */
    public void setShapeMasked(boolean shapeMasked) {
        boolean oldShapeMasked = this.shapeMasked;
        this.shapeMasked = shapeMasked;
        propertyChangeSupport.firePropertyChange("shapeMasked", oldShapeMasked, shapeMasked);
    }
}