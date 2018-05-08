package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.perfecto.apps.ocr.Create_Doc_Dialog;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.Documents_Adapter;
import com.perfecto.apps.ocr.models.Document;
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
 * Created by Hosam Azzam on 13/08/2017.
 */

public class MyDocs_Fragment extends Fragment {
    VolleyClass volley;
    CardView create;
    RecyclerView list;
    ArrayList<Document> documents = new ArrayList<>();
    Documents_Adapter documents_adapter;
    User curuser;
    ImageView home_ico;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.mydocs_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());

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
        create = rootview.findViewById(R.id.create_btn);
        list = rootview.findViewById(R.id.myfiles_list);

        documents_adapter = new Documents_Adapter(getContext(), documents, getFragmentManager());
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(documents_adapter);

        curuser = Perfecto.getUserLoginInfo(getContext());

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Create_Doc_Dialog create_doc_dialog = new Create_Doc_Dialog();
                create_doc_dialog.show(getFragmentManager(), create_doc_dialog.getTag());
                create_doc_dialog.addonCreateClickListener(new Create_Doc_Dialog.onCreateClickListener() {
                    @Override
                    public void onCreate(String uid) {
                        init(uid);
                    }
                });
            }
        });

        init(String.valueOf(curuser.getId()));

        return rootview;
    }

    public void init(final String uid) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "mydocs", new Response.Listener<String>() {
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

                        JSONArray docs = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        documents.clear();
                        for (int i = 0; i < docs.size(); i++) {
                            JSONObject docObj = (JSONObject) jsonParser.parse(docs.get(i).toString());
                            Document document = new Document();
                            document.setId(docObj.get("id").toString());
                            document.setName(docObj.get("name").toString());
                            document.setUserid(docObj.get("user_id").toString());
                            document.setDate(docObj.get("created_at").toString());
                            JSONArray docsfiles = (JSONArray) jsonParser.parse(docObj.get("files").toString());
                            document.setNumoffiles(docsfiles.size());
                            documents.add(document);
                            System.out.println("i " + i);
                        }
                        documents_adapter.notifyDataSetChanged();
                        if (docs.size() == 0) {
                            Toast.makeText(getContext(), getResources().getString(R.string.no_doc), Toast.LENGTH_LONG).show();
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
                params.put("user_id", uid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

}
