/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.timeline;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class TimeLine {

    public static enum Resolution {

        DAYS, MONTH, YEAR
    }
    /* */
    public final long fromDate;
    public final long toDate;
    private final long _between;
    private final int[] _dateCount;
    /* current timezone */
    private final Calendar t1 = Calendar.getInstance();
    private final Calendar t2 = Calendar.getInstance();
    private final Resolution resolution = Resolution.DAYS;

    public TimeLine(long fromDate, long toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        _between = transform(fromDate, toDate);
        /* int precision is enough for index */
        _dateCount = new int[(int) _between + 1];
    }

    private long transform(long startDate, long endDate) {
        switch (resolution) {
            case DAYS:
                return (endDate - startDate) / (24 * 60 * 60 * 1000);
            case MONTH:
                t1.setTimeInMillis(startDate);
                t2.setTimeInMillis(endDate);
                int yearDiff = t2.get(Calendar.YEAR) - t1.get(Calendar.YEAR);
                return t2.get(Calendar.MONTH) - t1.get(Calendar.MONTH) + 12 * yearDiff;
            case YEAR:
                t1.setTimeInMillis(startDate);
                t2.setTimeInMillis(endDate);
                return t2.get(Calendar.YEAR) - t1.get(Calendar.YEAR);
        }
        throw new IllegalArgumentException("Resolution not supported.");
    }

    public void addDate(long date) {
        if (_dateCount != null) {
            if (date < fromDate || date > toDate) {
                throw new IllegalArgumentException("Date out of Range!");
            }
            _dateCount[(int) transform(fromDate, date)] += 1;
        }
    }

    private long toDate(int offset) {
        switch (resolution) {
            case DAYS:
                return fromDate + (offset * (24 * 60 * 60 * 1000));
            case MONTH:
                t1.setTimeInMillis(fromDate);
                t1.add(Calendar.MONTH, offset);
                return t1.getTimeInMillis();
            case YEAR:
                t1.setTimeInMillis(fromDate);
                t1.add(Calendar.YEAR, offset);
                return t1.getTimeInMillis();
        }
        throw new IllegalArgumentException("Resolution not supported.");
    }

    public int[] getFrequencyReference(){
        return _dateCount;
    }
    
    public void random() {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int idx = r.nextInt(_dateCount.length);
            _dateCount[idx] += 1;
        }
    }

    public void print() {
        if (_dateCount != null) {
            for (int i = 0; i < _dateCount.length; i++) {
                System.out.println(new Date(toDate(i)) + " | " + _dateCount[i]);
            }
        }
    }
}