package com.example.serba.hygenechecker.models;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serba on 26/02/2018.
 */

public class RequestWrapper {
    private String apiUrl = "http://api.ratings.food.gov.uk/";
    private static RequestWrapper instance;
    private RequestQueue requestQueue;
    private Context context;

    private RequestWrapper(Context ctx) {
        context = ctx;
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestWrapper getInstance(Context context) {
        if (instance == null) {
            instance = new RequestWrapper(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public Request<JSONObject> addJsonObjectRequest(int method, String endpoint, final SearchParams params, Response.Listener<JSONObject> successListener, Response.ErrorListener errorListener) {
        String requestUrl = this.apiUrl + endpoint;
        if (params != null) {
            requestUrl += params.toQueryString();
        }
        Request<JSONObject> request = new JsonObjectRequest(method, requestUrl, null, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-api-version", "2");
                return headers;
            }
        };
        this.requestQueue.add(request);
        return request;
    }

    public void addRequest(Request<JSONObject> request) {
        this.requestQueue.add(request);
    }

    public void addJsonArrayRequest(int method, String endpoint, final SearchParams params, Response.Listener<JSONArray> successListener, Response.ErrorListener errorListener) {
        String requestUrl = this.apiUrl + endpoint;
        if (params != null) {
            requestUrl += params.toQueryString();
        }
        final Request<JSONArray> request = new JsonArrayRequest(method, requestUrl, null, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-api-version", "2");
                return headers;
            }
        };
        this.requestQueue.add(request);
    }
}
