package com.perfecto.apps.ocr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.models.Group;
import com.perfecto.apps.ocr.tools.Perfecto;

import java.util.ArrayList;

/**
 * Created by hosam azzam on 30/10/2017.
 */

public class Group_List_Adapter extends BaseAdapter {
    Context context;
    ArrayList<Group> groups = new ArrayList<>();
    OnItemClickListener listener;

    public Group_List_Adapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    public void addOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
                    .inflate(R.layout.card_group_simple_item, parent, false);
        }

        ImageView photo = convertView.findViewById(R.id.group_photo_img);
        TextView name = convertView.findViewById(R.id.group_name_txt);
        TextView count = convertView.findViewById(R.id.group_mem_count_txt);
        RelativeLayout container = convertView.findViewById(R.id.group_item_container);

        Glide.with(context).load(Perfecto.BASE_IMAGE_URL + groups.get(position).getPhoto()).asBitmap().into(photo);
        name.setText(groups.get(position).getName());
        count.setText(groups.get(position).getMemberscount() + " " + context.getResources().getString(R.string.member));

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(groups.get(position).getId());
            }
        });

        return convertView;
    }

    public interface OnItemClickListener {
        void onItemClick(String id);
    }
}
