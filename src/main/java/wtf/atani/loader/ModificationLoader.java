package wtf.atani.loader;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import org.lwjgl.opengl.Display;
import wtf.atani.account.storage.AccountStorage;
import wtf.atani.combat.CombatManager;
import wtf.atani.command.storage.CommandStorage;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.file.storage.FileStorage;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.processor.storage.ProcessorStorage;
import wtf.atani.security.checks.manager.ProtectionManager;
import wtf.atani.utils.interfaces.ClientInformationAccess;
import wtf.atani.value.storage.ValueStorage;

public class ModificationLoader implements ClientInformationAccess {

    private final long startTime = System.currentTimeMillis();
    private boolean beta = true;
    private String username = "TempUser";

    // Main Methods
    public void start() {
        setTitle();
        setupManagers();
        loadDiscordRPC();
        addShutdownHook();
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
        new ProtectionManager();
        new FontStorage();
        new ProcessorStorage();
        new ValueStorage();
        new CombatManager();
        new ModuleStorage();
        new CommandStorage();
        new AccountStorage();
        new FileStorage();
    }

    private void loadDiscordRPC() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
        }).build();
        DiscordRPC.discordInitialize("1141582938127998988", handlers, true);
        final DiscordRichPresence rich = new DiscordRichPresence.Builder(username)
                .setBigImage("icon", CLIENT_NAME + " - " + VERSION)
                .setDetails((beta ? "Bete" : "Free") + " Version")
                .setStartTimestamps(startTime)
                .build();
        DiscordRPC.discordUpdatePresence(rich);

    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::end));
    }

    // End
    private void save(){
        FileStorage.getInstance().save();
    }
}
