package com.example.wattanasuk_suvisa_s2532981.view;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wattanasuk_suvisa_s2532981.R;
import com.example.wattanasuk_suvisa_s2532981.model.Situation;
import com.example.wattanasuk_suvisa_s2532981.viewModel.SituationViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SituationListFragment extends Fragment {

    private SituationViewModel viewModel;
    private SituationAdapter adapter;
    private TextView statusText;
    private String updatedTime = "";


    private static final String[] DISTANCE_LABELS = {"Any distance", "10 km", "25 km", "50 km", "100 km"};
    private static final int[] DISTANCE_KM = {0, 10, 25, 50, 100};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statusText = view.findViewById(R.id.statusText);
        Button refreshButton = view.findViewById(R.id.refreshButton);
        EditText searchBox = view.findViewById(R.id.searchBox);
        Button clearButton = view.findViewById(R.id.clearButton);
        ListView listView = view.findViewById(R.id.situationList);


        viewModel = new ViewModelProvider(requireActivity()).get(SituationViewModel.class);

        adapter = new SituationAdapter(requireContext());
        listView.setAdapter(adapter);


        searchBox.setText(viewModel.getRoadSearch());

        viewModel.getSituations().observe(getViewLifecycleOwner(), situations -> {
            updatedTime = new SimpleDateFormat("HH:mm:ss", Locale.UK).format(new Date());
            showList();
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setRoadSearch(s.toString());
                showList();
            }
        });


        Spinner distanceSpinner = view.findViewById(R.id.distanceSpinner);
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, DISTANCE_LABELS);
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(distanceAdapter);
        for (int i = 0; i < DISTANCE_KM.length; i++) {
            if (DISTANCE_KM[i] == viewModel.getMaxDistanceKm()) {
                distanceSpinner.setSelection(i);
            }
        }

        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View row, int position, long id) {
                viewModel.setMaxDistanceKm(DISTANCE_KM[position]);
                showList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        clearButton.setOnClickListener(v -> searchBox.setText(""));

        refreshButton.setOnClickListener(v -> {
            statusText.setText("Loading...");
            viewModel.loadData();
        });

        listView.setOnItemClickListener((parent, row, position, id) -> {
            Situation tapped = adapter.getItem(position);
            viewModel.select(tapped);
            ((MainActivity) requireActivity()).showDetail();
        });
    }

    private void showList() {
        ArrayList<Situation> all = viewModel.getSituations().getValue();
        if (all == null) {
            return;
        }
        ArrayList<Situation> shown = viewModel.filter(all);
        adapter.setSituations(shown);

        String search = viewModel.getRoadSearch();
        int km = viewModel.getMaxDistanceKm();

        if (all.isEmpty()) {
            statusText.setText("No data. Check your internet and tap Refresh.");
        } else if (!search.isEmpty() || km > 0) {
            String text = shown.size() + " of " + all.size() + " situations";
            if (!search.isEmpty()) text = text + " on " + search;
            if (km > 0) text = text + " within " + km + " km of " + SituationViewModel.HOME_NAME;
            statusText.setText(text);
        } else if (viewModel.isOffline()) {
            statusText.setText("OFFLINE: " + all.size() + " saved situations");
        } else {
            statusText.setText(all.size() + " situations · updated " + updatedTime);
        }
    }
}