package com.perfecto.apps.ocr.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MyGroups_ِAdapter extends RecyclerView.Adapter<MyGroups_ِAdapter.MyViewHolder> {
    ArrayList<Group> groups = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;

    public MyGroups_ِAdapter(Context context, ArrayList<Group> groups, FragmentManager fragmentManager) {
        this.context = context;
        this.groups = groups;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_group_own_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(groups.get(position).getId());
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.container.setOnClickListener(new View.OnClickListener() {
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

        Glide.with(context).load(groups.get(position).getPhoto()).error(R.color.gray_Lite).into(holder.photo);
        holder.name.setText(groups.get(position).getName());

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        RoundCornerImageView photo;
        CardView container;

        MyViewHolder(View view) {
            super(view);

            photo = view.findViewById(R.id.group_photo_img);
            name = view.findViewById(R.id.group_name_txt);
            container = view.findViewById(R.id.group_item_container);

        }
    }
}
