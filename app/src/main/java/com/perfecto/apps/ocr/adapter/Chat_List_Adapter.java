package com.perfecto.apps.ocr.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.fragments.Group_Details_Fragment;
import com.perfecto.apps.ocr.models.Chat;

import java.util.ArrayList;

/**
 * Created by hosam azzam on 14/10/2017.
 */

public class Chat_List_Adapter extends RecyclerView.Adapter<Chat_List_Adapter.MyViewHolder> {
    ArrayList<Chat> chats = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;

    public Chat_List_Adapter(Context context, ArrayList<Chat> chats, FragmentManager fragmentManager) {
        this.context = context;
        this.chats = chats;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_chat_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.msg.setText(chats.get(position).getUser_name() + " : " + chats.get(position).getMsg());
        holder.groupname.setText(chats.get(position).getGroup_name());
        Glide.with(context).load(chats.get(position).getUser_photo());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group_Details_Fragment group_details_fragment = new Group_Details_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("group_id", chats.get(position).getGroup_id());
                group_details_fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .add(R.id.main_fragment_container, group_details_fragment, "group_details_fragment")
                        .addToBackStack("group_details_fragment")
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView groupphoto;
        TextView msg, groupname;
        CardView container;


        MyViewHolder(View view) {
            super(view);
            container = (CardView) view.findViewById(R.id.chat_item_container);
            groupphoto = (ImageView) view.findViewById(R.id.chat_group_photo_img);
            msg = (TextView) view.findViewById(R.id.chat_group_msg_txt);
            groupname = (TextView) view.findViewById(R.id.chat_group_name_txt);
        }
    }

}
