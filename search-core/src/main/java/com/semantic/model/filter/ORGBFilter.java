/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.handler.ImageLuceneFileHandler;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ORGBFilter extends OntologyNode implements IQueryGenerator {

    public static final String PROPERTY_RANGE_RGB = "property_range_rgb";
    public static final String PROPERTY_RGB_VECTOR = "property_mean_rgb";
    @XmlAttribute
    private int[] rgbVector;
    @XmlAttribute
    private int rangeRGB = 50;
    /* cache query */
    protected Query query;

    public ORGBFilter() {
        this("Similar Filter");
    }

    public ORGBFilter(String name) {
        super(name);
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            /* root query */
            BooleanQuery.Builder root = new BooleanQuery.Builder();
            for (int i = 0; i < rgbVector.length; i++) {
                root.add(IntPoint.newRangeQuery(
                        getLuceneField() + i,
                        rgbVector[i] - rangeRGB, rgbVector[i] + rangeRGB), 
                        Occur.MUST);
            }
            query = root.build();
        }
        return query;
    }

    @Override
    public String getLuceneField() {
        return ImageLuceneFileHandler.COLORMEAN;
    }

    public void setRangeRGB(int rangeRGB) {
        if (this.rangeRGB != rangeRGB) {
            query = null;
            firePropertyChange(PROPERTY_RANGE_RGB, this.rangeRGB, this.rangeRGB = rangeRGB);
        }
    }

    public int getRangeRGB() {
        return rangeRGB;
    }

    public void setMeanRGB(int[] rgbVector) {
        if (!Arrays.equals(rgbVector, this.rgbVector)) {
            query = null;
            firePropertyChange(PROPERTY_RGB_VECTOR, this.rgbVector, this.rgbVector = rgbVector);
        }
    }

    public int[] getRGBVector() {
        return rgbVector;
    }
}