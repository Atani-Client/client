package wtf.atani.module.impl.combat;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.Sys;
import wtf.atani.event.events.ClickingEvent;
import wtf.atani.event.events.RotationEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.PlayerHandler;

@ModuleInfo(name = "Extinguish", description = "Automatically extinguishes fire under you", category = Category.COMBAT)
public class Extinguish extends Module {

    public boolean changed;
    public int slotId;

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        if(mc.thePlayer.isBurning()) {
            for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {

                ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                if (itemStack != null && itemStack.getItem().getIdFromItem(itemStack.getItem()) == 326) {
                    if(!changed)
                        slotId = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = i;
                    changed = true;
                }
            }
        }
    }

    @Listen
    public void onRotate(RotationEvent rotationEvent) {
        if (mc.thePlayer.isBurning()) {
            if(mc.thePlayer.getHeldItem() == null) {
                return;
            }
            if (mc.thePlayer.getHeldItem().getItem().getIdFromItem(mc.thePlayer.getHeldItem().getItem()) == 326) {
                rotationEvent.setPitch(90);
            }
        }
    }

    @Listen
    public void onClicking(ClickingEvent clickingEvent) {
        if (mc.thePlayer.isBurning()) {
            if(mc.thePlayer.getHeldItem() == null) {
                return;
            }
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem().getIdFromItem(mc.thePlayer.getHeldItem().getItem()) == 326 && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
                if(mc.thePlayer == null || mc.theWorld == null)
                    return;

                MovingObjectPosition objectOver = mc.objectMouseOver;
                BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();

                if (objectOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
                    return;
                }


                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));

                //Reset to current hand.
                mc.thePlayer.inventory.currentItem = slotId;
                mc.playerController.updateController();

                if (itemstack != null && itemstack.stackSize == 0) {
                    mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
                }

                mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
            }
        }
        if(changed) {
            mc.thePlayer.inventory.currentItem = slotId;
            changed = false;
        }
    }

    @Override
    public void onEnable() {
        changed = false;
    }

    @Override
    public void onDisable() {

    }
}
