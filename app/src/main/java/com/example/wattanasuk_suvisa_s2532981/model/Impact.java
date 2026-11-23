package com.example.wattanasuk_suvisa_s2532981.model;

public class Impact {
    private int lanesRestricted = -1;   // -1 = not given
    private int lanesOpen = -1;
    private String delays = "";

    public int getLanesRestricted() {
        return lanesRestricted;
    }

    public void setLanesRestricted(int n) {
        lanesRestricted = n;
    }

    public int getLanesOpen() {
        return lanesOpen;
    }

    public void setLanesOpen(int n) {
        lanesOpen = n;
    }

    public String getDelays() {
        return delays;
    }

    public void setDelays(String delays) {
        this.delays = delays;
    }
}
