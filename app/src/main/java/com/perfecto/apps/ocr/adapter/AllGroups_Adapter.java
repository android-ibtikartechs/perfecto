package com.perfecto.apps.ocr.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hosamazzam.customviews.RoundCornerImageView;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.models.Group;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hosam Azzam on 08/10/2017.
 */

public class AllGroups_Adapter extends BaseAdapter {
    public onJoinClickListner onJoinClickListner;
    ArrayList<Group> groups = new ArrayList<>();
    Context context;
    User user;
    VolleyClass volley;
    FragmentManager fragmentManager;

    public AllGroups_Adapter(Context context, ArrayList<Group> groups, FragmentManager fragmentManager) {
        this.context = context;
        this.groups = groups;
        this.user = Perfecto.getUserLoginInfo(context);
        volley = VolleyClass.getInstance(context);
        this.fragmentManager = fragmentManager;
    }

    public void setOnJoinClickListner(onJoinClickListner onJoinClickListner) {
        this.onJoinClickListner = onJoinClickListner;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(groups.get(position).getId());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_group_item, parent, false);
        }


        RoundCornerImageView photo = convertView.findViewById(R.id.group_photo_img);
        TextView name = convertView.findViewById(R.id.group_name_txt);
        TextView memcount = convertView.findViewById(R.id.group_memcount_txt);
        final TextView join = convertView.findViewById(R.id.group_join_txt);

        Glide.with(context).load(groups.get(position).getPhoto()).error(R.color.gray_Lite).into(photo);
        name.setText(groups.get(position).getName());
        memcount.setText(String.valueOf(groups.get(position).getMemberscount()));

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join(String.valueOf(user.getId()), groups.get(position).getId());
            }
        });


        return convertView;
    }

    public void join(final String uid, final String gid) {
        final ProgressDialog loading = ProgressDialog.show(context, "", context.getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group/sendrequest", new Response.Listener<String>() {
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
                        Toast.makeText(context, context.getResources().getString(R.string.send_join_group), Toast.LENGTH_SHORT).show();
                        onJoinClickListner.onJoinClick();
                        loading.dismiss();
                    } else {
                        JSONObject responseObject = (JSONObject) jsonParser.parse(mainObject.get("Response").toString());
                        switch (responseObject.get("flag").toString()) {
                            case "0":
                                Toast.makeText(context, context.getResources().getString(R.string.add_by_admin), Toast.LENGTH_SHORT).show();
                                break;
                            case "1":
                                Toast.makeText(context, context.getResources().getString(R.string.add_by_request), Toast.LENGTH_SHORT).show();
                                break;
                            case "2":
                                Toast.makeText(context, context.getResources().getString(R.string.request_pending), Toast.LENGTH_SHORT).show();
                                break;
                            case "3":
                                Toast.makeText(context, context.getResources().getString(R.string.request_decline), Toast.LENGTH_SHORT).show();
                                break;
                        }

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
                params.put("user_id", uid);
                params.put("group_id", gid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    public interface onJoinClickListner {
        void onJoinClick();
    }
}
