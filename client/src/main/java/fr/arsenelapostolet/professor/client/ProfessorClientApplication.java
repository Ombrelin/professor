package fr.arsenelapostolet.professor.client;

import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.Application;
import org.gnome.adw.CenteringPolicy;
import org.gnome.adw.HeaderBar;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gtk.Button;
import org.gnome.gtk.Grid;

public class ProfessorClientApplication {

    public static void main(String[] args) {
        new ProfessorClientApplication(args);
    }

    private final Application app;

    public ProfessorClientApplication(String[] args) {
        app = new Application("my.example.HelloApp", ApplicationFlags.DEFAULT_FLAGS);
        app.onActivate(this::activate);
        app.run(args);
    }

    public void activate() {
        var window = new ApplicationWindow(app);
        window.setTitle("GTK from Java");
        window.setDefaultSize(1280, 720);

        var grid = new Grid();
        grid.setColumnSpacing(1);
        grid.setRowSpacing(1);

        var headerbar = new HeaderBar();
        headerbar.setHexpand(true);

        var newButton = Button.fromIconName("document-new-symbolic");
        headerbar.packStart(newButton);

        var openButton = Button.fromIconName("document-open-symbolic");
        openButton.setLabel("Open");
        headerbar.packStart(openButton);

        var saveButton = Button.fromIconName("document-save-symbolic");
        headerbar.packStart(saveButton);
        headerbar.setCenteringPolicy(CenteringPolicy.STRICT);
        grid.attach(headerbar, 0, 0, 4, 1);



        window.setContent(grid);
        window.present();
    }
}
