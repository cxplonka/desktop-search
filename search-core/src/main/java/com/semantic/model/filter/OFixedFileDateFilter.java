/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import java.util.Calendar;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OFixedFileDateFilter extends OntologyNode implements IQueryGenerator {

    public static enum DATE {

        TODAY("Today"),
        YESTERDAY("Yesterday"),
        THIS_WEEK("This week"),
        LAST_WEEK("Last week"),
        THIS_MONTH("This month"),
        LAST_MONTH("Last month"),
        THIS_YEAR("This year");

        private final String name;

        private DATE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @XmlAttribute
    private DATE date = DATE.TODAY;
    /* cache query */
    protected Query query;

    public OFixedFileDateFilter() {
        this(DATE.TODAY);
    }

    public OFixedFileDateFilter(DATE date) {
        super("OFixedFileDateFilter");
        setDate(date);
    }

    @Override
    public Query createQuery() {
        Calendar[] dates = generate(date);
        /* we dont cache query because of the dates */
        return LongPoint.newRangeQuery(
                getLuceneField(),
                dates[0].getTimeInMillis(),
                dates[1].getTimeInMillis());
    }

    static Calendar[] generate(DATE date) {
        Calendar to = Calendar.getInstance();
        Calendar from = Calendar.getInstance();
        to.set(Calendar.MINUTE, 0);
        to.set(Calendar.SECOND, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        //
        switch (date) {
            case TODAY:
                from.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case YESTERDAY:
                from.add(Calendar.DAY_OF_MONTH, -1);
                from.set(Calendar.HOUR_OF_DAY, 0);
                to.add(Calendar.DAY_OF_MONTH, -1);
                to.set(Calendar.HOUR_OF_DAY, 24);
                break;
            case THIS_WEEK:
                from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                from.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case LAST_WEEK:
                from.add(Calendar.WEEK_OF_MONTH, -2);
                from.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                from.set(Calendar.HOUR_OF_DAY, 24);

                to.add(Calendar.WEEK_OF_MONTH, -1);
                to.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                to.set(Calendar.HOUR_OF_DAY, 24);
                break;
            case THIS_MONTH:
                from.set(Calendar.DAY_OF_MONTH, 1);
                from.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case LAST_MONTH:
                from.add(Calendar.MONTH, -1);
                from.set(Calendar.DAY_OF_MONTH, 1);
                from.set(Calendar.HOUR_OF_DAY, 0);

                to.set(Calendar.DAY_OF_MONTH, 1);
                to.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case THIS_YEAR:
                from.set(Calendar.MONTH, 0);
                from.set(Calendar.DAY_OF_MONTH, 1);
                from.set(Calendar.HOUR_OF_DAY, 0);
                break;
        }
        return new Calendar[]{from, to};
    }

    @Override
    public String getName() {
        return date.toString();
    }

    @Override
    public String getLuceneField() {
        return LastModifiedField.NAME;
    }

    public void setDate(DATE date) {
        if (!this.date.equals(date)) {
            firePropertyChange("property_date", this.date, this.date = date);
        }
    }

    public DATE getDate() {
        return date;
    }
}
