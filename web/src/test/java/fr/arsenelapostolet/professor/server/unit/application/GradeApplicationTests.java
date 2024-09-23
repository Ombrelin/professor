package fr.arsenelapostolet.professor.server.unit.application;

import fr.arsenelapostolet.professor.server.core.application.GradesApplication;
import fr.arsenelapostolet.professor.server.core.entities.Student;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GradeApplicationTests {

    private final FakeStudentRepository studentRepository = new FakeStudentRepository();
    private final GradesApplication target = new GradesApplication(studentRepository);

    @Test
    public void createClass_buildClassAndInserts() {
        // Given
        final var csvLines = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/students.csv")))
                .lines()
                .toList();

        // When
        final var result = target.createClass("lsi1", csvLines.stream());

        // Then
        assertEquals("lsi1", result.name());
        assertThat(result.name()).isEqualTo("lsi1");
        assertThat(result.students())
                .hasSize(Math.toIntExact(csvLines.size() - 1))
                .allMatch(student -> student.getId() != null
                        && student.getEfreiClass().equals("lsi1")
                        && student.getGrades().isEmpty()
                        && student.getFirstName() != null
                        && student.getLastName() != null
                        && student.getEmail() != null && student.getEmail().contains("@")
                        && student.getGitlabUsername() != null
                        && student.getProjectUrl() != null);
        assertThat(result.students()).containsExactlyInAnyOrder(studentRepository.getData().values().toArray(new Student[0]));
    }

}
