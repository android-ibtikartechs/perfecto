package com.perfecto.apps.ocr.tools;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by dell on 08/11/2016.
 */

public class VolleyClass {

    private static VolleyClass vollayClass;
    private static RequestQueue Queue;
    private static StringRequest stringRequest;
    private static RequestFuture<String> requestFuture;
    private static Context mcontext;
    private NetworkListener networkListener;

    private VolleyClass(Context context) {
        mcontext = context;
        Queue = Volley.newRequestQueue(mcontext);
    }

    public static VolleyClass getInstance(Context context) {
        if (vollayClass == null) {
            vollayClass = new VolleyClass(context);
        }
        if (mcontext != context) {
            vollayClass = new VolleyClass(context);
        }
        return vollayClass;
    }

    public void setCallbackListener(NetworkListener callbackListener) {
        this.networkListener = callbackListener;
    }

    public String asyncGet(String url) {
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        networkListener.onResponce(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkListener.onFailure(error);

            }
        });

        Queue.add(stringRequest);

        return null;
    }

    public String asyncGet(String url, Object tag) {
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        networkListener.onResponce(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkListener.onFailure(error);

            }
        });
        stringRequest.setTag(tag);
        Queue.add(stringRequest);

        return null;
    }

    public String syncGet(String url) {
        requestFuture = RequestFuture.newFuture();
        stringRequest = new StringRequest(Request.Method.GET, url, requestFuture, requestFuture);
        Queue.add(stringRequest);
        try {
            return requestFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String syncGet(String url, String tag) {
        requestFuture = RequestFuture.newFuture();
        stringRequest = new StringRequest(Request.Method.GET, url, requestFuture, requestFuture);
        stringRequest.setTag(tag);
        Queue.add(stringRequest);
        try {
            return requestFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String asyncPost(String url, final Map<String, String> params) {
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        networkListener.onResponce(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkListener.onFailure(error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        Queue.add(stringRequest);

        return null;
    }

    public String asyncPost(String url, Object tag, final Map<String, String> params) {
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        networkListener.onResponce(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkListener.onFailure(error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        stringRequest.setTag(tag);
        Queue.add(stringRequest);

        return null;
    }

    public void cancelRequest(Object Tag) {
        if (Queue != null) {
            Queue.cancelAll(Tag);
        }
    }

    public RequestQueue getQueue() {
        return Queue;
    }

    public interface NetworkListener {
        void onResponce(String s);

        void onFailure(Exception e);
    }

}
