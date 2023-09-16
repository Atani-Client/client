package tech.atani.client.loader;

import de.florianmichael.viamcp.ViaMCP;
import org.lwjgl.opengl.Display;
import tech.atani.client.feature.anticheat.check.CheckStorage;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.account.storage.AccountStorage;
import tech.atani.client.feature.combat.CombatManager;
import tech.atani.client.feature.command.storage.CommandStorage;
import tech.atani.client.feature.theme.storage.ThemeStorage;
import tech.atani.client.listener.handling.EventHandling;
import tech.atani.client.files.storage.FileStorage;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.processor.storage.ProcessorStorage;
import tech.atani.client.utility.interfaces.ClientInformationAccess;
import tech.atani.client.utility.java.ArrayUtils;
import tech.atani.client.utility.internet.NetUtils;
import tech.atani.client.feature.value.storage.ValueStorage;
import tech.atani.client.utility.discord.DiscordRP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum Modification implements ClientInformationAccess {
    INSTANCE;

    public static long initTime = System.currentTimeMillis();

    private boolean loaded = false;

    public void start() {
        String authors = "";
        for (String author : AUTHORS)
            authors += author + ", ";
        authors = authors.substring(0, authors.length() - 2);
        Display.setTitle(CLIENT_NAME + " v" + CLIENT_VERSION + " | Made by " + authors);

        EventHandling.setInstance(new EventHandling());
        FontStorage.setInstance(new FontStorage());
        ProcessorStorage.setInstance(new ProcessorStorage());
        ValueStorage.setInstance(new ValueStorage());
        CombatManager.setInstance(new CombatManager());
        ThemeStorage.setInstance(new ThemeStorage());
        ModuleStorage.setInstance(new ModuleStorage());
        CommandStorage.setInstance(new CommandStorage());
        AccountStorage.setInstance(new AccountStorage());
        FileStorage.setInstance(new FileStorage());
        CheckStorage.setInstance(new CheckStorage());

        FontStorage.getInstance().init();
        ProcessorStorage.getInstance().init();
        ValueStorage.getInstance().init();
        CombatManager.getInstance().init();
        ThemeStorage.getInstance().init();
        ModuleStorage.getInstance().init();
        CommandStorage.getInstance().init();
        AccountStorage.getInstance().init();
        FileStorage.getInstance().init();
        CheckStorage.getInstance().init();

        try {
            ViaMCP.create();

            ViaMCP.INSTANCE.initAsyncSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DiscordRP.startup();
        DiscordRP.create();

        String[] fonts = {
                "5 Line Oblique", "AMC AAA01", "ANSI Regular", "ANSI Shadow", "Alligator", "Alligator2","Alphabet", "Banner", "Banner3", "Bell",
                "Big Chief", "Big Money-nw", "Block", "Calvin S", "Catwalk", "Colossal", "DOS Rebel", "Delta Corps Priest 1", "Doh",  "Speed",
                "Small Keyboard", "Siant Relief", "Lean"
        };
        Map<String, String> parameters = new HashMap<>();
        parameters.put("style", ArrayUtils.getRandomItem(fonts));
        parameters.put("text", "ATANI");
        try {
            System.out.println(NetUtils.sendPostRequest("https://texttoascii.com/api/figlet", (HashMap<String, String>) parameters));
        } catch (IOException e) {
            // Don't stop the client please, I can't launch it.
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::end));

        loaded = true;
    }

    public void end() {
        FileStorage.getInstance().save();
        DiscordRP.end();
    }

    public boolean isLoaded() {
        return loaded;
    }
}
