/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.semantic.model.OGroup;
import com.semantic.model.OntologyNode;
import com.semantic.model.filter.OFileDateFilter;
import com.semantic.model.filter.OFileSizeFilter;
import com.semantic.model.filter.OFixedFileDateFilter;
import com.semantic.model.filter.OGeoHashFilter;
import com.semantic.model.filter.OHasGPSFilter;
import com.semantic.model.filter.OHasUserTagFilter;
import com.semantic.model.filter.OImageAspectRatioFilter;
import com.semantic.model.filter.OImageResolutionFilter;
import com.semantic.model.filter.OKeywordFilter;
import com.semantic.model.filter.OMusicFilter;
import com.semantic.model.filter.OPowerPointFilter;
import com.semantic.model.filter.OVectorFileFilter;
import com.semantic.swing.tree.IDropAllowed;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import com.semantic.util.image.TextureManager;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <T>
 */
public class OGroupTreeNode<T extends OGroup> extends ONodeTreeNode<T> implements IDropAllowed {

    public static final Map<String, Class<? extends OntologyNode>> FILTERS
            = new TreeMap<String, Class<? extends OntologyNode>>();

    static {
        FILTERS.put("Group", OGroup.class);
        FILTERS.put("Music Filter", OMusicFilter.class);
        FILTERS.put("Vector Graphics Filter", OVectorFileFilter.class);
        FILTERS.put("File Date Filter", OFileDateFilter.class);
        FILTERS.put("File Size Filter", OFileSizeFilter.class);
        FILTERS.put("Image Aspect Ratio Filter", OImageAspectRatioFilter.class);
        FILTERS.put("Image Resolution Filter", OImageResolutionFilter.class);
        FILTERS.put("Keyword Filter", OKeywordFilter.class);
        FILTERS.put("PowerPoint Filter", OPowerPointFilter.class);
        FILTERS.put("GeoHash Filter", OGeoHashFilter.class);
        FILTERS.put("Has User Tag Filter", OHasUserTagFilter.class);
        FILTERS.put("Has GPS Tag Filter", OHasGPSFilter.class);
        FILTERS.put("Fixed Date Filter", OFixedFileDateFilter.class);
    }
    /* */
    protected Action addFilterAction;

    public OGroupTreeNode(T node) {
        super(node);
    }

    @Override
    protected void initNode() {
        super.initNode();
        setTreeNodeIcon(new ImageIcon(
                TextureManager.def().loadImage("small/node_group.png")));
    }

    @Override
    public Action[] getActions() {
        Action[] ret = super.getActions();
        if (addFilterAction == null) {
            addFilterAction = new AddFilterAction();
        }
        /* merge actions */
        List<Action> actions = new ArrayList<Action>();
        actions.add(addFilterAction);
        actions.add(null);
        Collections.addAll(actions, ret);
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public boolean isDropAllowed(AbstractOMutableTreeNode childNode) {
        return true;
    }

    class AddFilterAction extends AbstractAction {

        public AddFilterAction() {
            super("Add Filter");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            /* add filter to current group */
            Object selectedValue = JOptionPane.showInputDialog(
                    (Component) ae.getSource(),
                    "Select Filter", "Filter",
                    JOptionPane.INFORMATION_MESSAGE, null,
                    FILTERS.keySet().toArray(), null);
            /* create filter */
            if (selectedValue != null) {
                Class<? extends OntologyNode> clazz = FILTERS.get(
                        selectedValue.toString());
                /* try to instantiate */
                try {
                    getUserObject().addNode(clazz.newInstance());
                } catch (Exception ex) {
                    //no success, ignore
                }
            }
        }
    }
}
