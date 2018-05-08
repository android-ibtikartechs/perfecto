package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.Notifications_Adapter;
import com.perfecto.apps.ocr.models.Notification;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hosam Azzam on 27/09/2017.
 */

public class Notification_Fragment extends Fragment {
    ArrayList<Notification> notifications = new ArrayList<>();
    Notifications_Adapter notifications_adapter;
    VolleyClass volley;
    RecyclerView notification_list;
    TextView no_notification;
    User user;
    ImageView home_ico;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.notifications_fragment, container, false);

        volley = VolleyClass.getInstance(getContext());
        user = Perfecto.getUserLoginInfo(getContext());

        home_ico = getActivity().findViewById(R.id.toolbar_home_ico);
        home_ico.setVisibility(View.VISIBLE);
        home_ico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new Home_Fragment(), "Home_Fragment")
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        no_notification = rootview.findViewById(R.id.no_notification_txt);
        notification_list = rootview.findViewById(R.id.notification_list);

        notifications_adapter = new Notifications_Adapter(getContext(), notifications, getFragmentManager());
        notification_list.setAdapter(notifications_adapter);
        notification_list.setLayoutManager(new LinearLayoutManager(getContext()));
        notification_list.setHasFixedSize(false);

        init(String.valueOf(user.getId()));
        return rootview;
    }

    public void init(final String id) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "notifications", new Response.Listener<String>() {
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

                        JSONArray notify = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        notifications.clear();
                        for (int i = 0; i < notify.size(); i++) {
                            JSONObject notifyObj = (JSONObject) jsonParser.parse(notify.get(i).toString());
                            Notification notification = new Notification();
                            notification.setId(notifyObj.get("id").toString());
                            notification.setUserid(notifyObj.get("user_id").toString());
                            notification.setSenderid(notifyObj.get("sender_id").toString());
                            notification.setText(notifyObj.get("text").toString());
                            notification.setRequest_id(notifyObj.get("request_id").toString());
                            notification.setIs_read(notifyObj.get("is_read").toString());
                            notification.setFlag(notifyObj.get("flag").toString());
                            notification.setGroup_id(notifyObj.get("group_id").toString());
                            notification.setSender_name(notifyObj.get("sender_name").toString());
                            notification.setPhoto(notifyObj.get("sender_photo").toString());
                            notification.setDate(notifyObj.get("updated_at").toString());
                            notifications.add(notification);
                        }
                        notifications_adapter.notifyDataSetChanged();

                        if (notify.size() == 0) {
                            no_notification.setVisibility(View.VISIBLE);
                            no_notification.setText(getResources().getString(R.string.no_notification));
                        }

                        loading.dismiss();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", id);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
