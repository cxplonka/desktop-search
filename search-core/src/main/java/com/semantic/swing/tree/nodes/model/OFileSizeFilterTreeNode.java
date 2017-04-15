/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.semantic.model.filter.OFileSizeFilter;
import com.semantic.util.image.TextureManager;
import javax.swing.ImageIcon;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OFileSizeFilterTreeNode extends ONodeTreeNode<OFileSizeFilter> {

    public OFileSizeFilterTreeNode(OFileSizeFilter userData) {
        super(userData);
    }

    @Override
    protected void initNode() {
        super.initNode();
        setTreeNodeIcon(new ImageIcon(
                TextureManager.def().loadImage("small/node_file_size.png")));
    }
}