package com.example.familymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {
    private final Logger logger = Logger.getLogger("MainActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_frame_layout);

        if(fragment == null) {
            fragment = createLoginFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_frame_layout, fragment)
                    .commit();
        }
        else {
            if(fragment instanceof LoginFragment) {
                ( (LoginFragment) fragment).registerListener(this);
            }
        }
    }

    private LoginFragment createLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    public void notifyDone() {
        logger.info("In notifyDone");

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_frame_layout, fragment)
                .commit();
    }
}