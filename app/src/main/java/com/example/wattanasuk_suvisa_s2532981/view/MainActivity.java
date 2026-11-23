/*  Starter project for Mobile Platform Development - 1st diet 26/27
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required source and displays the
    raw data in a TextField
*/

//
// Name                 Suvisa Wattanasuk
// Student ID           S2532981
// Programme of Study   BSc/BSc (Hons) Software Development
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package com.example.wattanasuk_suvisa_s2532981.view;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.wattanasuk_suvisa_s2532981.R;
import com.example.wattanasuk_suvisa_s2532981.viewModel.SituationViewModel;

public class MainActivity extends AppCompatActivity {

    private SituationViewModel viewModel;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(SituationViewModel.class);
        twoPane = (findViewById(R.id.detailContainer) != null);
        showScreens();



        String myKey = "";
        try {
            Bundle metaData = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            if (metaData != null && metaData.getString("com.example.testlocalpropertieskey.MY_KEY") != null) {
                myKey = metaData.getString("com.example.testlocalpropertieskey.MY_KEY");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Config", "Could not read meta-data: " + e);
        }
        Log.d("Config", "Key found: " + !myKey.isEmpty());
    }

    public boolean isTwoPane() {
        return twoPane;
    }

    private void showScreens() {
        FragmentManager fm = getSupportFragmentManager();


        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment oldDetail = fm.findFragmentById(R.id.detailContainer);
        if (oldDetail != null) {
            transaction.remove(oldDetail);
        }


        transaction.replace(R.id.fragmentContainer, new SituationListFragment());

        if (twoPane) {
            transaction.replace(R.id.detailContainer, new SituationDetailFragment());
        }
        transaction.commitNow();

        if (!twoPane && viewModel.isDetailOpen()) {
            openDetailScreen();
        }
    }


    public void showDetail() {
        viewModel.setDetailOpen(true);
        if (!twoPane) {
            openDetailScreen();
        }
    }

    private void openDetailScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new SituationDetailFragment())
                .addToBackStack(null)
                .commit();
    }
}
