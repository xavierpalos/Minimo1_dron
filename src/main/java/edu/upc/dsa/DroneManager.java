package edu.upc.dsa;

import edu.upc.dsa.models.Drone;
import edu.upc.dsa.models.FlightPlan;
import edu.upc.dsa.models.Pilot;

import java.util.List;

public interface DroneManager {
    Drone addDrone(String id, String name, String model, String manufacturer);
    Pilot addPilot(String id, String name, String surname);

    List<Drone> getDronesByFlightHours();
    List<Pilot> getPilotsByflightHours();

    // ---------- 4. PILOTOS POR HORAS (descendente) ----------
    List<Pilot> getPilotsByFlightHours();

    Drone storeDrone(String droneId);
    Drone repairDrone();

    FlightPlan addFlightPlan(FlightPlan plan);

    List<FlightPlan> getFlightPlansByPilot(String pilotId);
    List<FlightPlan> getFloghtPlansByDron(String dronId);

    // ---------- 9. RESERVAS DE UN DRON ----------
    List<FlightPlan> getFlightPlansByDrone(String droneId);

    int numDrones();
    int numPilots();
    int numDronesInStore();
    void clear();

}
