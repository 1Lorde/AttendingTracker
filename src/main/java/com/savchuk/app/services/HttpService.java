// created by Vlad Savchuk 23.11.2019 22:05
package com.savchuk.app.services;

import okhttp3.*;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;

public class HttpService {
    private static HttpService instance;
    private final OkHttpClient httpClient;

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final String API_KEY= "sd0KgcYt4ExfOjmT5BSJRG6RW4a9TEkf";
    private final String API_SECRET= "W6-cC74y9m92bGRPg6fekCunjsfO4UAs";

    private HttpService() {
        httpClient = new OkHttpClient();
    }

    public static HttpService getInstance(){
        if (instance == null){
            instance = new HttpService();
        }
        return instance;
    }

    public String detectFaceRequest(File file) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("api_secret", API_SECRET)
                .addFormDataPart("image_file", "image", RequestBody.create(MEDIA_TYPE_PNG, file))
                .addFormDataPart("return_landmark", "1")
                .addFormDataPart("return_attributes", "gender,age")
                .build();

        Request request = new Request.Builder()
                .url("https://api-us.faceplusplus.com/facepp/v3/detect")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resp = response.body().string();
            System.out.println(resp);
            return resp;
        }
    }

    public void createFaceSetRequest(String displayName, String outerId) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("api_secret", API_SECRET)
                .addFormDataPart("display_name", displayName)
                .addFormDataPart("outer_id", outerId)
                .build();

        Request request = new Request.Builder()
                .url("https://api-us.faceplusplus.com/facepp/v3/faceset/create")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resp = response.body().string();
            System.out.println(resp);
        }
    }

    public void removeFaceSetRequest(String outerId) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("api_secret", API_SECRET)
                .addFormDataPart("outer_id", outerId)
                .addFormDataPart("check_empty", "0")
                .build();

        Request request = new Request.Builder()
                .url("https://api-us.faceplusplus.com/facepp/v3/faceset/delete")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resp = response.body().string();
            System.out.println(resp);
        }


    }

    public void getFaceSetDetailsRequest(String outerId) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("api_secret", API_SECRET)
                .addFormDataPart("outer_id", outerId)
                .build();

        Request request = new Request.Builder()
                .url("https://api-us.faceplusplus.com/facepp/v3/faceset/getdetail")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resp = response.body().string();
            System.out.println(resp);
        }


    }


    public void addFaceRequest(String faceToken, String outerId) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("api_secret", API_SECRET)
                .addFormDataPart("face_tokens", faceToken)
                .addFormDataPart("outer_id", outerId)
                .build();

        Request request = new Request.Builder()
                .url("https://api-us.faceplusplus.com/facepp/v3/faceset/addface")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resp = response.body().string();
            System.out.println(resp);
        }
    }

    public void removeFaceRequest(String faceToken, String outerId) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("api_secret", API_SECRET)
                .addFormDataPart("face_tokens", faceToken)
                .addFormDataPart("outer_id", outerId)
                .build();

        Request request = new Request.Builder()
                .url("https://api-us.faceplusplus.com/facepp/v3/faceset/removeface")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resp = response.body().string();
            System.out.println(resp);
        }
    }


    public void setUserIdRequest(String faceToken, String userId) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("api_secret", API_SECRET)
                .addFormDataPart("face_token", faceToken)
                .addFormDataPart("user_id", userId)
                .build();

        Request request = new Request.Builder()
                .url("https://api-us.faceplusplus.com/facepp/v3/face/setuserid")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resp = response.body().string();
            System.out.println(resp);
        }

    }

    public String searchRequest(File file, String outerId) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_key", API_KEY)
                .addFormDataPart("api_secret", API_SECRET)
                .addFormDataPart("image_file", "image", RequestBody.create(MEDIA_TYPE_PNG, file))
                .addFormDataPart("outer_id", outerId)
                .build();

        Request request = new Request.Builder()
                .url("https://api-us.faceplusplus.com/facepp/v3/search")
                .addHeader("User-Agent", "OkHttp Bot")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resp = response.body().string();
            System.out.println(resp);
            return resp;
        }
    }


}
