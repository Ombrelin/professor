package fr.arsenelapostolet.professor.views

import fr.arsenelapostolet.professor.core.entities.Student
import io.github.jwharm.javagi.gobject.annotations.RegisteredType
import io.github.jwharm.javagi.gobject.types.Types
import org.gnome.gio.ListStore
import org.gnome.gobject.GObject
import org.gnome.gtk.ColumnView
import org.gnome.gtk.ColumnViewColumn
import org.gnome.gtk.ListItem
import org.gnome.gtk.ListItemFactory
import org.gnome.gtk.ScrolledWindow
import org.gnome.gtk.SignalListItemFactory
import org.gnome.gtk.Label
import java.lang.foreign.MemorySegment
import org.gnome.glib.Type
import org.gnome.glib.GLib
class GradesView : ScrolledWindow() {



    init {
        val store = ListStore<GradesViewLine>(GradesViewLine.gtype)

        val treeView = ColumnView()
        child = treeView
        val itemFactory = SignalListItemFactory()
        itemFactory.onSetup { arg ->
            val listItem = arg as ListItem
            listItem.child = Label()
        }
        itemFactory.onBind { arg ->
            val listItem = arg as ListItem
            (listItem.child as Label).text = "test"
        }
        val column = ColumnViewColumn("Firstname", itemFactory)
        treeView.appendColumn(column)

    }

}