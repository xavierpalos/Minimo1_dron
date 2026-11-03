package edu.upc.dsa.models;

public class Pilot {
    private String name;
    private String surname;
    private String id;
    private double flightHours;

    public Pilot() {}
    public Pilot(String id,String name,String surname){
        this.id=id;
        this.name=name;
        this.surname=surname;
        this.flightHours=0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getFlightHours() {
        return flightHours;
    }

    public void setFlightHours(double flightHours) {
        this.flightHours = flightHours;
    }

    public void addFlightHours(double hours) {
        this.flightHours += hours;
    }
}
