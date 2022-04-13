package com.example.familymap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.preference.Preference;
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

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Family Map: Settings");

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
            DataCache.initializeSettings(preferences);

            preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    logger.info("In sharedPreferenceChanged");
                    switch(key) {
                        case "life_story":
                            DataCache.setIsLifeStoryEnabled(preferences.getBoolean("life_story", true));
                        case "family_tree":
                            DataCache.setIsFamilyTreeEnabled(preferences.getBoolean("family_tree", true));
                        case "spouse":
                            DataCache.setIsSpouseLineEnabled(preferences.getBoolean("spouse", true));
                        case "father_side":
                            DataCache.setIsFatherSideEnabled(preferences.getBoolean("father_side", true));
                        case "mother_side":
                            DataCache.setIsMotherSideEnabled(preferences.getBoolean("mother_side", true));
                        case "male_events":
                            DataCache.setIsMaleEventsEnabled(preferences.getBoolean("male_events", true));
                        case "female_events":
                            DataCache.setIsFemaleEventsEnabled(preferences.getBoolean("female_events", true));
                    }
                }
            });

            logger.finer("Mother's side" + preferences.getBoolean("mother_side", true));

            Preference logout = findPreference("logout");
            assert logout != null;
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    logger.info("In logout onPreferenceClick");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    DataCache.resetData();
                    startActivity(intent);
                    return true;
                }
            });
        }
    }
}