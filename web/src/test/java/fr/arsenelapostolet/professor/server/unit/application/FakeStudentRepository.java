package fr.arsenelapostolet.professor.server.unit.application;

import fr.arsenelapostolet.professor.server.core.entities.Student;
import fr.arsenelapostolet.professor.server.core.repositories.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FakeStudentRepository implements StudentRepository {

    private final Map<String, Student> data = new HashMap<>();

    @Override
    public Set<Student> saveStudents(Set<Student> students) {
        data.putAll(students.stream().collect(Collectors.toMap(Student::getId, student -> student)));
        return students;
    }

    public Map<String, Student> getData() {
        return data;
    }
}
