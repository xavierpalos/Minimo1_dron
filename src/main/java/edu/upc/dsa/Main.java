package edu.upc.dsa;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main {

    public static final String BASE_URI = "http://localhost:8080/dsaApp/";
    final static Logger logger = Logger.getLogger(Main.class);

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("edu.upc.dsa.services");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        DroneManager dm = DroneManagerImpl.getInstance();
        dm.addDrone("D1", "Halcon", "DJI", "Mavic 3");
        dm.addDrone("D2", "Aguila", "Parrot", "Anafi");
        dm.addDrone("D3", "Gorrion", "DJI", "Mini 4");
        dm.addPilot("P1", "Ana", "Garcia");
        dm.addPilot("P2", "Juan", "Lopez");

        final HttpServer server = startServer();
        logger.info("Servidor arrancado en " + BASE_URI);
        logger.info("Pulsa Enter para pararlo...");
        System.in.read();
        server.shutdownNow();
    }
}