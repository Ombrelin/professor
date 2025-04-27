package fr.arsenelapostolet.professor.core.application

import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.core.exceptions.InvalidResourceException
import fr.arsenelapostolet.professor.core.repositories.StudentRepository
import java.net.URI
import java.net.URISyntaxException

class GradesApplication(private val studentRepository: StudentRepository) {

    fun getStudent(): Collection<Student> {
        return studentRepository.getAllStudents()
    }

    fun importStudents(csvLines: Collection<String>): Set<Student> {
        val students = csvLines
            .drop(1)
            .map { line: String -> line.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
            .map { csvLine: Array<String> -> parseStudentFromCsv(csvLine) }

        return studentRepository.saveStudents(students.toSet())
    }

    private fun parseStudentFromCsv(csvLine: Array<String>): Student {
        if(csvLine.size < 7){
            throw InvalidResourceException(
                String.format(
                    "Line with id %s too few columns : %s",
                    csvLine[0],
                    csvLine.joinToString(",")
                )
            )
        }

        try {
            return Student(
                csvLine[1],
                csvLine[2],
                csvLine[3],
                csvLine[5],
                csvLine[6],
                mutableListOf(),
                csvLine[0],
                URI(csvLine[7])
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
