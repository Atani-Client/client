package wtf.atani.module.impl.combat;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "Extinguish", description = "Automatically extinguishes fire under you", category = Category.COMBAT)
public class Extinguish extends Module {

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        if (mc.thePlayer.isBurning()) {
            for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {

                ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                if (itemStack != null && itemStack.getItem().getIdFromItem(itemStack.getItem()) == 326) {
                    mc.thePlayer.inventory.currentItem = i;
                }
            }
            if(mc.thePlayer.getHeldItem() == null) {
                return;
            }
            if (mc.thePlayer.getHeldItem().getItem().getIdFromItem(mc.thePlayer.getHeldItem().getItem()) == 326) {
                float oldpitch = mc.thePlayer.rotationPitch;
                mc.thePlayer.rotationPitch = 90f;
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                mc.thePlayer.rotationPitch = oldpitch;
            }
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
