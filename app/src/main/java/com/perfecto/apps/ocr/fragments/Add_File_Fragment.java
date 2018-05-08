package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.Translate_Dialog;
import com.perfecto.apps.ocr.models.File;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.LoginCheckHandler;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hosam Azzam on 20/08/2017.
 */

public class Add_File_Fragment extends Fragment {
    VolleyClass volley;
    ImageView addFile_btn, share_btn, translate_btn, home_ico;
    EditText file_desc, file_name;
    Bundle bundle;
    User user;
    File file = new File();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.save_file_fragament, container, false);
        volley = VolleyClass.getInstance(getContext());
        user = Perfecto.getUserLoginInfo(getContext());

        home_ico = getActivity().findViewById(R.id.toolbar_home_ico);
        home_ico.setVisibility(View.VISIBLE);
        home_ico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new Home_Fragment(), "Home_Fragment")
                        .commit();
            }
        });
        file_desc = rootview.findViewById(R.id.file_content_txt);
        file_name = rootview.findViewById(R.id.file_name_txt);
        addFile_btn = rootview.findViewById(R.id.file_save_btn);
        share_btn = rootview.findViewById(R.id.file_share_btn);
        translate_btn = rootview.findViewById(R.id.file_translate_btn);

        bundle = getArguments();
        if (bundle != null) {
            file_desc.setText(bundle.getString("response"));
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

            file.setDesc(file_desc.getText().toString());
            file.setDocname(date);
            file.setName(date);
            file.setId("temp");
            file.setSourceLangPos(bundle.getString("doc_lang", "none"));

        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.fetch_txt_error), Toast.LENGTH_LONG).show();
            getFragmentManager().popBackStack();
        }

        addFile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginCheckHandler loginCheckHandler = new LoginCheckHandler(getContext());
                if (loginCheckHandler.checkLogin()) {
                    if (bundle.getString("doc_id").equals("temp")) {
                        if (!file_desc.getText().toString().equals("")) {
                            if (!file_name.getText().toString().equals("")) {
                                create(file_name.getText().toString(), bundle.getString("doc_id"), file_desc.getText().toString());
                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.enter_file_name), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (!file_desc.getText().toString().equals("")) {
                            if (!file_name.getText().toString().equals("")) {
                                create(file_name.getText().toString(), bundle.getString("doc_id"), file_desc.getText().toString());
                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.enter_file_name), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.orginal) + "\n" + file_desc.getText().toString() +
                        "\n\nCreated By:\n" + getResources().getString(R.string.app_name));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.send_to)));
            }
        });

        translate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Translate_Dialog translate_dialog = new Translate_Dialog();
                translate_dialog.setFileobj(file);
                translate_dialog.show(getFragmentManager(), translate_dialog.getTag());

            }
        });

        return rootview;
    }

    public void create(final String name, final String docid, final String desc) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "addfile", new Response.Listener<String>() {
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
                        Toast.makeText(getContext(), getResources().getString(R.string.file_added), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        try {
                            if (docid.equals("temp")) {
                                getFragmentManager().popBackStack();
                            } else {
                                getFragmentManager().popBackStack();
                                getFragmentManager().popBackStack();
                                MyFiles_Fragment myFiles_fragment = new MyFiles_Fragment();
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("doc_id", bundle.getString("doc_id"));
                                bundle1.putString("doc_name", bundle.getString("doc_name"));
                                myFiles_fragment.setArguments(bundle1);

                                getFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.main_fragment_container, myFiles_fragment, "myFiles_fragment")
                                        .addToBackStack("myFiles_fragment")
                                        .commit();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (docid.equals("temp")) {
                    params.put("user_id", String.valueOf(user.getId()));
                } else {
                    params.put("document_id", docid);
                }
                params.put("name", name);
                params.put("desc", desc);
                params.put("lang", bundle.getString("doc_lang", "none"));
                params.put("lang_to", "none");
                params.put("trans", "none");
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
