package wtf.atani.utils.player.rayTrace;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;
import wtf.atani.utils.interfaces.Methods;

import java.util.List;

public class RaytraceUtil implements Methods {

	public static MovingObjectPosition rayCast(final float partialTicks) {
		MovingObjectPosition objectMouseOver = null;
		final Entity entity = mc.getRenderViewEntity();
		if (entity != null && mc.theWorld != null) {
			mc.mcProfiler.startSection("pick");
			mc.pointedEntity = null;
			double d0 = mc.playerController.getBlockReachDistance();
			objectMouseOver = entity.rayTrace(d0, partialTicks);
			double d2 = d0;
			final Vec3 vec3 = entity.getPositionEyes(partialTicks);
			boolean flag = false;
			final boolean flag2 = true;
			if (mc.playerController.extendedReach()) {
				d0 = 6.0;
				d2 = 6.0;
			}
			else {
				if (d0 > 3.0) {
					flag = true;
				}
				d0 = d0;
			}
			if (objectMouseOver != null) {
				d2 = objectMouseOver.hitVec.distanceTo(vec3);
			}
			final Vec3 vec4 = entity.getLook(partialTicks);
			final Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
			Entity pointedEntity = null;
			Vec3 vec6 = null;
			final float f = 1.0f;
			final List list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING));
			double d3 = d2;
			final AxisAlignedBB realBB = null;
			for (int i = 0; i < list.size(); ++i) {
				final Entity entity2 = (Entity) list.get(i);
				final float f2 = entity2.getCollisionBorderSize();
				final AxisAlignedBB axisalignedbb = entity2.getEntityBoundingBox().expand(f2, f2, f2);
				final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec5);
				if (axisalignedbb.isVecInside(vec3)) {
					if (d3 >= 0.0) {
						pointedEntity = entity2;
						vec6 = ((movingobjectposition == null) ? vec3 : movingobjectposition.hitVec);
						d3 = 0.0;
					}
				}
				else if (movingobjectposition != null) {
					final double d4 = vec3.distanceTo(movingobjectposition.hitVec);
					if (d4 < d3 || d3 == 0.0) {
						boolean flag3 = false;
						if (Reflector.ForgeEntity_canRiderInteract.exists()) {
							flag3 = Reflector.callBoolean(entity2, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
						}
						if (entity2 == entity.ridingEntity && !flag3) {
							if (d3 == 0.0) {
								pointedEntity = entity2;
								vec6 = movingobjectposition.hitVec;
							}
						}
						else {
							pointedEntity = entity2;
							vec6 = movingobjectposition.hitVec;
							d3 = d4;
						}
					}
				}
			}
			if (pointedEntity != null && flag && vec3.distanceTo(vec6) > 3.0) {
				pointedEntity = null;
				objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec6, null, new BlockPos(vec6));
			}
			if (pointedEntity != null && (d3 < d2 || objectMouseOver == null)) {
				objectMouseOver = new MovingObjectPosition(pointedEntity, vec6);
				if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
					pointedEntity = pointedEntity;
				}
			}
		}
		return objectMouseOver;
	}

	public static MovingObjectPosition rayCast(final float partialTicks, final float[] rots) {
		MovingObjectPosition objectMouseOver = null;
		final Entity entity = mc.getRenderViewEntity();
		if (entity != null && mc.theWorld != null) {
			mc.mcProfiler.startSection("pick");
			mc.pointedEntity = null;
			double d0 = mc.playerController.getBlockReachDistance();
			objectMouseOver = entity.customRayTrace(d0, partialTicks, rots[0], rots[1]);
			double d2 = d0;
			final Vec3 vec3 = entity.getPositionEyes(partialTicks);
			boolean flag = false;
			final boolean flag2 = true;
			if (mc.playerController.extendedReach()) {
				d0 = 6.0;
				d2 = 6.0;
			}
			else {
				if (d0 > 3.0) {
					flag = true;
				}
				d0 = d0;
			}
			if (objectMouseOver != null) {
				d2 = objectMouseOver.hitVec.distanceTo(vec3);
			}
			final Vec3 vec4 = entity.getCustomLook(partialTicks, rots[0], rots[1]);
			final Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
			Entity pointedEntity = null;
			Vec3 vec6 = null;
			final float f = 1.0f;
			final List list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING));
			double d3 = d2;
			final AxisAlignedBB realBB = null;
			for (int i = 0; i < list.size(); ++i) {
				final Entity entity2 = (Entity) list.get(i);
				final float f2 = entity2.getCollisionBorderSize();
				final AxisAlignedBB axisalignedbb = entity2.getEntityBoundingBox().expand(f2, f2, f2);
				final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec5);
				if (axisalignedbb.isVecInside(vec3)) {
					if (d3 >= 0.0) {
						pointedEntity = entity2;
						vec6 = ((movingobjectposition == null) ? vec3 : movingobjectposition.hitVec);
						d3 = 0.0;
					}
				}
				else if (movingobjectposition != null) {
					final double d4 = vec3.distanceTo(movingobjectposition.hitVec);
					if (d4 < d3 || d3 == 0.0) {
						boolean flag3 = false;
						if (Reflector.ForgeEntity_canRiderInteract.exists()) {
							flag3 = Reflector.callBoolean(entity2, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
						}
						if (entity2 == entity.ridingEntity && !flag3) {
							if (d3 == 0.0) {
								pointedEntity = entity2;
								vec6 = movingobjectposition.hitVec;
							}
						}
						else {
							pointedEntity = entity2;
							vec6 = movingobjectposition.hitVec;
							d3 = d4;
						}
					}
				}
			}
			if (pointedEntity != null && flag && vec3.distanceTo(vec6) > 3.0) {
				pointedEntity = null;
				objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec6, null, new BlockPos(vec6));
			}
			if (pointedEntity != null && (d3 < d2 || objectMouseOver == null)) {
				objectMouseOver = new MovingObjectPosition(pointedEntity, vec6);
				if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
					pointedEntity = pointedEntity;
				}
			}
		}
		return objectMouseOver;
	}

	public static MovingObjectPosition rayCast(final float partialTicks, final float[] rots, final double range, final double hitBoxExpand) {
		MovingObjectPosition objectMouseOver = null;
		final Entity entity = mc.getRenderViewEntity();
		if (entity != null && mc.theWorld != null) {
			mc.mcProfiler.startSection("pick");
			mc.pointedEntity = null;
			double d0 = range;
			objectMouseOver = entity.customRayTrace(d0, partialTicks, rots[0], rots[1]);
			double d2 = d0;
			final Vec3 vec3 = entity.getPositionEyes(partialTicks);
			boolean flag = false;
			final boolean flag2 = true;
			if (mc.playerController.extendedReach()) {
				d0 = 6.0;
				d2 = 6.0;
			}
			else {
				if (d0 > 3.0) {
					flag = true;
				}
				d0 = d0;
			}
			if (objectMouseOver != null) {
				d2 = objectMouseOver.hitVec.distanceTo(vec3);
			}
			final Vec3 vec4 = entity.getCustomLook(partialTicks, rots[0], rots[1]);
			final Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
			Entity pointedEntity = null;
			Vec3 vec6 = null;
			final float f = 1.0f;
			final List list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING));
			double d3 = d2;
			final AxisAlignedBB realBB = null;
			for (int i = 0; i < list.size(); ++i) {
				final Entity entity2 = (Entity) list.get(i);
				final float f2 = (float)(entity2.getCollisionBorderSize() + hitBoxExpand);
				final AxisAlignedBB axisalignedbb = entity2.getEntityBoundingBox().expand(f2, f2, f2);
				final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec5);
				if (axisalignedbb.isVecInside(vec3)) {
					if (d3 >= 0.0) {
						pointedEntity = entity2;
						vec6 = ((movingobjectposition == null) ? vec3 : movingobjectposition.hitVec);
						d3 = 0.0;
					}
				}
				else if (movingobjectposition != null) {
					final double d4 = vec3.distanceTo(movingobjectposition.hitVec);
					if (d4 < d3 || d3 == 0.0) {
						boolean flag3 = false;
						if (Reflector.ForgeEntity_canRiderInteract.exists()) {
							flag3 = Reflector.callBoolean(entity2, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
						}
						if (entity2 == entity.ridingEntity && !flag3) {
							if (d3 == 0.0) {
								pointedEntity = entity2;
								vec6 = movingobjectposition.hitVec;
							}
						}
						else {
							pointedEntity = entity2;
							vec6 = movingobjectposition.hitVec;
							d3 = d4;
						}
					}
				}
			}
			if (pointedEntity != null && flag && vec3.distanceTo(vec6) > range) {
				pointedEntity = null;
				objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec6, null, new BlockPos(vec6));
			}
			if (pointedEntity != null && (d3 < d2 || objectMouseOver == null)) {
				objectMouseOver = new MovingObjectPosition(pointedEntity, vec6);
				if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
					pointedEntity = pointedEntity;
				}
			}
		}
		return objectMouseOver;
	}

}
