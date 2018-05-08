package com.perfecto.apps.ocr.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.Group_Share_Dialog;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.Translate_Dialog;
import com.perfecto.apps.ocr.fragments.File_Fragment;
import com.perfecto.apps.ocr.models.File;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hosam Azzam on 20/08/2017.
 */

public class Files_Adpater extends RecyclerView.Adapter<Files_Adpater.MyViewHolder> {
    ArrayList<File> files = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;
    VolleyClass volley;

    public Files_Adpater(Context context, ArrayList<File> files, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.files = files;
        volley = VolleyClass.getInstance(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_file_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.doc_name.setText(files.get(position).getDocname());
        holder.file_name.setText(files.get(position).getName());
        holder.date.setText(files.get(position).getDate());

        holder.contaniner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File_Fragment file_fragment = new File_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("from", "list");
                file_fragment.setArguments(bundle);
                file_fragment.setFileobj(files.get(holder.getAdapterPosition()));

                fragmentManager
                        .beginTransaction()
                        .add(R.id.main_fragment_container, file_fragment, "file_fragment")
                        .addToBackStack("file_fragment")
                        .commit();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(files.get(holder.getAdapterPosition()), String.valueOf(Perfecto.getUserLoginInfo(context).getId()));
            }
        });

        holder.translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Translate_Dialog translate_dialog = new Translate_Dialog();
                translate_dialog.setFileobj(files.get(holder.getAdapterPosition()));
                translate_dialog.show(fragmentManager, translate_dialog.getTag());
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group_Share_Dialog group_share_dialog = new Group_Share_Dialog();
                Bundle bundle = new Bundle();
                bundle.putString("file_id", files.get(position).getId());
                group_share_dialog.setArguments(bundle);
                group_share_dialog.show(fragmentManager, group_share_dialog.getTag());
            }
        });

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    private void delete(final File file, final String uid) {
        final ProgressDialog loading = ProgressDialog.show(context, "", "Please wait...", false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "deletefile", new Response.Listener<String>() {
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
                        Toast.makeText(context, mainObject.get("Response").toString(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        files.remove(file);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
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
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", uid);
                params.put("file_id", file.getId());
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView file_name, date, doc_name;
        LinearLayout delete, share, translate;
        CardView contaniner;

        MyViewHolder(View view) {
            super(view);

            doc_name = view.findViewById(R.id.doc_name_txt);
            file_name = view.findViewById(R.id.file_name_txt);
            date = view.findViewById(R.id.file_date_txt);
            delete = view.findViewById(R.id.file_delete_btn);
            share = view.findViewById(R.id.file_share_btn);
            translate = view.findViewById(R.id.file_translate_btn);
            contaniner = view.findViewById(R.id.file_item_container);

        }
    }

}
