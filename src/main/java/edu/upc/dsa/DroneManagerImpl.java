package edu.upc.dsa;

import edu.upc.dsa.models.*;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;


public class DroneManagerImpl implements DroneManager {

    // ---------- SINGLETON ----------
    private static DroneManagerImpl instance;
    final static Logger logger = Logger.getLogger(DroneManagerImpl.class);

    private Map<String, Drone> drones;
    private Map<String, Pilot> pilots;
    private Stack<Drone> store;
    private Map<String, List<FlightPlan>> plansByDrone;
    private Map<String, List<FlightPlan>> plansByPilot;

    private DroneManagerImpl() {
        this.drones = new HashMap<>();
        this.pilots = new HashMap<>();
        this.store = new Stack<>();
        this.plansByDrone = new HashMap<>();
        this.plansByPilot = new HashMap<>();
    }

    public static DroneManagerImpl getInstance() {
        if (instance == null) instance = new DroneManagerImpl();
        return instance;
    }

    // ---------- 1. AÑADIR DRON ----------
    @Override
    public Drone addDrone(String id, String name, String manufacturer, String model) {
        logger.info("addDrone(" + id + ", " + name + ", " + manufacturer + ", " + model + ")");
        if (drones.containsKey(id)) {
            logger.error("addDrone: ya existe un dron con id " + id);
            return null;
        }
        Drone d = new Drone(id, name, manufacturer, model);
        drones.put(id, d);
        plansByDrone.put(id, new ArrayList<>());
        logger.info("addDrone: dron añadido. Total: " + drones.size());
        return d;
    }

    // ---------- 2. AÑADIR PILOTO ----------
    @Override
    public Pilot addPilot(String id, String name, String surname) {
        logger.info("addPilot(" + id + ", " + name + ", " + surname + ")");
        if (pilots.containsKey(id)) {
            logger.error("addPilot: ya existe un piloto con id " + id);
            return null;
        }
        Pilot p = new Pilot(id, name, surname);
        pilots.put(id, p);
        plansByPilot.put(id, new ArrayList<>());
        logger.info("addPilot: piloto añadido. Total: " + pilots.size());
        return p;
    }

    // ---------- 3. DRONES POR HORAS (descendente) ----------
    @Override
    public List<Drone> getDronesByFlightHours() {
        logger.info("getDronesByFlightHours()");
        List<Drone> list = new ArrayList<>(drones.values());
        list.sort((d1, d2) -> Double.compare(d2.getFlightHours(), d1.getFlightHours()));
        logger.info("getDronesByFlightHours: " + list.size() + " drones");
        return list;
    }

    @Override
    public List<Pilot> getPilotsByflightHours() {
        return List.of();
    }

    // ---------- 4. PILOTOS POR HORAS (descendente) ----------
    @Override
    public List<Pilot> getPilotsByFlightHours() {
        logger.info("getPilotsByFlightHours()");
        List<Pilot> list = new ArrayList<>(pilots.values());
        list.sort((p1, p2) -> Double.compare(p2.getFlightHours(), p1.getFlightHours()));
        logger.info("getPilotsByFlightHours: " + list.size() + " pilotos");
        return list;
    }

    // ---------- 5. GUARDAR EN EL ALMACÉN ----------
    @Override
    public Drone storeDrone(String droneId) {
        logger.info("storeDrone(" + droneId + ")");
        Drone d = drones.get(droneId);
        if (d == null) {
            logger.error("storeDrone: el dron " + droneId + " no existe");
            return null;
        }
        if (d.isInMaintenance()) {
            logger.error("storeDrone: el dron " + droneId + " ya está en el almacén");
            return null;
        }
        d.setInMaintenance(true);
        store.push(d);
        logger.info("storeDrone: dron guardado. En almacén: " + store.size());
        return d;
    }

    // ---------- 6. REPARAR EL DRON DE LA PUERTA ----------
    @Override
    public Drone repairDrone() {
        logger.info("repairDrone()");
        if (store.isEmpty()) {
            logger.error("repairDrone: el almacén está vacío");
            return null;
        }
        Drone d = store.pop();
        d.setInMaintenance(false);
        logger.info("repairDrone: reparado el dron " + d.getId() + ". En almacén: " + store.size());
        return d;
    }

