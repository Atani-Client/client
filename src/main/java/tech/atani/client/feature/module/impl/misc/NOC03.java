package tech.atani.client.feature.module.impl.misc;

import cn.muyang.nativeobfuscator.Native;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;

import java.util.ArrayDeque;

@Native
@ModuleData(name = "NOC03", description = "Deletes C03 Packet", category = Category.MISCELLANEOUS, alwaysRegistered = true)
public class NOC03 extends Module {
    private boolean active;
    @Listen
    public void onPacket(PacketEvent event) {
        if(event.getPacket() instanceof C03PacketPlayer && active)
            event.setCancelled(true);
    }

    @Override
    public void onEnable() {
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
    }

}