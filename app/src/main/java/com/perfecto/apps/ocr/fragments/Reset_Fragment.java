package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hosam azzam on 05/10/2017.
 */

public class Reset_Fragment extends Fragment {
    VolleyClass volley;
    EditText email;
    CardView reset;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reset_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());

        reset = rootView.findViewById(R.id.reset_btn);
        email = rootView.findViewById(R.id.user_email_txt);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().equals("")) {
                    resetPassword(email.getText().toString());
                }
            }
        });
        return rootView;
    }

    public void resetPassword(final String email) {

        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "password/email", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONParser jsonParser = new JSONParser();
                    Object obj;
                    JSONObject mainObject;
                    obj = jsonParser.parse(response);
                    mainObject = (JSONObject) obj;

                    JSONObject statusObject = (JSONObject) jsonParser.parse(mainObject.get("status").toString());
                    if (statusObject.get("type").equals("sucess")) {
                        Toast.makeText(getContext(), mainObject.get("Response").toString(), Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();

                    } else {
                        Toast.makeText(getContext(), mainObject.get("Response").toString(), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
