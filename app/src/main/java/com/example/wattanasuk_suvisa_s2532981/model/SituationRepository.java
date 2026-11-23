package com.example.wattanasuk_suvisa_s2532981.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

public class SituationRepository {

    private static final String INCIDENTS_URL =
            "https://datex2.trafficscotland.org/rest/2.3/publications/UnplannedEvents/Content.xml";
    private static final String ROADWORKS_URL =
            "https://datex2.trafficscotland.org/rest/2.3/publications/CurrentRoadworks/Content.xml";

    private static final String CLIENT_ID = "198047aa-9774-4bd7-b231-3703b22cf4cb";
    private final String clientKey;

    private static final String INCIDENTS_FILE = "incidents.xml";
    private static final String ROADWORKS_FILE = "roadworks.xml";

    private final Context context;
    private final SituationParser parser = new SituationParser();
    private boolean usedSavedData = false;

    public SituationRepository(Context context) {
        this.context = context;
        clientKey = readApiKey();
    }

    public boolean usedSavedData() {
        return usedSavedData;
    }

    public ArrayList<Situation> loadAll() {
        usedSavedData = false;
        ArrayList<Situation> all = new ArrayList<>();
        all.addAll(parser.parse(getXml(INCIDENTS_URL, INCIDENTS_FILE), Situation.INCIDENT));
        all.addAll(parser.parse(getXml(ROADWORKS_URL, ROADWORKS_FILE), Situation.ROADWORKS));
        return all;
    }

    private String getXml(String address, String fileName) {
        String xml = download(address);
        if (!xml.isEmpty()) {
            saveToFile(fileName, xml);
            return xml;
        }
        usedSavedData = true;
        return readFromFile(fileName);
    }

    private String download(String address) {
        StringBuilder text = new StringBuilder();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);

            String encoded = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + clientKey).getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encoded);

            if (connection.getResponseCode() != 200) {
                Log.e("Repository", "Server answered " + connection.getResponseCode());
                return "";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                text.append(line).append("\n");
            }
            in.close();
            connection.disconnect();
        } catch (Exception e) {
            Log.e("Repository", "Download failed: " + e);
            return "";
        }

        // Keep only the XML, and reject the website's login page
        String xml = text.toString();
        int start = xml.indexOf("<");
        if (start < 0 || xml.contains("<!DOCTYPE html")) {
            return "";
        }
        return xml.substring(start);
    }

    private String readApiKey() {
        try {
            Bundle metaData = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData;
            if (metaData != null) {
                return metaData.getString("MY_API_KEY", "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Repository", "Could not read meta-data: " + e);
        }
        return "";
    }

    private void saveToFile(String fileName, String text) {
        try (FileWriter writer = new FileWriter(new File(context.getFilesDir(), fileName))) {
            writer.write(text);
        } catch (Exception e) {
            Log.e("Repository", "Could not save " + fileName + ": " + e);
        }
    }

    private String readFromFile(String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists()) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (Exception e) {
            Log.e("Repository", "Could not read " + fileName + ": " + e);
        }
        return text.toString();
    }
}