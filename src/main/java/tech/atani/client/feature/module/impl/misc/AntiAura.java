package tech.atani.client.feature.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.math.time.TimeHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleData(name = "AntiAura", description = "Can cause enemies aura to break", category = Category.MISCELLANEOUS)
public class AntiAura extends Module {
    private final TimeHelper timeHelper = new TimeHelper();
    private final List<Packet<?>> packetsList = new CopyOnWriteArrayList<>();
    @Listen
    public void onPacket(PacketEvent event) {
        if(event.getPacket() instanceof C03PacketPlayer) {
            packetsList.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Listen
    public void onUpdate(UpdateEvent event) {
        if (timeHelper.hasReached(50, true)) {
            if (!packetsList.isEmpty()) {
                for (Packet<?> packet : packetsList) {
                    mc.getNetHandler().addToSendQueue(packet);
                    packetsList.remove(packet);
                }
            }
            timeHelper.reset();
        }
    }
    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}