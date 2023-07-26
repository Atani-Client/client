package wtf.atani.module.impl.movement;

import com.google.common.base.Supplier;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import wtf.atani.event.events.MoveEntityEvent;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Flight", description = "Mkaes you fly", category = Category.MOVEMENT)
public class Flight extends Module {
    private final StringBoxValue mode =new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Vanilla", "Old NCP", "Collision", "Vulcan", "Grim Explosion"});
    private final StringBoxValue vulcanMode =new StringBoxValue("Vulcan Mode", "Which mode will the vulcan mode use?", this, new String[]{"Normal", "Glide"}, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Vulcan")});
    private final SliderValue<Integer> time = new SliderValue<>("Time", "How long will the flight fly?", this, 10, 3, 15, 0, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Vulcan")});
    private final SliderValue<Float> timer = new SliderValue<>("Timer", "How high will be the timer when flying?", this, 0.2f, 0.1f, 0.5f, 1, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Vulcan")});

    private final SliderValue<Float> speed = new SliderValue<>("Speed", "How fast will the fly be?", this, 1.4f, 0f, 5f, 1, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Vulcan") || mode.getValue().equalsIgnoreCase("Vanilla")});

    // Old NCP
    private double moveSpeed;
    private boolean jumped;

    // Vulcan
    private double startY;
    private int stage;

    // Grim
    boolean velo = false;

    @Listen
    public final void nnUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        switch(mode.getValue()) {
            case "Grim Explosion":
                if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
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
            case "Old NCP":
                if(mc.thePlayer.onGround && !jumped) {
                    mc.thePlayer.jump();
                    jumped = true;
                }
                break;
            case "Vulcan":
                if(updateMotionEvent.getType() == UpdateMotionEvent.Type.PRE) {
                    switch(vulcanMode.getValue()) {
                        case "Normal":
                            stage++;

                            switch (stage) {
                                case 1:
                                    if(!mc.thePlayer.onGround) {
                                        sendMessage("You need to be on ground to do this!");
                                        toggle();
                                        return;
                                    }
                                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ);
                                    mc.timer.timerSpeed = (float) timer.getValue();
                                    break;
                                default:
                                    if(stage == 2 && mc.thePlayer.posY != startY) {
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
                            if(!mc.thePlayer.isInWater()) {
                                if (!mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 2 == 0) {
                                    mc.thePlayer.motionY = -0.1476D;
                                } else {
                                    mc.thePlayer.motionY = -0.0975D;
                                }
                            }
                            break;
                    }
                }
                break;
        }
    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        switch (mode.getValue()) {
            case "Grim Explosion":
                if (packetEvent.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) packetEvent.getPacket();
                    if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        velo = true;
                    }
                }
                break;
        }
    }

    @Listen
    public final void onMove(MoveEntityEvent moveEntityEvent) {
        switch (mode.getValue()) {
            case "Old NCP":
                if(!mc.thePlayer.onGround) {
                    moveEntityEvent.setY(mc.thePlayer.ticksExisted % 2 == 0 ? -1.0E-9 : 1.0E-9);
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
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        jumped = false;
        MoveUtil.strafe(0);
        mc.timer.timerSpeed = 1f;
        stage = 0;
        mc.thePlayer.speedInAir = 0.02F;
    }
}
