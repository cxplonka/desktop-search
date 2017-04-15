/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes;

import com.semantic.model.ODirectoryNode;
import com.semantic.model.OFileSystem;
import com.semantic.swing.tree.nodes.model.FileTypeTreeNode;
import com.semantic.swing.tree.nodes.model.ONodeTreeNode;
import com.semantic.model.filter.OMimeTypeFilter;
import com.semantic.model.OGroup;
import com.semantic.model.OModel;
import com.semantic.model.OntologyNode;
import com.semantic.model.filter.OFileDateFilter;
import com.semantic.model.filter.OFileSizeFilter;
import com.semantic.model.filter.OFixedFileDateFilter;
import com.semantic.swing.tree.nodes.model.FileSystemTreeNode;
import com.semantic.swing.tree.nodes.model.ODirectoryTreeNode;
import com.semantic.swing.tree.nodes.model.OFileDateFilterTreeNode;
import com.semantic.swing.tree.nodes.model.OFileSizeFilterTreeNode;
import com.semantic.swing.tree.nodes.model.OFixedFileDateFilterTreeNode;
import com.semantic.swing.tree.nodes.model.OGroupTreeNode;
import com.semantic.swing.tree.nodes.model.OModelTreeNode;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class TreeNodeFactory {

    private static TreeNodeFactory ref;

    public synchronized static TreeNodeFactory def() {
        if (ref == null) {
            ref = new TreeNodeFactory();
        }
        return ref;
    }
    private final Map<Class, ITreeNodeCreator> builder = new HashMap<Class, ITreeNodeCreator>();

    public TreeNodeFactory() {
        initDefault();
    }

    protected void initDefault() {
        registerFactory(OModel.class, OModelTreeNode.class);
        registerFactory(OMimeTypeFilter.class, FileTypeTreeNode.class);
        registerFactory(OntologyNode.class, ONodeTreeNode.class);
        registerFactory(OFileSystem.class, FileSystemTreeNode.class);
        registerFactory(ODirectoryNode.class, ODirectoryTreeNode.class);
        registerFactory(OGroup.class, OGroupTreeNode.class);
        registerFactory(OFileDateFilter.class, OFileDateFilterTreeNode.class);
        registerFactory(OFileSizeFilter.class, OFileSizeFilterTreeNode.class);
        registerFactory(OFixedFileDateFilter.class, OFixedFileDateFilterTreeNode.class);
    }

    public void registerFactory(Class<? extends OntologyNode> clazz, Class<? extends AbstractOMutableTreeNode> treeClazz) {
        builder.put(clazz, new DefaultNodeCreator(clazz, treeClazz));
    }

    public <T extends OntologyNode> AbstractOMutableTreeNode<T> createTreeNode(T node) {
        ITreeNodeCreator ret = builder.get(node.getClass());
        if (ret == null) {
            return new ONodeTreeNode<T>(node);
        }
        return ret.createTreeNode(node);
    }
}