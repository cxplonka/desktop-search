/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.semantic.model.OModel;
import com.semantic.util.image.TextureManager;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OModelTreeNode extends OGroupTreeNode<OModel>{    

    public OModelTreeNode(OModel node) {
        super(node);
    }

    @Override
    protected void initNode() {
        super.initNode();
        setCheckBoxVisible(false);
        setTreeNodeIcon(new ImageIcon(
                TextureManager.def().loadImage("small/node_root.png")));
    }

    @Override
    public Action[] getActions() {
        return null;
    }
}