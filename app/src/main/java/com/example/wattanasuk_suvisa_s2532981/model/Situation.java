package com.example.wattanasuk_suvisa_s2532981.model;

import java.util.ArrayList;


public class Situation {

    // Which feed a situation came from
    public static final String INCIDENT = "Incident";
    public static final String ROADWORKS = "Roadworks";

    private String category = "";
    private String id = "";
    private String type = "";
    private String description = "";


    private ArrayList<String> descriptionParts = new ArrayList<>();

    private Location location = new Location();
    private Impact impact = new Impact();
    private Validity validity = new Validity();


    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }


    public void setDescription(String text) {
        description = text.trim();
        descriptionParts.clear();
        for (String part : description.split(" - ")) {
            if (!part.trim().isEmpty()) {
                descriptionParts.add(part.trim());
            }
        }
    }

    public ArrayList<String> getDescriptionParts() { return descriptionParts; }

    public Location getLocation() { return location; }
    public Impact getImpact() { return impact; }
    public Validity getValidity() { return validity; }




    public String getRoad() {
        return location.getRoad();
    }


    public String getTrafficManagement() {
        if (category.equals(ROADWORKS) && descriptionParts.size() >= 2) {
            return descriptionParts.get(descriptionParts.size() - 1);
        }
        return "";
    }
    public String getNiceType() {
        if (type.isEmpty()) {
            return category;
        }
        String result = "";
        for (int i = 0; i < type.length(); i++) {
            char letter = type.charAt(i);
            if (Character.isUpperCase(letter)) {
                result = result + " " + Character.toLowerCase(letter);
            } else {
                result = result + letter;
            }
        }
        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }

    @Override
    public String toString() {
        return category + " | " + getNiceType() + " | " + getRoad() + " | " + description;
    }
}