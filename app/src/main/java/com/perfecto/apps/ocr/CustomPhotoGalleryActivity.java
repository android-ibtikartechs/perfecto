package com.perfecto.apps.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by Hosam Azzam on 01/11/2017.
 */

public class CustomPhotoGalleryActivity extends AppCompatActivity {

    private GridView grdImages;
    private Button btnSelect;

    private ImageAdapter imageAdapter;
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_gallery);
        grdImages = findViewById(R.id.grdImages);
        btnSelect = findViewById(R.id.btnSelect);

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        @SuppressWarnings("deprecation")
        Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            ids[i] = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
            System.out.println("i ===" + i + " :" + arrPath[i]);
        }

        imageAdapter = new ImageAdapter();
        grdImages.setAdapter(imageAdapter);
        imagecursor.close();


        btnSelect.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                for (int i = 0; i < len; i++) {
                    if (thumbnailsselection[i]) {
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";
                    }
                }
                if (cnt == 0) {
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                } else {

                    Log.d("SelectedImages", selectImages);
                    Intent i = new Intent();
                    i.putExtra("data", selectImages);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }


    /**
     * This method used to set bitmap.
     *
     * @param iv represented ImageView
     * @param id represented id
     */

    @SuppressLint("StaticFieldLeak")
    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }


    /**
     * List adapter
     *
     * @author tasol
     */

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
                holder.imgThumb = convertView.findViewById(R.id.imgThumb);
                holder.chkImage = convertView.findViewById(R.id.chkImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);
            holder.chkImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });
            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (thumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        holder.chkImage.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });
            try {
                Glide.with(CustomPhotoGalleryActivity.this).load(new File(arrPath[position])).listener(new RequestListener<File, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                        e.printStackTrace();

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        System.out.println("lllllllllllll");
                        return false;
                    }
                }).error(R.drawable.user_ico).into(holder.imgThumb);


                //   setBitmap(holder.imgThumb, ids[position]);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            holder.chkImage.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }


    /**
     * Inner class
     *
     * @author tasol
     */
    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }

}