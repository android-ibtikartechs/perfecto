package com.perfecto.apps.ocr.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.Slider_Pager_Adapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hosam Azzam on 13/08/2017.
 */

public class Guide_fragment extends DialogFragment {
    Slider_Pager_Adapter sliderPagerAdapter;
    ArrayList<Integer> slider_image_list;
    int page_position = 0;
    private ViewPager images_slider;
    private LinearLayout pages_dots;
    private TextView[] dots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_guide, container);
        //  getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // method for initialisation
        init(rootView);

// method for adding indicators
        addBottomDots(0);

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == slider_image_list.size()) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                images_slider.setCurrentItem(page_position, true);
            }
        };

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 9000);

        rootView.findViewById(R.id.guide_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    private void init(View v) {
        images_slider = v.findViewById(R.id.image_page_slider);
        pages_dots = v.findViewById(R.id.page_dots);

        slider_image_list = new ArrayList<>();

//Add few items to slider_image_list ,this should contain url of images which should be displayed in slider
// here i am adding few sample image links, you can add your own

        slider_image_list.add(R.drawable.perfectoguide1);
        slider_image_list.add(R.drawable.perfectoguide2);
        slider_image_list.add(R.drawable.perfectoguide3);
        slider_image_list.add(R.drawable.perfectoguide4);
        slider_image_list.add(R.drawable.perfectoguide5);
        slider_image_list.add(R.drawable.perfectoguide6);
        slider_image_list.add(R.drawable.perfectoguide7);
        slider_image_list.add(R.drawable.perfectoguide8);
        slider_image_list.add(R.drawable.perfectoguide9);


        sliderPagerAdapter = new Slider_Pager_Adapter(getActivity(), slider_image_list);
        images_slider.setAdapter(sliderPagerAdapter);
        images_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void addBottomDots(int currentPage) {
        try {
            if (getContext() != null) {
                dots = new TextView[slider_image_list.size()];

                pages_dots.removeAllViews();
                pages_dots.setPadding(0, 0, 0, 20);
                for (int i = 0; i < dots.length; i++) {


                    dots[i] = new TextView(getContext());
                    dots[i].setText(Html.fromHtml("&#8226;"));
                    dots[i].setTextSize(35);
                    dots[i].setTextColor(Color.parseColor("#000000"));
                    pages_dots.addView(dots[i]);

                }

                if (dots.length > 0)
                    dots[currentPage].setTextColor(Color.parseColor("#357dc9"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}