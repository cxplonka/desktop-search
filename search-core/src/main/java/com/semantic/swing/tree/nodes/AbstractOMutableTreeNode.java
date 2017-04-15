/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes;

import com.semantic.model.OntologyNode;
import com.semantic.util.image.TextureManager;
import com.semantic.util.property.IPropertyKey;
import com.semantic.util.property.PropertyKey;
import com.semantic.util.property.PropertySupport;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public abstract class AbstractOMutableTreeNode<T extends OntologyNode> extends PropertySupport
        implements MutableTreeNode {

    public static IPropertyKey<Boolean> KEY_NODE_HIGHLIGHTED = PropertyKey.createWithOut(
            "tree_node_highlighted", Boolean.class, false);
    /* */
    public static final String TREECHILD_INSERT = "treechild_insert";
    public static final String TREECHILD_WILLREMOVE = "treechild_removed";
    /* like to propagate up to the root */
    private boolean forwardPropertyChange = true;
    /**
     * userdata for this node
     */
    protected T userData;
    /**
     * the childlist, will be created lazy
     * sorted implementations possible
     */
    protected List<MutableTreeNode> children;
    /**
     * the default treenode icon
     */
    protected static final Icon DEFAULT_TREE_ICON =
            new ImageIcon(TextureManager.def().loadImage(TextureManager.DEFAULT_ICON));
    /**
     * the current parent of this node
     */
    protected AbstractOMutableTreeNode parent;
    /** allow to add children to the node */
    protected boolean allowsChildren = true;
    /** should the checkbox be visible */
    protected boolean checkBoxVisible = true;
    /** node is checked */
    protected boolean checked = false;
    /**
     * current icon for the node
     */
    protected Icon node_icon = DEFAULT_TREE_ICON;

    public AbstractOMutableTreeNode() {
        this(null);
    }

    public AbstractOMutableTreeNode(T userData) {
        this.userData = userData;
    }

    protected abstract List<MutableTreeNode> createLazyChildList();

    public void setTreeNodeIcon(Icon icon) {
        this.node_icon = icon;
    }

    public Icon getTreeNodeIcon() {
        return node_icon;
    }

    public void setChecked(boolean value) {
        this.checked = value;
        /* check subnodes - digIn mode */
        if (children != null) {
            for (MutableTreeNode node : children) {
                ((AbstractOMutableTreeNode) node).setChecked(value);
            }
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setCheckBoxVisible(boolean value) {
        this.checkBoxVisible = value;
    }

    public boolean isCheckBoxVisible() {
        return checkBoxVisible;
    }

    public void setForwardPropertyChange(boolean forwardPropertyChange) {
        this.forwardPropertyChange = forwardPropertyChange;
    }

    public boolean isForwardPropertyChange() {
        return forwardPropertyChange;
    }

    public void addNode(MutableTreeNode node) {
        insert(node, getChildCount());
    }

    @Override
    public void insert(MutableTreeNode child, int index) {
        if (!allowsChildren) {
            throw new IllegalStateException("node does not allow children");
        }
        MutableTreeNode oldParent = (MutableTreeNode) child.getParent();
        /* remove from old parent */
        if (oldParent != null) {
            oldParent.remove(child);
        }
        child.setParent(this);
        /* lazy init */
        if (children == null) {
            children = createLazyChildList();
        }
        children.add(index, child);
        /* fire up to the root */
        firePropertyChange(TREECHILD_INSERT, null, child);
    }

    @Override
    public void remove(int index) {
        MutableTreeNode child = (MutableTreeNode) getChildAt(index);
        /* before remove, for reading index for the listeners */
        firePropertyChange(TREECHILD_WILLREMOVE, null, child);
        /* */
        children.remove(index);
        child.setParent(null);
    }

    @Override
    public void remove(MutableTreeNode node) {
        int idx = getIndex(node);
        if (idx != -1) {
            remove(idx);
        }
    }

    public int getLevel() {
        TreeNode ancestor;
        int levels = 0;
        ancestor = this;
        while ((ancestor = ancestor.getParent()) != null) {
            levels++;
        }
        return levels;
    }

    /**
     * remove this node from the parent if node childs where attached
     * @param node
     */
    public void removeEmpty(Object data) {
        remove(data);
        /* if no childs */
        if (getChildCount() == 0) {
            removeFromParent();
        }
    }

    public void remove(Object data) {
        remove(searchNode(data));
    }

    public void removeAllChildren() {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            remove(i);
        }
    }

    @Override
    public void setUserObject(Object object) {
        userData = (T) object;
    }

    @Override
    public void removeFromParent() {
        MutableTreeNode parentNode = (MutableTreeNode) getParent();
        if (parentNode != null) {
            parentNode.remove(this);
        }
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        parent = (AbstractOMutableTreeNode) newParent;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        if (children != null) {
            return children.indexOf(node);
        }
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return allowsChildren;
    }

    @Override
    public boolean isLeaf() {
        return (getChildCount() == 0);
    }

    @Override
    public Enumeration children() {
        if (children == null) {
            return Collections.enumeration(Collections.EMPTY_LIST);
        } else {
            return Collections.enumeration(children);
        }
    }

    public T getUserObject() {
        return (T) userData;
    }

    public String getDisplayName() {
        if (userData != null) {
            return userData.toString();
        }
        return "no displayname";
    }

    public AbstractOMutableTreeNode searchNode(Object userData) {
        AbstractOMutableTreeNode ret = null;
        if (children != null) {
            for (MutableTreeNode child : children) {
                AbstractOMutableTreeNode wchild = (AbstractOMutableTreeNode) child;
                if (wchild.getUserObject() != null && wchild.getUserObject().equals(userData)) {
                    ret = wchild;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Returns the path from the root, to get to this node.  The last
     * element in the path is this node.
     *
     * @return an array of TreeNode objects giving the path, where the
     *         first element in the path is the root and the last
     *         element is this node.
     */
    public TreeNode[] getPath() {
        return getPathToRoot(this, 0);
    }

    public boolean isNodeAncestor(TreeNode anotherNode) {
        if (anotherNode == null) {
            return false;
        }
        TreeNode ancestor = this;
        do {
            if (ancestor == anotherNode) {
                return true;
            }
        } while ((ancestor = ancestor.getParent()) != null);
        return false;
    }

    public boolean isNodeDescendant(AbstractOMutableTreeNode anotherNode) {
        if (anotherNode == null) {
            return false;
        }
        return anotherNode.isNodeAncestor(this);
    }

    public TreeNode searchNode(TreeNode node, Class<? extends TreeNode> clazz) {
        if (clazz.isInstance(node)) {
            return node;
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode ret = searchNode(node.getChildAt(i), clazz);
                if (clazz.isInstance(ret)) {
                    return ret;
                }
            }
        }
        return null;
    }

    public AbstractOMutableTreeNode searchToRoot(Class clazz) {
        AbstractOMutableTreeNode ancestor = this;
        do {
            if (clazz.isInstance(ancestor.getUserObject())) {
                return ancestor;
            }
        } while ((ancestor = (AbstractOMutableTreeNode) ancestor.getParent()) != null);
        return null;
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     *
     * @param aNode  the TreeNode to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node
     */
    protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[] retNodes;

        /* Check for null, in case someone passed in a null node, or
        they passed in an element that isn't rooted at root. */
        if (aNode == null) {
            if (depth == 0) {
                return null;
            } else {
                retNodes = new TreeNode[depth];
            }
        } else {
            depth++;
            retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        /* fire to listeners */
        PropertyChangeEvent evt = null;
        if (propertyChangeSupport != null) {
            super.firePropertyChange(evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue));
        }
        /* fire up to the root */
        if (parent != null && forwardPropertyChange) {
            parent.fireChildPropertyChange(evt != null ? evt : new PropertyChangeEvent(this, propertyName, oldValue, newValue));
        }
    }

    protected void fireChildPropertyChange(final PropertyChangeEvent evt) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.firePropertyChange(evt);
        }
        /* fire to root */
        if (parent != null) {
            parent.fireChildPropertyChange(evt);
        }
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public void dispose() {
        super.dispose();
        /* clean up data */
        userData = null;
        /* clean up childrens */
        if (children != null) {
            children.clear();
            children = null;
        }
        parent = null;
    }
}