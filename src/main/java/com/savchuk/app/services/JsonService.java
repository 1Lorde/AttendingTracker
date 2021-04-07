// created by Vlad Savchuk 24.11.2019 9:26
package com.savchuk.app.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;

public class JsonService {
    private static JsonService instance;

    private JsonService() {

    }

    public static JsonService getInstance(){
        if (instance == null){
            instance = new JsonService();
        }
        return instance;
    }

    public String parseDetectJson(String jsonString){
        JSONObject obj = new JSONObject(jsonString);
        JSONArray arr = obj.getJSONArray("faces");
        return arr.getJSONObject(0).getString("face_token");
    }

    public AbstractMap.SimpleEntry<Double, String> parseSearchJson(String jsonString){
        JSONObject obj = new JSONObject(jsonString);
        try {
            JSONArray arr = obj.getJSONArray("results");
            double confidence = arr.getJSONObject(0).getDouble("confidence");
            String userId = arr.getJSONObject(0).getString("user_id");
            return new AbstractMap.SimpleEntry<>(confidence, userId);
        } catch (JSONException e){
            return null;
        }


    }
}
