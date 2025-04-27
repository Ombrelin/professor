package fr.arsenelapostolet.professor.views

import fr.arsenelapostolet.professor.core.entities.Student
import fr.arsenelapostolet.professor.viewmodels.StudentsViewModel
import fr.arsenelapostolet.professor.views.wigets.BigButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gnome.adw.*
import org.gnome.adw.ApplicationWindow
import org.gnome.adw.Spinner
import org.gnome.gio.Cancellable
import org.gnome.gio.Icon
import org.gnome.gtk.*

@OptIn(DelicateCoroutinesApi::class)
class StudentsView(
    private val window: ApplicationWindow,
    private val viewModel: StudentsViewModel,
) : View {

    private val mainScrollWindow = ScrolledWindow()
    private val mainFlowBox = buildFlowBox()

    init {
        this.viewModel.students.registerHandler { _, _ -> renderStudentList() }
        GlobalScope.launch {
            viewModel.init()
        }
        mainScrollWindow.child = mainFlowBox
        (mainScrollWindow.child as Viewport).scrollToFocus = false
    }

    override fun getWidget(): Widget = mainScrollWindow

    private fun buildFlowBox(): FlowBox = FlowBox
        .builder()
        .setValign(Align.CENTER)
        .setHalign(Align.CENTER)
        .setOrientation(Orientation.VERTICAL)
        .setMarginStart(64)
        .setSelectionMode(SelectionMode.NONE)
        .build()

    private fun renderStudentList() {
        mainFlowBox.removeAll()

        val importStudentButton = buildImportStudentButton()
        mainFlowBox.append(importStudentButton)

        val spinner = Spinner()
        spinner.setSizeRequest(128, 128)
        mainFlowBox.append(spinner)


        val studentsListBox = createStudentsListBox()

        for (student in viewModel.students.value) {
            buildStudentListBoxRow(student, studentsListBox)
        }
        mainFlowBox.append(studentsListBox)
        mainFlowBox.remove(spinner)
    }

    private fun createStudentsListBox(): PreferencesGroup {
        val listBox = PreferencesGroup()
        listBox.setSizeRequest(550, 64)
        return listBox
    }

    private fun buildStudentListBoxRow(
        student: Student,
        listBox: PreferencesGroup,
    ) {
        val listRow = ExpanderRow.builder()
            .setTitle(student.fullName)
            .setSubtitle(student.gitlabUsername)
            .build()
        listRow.addPrefix(Image.fromGicon(Icon.newForString("avatar-default-symbolic")))

        val subRowGitlabLink = buildLinkSubRow(
            student,
            "Lien du projet Gitlab",
            "adw-external-link-symbolic",
            student.projectUrl.toString(),
            if (student.projectUrl.toString().length > 60) student.projectUrl.toString()
                .substring(0..60) + "..." else student.projectUrl.toString()
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

    private fun buildLinkSubRow(
        student: Student,
        subRowTitle: String,
        icon: String,
        link: String,
        subtitle: String = "",
    ): ActionRow {
        val subrow = ActionRow.builder()
            .setTitle(subRowTitle)
            .setSubtitle(subtitle)
            .setUseMarkup(false)
            .setSubtitle(student.gitlabUsername)
            .setActivatable(true)
            .build()

        val gitlabPageImage = Image.fromGicon(Icon.newForString(icon))
        subrow.onActivated {
            GlobalScope.launch {
                UriLauncher(link).launch(window, Cancellable(), null)
            }
        }
        subrow.addSuffix(gitlabPageImage)
        return subrow
    }

    private fun buildImportStudentButton(): Button {
        return BigButton("Import class...") {
            GlobalScope.launch {
                viewModel.importClass()
            }
        }

        //val button = Button.builder()
        //    .setLabel("Import class...")
        //    .setCssClasses(arrayOf("pill", "accent"))
        //    .build()
//
        //button.setSizeRequest(300, 32)
        //button.onClicked {
        //    GlobalScope.launch {
        //        viewModel.importClass()
        //    }
        //}
//
        //return button
    }
}