/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OFileDateFilter extends OntologyNode implements IQueryGenerator {

    public static final String PROPERTY_DATE_FROM = "property_date_from";
    public static final String PROPERTY_DATE_TO = "property_date_to";
    /* */
    @XmlAttribute
    private Date fromDate = new GregorianCalendar(2010, 4, 1).getTime();
    @XmlAttribute
    private Date toDate = new Date();
    /* cache query */
    protected Query query;

    public OFileDateFilter() {
        this("Date Filter");
    }
    
    public OFileDateFilter(String name) {
        super(name);
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            query = LongPoint.newRangeQuery(getLuceneField(),
                    fromDate.getTime(), toDate.getTime());
        }
        return query;
    }

    @Override
    public String getLuceneField() {
        return LastModifiedField.NAME;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setFromDate(Date fromDate) {
        if (!this.fromDate.equals(fromDate)) {            
            query = null;
            firePropertyChange(PROPERTY_DATE_FROM, this.fromDate, this.fromDate = fromDate);
        }
    }

    public void setToDate(Date toDate) {
        if (!this.toDate.equals(toDate)) {            
            query = null;
            firePropertyChange(PROPERTY_DATE_TO, this.toDate, this.toDate = toDate);
        }
    }
}