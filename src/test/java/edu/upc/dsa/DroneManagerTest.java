package edu.upc.dsa;

import edu.upc.dsa.models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DroneManagerTest {

    DroneManager dm;

    @Before
    public void setUp() {
        dm = DroneManagerImpl.getInstance();
        dm.addDrone("D1", "Halcon", "DJI", "Mavic 3");
        dm.addDrone("D2", "Aguila", "Parrot", "Anafi");
        dm.addDrone("D3", "Gorrion", "DJI", "Mini 4");
        dm.addPilot("P1", "Ana", "Garcia");
        dm.addPilot("P2", "Juan", "Lopez");
    }

    @After
    public void tearDown() {
        dm.clear();
    }

    // Método de ayuda para crear planes de vuelo sin repetir código
    private FlightPlan plan(String droneId, String pilotId, String date, double hours) {
        return new FlightPlan(droneId, pilotId, date, hours,
                new Position(41.27, 1.98), new Position(41.39, 2.17));
    }

    // ---------- TEST 1: altas ----------
    @Test
    public void testAddDroneAndPilot() {
        assertEquals(3, dm.numDrones());
        assertEquals(2, dm.numPilots());

        // Añadir uno nuevo funciona
        assertNotNull(dm.addDrone("D4", "Buho", "Autel", "EVO"));
        assertEquals(4, dm.numDrones());

        // Un id repetido se rechaza y no cambia el total
        assertNull(dm.addDrone("D1", "Otro", "DJI", "Air"));
        assertEquals(4, dm.numDrones());
        assertNull(dm.addPilot("P1", "Otra", "Persona"));
        assertEquals(2, dm.numPilots());
    }

    // ---------- TEST 2: almacén (LIFO) ----------
    @Test
    public void testStoreAndRepairDrone() {
        assertEquals(0, dm.numDronesInStore());

        // Guardamos D1, D2, D3: D3 queda junto a la puerta
        dm.storeDrone("D1");
        dm.storeDrone("D2");
        dm.storeDrone("D3");
        assertEquals(3, dm.numDronesInStore());

        // Un dron que ya está dentro, o que no existe, no se puede guardar
        assertNull(dm.storeDrone("D1"));
        assertNull(dm.storeDrone("D99"));
        assertEquals(3, dm.numDronesInStore());

        // Se repara el último que entró
        Drone repaired = dm.repairDrone();
        assertEquals("D3", repaired.getId());
        assertFalse(repaired.isInMaintenance());
        assertEquals(2, dm.numDronesInStore());

        assertEquals("D2", dm.repairDrone().getId());
        assertEquals("D1", dm.repairDrone().getId());

        // Almacén vacío
        assertNull(dm.repairDrone());
    }

    // ---------- TEST 3: reservas y sus errores ----------
    @Test
    public void testAddFlightPlan() {
        // Reserva correcta: D1 con P1, de 10:00 a 12:00
        assertNotNull(dm.addFlightPlan(plan("D1", "P1", "2026-11-05T10:00", 2)));

        // Error: el dron D1 ya vuela a las 11:00
        assertNull(dm.addFlightPlan(plan("D1", "P2", "2026-11-05T11:00", 1)));

        // Error: el piloto P1 ya vuela a las 11:00
        assertNull(dm.addFlightPlan(plan("D2", "P1", "2026-11-05T11:00", 1)));

        // Correcto: empieza justo cuando acaba el otro (12:00), no solapa
        assertNotNull(dm.addFlightPlan(plan("D1", "P2", "2026-11-05T12:00", 1)));

        // Error: dron en mantenimiento
        dm.storeDrone("D3");
        assertNull(dm.addFlightPlan(plan("D3", "P2", "2026-11-06T10:00", 1)));

        // Error: dron o piloto inexistente
        assertNull(dm.addFlightPlan(plan("D99", "P1", "2026-11-07T10:00", 1)));
        assertNull(dm.addFlightPlan(plan("D2", "P99", "2026-11-07T10:00", 1)));

        // Error: fecha mal escrita
        assertNull(dm.addFlightPlan(plan("D2", "P1", "mañana", 1)));

        // Solo se han guardado las 2 reservas correctas
        assertEquals(2, dm.getFlightPlansByDrone("D1").size());
        assertEquals(1, dm.getFlightPlansByPilot("P1").size());
        assertEquals(1, dm.getFlightPlansByPilot("P2").size());
        assertEquals(0, dm.getFlightPlansByDrone("D3").size());
        assertNull(dm.getFlightPlansByPilot("P99"));
    }

    // ---------- TEST 4: listados por horas de vuelo ----------
    @Test
    public void testListsByFlightHours() {
        dm.addFlightPlan(plan("D1", "P1", "2026-11-05T10:00", 3));   // D1=3, P1=3
        dm.addFlightPlan(plan("D2", "P2", "2026-11-05T10:00", 1));   // D2=1, P2=1
        dm.addFlightPlan(plan("D2", "P1", "2026-11-06T10:00", 4));   // D2=5, P1=7

        // Drones: D2 (5h) > D1 (3h) > D3 (0h)
        List<Drone> drones = dm.getDronesByFlightHours();
        assertEquals("D2", drones.get(0).getId());
        assertEquals("D1", drones.get(1).getId());
        assertEquals("D3", drones.get(2).getId());
        assertEquals(5.0, drones.get(0).getFlightHours(), 0.001);

        // Pilotos: P1 (7h) > P2 (1h)
        List<Pilot> pilots = dm.getPilotsByFlightHours();
        assertEquals("P1", pilots.get(0).getId());
        assertEquals("P2", pilots.get(1).getId());
        assertEquals(7.0, pilots.get(0).getFlightHours(), 0.001);
    }
}
