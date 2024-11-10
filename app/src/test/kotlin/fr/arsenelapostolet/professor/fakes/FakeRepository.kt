package fr.arsenelapostolet.professor.fakes

abstract class FakeRepository<T>(data: Map<String, T> = emptyMap()) {
    val data: MutableMap<String, T> = data.toMutableMap()
};