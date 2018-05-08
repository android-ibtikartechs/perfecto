package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.Files_Adpater;
import com.perfecto.apps.ocr.models.File;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hosam Azzam on 20/08/2017.
 */

public class MyFiles_Fragment extends Fragment {
    VolleyClass volley;
    Bundle bundle;
    CardView add_file;
    RecyclerView list;
    ArrayList<File> files = new ArrayList<>();
    Files_Adpater files_adpater;
    User curuser;
    ImageView home_ico;
    java.io.File file;
    Uri uri;
    Intent CamIntent, GalIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.myfiles_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());
        add_file = rootview.findViewById(R.id.add_file_btn);
        list = rootview.findViewById(R.id.myfiles_list);

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

        files_adpater = new Files_Adpater(getContext(), files, getFragmentManager());
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(files_adpater);

        bundle = getArguments();
        init(bundle.getString("doc_id"), bundle.getString("doc_name"));

        add_file.setOnClickListener(new View.OnClickListener() {
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

        return rootview;
    }

    public void init(final String docid, final String docname) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "docfiles", new Response.Listener<String>() {
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

                        JSONArray Files = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        files.clear();
                        for (int i = 0; i < Files.size(); i++) {
                            JSONObject docObj = (JSONObject) jsonParser.parse(Files.get(i).toString());
                            File file = new File();
                            file.setId(docObj.get("id").toString());
                            file.setName(docObj.get("name").toString());
                            file.setDocid(docid);
                            file.setDocname(docname);
                            file.setDesc(docObj.get("desc").toString());
                            file.setSourceLangPos(docObj.get("lang").toString());
                            file.setTrans(docObj.get("trans").toString());
                            file.setDate(docObj.get("created_at").toString());
                            files.add(file);
                            System.out.println("i " + i);
                        }
                        files_adpater.notifyDataSetChanged();
                        if (Files.size() == 0) {
                            Toast.makeText(getContext(), getResources().getString(R.string.no_file), Toast.LENGTH_LONG).show();
                        }


                        loading.dismiss();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("document_id", docid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
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

        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(GalIntent, getResources().getString(R.string.select_gallery)), 2);

    }

    public void sent(String Image, final String Lang, final String LangIndex) {
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
                    Add_File_Fragment add_file_fragment = new Add_File_Fragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("response", extractText(response));
                    bundle1.putString("doc_id", bundle.getString("doc_id"));
                    bundle1.putString("doc_name", bundle.getString("doc_name"));
                    bundle1.putString("doc_lang", LangIndex);
                    add_file_fragment.setArguments(bundle1);

                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.main_fragment_container, add_file_fragment, "add_file_fragment")
                            .addToBackStack("add_file_fragment")
                            .commit();
                    loading.dismiss();
                    Log.i("VOLLEY", response);
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


    public void initaddfile(Uri resultUri, String lang, String index) {
        try {
            Bitmap bitmap;
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            sent(encodedImage, lang, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 0 && resultCode == RESULT_OK) {
                //from camera
                uri = Uri.fromFile(file);

                CropImage.activity(uri)
                        .setCropMenuCropButtonTitle(getContext().getResources().getString(R.string.crop))
                        .start(getContext(), MyFiles_Fragment.this);
            } else if (requestCode == 2) {
                //from gallery
                if (data != null) {

                    uri = data.getData();

                    CropImage.activity(uri)
                            .setCropMenuCropButtonTitle(getContext().getResources().getString(R.string.crop))
                            .start(getContext(), MyFiles_Fragment.this);

                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = result.getUri();

                    android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setTitle(getResources().getString(R.string.dialog_fetch));

                    builder.setPositiveButton(getResources().getString(R.string.arabic), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            initaddfile(resultUri, "ar", "3");
                        }

                    });
                    builder.setNegativeButton(getResources().getString(R.string.english), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            initaddfile(resultUri, "en", "20");
                        }

                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


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

}
