package fr.arsenelapostolet.professor.views

import fr.arsenelapostolet.professor.viewmodels.utils.FileService
import kotlinx.coroutines.*
import org.gnome.adw.ApplicationWindow
import org.gnome.gio.AsyncReadyCallback
import org.gnome.gio.AsyncResult
import org.gnome.gio.Cancellable
import org.gnome.gobject.GObject
import org.gnome.gtk.FileDialog
import java.lang.foreign.MemorySegment

class FilePicker(val window: ApplicationWindow) : FileService {

    override suspend fun pickFile(): String {
        val dialog = FileDialog()
        val callBack = PickCallback()
        dialog.open(window, Cancellable(), callBack)
        while (!callBack.finished) {
            delay(1000)
        }

        return callBack.file.toString()
    }

    private class PickCallback : AsyncReadyCallback {
        var _finished = false
        var _file: String? = null

        val finished
            get() = _finished

        val file
            get() = _file

        override fun run(
            sourceObject: GObject?,
            res: AsyncResult?,
            data: MemorySegment?,
        ) {
            _file = (sourceObject as FileDialog).openFinish(res).path
            _finished = true
        }

    }
}
