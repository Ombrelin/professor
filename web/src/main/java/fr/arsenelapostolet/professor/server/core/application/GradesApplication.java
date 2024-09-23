package fr.arsenelapostolet.professor.server.core.application;

import com.google.inject.Inject;
import fr.arsenelapostolet.professor.server.core.contracts.CreateClassResponse;
import fr.arsenelapostolet.professor.server.core.entities.Student;
import fr.arsenelapostolet.professor.server.core.exceptions.InvalidResourceException;
import fr.arsenelapostolet.professor.server.core.repositories.StudentRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GradesApplication {

    private final StudentRepository studentRepository;

    @Inject
    public GradesApplication(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public CreateClassResponse createClass(String className, Stream<String> csvLines) {
        final var students = csvLines
                .skip(1)
                .map(line -> line.split(","))
                .map(csvLine -> parseStudentFromCsv(className, csvLine));

        return new CreateClassResponse(
                className,
                studentRepository.saveStudents(students.collect(Collectors.toSet()))
        );
    }

    private Student parseStudentFromCsv(String className, String[] csvLine) {
        try {
            return new Student(
                    csvLine[0],
                    csvLine[1],
                    csvLine[2],
                    csvLine[4],
                    csvLine[5],
                    new HashSet<>(),
                    className,
                    new URI(csvLine[6])
            );
        } catch (URISyntaxException uriSyntaxException) {
            throw new InvalidResourceException(
                    String.format(
                            "Student with id %s has an invalid project URL : %s", csvLine[0],
                            csvLine[6]
                    ),
                    uriSyntaxException
            );
        }
    }

}
