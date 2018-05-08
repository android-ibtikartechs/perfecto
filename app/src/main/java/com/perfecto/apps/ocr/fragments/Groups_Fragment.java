package com.perfecto.apps.ocr.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
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
import com.hosamazzam.customviews.NonScrollListView;
import com.perfecto.apps.ocr.Create_Group_Dialog;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.AllGroups_Adapter;
import com.perfecto.apps.ocr.adapter.MyGroups_ِAdapter;
import com.perfecto.apps.ocr.adapter.MyJoinedGroups_Adapter;
import com.perfecto.apps.ocr.models.Group;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.AsyncProgressDialog;
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

public class Groups_Fragment extends Fragment {
    VolleyClass volley;
    CardView addBtn;
    NonScrollListView allgroups, myjoinedgroups;
    RecyclerView mygroups;
    User curuer;
    AllGroups_Adapter allGroups_adapter;
    MyJoinedGroups_Adapter myJoinedGroups_adapter;
    MyGroups_ِAdapter myGroups_ِAdapter;
    ArrayList<Group> groups1 = new ArrayList<>(),
            groups2 = new ArrayList<>(), groups3 = new ArrayList<>();
    ImageView home_ico;
    AsyncProgressDialog asyncProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.groups_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());
        curuer = Perfecto.getUserLoginInfo(getContext());

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

        addBtn = rootview.findViewById(R.id.add_group_btn);
        mygroups = rootview.findViewById(R.id.mygroups_list);
        myjoinedgroups = rootview.findViewById(R.id.join_groups_list);
        allgroups = rootview.findViewById(R.id.all_groups_list);

        mygroups.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        myGroups_ِAdapter = new MyGroups_ِAdapter(getContext(), groups1, getFragmentManager());
        mygroups.setAdapter(myGroups_ِAdapter);
        myJoinedGroups_adapter = new MyJoinedGroups_Adapter(getContext(), groups2, getFragmentManager());
        myjoinedgroups.setAdapter(myJoinedGroups_adapter);
        allGroups_adapter = new AllGroups_Adapter(getContext(), groups3, getFragmentManager());
        allgroups.setAdapter(allGroups_adapter);
        allGroups_adapter.setOnJoinClickListner(new AllGroups_Adapter.onJoinClickListner() {
            @Override
            public void onJoinClick() {
                initMyGroups(String.valueOf(curuer.getId()));
                initMyJoiningGroups(String.valueOf(curuer.getId()));
                initAllGroups(String.valueOf(curuer.getId()));
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                //   Toast.makeText(getContext(), "pop", Toast.LENGTH_SHORT).show();
                try {
                    if (getFragmentManager() != null) {
                        if (getFragmentManager().findFragmentByTag("Groups_Fragment") != null) {
                            if (getFragmentManager().findFragmentByTag("Groups_Fragment").isVisible()) {
                                initMyGroups(String.valueOf(curuer.getId()));
                                initMyJoiningGroups(String.valueOf(curuer.getId()));
                                initAllGroups(String.valueOf(curuer.getId()));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        asyncProgressDialog = new AsyncProgressDialog(getContext(), 3, "", getResources().getString(R.string.pls_wait), false);
        asyncProgressDialog.build();
        asyncProgressDialog.show();

        initMyGroups(String.valueOf(curuer.getId()));
        initMyJoiningGroups(String.valueOf(curuer.getId()));
        initAllGroups(String.valueOf(curuer.getId()));

        return rootview;
    }

    public void addGroup() {
        Create_Group_Dialog create_group_dialog = new Create_Group_Dialog();
        create_group_dialog.show(getFragmentManager(), create_group_dialog.getTag());
        create_group_dialog.addonCreateClickListener(new Create_Group_Dialog.onCreateClickListener() {
            @Override
            public void onCreate(String uid) {
                initMyGroups(uid);
                initMyJoiningGroups(uid);
                initAllGroups(uid);
            }
        });
    }

    public void initMyGroups(final String id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "mygroups", new Response.Listener<String>() {
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

                        JSONArray groups = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        groups1.clear();
                        for (int i = 0; i < groups.size(); i++) {
                            JSONObject groupObj = (JSONObject) jsonParser.parse(groups.get(i).toString());
                            Group group = new Group();
                            group.setId(groupObj.get("id").toString());
                            group.setName(groupObj.get("name").toString());
                            if (groupObj.get("photo") != null) {
                                group.setPhoto(groupObj.get("photo").toString());
                            }
                            group.setMemberscount(Integer.valueOf(groupObj.get("members").toString()));
                            groups1.add(group);
                        }
                        myGroups_ِAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    asyncProgressDialog.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    asyncProgressDialog.close();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                asyncProgressDialog.close();
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

    public void initAllGroups(final String id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "allgroups", new Response.Listener<String>() {
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

                        JSONArray groups = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        groups3.clear();
                        for (int i = 0; i < groups.size(); i++) {
                            JSONObject groupObj = (JSONObject) jsonParser.parse(groups.get(i).toString());
                            Group group = new Group();
                            group.setId(groupObj.get("id").toString());
                            group.setName(groupObj.get("name").toString());
                            if (groupObj.get("photo") != null) {
                                group.setPhoto(groupObj.get("photo").toString());
                            }
                            group.setMemberscount(Integer.valueOf(groupObj.get("members").toString()));
                            groups3.add(group);
                        }
                        allGroups_adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    asyncProgressDialog.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    asyncProgressDialog.close();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                asyncProgressDialog.close();
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

    public void initMyJoiningGroups(final String id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "mygrouplist", new Response.Listener<String>() {
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

                        JSONArray groups = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        groups2.clear();
                        for (int i = 0; i < groups.size(); i++) {
                            JSONObject groupObj = (JSONObject) jsonParser.parse(groups.get(i).toString());
                            Group group = new Group();
                            group.setId(groupObj.get("id").toString());
                            group.setName(groupObj.get("name").toString());
                            if (groupObj.get("photo") != null) {
                                group.setPhoto(groupObj.get("photo").toString());
                            }
                            group.setMemberscount(Integer.valueOf(groupObj.get("members").toString()));
                            groups2.add(group);
                        }
                        myJoinedGroups_adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    asyncProgressDialog.close();


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    asyncProgressDialog.close();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                asyncProgressDialog.close();
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
