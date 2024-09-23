package fr.arsenelapostolet.professor.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.research.ws.wadl.Resource;
import fr.arsenelapostolet.professor.server.core.application.GradesApplication;
import fr.arsenelapostolet.professor.server.core.entities.Deliverable;
import fr.arsenelapostolet.professor.server.data.entities.GradeEntity;
import fr.arsenelapostolet.professor.server.data.entities.StudentEntity;
import fr.arsenelapostolet.professor.server.web.ExampleService;
import org.glassfish.jersey.inject.hk2.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static org.hibernate.cfg.JdbcSettings.*;
import static org.hibernate.cfg.TransactionSettings.AUTO_CLOSE_SESSION;


public class RestConfig extends ResourceConfig {
    private final Application.DatabaseConfiguration databaseConfiguration;

    public RestConfig(Application.DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
        packages("fr.arsenelapostolet.professor.server.web");
        Injector injector = Guice.createInjector(new GuiceModule(databaseConfiguration));
        HK2toGuiceModule hk2Module = new HK2toGuiceModule(injector);
        register(hk2Module);
        registerClasses(MultiPartFeature.class);
    }

    private static final class HK2toGuiceModule extends AbstractBinder {
        private final Injector guiceInjector;

        public HK2toGuiceModule(Injector guiceInjector) {
            this.guiceInjector = guiceInjector;
        }

        @Override
        protected void configure() {
            bindFactory(() -> guiceInjector.getInstance(ExampleService.class)).to(ExampleService.class);
            bindFactory(() -> guiceInjector.getInstance(GradesApplication.class)).to(GradesApplication.class);
        }
    }
}