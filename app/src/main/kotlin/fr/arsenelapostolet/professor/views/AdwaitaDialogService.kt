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

        val toolbarView = ToolbarView()
            .apply { addTopBar(HeaderBar()) }

        val dialog = Dialog.builder()
            .setContentWidth(400)
            .setContentHeight(200)
            .build()
            .apply {
                child = toolbarView
            }

        toolbarView.content = buildBox()
            .apply {
                append(Label(prompt))
                val entry = Entry()
                append(entry)
                val child = buildButton()
                    .apply {
                        onClicked {
                            result = entry.text
                            dialog.close()
                            semaphore.release()
                        }
                    }
                append(child)
            }

        dialog.present(window)
        semaphore.acquire()

        return result
    }

    private fun buildButton(): Button = Button
        .builder()
        .setLabel("Ok")
        .setCssClasses(arrayOf("pill", "accent"))
        .build()

    private fun buildBox(): Box = Box.builder()
        .setOrientation(Orientation.VERTICAL)
        .setSpacing(32)
        .setMarginStart(32)
        .setMarginEnd(32)
        .setMarginTop(32)
        .setMarginBottom(32)
        .build()
}