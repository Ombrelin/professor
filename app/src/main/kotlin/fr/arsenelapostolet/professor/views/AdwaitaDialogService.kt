package fr.arsenelapostolet.professor.views

import fr.arsenelapostolet.professor.viewmodels.utils.DialogService
import kotlinx.coroutines.sync.Semaphore
import org.gnome.adw.ApplicationWindow
import org.gnome.adw.Dialog
import org.gnome.adw.HeaderBar
import org.gnome.adw.ToolbarView
import org.gnome.gtk.*

class AdwaitaDialogService(private val window: ApplicationWindow) : DialogService {
    override suspend fun prompt(prompt: String): String? {
        val semaphore = Semaphore(1, 1)
        var result: String? = null

        val dialog = Dialog()
        dialog.contentWidth = 400
        dialog.contentHeight = 200
        val toolbarView = ToolbarView()
        dialog.child = toolbarView
        toolbarView.addTopBar(HeaderBar())

        val box = Box(Orientation.VERTICAL, 32)
        box.marginStart = 32
        box.marginEnd = 32
        box.marginTop = 32
        box.marginBottom = 32
        box.append(Label(prompt))
        val entry = Entry()
        box.append(entry)

        val button = Button()
        button.label = "Ok"
        button.cssClasses = arrayOf("pill", "accent")
        button.onClicked {
            result = entry.text
            dialog.close()
            semaphore.release()
        }
        box.append(button)

        toolbarView.content = box
        dialog.present(window)
        semaphore.acquire()

        return result
    }


}