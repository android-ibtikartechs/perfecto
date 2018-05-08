package com.perfecto.apps.ocr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.perfecto.apps.ocr.fragments.Home_Fragment;
import com.perfecto.apps.ocr.fragments.Login_Fragment;
import com.perfecto.apps.ocr.tools.AppRater;
import com.perfecto.apps.ocr.tools.ConfigurationWrapper;
import com.perfecto.apps.ocr.tools.Perfecto;

import java.util.Locale;

/**
 * Created by Hosam Azzam on 21/08/2017.
 */

public class MainActivity extends AppCompatActivity {

    ImageView home_ico;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home_ico = (ImageView) findViewById(R.id.toolbar_home_ico);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        boolean isBackToLogin = intent.getBooleanExtra("login18",false);
        if(isBackToLogin)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, new Login_Fragment(), "Login_Fragment")
                    .commit();
        }
        /*if (Perfecto.getUserLoginInfo(this) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, new Login_Fragment(), "Login_Fragment")
                    .commit();

        } */ else {
            Home_Fragment home_fragment = new Home_Fragment();
            if (intent != null) {
                home_fragment.setIntent(intent);
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, home_fragment, "Home_Fragment")
                    .commit();
        }

        AppRater.app_launched(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            try {
                fragment.onActivityResult(requestCode, resultCode, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        System.out.println(getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            home_ico.setVisibility(View.GONE);

            //  Toast.makeText(getApplicationContext(), "backed", Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();

    }

    @Override
    protected void attachBaseContext(Context newBase) { // important don't miss this func to change LANG
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String LANG_CURRENT = preferences.getString("Language", "en");
        System.out.println(LANG_CURRENT);
        super.attachBaseContext(ConfigurationWrapper.wrapLocale(newBase, new Locale(LANG_CURRENT)));
    }
}
