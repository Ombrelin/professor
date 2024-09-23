package fr.arsenelapostolet.professor.server.core.entities;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

public class Student {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String gitlabUsername;
    private final Set<Grade> grades;
    private final String efreiClass;
    private final URI projectUrl;

    public Student(String id, String firstName, String lastName, String email, String gitlabUsername, Set<Grade> grades, String efreiClass, URI projectUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gitlabUsername = gitlabUsername;
        this.grades = grades;
        this.efreiClass = efreiClass;
        this.projectUrl = projectUrl;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getGitlabUsername() {
        return gitlabUsername;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public String getEfreiClass() {
        return efreiClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) && Objects.equals(email, student.email) && Objects.equals(gitlabUsername, student.gitlabUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, gitlabUsername);
    }

    public URI getProjectUrl() {
        return this.projectUrl;
    }
}
