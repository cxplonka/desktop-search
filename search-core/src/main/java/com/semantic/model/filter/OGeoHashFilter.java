/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.google.georesponsev1.jaxb.GeocodeResponse;
import com.google.georesponsev1.jaxb.Viewport;
import com.semantic.lucene.fields.image.LatField;
import com.semantic.lucene.fields.image.LonField;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import com.semantic.util.swing.GeoCoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 *
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OGeoHashFilter extends OntologyNode implements IQueryGenerator {

    protected static final Logger log = Logger.getLogger(OGeoHashFilter.class.getName());
    /* */
    public static final String PROPERTY_GEO_PLACENAME = "property_geo_placename";
    public static final double KILOMETERS_PER_DEGREE = 111.3171;
    /* */
    @XmlAttribute
    private double southWestLat = 0;
    @XmlAttribute
    private double southWestLng = 0;
    @XmlAttribute
    private double northEastLat = 0;
    @XmlAttribute
    private double northEastLng = 0;
    @XmlAttribute
    private String placeName = "unknown";
    @XmlElement
    private String description = "unknown";
    /* cache query */
    protected Query query;

    public OGeoHashFilter() {
        super("Lat/Lon Filter");
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            /* root query */            
            BooleanQuery.Builder root = new BooleanQuery.Builder();
            root.add(DoublePoint.newRangeQuery(LatField.NAME,
                    southWestLat, northEastLat), Occur.MUST);
            root.add(DoublePoint.newRangeQuery(LonField.NAME,
                    southWestLng, northEastLng), Occur.MUST);
            query = root.build();
        }
        return query;
    }

    @Override
    public String getLuceneField() {
        return LatField.NAME;
    }

    public void setPlaceName(String placeName) {
        if (!this.placeName.equalsIgnoreCase(placeName)) {
            try {
                GeocodeResponse response = GeoCoder.instance().resolve(placeName);
                Viewport vp = response.getResult().getGeometry().getViewport();
                southWestLat = vp.getSouthwest().getLat().doubleValue();
                southWestLng = vp.getSouthwest().getLng().doubleValue();
                northEastLat = vp.getNortheast().getLat().doubleValue();
                northEastLng = vp.getNortheast().getLng().doubleValue();
                description = GeoCoder.generateDescription(response);
            } catch (Exception ex) {
                log.log(Level.WARNING, "Can not resolve placename!", ex);
            }
            query = null;
            firePropertyChange(PROPERTY_GEO_PLACENAME, this.placeName, this.placeName = placeName);
        }
    }

    public String getPlaceName() {
        return placeName;
    }

    public double getSouthWestLat() {
        return southWestLat;
    }

    public double getSouthWestLng() {
        return southWestLng;
    }

    public double getNorthEastLat() {
        return northEastLat;
    }

    public double getNorthEastLng() {
        return northEastLng;
    }

    public String getDescription() {
        return description;
    }
}