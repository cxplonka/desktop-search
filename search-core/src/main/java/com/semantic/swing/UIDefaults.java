/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class UIDefaults {

    /**
     * (valuetype - Color)
     */
    public static final String BACKGROUND_TREE = "isearch.default.background.tree";
    public static final String BACKGROUND_GRID = "isearch.default.background.grid";
    public static final String BORDER_GRID_VIEW = "border.grid.view";
    public static final String BORDER_TREE_VIEW = "border.tree.view";
    public static final String INFO_SCROLLPANE_BORDER = "info.scrollpane.border";
    public static final String PROPERTYSHEET_BORDER = "propertysheet.scrollpane.border";
    /**
     * gradient from (valuetype - Color)
     */
    public static final String LIST_UNSELECTION_FROM = "isearch.list.unselection.from";
    /**
     * gradient to (valuetype - Color)
     */
    public static final String LIST_UNSELECTION_TO = "isearch.list.unselection.to";
    /**
     * valuetype - boolean
     */
    public static final String ROOTPANE_SHAPED = "rootpane.shaped";
    /* */
    protected static final Object[][] DEFAULTS = {
        {"Synthetica.window.decoration", true},
        {ROOTPANE_SHAPED, true},
        {BACKGROUND_TREE, new Color(255, 255, 255)},
        {INFO_SCROLLPANE_BORDER, new MatteBorder(1, 1, 0, 0, Color.GRAY)},
        {PROPERTYSHEET_BORDER, new MatteBorder(1, 0, 0, 1, Color.GRAY)},
        {BORDER_GRID_VIEW, new MatteBorder(1, 1, 1, 1, Color.GRAY)},
        {BORDER_TREE_VIEW, new MatteBorder(1, 0, 1, 1, Color.GRAY)},
        {BACKGROUND_GRID, Color.WHITE},
        {LIST_UNSELECTION_FROM, new Color(231, 235, 240)},
        {LIST_UNSELECTION_TO, new Color(205, 205, 205)},
        {"Tree.rendererFillBackground", false},
        {"TabbedPane.borderHightlightColor", Color.WHITE}
    };
    protected static final Object[][] MAC_DEFAULTS = {
        {"Synthetica.window.decoration", false},
        {ROOTPANE_SHAPED, false},
        {BACKGROUND_TREE, new Color(231, 235, 240)},
        {INFO_SCROLLPANE_BORDER, BorderFactory.createEmptyBorder()},
        {PROPERTYSHEET_BORDER, BorderFactory.createEmptyBorder()},
        {BORDER_GRID_VIEW, new MatteBorder(1, 0, 1, 0, Color.GRAY)},
        {BORDER_TREE_VIEW, new MatteBorder(1, 0, 1, 0, Color.GRAY)},
        {BACKGROUND_GRID, Color.WHITE},
        {LIST_UNSELECTION_FROM, new Color(231, 235, 240)},
        {LIST_UNSELECTION_TO, new Color(205, 205, 205)},
        {"Tree.rendererFillBackground", false}
    };

    public static void loadDefaults() {
        for (Object[] DEFAULTS1 : DEFAULTS) {
            UIManager.put(DEFAULTS1[0], DEFAULTS1[1]);
        }
    }

    public static void loadMacDefaults() {
        for (Object[] MAC_DEFAULTS1 : MAC_DEFAULTS) {
            UIManager.put(MAC_DEFAULTS1[0], MAC_DEFAULTS1[1]);
        }
    }
}
