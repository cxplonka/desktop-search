/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import com.google.georesponsev1.jaxb.AddressComponent;
import com.google.georesponsev1.jaxb.GeocodeResponse;
import com.google.georesponsev1.jaxb.Location;
import com.google.georesponsev1.jaxb.Result;
import com.google.georesponsev1.jaxb.Viewport;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * http://code.google.com/intl/de-DE/apis/maps/documentation/geocoding/v2/
 * 
 * limits:
 * https://developers.google.com/maps/documentation/geocoding/?hl=de-DE#Limits
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class GeoCoder {

    private static final String SERVICE = "http://maps.googleapis.com/maps/api/geocode/xml?address=%s&sensor=false";
    private static GeoCoder instance;
    private Unmarshaller unmarshaller;

    public synchronized static GeoCoder instance() {
        if (instance == null) {
            instance = new GeoCoder();
        }
        return instance;
    }

    private GeoCoder() {
    }

    public GeocodeResponse resolve(String placeName) throws IOException {
        try {
            if (unmarshaller == null) {
                JAXBContext jc = JAXBContext.newInstance("com.google.georesponsev1.jaxb");
                unmarshaller = jc.createUnmarshaller();
            }
            /* */
            GeocodeResponse response = (GeocodeResponse) unmarshaller.unmarshal(
                    new URL(String.format(SERVICE, URLEncoder.encode(placeName, "UTF-8"))));

            if (response.getStatus().equals("ZERO_RESULTS")) {
                throw new IOException("No Results found.");
            } else if (response.getStatus().equals("OVER_QUERY_LIMIT")) {
                throw new IOException("You are over the Query limit.");
            } else if (response.getStatus().equals("REQUEST_DENIED")) {
                throw new IOException("Request denied.");
            } else if (response.getStatus().equals("INVALID_REQUEST")) {
                throw new IOException("Invalid request.");
            }

            return response;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public static String generateDescription(GeocodeResponse response) {
        StringBuilder description = new StringBuilder();

        Result result = response.getResult();
        description.append(String.format("%s: %s\n", result.getType(), result.getFormattedAddress()));

        for (AddressComponent ac : result.getAddressComponent()) {
            description.append(String.format("\t%s: %s\n", ac.getType(), ac.getLongName()));
        }

        Location loc = result.getGeometry().getLocation();
        description.append(String.format("Location - lat: %s - lon: %s\n", loc.getLat(), loc.getLng()));

        Viewport v = result.getGeometry().getViewport();
        description.append("ViewPort:\n");
        description.append(String.format("\tsouth-west - lat: %s - lon: %s\n",
                v.getSouthwest().getLat(), v.getSouthwest().getLng()));
        description.append(String.format("\tnorth-east - lat: %s - lon: %s\n",
                v.getNortheast().getLat(), v.getNortheast().getLng()));

        return description.toString();
    }
}