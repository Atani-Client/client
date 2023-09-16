package tech.atani.utils.player;

import tech.atani.utils.interfaces.IMethods;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PacketUtil implements IMethods {

    public static void sendBlocking(boolean callEvent, boolean place) {
        C08PacketPlayerBlockPlacement packet = place ?
                new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.player.getHeldItem(), 0, 0, 0) :
                new C08PacketPlayerBlockPlacement(mc.player.getHeldItem());

        if (callEvent)
            mc.player.connection.send(packet);
        else mc.player.connection.sendSilent(packet);
    }

    public static void releaseUseItem(boolean callEvent) {
        C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

        if (callEvent)
            mc.player.connection.send(packet);
        else mc.player.connection.sendSilent(packet);
    }

}
