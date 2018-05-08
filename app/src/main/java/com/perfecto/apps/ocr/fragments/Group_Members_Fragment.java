package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.Members_List_Adapter;
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
 * Created by Hosam Azzam on 26/10/2017.
 */

public class Group_Members_Fragment extends Fragment {
    VolleyClass volley;
    User user;
    ArrayList<User> users = new ArrayList<>();
    Members_List_Adapter members_list_adapter;
    RecyclerView member_list;
    Bundle bundle;
    String gid = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.group_members_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());
        user = Perfecto.getUserLoginInfo(getContext());
        bundle = getArguments();
        if (bundle != null) {
            gid = bundle.getString("group_id", "0");
        }

        member_list = rootview.findViewById(R.id.group_member_list);
        members_list_adapter = new Members_List_Adapter(getContext(), users, gid);

        member_list.setLayoutManager(new LinearLayoutManager(getContext()));
        member_list.setAdapter(members_list_adapter);

        init(gid);

        return rootview;
    }

    public void init(final String gid) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group/users", new Response.Listener<String>() {
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

                        JSONArray membersObj = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        for (int i = 0; i < membersObj.size(); i++) {
                            JSONObject member = (JSONObject) jsonParser.parse(membersObj.get(i).toString());
                            User mem = new User();
                            mem.setName(member.get("name").toString());
                            mem.setPhoto(member.get("photo").toString());
                            mem.setId(Long.valueOf(member.get("id").toString()));
                            users.add(mem);
                        }
                        members_list_adapter.notifyDataSetChanged();


                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", gid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
