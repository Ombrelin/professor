package fr.arsenelapostolet.professor.views

import io.github.jwharm.javagi.gobject.types.Types
import org.gnome.glib.Type
import org.gnome.gobject.GObject
import java.lang.foreign.MemorySegment

private class GradesViewLine : GObject {
    constructor() : super(GradesViewLine::class.java)

    constructor(address: MemorySegment?) : super(address)

    companion object {
        val gtype: Type = Types.register(GradesViewLine::class.java)
    }
}