package wtf.atani.loader;

import org.lwjgl.opengl.Display;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.interfaces.ClientInformationAccess;
import wtf.atani.value.storage.ValueStorage;

public class ModificationLoader implements ClientInformationAccess {

    // Main Methods
    public void start() {
        setTitle();
        setupManagers();
        addShutdownHook();
    }

    public void end() {

    }

    // Start
    private void setTitle() {
        String authors = "";
        for (String author : AUTHORS)
            authors += author + ", ";
        authors = authors.substring(0, authors.length() - 2);
        Display.setTitle(CLIENT_NAME + " v" + VERSION + " | Made by " + authors);
    }

    private void setupManagers() {
        new EventHandling();
        new FontStorage();
        new ValueStorage();
        new ModuleStorage();
    }

    // End
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::end));
    }

}
