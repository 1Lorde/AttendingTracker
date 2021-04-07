// created by Vlad Savchuk 28.11.2019 0:13
package com.savchuk.app.models;


public class Attending implements Cloneable{
    private String cadetId;
    private String cadet;
    private String arrival;
    private String departure;

    public Attending() {
    }

    public Attending(String cadetId, String cadet) {
        this.cadetId = cadetId;
        this.arrival = "";
        this.departure = "";
        this.cadet = cadet;
    }

    public String getCadet() {
        return cadet;
    }

    public void setCadet(String cadet) {
        this.cadet = cadet;
    }

    public String getCadetId() {
        return cadetId;
    }

    public void setCadetId(String cadetId) {
        this.cadetId = cadetId;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    @Override
    public String toString() {
        return "Attending{" +
                "cadetId='" + cadetId + '\'' +
                ", arrival=" + arrival +
                ", departure=" + departure +
                '}';
    }

    @Override
    public Attending clone() throws CloneNotSupportedException {
        return (Attending) super.clone();
    }
}
