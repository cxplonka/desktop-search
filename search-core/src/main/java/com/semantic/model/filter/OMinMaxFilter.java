/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.data.xml.NumberXMLAdapter;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian
 */
public abstract class OMinMaxFilter<T extends Number> extends OntologyNode implements IQueryGenerator {

    public static final String PROPERTY_FILTER_MIN = "property_filter_min";
    public static final String PROPERTY_FILTER_MAX = "property_filter_max";
    @XmlAttribute
    @XmlJavaTypeAdapter(NumberXMLAdapter.class)
    private T minSize;
    @XmlAttribute
    @XmlJavaTypeAdapter(NumberXMLAdapter.class)
    private T maxSize;
    @XmlAttribute
    private Class<T> clazz;
    /* cache query */
    protected Query query;

    public OMinMaxFilter() {
        super("Min/Max Filter");
    }

    public OMinMaxFilter(Class<T> clazz, T minSize, T maxSize) {
        this();
        this.clazz = clazz;
        this.maxSize = maxSize;
        this.minSize = minSize;
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            if (Integer.class.equals(clazz)) {
                query = NumericRangeQuery.newIntRange(getLuceneField(),
                        minSize.intValue(), maxSize.intValue(), true, true);
            } else if (Float.class.equals(clazz)) {
                query = NumericRangeQuery.newFloatRange(getLuceneField(),
                        minSize.floatValue(), maxSize.floatValue(), true, true);
            } else if (Long.class.equals(clazz)) {
                query = NumericRangeQuery.newLongRange(getLuceneField(),
                        minSize.longValue(), maxSize.longValue(), true, true);
            } else if (Double.class.equals(clazz)) {
                query = NumericRangeQuery.newDoubleRange(getLuceneField(),
                        minSize.doubleValue(), maxSize.doubleValue(), true, true);
            } else {
                throw new UnsupportedOperationException("not supported data type!");
            }
        }
        return query;
    }

    public void setMaxSize(T maxSize) {
        if (!this.maxSize.equals(maxSize)) {
            query = null;
            firePropertyChange(PROPERTY_FILTER_MAX, this.maxSize, this.maxSize = maxSize);
        }
    }

    public void setMinSize(T minSize) {
        if (!this.minSize.equals(minSize)) {
            query = null;
            firePropertyChange(PROPERTY_FILTER_MIN, this.minSize, this.minSize = minSize);
        }
    }

    public T getMaxSize() {
        return maxSize;
    }

    public T getMinSize() {
        return minSize;
    }

    public Class<T> getType() {
        return clazz;
    }
}