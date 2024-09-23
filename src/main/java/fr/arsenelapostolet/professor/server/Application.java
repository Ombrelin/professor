package fr.arsenelapostolet.professor.server;

import jakarta.servlet.ServletException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.Closeable;
import java.io.File;

public class Application implements Closeable {


    private final Tomcat tomcat;

    public static void main(String[] args) throws LifecycleException, ServletException {
        try (final var application = new Application(
                new DatabaseConfiguration(
                        System.getenv("DB_USERNAME"),
                        System.getenv("DB_PASSWORD"),
                        System.getenv("DB_NAME"),
                        System.getenv("DB_HOSTNAME"),
                        System.getenv("DB_PORT")
                )
        )) {
            application.launchBlocking();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Application(DatabaseConfiguration databaseConfiguration) {
        tomcat = new Tomcat();
        tomcat.setPort(8080);
        final var connector = tomcat.getConnector();
        Context context = tomcat.addWebapp("/api", new File(".").getAbsolutePath());
        ServletContainer servletContainer = new ServletContainer(new RestConfig(databaseConfiguration));
        tomcat.addServlet("/api", "restConfig", servletContainer);
        context.addServletMappingDecoded("/v1/*", "restConfig");
    }

    public void launchNonBlocking() {
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    public void launchBlocking() {
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            tomcat.stop();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    public record DatabaseConfiguration(
            String username,
            String password,
            String database,
            String hostname,
            String port
    ) {
    }
}
