package com.example.wattanasuk_suvisa_s2532981.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Validity {
    private Date start = null;
    private Date end = null;

    public void setStart(String text) { start = toDate(text); }
    public void setEnd(String text) { end = toDate(text); }


    public String getStartText() { return toText(start); }
    public String getEndText() { return toText(end); }


    public long getDays() {
        if (start == null || end == null) return -1;
        return (end.getTime() - start.getTime()) / (1000L * 60 * 60 * 24);
    }


    private Date toDate(String text) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.UK).parse(text.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String toText(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("EEE d MMM yyyy, HH:mm", Locale.UK).format(date);
    }
}
