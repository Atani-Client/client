package tech.atani.client.feature.module.impl.option;

import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import tech.atani.client.listener.event.Event;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@ModuleData(name = "Security", description = "Patches common exploits", category = Category.OPTIONS, frozenState = true, enabled = true)
public class Security extends Module {
    public final CheckBoxValue antiResourcePackExploit = new CheckBoxValue("Anti Resource Pack Exploit", "Prevent servers using the resource pack exploit to scan your files?", this, true);

    @Listen
    public void onUpdate(PacketEvent event) {
    	if(mc.thePlayer == null || mc.theWorld == null)
    		return;
        if(antiResourcePackExploit.getValue()) {
            if (event.getType() == PacketEvent.Type.INCOMING) {
                if(event.getPacket() instanceof S48PacketResourcePackSend) {
                    final S48PacketResourcePackSend s48 = (S48PacketResourcePackSend) event.getPacket();
                    String url = s48.getURL(), hash = s48.getHash();
                    try {
                        URI uri = new URI(url);
                        String scheme = uri.getScheme();
                        boolean isLevelProtocol = "level".equals(scheme);
                        if (!"http".equals(scheme) && !"https".equals(scheme) && !isLevelProtocol) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                            event.setCancelled(true);
                        }
                        url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());
                        if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                            sendMessage("SERVER TRIED TO ACCESS YOUR FILE USING EXPLOITS: " + uri);
                            event.setCancelled(true);
                        }
                    } catch (URISyntaxException | UnsupportedEncodingException ex) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}