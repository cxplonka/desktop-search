/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.l2fprod.common.util.OS;
import com.semantic.ApplicationContext;
import com.semantic.crawler.filesystem.LuceneIndexWriteTask;
import com.semantic.lucene.IndexManager;
import com.semantic.model.ODirectoryNode;
import com.semantic.model.OFileSystem;
import com.semantic.model.OntologyNode;
import com.semantic.swing.MainFrame;
import com.semantic.swing.action.RenameJPGAction;
import com.semantic.swing.tree.IActionNode;
import com.semantic.util.image.TextureManager;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FileSystemTreeNode extends OGroupTreeNode<OFileSystem> implements IActionNode {

    private static final Logger log = Logger.getLogger(FileSystemTreeNode.class.getName());
    private Action addDirectoryAction;
    private Action reIndexAction;
    private Action renameAction;
    private JFileChooser chooser;

    public FileSystemTreeNode(OFileSystem userData) {
        super(userData);
    }

    @Override
    protected void initNode() {
        super.initNode();
        setTreeNodeIcon(new ImageIcon(
                TextureManager.def().loadImage("small/node_filesystem.png")));
    }

    @Override
    public Action[] getActions() {
        if (addDirectoryAction == null) {
            addDirectoryAction = new AddSourceAction();
            reIndexAction = new ReIndexAllAction();
            renameAction = new RenameJPGAction();
        }
        return new Action[]{addDirectoryAction, null, reIndexAction, renameAction};
    }

    class AddSourceAction extends AbstractAction {

        public AddSourceAction() {
            super("Add Source");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            File directory = null;
            /* native dialog for mac, not so much problems */
            if (OS.isMacOSX()) {
                MainFrame frame = ApplicationContext.instance().get(ApplicationContext.MAIN_VIEW);
                FileDialog fileDialog = new FileDialog(frame, "Select Directory", FileDialog.LOAD);
                fileDialog.setVisible(true);
                /* construct directory path */
                if (fileDialog.getDirectory() != null) {
                    directory = new File(fileDialog.getDirectory() + fileDialog.getFile());
                }
            } else {
                if (chooser == null) {
                    chooser = new JFileChooser();
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                }
                int ret = chooser.showDialog((Component) ae.getSource(), "Select Directory");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    /* add to the ontology model node */
                    directory = chooser.getSelectedFile();
                }
            }
            /* */
            if (directory != null) {
                getUserObject().addNode(new ODirectoryNode(directory));
            }
        }
    }

    class ReIndexAllAction extends AbstractAction {

        public ReIndexAllAction() {
            super("Re-/Index All (Delete old Index)");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            int ret = JOptionPane.showConfirmDialog(
                    (Component) ae.getSource(),
                    "Are you sure?", this.getValue(NAME).toString(),
                    JOptionPane.YES_NO_OPTION);
            /* */
            if (ret == JOptionPane.YES_OPTION) {
                /* start indexing */
                IndexManager lucene = ApplicationContext.instance().get(
                        IndexManager.LUCENE_MANAGER);
                try {
                    lucene.reset();

                    log.info("Index deleted...");
                    /* collect directories */
                    List<File> sources = new ArrayList<File>();
                    OntologyNode node = getUserObject();
                    for (int i = 0; i < node.getNodeCount(); i++) {
                        OntologyNode child = node.getChildAt(i);
                        if (child instanceof ODirectoryNode) {
                            sources.add(((ODirectoryNode) child).getDirectory());
                        }
                    }
                    /* push to task service */
                    if (!sources.isEmpty()) {
                        lucene.getTaskService().submit(new LuceneIndexWriteTask(
                                sources.toArray(new File[sources.size()])));
                    }
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
