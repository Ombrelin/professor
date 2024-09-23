package fr.arsenelapostolet.professor.server.core.repositories;

import fr.arsenelapostolet.professor.server.core.entities.Student;

import java.util.Collection;
import java.util.Set;

public interface StudentRepository {
    Set<Student> saveStudents(Set<Student> students);
}
