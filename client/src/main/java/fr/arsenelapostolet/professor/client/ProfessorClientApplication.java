package fr.arsenelapostolet.professor.client;

import org.gnome.gio.ApplicationFlags;
import org.gnome.gtk.*;

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
        window.setDefaultSize(300, 200);

        var box = Box.builder()
                .setOrientation(Orientation.VERTICAL)
                .setHalign(Align.CENTER)
                .setValign(Align.CENTER)
                .build();

        var button = Button.withLabel("Hello world!");
        button.onClicked(window::close);

        box.append(button);
        window.setChild(box);
        window.present();
    }
}
