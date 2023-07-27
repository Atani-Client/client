package wtf.atani.loader;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.lwjgl.opengl.Display;
import wtf.atani.command.storage.CommandStorage;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.file.storage.FileStorage;
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

        MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult microsoftAuthResult = microsoftAuthenticator.loginWithCredentials("legendarysomeone92@gmail.com", "V[HVNHVt]y^)upB{pvj#6p]r*5@z,yt6MjOgQEZFRPxdmVM,yf");
            Minecraft.getMinecraft().session = new Session(microsoftAuthResult.getProfile().getName(), microsoftAuthResult.getProfile().getId(), microsoftAuthResult.getAccessToken(), "mojang");
        } catch (MicrosoftAuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    public void end() {
        save();
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
        new CommandStorage();
        new FileStorage();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::end));
    }

    // End
    private void save(){
        FileStorage.getInstance().save();
    }
}
