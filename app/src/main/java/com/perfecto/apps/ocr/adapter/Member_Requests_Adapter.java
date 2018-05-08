package com.perfecto.apps.ocr.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hosam Azzam on 12/10/2017.
 */

public class Member_Requests_Adapter extends BaseAdapter {
    ArrayList<com.perfecto.apps.ocr.models.Request> requests = new ArrayList<>();
    Context context;
    VolleyClass volley;
    onChangeRequestStatus listenr;

    public Member_Requests_Adapter(Context context, ArrayList<com.perfecto.apps.ocr.models.Request> requests) {
        this.context = context;
        this.requests = requests;
        volley = VolleyClass.getInstance(context);
    }

    public void addOnChangeRequestStatus(onChangeRequestStatus listenr) {
        this.listenr = listenr;
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int position) {
        return requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(requests.get(position).getId());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_member_request_item, parent, false);
        }

        ImageView photo = convertView.findViewById(R.id.mem_photo_img);
        TextView name = convertView.findViewById(R.id.mem_name_txt);
        TextView accept = convertView.findViewById(R.id.mem_accept_txt);
        TextView decline = convertView.findViewById(R.id.mem_decline_txt);

        name.setText(requests.get(position).getUser_name());
        Glide.with(context).load(Perfecto.BASE_IMAGE_URL + requests.get(position).getUser_photo()).into(photo);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRequestStatus(requests.get(position).getId(), "1", requests.get(position));
                requests.remove(requests.get(position));
                listenr.onChange();
                notifyDataSetChanged();

            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRequestStatus(requests.get(position).getId(), "3", requests.get(position));
                requests.remove(requests.get(position));
                listenr.onChange();
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    public void changeRequestStatus(final String reqid, final String flag, final com.perfecto.apps.ocr.models.Request requestObj) {
        final ProgressDialog loading = ProgressDialog.show(context, "", context.getResources().getString(R.string.pls_wait), false, false);
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
                            Toast.makeText(context, context.getResources().getString(R.string.request_accepted), Toast.LENGTH_SHORT).show();
                        }
                        if (flag.equals("3")) {
                            Toast.makeText(context, context.getResources().getString(R.string.request_rejected), Toast.LENGTH_SHORT).show();
                        }
                        requests.remove(requestObj);
                        notifyDataSetChanged();


                        loading.dismiss();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(context, context.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    public interface onChangeRequestStatus {
        void onChange();
    }
}
