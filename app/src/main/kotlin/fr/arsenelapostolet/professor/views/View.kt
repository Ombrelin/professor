package fr.arsenelapostolet.professor.views

import org.gnome.gtk.Widget

interface View {
    fun getWidget(): Widget
}