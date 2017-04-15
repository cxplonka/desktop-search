/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.action;

import com.semantic.ApplicationContext;
import com.semantic.model.ModelStore;
import com.semantic.model.OModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ExportModelAction extends AbstractAction {

    private JFileChooser chooser;

    public ExportModelAction() {
        super("Export");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (chooser == null) {
            chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new FileNameExtensionFilter("[xml] - Ontology", "xml"));
        }
        int ret = chooser.showSaveDialog((Component) e.getSource());
        if (ret == JFileChooser.APPROVE_OPTION) {
            /* export */
            OModel model = ApplicationContext.instance().get(ApplicationContext.MODEL);
            ModelStore.store(model, chooser.getSelectedFile());
        }
    }
}