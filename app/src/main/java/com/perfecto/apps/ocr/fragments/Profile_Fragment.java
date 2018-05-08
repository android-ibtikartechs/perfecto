package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.MainActivity;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hosam Azzam on 13/08/2017.
 */

public class Profile_Fragment extends Fragment {
    VolleyClass volley;
    CircleImageView user_photo;
    TextView email, name, username, phone, signout;
    User user;
    CardView change, mydocs;
    ImageView home_ico;
    Bitmap bitmap = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.profile_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());

        home_ico = getActivity().findViewById(R.id.toolbar_home_ico);
        home_ico.setVisibility(View.VISIBLE);
        home_ico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new Home_Fragment(), "Home_Fragment")
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        email = rootview.findViewById(R.id.user_email_txt);
        name = rootview.findViewById(R.id.user_name_txt);
        username = rootview.findViewById(R.id.user_username_txt);
        phone = rootview.findViewById(R.id.user_phone_txt);
        signout = rootview.findViewById(R.id.app_signout_txt);
        user_photo = rootview.findViewById(R.id.user_profile_img);
        change = rootview.findViewById(R.id.change_btn);
        mydocs = rootview.findViewById(R.id.go_to_docs_btn);

        user = Perfecto.getUserLoginInfo(getContext());
        if (user != null) {
            email.setText(user.getEmail());
            name.setText(getContext().getResources().getString(R.string.name) + " : " + user.getName());
            phone.setText(user.getPhone());
            username.setText(getContext().getResources().getString(R.string.username) + " : " + user.getUsername());
            if (user.getPhoto().contains("uploads/")) {
                Glide.with(getContext()).load(Perfecto.BASE_IMAGE_URL + user.getPhoto()).error(R.drawable.user_ico).into(user_photo);
            } else {
                Glide.with(getContext()).load(user.getPhoto()).error(R.drawable.user_ico).into(user_photo);
            }
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle(getResources().getString(R.string.select_photo));

                builder.setPositiveButton(getResources().getString(R.string.camera), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 2222);
                    }

                });
                builder.setNegativeButton(getResources().getString(R.string.gallery), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_pic)), 1111);
                    }

                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        mydocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.main_fragment_container, new MyDocs_Fragment(), "MyDocs_Fragment")
                        .addToBackStack("MyDocs_Fragment")
                        .commit();
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Perfecto.unRegisterUserLogin(getContext());
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return rootview;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.i("Image", encodedImage);
        return encodedImage;
    }

    public void uploadImage() {
        final ProgressDialog loading = ProgressDialog.show(getContext(), getResources().getString(R.string.uploading), getResources().getString(R.string.pls_wait), false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "updateprofile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONParser jsonParser = new JSONParser();
                            Object obj;
                            JSONObject mainObject;
                            obj = jsonParser.parse(response);
                            mainObject = (JSONObject) obj;

                            JSONObject statusObject = (JSONObject) jsonParser.parse(mainObject.get("status").toString());
                            if (statusObject.get("type").equals("success") || statusObject.get("code").equals(0)) {

                                JSONObject responseObject = (JSONObject) jsonParser.parse(mainObject.get("Response").toString());

                                User user = new User();
                                user.setId(Long.valueOf(responseObject.get("id").toString()));
                                user.setName(responseObject.get("name").toString());
                                user.setPhoto(responseObject.get("photo").toString());
                                user.setPhone(responseObject.get("phone").toString());
                                user.setUsername(responseObject.get("username").toString());
                                user.setEmail(responseObject.get("email").toString());

                                Perfecto.registerUserLogin(getContext(), user);
                                if (responseObject.get("photo").toString().contains("uploads/")) {
                                    Glide.with(getContext()).load(Perfecto.BASE_IMAGE_URL + responseObject.get("photo").toString()).error(R.drawable.user_ico).into(user_photo);
                                } else {
                                    Glide.with(getContext()).load(responseObject.get("photo").toString()).error(R.drawable.user_ico).into(user_photo);
                                }

                                Toast.makeText(getContext(), getResources().getString(R.string.done), Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                            }
                            loading.dismiss();
                            //Showing toast message of the response

                            System.out.println(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        volleyError.printStackTrace();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                Map<String, String> params = new Hashtable<>();
                params.put("photo", image);
                params.put("email", user.getEmail());
                params.put("phone", user.getPhone());
                params.put("name", user.getName());
                params.put("username", user.getUsername());
                params.put("user_id", String.valueOf(user.getId()));

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 3, 2));
        volley.getQueue().add(stringRequest);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2222 && resultCode == RESULT_OK) {
            //Getting the Bitmap from Camera
            try {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap = thumbnail;
                uploadImage();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }


}
