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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.Chat_List_Adapter;
import com.perfecto.apps.ocr.models.Chat;
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

public class Chat_Rooms_Fragment extends Fragment {
    ImageView home_ico;
    RecyclerView chat_list;
    ArrayList<Chat> chats = new ArrayList<>();
    VolleyClass volley;
    User user;
    Chat_List_Adapter chat_list_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.chats_fragment, container, false);

        volley = VolleyClass.getInstance(getContext());
        user = Perfecto.getUserLoginInfo(getContext());

        chat_list_adapter = new Chat_List_Adapter(getContext(), chats, getFragmentManager());
        chat_list = rootview.findViewById(R.id.chat_list);

        chat_list.setLayoutManager(new LinearLayoutManager(getContext()));
        chat_list.setAdapter(chat_list_adapter);


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

        init(String.valueOf(user.getId()));

        return rootview;
    }

    public void init(final String uid) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "inbox", new Response.Listener<String>() {
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
                        chats.clear();
                        for (int i = 0; i < notify.size(); i++) {
                            JSONObject notifyObj = (JSONObject) jsonParser.parse(notify.get(i).toString());
                            Chat chat = new Chat();
                            chat.setUser_id(notifyObj.get("user_id").toString());
                            chat.setGroup_id(notifyObj.get("group_id").toString());
                            chat.setUser_photo(notifyObj.get("user_photo").toString());
                            chat.setUser_name(notifyObj.get("user_name").toString());
                            chat.setGroup_photo(notifyObj.get("group_photo").toString());
                            chat.setGroup_name(notifyObj.get("group_name").toString());
                            chat.setMsg(notifyObj.get("message").toString());
                            chats.add(chat);
                        }
                        chat_list_adapter.notifyDataSetChanged();


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
                params.put("user_id", uid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
