package fr.arsenelapostolet.professor.core.contracts

import fr.arsenelapostolet.professor.core.entities.Student

class CreateClassResponse(val name: String, students: Set<Student>) {
    val students: Set<Student> = students
}