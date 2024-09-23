package fr.arsenelapostolet.professor.server.data.entities;

import fr.arsenelapostolet.professor.server.core.entities.Grade;
import fr.arsenelapostolet.professor.server.core.entities.Student;
import jakarta.persistence.*;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "Student")
public class StudentEntity {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gitlabUsername;
    private URI projectUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<GradeEntity> grades;
    private String efreiClass;

    public StudentEntity(Student student) {
        this.id = student.getId();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.email = student.getEmail();
        this.gitlabUsername = student.getGitlabUsername();
        this.grades = student
                .getGrades()
                .stream()
                .map(grade -> new GradeEntity(grade, this))
                .collect(Collectors.toSet());
        this.efreiClass = student.getEfreiClass();
        this.projectUrl = student.getProjectUrl();
    }

    public StudentEntity() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGitlabUsername() {
        return gitlabUsername;
    }

    public void setGitlabUsername(String gitlabUsername) {
        this.gitlabUsername = gitlabUsername;
    }

    public Set<GradeEntity> getGrades() {
        return grades;
    }

    public void setGrades(Set<GradeEntity> grades) {
        this.grades = grades;
    }

    public String getEfreiClass() {
        return efreiClass;
    }

    public void setEfreiClass(String efreiClass) {
        this.efreiClass = efreiClass;
    }

    public URI getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(URI projectUrl) {
        this.projectUrl = projectUrl;
    }
}
