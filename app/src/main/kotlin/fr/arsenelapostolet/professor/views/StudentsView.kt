package fr.arsenelapostolet.professor.views

import fr.arsenelapostolet.professor.viewmodels.StudentsViewModel
import kotlinx.coroutines.*
import org.checkerframework.checker.units.qual.s
import org.gnome.adw.ActionRow
import org.gnome.adw.ApplicationWindow
import org.gnome.adw.ExpanderRow
import org.gnome.adw.PreferencesGroup
import org.gnome.gio.Cancellable
import org.gnome.gtk.Align
import org.gnome.gtk.Button
import org.gnome.gtk.FlowBox
import org.gnome.gtk.LinkButton
import org.gnome.gtk.ScrolledWindow
import org.gnome.gtk.SelectionMode
import org.gnome.gtk.UriLauncher
import java.awt.Desktop
import java.net.URI

class StudentsView : ScrolledWindow {

    private val viewModel: StudentsViewModel;
    private val window: ApplicationWindow;
    constructor(window: ApplicationWindow, viewModel: StudentsViewModel) : super() {
        this.window = window
        val flowBox = FlowBox()
        this.viewModel = viewModel
        this.viewModel.students.registerHandler { _, _ -> renderStudentList(flowBox) }
        GlobalScope.launch {
            viewModel.init()
        }


        if (!viewModel.studentsLoaded) {
            flowBox.valign = Align.CENTER
            flowBox.halign = Align.CENTER
            flowBox.marginStart = 64
            flowBox.selectionMode = SelectionMode.NONE


            val button = Button()
            button.label = "Import class..."
            button.cssClasses = arrayOf("pill", "accent")

            button.onClicked {
                GlobalScope.launch {
                    viewModel.importClass()
                }
            }

            flowBox.append(button)
        } else {
            renderStudentList(flowBox)
        }
        child = flowBox
    }

    private fun renderStudentList(flowBox: FlowBox) {
        val listBox = PreferencesGroup()
        listBox.setSizeRequest(550, 64)
        for (student in viewModel.students.value) {
            val listRow = ExpanderRow()
            listRow.title = student.fullName
            listRow.subtitle = student.gitlabUsername

            val subRowGitlabLink = ActionRow()
            subRowGitlabLink.title = "Gitlab Project URL"
            subRowGitlabLink.useMarkup = false
            subRowGitlabLink.subtitle = student.projectUrl.toString().substring(0..60) + "..."
            val button = Button()
            button.label = "Open...";
            button.setSizeRequest(30, 3)
            button.onClicked {
                GlobalScope.launch {
                    UriLauncher(student.projectUrl.toString()).launch(window, Cancellable(), null)
                }
            }
            subRowGitlabLink.addSuffix(button)
            listRow.addRow(subRowGitlabLink)

            listBox.add(listRow)

        }
        flowBox.removeAll()
        flowBox.append(listBox)
    }
}