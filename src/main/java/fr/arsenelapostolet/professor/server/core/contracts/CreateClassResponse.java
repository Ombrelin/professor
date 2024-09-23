package fr.arsenelapostolet.professor.server.core.contracts;

import fr.arsenelapostolet.professor.server.core.entities.Student;

import java.util.Collection;
import java.util.Set;

public record CreateClassResponse (String name, Set<Student> students) {

}