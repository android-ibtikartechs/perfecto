package com.perfecto.apps.ocr;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.models.Notification;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hosam azzam on 16/08/2017.
 */

public class Request_Group_Dialog extends DialogFragment {
    TextView name, msg, date;
    LinearLayout accept, decline;
    VolleyClass volley;
    User user;
    Notification notifyObj;

    public void setNotifyObj(Notification notifyObj) {
        this.notifyObj = notifyObj;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_request_group, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        volley = VolleyClass.getInstance(getContext());

        name = rootView.findViewById(R.id.sender_name_txt);
        msg = rootView.findViewById(R.id.sender_msg_txt);
        date = rootView.findViewById(R.id.request_date_txt);
        decline = rootView.findViewById(R.id.request_decline_btn);
        accept = rootView.findViewById(R.id.request_accept_btn);
        user = Perfecto.getUserLoginInfo(getContext());

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifyObj.getFlag().equals("4"))
                    changeRequestStatus(notifyObj.getRequest_id(), "0");
                else
                    changeRequestStatus(notifyObj.getRequest_id(), "1");

            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifyObj.getFlag().equals("4"))
                    changeRequestStatus(notifyObj.getRequest_id(), "5");
                else
                    changeRequestStatus(notifyObj.getRequest_id(), "3");


            }
        });

        name.setText(notifyObj.getSender_name());
        msg.setText(notifyObj.getText());
        date.setText(notifyObj.getDate());


        return rootView;
    }


    public void changeRequestStatus(final String reqid, final String flag) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getContext().getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group/changerequest", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONParser jsonParser = new JSONParser();
                    Object obj;
                    JSONObject mainObject;
                    obj = jsonParser.parse(response);
                    mainObject = (JSONObject) obj;

                    JSONObject statusObject = (JSONObject) jsonParser.parse(mainObject.get("status").toString());
                    if (statusObject.get("type").equals("success") || statusObject.get("code").equals(0)) {
                        if (flag.equals("1")) {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.request_accepted), Toast.LENGTH_SHORT).show();
                        }
                        if (flag.equals("3")) {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.request_rejected), Toast.LENGTH_SHORT).show();

                        }
                        loading.dismiss();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                    dismiss();


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request_id", reqid);
                params.put("flag", flag);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

}
