/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test;

import com.semantic.util.swing.jtree.CheckBoxTreeCellRenderer;
import com.semantic.util.swing.jtree.CheckBoxJTree;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.TristateCheckBox;
import com.semantic.model.OntologyNode;
import com.semantic.swing.tree.SemanticTreeModel;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import com.semantic.util.swing.jtree.TreeExpansionState;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.jdesktop.swingx.JXHyperlink;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TestTree extends JFrame {

    private AbstractOMutableTreeNode root;
    
    public TestTree() {
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout());
        /* */
        OntologyNode model = new OntologyNode("root");
        model.addNode(new OntologyNode("1"));
        model.addNode(new OntologyNode("2"));
        /* same instance, different wrapper */
        OntologyNode same = new OntologyNode("same");
        same.set(TreeExpansionState.TREE_NODE_CHECKED, true);
        model.addNode(same);
        model.addNode(new OntologyNode("3"));
        model.addNode(same);
        OntologyNode other = new OntologyNode("other");
        other.addNode(new OntologyNode("bla"));
        other.addNode(same);
        model.addNode(other);

//        model.addPropertyChangeListener(TreeExpansionState.TREE_NODE_CHECKED.getName(), new PropertyChangeListener() {
//
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                System.out.println(String.format("checked: %s - %s", 
//                        ((OntologyNode)evt.getSource()).getDisplayName(), 
//                        evt.getNewValue()));
//            }
//        });

        CheckBoxJTree tree = new CheckBoxJTree();
        tree.setModel(new SemanticTreeModel(root = new ListenTreeNode(model)));
        /* keep in sync - for checkboxtree */
//        TreeCheckingSynchronizer sync = new TreeCheckingSynchronizer();
//        tree.getCheckBoxTreeSelectionModel().addTreeSelectionListener(sync);

        tree.setCellRenderer(new CheckBoxTreeCellRenderer());        
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        add(tree, BorderLayout.CENTER);
        add(new JXHyperlink(new CheckAction()), BorderLayout.SOUTH);
        
        TristateCheckBox c = new TristateCheckBox();
        c.setState(TristateCheckBox.STATE_MIXED);
        add(c, BorderLayout.WEST);
    }

    class CheckAction extends AbstractAction {

        public CheckAction() {
            super("test");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            checked(root);
        }

        private void checked(TreeNode node) {
            if (node instanceof AbstractOMutableTreeNode) {
                AbstractOMutableTreeNode n = (AbstractOMutableTreeNode) node;
                if (n.isChecked()) {
                    System.out.println("checked - " + n);
                }
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                checked(node.getChildAt(i));
            }
        }
    }
    
    public static void main(String[] arg) throws Exception{
        
        
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TestTree test = new TestTree();
                test.setVisible(true);
            }
        });
    }
}