package com.perfecto.apps.ocr.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.models.Msg;
import com.perfecto.apps.ocr.tools.Perfecto;

import java.util.ArrayList;

/**
 * Created by hosam azzam on 14/10/2017.
 */
public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.MyViewHolder> {

    ArrayList<Msg> msgs = new ArrayList<>();
    Context context;
    FragmentManager fragmentManager;

    public Chat_Adapter(Context context, ArrayList<Msg> msgs) {
        this.context = context;
        this.msgs = msgs;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_chat_msg_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Glide.with(context).load(Perfecto.BASE_IMAGE_URL + msgs.get(position).getUser_photo()).dontAnimate().into(holder.userphoto);

        if (!msgs.get(position).getMsg().equals("")) {
            holder.msg.setVisibility(View.VISIBLE);
            holder.msg.setText(msgs.get(position).getUser_name() + "\n" + msgs.get(position).getMsg());
        }
        holder.time.setText(msgs.get(position).getDate());


    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userphoto;
        TextView msg, time;


        MyViewHolder(View view) {
            super(view);
            userphoto = (ImageView) view.findViewById(R.id.user_photo_img);
            msg = (TextView) view.findViewById(R.id.chat_msg);
            time = (TextView) view.findViewById(R.id.chat_time);
        }
    }
}