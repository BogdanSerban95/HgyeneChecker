package com.example.serba.hygenechecker.models;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by serba on 26/02/2018.
 */

public class SearchParams implements Serializable {
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private String name;
    private String address;
    private Integer pageSize;
    private Integer pageNumber;

    public SearchParams() {
        this.pageSize = 8;
        this.pageNumber = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String businessName) {
        this.name = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void nextPage() {
        this.pageNumber++;
    }

    public Map<String, String> toHashMap() {
        HashMap<String, String> output = new HashMap<String, String>();
        Gson gson = new Gson();
        String jsonThis = gson.toJson(this);
        output = gson.fromJson(jsonThis, output.getClass());
        Log.e("hash", output.toString());
        return output;
    }

    public String toQueryString() {
        StringBuilder result = new StringBuilder();
        Map<String, String> paramsMap = this.toHashMap();
        if (paramsMap.keySet().size() == 0)
            return "";
        else
            result.append("?");
        for (String key : paramsMap.keySet()) {
            result.append(key)
                    .append("=")
                    .append(
                            String.valueOf(paramsMap.get(key))
                                    .replace(" ", "%20")
                                    .replace(".0", "")
                    )
                    .append("&");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }
}
