package com.example.wattanasuk_suvisa_s2532981.model;


import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class SituationParser {

    public ArrayList<Situation> parse(String xml, String category) {
        ArrayList<Situation> list = new ArrayList<>();
        if (xml == null || xml.isEmpty()) {
            return list;
        }

        Situation current = null;
        boolean insideTo = false;
        boolean insideComment = false;
        String lastName = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    String tag = xpp.getName();


                    if (tag.equals("situationRecord")) {
                        current = new Situation();
                        current.setCategory(category);
                        current.setId(xpp.getAttributeValue(null, "id"));
                    }
                    else if (current == null) {
                        // before the first record: ignore
                    }


                    else if (tag.equals("generalPublicComment")) {
                        insideComment = true;
                    }
                    else if (tag.equals("to")) {
                        insideTo = true;
                    }


                    else if (tag.equals("overallStartTime")) {
                        current.getValidity().setStart(xpp.nextText());
                    }
                    else if (tag.equals("overallEndTime")) {
                        current.getValidity().setEnd(xpp.nextText());
                    }


                    else if (tag.equals("numberOfLanesRestricted")) {
                        current.getImpact().setLanesRestricted(toWholeNumber(xpp.nextText()));
                    }
                    else if (tag.equals("numberOfOperationalLanes")) {
                        current.getImpact().setLanesOpen(toWholeNumber(xpp.nextText()));
                    }
                    else if (tag.equals("delaysType")) {
                        current.getImpact().setDelays(xpp.nextText());
                    }


                    else if (tag.equals("roadOrCarriagewayOrLaneManagementType")
                            || tag.equals("abnormalTrafficType")
                            || tag.equals("roadMaintenanceType")) {
                        current.setType(xpp.nextText());
                    }


                    else if (tag.equals("tpegDirection")) {
                        current.getLocation().setDirection(xpp.nextText());
                    }
                    else if (tag.equals("latitude")) {
                        double lat = toDecimalNumber(xpp.nextText());
                        if (insideTo) current.getLocation().getTo().setLatitude(lat);
                        else current.getLocation().getFrom().setLatitude(lat);
                    }
                    else if (tag.equals("longitude")) {
                        double lon = toDecimalNumber(xpp.nextText());
                        if (insideTo) current.getLocation().getTo().setLongitude(lon);
                        else current.getLocation().getFrom().setLongitude(lon);
                    }


                    else if (tag.equals("value")) {
                        String text = xpp.nextText();
                        if (insideComment) {
                            current.setDescription(text);
                        } else {
                            lastName = text;
                        }
                    }


                    else if (tag.equals("tpegIlcPointDescriptorType")
                            || tag.equals("tpegOtherPointDescriptorType")) {
                        String kind = xpp.nextText();
                        if (kind.equals("tpegIlcName1") || kind.equals("linkName")) {

                            if (current.getLocation().getRoad().isEmpty()) {
                                current.getLocation().setRoad(lastName);
                            }
                        }
                        else if (kind.equals("intersectionName") || kind.equals("nonLinkedPointName")) {

                            if (insideTo) current.getLocation().getTo().setName(lastName);
                            else current.getLocation().getFrom().setName(lastName);
                        }
                    }

                }

                else if (eventType == XmlPullParser.END_TAG) {
                    String tag = xpp.getName();
                    if (tag.equals("generalPublicComment")) {
                        insideComment = false;
                    }
                    else if (tag.equals("to")) {
                        insideTo = false;
                    }
                    else if (tag.equals("situationRecord") && current != null) {
                        list.add(current);
                        current = null;
                    }
                }

                eventType = xpp.next();
            }
        } catch (Exception e) {
            Log.e("Parsing", "Error while parsing: " + e);
        }
        return list;
    }

    private int toWholeNumber(String text) {
        try { return Integer.parseInt(text.trim()); }
        catch (Exception e) { return -1; }
    }

    private double toDecimalNumber(String text) {
        try { return Double.parseDouble(text.trim()); }
        catch (Exception e) { return 0; }
    }
}

