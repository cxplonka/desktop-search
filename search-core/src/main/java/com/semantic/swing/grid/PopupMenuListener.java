/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.grid;

import com.semantic.ApplicationContext;
import com.semantic.lucene.IndexManager;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.swing.MainFrame;
import com.semantic.swing.clipboard.TransferableFile;
import com.semantic.swing.layerui.ImageLayerUI;
import com.semantic.util.FileUtil;
import com.semantic.util.lazy.LazyList;
import com.semantic.util.swing.SwingUtils;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.lucene.document.Document;
import org.jdesktop.swingx.util.OS;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class PopupMenuListener extends MouseAdapter implements ListSelectionListener {

    private static final Logger log = Logger.getLogger(PopupMenuListener.class.getName());
    //
    private JPopupMenu popup;
    private final Action[] actions = new Action[]{
        new ViewDocumentAction(),
        null,
        new CopyClipBoardAction(),
        new DeleteDocumentsAction(),
        new CopyFilesAction(),
        null,
        new OpenExternalAction()
    };
    //
    private int[] docIDs = new int[0];
    private final LazyList<Document> documents;
    private final JComponent parent;

    public PopupMenuListener(JComponent parent, LazyList<Document> documents) {
        this.parent = parent;
        this.documents = documents;
        SwingUtils.registerKeyBoardAction(parent, actions[3]);//DeleteDocumentsAction
        SwingUtils.registerKeyBoardAction(parent, actions[0]);//ViewAction
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //user want see popup menu
        if (SwingUtilities.isRightMouseButton(e)) {
            if (popup == null) {
                popup = new JPopupMenu();
                for (Action action : actions) {
                    if (action != null) {
                        popup.add(action);
                    } else {
                        popup.addSeparator();
                    }
                }
            }
            //
            boolean enabled = docIDs.length > 0;
            for (Action action : actions) {
                if (action != null) {
                    action.setEnabled(enabled);
                }
            }
            if (enabled) {
                actions[0].setEnabled(parent instanceof ResultView);
            }
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
        //selected event come before clicked
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1) {
            actions[0].actionPerformed(null);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //table selection || result selection
        if (!e.getValueIsAdjusting()) {
            ListSelectionModel selectionModel = (ListSelectionModel) e.getSource();
            int min = selectionModel.getMinSelectionIndex();
            //min = max for one selection
            int max = selectionModel.getMaxSelectionIndex() + 1;
            //-1 = no selection
            if (min != -1) {
                docIDs = new int[max - min];
                //read selected document id's
                for (int i = min; i < max; i++) {
                    if (selectionModel.isSelectedIndex(i)) {
                        //fetch document
                        docIDs[i - min] = i;
                    } else {
                        docIDs[i - min] = -1;
                    }
                }
            } else {
                docIDs = new int[0];
            }
        }
    }

    class OpenExternalAction extends AbstractAction {

        public OpenExternalAction() {
            super("Open External");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (docIDs.length > 0) {
                Document doc = documents.get(docIDs[0]);
                try {
                    Desktop.getDesktop().open(new File(doc.get(FileNameField.NAME)));
                } catch (IOException ex) {
                    log.log(Level.SEVERE, "can not open external application!", ex);
                }
            }
        }
    }

    class ViewDocumentAction extends AbstractAction {

        public ViewDocumentAction() {
            super("View Document");
//            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (docIDs.length > 0) {
                ImageLayerUI ui = ((ResultView) parent).getImageLayerUI();
                ui.setEnabled(true);
                ui.setCurrentList(documents, docIDs[0]);
            }
        }
    }

    class DeleteDocumentsAction extends AbstractAction {

        public DeleteDocumentsAction() {
            super("Delete Document(s)");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IndexManager index = ApplicationContext.instance().get(IndexManager.LUCENE_MANAGER);
            //really!?
            if (docIDs.length > 0) {
                int ret = JOptionPane.showConfirmDialog(
                        SwingUtilities.windowForComponent(parent),
                        "Are you sure?",
                        String.format("Delete [%s] Document(s)", docIDs.length),
                        JOptionPane.WARNING_MESSAGE);
                if (ret == JOptionPane.OK_OPTION) {
                    //remove documents from filesystem and index
                    for (int id : docIDs) {
                        try {
                            if (id != -1) {
                                Document doc = documents.get(id);
                                File file = new File(doc.get(FileNameField.NAME));
                                index.entryDeleted(file);
                                file.delete();
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }
    }

    class CopyClipBoardAction extends AbstractAction {

        public CopyClipBoardAction() {
            super("Copy to Clipboard");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Clipboard clipboard
                    = Toolkit.getDefaultToolkit().getSystemClipboard();
            //
            TransferableFile transferable = new TransferableFile();
            for (int id : docIDs) {
                if (id != -1) {
                    Document doc = documents.get(id);
                    transferable.addFile(new File(doc.get(FileNameField.NAME)));
                }
            }
            clipboard.setContents(transferable, null);
        }
    }

    class CopyFilesAction extends AbstractAction {

        private JFileChooser chooser;

        public CopyFilesAction() {
            super("Copy Document(s)");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            File directory = null;
            //native dialog for mac, not so much problems
            if (OS.isMacOSX()) {
                MainFrame frame = ApplicationContext.instance().get(ApplicationContext.MAIN_VIEW);
                FileDialog fileDialog = new FileDialog(frame, "Select Directory", FileDialog.LOAD);
                fileDialog.setVisible(true);
                //construct directory path
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
                    //add to the ontology model node
                    directory = chooser.getSelectedFile();
                }
            }
            //copy selected files
            if (directory != null) {
                for (int id : docIDs) {
                    if (id != -1) {
                        Document doc = documents.get(id);
                        File currentFile = new File(doc.get(FileNameField.NAME));
                        try {
                            FileUtil.copyFile(currentFile, directory);
                        } catch (IOException e) {
                            log.log(Level.WARNING, String.format("Can not copy file[%s] to directory[%s].",
                                    currentFile, directory), e);
                        }
                    }
                }
            }
        }
    }
}
