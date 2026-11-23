package com.example.wattanasuk_suvisa_s2532981.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.wattanasuk_suvisa_s2532981.model.Situation;
import com.example.wattanasuk_suvisa_s2532981.model.SituationRepository;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SituationViewModel extends AndroidViewModel {


    private static final int REFRESH_MINUTES = 60;

    private final SituationRepository repository;
    private final ScheduledExecutorService backgroundThread = Executors.newSingleThreadScheduledExecutor();
    private final MutableLiveData<ArrayList<Situation>> situations = new MutableLiveData<>();
    private final MutableLiveData<Situation> selected = new MutableLiveData<>();
    private volatile boolean offline = false;
    private boolean detailOpen = false;
    private String roadSearch = "";
    private int maxDistanceKm = 0;

    public static final double HOME_LAT = 55.8668;
    public static final double HOME_LON = -4.2502;
    public static final String HOME_NAME = "GCU";

    public SituationViewModel(@NonNull Application application) {
        super(application);
        repository = new SituationRepository(application);
        backgroundThread.scheduleWithFixedDelay(() -> fetch(), 0, REFRESH_MINUTES, TimeUnit.MINUTES);
    }

    public LiveData<ArrayList<Situation>> getSituations() {
        return situations;
    }


    public LiveData<Situation> getSelected() {
        return selected;
    }


    public void select(Situation situation) {
        selected.setValue(situation);
    }

    public boolean isDetailOpen() {
        return detailOpen;
    }

    public void setDetailOpen(boolean open) {
        detailOpen = open;
    }



    public String getRoadSearch() {
        return roadSearch;
    }

    public void setRoadSearch(String text) {

        roadSearch = text.replace(" ", "").toUpperCase();
    }


    public ArrayList<Situation> filterByRoad(ArrayList<Situation> all) {
        if (roadSearch.isEmpty()) {
            return all;
        }
        ArrayList<Situation> result = new ArrayList<>();
        for (Situation s : all) {
            if (matchesRoad(s)) {
                result.add(s);
            }
        }
        return result;
    }

    private boolean matchesRoad(Situation s) {
        if (s.getRoad().replace(" ", "").equalsIgnoreCase(roadSearch)) {
            return true;
        }

        for (String word : s.getDescription().split("[^A-Za-z0-9]+")) {
            if (word.equalsIgnoreCase(roadSearch)) {
                return true;
            }
        }
        return false;
    }

    public int getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public void setMaxDistanceKm(int km) {
        maxDistanceKm = km;
    }


    public ArrayList<Situation> filter(ArrayList<Situation> all) {
        ArrayList<Situation> byRoad = filterByRoad(all);
        if (maxDistanceKm == 0) {
            return byRoad;   // any distance
        }
        ArrayList<Situation> result = new ArrayList<>();
        for (Situation s : byRoad) {
            double km = distanceKm(s);
            if (km >= 0 && km <= maxDistanceKm) {
                result.add(s);
            }
        }
        return result;
    }

    public double distanceKm(Situation s) {
        double best = -1;
        double[] points = {
                s.getLocation().getFrom().getLatitude(), s.getLocation().getFrom().getLongitude(),
                s.getLocation().getTo().getLatitude(), s.getLocation().getTo().getLongitude() };

        for (int i = 0; i < points.length; i += 2) {
            if (points[i] == 0) continue;
            float[] metres = new float[1];
            android.location.Location.distanceBetween(HOME_LAT, HOME_LON, points[i], points[i + 1], metres);
            double km = metres[0] / 1000.0;
            if (best < 0 || km < best) {
                best = km;
            }
        }
        return best;
    }


    public boolean isOffline() {
        return offline;
    }


    public void loadData() {
        backgroundThread.execute(() -> fetch());
    }


    private void fetch() {
        Log.d("ViewModel", "Downloading at " + new java.util.Date());
        ArrayList<Situation> list = repository.loadAll();
        offline = repository.usedSavedData();
        situations.postValue(list);
    }

    @Override
    protected void onCleared() {
        backgroundThread.shutdownNow();
    }
}
