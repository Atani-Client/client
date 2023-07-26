package wtf.atani.module.impl.movement;

import com.google.common.base.Supplier;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import wtf.atani.event.events.MoveEntityEvent;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Speed", description = "Makes you speedy", category = Category.MOVEMENT)
public class Speed extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"BHop", "Strafe", "Incognito", "Karhu", "NCP", "Old NCP", "Verus", "Vulcan", "Spartan"});
    private final StringBoxValue spartanMode = new StringBoxValue("Spartan Mode", "Which mode will the spartan mode use?", this, new String[]{"Normal", "Y-Port Jump", "Timer"}, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Spartan")});
    private final StringBoxValue verusMode = new StringBoxValue("Verus Mode", "Which mode will the verus mode use?", this, new String[]{"Normal", "Slow", "Air Boost"}, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Verus")});
    private final StringBoxValue vulcanMode = new StringBoxValue("Vulcan Mode", "Which mode will the vulcan mode use?", this, new String[]{"Normal", "Slow", "Ground"}, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Vulcan")});

    private final StringBoxValue incognitoMode = new StringBoxValue("Incognito Mode", "Which mode will the incognito mode use?", this, new String[]{"Normal", "Exploit"}, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Incognito")});
    private final SliderValue<Float> boost = new SliderValue<>("Boost", "How much will the bhop boost?", this, 1.2f, 0.1f, 5.0f, 1, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("BHop")});
    private SliderValue<Float> jumpheight = new SliderValue<>("Jump Height", "How high will the bhop jump?", this, 0.41f, 0.01f, 1.0f, 2, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("BHop")});

    // Spartan
    private final TimeHelper sparanTimer = new TimeHelper();
    private boolean spartanBoost = true;
    
    // Verus
    private int verusDamageTicks;
    private int verusTicks;

    // Vulcan
    private int vulcanTicks;
    private double y;
    private TimeHelper vulcanTimer;

    @Listen
    public final void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        switch (mode.getValue()) {
            case "Strafe":
                if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    if(isMoving()) {
                        MoveUtil.strafe(null);

                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                        }
                    }
                }
                break;
            case "Vulcan":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    mc.gameSettings.keyBindJump.pressed = false;
                    if (mc.thePlayer.onGround) {
                        vulcanTicks = 0;
                    } else {
                        vulcanTicks++;
                    }

                    switch (vulcanMode.getValue()) {
                        case "Normal":
                            switch (vulcanTicks) {
                                case 0:
                                    mc.thePlayer.jump();

                                    MoveUtil.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6F : 0.485F);
                                    break;

                                case 1:
                                case 2:
                                    MoveUtil.strafe();
                                    break;

                                case 5:
                                    mc.thePlayer.motionY = MoveUtil.getPredictedMotion(mc.thePlayer.motionY, 3);
                                    break;
                                case 10:
                                    MoveUtil.strafe((float) (MoveUtil.getSpeed() * 0.8));
                            }
                            break;
                        case "Slow":
                            if(mc.thePlayer.onGround) {
                                vulcanTicks = 0;
                            } else {
                                vulcanTicks++;
                            }
                            switch(vulcanTicks) {
                                case 0:
                                    mc.thePlayer.jump();

                                    MoveUtil.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6F : 0.485F);
                                    break;
                                case 1:
                                case 2:
                                case 8:
                                    MoveUtil.strafe();
                                    break;
                                case 5:
                                    mc.thePlayer.motionY = MoveUtil.getPredictedMotion(mc.thePlayer.motionY, 6);
                                    break;
                            }
                            break;
                        case "Ground":
                            if (mc.thePlayer.onGround) {
                                mc.timer.timerSpeed = 1.02F;
                                y = 0.01;
                                mc.thePlayer.motionY = 0.01;
                                MoveUtil.strafe((float) (0.4175 + MoveUtil.getSpeedBoost(1.5F)));
                            } else {
                                mc.timer.timerSpeed = 1;
                                if (y == 0.01) {
                                    MoveUtil.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? MoveUtil.getBaseMoveSpeed() * 1.11F : MoveUtil.getBaseMoveSpeed() * 1.04F);
                                    y = 0;
                                }

                                if(mc.thePlayer.ticksExisted % 10 == 0) {

                                    mc.thePlayer.motionX *= 1.00575;
                                    mc.thePlayer.motionZ *= 1.00575;

                                    MoveUtil.strafe((float) (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? MoveUtil.getSpeed() * 1.04 : MoveUtil.getSpeed()));

                                    mc.timer.timerSpeed = 1.3F;
                                }
                                break;
                            }
                    }
                }
            break;
            case "Verus":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    if(mc.thePlayer.onGround) {
                        verusTicks = 0;
                    } else {
                        verusTicks++;
                    }

                    if(mc.thePlayer.hurtTime > 1 && !mc.thePlayer.isBurning() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && true && verusDamageTicks > 30) {
                        MoveUtil.strafe(5);
                        mc.thePlayer.motionX *= 1.2F;
                        mc.thePlayer.motionZ *= 1.2F;
                        mc.thePlayer.motionY = 0.1F;
                        verusDamageTicks = 0;
                    } else {
                        verusDamageTicks++;
                    }

                    switch(verusMode.getValue()) {
                        case "Slow":
                            if (!isMoving()) {
                                mc.gameSettings.keyBindJump.pressed = false;
                                return;
                            }

                            mc.gameSettings.keyBindJump.pressed = true;
                            mc.thePlayer.speedInAir = (float) (0.02 + Math.random() / 100);

                            MoveUtil.strafe((float) MoveUtil.getSpeed());
                            break;
                        case "Normal":
                            if(!isMoving())
                                return;

                            if (mc.thePlayer.onGround) {
                                MoveUtil.strafe(0.53F);
                                mc.thePlayer.jump();
                            } else {
                                MoveUtil.strafe(0.33F);
                            }

                            if (mc.thePlayer.moveForward < 0) {
                                MoveUtil.strafe(0.3F);
                            }
                            break;
                        case "Air Boost":
                            switch(verusTicks) {
                                case 0:
                                    mc.thePlayer.jump();
                                    MoveUtil.strafe(0.4F);
                                    break;
                                case 1:
                                    MoveUtil.strafe(MoveUtil.getBaseMoveSpeed() + 0.05F);
                                    break;
                                case 5:
                                case 10:
                                case 15:
                                case 20:
                                    if (mc.thePlayer.moveForward > 0 && mc.thePlayer.moveStrafing == 0) {
                                        MoveUtil.strafe(0.375F);
                                    } else if (mc.thePlayer.moveStrafing != 0 && mc.thePlayer.moveForward > 0) {
                                        MoveUtil.strafe(0.38F);
                                    } else if (mc.thePlayer.moveStrafing == 0 && mc.thePlayer.moveForward < 0) {
                                        MoveUtil.strafe(0.34F);
                                    } else if (mc.thePlayer.moveForward == 0) {
                                        MoveUtil.strafe(0.34F);
                                    }
                                    break;
                                case 8:
                                    mc.thePlayer.onGround = true;
                                    mc.thePlayer.jump();
                                    break;
                            }
                            MoveUtil.strafe((float) MoveUtil.getSpeed());
                    }
                }
            break;
            case "Spartan":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    switch (spartanMode.getValue()) {
                        case "Normal":
                            mc.gameSettings.keyBindJump.pressed = isMoving();

                            MoveUtil.strafe((float) MoveUtil.getSpeed());

                            mc.timer.timerSpeed = (float) (1.07 + Math.random() / 25);

                            if (0 > mc.thePlayer.moveForward && mc.thePlayer.moveStrafing == 0) {
                                mc.thePlayer.speedInAir = (float) (0.04 - Math.random() / 100);
                            } else if (mc.thePlayer.moveStrafing != 0) {
                                mc.thePlayer.speedInAir = (float) (0.036 - Math.random() / 100);
                            } else {
                                mc.thePlayer.speedInAir = (float) (0.0215F - Math.random() / 1000);
                            }

                            if (mc.thePlayer.onGround) {
                                MoveUtil.strafe((float) (MoveUtil.getSpeed() + 0.002));
                            }
                            break;

                        case "Y-Port Jump":
                            mc.gameSettings.keyBindJump.pressed = true;

                            MoveUtil.strafe((float) MoveUtil.getSpeed());

                            mc.timer.timerSpeed = (float) (1.07 + Math.random() / 25);

                            if (0 > mc.thePlayer.moveForward && mc.thePlayer.moveStrafing == 0) {
                                mc.thePlayer.speedInAir = (float) (0.04 - Math.random() / 100);
                            } else if (mc.thePlayer.moveStrafing != 0) {
                                mc.thePlayer.speedInAir = (float) (0.036 - Math.random() / 100);
                            } else {
                                mc.thePlayer.speedInAir = (float) (0.0215F - Math.random() / 1000);
                            }

                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.0002F;
                                MoveUtil.strafe((float) (MoveUtil.getSpeed() + 0.008));
                            }
                            break;

                        case "Timer":
                            if(mc.thePlayer.onGround && isMoving()) {
                                mc.thePlayer.jump();
                            }

                            if(sparanTimer.hasReached(3000)) {
                                spartanBoost = !spartanBoost;
                                sparanTimer.reset();
                            }

                            if(spartanBoost) {
                                mc.timer.timerSpeed = 1.6f;
                            } else {
                                mc.timer.timerSpeed = 1.0f;
                            }
                            break;
                    }
                }
            break;
            case "Old NCP":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    mc.thePlayer.setSprinting(true);
                    if (isMoving()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionX *= 0.75;
                            mc.thePlayer.motionZ *= 0.75;
                        } else {
                            if (mc.thePlayer.motionY < 0.4) {
                                mc.thePlayer.motionY = -1337.0;
                                MoveUtil.setMoveSpeed(null, 0.26);
                            }
                        }
                    }
                }
                break;
            case "NCP":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    if(mc.thePlayer.fallDistance > 0.75) {
                        mc.timer.timerSpeed = 1.23F;
                    } else {
                        mc.timer.timerSpeed = 1;
                    }
                    if (mc.thePlayer.onGround && isMoving()) {
                        mc.thePlayer.jump();
                        MoveUtil.strafe((float) (0.433 + MoveUtil.getSpeedBoost(0.6F) + Math.random() / 40 + mc.thePlayer.moveForward / 30));
                    } else if(isMoving()) {
                        MoveUtil.strafe((float) MoveUtil.getSpeed());
                    }
                }
                break;
            case "Karhu":
                if(mc.gameSettings.keyBindJump.pressed ||!isMoving())
                    return;

                mc.thePlayer.setSprinting(true);

                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    if(mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = 1;
                        mc.thePlayer.jump();
                        mc.thePlayer.motionY *= 0.55;
                    } else {
                        mc.timer.timerSpeed = (float) (1 + (Math.random() - 0.5) / 100);
                    }
                }
                break;
            case "Incognito":
                switch (this.incognitoMode.getValue()) {
                    case "Normal":
                        mc.gameSettings.keyBindJump.pressed = isMoving();

                        if(mc.thePlayer.onGround) {
                            MoveUtil.strafe((float) (0.36 + Math.random() / 70 + MoveUtil.getSpeedBoost(1)));
                        } else {
                            MoveUtil.strafe((float) (MoveUtil.getSpeed() - (float) (Math.random() - 0.5F) / 70F));
                        }

                        if(MoveUtil.getSpeed() < 0.25F) {
                            MoveUtil.strafe((float) (MoveUtil.getSpeed() + 0.02));
                        }
                        break;
                    case "Exploit":
                        mc.gameSettings.keyBindJump.pressed = isMoving();

                        float speed = (float) (Math.random() * 2.5);
                        if(0.4 > speed) {
                            speed = 1;
                        }

                        if(mc.thePlayer.onGround) {
                            mc.timer.timerSpeed = (float) (speed + Math.random() / 2);
                            MoveUtil.strafe((float) (0.36 + Math.random() / 70 + MoveUtil.getSpeedBoost(1)));
                        } else {
                            mc.timer.timerSpeed = speed;
                            MoveUtil.strafe((float) (MoveUtil.getSpeed() - (float) (Math.random() - 0.5F) / 70F));
                        }

                        if(MoveUtil.getSpeed() < 0.25F) {
                            MoveUtil.strafe((float) (MoveUtil.getSpeed() + 0.02));
                        }
                        break;
                }
                break;
        }
    }

    @Listen
    public void onMove(MoveEntityEvent moveEntityEvent) {
        switch (mode.getValue()) {
            case "BHop":
                MoveUtil.setMoveSpeed(moveEntityEvent, boost.getValue().floatValue());
                if (isMoving()) {
                    if (mc.thePlayer.onGround) {
                        moveEntityEvent.setY(mc.thePlayer.motionY = jumpheight.getValue());
                    }
                } else {
                    mc.thePlayer.motionX = 0.0;
                    mc.thePlayer.motionZ = 0.0;
                }
                break;
        }
    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        switch (mode.getValue()) {
            case "Vulcan":
                if(packetEvent.getPacket() instanceof C03PacketPlayer) {
                    ((C03PacketPlayer) packetEvent.getPacket()).y = mc.thePlayer.posY + y;
                }
                break;
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.speedInAir = 0.02F;
        mc.gameSettings.keyBindJump.pressed = false;
    }
}
