package tech.atani.client.utility.player;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.*;
import tech.atani.client.utility.interfaces.ClientInformationAccess;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.VecUtil;
import tech.atani.client.utility.player.rotation.RotationUtil;

public class PlayerUtil implements Methods {

    public static boolean canBuildForward() {
        final float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        return (yaw > 77.5 && yaw < 102.5)
                || (yaw > 167.5 || yaw < -167.0f)
                || (yaw < -77.5 && yaw > -102.5)
                || (yaw > -12.5 && yaw < 12.5);
    }

    public static int getLeatherArmorColor(EntityPlayer player) {
        int armorColor = -1;
        for (ItemStack itemStack : player.inventory.armorInventory) {
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                    // The color of leather armor is stored in the item's display tag
                    if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("display", 10)) {
                        armorColor = itemStack.getTagCompound().getCompoundTag("display").getInteger("color");
                        break;
                    }
                }
            }
        }
        return armorColor;
    }


    public static int getIndexOfItem() {
        final InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
        return inventoryPlayer.currentItem;
    }

    public static ItemStack getItemStack() {
        return (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null ? null : mc.thePlayer.inventoryContainer.getSlot(getIndexOfItem() + 36).getStack());
    }
}
