/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.lazy;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Daniel Pfeifer
 */
public abstract class LazyListTableModel<T> extends AbstractTableModel implements OnLoadListener {

    private final LazyList<T> lazyList;

    public LazyListTableModel(LazyList<T> lazyList) {
        this.lazyList = lazyList;
        this.lazyList.addOnLoadListener(this);
    }

    @Override
    public void elementLoaded(final OnLoadEvent event) {
        /* push to edt */
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (TableModelListener listener : getTableModelListeners()) {
                    listener.tableChanged(new TableModelEvent(
                            LazyListTableModel.this, event.getIndexInterval().getStart(),
                            event.getIndexInterval().getEnd()));
                }
            }
        });
    }

    protected LazyList<T> getLazyList() {
        return lazyList;
    }

    @Override
    public int getRowCount() {
        return getLazyList().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (getLazyList().isLoaded(rowIndex)) {
            return getColumnValue(rowIndex, columnIndex, getLazyList().get(rowIndex));
        } else {
            getLazyList().getAsynchronous(rowIndex);
            return getDummyValueAt(rowIndex, columnIndex);
        }
    }

    public Object getDummyValueAt(int rowIndex, int columnIndex) {
        return "";
    }

    public abstract Object getColumnValue(int rowIndex, int columnIndex, T listElement);
}