package tech.atani.client.feature.module.impl.movement;

import com.google.common.base.Supplier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.event.minecraft.player.movement.MovePlayerEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.event.minecraft.world.CollisionBoxesEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;
import tech.atani.client.utility.player.movement.MoveUtil;
import tech.atani.client.feature.module.value.impl.SliderValue;
import tech.atani.client.feature.module.value.impl.StringBoxValue;

@ModuleData(name = "Flight", description = "Makes you fly", category = Category.MOVEMENT)
public class Flight extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Vanilla", "Old NCP", "Collision", "Vulcan", "Grim", "Verus", "Test"});
    private final StringBoxValue vulcanMode = new StringBoxValue("Vulcan Mode", "Which mode will the vulcan mode use?", this, new String[]{"Normal", "Clip & Glide", "Glide", "Vanilla"}, new Supplier[]{() -> mode.is("Vulcan")});
    private final StringBoxValue grimMode = new StringBoxValue("Grim Mode", "Which mode will the grim mode use?", this, new String[]{"Explosion", "Boat"}, new Supplier[]{() -> mode.is("Grim")});
    private final StringBoxValue verusMode = new StringBoxValue("Verus Mode", "Which mode will the verus mode use?", this, new String[]{"Damage", "Jump"}, new Supplier[]{() -> mode.is("Verus")});
    private final SliderValue<Integer> time = new SliderValue<>("Time", "How long will the flight fly?", this, 10, 3, 15, 0, new Supplier[]{() -> mode.is("Vulcan") && vulcanMode.is("Normal")});
    private final SliderValue<Float> timer = new SliderValue<>("Timer", "How high will be the timer when flying?", this, 0.2f, 0.1f, 0.5f, 1, new Supplier[]{() -> mode.is("Vulcan") && vulcanMode.is("Normal")});
    private final SliderValue<Float> speed = new SliderValue<>("Speed", "How fast will the fly be?", this, 1.4f, 0f, 10f, 1, new Supplier[]{() -> mode.is("Vulcan") || mode.is("Vanilla")});

    // Old NCP
    private double moveSpeed;
    private boolean jumped;

    // Vulcan
    private double startY;
    private int stage;
    public int jumps;
    private final TimeHelper glideTime = new TimeHelper();

    // Grim
    boolean velo = false;
    private boolean launch;
    private int launchTicks;

    // Verus
    private final TimeHelper verusTimer = new TimeHelper();

    @Override
    public String getSuffix() {
    	return mode.getValue();
    }

    @Listen
    public final void onCollisionBoxes(CollisionBoxesEvent collisionBoxesEvent) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;

        switch(mode.getValue()) {
        case "Collision":
            collisionBoxesEvent.setBoundingBox(new AxisAlignedBB(-2, -1, -2, 2, 1, 2).offset(collisionBoxesEvent.getBlockPos().getX(), collisionBoxesEvent.getBlockPos().getY(), collisionBoxesEvent.getBlockPos().getZ()));
            break;
        }
    }

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        switch (this.mode.getValue()) {
            case "Grim":
                switch (this.grimMode.getValue()) {
                    case "Boat":
                        if(mc.thePlayer.isRiding() && mc.thePlayer.ridingEntity != null) {
                            if(mc.thePlayer.ridingEntity instanceof EntityBoat) {
                                EntityBoat boat = (EntityBoat) mc.thePlayer.ridingEntity;
                                float yaw = boat.rotationYaw;
                                float pitch = 90;
                                rotationEvent.setYaw(yaw);
                                rotationEvent.setPitch(pitch);
                            }
                        }
                        break;
                }
                break;
        }
    }

    @Listen
    public final void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        switch (mode.getValue()) {
            case "Verus":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    switch (verusMode.getValue()) {
                        case "Damage":
                            if (mc.thePlayer.hurtTime != 0) {
                                MoveUtil.strafe(5);
                            }

                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                                MoveUtil.strafe(0.4F);
                            }
                            break;

                        case "Jump":
                            if (verusTimer.hasReached(545, true)) {
                                mc.thePlayer.jump();
                                mc.thePlayer.onGround = true;
                            }
                            break;
                    }
                }
                break;
            case "Grim":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    switch (grimMode.getValue()) {
                        case "Explosion":
                            if (velo) {
                                if (mc.thePlayer.hurtTime != 0) {
                                    mc.thePlayer.posY -= 100;
                                    mc.thePlayer.posX += 15;
                                    mc.thePlayer.posZ += 15;
                                    mc.thePlayer.jump();
                                    mc.thePlayer.motionY *= 1.022;
                                    mc.thePlayer.onGround = true;
                                }
                                velo = false;
                            }
                            break;
                        case "Boat":
                            if(mc.thePlayer.isRiding() && mc.thePlayer.ridingEntity instanceof EntityBoat) {
                                launch = true;
                            }
                            if(launch && !mc.thePlayer.isRiding()) {
                                mc.thePlayer.motionY = 1.5;
                                MoveUtil.strafe(1.5);
                                launch = false;
                            }
                            break;
                    }
                }
                break;
            case "Vanilla":
                moveSpeed = speed.getValue();

                if (mc.gameSettings.keyBindJump.isKeyDown())
                    mc.thePlayer.motionY = moveSpeed / 2;

                else if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = -moveSpeed / 2;
                else
                    mc.thePlayer.motionY = 0;

                MoveUtil.strafe(moveSpeed);
                break;
            case "Test":
                if(!isMoving())
                    return;

                mc.gameSettings.keyBindJump.pressed = mc.thePlayer.onGround;

                if(mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 0.8F;
                } else {
                    mc.timer.timerSpeed = 1.1F;
                }
                break;
            case "Old NCP":
                if (mc.thePlayer.onGround && !jumped) {
                    mc.thePlayer.jump();
                    jumped = true;
                }
                break;
            case "Vulcan":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.PRE) {
                    switch (vulcanMode.getValue()) {
                        case "Clip & Glide":
                            if(mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                            } else if(mc.thePlayer.fallDistance > 0.25) {
                                if(jumps < 3) {
                                    this.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (jumps == 0 ? 10 : 10), mc.thePlayer.posZ);
                                    jumps++;
                                } else {
                                    jumped = true;

                                    if(glideTime.hasReached(146, true)) {
                                        mc.thePlayer.motionY = -0.1476D;
                                    } else {
                                        mc.thePlayer.motionY = -0.0975D;
                                    }
                                }
                            }
                            break;
                        case "Normal":
                            stage++;

                            switch (stage) {
                                case 1:
                                    if (!mc.thePlayer.onGround) {
                                        sendMessage("You need to be on ground to do this!");
                                        toggle();
                                        return;
                                    }
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ);
                                    mc.timer.timerSpeed = timer.getValue();
                                    break;
                                default:
                                    if (stage == 2 && mc.thePlayer.posY != startY) {
                                        mc.thePlayer.setPosition(mc.thePlayer.posX, startY, mc.thePlayer.posZ);
                                    }
                                    if (stage < time.getValue()) {
                                        mc.thePlayer.motionY = 0;
                                        MoveUtil.strafe(speed.getValue());
                                    }

                                    if (stage >= time.getValue()) {
                                        MoveUtil.strafe(0);
                                        setEnabled(false);
                                    }
                                    break;
                            }
                            break;
                        case "Glide":
                            if (!mc.thePlayer.isInWater()) {
                                if (!mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 2 == 0) {
                                    mc.thePlayer.motionY = -0.1476D;
                                } else {
                                    mc.thePlayer.motionY = -0.0975D;
                                }
                            }
                            break;
                        case "Vanilla":
                            mc.thePlayer.onGround = true;
                            mc.thePlayer.motionY = 0.0;
                            break;
                    }
                }
                break;
        }
    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        switch (mode.getValue()) {
            case "Grim":
                if(grimMode.is("Explosion")) {
                    if (packetEvent.getPacket() instanceof S12PacketEntityVelocity) {
                        S12PacketEntityVelocity packet = (S12PacketEntityVelocity) packetEvent.getPacket();
                        if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                            velo = true;
                        }
                    }
                }
                break;
            case "Vulcan":
                switch(vulcanMode.getValue()){
                    case "Clip & Glide":
                        if(jumped && packetEvent.getPacket() instanceof C03PacketPlayer) {
                            C03PacketPlayer packet = (C03PacketPlayer) packetEvent.getPacket();

                            if(mc.thePlayer.ticksExisted % 11 == 0) {
                                packet.setOnGround(true);
                            }
                        }
                        break;
                    case "Vanilla":
                        if(packetEvent.getPacket() instanceof C03PacketPlayer) {
                            packetEvent.setCancelled(true);
                        }
                        break;
                }
                break;
        }
    }

    @Listen
    public final void onMove(MovePlayerEvent movePlayerEvent) {
        switch (mode.getValue()) {
            case "Old NCP":
                if (!mc.thePlayer.onGround) {
                    movePlayerEvent.setY(mc.thePlayer.ticksExisted % 2 == 0 ? -1.0E-9 : 1.0E-9);
                    mc.thePlayer.motionY = 0;

                    if (moveSpeed >= MoveUtil.getBaseMoveSpeed()) {
                        moveSpeed -= moveSpeed / 102;
                    }

                    if (mc.thePlayer.isCollidedHorizontally || !this.isMoving()) {
                        moveSpeed = MoveUtil.getBaseMoveSpeed();
                    }

                    MoveUtil.strafe(moveSpeed);
                }
                break;
        }
    }

    @Override
    public void onEnable() {
        jumped = false;
        moveSpeed = speed.getValue();
        startY = mc.thePlayer.posY;
        stage = 0;
        jumped = false;
        jumps = 0;
        launch = false;
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        jumped = false;
        MoveUtil.strafe(0);
        mc.timer.timerSpeed = 1f;
        launch = false;
        stage = 0;
        mc.thePlayer.speedInAir = 0.02F;
        jumped = false;
        jumps = 0;

        if(mode.is("Vulcan") && vulcanMode.is("Vanilla")) {
            this.setPosition(mc.thePlayer.posX + 0.01, mc.thePlayer.posY, mc.thePlayer.posZ + 0.01);
        }
    }
}
