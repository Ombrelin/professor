package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.core.contracts.CreateClassResponse
import fr.arsenelapostolet.professor.core.entities.Grade
import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.core.exceptions.InvalidResourceException
import fr.arsenelapostolet.professor.core.repositories.StudentRepository
import java.net.URI
import java.net.URISyntaxException

class GradesApplication(private val studentRepository: StudentRepository) {

    fun getStudent(): Collection<Student> {
        return studentRepository.getAllStudents()
    }

    fun createClass(className: String, csvLines: Collection<String>): CreateClassResponse {
        val students = csvLines
            .drop(1)
            .map { line: String -> line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
            .map { csvLine: Array<String> -> parseStudentFromCsv(className, csvLine) }

        return CreateClassResponse(
            className,
            studentRepository.saveStudents(students.toSet())
        )
    }

    private fun parseStudentFromCsv(className: String, csvLine: Array<String>): Student {
        try {
            return Student(
                csvLine[0],
                csvLine[1],
                csvLine[2],
                csvLine[4],
                csvLine[5],
                HashSet(),
                className,
                URI(csvLine[6])
            )
        } catch (uriSyntaxException: URISyntaxException) {
            throw InvalidResourceException(
                String.format(
                    "Student with id %s has an invalid project URL : %s", csvLine[0],
                    csvLine[6]
                ),
                uriSyntaxException
            )
        }
    }
}
