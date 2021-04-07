// created by Vlad Savchuk 24.11.2019 8:24
package com.savchuk.app.services;

import com.savchuk.app.models.Cadet;
import org.bson.types.ObjectId;

import java.io.File;
import java.util.AbstractMap;

public class FaceService {
    private static FaceService instance;

    private final String OUTER_ID = "myFaceSet";

    private HttpService httpService;
    private JsonService jsonService;

    private FaceService() {
        httpService = HttpService.getInstance();
        jsonService = JsonService.getInstance();
    }

    public static FaceService getInstance(){
        if (instance == null){
            instance = new FaceService();
        }
        return instance;
    }

    public String getFaceToken(File file) throws Exception {
        return jsonService.parseDetectJson(httpService.detectFaceRequest(file));
    }

    public void addFace(File file, Cadet cadet) throws Exception {
        String faceToken = getFaceToken(file);
        httpService.addFaceRequest(faceToken, OUTER_ID);
        httpService.setUserIdRequest(faceToken, cadet.getId().toString());
        cadet.setFaceToken(faceToken);
    }

    public void removeFace(String faceToken) throws Exception {
        httpService.removeFaceRequest(faceToken, OUTER_ID);
    }

    public ObjectId searchFace(File file) throws Exception {
        String result = httpService.searchRequest(file, OUTER_ID);
        if (jsonService.parseSearchJson(result) == null)
            return null;
        AbstractMap.SimpleEntry<Double, String> entry = jsonService.parseSearchJson(result);
        double confidence = entry.getKey();
        String userId = entry.getValue();

        if (confidence>85){
            return new ObjectId(userId);
        }
        return null;
    }
}
