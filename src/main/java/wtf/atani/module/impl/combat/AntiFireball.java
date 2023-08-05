package wtf.atani.module.impl.combat;

import com.sun.jna.platform.win32.WinUser;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MovingObjectPosition;
import wtf.atani.event.events.AttackEvent;
import wtf.atani.event.events.MoveFlyingEvent;
import wtf.atani.event.events.RotationEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.utils.player.RotationUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "AntiFireball", description = "Automatically deflects fireballs", category = Category.COMBAT)
public class AntiFireball extends Module {

    private StringBoxValue swingMode = new StringBoxValue("Swing Mode", "How will the module swing?", this, new String[]{"Normal", "Packet", "None"});
    private CheckBoxValue rotate = new CheckBoxValue("Rotate", "Rotate at fireballs?", this, true);
    public CheckBoxValue rayTrace = new CheckBoxValue("Ray Trace", "Ray Trace?",this, true);
    private CheckBoxValue stopMove = new CheckBoxValue("Stop Move", "Stop movement?", this, false);
    private SliderValue<Float> radius = new SliderValue<>("Radius", "At which radius will the module operate?", this, 3F, 0F, 6F, 1);

    private TimeHelper timeHelper = new TimeHelper();
    private boolean attacking = false;
    private EntityFireball entity;

    @Listen
    public void onAttack(AttackEvent event) {
        for (Object entityObj : mc.theWorld.loadedEntityList) {
            if (entityObj instanceof EntityFireball) {
                this.entity = (EntityFireball) entityObj;
                if (mc.thePlayer.getDistanceToEntity(entity) < radius.getValue() && timeHelper.hasReached(300) && (!rayTrace.getValue() || (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit.getEntityId() == entity.getEntityId()))) {
                    attacking = true;

                    if (swingMode.getValue().equals("Normal")) {
                        mc.thePlayer.swingItem();
                    } else if (swingMode.getValue().equals("Packet")) {
                        mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    }
                    mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));

                    timeHelper.reset();
                    break;
                }
            }
        }
        this.entity = null;
    }

    @Listen
    public void onRotate(RotationEvent rotationEvent) {
        if(this.entity != null) {
            float[] rotations = RotationUtil.getRotation(entity, true, false, 0, 0, 0, 0, false, 180, 180, 180, 180, false, false);
            rotationEvent.setYaw(rotations[0]);
            rotationEvent.setPitch(rotations[1]);
        }
    }

    @Listen
    public void onMoveFlying(MoveFlyingEvent moveFlyingEvent) {
        if(attacking && stopMove.getValue()) {
            attacking = false;
            moveFlyingEvent.setForward(0);
            moveFlyingEvent.setStrafe(0);
            moveFlyingEvent.setFriction(0);
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
