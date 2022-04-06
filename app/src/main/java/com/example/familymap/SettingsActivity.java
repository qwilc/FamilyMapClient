package com.example.familymap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.logging.Level;
import java.util.logging.Logger;

import logger.LoggerConfig;

public class SettingsActivity extends UpNavigatingActivity {
    private final Logger logger = Logger.getLogger("SettingsActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Family Map: Settings");

        if (savedInstanceState == null) {
            logger.fine("Creating new settings fragment");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final Logger logger = Logger.getLogger("SettingsFragment");
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            LoggerConfig.configureLogger(logger, Level.FINEST);
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

            logger.finer("Mother's side" + String.valueOf(preferences.getBoolean("mother_side", true)));

            //TODO: Logout probably doesn't need to be a preference, but i need to go change that in root_preferences.xml
            Preference logout = findPreference("logout");
            assert logout != null;
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    logger.info("In logout onPreferenceClick");
                    Intent intent= new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    //TODO: MAKE SURE YOU RESET EVERYTHING!!! datacache, maybe settings?
                    return true;
                }
            });
        }
    }
}