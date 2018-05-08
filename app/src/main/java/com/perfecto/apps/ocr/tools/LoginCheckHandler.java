package com.perfecto.apps.ocr.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.perfecto.apps.ocr.MainActivity;
import com.perfecto.apps.ocr.fragments.LoginHandlerDialogFragment;
import com.perfecto.apps.ocr.tools.Perfecto;

public class LoginCheckHandler implements LoginHandlerDialogFragment.FragmentButtonsListener {
    private Context context;

    public LoginCheckHandler(Context context) {
        this.context = context;
    }

    public boolean checkLogin()
    {
        if(Perfecto.getUserLoginState(context))
        {
            return true;
        }

        else {
            AppCompatActivity activity = (AppCompatActivity) context;
            FragmentManager fm = activity.getSupportFragmentManager();

            LoginHandlerDialogFragment loginHandlerDialogFragment = new LoginHandlerDialogFragment();
            loginHandlerDialogFragment.setButtonListener(this);
            loginHandlerDialogFragment.show(fm, "login alert dialog");
            return false;
        }
    }

    @Override
    public void onAlertButtonClickLisener(int buttonFlag) {
        AppCompatActivity activity = (AppCompatActivity) context;
        if (buttonFlag == 0)
        {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("login18",true);
            activity.startActivity(intent);
        }
    }
}
