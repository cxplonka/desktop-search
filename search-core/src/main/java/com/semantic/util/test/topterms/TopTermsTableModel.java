/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test.topterms;

import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.lucene.fields.image.CommentField;
import com.semantic.lucene.fields.image.ExifMakeField;
import com.semantic.lucene.fields.image.ExifModelField;
import static com.semantic.lucene.handler.ImageLuceneFileHandler.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.table.AbstractTableModel;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TopTermsTableModel extends AbstractTableModel {

    private static final Map<String, String> _description = new HashMap<String, String>();

    static {
        _description.put(CommentField.NAME, "Comments");
        _description.put(FileExtField.NAME, "Extension");
        _description.put(ExifMakeField.NAME, "Producer");
        _description.put(ExifModelField.NAME, "Camera Model");
        _description.put(MimeTypeField.NAME, "Mime Type");
    }
    private List<TopTerms> topTerms = new ArrayList<TopTerms>();
    private int rowCount = 0;

    public TopTermsTableModel() {
        super();
    }

    public void setQuery(IndexSearcher searcher, Query rootQuery) {
        rowCount = 0;

//        try {
//            NumberRangeCollector c = new NumberRangeCollector(FIELD_FILE_LAST_MODIFIED);
//            searcher.search(rootQuery, c);
//            System.out.println(c.getRange());
//            long from = (long) c.getRange().getLowerBound();
//            long to = (long) c.getRange().getUpperBound();
//            System.out.println(new Date(from) + " | " + new Date(to));
//
//            final TimeLine line = new TimeLine(from, to);
//            searcher.search(rootQuery, new NumberRangeCollector(FIELD_FILE_LAST_MODIFIED) {
//                @Override
//                public void collect(int i, Number number) {
//                    line.addDate(number.longValue());
//                }
//            });
//            line.print();
//
//        } catch (Throwable ex) {
//            Logger.getLogger(TopTermsTableModel.class.getName()).log(Level.SEVERE, null, ex);
//        }

        topTerms.clear();
        for (Map.Entry<String, String> _field : _description.entrySet()) {
            String field = _field.getKey();
            try {
                TopTermsCollector collector = new TopTermsCollector(field);
                searcher.search(rootQuery, collector);
                /* sorted terms, by frequency */
                TreeMap<Integer, String> terms = collector.getTopTerms();
                if (!terms.isEmpty()) {
                    List<TopTerm> list = new ArrayList<TopTerm>();
                    for (Map.Entry<Integer, String> entry : terms.descendingMap().entrySet()) {
                        list.add(new TopTerm(field, entry.getValue(), entry.getKey()));
                    }
                    topTerms.add(new TopTerms(field, list));
                    rowCount = Math.max(rowCount, terms.size());
                }
            } catch (IOException ex) {
            }
        }
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public String getColumnName(int column) {
        return _description.get(topTerms.get(column).getField());
    }

    @Override
    public int getColumnCount() {
        return topTerms.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TopTerms terms = topTerms.get(columnIndex);
        if (rowIndex < terms.getTerms().size()) {
            return terms.getTerms().get(rowIndex);
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return TopTerm.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    class TopTerms {

        String field;
        List<TopTerm> terms;

        public TopTerms(String field, List<TopTerm> terms) {
            this.field = field;
            this.terms = terms;
        }

        public String getField() {
            return field;
        }

        public List<TopTerm> getTerms() {
            return terms;
        }
    }
}