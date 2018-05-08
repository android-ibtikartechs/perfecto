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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hosamazzam.customviews.NonScrollListView;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.Files_Group_Adapter;
import com.perfecto.apps.ocr.adapter.Invite_Adapter;
import com.perfecto.apps.ocr.adapter.Member_Requests_Adapter;
import com.perfecto.apps.ocr.adapter.Members_Adapter;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hosam azzam on 11/10/2017.
 */

public class Group_Details_Fragment extends Fragment {
    ArrayList<com.perfecto.apps.ocr.models.Request> requests = new ArrayList<>();
    ArrayList<User> members = new ArrayList<>();
    ArrayList<com.perfecto.apps.ocr.models.File> files = new ArrayList<>();
    Member_Requests_Adapter member_requests_adapter;
    Members_Adapter members_adapter;
    Files_Group_Adapter files_group_adapter;
    VolleyClass volley;
    User user;
    Bundle bundle;
    String gid;
    Boolean is_Admin = false;
    RecyclerView member_list;
    NonScrollListView request_list, file_list;
    TextView request_txt, group_name, member_count, invite_txt, file_txt;
    ImageView group_photo, home_ico, delete_btn, camer_btn;
    RelativeLayout chat_header, member_holder;
    CardView invite_btn, add_btn, leave_btn;
    LinearLayout add_panel;
    AutoCompleteTextView add_txt;
    ArrayList<User> user_list = new ArrayList<>();
    String add_user_id = "";
    Bitmap bitmap = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.group_details_fragment, container, false);

        volley = VolleyClass.getInstance(getContext());
        user = Perfecto.getUserLoginInfo(getContext());
        bundle = getArguments();

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

        member_requests_adapter = new Member_Requests_Adapter(getContext(), requests);
        files_group_adapter = new Files_Group_Adapter(getContext(), files, getFragmentManager());
        member_requests_adapter.addOnChangeRequestStatus(new Member_Requests_Adapter.onChangeRequestStatus() {
            @Override
            public void onChange() {
                init(gid);
            }
        });
        members_adapter = new Members_Adapter(getContext(), members);

        member_list = rootview.findViewById(R.id.group_member_list);
        request_list = rootview.findViewById(R.id.group_request_list);
        file_list = rootview.findViewById(R.id.group_files_list);
        member_holder = rootview.findViewById(R.id.group_member_holder);
        request_txt = rootview.findViewById(R.id.group_request_txt);
        file_txt = rootview.findViewById(R.id.group_files_txt);
        group_name = rootview.findViewById(R.id.group_name_txt);
        member_count = rootview.findViewById(R.id.group_memcount_txt);
        group_photo = rootview.findViewById(R.id.group_photo_img);
        camer_btn = rootview.findViewById(R.id.group_camera_btn);
        delete_btn = rootview.findViewById(R.id.group_delete_btn);
        chat_header = rootview.findViewById(R.id.group_chat_header);

        invite_txt = rootview.findViewById(R.id.group_invite_txt);
        invite_btn = rootview.findViewById(R.id.group_invite_btn);

        leave_btn = rootview.findViewById(R.id.group_leave_btn);

        add_txt = rootview.findViewById(R.id.group_add_txt);
        add_btn = rootview.findViewById(R.id.group_add_btn);
        add_panel = rootview.findViewById(R.id.group_add_panel);

        add_txt.setThreshold(0);
        add_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invite_txt.requestFocus();
            }
        });

        add_txt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                add_user_id = String.valueOf(add_txt.getAdapter().getItemId(position));
            }
        });

        invite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!invite_txt.getText().equals("")) {
                    sendInvition(invite_txt.getText().toString());
                }
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!invite_txt.getText().equals("")) {
                    if (!add_user_id.equals("")) {
                        sendRequest(add_user_id, gid);
                    }
                }
            }
        });


        member_list.setAdapter(members_adapter);
        member_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        request_list.setAdapter(member_requests_adapter);

        file_list.setAdapter(files_group_adapter);

        gid = bundle.getString("group_id");

        chat_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat_Group_Fragment chat_group_fragment = new Chat_Group_Fragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("group_id", gid);
                chat_group_fragment.setArguments(bundle1);
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.main_fragment_container, chat_group_fragment, "chat_group_fragment")
                        .addToBackStack("chat_group_fragment")
                        .commit();

            }
        });

        camer_btn.setOnClickListener(new View.OnClickListener() {
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

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroup();
            }
        });

        leave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });


        init(gid);
        initAddUsers();

        return rootview;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public void initAddUsers() {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "users", new Response.Listener<String>() {
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
                        JSONArray membersObj = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        for (int i = 0; i < membersObj.size(); i++) {
                            JSONObject member = (JSONObject) jsonParser.parse(membersObj.get(i).toString());
                            User mem = new User();
                            if (member.get("name") != null)
                                mem.setName(member.get("name").toString());
                            if (member.get("photo") != null)
                                mem.setPhoto(member.get("photo").toString());
                            if (member.get("email") != null)
                                mem.setEmail(member.get("email").toString());

                            mem.setId(Long.valueOf(member.get("id").toString()));

                            user_list.add(mem);
                        }
                        Invite_Adapter invite_Adapter = new Invite_Adapter(getContext(), R.layout.card_user_item, user_list);
                        add_txt.setAdapter(invite_Adapter);
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
                return new HashMap<>();
            }
        };

        volley.getQueue().add(stringRequest);
    }

    public void init(final String id) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group", new Response.Listener<String>() {
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
                        JSONObject groupObj = (JSONObject) jsonParser.parse(mainObject.get("Response").toString());

                        if (String.valueOf(user.getId()).equals(groupObj.get("user_id").toString())) {
                            is_Admin = true;

                        }

                        if (!is_Admin) {
                            request_list.setVisibility(View.GONE);
                            request_txt.setVisibility(View.GONE);
                            add_panel.setVisibility(View.GONE);
                            delete_btn.setVisibility(View.GONE);
                            camer_btn.setVisibility(View.GONE);
                            leave_btn.setVisibility(View.VISIBLE);
                        }
                        group_name.setText(groupObj.get("name").toString());
                        if (groupObj.get("photo") != null)
                            Glide.with(getContext()).load(Perfecto.BASE_IMAGE_URL + groupObj.get("photo").toString()).asBitmap().into(group_photo);

                        requests.clear();
                        JSONArray requestObj = (JSONArray) jsonParser.parse(groupObj.get("requests").toString());
                        for (int i = 0; i < requestObj.size(); i++) {

                            JSONObject request = (JSONObject) jsonParser.parse(requestObj.get(i).toString());
                            com.perfecto.apps.ocr.models.Request req = new com.perfecto.apps.ocr.models.Request();
                            req.setId(request.get("id").toString());
                            req.setUser_id(request.get("user_id").toString());
                            req.setGroup_id(request.get("group_id").toString());
                            req.setFlag(request.get("flag").toString());
                            JSONObject user = (JSONObject) jsonParser.parse(request.get("user").toString());
                            req.setUser_name(user.get("name").toString());
                            req.setUser_photo(user.get("photo").toString());
                            requests.add(req);
                        }
                        if (requestObj.size() != 0) {
                            request_txt.setVisibility(View.VISIBLE);
                            request_list.setVisibility(View.VISIBLE);
                        }
                        member_requests_adapter.notifyDataSetChanged();

                        members.clear();
                        JSONArray membersObj = (JSONArray) jsonParser.parse(groupObj.get("members").toString());
                        for (int i = 0; i < membersObj.size(); i++) {
                            JSONObject member = (JSONObject) jsonParser.parse(membersObj.get(i).toString());
                            User mem = new User();
                            mem.setName(member.get("name").toString());
                            mem.setPhoto(member.get("photo").toString());
                            mem.setId(Long.valueOf(member.get("id").toString()));
                            members.add(mem);
                        }

                        member_count.setText(membersObj.size() + " " + getResources().getString(R.string.member));

                        if (membersObj.size() != 0) {
                            member_holder.setVisibility(View.VISIBLE);
                            member_list.setVisibility(View.VISIBLE);
                        }

                        members_adapter.notifyDataSetChanged();


                        JSONArray Files = (JSONArray) jsonParser.parse(groupObj.get("files").toString());
                        files.clear();
                        for (int i = 0; i < Files.size(); i++) {
                            JSONObject docObj = (JSONObject) jsonParser.parse(Files.get(i).toString());
                            com.perfecto.apps.ocr.models.File file = new com.perfecto.apps.ocr.models.File();
                            file.setId(docObj.get("id").toString());
                            file.setName(docObj.get("name").toString());
                            file.setDesc(docObj.get("desc").toString());
                            file.setDocid(docObj.get("document_id").toString());
                            file.setSourceLangPos(docObj.get("lang").toString());
                            file.setTrans(docObj.get("trans").toString());
                            file.setDate(docObj.get("created_at").toString());
                            files.add(file);
                        }
                        files_group_adapter.notifyDataSetChanged();
                        if (Files.size() != 0) {
                            file_list.setVisibility(View.VISIBLE);
                            file_txt.setVisibility(View.VISIBLE);
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
                params.put("group_id", id);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    public void sendInvition(final String email) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getContext().getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "invitation", new Response.Listener<String>() {
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
                        Toast.makeText(getContext(), mainObject.get("Response").toString(), Toast.LENGTH_SHORT).show();
                        invite_txt.setText("");
                        loading.dismiss();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    public void sendRequest(final String uid, final String gid) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getContext().getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group/adduser", new Response.Listener<String>() {
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
                        Toast.makeText(getContext(), mainObject.get("Response").toString(), Toast.LENGTH_SHORT).show();
                        add_txt.setText("");
                        add_user_id = "";
                        loading.dismiss();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.request_pending), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", uid);
                params.put("group_id", gid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    public void updateCover() {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "updategroup", new Response.Listener<String>() {
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
                        Toast.makeText(getContext(), getResources().getString(R.string.group_updated), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        init(gid);
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", gid);
                params.put("user_id", String.valueOf(user.getId()));
                if (bitmap != null) {
                    params.put("photo", getStringImage(bitmap));
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 3, 2));
        volley.getQueue().add(stringRequest);
    }

    public void deleteGroup() {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "deletegroup", new Response.Listener<String>() {
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
                        Toast.makeText(getContext(), getResources().getString(R.string.group_deleted), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        getFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", gid);
                params.put("user_id", String.valueOf(user.getId()));
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    public void leaveGroup() {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group/deluser", new Response.Listener<String>() {
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
                        Toast.makeText(getContext(), getResources().getString(R.string.group_leaved), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        getFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", gid);
                params.put("user_id", String.valueOf(user.getId()));
                return params;
            }
        };

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
                updateCover();
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
                updateCover();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }
}
