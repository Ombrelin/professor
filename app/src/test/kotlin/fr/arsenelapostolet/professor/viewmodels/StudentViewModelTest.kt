package fr.arsenelapostolet.professor.viewmodels


import fr.arsenelapostolet.professor.core.application.GradesApplication
import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.fakes.FakeStudentRepository
import fr.arsenelapostolet.professor.viewmodels.utils.FileService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudentViewModelTest {
    private val fakeRepository = FakeStudentRepository()
    private val mockFilPicker = mockk<FileService>()
    private val target = StudentsViewModel(GradesApplication(fakeRepository), mockFilPicker)

    @Test
    fun `When initializing viewmodel with no students in database, studentsLoaded is false`() = runBlocking {
        target.init()
        assertFalse(target.studentsLoaded)
    }

    @Test
    fun `When importing a new class from file, students are loaded in db and studentsLoaded is true`() = runBlocking {
        target.init()
        val testFilePath = StudentViewModelTest::class.java.getResource("/students.csv").path
        coEvery { mockFilPicker.pickFile() } returns testFilePath

        target.importClass()

        assertEquals(75, fakeRepository.data.size)
        assertEquals(75, target.students.value.size)
        assertTrue(target.studentsLoaded)
    }

    @Test
    fun `When importing a new class from file twice, all students are loaded in db and studentsLoaded is true`() = runBlocking {
        target.init()
        val testFilePath = StudentViewModelTest::class.java.getResource("/students.csv").path
        val otherTestFilePath = StudentViewModelTest::class.java.getResource("/other-students.csv").path
        coEvery { mockFilPicker.pickFile() } returnsMany listOf(testFilePath, otherTestFilePath)

        target.importClass()
        target.importClass()

        assertEquals(76, fakeRepository.data.size)
        assertEquals(76, target.students.value.size)
        assertTrue(target.studentsLoaded)
    }


    @Test
    fun `When initializing viewmodel with student in database, studentsLoaded is true`() = runBlocking {
        fakeRepository.data["1"] = Student(
            "40284284",
            "John",
            "Shepard",
            "jshepard@n7.gov.all",
            "jshepard",
            mutableListOf(),
            "lsi1",
            URI.create("https://gitlab.n7.gov.all/stopthecollectors")
        )
        target.init()
        assertTrue(target.studentsLoaded)
    }
}