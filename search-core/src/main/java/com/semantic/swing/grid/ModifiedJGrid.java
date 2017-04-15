/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.grid;

import com.guigarage.jgrid.JGrid;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ModifiedJGrid extends JGrid implements MouseListener {

    @Override
    public void addNotify() {
        super.addNotify();
        addMouseListener(this);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        removeMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int index = getCellAt(e.getPoint());
            if (index < 0) {
                getSelectionModel().clearSelection();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}