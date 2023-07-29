package wtf.atani.utils.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.player.RotationUtil;

import java.util.ArrayList;
import java.util.List;

public class FightUtil implements Methods {

    public static boolean canHit(double chance) {
        return Math.random() <= chance;
    }

    public static List<EntityLivingBase> getMultipleTargets(double range, boolean players, boolean animals, boolean walls, boolean mobs, boolean invis) {
        List<EntityLivingBase> list = new ArrayList<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase))
                continue;
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            if (entityLivingBase == mc.thePlayer ||
                    getRange(entityLivingBase) > range
                    || !entityLivingBase.canEntityBeSeen(mc.thePlayer) && !walls
                    || entityLivingBase.isDead
                    || entityLivingBase instanceof EntityArmorStand
                    || entityLivingBase instanceof EntityVillager
                    || entityLivingBase instanceof EntityAnimal && !animals
                    || entityLivingBase instanceof EntitySquid && !animals
                    || entityLivingBase instanceof EntityPlayer && !players
                    || entityLivingBase instanceof EntityMob && !mobs
                    || entityLivingBase instanceof EntitySlime && !mobs
                    || entityLivingBase.isInvisible() && !invis) continue;
            if (list.size() > 5)
                continue;
            list.add(entityLivingBase);
        }
        return list;
    }

    public static boolean isValid(EntityLivingBase entityLivingBase, double range, boolean invis, boolean players, boolean animals, boolean mobs) {
        return !(getRange(entityLivingBase) > range
                || entityLivingBase.isDead
                || entityLivingBase instanceof EntityArmorStand
                || entityLivingBase instanceof EntityVillager
                || entityLivingBase instanceof EntityPlayer && !players
                || entityLivingBase instanceof EntityAnimal && !animals
                || entityLivingBase instanceof EntityMob && !mobs
                || entityLivingBase.isInvisible() && !invis
                || entityLivingBase == mc.thePlayer);
    }

    public static double getRange(Entity entity) {
        if(mc.thePlayer == null)
            return 0;
        return mc.thePlayer.getPositionEyes(1.0f).distanceTo(RotationUtil.getBestVector(mc.thePlayer.getPositionEyes(1F),
                entity.getEntityBoundingBox()));
    }

    public static double getEffectiveHealth(EntityLivingBase entity) {
        return entity.getHealth() * (entity.getMaxHealth() / entity.getTotalArmorValue());
    }

}