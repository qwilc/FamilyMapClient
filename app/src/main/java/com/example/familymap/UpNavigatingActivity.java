package com.example.familymap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.MenuItem;

public class UpNavigatingActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        if(menu.getItemId() == android.R.id.home) {
            DataCache.setIsEventActivity(false);
            DataCache.loadSavedEvent();
            Intent intent= new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}