    // ---------- 7. AÑADIR PLAN DE VUELO ----------
    @Override
    public FlightPlan addFlightPlan(FlightPlan plan) {
        logger.info("addFlightPlan(dron=" + plan.getDroneId() + ", piloto=" + plan.getPilotId()
                + ", fecha=" + plan.getDate() + ", horas=" + plan.getHours() + ")");

        Drone d = drones.get(plan.getDroneId());
        if (d == null) {
            logger.error("addFlightPlan: el dron " + plan.getDroneId() + " no existe");
            return null;
        }
        Pilot p = pilots.get(plan.getPilotId());
        if (p == null) {
            logger.error("addFlightPlan: el piloto " + plan.getPilotId() + " no existe");
            return null;
        }
        if (d.isInMaintenance()) {
            logger.error("addFlightPlan: el dron " + d.getId() + " está en mantenimiento");
            return null;
        }

        LocalDateTime start;
        try {
            start = LocalDateTime.parse(plan.getDate());
        } catch (DateTimeParseException e) {
            logger.error("addFlightPlan: fecha con formato incorrecto: " + plan.getDate());
            return null;
        }
        LocalDateTime end = start.plusMinutes((long) (plan.getHours() * 60));

        if (overlaps(plansByDrone.get(d.getId()), start, end)) {
            logger.error("addFlightPlan: el dron " + d.getId() + " ya tiene un vuelo en ese intervalo");
            return null;
        }
        if (overlaps(plansByPilot.get(p.getId()), start, end)) {
            logger.error("addFlightPlan: el piloto " + p.getId() + " ya tiene un vuelo en ese intervalo");
            return null;
        }

        plansByDrone.get(d.getId()).add(plan);
        plansByPilot.get(p.getId()).add(plan);
        d.addFlightHours(plan.getHours());
        p.addFlightHours(plan.getHours());

        logger.info("addFlightPlan: reserva creada. Horas dron=" + d.getFlightHours()
                + ", horas piloto=" + p.getFlightHours());
        return plan;
    }

    // Método privado de ayuda: ¿algún vuelo de la lista se solapa con [start, end)?
    private boolean overlaps(List<FlightPlan> plans, LocalDateTime start, LocalDateTime end) {
        for (FlightPlan fp : plans) {
            LocalDateTime s = LocalDateTime.parse(fp.getDate());
            LocalDateTime e = s.plusMinutes((long) (fp.getHours() * 60));
            if (start.isBefore(e) && s.isBefore(end)) return true;
        }
        return false;
    }

    // ---------- 8. RESERVAS DE UN PILOTO ----------
    @Override
    public List<FlightPlan> getFlightPlansByPilot(String pilotId) {
        logger.info("getFlightPlansByPilot(" + pilotId + ")");
        if (!pilots.containsKey(pilotId)) {
            logger.error("getFlightPlansByPilot: el piloto " + pilotId + " no existe");
            return null;
        }
        List<FlightPlan> list = plansByPilot.get(pilotId);
        logger.info("getFlightPlansByPilot: " + list.size() + " reservas");
        return list;
    }

    @Override
    public List<FlightPlan> getFloghtPlansByDron(String dronId) {
        return List.of();
    }

    // ---------- 9. RESERVAS DE UN DRON ----------
    @Override
    public List<FlightPlan> getFlightPlansByDrone(String droneId) {
        logger.info("getFlightPlansByDrone(" + droneId + ")");
        if (!drones.containsKey(droneId)) {
            logger.error("getFlightPlansByDrone: el dron " + droneId + " no existe");
            return null;
        }
        List<FlightPlan> list = plansByDrone.get(droneId);
        logger.info("getFlightPlansByDrone: " + list.size() + " reservas");
        return list;
    }

    // ---------- AUXILIARES ----------
    @Override public int numDrones()        { return drones.size(); }
    @Override public int numPilots()        { return pilots.size(); }
    @Override public int numDronesInStore() { return store.size(); }

    @Override
    public void clear() {
        logger.info("clear()");
        drones.clear();
        pilots.clear();
        store.clear();
        plansByDrone.clear();
        plansByPilot.clear();
        logger.info("clear: datos borrados");
    }
}