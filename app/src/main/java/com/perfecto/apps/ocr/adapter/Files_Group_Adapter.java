package com.perfecto.apps.ocr.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.Translate_Dialog;
import com.perfecto.apps.ocr.fragments.File_Fragment;
import com.perfecto.apps.ocr.models.File;
import com.perfecto.apps.ocr.tools.VolleyClass;

import java.util.ArrayList;

/**
 * Created by Hosam Azzam on 30/10/2017.
 */

public class Files_Group_Adapter extends BaseAdapter {

    ArrayList<File> files = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;
    VolleyClass volley;

    public Files_Group_Adapter(Context context, ArrayList<File> files, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.files = files;
        volley = VolleyClass.getInstance(context);
    }


    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(files.get(position).getId());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_file_simple_item, parent, false);
        }

        TextView file_name = convertView.findViewById(R.id.file_name_txt);
        TextView date = convertView.findViewById(R.id.file_date_txt);
        LinearLayout translate = convertView.findViewById(R.id.file_translate_btn);
        CardView contaniner = convertView.findViewById(R.id.file_item_container);

        file_name.setText(files.get(position).getName());
        date.setText(files.get(position).getDate());

        contaniner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File_Fragment file_fragment = new File_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("from", "list");
                file_fragment.setArguments(bundle);
                file_fragment.setFileobj(files.get(position));

                fragmentManager
                        .beginTransaction()
                        .add(R.id.main_fragment_container, file_fragment, "file_fragment")
                        .addToBackStack("file_fragment")
                        .commit();
            }
        });

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Translate_Dialog translate_dialog = new Translate_Dialog();
                translate_dialog.setFileobj(files.get(position));
                translate_dialog.show(fragmentManager, translate_dialog.getTag());
            }
        });


        return convertView;
    }
}
