package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.perfecto.apps.ocr.About_Dialog;
import com.perfecto.apps.ocr.MainActivity;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.LoginCheckHandler;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.SwipeTouchListener;
import com.perfecto.apps.ocr.tools.VolleyClass;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Hosam Azzam on 13/08/2017.
 */

public class Home_Fragment extends Fragment {

    VolleyClass volley;
    LinearLayout capture, profile, home_layout;
    GridLayout grid_layout;
    RelativeLayout about, exit, setting, files, groups, lang, notification;
    java.io.File file;
    Uri uri;
    Intent CamIntent, GalIntent;
    CircleImageView user_img;
    TextView user_name;
    ImageView home_ico;
    ImageView share;
    Intent intent;
    boolean IS_MULTIPLE = false;
    ArrayList<Uri> imageResultUri = new ArrayList<>();
    Map<Integer, String> FinalTextExtracted = new HashMap<>();

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.home_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());

        home_ico = getActivity().findViewById(R.id.toolbar_home_ico);
        home_ico.setVisibility(View.GONE);

        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleSendImage(intent); // Handle single image being sent
                }
            } else if (Intent.ACTION_VIEW.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleSendImage(intent); // Handle single image being sent
                }
            }
        }

        capture = rootview.findViewById(R.id.capture_layer);
        about = rootview.findViewById(R.id.about_layer);
        exit = rootview.findViewById(R.id.exit_layer);
        setting = rootview.findViewById(R.id.setting_layer);
        files = rootview.findViewById(R.id.files_layer);
        groups = rootview.findViewById(R.id.groups_layer);
        lang = rootview.findViewById(R.id.lang_layer);
        notification = rootview.findViewById(R.id.notification_layer);
        profile = rootview.findViewById(R.id.profile_layer);
        home_layout = rootview.findViewById(R.id.home_layout_container);
        grid_layout = rootview.findViewById(R.id.grid_layout_container);
        share = rootview.findViewById(R.id.home_share_btn);

        user_img = rootview.findViewById(R.id.home_user_photo_img);
        user_name = rootview.findViewById(R.id.home_user_name_txt);


        User curuser = Perfecto.getUserLoginInfo(getContext());
        if (curuser != null) {
            user_name.setText(curuser.getName());
            if (curuser.getPhoto().contains("uploads/")) {
                Glide.with(getContext()).load(Perfecto.BASE_IMAGE_URL + curuser.getPhoto()).dontAnimate().error(R.drawable.user_ico).into(user_img);
            } else {
                Glide.with(getContext()).load(curuser.getPhoto()).dontAnimate().error(R.drawable.user_ico).into(user_img);
            }
        }


        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle(getResources().getString(R.string.select_photo));

                builder.setPositiveButton(getResources().getString(R.string.camera), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClickImageFromCamera();
                    }

                });
                builder.setNegativeButton(getResources().getString(R.string.gallery), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GetImageFromGallery();
                    }

                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                About_Dialog about_dialog = new About_Dialog();
                about_dialog.show(getFragmentManager(), about_dialog.getTag());
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guide_fragment guide_fragment = new Guide_fragment();
                guide_fragment.show(getFragmentManager(), guide_fragment.getTag());
            }
        });

        files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginCheckHandler loginCheckHandler = new LoginCheckHandler(getContext());
                if (loginCheckHandler.checkLogin()){
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.main_fragment_container, new MyDocs_Fragment(), "MyDocs_Fragment")
                            .addToBackStack("MyDocs_Fragment")
                            .commit();
                }
                /* if (!Perfecto.getUserLoginState(getActivity())) {
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.main_fragment_container, new Login_Fragment(), "Login_Fragment")
                            .addToBackStack("Login_Fragment")
                            .commit();
                    Toast.makeText(getContext(), "You are not login", Toast.LENGTH_SHORT).show();
                }*/ /*else {
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.main_fragment_container, new MyDocs_Fragment(), "MyDocs_Fragment")
                            .addToBackStack("MyDocs_Fragment")
                            .commit();
                }*/
            }
        });

        groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //      Toast.makeText(getContext(), "Comming soon", Toast.LENGTH_SHORT).show();
                LoginCheckHandler loginCheckHandler = new LoginCheckHandler(getContext());
                if (loginCheckHandler.checkLogin()){
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new Groups_Fragment(), "Groups_Fragment")
                            .addToBackStack("Groups_Fragment")
                            .commit();
                }
                /*getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new Groups_Fragment(), "Groups_Fragment")
                        .addToBackStack("Groups_Fragment")
                        .commit();*/

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginCheckHandler loginCheckHandler = new LoginCheckHandler(getContext());
                if (loginCheckHandler.checkLogin()){
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.main_fragment_container, new Profile_Fragment(), "Profile_Fragment")
                            .addToBackStack("Profile_Fragment")
                            .commit();
                }

                /*if (!Perfecto.getUserLoginState(getContext())) {
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.main_fragment_container, new Login_Fragment(), "Login_Fragment")
                            .addToBackStack("Login_Fragment")
                            .commit();
                    Toast.makeText(getContext(), "You are not login", Toast.LENGTH_SHORT).show();
                } else {
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.main_fragment_container, new Profile_Fragment(), "Profile_Fragment")
                            .addToBackStack("Profile_Fragment")
                            .commit();
                }*/
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                final String LANG_CURRENT = preferences.getString("Language", "en");
                if (LANG_CURRENT.equals("ar")) {
                    changeLang(getContext(), "en");
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
                if (LANG_CURRENT.equals("en")) {
                    changeLang(getContext(), "ar");
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(getContext(), "Comming soon", Toast.LENGTH_SHORT).show();
                LoginCheckHandler loginCheckHandler = new LoginCheckHandler(getContext());
                if (loginCheckHandler.checkLogin()){
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, new Notification_Fragment(), "Notification_Fragment")
                            .addToBackStack("Notification_Fragment")
                            .commit();
                }
                /*
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new Notification_Fragment(), "Notification_Fragment")
                        .addToBackStack("Notification_Fragment")
                        .commit();*/

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.perfecto.apps.ocr");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.send_to)));
            }
        });

        SwipeTouchListener swipeTouchListener = new SwipeTouchListener();
        swipeTouchListener.setActivity(getActivity());
        swipeTouchListener.setonSwipTouchListner(new SwipeTouchListener.onSwipTouchListner() {
            @Override
            public void onBottomToTopSwipe() {
            }

            @Override
            public void onTopToBottomSwipe() {
            }

            @Override
            public void onRightToLeftSwipe() {
                ClickImageFromCamera();
            }

            @Override
            public void onLeftToRightSwipe() {
                GetImageFromGallery();
            }
        });

        home_layout.setOnTouchListener(swipeTouchListener);
        grid_layout.setOnTouchListener(swipeTouchListener);


        return rootview;
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            CropImage.activity(imageUri)
                    .start(getContext(), Home_Fragment.this);
        }
    }

    public void ClickImageFromCamera() {
        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        file = new java.io.File(Environment.getExternalStorageDirectory(),
                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);
        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        CamIntent.putExtra("return-data", true);
        startActivityForResult(CamIntent, 0);
    }

    public void GetImageFromGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle(getResources().getString(R.string.select_multiple_photo));

            builder.setPositiveButton(getResources().getString(R.string.single), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GetSingleImageFromGallery();
                }

            });
            builder.setNegativeButton(getResources().getString(R.string.multiple), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GetMultipleImageFromGallery();
                }

            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            GetSingleImageFromGallery();
        }
    }

    public void GetSingleImageFromGallery() {
        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(GalIntent, getResources().getString(R.string.select_gallery)), 2);
    }

    public void GetMultipleImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_gallery)), 3);

    }

    public void prepareImage(Uri resultUri, String lang, String index) {
        try {
            Bitmap bitmap;
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            fetchText(encodedImage, lang, index, true, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepareMultipleImage(String lang, String index) {
        for (int i = 0; i < imageResultUri.size(); i++) {
            try {
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageResultUri.get(i));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                fetchText(encodedImage, lang, index, false, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchText(String Image, final String Lang, final String langindex, final boolean is_single_image, final int page) {
        try {
            final ProgressDialog loading = ProgressDialog.show(getContext(), getResources().getString(R.string.ocr_title), getResources().getString(R.string.ocr_msg), false, false);
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            String URL = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyCUcLyaMXO-a4BK_daKWC2Vr37wLTZqlNA";
            final String requestBody = "{\n" +
                    "  \"requests\":[\n" +
                    "    {\n" +
                    "      \"image\":{\n" +
                    "  \"content\":\"" + Image + "\"" +
                    "      },\n" +
                    "      \"features\":[\n" +
                    "        {\n" +
                    "          \"type\":\"DOCUMENT_TEXT_DETECTION\",\n" +
                    "          \"maxResults\":1\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"imageContext\":{\n" +
                    "    \t  \"languageHints\":\"" + Lang + "\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            //     System.out.println(requestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (is_single_image) {
                        Add_File_Fragment add_file_fragment = new Add_File_Fragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("response", extractText(response));
                        bundle1.putString("doc_id", "temp");
                        bundle1.putString("doc_name", "temp");
                        bundle1.putString("doc_lang", langindex);
                        add_file_fragment.setArguments(bundle1);

                        getFragmentManager()
                                .beginTransaction()
                                .add(R.id.main_fragment_container, add_file_fragment, "add_file_fragment")
                                .addToBackStack("add_file_fragment")
                                .commit();
                        loading.dismiss();
                        Log.i("VOLLEY", response);
                    } else {
                        FinalTextExtracted.put(page, extractText(response));
                        Toast.makeText(getContext(), "Page " + FinalTextExtracted.size() + " complete", Toast.LENGTH_LONG).show();
                        if (FinalTextExtracted.size() == imageResultUri.size()) {
                            Add_File_Fragment add_file_fragment = new Add_File_Fragment();
                            Bundle bundle1 = new Bundle();

                            bundle1.putString("response", FinalizeTextExtraction());
                            bundle1.putString("doc_id", "temp");
                            bundle1.putString("doc_name", "temp");
                            bundle1.putString("doc_lang", langindex);
                            add_file_fragment.setArguments(bundle1);

                            getFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.main_fragment_container, add_file_fragment, "add_file_fragment")
                                    .addToBackStack("add_file_fragment")
                                    .commit();
                        }
                        loading.dismiss();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    loading.dismiss();

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 3, 2));

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    public String extractText(String data) {
        try {
            JSONParser jsonParser = new JSONParser();
            Object obj;
            JSONObject mainObject;
            obj = jsonParser.parse(data);
            mainObject = (JSONObject) obj;

            JSONArray responses = (JSONArray) jsonParser.parse(mainObject.get("responses").toString());
            if (responses.size() != 0) {
                JSONObject textAnnotations = (JSONObject) jsonParser.parse(responses.get(0).toString());
                JSONArray TextList = (JSONArray) jsonParser.parse(textAnnotations.get("textAnnotations").toString());
                if (TextList.size() != 0) {
                    JSONObject text = (JSONObject) jsonParser.parse(TextList.get(0).toString());
                    return text.get("description").toString();
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public String FinalizeTextExtraction() {
        String result = "";
        for (int i = 0; i < FinalTextExtracted.size(); i++) {
            result += "[ Page : " + (+i + 1) + " ]\n" + FinalTextExtracted.get(i) + "\n";
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 0 && resultCode == RESULT_OK) {
                //from camera
                uri = Uri.fromFile(file);
                IS_MULTIPLE = false;
                CropImage.activity(uri)
                        .setCropMenuCropButtonTitle(getContext().getResources().getString(R.string.crop))
                        .start(getContext(), Home_Fragment.this);
            } else if (requestCode == 2) {
                //from gallery
                if (data != null) {

                    uri = data.getData();
                    IS_MULTIPLE = false;
                    CropImage.activity(uri)
                            .setCropMenuCropButtonTitle(getContext().getResources().getString(R.string.crop))
                            .start(getContext(), Home_Fragment.this);

                }
            } else if (requestCode == 3 && resultCode == RESULT_OK
                    && data != null) {
                try {
                    List<String> imagesEncodedList;

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    imagesEncodedList = new ArrayList<String>();
             /*   if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_LONG).show();

                    // Get the cursor
                    Cursor cursor = getContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                } else {*/
                    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String imageEncoded = cursor.getString(columnIndex);
                            Log.v("LOG_TAG", "Selected Image" + imageEncoded);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
                            IS_MULTIPLE = true;
                            CropImage.activity(uri)
                                    .setCropMenuCropButtonTitle(getContext().getResources().getString(R.string.crop))
                                    .start(getContext(), Home_Fragment.this);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setCancelable(true);
                        builder.setTitle(getResources().getString(R.string.dialog_fetch));

                        builder.setPositiveButton(getResources().getString(R.string.arabic), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                prepareMultipleImage("ar", "3");
                            }

                        });
                        builder.setNegativeButton(getResources().getString(R.string.english), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                prepareMultipleImage("en", "20");
                            }

                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                    // }
                    // }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }


            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    final Uri resultUri;
                    if (!IS_MULTIPLE) {
                        resultUri = result.getUri();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setCancelable(true);
                        builder.setTitle(getResources().getString(R.string.dialog_fetch));

                        builder.setPositiveButton(getResources().getString(R.string.arabic), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                prepareImage(resultUri, "ar", "3");
                            }

                        });
                        builder.setNegativeButton(getResources().getString(R.string.english), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                prepareImage(resultUri, "en", "20");
                            }

                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        imageResultUri.add(result.getUri());
                    }


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getContext(), getResources().getString(R.string.crop_error), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();
        }

    }

    public void changeLang(Context context, String lang) { // func to change lang
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Language", lang);
        editor.apply();
    }
}
