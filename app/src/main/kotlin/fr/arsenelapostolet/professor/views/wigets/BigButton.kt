package fr.arsenelapostolet.professor.views.wigets

import org.gnome.gtk.Button

class BigButton : Button {
    constructor(label: String) : super() {
        this.label = label
        this.cssClasses = arrayOf("pill", "accent")

        this.setSizeRequest(300, 32)
    }

    constructor(label: String, action: () -> Unit) : this(label) {
        this.onClicked {
            action()
        }

    }
}