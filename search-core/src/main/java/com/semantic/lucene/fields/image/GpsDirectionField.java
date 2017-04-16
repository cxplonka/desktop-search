/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.fields.image;

import com.semantic.lucene.util.IFieldProperty;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;

/**
 * GPS field TAG_GPS_IMG_DIRECTION in degree (datatype - int)
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class GpsDirectionField implements IFieldProperty<Integer> {

    public static final String NAME = "image_exif_gps_img_direction";

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void add(Document doc, Integer value) {
        doc.add(new IntPoint(getName(), value));
        doc.add(new StoredField(getName(), value));
    }
}
