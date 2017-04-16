/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.fields.image;

import com.semantic.lucene.util.IFieldProperty;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.StoredField;

/**
 * GPS Exif image exchange format informations
 * GPS field TAG_GPS_LATITUDE in decimal degree (datatype - double)
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LatField implements IFieldProperty<Double> {

    public static final String NAME = "image_exif_gps_lat";

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void add(Document doc, Double value) {
        doc.add(new DoublePoint(getName(), value));
        doc.add(new StoredField(getName(), value));
    }
}
