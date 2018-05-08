package com.perfecto.apps.ocr.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.perfecto.apps.ocr.R;

import java.util.ArrayList;

/**
 * Created by Hosam Azzam on 06/11/2017.
 */

public class Slider_Pager_Adapter extends PagerAdapter {
    Activity activity;
    ArrayList<Integer> image_arraylist;
    private LayoutInflater layoutInflater;

    public Slider_Pager_Adapter(Activity activity, ArrayList<Integer> image_arraylist) {
        this.activity = activity;
        this.image_arraylist = image_arraylist;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.layout_slider, container, false);
        ImageView im_slider = view.findViewById(R.id.im_slider);
        im_slider.setImageResource(image_arraylist.get(position));
        im_slider.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return image_arraylist.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}