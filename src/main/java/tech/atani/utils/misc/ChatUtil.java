package tech.atani.utils.misc;

import net.minecraft.util.Formatting;
import tech.atani.Client;
import tech.atani.utils.interfaces.IMethods;
import net.minecraft.util.ChatComponentText;

public class ChatUtil implements IMethods {
    public static void display(final Object message) {
        if (mc.player != null) {
            mc.player.addChatMessage(new ChatComponentText(getPrefix() + message));
        }
    }

    private static String getPrefix() {
        final String color = Formatting.LIGHT_PURPLE.toString();
        return Formatting.BOLD + color + Client.NAME
                + Formatting.RESET + color + " "
                + Formatting.RESET;
    }
}
