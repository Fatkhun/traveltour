package com.fatkhun.travelia.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TourWisataListActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_wisata);

        // getSupportFragmentManager() is use to return the fragment manager for interacting with
        // fragment associated with this activity.
        // beginTransaction() is use to start operations on the current fragment manager.
        // replace() is use to replace the the content with mountain list fragment.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new TourWisataActivity())
                .commit();
    }
}
