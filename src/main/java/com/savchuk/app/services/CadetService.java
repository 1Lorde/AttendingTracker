// created by Vlad Savchuk 14.11.2019 21:42
package com.savchuk.app.services;

import com.savchuk.app.models.Cadet;
import org.bson.types.ObjectId;


import java.util.ArrayList;
import java.util.HashMap;


public class CadetService {
    private DatabaseService databaseService;
    private static CadetService instance;
    private static HashMap<ObjectId, Cadet> cadetsMap;
    private CadetService() {
        databaseService = DatabaseService.getInstance();
        databaseService.init();
        updateMap();
    }

    public static CadetService getInstance(){
        if (instance == null){
            instance = new CadetService();
        }
        return instance;
    }

    public synchronized ArrayList<Cadet> findAll(){
        return findAll(null);
    }

    public synchronized ArrayList<Cadet> findAll(String filter){
        updateMap();
        ArrayList<Cadet> result = new ArrayList<>();

        if (filter != null && !filter.isEmpty()){
            for (Cadet cadet : cadetsMap.values()) {
                if (cadet.toString().toLowerCase().contains(filter.toLowerCase())){
                    try {
                        result.add(cadet.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
        return new ArrayList<>(cadetsMap.values());


    }

    public synchronized long count(){
        return cadetsMap.size();
    }

    public synchronized void delete(Cadet cadet){
        databaseService.deleteCadet(cadet);
        updateMap();
    }

    public synchronized void save(Cadet cadet){
        if (cadet == null){
            System.out.println("can't save Cadet (null).");
            return;
        }

        databaseService.addCadet(cadet);
        updateMap();
    }

    public synchronized void update(Cadet cadet){
        if (cadet == null){
            System.out.println("can't update Cadet (null).");
            return;
        }
        databaseService.updateCadet(cadet);
        updateMap();
    }


    private void updateMap(){
        if (databaseService.getCadets() != null)
            cadetsMap = databaseService.getCadets();
        else
            cadetsMap = new HashMap<>();
    }
}
