package tech.atani.client.feature.module.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.player.movement.MovePlayerEvent;
import tech.atani.client.listener.event.minecraft.player.movement.SafeWalkEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;

@ModuleData(name = "SafeWalk", description = "Prevents you from falling off edges", category = Category.MOVEMENT)
public class SafeWalk extends Module {

    @Listen
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;

        event.setSafe(true);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
