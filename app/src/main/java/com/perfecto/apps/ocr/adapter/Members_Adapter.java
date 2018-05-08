package com.perfecto.apps.ocr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;

import java.util.ArrayList;

/**
 * Created by Hosam Azzam on 12/10/2017.
 */

public class Members_Adapter extends RecyclerView.Adapter<Members_Adapter.MyViewHolder> {
    ArrayList<User> users = new ArrayList<>();
    Context context;

    public Members_Adapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_member_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.name.setText(users.get(position).getName());
        Glide.with(context).load(Perfecto.BASE_IMAGE_URL + users.get(position).getPhoto()).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView photo;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.mem_name_txt);
            photo = (ImageView) view.findViewById(R.id.mem_photo_img);
        }
    }
}
