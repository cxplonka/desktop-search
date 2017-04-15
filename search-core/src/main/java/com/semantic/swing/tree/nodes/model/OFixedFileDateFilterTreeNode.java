/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import com.semantic.model.filter.OFixedFileDateFilter;
import com.semantic.swing.propertysheet.OPropertyEditorRegistry;
import com.semantic.swing.propertysheet.OPropertyRendererRegistry;
import com.semantic.swing.propertysheet.editor.EnumComboBoxPropertyEditor;
import com.semantic.util.image.TextureManager;
import javax.swing.ImageIcon;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OFixedFileDateFilterTreeNode extends ONodeTreeNode<OFixedFileDateFilter> {

    static {
        //editor
        OPropertyEditorRegistry.def().registerEditor(
                OFixedFileDateFilter.DATE.class, EnumComboBoxPropertyEditor.class);
        //renderer
        OPropertyRendererRegistry.def().registerRenderer(
                OFixedFileDateFilter.DATE.class, DefaultCellRenderer.class);
    }

    public OFixedFileDateFilterTreeNode(OFixedFileDateFilter userData) {
        super(userData);
    }

    @Override
    protected void initNode() {
        super.initNode();
        setTreeNodeIcon(new ImageIcon(
                TextureManager.def().loadImage("small/node_filter_date.png")));
    }
}
