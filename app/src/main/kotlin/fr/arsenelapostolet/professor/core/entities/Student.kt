package fr.arsenelapostolet.professor.core.entities

import java.net.URI

class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val gitlabUsername: String,
    val grades: Set<Grade>,
    val efreiClass: String,
    val projectUrl: URI,
) {
    val fullName: String
        get() = "$firstName $lastName"
}