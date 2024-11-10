package fr.arsenelapostolet.professor.core.repositories

import fr.arsenelapostolet.professor.core.entities.Student

interface StudentRepository {
    fun saveStudents(students: Set<Student>): Set<Student>
}
