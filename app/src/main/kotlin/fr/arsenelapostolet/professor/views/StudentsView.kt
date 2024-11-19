package fr.arsenelapostolet.professor.views

import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.viewmodels.StudentsViewModel
import kotlinx.coroutines.*
import org.gnome.adw.ActionRow
import org.gnome.adw.ApplicationWindow
import org.gnome.adw.ExpanderRow
import org.gnome.adw.PreferencesGroup
import org.gnome.adw.Spinner
import org.gnome.gio.Cancellable
import org.gnome.gio.Icon
import org.gnome.gtk.Align
import org.gnome.gtk.Button
import org.gnome.gtk.FlowBox
import org.gnome.gtk.Image
import org.gnome.gtk.ScrolledWindow
import org.gnome.gtk.SelectionMode
import org.gnome.gtk.UriLauncher
import org.gnome.gtk.Viewport

class StudentsView(private val window: ApplicationWindow, private val viewModel: StudentsViewModel) : ScrolledWindow() {

    private val mainFlowBox = buildFlowBox()

    init {
        this.viewModel.students.registerHandler { _, _ -> renderStudentList() }
        GlobalScope.launch {
            viewModel.init()
        }
        child = mainFlowBox
        (this.child as Viewport).scrollToFocus = false
    }

    private fun buildFlowBox(): FlowBox {
        val flowBox = FlowBox()
        flowBox.valign = Align.CENTER
        flowBox.halign = Align.CENTER
        flowBox.marginStart = 64
        flowBox.selectionMode = SelectionMode.NONE
        return flowBox
    }

    private fun renderStudentList() {
        mainFlowBox.removeAll()
        val spinner = Spinner()
        spinner.setSizeRequest(128, 128)
        mainFlowBox.append(spinner)

        if (!viewModel.studentsLoaded) {
            val importStudentButton = buildImportStudentButton()
            mainFlowBox.removeAll()
            mainFlowBox.append(importStudentButton)
        } else {
            val studentsListBox  = createStudentsListBox()

            for (student in viewModel.students.value) {
                buildStudentListBoxRow(student, studentsListBox)
            }
            mainFlowBox.removeAll()
            mainFlowBox.append(studentsListBox)
        }
    }

    private fun createStudentsListBox(): PreferencesGroup {
        val listBox = PreferencesGroup()
        listBox.setSizeRequest(550, 64)
        return listBox
    }

    private fun buildStudentListBoxRow(
        student: Student,
        listBox: PreferencesGroup
    ) {
        val listRow = ExpanderRow()
        listRow.title = student.fullName
        listRow.subtitle = student.gitlabUsername
        listRow.addPrefix(Image.fromGicon(Icon.newForString("avatar-default-symbolic")))

        val subRowGitlabLink = buildLinkSubRow(
            student,
            "Lien du projet Gitlab",
            "adw-external-link-symbolic",
            student.projectUrl.toString(),
            student.projectUrl.toString().substring(0..60) + "..."
        )
        val subRowMailLink = buildLinkSubRow(
            student,
            "Adresse Email",
            "mail-unread-symbolic",
            "mailto:${student.email}"
        )
        val subrowGitlabPage = buildLinkSubRow(
            student,
            "Profil Gitlab",
            "adw-external-link-symbolic",
            "https://gitlab.com/${student.gitlabUsername}"
        )

        listRow.addRow(subRowGitlabLink)
        listRow.addRow(subrowGitlabPage)
        listRow.addRow(subRowMailLink)

        listBox.add(listRow)
    }

    private fun buildLinkSubRow(student: Student, subRowTitle: String, icon: String, link: String, subtitle: String = ""): ActionRow {
        val subrow = ActionRow()
        subrow.title = subRowTitle
        subrow.subtitle = subtitle
        subrow.useMarkup = false
        subrow.subtitle = student.gitlabUsername
        subrow.activatable = true
        val GitlabPageImage = Image.fromGicon(Icon.newForString(icon))
        subrow.onActivated {
            GlobalScope.launch {
                UriLauncher(link).launch(window, Cancellable(), null)
            }
        }
        subrow.addSuffix(GitlabPageImage)
        return subrow
    }

    private fun buildImportStudentButton(): Button {
        val button = Button()
        button.label = "Import class..."
        button.cssClasses = arrayOf("pill", "accent")
        button.setSizeRequest(300, 32)
        button.onClicked {
            GlobalScope.launch {
                viewModel.importClass()
            }
        }
        return button
    }
}