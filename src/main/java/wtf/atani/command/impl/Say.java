package wtf.atani.command.impl;

import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.atani.command.Command;
import wtf.atani.command.data.CommandInfo;

@CommandInfo(name = "say", description = "Say stuff")
public class Say extends Command {
    @Override
    public boolean execute(String[] args) {
        if (args.length == 1) {
            String msg = args[0];
            getPlayer().sendQueue.addToSendQueue(new C01PacketChatMessage(msg));
            return true;
        } else if (args.length == 0) {
            sendHelp(this, "[Message]");
        } else {
            return false;
        }

        return true;
    }
}