package fr.arsenelapostolet.professor.server.integration.api;

import com.google.gson.Gson;
import fr.arsenelapostolet.professor.server.Application;
import fr.arsenelapostolet.professor.server.core.contracts.CreateClassResponse;
import fr.arsenelapostolet.professor.server.core.entities.Student;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GradeApiTests {

    public static final HttpClient httpClient = HttpClient
            .newHttpClient();
    private static PostgreSQLContainer postgreSQLContainer = null;
    private static Application application;

    @BeforeAll
    static void setUp() {
        Network network = Network.newNetwork();
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer()
                .withNetwork(network)
                .withNetworkAliases("database");
        postgreSQLContainer.start();
        application = new Application(new Application.DatabaseConfiguration(
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword(),
                postgreSQLContainer.getDatabaseName(),
                postgreSQLContainer.getHost(),
                postgreSQLContainer.getFirstMappedPort().toString()

        ));
        application.launchNonBlocking();
    }

    @Test
    public void createClass_returnsClassDataAndCreatesClassInDatabase() throws URISyntaxException, IOException, InterruptedException {
        // Given
        final var studentFileLines = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream("/students.csv")))
                .lines()
                .toList();
        final var studentsFileContent = String.join("\n", studentFileLines);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/grades/classes/lsi1"))
                .POST(HttpRequest.BodyPublishers.ofString(studentsFileContent))
                .header("Content-Type", "text/plain")
                .header("Accept", "*/*")
                .build();

        // When
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Then
        assertEquals(200, response.statusCode());
        final var payload = new Gson().fromJson(response.body(), CreateClassResponse.class);
        assertEquals("lsi1", payload.name());
        assertEquals(studentFileLines.size() - 1, payload.students().size());
        assertThat(payload.students().stream().map(Student::getId)).doesNotContain((String) null);
        assertThat(payload.students().stream().map(Student::getFirstName)).doesNotContain((String) null);
        assertThat(payload.students().stream().map(Student::getLastName)).doesNotContain((String) null);
        assertThat(payload.students().stream().map(Student::getEmail)).doesNotContain((String) null);
        assertThat(payload.students().stream().map(Student::getGitlabUsername)).doesNotContain((String) null);
        assertThat(payload.students().stream().map(Student::getEfreiClass)).doesNotContain((String) null);
        assertThat(payload.students().stream().map(Student::getProjectUrl)).doesNotContain((URI) null);
    }

    @AfterAll
    static void tearDown() {
        application.close();
        postgreSQLContainer.stop();
        httpClient.close();
    }

}
