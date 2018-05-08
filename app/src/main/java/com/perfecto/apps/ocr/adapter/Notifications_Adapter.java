package com.perfecto.apps.ocr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.Request_Group_Dialog;
import com.perfecto.apps.ocr.fragments.Group_Details_Fragment;
import com.perfecto.apps.ocr.models.Notification;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import java.util.ArrayList;

/**
 * Created by LENOVO on 16/08/2017.
 */

public class Notifications_Adapter extends RecyclerView.Adapter<Notifications_Adapter.MyViewHolder> {
    ArrayList<Notification> notifications = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;
    VolleyClass volley;


    public Notifications_Adapter(Context context, ArrayList<Notification> notifications, FragmentManager fragmentManager) {
        this.context = context;
        this.notifications = notifications;
        this.fragmentManager = fragmentManager;
        volley = VolleyClass.getInstance(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_notification_join_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(notifications.get(position).getSender_name());
        holder.date.setText(notifications.get(position).getDate());
        holder.msg.setText(notifications.get(position).getText());

        Glide.with(context).load(Perfecto.BASE_IMAGE_URL + notifications.get(position).getPhoto()).into(holder.photo);


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifications.get(position).getFlag().equals("1")) {
                    Group_Details_Fragment group_details_fragment = new Group_Details_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("group_id", notifications.get(position).getGroup_id());
                    group_details_fragment.setArguments(bundle);
                    fragmentManager
                            .beginTransaction()
                            .add(R.id.main_fragment_container, group_details_fragment, "group_details_fragment")
                            .addToBackStack("group_details_fragment")
                            .commit();

                } else {
                    Request_Group_Dialog request_group_dialog = new Request_Group_Dialog();
                    request_group_dialog.setNotifyObj(notifications.get(position));
                    request_group_dialog.show(fragmentManager, request_group_dialog.getTag());
                }

            }
        });
        if (notifications.get(position).getIs_read().equals("0")) {
            holder.container.setCardBackgroundColor(Color.parseColor("#dcdcdc"));
        } else {
            holder.container.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, msg, date;
        CircleImageView photo;
        CardView container;

        MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.notification_user_name_txt);
            msg = view.findViewById(R.id.notification_user_msg_txt);
            date = view.findViewById(R.id.notification_date_txt);
            photo = view.findViewById(R.id.notification_user_photo_img);
            container = view.findViewById(R.id.notification_item_container);
        }
    }
}
