/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.semantic.util.swing.JStackedBox;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.jdesktop.swingx.JXCollapsiblePane;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class StackBoxTest extends JFrame {

    public StackBoxTest() throws HeadlessException {
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout());
        
        JStackedBox left = new JStackedBox();
        left.addBox("left", new JScrollPane(new JTree()));
        add(left, BorderLayout.WEST);
        
        JStackedBox right = new JStackedBox();
        right.addBox("right", new JTree());
        add(right, BorderLayout.EAST);
        
        add(new JLabel("middle"), BorderLayout.CENTER);
    }

    public static void main(String[] arg) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                StackBoxTest test = new StackBoxTest();
                test.setVisible(true);
            }
        });
    }
}
