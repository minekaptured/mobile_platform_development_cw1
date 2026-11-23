package com.example.wattanasuk_suvisa_s2532981.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wattanasuk_suvisa_s2532981.R;
import com.example.wattanasuk_suvisa_s2532981.model.Impact;
import com.example.wattanasuk_suvisa_s2532981.model.Location;
import com.example.wattanasuk_suvisa_s2532981.model.Situation;
import com.example.wattanasuk_suvisa_s2532981.model.Validity;
import com.example.wattanasuk_suvisa_s2532981.viewModel.SituationViewModel;

import java.util.Locale;


public class SituationDetailFragment extends Fragment {

    private LinearLayout rows;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SituationViewModel viewModel = new ViewModelProvider(requireActivity()).get(SituationViewModel.class);
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        if (((MainActivity) requireActivity()).isTwoPane()) {
            backButton.setVisibility(View.GONE);
        }

        viewModel.getSelected().observe(getViewLifecycleOwner(), situation -> {
            if (situation != null) {
                show(view, situation);
            } else {
                TextView title = view.findViewById(R.id.detailTitle);
                title.setText("Tap a situation in the list to see its details");
            }
        });
    }
    private void show(View view, Situation s) {
        Location location = s.getLocation();
        Impact impact = s.getImpact();
        Validity validity = s.getValidity();

        // Top part
        TextView title = view.findViewById(R.id.detailTitle);
        TextView category = view.findViewById(R.id.detailCategory);
        TextView description = view.findViewById(R.id.detailDescription);
        title.setText(s.getRoad() + " · " + s.getNiceType());
        category.setText(s.getCategory());
        description.setText(s.getDescription());
        rows = view.findViewById(R.id.detailRows);
        rows.removeAllViews();

        addHeading("Location");
        addRow("Road", location.getRoad());
        addRow("Direction", niceDirection(location.getDirection()));
        addRow("From", location.getFrom().getName());
        addRow("To", location.getTo().getName());
        addRow("Work type", s.getTrafficManagement());
        addRow("Coordinates", coordinates(location));

        addHeading("When");
        addRow("Started", validity.getStartText());
        addRow("Expected to end", validity.getEndText());
        if (validity.getDays() >= 0) {
            addRow("Duration", validity.getDays() + " days");
        }

        addHeading("Impact");
        addRow("Delays", niceDelays(impact.getDelays()));
        if (impact.getLanesRestricted() >= 0) {
            addRow("Lanes restricted", String.valueOf(impact.getLanesRestricted()));
        }
        if (impact.getLanesOpen() >= 0) {
            addRow("Lanes open", String.valueOf(impact.getLanesOpen()));
        }

        addHeading("Reference");
        addRow("ID", s.getId());
    }

    private void addHeading(String text) {
        TextView heading = new TextView(requireContext());
        heading.setText(text);
        heading.setTextSize(16);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 32, 0, 8);
        rows.addView(heading);
    }

    private void addRow(String label, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_detail_row, rows, false);
        TextView labelView = row.findViewById(R.id.rowLabel);
        TextView valueView = row.findViewById(R.id.rowValue);
        labelView.setText(label);
        valueView.setText(value);
        rows.addView(row);
    }

    private String niceDirection(String direction) {
        if (direction.isEmpty()) return "";
        if (direction.equals("bothWays")) return "Both ways";
        return Character.toUpperCase(direction.charAt(0)) + direction.substring(1).toLowerCase();
    }

    private String niceDelays(String delays) {
        if (delays.equals("delays")) return "Delays possible";
        if (delays.equals("longDelays")) return "Long delays";
        if (delays.equals("veryLongDelays")) return "Very long delays";
        return "";
    }
    private String coordinates(Location location) {
        if (location.getFrom().getLatitude() == 0) return "";
        String text = String.format(Locale.UK, "%.5f, %.5f",
                location.getFrom().getLatitude(), location.getFrom().getLongitude());
        if (location.getTo().getLatitude() != 0) {
            text = "From " + text + String.format(Locale.UK, "\nto %.5f, %.5f",
                    location.getTo().getLatitude(), location.getTo().getLongitude());
        }
        return text;
    }
}
