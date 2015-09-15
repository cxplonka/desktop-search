/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author cplonka
 */
public class DateUtil {

    public static Date parseEXIFFormat(String dateString) {
        /* from metadataextractor */
        // This seems to cover all known Exif date strings
        // Note that "    :  :     :  :  " is a valid date string according to the Exif spec (which means 'unknown date'): http://www.awaresystems.be/imaging/tiff/tifftags/privateifd/exif/datetimeoriginal.html
        String datePatterns[] = {
            "yyyy:MM:dd HH:mm:ss",
            "yyyy:MM:dd HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy.MM.dd HH:mm"};
        for (String datePattern : datePatterns) {
            try {
                /* current timezone */
                DateFormat parser = new SimpleDateFormat(datePattern);
                return parser.parse(dateString);
            } catch (ParseException ex) {
                // simply try the next pattern
            }
        }
        return null;
    }
}