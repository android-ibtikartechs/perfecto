package com.perfecto.apps.ocr.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hosamazzam.customviews.RoundCornerImageView;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.fragments.Group_Details_Fragment;
import com.perfecto.apps.ocr.models.Group;

import java.util.ArrayList;

/**
 * Created by Hosam Azzam on 08/10/2017.
 */

public class MyJoinedGroups_Adapter extends BaseAdapter {
    ArrayList<Group> groups = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;

    public MyJoinedGroups_Adapter(Context context, ArrayList<Group> groups, FragmentManager fragmentManager) {
        this.context = context;
        this.groups = groups;
        this.fragmentManager = fragmentManager;
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
        TextView join = convertView.findViewById(R.id.group_join_txt);

        Glide.with(context).load(groups.get(position).getPhoto()).error(R.color.gray_Lite).into(photo);
        name.setText(groups.get(position).getName());
        memcount.setText(String.valueOf(groups.get(position).getMemberscount()));
        join.setVisibility(View.GONE);


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group_Details_Fragment group_details_fragment = new Group_Details_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("group_id", groups.get(position).getId());
                group_details_fragment.setArguments(bundle);
                fragmentManager
                        .beginTransaction()
                        .add(R.id.main_fragment_container, group_details_fragment, "group_details_fragment")
                        .addToBackStack("group_details_fragment")
                        .commit();
            }
        });

        return convertView;
    }
}
