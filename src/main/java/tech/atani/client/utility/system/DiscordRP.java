package tech.atani.client.utility.system;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import tech.atani.client.menace.MenaceUser;
import tech.atani.client.utility.interfaces.ClientInformationAccess;

public class DiscordRP implements ClientInformationAccess {

    public static DiscordRP instance = new DiscordRP();

    private boolean running = true;
    private long created = 0;

    public void start() {
        this.created = System.currentTimeMillis();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(user -> update("Authenticating...")).build();

        DiscordRPC.discordInitialize("1141582938127998988", handlers, true);

        new Thread("Discord RPC Callback") {

            @Override
            public void run() {

                while (running) {
                    DiscordRPC.discordRunCallbacks();
                }

            }

        }.start();

    }

    public void stop() {
        this.running = false;
        DiscordRPC.discordShutdown();
    }

    public void update(String line) {
        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(line);
        b.setBigImage("icon", CLIENT_NAME + " - " + CLIENT_VERSION);
        if (MenaceUser.getInstance() != null) {
            b.setDetails(MenaceUser.getInstance().getUsername() + " [" + MenaceUser.getInstance().getUID() + "]");
        }
        b.setStartTimestamps(created);

        DiscordRPC.discordUpdatePresence(b.build());
    }

}
