package tech.atani.client.feature.module.impl.combat;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.listener.event.Event;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.game.RunTickEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.math.time.TimeHelper;
import tech.atani.client.feature.module.value.impl.CheckBoxValue;
import tech.atani.client.feature.module.value.impl.SliderValue;
import tech.atani.client.feature.module.value.impl.StringBoxValue;

import java.util.ArrayList;

@ModuleData(name = "Backtrack", description = "Delay entity packets to get higher reach", category = Category.COMBAT)
public class Backtrack extends Module {

    private final ArrayList<Packet<INetHandler>> packets = new ArrayList<>();

    public StringBoxValue packetMode = new StringBoxValue("Packets", "Which packets to cancel?", this, new String[]{"Select", "All"});
    private SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the packet delay?", this, 450L, 0L, 5000L, 0);
    public SliderValue<Float> maximumRange = new SliderValue<>("Maximum Range", "What'll be the maximum range?", this, 6f, 3f, 6f, 1);
    public CheckBoxValue onlyWhenNeeded = new CheckBoxValue("Only When Out of Reach", "Backtrack target only if it is out of reach?", this, true);

    private EntityLivingBase entity = null;
    private INetHandler packetListener = null;
    private WorldClient lastWorld;
    private final TimeHelper timeHelper = new TimeHelper();
    
    @Listen
    public final void onTick(RunTickEvent runTickEvent) {
        try {
            if (entity != null && getPlayer() != null && this.packetListener != null && getWorld() != null) {
                double d0 = (double) this.entity.realPosX / 32.0D;
                double d1 = (double) this.entity.realPosY / 32.0D;
                double d2 = (double) this.entity.realPosZ / 32.0D;
                double d3 = (double) this.entity.serverPosX / 32.0D;
                double d4 = (double) this.entity.serverPosY / 32.0D;
                double d5 = (double) this.entity.serverPosZ / 32.0D;
                AxisAlignedBB alignedBB = new AxisAlignedBB(d3 - (double) this.entity.width, d4, d5 - (double) this.entity.width, d3 + (double) this.entity.width, d4 + (double) this.entity.height, d5 + (double) this.entity.width);
                Vec3 positionEyes = getPlayer().getPositionEyes(getTimer().renderPartialTicks);
                double currentX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB.minX, alignedBB.maxX);
                double currentY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB.minY, alignedBB.maxY);
                double currentZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB.minZ, alignedBB.maxZ);
                AxisAlignedBB alignedBB2 = new AxisAlignedBB(d0 - (double) this.entity.width, d1, d2 - (double) this.entity.width, d0 + (double) this.entity.width, d1 + (double) this.entity.height, d2 + (double) this.entity.width);
                double realX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB2.minX, alignedBB2.maxX);
                double realY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB2.minY, alignedBB2.maxY);
                double realZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB2.minZ, alignedBB2.maxZ);
                double distance = this.maximumRange.getValue().floatValue();
                if (!this.getPlayer().canEntityBeSeen(this.entity)) {
                    distance = distance > 3 ? 3 : distance;
                }
                double bestX = MathHelper.clamp_double(positionEyes.xCoord, this.entity.getEntityBoundingBox().minX, this.entity.getEntityBoundingBox().maxX);
                double bestY = MathHelper.clamp_double(positionEyes.yCoord, this.entity.getEntityBoundingBox().minY, this.entity.getEntityBoundingBox().maxY);
                double bestZ = MathHelper.clamp_double(positionEyes.zCoord, this.entity.getEntityBoundingBox().minZ, this.entity.getEntityBoundingBox().maxZ);
                boolean b = false;
                if (positionEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 2.9 || (getPlayer().hurtTime < 8 && getPlayer().hurtTime > 1)) {
                    b = true;
                }
                if (!this.onlyWhenNeeded.getValue()) {
                    b = true;
                }
                if (!(b && positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) + 0.05) || !(getPlayer().getDistance(d0, d1, d2) < distance) || this.timeHelper.hasReached((long) this.delay.getValue())) {
                    this.resetPackets(this.packetListener);
                    this.timeHelper.reset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        if (packetEvent.getiNetHandler() != null && packetEvent.getiNetHandler() instanceof OldServerPinger) return;
        if (mc.theWorld != null) {
            if (packetEvent.getType() == PacketEvent.Type.INCOMING) {
                this.packetListener = packetEvent.getiNetHandler();
                synchronized (Backtrack.class) {
                    final Packet<?> p = packetEvent.getPacket();
                    if (p instanceof S14PacketEntity) {
                        S14PacketEntity packetEntity = (S14PacketEntity) p;
                        final Entity entity = getWorld().getEntityByID(packetEntity.getEntityId());
                        if (entity instanceof EntityLivingBase) {
                            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                            entityLivingBase.realPosX += packetEntity.func_149062_c();
                            entityLivingBase.realPosY += packetEntity.func_149061_d();
                            entityLivingBase.realPosZ += packetEntity.func_149064_e();
                        }
                    }
                    if (p instanceof S18PacketEntityTeleport) {
                        S18PacketEntityTeleport teleportPacket = (S18PacketEntityTeleport) p;
                        final Entity entity = getWorld().getEntityByID(teleportPacket.getEntityId());
                        if (entity instanceof EntityLivingBase) {
                            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                            entityLivingBase.realPosX = teleportPacket.getX();
                            entityLivingBase.realPosY = teleportPacket.getY();
                            entityLivingBase.realPosZ = teleportPacket.getZ();
                        }
                    }

                    this.entity = null;
                    try {
                        if (ModuleStorage.getInstance()
                                .getByClass(KillAura.class)
                                .isEnabled()) {
                            this.entity = KillAura.curEntity;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    if (this.entity == null) {
                        this.resetPackets(packetEvent.getiNetHandler());
                        return;
                    }
                    if (getWorld() != null && getPlayer() != null) {
                        if (this.lastWorld != getWorld()) {
                            resetPackets(packetEvent.getiNetHandler());
                            this.lastWorld = getWorld();
                            return;
                        }
                        this.addPackets(p, packetEvent);
                    }
                    this.lastWorld = getWorld();
                }
            }
        }
    }

    private void resetPackets(INetHandler netHandler) {
        if (this.packets.size() > 0) {
            synchronized (this.packets) {
                while (this.packets.size() != 0) {
                    try {
                        this.packets.get(0).processPacket(netHandler);
                    } catch (Exception ignored) {
                    }
                    this.packets.remove(this.packets.get(0));
                }

            }
        }
    }

    private void addPackets(Packet packet, Event eventReadPacket) {
        synchronized (this.packets) {
            if (this.blockPacket(packet)) {
                this.packets.add(packet);
                eventReadPacket.setCancelled(true);
            }
        }
    }

    private boolean blockPacket(Packet packet) {
        switch (this.packetMode.getValue()) {
            case "All":
                return true;
            default:
                if (packet instanceof S03PacketTimeUpdate) {
                    return true;
                } else if (packet instanceof S00PacketKeepAlive) {
                    return true;
                } else if (packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion) {
                    return true;
                } else {
                    return packet instanceof S32PacketConfirmTransaction || packet instanceof S14PacketEntity || packet instanceof S19PacketEntityStatus || packet instanceof S19PacketEntityHeadLook || packet instanceof S18PacketEntityTeleport || packet instanceof S0FPacketSpawnMob;
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
