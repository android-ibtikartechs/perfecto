package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.perfecto.apps.ocr.models.File;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hosam Azzam on 21/08/2017.
 */

public class File_Fragment extends Fragment {
    TextView name, desc, trans;
    ImageView update, share, back, home_ico;
    VolleyClass volley;
    File fileobj;
    Bundle bundle;
    User user;

    public void setFileobj(File fileobj) {
        this.fileobj = fileobj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.result_translate_fragment, container, false);
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
        bundle = getArguments();

        name = rootview.findViewById(R.id.file_name_txt);
        desc = rootview.findViewById(R.id.file_desc_txt);
        trans = rootview.findViewById(R.id.file_translate_txt);

        update = rootview.findViewById(R.id.file_save_btn);
        share = rootview.findViewById(R.id.file_share_btn);
        back = rootview.findViewById(R.id.file_back_btn);

        name.setText(fileobj.getName());
        desc.setText(fileobj.getDesc());
        trans.setText(fileobj.getTrans());

        if (bundle.getString("from").equals("list")) {
            update.setVisibility(View.GONE);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileobj.getId().equals("temp")) {
                    createFile(fileobj);
                } else {
                    updateFile(fileobj);
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if (fileobj.getTrans().equals("none")) {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.orginal) + "\n" + fileobj.getDesc() + "\n\n");
                } else {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.orginal) + "\n" + fileobj.getDesc() +
                            "\n\n" + getResources().getString(R.string.translation) + "\n" + fileobj.getTrans() +
                            "\n\nCreated By:\n" + getResources().getString(R.string.app_name));
                }
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.send_to)));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return rootview;
    }

    public void createFile(final File file) {
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

                        update.setVisibility(View.GONE);


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

                params.put("user_id", String.valueOf(user.getId()));
                params.put("name", file.getName());
                params.put("desc", file.getDesc());
                params.put("lang", bundle.getString("lang_from"));
                params.put("lang_to", bundle.getString("lang_to"));
                params.put("trans", file.getTrans());
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    public void updateFile(final File file) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "updatefile", new Response.Listener<String>() {
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
                        Toast.makeText(getContext(), getResources().getString(R.string.file_updated), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        update.setVisibility(View.GONE);

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
                params.put("desc", file.getDesc());
                params.put("trans", file.getTrans());
                params.put("name", file.getName());
                params.put("lang", bundle.getString("lang_from"));
                params.put("lang_to", bundle.getString("lang_to"));
                params.put("file_id", file.getId());
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
