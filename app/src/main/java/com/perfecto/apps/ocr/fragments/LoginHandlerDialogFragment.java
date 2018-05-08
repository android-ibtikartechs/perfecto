package com.perfecto.apps.ocr.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perfecto.apps.ocr.R;

public class LoginHandlerDialogFragment extends DialogFragment {
    TextView btnLogin, btnLoginLater;
    FragmentButtonsListener listener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_login_alert, container, false);
        btnLogin = rootView.findViewById(R.id.tv_btn_login);
        btnLoginLater = rootView.findViewById(R.id.tv_btn_login_later);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onAlertButtonClickLisener(0);
            }
        });

        btnLoginLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAlertButtonClickLisener(1);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public interface FragmentButtonsListener
    {
        public void onAlertButtonClickLisener (int buttonFlag);
    }
    public void setButtonListener (FragmentButtonsListener listener)
    {
        this.listener = listener;
    }
}
