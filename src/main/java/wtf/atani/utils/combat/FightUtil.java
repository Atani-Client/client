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
import wtf.atani.combat.CombatManager;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.player.RotationUtil;

import java.util.ArrayList;
import java.util.List;

public class FightUtil implements Methods {

    public static boolean canHit(double chance) {
        return Math.random() <= chance;
    }

    public static List<EntityLivingBase> getMultipleTargets(double minRange, double maxRange, boolean players, boolean animals, boolean walls, boolean mobs, boolean invis) {
        List<EntityLivingBase> list = new ArrayList<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase))
                continue;
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            if (entityLivingBase == mc.thePlayer ||
                    getRange(entityLivingBase) > maxRange
                    || getRange(entityLivingBase) < minRange
                    || !entityLivingBase.canEntityBeSeen(mc.thePlayer) && !walls
                    || entityLivingBase.isDead
                    || entityLivingBase instanceof EntityArmorStand
                    || entityLivingBase instanceof EntityVillager
                    || entityLivingBase instanceof EntityAnimal && !animals
                    || entityLivingBase instanceof EntitySquid && !animals
                    || entityLivingBase instanceof EntityPlayer && !players
                    || entityLivingBase instanceof EntityMob && !mobs
                    || entityLivingBase instanceof EntitySlime && !mobs
                    || CombatManager.getInstance().hasBot(entity)
                    || entityLivingBase.isInvisible() && !invis) continue;
            if (list.size() > 5)
                continue;
            list.add(entityLivingBase);
        }
        return list;
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
                    || CombatManager.getInstance().hasBot(entity)
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
                || mc.theWorld.getEntityByID(entityLivingBase.getEntityId()) != entityLivingBase
                || entityLivingBase == mc.thePlayer
                ||  entityLivingBase == null
                || entityLivingBase.getEntityId() == mc.thePlayer.getEntityId());
    }

    // For esp
    public static boolean isValidWithPlayer(Entity entity, boolean invis, boolean players, boolean animals, boolean mobs) {
        return !(entity.isDead
                || entity instanceof EntityArmorStand
                || entity instanceof EntityVillager
                || entity instanceof EntityPlayer && !players
                || entity instanceof EntityAnimal && !animals
                || entity instanceof EntityMob && !mobs
                || entity.isInvisible() && !invis
                || mc.theWorld.getEntityByID(entity.getEntityId()) != entity
                || entity == null );
    }

    public static boolean isValidWithPlayer(Entity entity, float range, boolean invis, boolean players, boolean animals, boolean mobs) {
        return !(entity.isDead
                || mc.thePlayer == entity && mc.gameSettings.thirdPersonView == 0
                || getRange(entity) > range
                || entity instanceof EntityArmorStand
                || entity instanceof EntityVillager
                || entity instanceof EntityPlayer && !players
                || entity instanceof EntityAnimal && !animals
                || entity instanceof EntityMob && !mobs
                || entity.isInvisible() && !invis
                || mc.theWorld.getEntityByID(entity.getEntityId()) != entity
                || entity == null );
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