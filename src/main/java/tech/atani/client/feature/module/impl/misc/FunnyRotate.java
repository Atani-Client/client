package tech.atani.client.feature.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.math.time.TimeHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleData(name = "FunnyRotate", description = "Rotates Funny", category = Category.MISCELLANEOUS)
public class FunnyRotate extends Module {
    private float hehe;
    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        rotationEvent.setYaw((float) (rotationEvent.getYaw() + Math.random() * 360));
    }
    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}