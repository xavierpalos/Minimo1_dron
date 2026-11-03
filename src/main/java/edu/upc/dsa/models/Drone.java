package edu.upc.dsa.models;

public class Drone {
    private String id;
    private String name;
    private String model;
    private String manufacturer;
    private double flightHours;
    private boolean inMaintenance;

    public Drone(){}
    public Drone(String id,String name,String manufacturer,String model){
        this.id=id;
        this.name=name;
        this.model=model;
        this.manufacturer=manufacturer;
        this.flightHours=0;
        this.inMaintenance=false;
    }
    public void addFlightHours(double hours) {
        this.flightHours+=hours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getFlightHours() {
        return flightHours;
    }

    public void setFlightHours(double flightHours) {
        this.flightHours = flightHours;
    }

    public boolean isInMaintenance() {
        return inMaintenance;
    }

    public void setInMaintenance(boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }
}
