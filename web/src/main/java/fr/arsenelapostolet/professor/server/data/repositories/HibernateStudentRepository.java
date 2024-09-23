package fr.arsenelapostolet.professor.server.data.repositories;

import com.google.inject.Inject;
import fr.arsenelapostolet.professor.server.core.entities.Student;
import fr.arsenelapostolet.professor.server.core.repositories.StudentRepository;
import fr.arsenelapostolet.professor.server.data.entities.StudentEntity;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.Set;

public class HibernateStudentRepository implements StudentRepository {

    private final SessionFactory sessionFactory;

    @Inject
    public HibernateStudentRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Set<Student> saveStudents(Set<Student> students) {
        sessionFactory.inSession(session -> {
            session.getTransaction().begin();
            students
                    .stream()
                    .map(StudentEntity::new)
                    .forEach(session::persist);
            session.getTransaction().commit();
        });

        return students;
    }
}
