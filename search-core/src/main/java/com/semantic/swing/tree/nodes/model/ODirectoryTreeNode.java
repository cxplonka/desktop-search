/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.semantic.ApplicationContext;
import com.semantic.lucene.IndexManager;
import com.semantic.crawler.filesystem.LuceneIndexWriteTask;
import com.semantic.model.ODirectoryNode;
import com.semantic.util.image.TextureManager;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ODirectoryTreeNode extends ONodeTreeNode<ODirectoryNode> {

    private Action indexAction;

    public ODirectoryTreeNode(ODirectoryNode userData) {
        super(userData);
    }

    @Override
    protected void initNode() {
        super.initNode();
        setTreeNodeIcon(new ImageIcon(
                TextureManager.def().loadImage("small/node_folder.png")));
    }

    @Override
    public Action[] getActions() {
        Action[] ret = super.getActions();
        if (indexAction == null) {
            indexAction = new ReIndexDirectoryAction();
        }
        /* merge actions */
        List<Action> actions = new ArrayList<Action>();
        actions.add(indexAction);
        actions.add(null);
        Collections.addAll(actions, ret);
        return actions.toArray(new Action[actions.size()]);
    }

    class ReIndexDirectoryAction extends AbstractAction {

        public ReIndexDirectoryAction() {
            super("Re-/Index Directory");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            /* start indexing */
            IndexManager lucene = ApplicationContext.instance().get(
                    IndexManager.LUCENE_MANAGER);
            /* push to task service */
            lucene.getTaskService().submit(new LuceneIndexWriteTask(
                    getUserObject().getDirectory()));
        }
    }
}