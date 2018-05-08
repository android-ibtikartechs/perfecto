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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.fragments.File_Fragment;
import com.perfecto.apps.ocr.models.File;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hosam Azzam on 21/08/2017.
 */

public class Translate_Dialog extends DialogFragment {
    VolleyClass volley;
    File fileobj;
    Spinner lang_to, lang_from;
    String from = "", to = "";
    int from_pos = 0, to_pos = 0;
    TextView title;
    CardView translate_btn;

    public void setFileobj(File fileobj) {
        this.fileobj = fileobj;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_translate, container, false);
        try {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Perfecto.initLangCode();
        volley = VolleyClass.getInstance(getContext());

        title = rootView.findViewById(R.id.file_name_txt);
        lang_to = rootView.findViewById(R.id.translate_to_spin);
        lang_from = rootView.findViewById(R.id.translate_from_spin);
        translate_btn = rootView.findViewById(R.id.translate_btn);
        title.setText(fileobj.getName());

        if (!fileobj.getSourceLangPos().equals("none")) {
            System.out.println("pos : " + fileobj.getSourceLangPos());
            lang_from.setSelection(Integer.valueOf(fileobj.getSourceLangPos()));
            if (fileobj.getSourceLangPos().equals("3")) {
                lang_to.setSelection(20);
            } else {
                lang_to.setSelection(3);
            }
        }

        translate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!from.equals("") && !to.equals("")) {
                    translate(fileobj, to, from);
                }
            }
        });

        lang_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String lang_Name = adapterView.getItemAtPosition(position).toString();
                to = Perfecto.LangCode.get(lang_Name);
                to_pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lang_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String lang_Name = adapterView.getItemAtPosition(position).toString();
                from = Perfecto.LangCode.get(lang_Name);
                from_pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return rootView;
    }

    private void translate(final File file, final String lang_to, final String lang_from) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://translation.googleapis.com/language/translate/v2", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONParser jsonParser = new JSONParser();
                    Object obj;
                    JSONObject mainObject;
                    obj = jsonParser.parse(response);
                    mainObject = (JSONObject) obj;
                    JSONObject dataObject = (JSONObject) jsonParser.parse(mainObject.get("data").toString());
                    JSONArray translations = (JSONArray) jsonParser.parse(dataObject.get("translations").toString());
                    if (translations.size() != 0) {
                        JSONObject transItem = (JSONObject) jsonParser.parse(translations.get(0).toString());
                        file.setTrans(transItem.get("translatedText").toString());
                        dismiss();
                        File_Fragment file_fragment = new File_Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("from", "translate");
                        bundle.putString("lang_from", String.valueOf(from_pos));
                        bundle.putString("lang_to", String.valueOf(to_pos));
                        System.out.println("lang_from" + String.valueOf(from_pos));
                        file.setSourceLangPos(String.valueOf(from_pos));
                        file_fragment.setArguments(bundle);
                        file_fragment.setFileobj(file);

                        getFragmentManager()
                                .beginTransaction()
                                .add(R.id.main_fragment_container, file_fragment, "file_fragment")
                                .addToBackStack("file_fragment")
                                .commit();
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
                params.put("target", lang_to);
                params.put("q", file.getDesc());
                params.put("format", "text");
                params.put("source", lang_from);
                params.put("key", "AIzaSyCUcLyaMXO-a4BK_daKWC2Vr37wLTZqlNA");
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
