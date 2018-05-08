package com.perfecto.apps.ocr.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.models.User;
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

public class Members_List_Adapter extends RecyclerView.Adapter<Members_List_Adapter.MyViewHolder> {
    ArrayList<User> users = new ArrayList<>();
    Context context;
    VolleyClass volley;
    String gid;

    public Members_List_Adapter(Context context, ArrayList<User> users, String gid) {
        this.context = context;
        this.users = users;
        this.gid = gid;
        this.volley = VolleyClass.getInstance(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_member_h_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(users.get(position).getName());
        Glide.with(context).load(Perfecto.BASE_IMAGE_URL + users.get(position).getPhoto()).into(holder.photo);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMember(String.valueOf(users.get(position).getId()), gid);

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void deleteMember(final String uid, final String gid) {
        final ProgressDialog loading = ProgressDialog.show(context, "", context.getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group/deluser", new Response.Listener<String>() {
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
                        Toast.makeText(context, context.getResources().getString(R.string.member_removed), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, context.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", gid);
                params.put("user_id", uid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, delete;
        ImageView photo;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.mem_name_txt);
            delete = view.findViewById(R.id.mem_remove_txt);
            photo = view.findViewById(R.id.mem_photo_img);
        }
    }
}
