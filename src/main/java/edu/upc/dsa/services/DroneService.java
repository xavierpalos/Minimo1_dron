package edu.upc.dsa.services;

import edu.upc.dsa.DroneManager;
import edu.upc.dsa.DroneManagerImpl;
import edu.upc.dsa.models.*;
import edu.upc.dsa.models.TO.DroneTO;
import edu.upc.dsa.models.TO.PilotTO;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
public class DroneService {

    private DroneManager dm;

    public DroneService() {
        this.dm = DroneManagerImpl.getInstance();
    }

    // 1. Añadir dron
    @POST
    @Path("/drones")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDrone(DroneTO to) {
        Drone d = dm.addDrone(to.getId(), to.getName(), to.getManufacturer(), to.getModel());
        if (d == null) return Response.status(409).build();
        return Response.status(201).entity(d).build();
    }

    // 2. Añadir piloto
    @POST
    @Path("/pilots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPilot(PilotTO to) {
        Pilot p = dm.addPilot(to.getId(), to.getName(), to.getSurname());
        if (p == null) return Response.status(409).build();
        return Response.status(201).entity(p).build();
    }

    // 3. Drones por horas de vuelo (desc)
    @GET
    @Path("/drones")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDrones() {
        List<Drone> list = dm.getDronesByFlightHours();
        GenericEntity<List<Drone>> entity = new GenericEntity<List<Drone>>(list) {};
        return Response.status(200).entity(entity).build();
    }

    // 4. Pilotos por horas de vuelo (desc)
    @GET
    @Path("/pilots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPilots() {
        List<Pilot> list = dm.getPilotsByFlightHours();
        GenericEntity<List<Pilot>> entity = new GenericEntity<List<Pilot>>(list) {};
        return Response.status(200).entity(entity).build();
    }

    // 5. Guardar dron en el almacén
    @POST
    @Path("/warehouse/{droneId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeDrone(@PathParam("droneId") String droneId) {
        Drone d = dm.storeDrone(droneId);
        if (d == null) return Response.status(404).build();
        return Response.status(201).entity(d).build();
    }

    // 6. Reparar el dron más cercano a la puerta
    @PUT
    @Path("/warehouse/repair")
    @Produces(MediaType.APPLICATION_JSON)
    public Response repairDrone() {
        Drone d = dm.repairDrone();
        if (d == null) return Response.status(404).build();
        return Response.status(200).entity(d).build();
    }

    // 7. Añadir reserva de plan de vuelo
    @POST
    @Path("/flightplans")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFlightPlan(FlightPlan plan) {
        FlightPlan result = dm.addFlightPlan(plan);
        if (result == null) return Response.status(400).build();
        return Response.status(201).entity(result).build();
    }

    // 8. Reservas de un piloto
    @GET
    @Path("/pilots/{id}/flightplans")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlansByPilot(@PathParam("id") String id) {
        List<FlightPlan> list = dm.getFlightPlansByPilot(id);
        if (list == null) return Response.status(404).build();
        GenericEntity<List<FlightPlan>> entity = new GenericEntity<List<FlightPlan>>(list) {};
        return Response.status(200).entity(entity).build();
    }

    // 9. Reservas de un dron
    @GET
    @Path("/drones/{id}/flightplans")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlansByDrone(@PathParam("id") String id) {
        List<FlightPlan> list = dm.getFlightPlansByDrone(id);
        if (list == null) return Response.status(404).build();
        GenericEntity<List<FlightPlan>> entity = new GenericEntity<List<FlightPlan>>(list) {};
        return Response.status(200).entity(entity).build();
    }
}
