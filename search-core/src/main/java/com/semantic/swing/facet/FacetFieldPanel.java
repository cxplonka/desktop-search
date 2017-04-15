/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.facet;

import javax.swing.DefaultListModel;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.LabelAndValue;
//import org.apache.lucene.facet.search.FacetResultNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FacetFieldPanel extends javax.swing.JPanel {

    private final DefaultListModel listModel = new DefaultListModel();

    public FacetFieldPanel() {
        this("Title");
    }

    public FacetFieldPanel(String title) {
        super();
        initComponents();
        setTitle(title);
        jList1.setModel(listModel);
    }

    public void setResult(FacetResult result) {
        listModel.removeAllElements();
        if (result != null) {
            for (LabelAndValue lv : result.labelValues) {
                listModel.addElement(String.format("%s [%s]",
                        lv.label, lv.value));
            }
        }
    }

    public void setTitle(String value) {
        ltitle.setText(value);
        lsmalltitle.setText(value);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ltitle = new javax.swing.JLabel();
        lsmalltitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        ltitle.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        ltitle.setText("Title");

        lsmalltitle.setText("small");
        lsmalltitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 50, 1, 1));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jScrollPane1.setBorder(null);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ltitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lsmalltitle)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ltitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lsmalltitle)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lsmalltitle;
    private javax.swing.JLabel ltitle;
    // End of variables declaration//GEN-END:variables
}
