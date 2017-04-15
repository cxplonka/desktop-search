/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXSearchField;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TreeFiltering {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame f = new JFrame("test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout());

        final Node root = createTree();
        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        final JTree tree = new JTree(treeModel);

        final JXSearchField search = new JXSearchField("Filter");

        search.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String f = search.getText().trim();
                Node currentRoot = (Node) tree.getModel().getRoot();
                Enumeration<TreePath> en = currentRoot != null
                        ? tree.getExpandedDescendants(new TreePath(currentRoot.getPath())) : null;
                List<TreePath> pl = en != null ? Collections.list(en) : null;
                if (f.length() > 0) {
                    tree.setModel(new DefaultTreeModel(createFilteredTree(root, f)));
                } else {
                    tree.setModel(treeModel);
                }
                if (en != null) {
                    Node r = (Node) tree.getModel().getRoot();
                    if (r != null) {
                        restoreExpandedState(r, pl, tree);
                    }
                }
                tree.repaint();
            }
        });

        content.add(search, BorderLayout.NORTH);

        content.add(new JScrollPane(tree), BorderLayout.CENTER);

        tree.setCellRenderer(new Renderer(search));

        f.setContentPane(content);

        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private static class Renderer extends DefaultTreeCellRenderer {

        private String filter;
        private int matchOffset;
        private JXSearchField filterInput;
        private String label;

        public Renderer(JXSearchField filterInput) {
            this.filterInput = filterInput;
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean sel, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            filter = filterInput.getText().trim().toLowerCase();
            Node node = (Node) value;
            label = (String) node.getUserObject();
            if (filter.length() > 0 && label.toLowerCase().contains(filter)) {
                matchOffset = label.toLowerCase().indexOf(filter);
            } else {
                matchOffset = -1;
            }
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            if (matchOffset > -1) {
                int start = getLabelStart();
                String preMatch = label.substring(0, matchOffset);
                if (matchOffset < 0) {
                    throw new RuntimeException();
                } else if (filter.length() < 1) {
                    throw new RuntimeException();
                }

                String matchPart = label.substring(matchOffset, matchOffset + filter.length());

                int preWidth = g.getFontMetrics().stringWidth(preMatch);
                int matchWidth = g.getFontMetrics().stringWidth(matchPart);

                g.setColor(Color.yellow);
                g.fillRect(start + preWidth, 0, matchWidth, getHeight());
                g.setColor(getTextNonSelectionColor());
                Rectangle rect = g.getFontMetrics().getStringBounds(matchPart, g).getBounds();
                g.drawString(matchPart, start + preWidth + 1, -rect.y);
            }

        }
// copy from superclass

        private int getLabelStart() {
            Icon currentI = getIcon();
            if (currentI != null && getText() != null) {
                return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
            }
            return 0;
        }
    }

    static void restoreExpandedState(Node base, List<TreePath> exps, JTree tree) {
        if (base == null) {
            throw new NullPointerException();
        }
        if (wasExpanded(base, exps)) {
            tree.expandPath(new TreePath(base.getPath()));
        }
        int c = base.getChildCount();
        for (int i = 0; i < c; ++i) {
            Node n = (Node) base.getChildAt(i);
            restoreExpandedState(n, exps, tree);
        }
    }

    static boolean wasExpanded(Node n, List<TreePath> en) {
        if (n == null) {
            throw new NullPointerException();
        }
        for (TreePath path : en) {
            for (Object o : path.getPath()) {
                if (((Node) o).getUserObject() == n.getUserObject()) {
                    return true;
                }
            }
        }
        return false;
    }

    static Node createTree() {
        Node root = new Node("root");
        String[] words = new String[]{
            "lorem", "ipsum", "quux", "dolor", "foo", "bar", "foobar"};

        for (int i = 0; i < 1; ++i) {
            Node a = new Node(words[i % words.length] + " " + i);
            root.add(a);
            for (int j = 0; j < 1000; j++) {
                Node b = new Node(words[j % words.length] + " " + i + "." + j);
                a.add(b);
            }
        }

        return root;
    }

    static Node createTree2() {
        return node("root",
                node("functional", "lisp", "haskell", "caml"),
                node("imperative", "java", "c", "python", "ruby"),
                node("language creators",
                node("haskell", "wadler", "peyton-jones"),
                node("clojure", "hickey", "halloway", "sierra")));
    }

    static Node createTree3() {
        return node("root",
                node("Europe",
                node("Austria", "Vienna", "Linz", "Salzburg"),
                node("Germany", "Berlin", "Hamburg", "Munich"),
                node("UK", "London", "Yorkshire", "Oxford")),
                node("USA",
                node("California", "Los Angeles", "San Francisco", "San Diego"),
                node("New York", "New York City", "Buffalo", "Rochester")));
    }

    static Node node(String name, Object... children) {
        Node n = new Node(name);
        for (Object o : children) {
            if (o instanceof Node) {
                n.add((Node) o);
            } else {
                n.add(new Node(o));
            }
        }
        return n;
    }

    static Node createFilteredTree(Node parent, String filter) {
        int c = parent.getChildCount();
        Node fparent = new Node(parent.getUserObject());
        boolean matches = ((String) parent.getUserObject()).toLowerCase().contains(filter.toLowerCase());
        for (int i = 0; i < c; ++i) {
            Node n = (Node) parent.getChildAt(i);
            Node f = createFilteredTree(n, filter);
            if (f != null) {
                fparent.add(f);
                matches = true;
            }
        }
        return matches ? fparent : null;
    }

    static private class Node extends DefaultMutableTreeNode {

        public Node(Object userObject) {
            super(userObject);
        }

        public boolean isLeaf() {
            return false;
        }
    }
}
