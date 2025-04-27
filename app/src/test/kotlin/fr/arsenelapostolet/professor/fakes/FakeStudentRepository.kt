package fr.arsenelapostolet.professor.fakes

import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.core.repositories.StudentRepository

class FakeStudentRepository(data: Map<String, Student> = emptyMap()) : StudentRepository,
    FakeRepository<Student>(data) {

    override fun saveStudents(students: Set<Student>): Set<Student> {
        for (student in students) {
            data[student.id] = student
        }

        return students.toSet();
    }

    override fun getAllStudents(): Set<Student> = data.values.toSet();
}