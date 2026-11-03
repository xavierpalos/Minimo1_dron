package edu.upc.dsa.models;

public class FlightPlan {
    private String droneId;
    private String pilotId;
    private String date;
    private double hours;
    private Position start;
    private Position destination;

    public FlightPlan() {}
    public FlightPlan(String droneId,String pilotId,String date,double hours,Position start,Position destination){
        this.droneId=droneId;
        this.pilotId=pilotId;
        this.date=date;
        this.hours=hours;
        this.start=start;
        this.destination=destination;
    }

    public String getDroneId() {
        return droneId;
    }

    public void setDroneId(String droneId) {
        this.droneId = droneId;
    }

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public Position getDestination() {
        return destination;
    }

    public void setDestination(Position destination) {
        this.destination = destination;
    }
}
