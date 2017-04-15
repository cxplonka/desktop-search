/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.slideshow;

import com.semantic.ApplicationContext;
import com.semantic.util.lazy.LazyList;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ShowSlideShowAction extends AbstractAction {

    private SlideShowDialog dialog;
    private LazyList<Document> lazyList;

    public ShowSlideShowAction() {
        super("show_slideshow");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (dialog == null) {
            ApplicationContext ctx = ApplicationContext.instance();
            dialog = new SlideShowDialog(ctx.get(ApplicationContext.MAIN_VIEW));
        }
        dialog.setModel(lazyList);
        dialog.setVisible(true);
    }
    
    public void setModel(LazyList<Document> lazyList){
        this.lazyList = lazyList;
    }
}