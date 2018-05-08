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
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.fragments.MyFiles_Fragment;
import com.perfecto.apps.ocr.models.Document;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LENOVO on 16/08/2017.
 */

public class Documents_Adapter extends RecyclerView.Adapter<Documents_Adapter.MyViewHolder> {
    ArrayList<Document> documents = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;
    VolleyClass volley;


    public Documents_Adapter(Context context, ArrayList<Document> doucments, FragmentManager fragmentManager) {
        this.context = context;
        this.documents = doucments;
        this.fragmentManager = fragmentManager;
        volley = VolleyClass.getInstance(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_doc_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.name.setText(documents.get(position).getName());
        holder.date.setText(documents.get(position).getDate());
        holder.files.setText(documents.get(position).getNumoffiles() + " File/s");

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(documents.get(holder.getAdapterPosition()), String.valueOf(Perfecto.getUserLoginInfo(context).getId()));
            }
        });

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFiles_Fragment myFiles_fragment = new MyFiles_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("doc_id", documents.get(holder.getAdapterPosition()).getId());
                bundle.putString("doc_name", documents.get(holder.getAdapterPosition()).getName());
                myFiles_fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .add(R.id.main_fragment_container, myFiles_fragment, "myFiles_fragment")
                        .addToBackStack("myFiles_fragment")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    private void delete(final Document doc, final String uid) {
        final ProgressDialog loading = ProgressDialog.show(context, "", "Please wait...", false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "deletedoc", new Response.Listener<String>() {
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
                        documents.remove(doc);
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
                params.put("document_id", doc.getId());
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, files;
        LinearLayout delete;
        CardView container;

        MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.doc_name_txt);
            files = view.findViewById(R.id.doc_files_txt);
            date = view.findViewById(R.id.doc_date_txt);
            delete = view.findViewById(R.id.doc_delete_btn);
            container = view.findViewById(R.id.doc_item_container);


        }
    }
}
