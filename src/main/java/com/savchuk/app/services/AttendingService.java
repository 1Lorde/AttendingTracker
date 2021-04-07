// created by Vlad Savchuk 28.11.2019 0:38
package com.savchuk.app.services;

import com.savchuk.app.models.Attending;
import com.savchuk.app.models.Cadet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class AttendingService {
    private DatabaseService databaseService;
    private static AttendingService instance;
    private static ArrayList<Attending> attendings;

    private AttendingService() {
        databaseService = DatabaseService.getInstance();
        databaseService.init();
    }

    public static AttendingService getInstance(){
        if (instance == null){
            instance = new AttendingService();
        }
        return instance;
    }

    public synchronized ArrayList<Attending> findAll(){
        return findAll(null);
    }

    public synchronized ArrayList<Attending> findAll(String filter){
        updateList();
        ArrayList<Attending> result = new ArrayList<>();

        if (filter != null && !filter.isEmpty()){
            for (Attending attending : attendings) {
                if (attending.toString().toLowerCase().contains(filter.toLowerCase())){
                    try {
                        result.add(attending.clone());
                        System.out.println(attending);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
        return attendings;
    }

    public synchronized void save(Attending attending){
        if (attending == null){
            return;
        }

        databaseService.addAttending(attending);
        updateList();
    }

    public synchronized void update(Attending attending){
        if (attending == null){
            return;
        }
        databaseService.updateAttending(attending);
        updateList();
    }

    public void checkIn(Cadet cadet){
        String shortInfo = cadet.getSurname()+" "+cadet.getName()+" ("+cadet.getGroup()+" group)";
        Attending attending = new Attending(cadet.getId().toString(), shortInfo);

        ArrayList<Attending> currentCadetAttendings = findAll(cadet.getId().toString());

        if (!currentCadetAttendings.isEmpty()){
            boolean isNewAttend = true;
            for (Attending a: currentCadetAttendings) {
                if (a.getArrival().isEmpty()){
                    a.setArrival(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd.MM.yy")));
                    isNewAttend = false;
                    update(a);
                    break;
                }
            }
            if (isNewAttend){
                attending.setDeparture(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd.MM.yy")));
                save(attending);
            }

        }else{
            attending.setDeparture(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd.MM.yy")));
            save(attending);
        }






    }


    public synchronized int getInside(){
        int count = 0;
        if (databaseService.getCadets() != null){
            for (Cadet c: databaseService.getCadets().values()) {
                if (c.toString().contains("true")){
                    count++;
                }
            }
        }
        return count;
    }

    public synchronized int getOutside(){
        int count = 0;
        if (databaseService.getCadets() != null){
            for (Cadet c: databaseService.getCadets().values()) {
                if (c.toString().contains("false")){
                    count++;
                }
            }
        }

        return count;
    }

    private void updateList(){
        if (databaseService.getAttendings() != null)
            attendings = databaseService.getAttendings();
        else{
            attendings = new ArrayList<>();
        }
    }

}
