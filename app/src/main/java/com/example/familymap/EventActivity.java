package com.example.familymap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class EventActivity extends UpNavigatingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        DataCache.setIsEventActivity(true);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.event_fragment_frame_layout, fragment)
                .commit();
    }
}