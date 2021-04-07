// created by Vlad Savchuk 11.11.2019 8:52
package com.savchuk.app.models;


import org.bson.types.ObjectId;

import java.time.LocalDate;

public class Cadet implements Cloneable{
    private ObjectId id;
    private Ranks rank;
    private String surname;
    private String name;
    private int group;
    private LocalDate birthday;
    private String phone;
    private boolean present = true;
    private String faceToken;

    public Cadet() {
    }

    public Cadet(Ranks rank, String surname, String name, int group, LocalDate birthday, String phone) {
        this.id = ObjectId.get();
        this.rank = rank;
        this.surname = surname;
        this.name = name;
        this.group = group;
        this.birthday = birthday;
        this.phone = phone;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Ranks getRank() {
        return rank;
    }

    public void setRank(Ranks rank) {
        this.rank = rank;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

    @Override
    public String toString() {
        return "Cadet{" +
                "id=" + id +
                ", rank=" + rank +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", group=" + group +
                ", birthday=" + birthday +
                ", phone='" + phone + '\'' +
                ", present=" + present +
                '}';
    }

    @Override
    public Cadet clone() throws CloneNotSupportedException {
        return (Cadet) super.clone();
    }
}
