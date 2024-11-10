package fr.arsenelapostolet.professor.views

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
        flowBox.valign = Align.CENTER
        flowBox.halign = Align.CENTER
        flowBox.marginStart = 64
        flowBox.selectionMode = SelectionMode.NONE

        //val spinner = Spinner()
        //spinner.setSizeRequest(128, 128)
        //flowBox.append(spinner)

        child = flowBox

        (this.child as Viewport).scrollToFocus = false
    }

    private fun renderStudentList(flowBox: FlowBox) {
        flowBox.removeAll()
        val spinner = Spinner()
        spinner.setSizeRequest(128, 128)
        flowBox.append(spinner)

        if (!viewModel.studentsLoaded) {
            val button = Button()
            button.label = "Import class..."
            button.cssClasses = arrayOf("pill", "accent")
            button.setSizeRequest(300, 32)
            button.onClicked {
                GlobalScope.launch {
                    viewModel.importClass()
                }
            }
            flowBox.removeAll()
            flowBox.append(button)
        } else {
            val listBox = PreferencesGroup()
            listBox.setSizeRequest(550, 64)
            for (student in viewModel.students.value) {
                val listRow = ExpanderRow()
                listRow.title = student.fullName
                listRow.subtitle = student.gitlabUsername
                listRow.addPrefix(Image.fromGicon(Icon.newForString("avatar-default-symbolic")))

                val subRowGitlabLink = ActionRow()
                subRowGitlabLink.title = "Lien du projet Gitlab"
                subRowGitlabLink.useMarkup = false
                subRowGitlabLink.subtitle = student.projectUrl.toString().substring(0..60) + "..."
                val imageGitlabLink = Image.fromGicon(Icon.newForString("adw-external-link-symbolic"))
                subRowGitlabLink.activatable = true
                subRowGitlabLink.onActivated {
                    println("test")
                    GlobalScope.launch {
                        UriLauncher(student.projectUrl.toString()).launch(window, Cancellable(), null)
                    }
                }
                subRowGitlabLink.addSuffix(imageGitlabLink)

                val subRowMailLink = ActionRow()
                subRowMailLink.title = "Adresse Email"
                subRowMailLink.useMarkup = false
                subRowMailLink.subtitle = student.email
                val mailLinkImage = Image.fromGicon(Icon.newForString("mail-unread-symbolic"))
                subRowMailLink.activatable = true
                subRowMailLink.onActivated {
                    println("test")
                    GlobalScope.launch {
                        UriLauncher("mailto:${student.email}").launch(window, Cancellable(), null)
                    }
                }
                subRowMailLink.addSuffix(mailLinkImage)

                val subrowGitlabPage = ActionRow()
                subrowGitlabPage.title = "Profil Gitlab"
                subrowGitlabPage.useMarkup = false
                subrowGitlabPage.subtitle = student.gitlabUsername
                subrowGitlabPage.activatable = true
                val GitlabPageImage = Image.fromGicon(Icon.newForString("adw-external-link-symbolic"))
                subrowGitlabPage.onActivated {
                    println("test")
                    GlobalScope.launch {
                        UriLauncher("https://gitlab.com/${student.gitlabUsername}").launch(window, Cancellable(), null)
                    }
                }
                subrowGitlabPage.addSuffix(GitlabPageImage)

                listRow.addRow(subRowGitlabLink)
                listRow.addRow(subrowGitlabPage)
                listRow.addRow(subRowMailLink)


                listBox.add(listRow)

            }
            flowBox.removeAll()
            flowBox.append(listBox)
        }
    }
}