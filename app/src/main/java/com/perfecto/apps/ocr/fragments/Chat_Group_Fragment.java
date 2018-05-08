package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.adapter.Chat_Adapter;
import com.perfecto.apps.ocr.models.Msg;
import com.perfecto.apps.ocr.models.User;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hosam azzam on 14/10/2017.
 */

public class Chat_Group_Fragment extends Fragment {
    ArrayList<Msg> msgs = new ArrayList<>();
    Chat_Adapter chat_adapter;
    VolleyClass volley;
    EditText msg_txt;
    String group_id;
    User user;
    RecyclerView chat_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.chat_group_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());
        Bundle bundle = getArguments();
        group_id = bundle.getString("group_id");
        user = Perfecto.getUserLoginInfo(getContext());
        chat_list = contentView.findViewById(R.id.group_chat_list);
        msg_txt = contentView.findViewById(R.id.group_chat_msg_txt);

        chat_adapter = new Chat_Adapter(getContext(), msgs);
        chat_list.setLayoutManager(new LinearLayoutManager(getContext()));
        chat_list.setAdapter(chat_adapter);

        contentView.findViewById(R.id.group_chat_send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!msg_txt.getText().toString().equals("")) {
                    sendMsg(msg_txt.getText().toString());
                }
            }
        });

        init(group_id);
        return contentView;
    }

    public void init(final String gid) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group/chat", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONParser jsonParser = new JSONParser();
                    Object obj;
                    JSONObject mainObject;
                    obj = jsonParser.parse(response);
                    mainObject = (JSONObject) obj;
                    msgs.clear();
                    JSONObject statusObject = (JSONObject) jsonParser.parse(mainObject.get("status").toString());
                    if (statusObject.get("type").equals("success") || statusObject.get("code").equals(0)) {
                        JSONArray msgsObject = (JSONArray) jsonParser.parse(mainObject.get("Response").toString());
                        for (int i = 0; i < msgsObject.size(); i++) {
                            JSONObject msgObj = (JSONObject) jsonParser.parse(msgsObject.get(i).toString());
                            Msg msg = new Msg();
                            msg.setId(msgObj.get("id").toString());
                            msg.setMsg(msgObj.get("message").toString());
                            msg.setUser_id(msgObj.get("user_id").toString());
                            msg.setDate(msgObj.get("created_at").toString());
                            JSONObject userObj = (JSONObject) jsonParser.parse(msgObj.get("user").toString());
                            if (userObj.get("photo") != null)
                                msg.setUser_photo(userObj.get("photo").toString());
                            if (userObj.get("name") != null)
                                msg.setUser_name(userObj.get("name").toString());
                            msgs.add(msg);
                        }
                        chat_adapter.notifyDataSetChanged();


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
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", gid);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);

    }

    public void sendMsg(final String msg) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + "group/send", new Response.Listener<String>() {
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
                        init(group_id);
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
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", group_id);
                params.put("user_id", String.valueOf(user.getId()));
                params.put("message", msg);
                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }
}
