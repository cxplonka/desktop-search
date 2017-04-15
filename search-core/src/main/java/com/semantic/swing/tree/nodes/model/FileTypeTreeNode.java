/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.semantic.model.filter.OMimeTypeFilter;
import com.semantic.swing.tree.IActionNode;
import com.semantic.util.image.TextureManager;
import javax.swing.ImageIcon;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FileTypeTreeNode extends ONodeTreeNode<OMimeTypeFilter> implements IActionNode {

    public FileTypeTreeNode(OMimeTypeFilter userData) {
        super(userData);
    }

    @Override
    protected void initNode() {
        super.initNode();
        if(userData.getName().startsWith("image")){
            setTreeNodeIcon(new ImageIcon(
                    TextureManager.def().loadImage("small/node_image.png")));
        }        
    }
}