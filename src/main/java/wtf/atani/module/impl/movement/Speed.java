package wtf.atani.module.impl.movement;

import com.google.common.base.Supplier;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
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
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Speed", description = "Makes you speedy", category = Category.MOVEMENT)
public class Speed extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"BHop", "Strafe", "Incognito", "Karhu", "NCP", "BlocksMC", "Old NCP", "Verus", "Vulcan", "Matrix", "Spartan", "Grim", "Test", "WatchDog", "Intave", "MineMenClub", "Polar Test", "Custom"});
    private final StringBoxValue spartanMode = new StringBoxValue("Spartan Mode", "Which mode will the spartan mode use?", this, new String[]{"Normal", "Y-Port Jump", "Timer"}, new Supplier[]{() -> mode.is("Spartan")});
    private final StringBoxValue verusMode = new StringBoxValue("Verus Mode", "Which mode will the verus mode use?", this, new String[]{"Normal", "Slow", "Air Boost"}, new Supplier[]{() -> mode.is("Verus")});
    private final StringBoxValue vulcanMode = new StringBoxValue("Vulcan Mode", "Which mode will the vulcan mode use?", this, new String[]{"Normal", "Slow", "Ground", "Y-Port"}, new Supplier[]{() -> mode.is("Vulcan")});
    private final StringBoxValue ncpMode = new StringBoxValue("NCP Mode", "Which mode will the ncp mode use?", this, new String[]{"Custom", "Normal", "Normal 2", "Stable"}, new Supplier[]{() -> mode.is("NCP")});
    private final SliderValue<Double> ncpJumpMotion = new SliderValue<>("NCP Jump Motion", "What Motion will the NCP Speed use?", this, 0.41d, 0.4, 0.42d, 3, new Supplier[]{() -> mode.is("NCP") && ncpMode.is("Custom")});
    private final SliderValue<Double> ncpOnGroundSpeed = new SliderValue<>("NCP onGround Speed", "How fast will the NCP Speed move onGround?", this, 0.485d, 0.1, 0.5d, 3, new Supplier[]{() -> mode.is("NCP") && ncpMode.is("Custom")});
    private final CheckBoxValue ncpMotionModify = new CheckBoxValue("NCP Motion Modification", "Will the speed modify Motion Y?", this, true, new Supplier[]{() -> mode.is("NCP") && ncpMode.is("Custom")});
    private final StringBoxValue incognitoMode = new StringBoxValue("Incognito Mode", "Which mode will the incognito mode use?", this, new String[]{"Normal", "Exploit"}, new Supplier[]{() -> mode.is("Incognito")});
    private final SliderValue<Float> boost = new SliderValue<>("Boost", "How much will the bhop boost?", this, 1.2f, 0.1f, 5.0f, 1, new Supplier[]{() -> mode.is("BHop")});
    private final SliderValue<Float> jumpHeight = new SliderValue<>("Jump Height", "How high will the bhop jump?", this, 0.41f, 0.01f, 1.0f, 2, new Supplier[]{() -> mode.is("BHop")});

    //Custom
    private final SliderValue<Double> motionY = new SliderValue<>("Motion Y", "How big will the y motion be?", this, 0.42d, 0.01d, 2d, 2, new Supplier[]{() -> mode.is("Custom")});
    private final SliderValue<Double> airSpeed = new SliderValue<>("Air Speed", "How fast will you go in air?", this, 1d, 0.01d, 3d, 2, new Supplier[]{() -> mode.is("Custom")});
    private final SliderValue<Double> friction = new SliderValue<>("Friction", "How big or small will friction be?", this, 0.42d, 0.01d, 2d, 2, new Supplier[]{() -> mode.is("Custom")});
    private final SliderValue<Float> timer = new SliderValue<>("Timer", "How fast should the game speed be?", this, 1f, 0.1f, 5f, 1, new Supplier[]{() -> mode.is("Custom")});
    private final SliderValue<Double> groundSpeed = new SliderValue<>("Ground Speed", "How big or small will the y motion be?", this, 1d, 0.01d, 3d, 2, new Supplier[]{() -> mode.is("Custom")});
    private final CheckBoxValue strafe = new CheckBoxValue("Strafe", "Should the module enable strafing?", this, false, new Supplier[]{() -> mode.is("Custom")});
    private final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Should the module enable sprinting?", this, true, new Supplier[]{() -> mode.is("Custom")});
    private final CheckBoxValue stop = new CheckBoxValue("Stop", "Should the module stop all motion when not moving?", this, false, new Supplier[]{() -> mode.is("Custom")});
    private final CheckBoxValue yPort = new CheckBoxValue("Y-Port", "Should the module y-port?", this, false, new Supplier[]{() -> mode.is("Custom")});
    private final SliderValue<Double> minusMotionY = new SliderValue<>("Minus Motion Y", "How big will the -y motion be?", this, 0.42d, 0.01d, 2d, 2, new Supplier[]{() -> mode.is("Custom") && yPort.getValue()});

    // Spartan
    private final TimeHelper spartanTimer = new TimeHelper();
    private boolean spartanBoost = true;

    // Verus
    private int verusDamageTicks;
    private int verusTicks;

    // Vulcan
    private int vulcanTicks;
    private double y;

    // WatchDog
    private int watchDogTicks;

    // NCP
    private int ncpTicks;

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    @Listen
    public final void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        switch (mode.getValue()) {
            case "Custom":
                mc.timer.timerSpeed = timer.getValue();
                if (mc.thePlayer.onGround) {
                    MoveUtil.setMoveSpeed(groundSpeed.getValue());

                    if (mc.thePlayer.moveForward != 0.0) {
                        mc.thePlayer.motionY = motionY.getValue();
                    }
                } else if (mc.thePlayer.isAirBorne) {
                    MoveUtil.setMoveSpeed(airSpeed.getValue());

                    if (mc.thePlayer.motionY <= 0.0) {
                        mc.thePlayer.motionY *= friction.getValue();
                    }

                    if (strafe.getValue()) {
                        MoveUtil.setMoveSpeed(Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
                    }

                    if (yPort.getValue()) {
                        mc.thePlayer.motionY = -minusMotionY.getValue();
                    }

                } else {
                    MoveUtil.setMoveSpeed(Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
                    if (yPort.getValue()) {
                        mc.thePlayer.motionY = -minusMotionY.getValue();
                    }
                }

                if (sprint.getValue() && !mc.thePlayer.isSprinting()) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
                }

                if (stop.getValue() && !this.isMoving()) {
                    mc.thePlayer.motionX = 0.0;
                    mc.thePlayer.motionZ = 0.0;
                }

                break;
            case "Polar Test":
                if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
                    if (isMoving()) {
                        if (mc.thePlayer.onGround) {
                            mc.gameSettings.keyBindJump.pressed = false;
                            mc.thePlayer.jump();
                        }

                        if (mc.thePlayer.motionY > 0.003) {
                            mc.thePlayer.motionX *= 1.01;
                            mc.thePlayer.motionZ *= 1.01;
                        } else if (mc.thePlayer.motionY < 0.0029) {
                            // This dont do shit?
                            mc.thePlayer.motionX *= 1.0;
                            mc.thePlayer.motionZ *= 1.0;
                        }
                    }
                }
                break;
            case "Matrix":
                if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    if (!isMoving()) {
                        mc.gameSettings.keyBindJump.pressed = false;
                        return;
                    }

                    mc.gameSettings.keyBindJump.pressed = true;

                    mc.thePlayer.speedInAir = 0.0204F;

                    if(mc.thePlayer.motionY > 0.4) {
                        mc.thePlayer.motionX *= 1.003F;
                        mc.thePlayer.motionZ *= 1.003F;
                    }

                    if(mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = (float) (1.1 - Math.random() / 10);
                        mc.thePlayer.motionX *= 1.0045F;
                        mc.thePlayer.motionZ *= 1.0045F;
                    } else {
                        mc.timer.timerSpeed = Math.random() > 0.95 ? 1.03F : (float) (1 - Math.random() / 500);
                    }
                }
                break;
            case "Strafe":
                if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    if(isMoving()) {
                        MoveUtil.strafe(null);

                        if (mc.thePlayer.onGround && this.isMoving()) {
                            mc.thePlayer.jump();
                        }
                    }
                }
                break;
            case "Vulcan":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    if(!vulcanMode.getValue().equalsIgnoreCase("Ground"))
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
                                    if(this.isMoving()) {
                                        mc.thePlayer.jump();
                                        mc.timer.timerSpeed = 1.2F;
                                    } else {
                                        mc.timer.timerSpeed = 1;
                                    }
                                    MoveUtil.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6F : 0.485F);
                                    break;
                                case 1:
                                case 2:
                                    MoveUtil.strafe();
                                    mc.timer.timerSpeed = 1;
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
                                    if(this.isMoving())
                                        mc.thePlayer.jump();

                                    MoveUtil.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6F : 0.485F);
                                    break;
                                case 1:
                                case 2:
                                case 8:
                                    MoveUtil.strafe();
                                    break;
                            }
                            break;
                        case "Ground":
                            if (mc.thePlayer.onGround) {
                                mc.timer.timerSpeed = 1.05F;
                                y = 0.01;
                                mc.thePlayer.motionY = 0.01;
                                MoveUtil.strafe((float) (0.4175 + MoveUtil.getSpeedBoost(1.53F)));
                            } else {
                                mc.timer.timerSpeed = 1;
                                if (y == 0.01) {
                                    MoveUtil.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? MoveUtil.getBaseMoveSpeed() * 1.15F : MoveUtil.getBaseMoveSpeed() * 1.04F);
                                    y = 0;
                                }

                                if(mc.thePlayer.ticksExisted % 10 == 0) {

                                    mc.thePlayer.motionX *= 1.00575;
                                    mc.thePlayer.motionZ *= 1.00575;

                                    MoveUtil.strafe((float) (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? MoveUtil.getSpeed() * 1.08 : MoveUtil.getSpeed()));

                                    mc.timer.timerSpeed = 1.18F;
                                }
                            }
                            break;
                        case "YPort":
                            if(mc.thePlayer.onGround) {
                                vulcanTicks = 0;
                                mc.thePlayer.jump();
                                mc.timer.timerSpeed = 1.07F;
                                MoveUtil.strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6 : 0.485);
                            } else {
                                vulcanTicks++;
                                mc.timer.timerSpeed = 1;
                            }

                            if(mc.thePlayer.fallDistance > 0.1) {
                                mc.timer.timerSpeed = 1.3F;
                                mc.thePlayer.motionY = -1337;
                            }

                            if(3 > vulcanTicks) {
                                MoveUtil.strafe();
                            }
                            break;
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

                    if(mc.thePlayer.hurtTime > 1 && !mc.thePlayer.isBurning() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && verusDamageTicks > 30) {
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
                            if(mc.thePlayer.hurtTime != 0) {
                                MoveUtil.strafe(5);
                            }
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

                                    mc.timer.timerSpeed = 1;
                                    break;
                                case 8:
                                    mc.thePlayer.onGround = true;
                                    mc.thePlayer.jump();
                                    mc.timer.timerSpeed = 1.6F;
                                    break;
                            }
                            MoveUtil.strafe((float) MoveUtil.getSpeed() + 0.002);
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

                            if(spartanTimer.hasReached(3000)) {
                                spartanBoost = !spartanBoost;
                                spartanTimer.reset();
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
                            mc.timer.timerSpeed = 1;
                        } else {
                            if (mc.thePlayer.motionY < 0.4) {
                                mc.thePlayer.motionY = -1337.0;
                                MoveUtil.setMoveSpeed(null, 0.261);
                                mc.timer.timerSpeed = (float) (1.07 + Math.random() / 33);
                            }
                        }
                    }
                }
                break;
            case "NCP":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    if(mc.thePlayer.onGround)
                        ncpTicks = 0;
                    else
                        ncpTicks++;

                    switch (ncpMode.getValue()) {
                        case "Custom":
                            if(mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                                mc.thePlayer.motionY = ncpJumpMotion.getValue();
                                MoveUtil.strafe(ncpOnGroundSpeed.getValue());
                            }
                            break;
                        case "Normal":
                            if(!isMoving())
                                return;

                            if(mc.thePlayer.onGround) {
                                mc.timer.timerSpeed = 2F;
                                mc.thePlayer.motionY = ncpJumpMotion.getValue();
                                MoveUtil.strafe(0.48 + MoveUtil.getSpeedBoost(4));
                            } else {
                                mc.timer.timerSpeed = 1;
                                MoveUtil.strafe(MoveUtil.getSpeed() + MoveUtil.getSpeedBoost(0.375F));
                            }

                            if(ncpTicks == 5 && ncpMotionModify.getValue()) {
                                mc.thePlayer.motionY -= 0.1;
                            }
                            break;
                            // Tabio please dont kill me for the naming
                        case "Normal 2":
                            if(!isMoving()) {
                                MoveUtil.strafe(0);
                                return;
                            }

                            if(mc.thePlayer.onGround) {
                                mc.timer.timerSpeed = 2F;
                                mc.thePlayer.jump();
                                mc.thePlayer.motionY = ncpJumpMotion.getValue();
                                MoveUtil.strafe(0.48 + MoveUtil.getSpeedBoost(5));
                            } else {
                                mc.timer.timerSpeed = (float) (1.02 - Math.random() / 50);
                            }

                            MoveUtil.strafe();

                            if(ncpTicks == 5 && ncpMotionModify.getValue()) {
                                mc.thePlayer.motionY -= 0.1;
                            }
                            break;
                        case "Stable":
                            if(!isMoving())
                                return;

                            if(mc.thePlayer.onGround) {
                                mc.timer.timerSpeed = 1F;
                                mc.thePlayer.motionY = ncpJumpMotion.getValue();
                                MoveUtil.strafe(0.48 + MoveUtil.getSpeedBoost(4));
                            } else {
                                mc.timer.timerSpeed = (float) (1 + Math.random() / 7.5);
                                // this can be patched any moment, currently works on eu.loyisa.cn
                                MoveUtil.strafe(MoveUtil.getBaseMoveSpeed() - 0.02 + MoveUtil.getSpeedBoost(1.75F));
                            }

                            if(ncpTicks == 5 && ncpMotionModify.getValue()) {
                                mc.thePlayer.motionY -= 0.1;
                            }
                            break;
                    }
                }
                break;
            case "BlocksMC":
                mc.gameSettings.keyBindJump.pressed = isMoving();

                if(!isMoving())
                    return;

                if(mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 2;
                    if(mc.thePlayer.moveForward < 0) {
                        MoveUtil.strafe(0.48);
                    }
                } else {
                    if(mc.thePlayer.moveForward < 0 && MoveUtil.getSpeed() < MoveUtil.getBaseMoveSpeed()) {
                        MoveUtil.strafe(MoveUtil.getBaseMoveSpeed());
                    } else if(mc.thePlayer.moveForward > 0 && MoveUtil.getSpeed() < MoveUtil.getBaseMoveSpeed() - 0.02) {
                        MoveUtil.strafe(MoveUtil.getBaseMoveSpeed() - 0.02);
                    }
                    mc.timer.timerSpeed = (float) (1.05 - Math.random() / 21);
                    MoveUtil.strafe();
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
            // Do not change the name I will be very made >:(
            case "WatchDog":
                if(MoveUtil.getSpeed() == 0) {
                    mc.timer.timerSpeed = 1;
                } else {
                    mc.timer.timerSpeed = (float) (1 + Math.random() / 30);
                    if(mc.thePlayer.onGround) {
                        watchDogTicks = 0;
                        mc.thePlayer.jump();
                        MoveUtil.strafe((float) (0.525 - Math.random() / 10));
                    } else {
                        watchDogTicks++;
                        mc.thePlayer.motionY -= 0.0008;
                        if(watchDogTicks == 1) {
                            mc.thePlayer.motionY -= 0.002;
                        }

                        if(watchDogTicks == 8) {
                            mc.thePlayer.motionY -= 0.003;
                        }
                    }
                }
                break;
            case "Intave":
                mc.gameSettings.keyBindJump.pressed = MoveUtil.getSpeed() != 0;

                if(mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1.07F;
                } else {
                    mc.timer.timerSpeed = (float) (1 + Math.random() / 1200);
                }

                if(mc.thePlayer.motionY > 0) {
                    mc.timer.timerSpeed += 0.01;
                    mc.thePlayer.motionX *= 1.0004;
                    mc.thePlayer.motionZ *= 1.0004;
                }

                if(mc.thePlayer.hurtTime != 0) {
                    mc.timer.timerSpeed = 1.21F;
                }
                break;
            case "Test":
                break;
            case "MineMenClub":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    mc.thePlayer.setSprinting(this.isMoving());

                    if (mc.thePlayer.onGround && this.isMoving()){
                        mc.thePlayer.jump();
                    } else {
                        if (mc.thePlayer.hurtTime <= 6) {
                            MoveUtil.strafe();
                        }
                    }
                }
                break;
            case "Grim":
                if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
                    getGameSettings().keyBindSprint.pressed = true;

                    if (mc.thePlayer.onGround && this.isMoving()){
                        mc.thePlayer.jump();
                    }
                }
                break;
        }
    }

    @Listen
    public void onMove(MoveEntityEvent moveEntityEvent) {
        switch (mode.getValue()) {
            case "BHop":
                MoveUtil.setMoveSpeed(moveEntityEvent, boost.getValue());
                if (isMoving()) {
                    if (mc.thePlayer.onGround) {
                        moveEntityEvent.setY(mc.thePlayer.motionY = jumpHeight.getValue());
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
                    if(vulcanMode.is("YPort")) {
                        if(0 > mc.thePlayer.fallDistance) {
                            ((C03PacketPlayer) packetEvent.getPacket()).y -= mc.thePlayer.fallDistance - 1;
                        } else {
                            ((C03PacketPlayer) packetEvent.getPacket()).y -= mc.thePlayer.fallDistance;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.speedInAir = 0.02F;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }
}
