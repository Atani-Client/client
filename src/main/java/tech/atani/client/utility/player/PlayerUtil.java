package tech.atani.client.utility.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.MathHelper;
import tech.atani.client.utility.interfaces.Methods;

public class PlayerUtil implements Methods {

    public static double getDistance(double x, double y, double z) {
        double d0 = mc.thePlayer.posX - x;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double d2 = mc.thePlayer.posZ - z;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static boolean canBuildForward() {
        final float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        return (yaw > 77.5 && yaw < 102.5)
                || (yaw > 167.5 || yaw < -167.0f)
                || (yaw < -77.5 && yaw > -102.5)
                || (yaw > -12.5 && yaw < 12.5);
    }

    public static boolean isFriendlyFireAllowed(EntityPlayer player) {
        Team team = getPlayerTeam(player);
        if (team != null) {
            Team.EnumVisible nameTagVisibility = team.getNameTagVisibility();
            return nameTagVisibility != Team.EnumVisible.HIDE_FOR_OWN_TEAM &&
                    nameTagVisibility != Team.EnumVisible.HIDE_FOR_OTHER_TEAMS;
        }
        return true;
    }

    public static Team getPlayerTeam(EntityPlayer player) {
        ScorePlayerTeam playerTeam = mc.theWorld.getScoreboard().getPlayersTeam(player.getCommandSenderName());
        if (playerTeam != null) {
            return mc.theWorld.getScoreboard().getTeam(playerTeam.getTeamName());
        }
        return null;
    }

    public static boolean hasLeatherArmor(EntityPlayer player) {
        for (ItemStack itemStack : player.inventory.armorInventory) {
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                    return true;
                }
            }
        }
        return false;
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
}
