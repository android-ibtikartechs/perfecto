package com.perfecto.apps.ocr;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.adapter.Group_List_Adapter;
import com.perfecto.apps.ocr.models.Group;
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
 * Created by Hosam Azzam on 30/10/2017.
 */

public class Group_Share_Dialog extends DialogFragment {
    VolleyClass volley;
    User user;
    ArrayList<Group> groups = new ArrayList<>();
    Group_List_Adapter group_list_adapter;
    ListView group_list;
    CardView ok;
    String fid;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_group_list_dialog, container, false);
        try {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        bundle = getArguments();
        if (bundle != null) {
            fid = bundle.getString("file_id", "0");
        }

        group_list = rootView.findViewById(R.id.groups_list);
        ok = rootView.findViewById(R.id.dialog_ok_btn);

        user = Perfecto.getUserLoginInfo(getContext());
        volley = VolleyClass.getInstance(getContext());

        group_list_adapter = new Group_List_Adapter(getContext(), groups);
        group_list_adapter.addOnItemClickListener(new Group_List_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String gid) {
                shareToGroup(String.valueOf(user.getId()), gid, fid);
            }
        });
        group_list.setAdapter(group_list_adapter);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        init(String.valueOf(user.getId()));

        return rootView;
    }

    public void init(final String uid) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "groupslist", new Response.Listener<String>() {
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

                        JSONArray groupslist = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        groups.clear();
                        for (int i = 0; i < groupslist.size(); i++) {
                            JSONObject groupObj = (JSONObject) jsonParser.parse(groupslist.get(i).toString());
                            Group group = new Group();
                            group.setId(groupObj.get("id").toString());
                            group.setName(groupObj.get("name").toString());
                            if (groupObj.get("photo") != null) {
                                group.setPhoto(groupObj.get("photo").toString());
                            }
                            group.setMemberscount(Integer.valueOf(groupObj.get("members").toString()));
                            groups.add(group);
                        }
                        group_list_adapter.notifyDataSetChanged();

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

    public void shareToGroup(final String uid, final String gid, final String fid) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "filegroupshare", new Response.Listener<String>() {
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
                        Toast.makeText(getContext(), mainObject.get("Response").toString(), Toast.LENGTH_SHORT).show();
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
                params.put("group_id", gid);
                params.put("file_id", fid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
