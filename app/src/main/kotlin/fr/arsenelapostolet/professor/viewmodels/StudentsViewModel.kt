package fr.arsenelapostolet.professor.viewmodels

import fr.arsenelapostolet.professor.core.application.GradesApplication
import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.viewmodels.utils.FileService
import fr.arsenelapostolet.professor.viewmodels.utils.ViewModel
import fr.arsenelapostolet.professor.viewmodels.utils.ViewModelProperty
import java.io.File

class StudentsViewModel(val gradesApplication: GradesApplication, val fileService: FileService) : ViewModel {
    val students: ViewModelProperty<Collection<Student>> = ViewModelProperty(emptySet())

    val studentsLoaded get() = !students.value.isEmpty()

    override suspend fun init() {
        students.value = gradesApplication.getStudent()
    }

    suspend fun importClass() {
        val filePath = fileService.pickFile()
        gradesApplication.importStudents(File(filePath).readLines())
        students.value = gradesApplication.getStudent()
    }
}