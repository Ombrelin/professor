package fr.arsenelapostolet.professor.views

import fr.arsenelapostolet.professor.viewmodels.GitToolsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gnome.adw.ActionRow
import org.gnome.adw.ExpanderRow
import org.gnome.adw.PreferencesGroup
import org.gnome.gio.Cancellable
import org.gnome.gio.Icon
import org.gnome.gtk.Align
import org.gnome.gtk.Button
import org.gnome.gtk.FlowBox
import org.gnome.gtk.Image
import org.gnome.gtk.Label
import org.gnome.gtk.ScrolledWindow
import org.gnome.gtk.SelectionMode
import org.gnome.gtk.UriLauncher

class GitToolsView(private val viewModel: GitToolsViewModel) : ScrolledWindow() {

    private val mainFlowBox = buildFlowBox()

    init {
        onShow {
            GlobalScope.launch {
                viewModel.init()
            }
        }

        child = mainFlowBox

        val listRow = ExpanderRow()
        listRow.title = "Token Gitlab"
        listRow.subtitle = "Gestion du token d'authentification Gitlab"
        listRow.addPrefix(Image.fromGicon(Icon.newForString("avatar-default-symbolic")))

        val listBox = createControlsListBox()

        val subrowGitlabTokenAvailability = ActionRow()
        subrowGitlabTokenAvailability.title = "Token Gitlab"
        subrowGitlabTokenAvailability.subtitle = "Disponibilité du token Gitlab"
        subrowGitlabTokenAvailability.useMarkup = false
        subrowGitlabTokenAvailability.activatable = false
        val labelAvailability = Label(viewModel.gitlabTokenAvailable.value.toString())
        subrowGitlabTokenAvailability.addSuffix(labelAvailability)

        val subrowSyncButton = ActionRow()
        subrowSyncButton.title = "Mettre à jours"
        subrowSyncButton.subtitle = "Mettre à jours le token d'authentification Gitlab"
        subrowSyncButton.useMarkup = false
        subrowSyncButton.activatable = true
        val GitlabPageImage = Image.fromGicon(Icon.newForString("adw-external-link-symbolic"))
        subrowSyncButton.addSuffix(GitlabPageImage)
        subrowSyncButton.onActivated {
            GlobalScope.launch {
                viewModel.init()
            }
        }
        //val button = Button()
        //button.label = "Changer le token"
        //button.cssClasses = arrayOf("pill", "accent")
        //subrowSyncButton.addSuffix(button)



        listRow.addRow(subrowGitlabTokenAvailability)
        listRow.addRow(subrowSyncButton)

        listBox.add(listRow)


        mainFlowBox.append(listBox)

        viewModel.gitlabTokenAvailable.registerHandler { old, new ->  labelAvailability.text = new.toString() }
    }

    private fun buildFlowBox(): FlowBox {
        val flowBox = FlowBox()
        flowBox.valign = Align.CENTER
        flowBox.halign = Align.CENTER
        flowBox.marginStart = 64
        flowBox.selectionMode = SelectionMode.NONE
        return flowBox
    }

    private fun createControlsListBox(): PreferencesGroup {
        val listBox = PreferencesGroup()
        listBox.setSizeRequest(550, 64)
        return listBox
    }
}