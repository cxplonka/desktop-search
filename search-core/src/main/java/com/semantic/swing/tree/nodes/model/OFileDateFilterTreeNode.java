/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.semantic.model.filter.OFileDateFilter;
import com.semantic.util.image.TextureManager;
import javax.swing.ImageIcon;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OFileDateFilterTreeNode extends ONodeTreeNode<OFileDateFilter> {

    public OFileDateFilterTreeNode(OFileDateFilter userData) {
        super(userData);
    }

    @Override
    protected void initNode() {
        super.initNode();
        setTreeNodeIcon(new ImageIcon(
                TextureManager.def().loadImage("small/node_filter_date.png")));
    }
}