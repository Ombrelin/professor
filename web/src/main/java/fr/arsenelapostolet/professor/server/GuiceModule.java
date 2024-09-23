package fr.arsenelapostolet.professor.server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import fr.arsenelapostolet.professor.server.core.application.GradesApplication;
import fr.arsenelapostolet.professor.server.core.entities.Deliverable;
import fr.arsenelapostolet.professor.server.core.repositories.StudentRepository;
import fr.arsenelapostolet.professor.server.data.entities.DeliverableEntity;
import fr.arsenelapostolet.professor.server.data.entities.GradeEntity;
import fr.arsenelapostolet.professor.server.data.entities.StudentEntity;
import fr.arsenelapostolet.professor.server.data.repositories.HibernateStudentRepository;
import fr.arsenelapostolet.professor.server.web.ExampleService;
import fr.arsenelapostolet.professor.server.web.ExempleServiceImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static org.hibernate.cfg.JdbcSettings.*;
import static org.hibernate.cfg.TransactionSettings.AUTO_CLOSE_SESSION;

public class GuiceModule extends AbstractModule {
    private final Application.DatabaseConfiguration databaseConfiguration;

    public GuiceModule(Application.DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    @Override
    protected void configure() {
        bind(ExampleService.class).to(ExempleServiceImpl.class);
        bind(SessionFactory.class).toProvider(this::createSessionFactory).in(Singleton.class);
        bind(GradesApplication.class);
        bind(StudentRepository.class).to(HibernateStudentRepository.class);
    }

    private SessionFactory createSessionFactory() {

        return new Configuration()
                .addAnnotatedClass(StudentEntity.class)
                .addAnnotatedClass(GradeEntity.class)
                .addAnnotatedClass(DeliverableEntity.class)
                .setProperty(JAKARTA_JDBC_URL, buildJdbcUri())
                .setProperty(JAKARTA_JDBC_USER, databaseConfiguration.username())
                .setProperty(JAKARTA_JDBC_PASSWORD, databaseConfiguration.password())
                .setProperty(AUTO_CLOSE_SESSION, true)
                .setProperty(POOL_SIZE, 1)
                .setProperty(SHOW_SQL, true)
                .setProperty(FORMAT_SQL, true)
                .setProperty(HIGHLIGHT_SQL, true)
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .buildSessionFactory();

    }

    private String buildJdbcUri() {
        return "jdbc:postgresql://"
                + databaseConfiguration.hostname() +
                ":" + databaseConfiguration.port() +
                "/" + databaseConfiguration.database();
    }
}
