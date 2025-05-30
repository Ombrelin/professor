package fr.arsenelapostolet.professor.views

import fr.arsenelapostolet.professor.viewmodels.GitToolsViewModel
import fr.arsenelapostolet.professor.views.wigets.BigButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gnome.adw.ActionRow
import org.gnome.adw.ExpanderRow
import org.gnome.adw.PreferencesGroup
import org.gnome.gio.Icon
import org.gnome.gtk.*

@OptIn(DelicateCoroutinesApi::class)
class GitToolsView(private val viewModel: GitToolsViewModel) : View {

    private val mainScrollWindow = ScrolledWindow()
    private val mainFlowBox = buildFlowBox()

    init {

        GlobalScope.launch {
            viewModel.init()
        }

        mainScrollWindow.child = mainFlowBox

        val listRow = ExpanderRow()
        listRow.title = "Token Gitlab"
        listRow.subtitle = "Gestion du token d'authentification Gitlab"
        listRow.addPrefix(Image.fromResource("/icons/key-symbolic"))
        val listBox = createControlsListBox()

        val subrowGitlabTokenAvailability = ActionRow
            .builder()
            .setTitle("Token Gitlab")
            .setSubtitle("Disponibilité du token Gitlab")
            .setUseMarkup(false)
            .setActivatable(false)
            .build()
        val labelAvailability = Label(viewModel.gitlabTokenAvailable.value.toString())
        subrowGitlabTokenAvailability.addSuffix(labelAvailability)

        val subrowSyncButton = ActionRow.builder()
            .setTitle("Mettre à jour")
            .setSubtitle("Mettre à jours le token d'authentification Gitlab")
            .setUseMarkup(false)
            .setActivatable(true)
            .build()

        val gitlabPageImage = Image.fromGicon(Icon.newForString("document-edit-symbolic"))

        subrowSyncButton.addSuffix(gitlabPageImage)
        subrowSyncButton.onActivated {
            GlobalScope.launch {
                viewModel.updateGitlabToken()
            }
        }

        listRow.addRow(subrowGitlabTokenAvailability)
        listRow.addRow(subrowSyncButton)

        val listRowGitSynchronization = ExpanderRow()
        listRowGitSynchronization.title = "Synchronisation des dépôts locaux"
        listRowGitSynchronization.subtitle = "Synchronise les dépôts"
        listRowGitSynchronization.addPrefix(Image.fromResource("/icons/key-symbolic"))

        listBox.add(listRow)


        mainFlowBox.append(listBox)

        mainFlowBox.append(buildSynchronizeButton())
        mainFlowBox.append(buildSynchronizeGradesButton())


        viewModel.gitlabTokenAvailable.registerHandler { old, new -> labelAvailability.text = new.toString() }
    }

    private fun buildSynchronizeButton(): BigButton {
        val button = BigButton("Synchronizer les dépôts")
        button.onClicked {
            GlobalScope.launch {
                val progressBar = ProgressBar()
                mainFlowBox.append(progressBar)
                mainFlowBox.remove(button)

                viewModel.refreshProgress.registerHandler {
                        old, new -> progressBar.fraction = new
                }

                viewModel.syncLocalGitRepositories()
                mainFlowBox.remove(progressBar)
                mainFlowBox.append(button)


            }
        }
        return button;
    }

    private fun buildSynchronizeGradesButton(): BigButton {
        val button = BigButton("Synchronizer les notes")
        button.onClicked {
            GlobalScope.launch {
                val spinner = Spinner()
                mainFlowBox.append(spinner)
                mainFlowBox.remove(button)

                viewModel.syncGrades()
                mainFlowBox.remove(spinner)
                mainFlowBox.append(button)


            }
        }
        return button;
    }

    private fun buildFlowBox(): FlowBox {
        val flowBox = FlowBox()
        flowBox.valign = Align.CENTER
        flowBox.halign = Align.CENTER
        flowBox.marginStart = 64
        flowBox.selectionMode = SelectionMode.NONE
        flowBox.maxChildrenPerLine = 1
        flowBox.rowSpacing = 8
        return flowBox
    }

    private fun createControlsListBox(): PreferencesGroup {
        val listBox = PreferencesGroup()
        listBox.setSizeRequest(550, 64)
        return listBox
    }

    override fun getWidget(): Widget = mainScrollWindow
}