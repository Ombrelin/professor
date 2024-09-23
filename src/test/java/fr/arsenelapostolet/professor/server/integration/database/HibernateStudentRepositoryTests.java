package fr.arsenelapostolet.professor.server.integration.database;

import fr.arsenelapostolet.professor.server.core.entities.Student;
import fr.arsenelapostolet.professor.server.data.entities.DeliverableEntity;
import fr.arsenelapostolet.professor.server.data.entities.GradeEntity;
import fr.arsenelapostolet.professor.server.data.entities.StudentEntity;
import fr.arsenelapostolet.professor.server.data.repositories.HibernateStudentRepository;
import org.apache.catalina.LifecycleException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static org.hibernate.cfg.JdbcSettings.*;
import static org.hibernate.cfg.TransactionSettings.AUTO_CLOSE_SESSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HibernateStudentRepositoryTests {

    private static PostgreSQLContainer postgreSQLContainer = null;
    private static SessionFactory sessionFactory = null;

    @BeforeAll
    static void setUp() {
        postgreSQLContainer = new PostgreSQLContainer();
        postgreSQLContainer.start();

        sessionFactory = new Configuration().addAnnotatedClass(StudentEntity.class)
                .addAnnotatedClass(GradeEntity.class)
                .addAnnotatedClass(DeliverableEntity.class)
                .setProperty(JAKARTA_JDBC_URL, buildJdbcUri())
                .setProperty(JAKARTA_JDBC_USER, postgreSQLContainer.getUsername())
                .setProperty(JAKARTA_JDBC_PASSWORD, postgreSQLContainer.getPassword())
                .setProperty(AUTO_CLOSE_SESSION, true)
                .setProperty(POOL_SIZE, 1)
                .setProperty(SHOW_SQL, true)
                .setProperty(FORMAT_SQL, true)
                .setProperty(HIGHLIGHT_SQL, true)
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .buildSessionFactory();
    }

    @Test
    public void saveStudents_insertRecordsInDatabase() throws URISyntaxException {
        // Given
        final var students = Set.of(
                new Student(
                        "20220019",
                        "John",
                        "Doe",
                        "test@test.fr",
                        "johnDoe",
                        Set.of(),
                        "lsi1",
                        new URI("http://helloworld.com")
                ),
                new Student(
                        "20220020",
                        "Jane",
                        "Doe",
                        "test1@test.fr",
                        "janeDoe",
                        Set.of(),
                        "lsi2",
                        new URI("http://helloworld1.com")
                )
        );
        final var repository = new HibernateStudentRepository(sessionFactory);

        // When
        repository.saveStudents(students);

        // Then
        final var entitiesFromDb = sessionFactory.fromSession(session -> session.createSelectionQuery(
                "from Student order by id",
                StudentEntity.class
        ).list());

        assertEquals(2, entitiesFromDb.size());
        final var john = entitiesFromDb.getFirst();
        final var jane = entitiesFromDb.getLast();

        assertEquals("20220019", john.getId());
        assertEquals("John", john.getFirstName());
        assertEquals("Doe", john.getLastName());
        assertEquals("test@test.fr", john.getEmail());
        assertEquals("johnDoe", john.getGitlabUsername());
        assertEquals("lsi1", john.getEfreiClass());
        assertEquals("http://helloworld.com", john.getProjectUrl().toString());

        assertEquals("20220020", jane.getId());
        assertEquals("Jane", jane.getFirstName());
        assertEquals("Doe", jane.getLastName());
        assertEquals("test1@test.fr", jane.getEmail());
        assertEquals("janeDoe", jane.getGitlabUsername());
        assertEquals("lsi2", jane.getEfreiClass());
        assertEquals("lsi2", jane.getEfreiClass());
        assertEquals("http://helloworld1.com", jane.getProjectUrl().toString());

    }

    private static String buildJdbcUri() {
        return "jdbc:postgresql://" + postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getFirstMappedPort() + "/" + postgreSQLContainer.getDatabaseName();
    }

    @AfterAll
    static void tearDown() {
        postgreSQLContainer.stop();
    }
}
