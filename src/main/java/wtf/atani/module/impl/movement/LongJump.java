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

@ModuleInfo(name = "LongJump", description = "Jumps long", category = Category.MOVEMENT)
public class LongJump extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"NCP", "Test", "Vulcan", "Intave"});
    private final SliderValue<Float> vulcanHeight = new SliderValue<>("Height", "High high will the player jump?", this, 4F, 0.4F, 10F, 0, new Supplier[]{() -> mode.is("Vulcan")});

    // NCP
    private int ncpTicks;
    private double ncpSpeed;

    // Vulcan
    private int vulcanClips = 0;
    private boolean vulcanJumped;

    // Test
    private float testSpeed;
    private int testTicks;
    private final TimeHelper testTimer = new TimeHelper();

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    @Listen
    public final void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            switch (mode.getValue()) {
                // Fucking insane
                case "Intave":
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        mc.thePlayer.jump();
                        this.setEnabled(false);
                    }
                    break;
            }
        }
    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        if(packetEvent.getType() == PacketEvent.Type.INCOMING) {
            switch(mode.getValue()) {
            case "Vulcan":
                if(vulcanJumped && packetEvent.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer packet = (C03PacketPlayer) packetEvent.getPacket();

                    if(mc.thePlayer.ticksExisted % 11 == 0) {
                        packet.setOnGround(true);
                    }
                }
                break;
            }
        }
    }

    @Listen
    public final void onMove(MoveEntityEvent moveEntityEvent) {
        mc.gameSettings.keyBindJump.pressed = MoveUtil.getSpeed() != 0;
        switch(mode.getValue()) {
        case "NCP":
            if (mc.thePlayer.onGround) {
                ncpTicks = 0;
            } else {
                ncpTicks++;
            }

            if(mc.thePlayer.onGround) {
                ncpSpeed = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6 : 0.53;
            } else {
                ncpSpeed -= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.03 : 0.02;
            }

            if(mc.thePlayer.moveForward > 0 && mc.thePlayer.moveStrafing == 0) {
                if(ncpSpeed < 0.2) {
                    MoveUtil.strafe(0.2 + MoveUtil.getSpeedBoost(7));
                }
                    MoveUtil.strafe(ncpSpeed + MoveUtil.getSpeedBoost(7));
            }

            switch(ncpTicks) {
                case 4:
                    ncpSpeed += 0.07;
                    mc.thePlayer.motionY += 0.02;
                    break;
                case 6:
                    mc.thePlayer.motionY += 0.02;
                    break;
                case 8:
                    mc.thePlayer.motionY += 0.1;
                    ncpSpeed += 0.014;
                    break;
            }
            break;
        case "Vulcan":
            if(mc.thePlayer.onGround) {
                if(!vulcanJumped) {
                    mc.thePlayer.jump();
                } else {
                    this.setEnabled(false);
                }
            } else if(mc.thePlayer.fallDistance > 0.25) {
                if(vulcanClips < 3) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (vulcanClips == 0 ? 10 : vulcanHeight.getValue()), mc.thePlayer.posZ);
                    vulcanClips++;
                } else {
                    vulcanJumped = true;

                    if(testTimer.hasReached(145, true)) {
                        mc.thePlayer.motionY = -0.1476D;
                    } else {
                        mc.thePlayer.motionY = -0.0975D;
                    }
                }
            }
            break;
            case "Test":
                if(mc.thePlayer.onGround) {
                    testTicks = 0;
                } else {
                    testTicks++;
                }
                switch (testTicks) {
                    case 0:
                        mc.thePlayer.jump();
                        testSpeed = 0.485F;
                        break;
                    case 1:
                        testSpeed = (float) MoveUtil.getBaseMoveSpeed();
                        break;
                    case 2:
                        testSpeed = (float) ((MoveUtil.getBaseMoveSpeed() * 1.2 + Math.random() / 8) + Math.random() / 9);
                        break;
                }

                if(testTicks > 4) {
                testSpeed -= 0.01;
                }

                if(mc.thePlayer.moveForward > 0 && mc.thePlayer.moveStrafing == 0)
                    MoveUtil.strafe(testSpeed);
                break;
        }
    }

    @Override
    public void onEnable() {
        vulcanClips = 0;
        vulcanJumped = false;
    }

    @Override
    public void onDisable() {
        vulcanClips = 0;
        vulcanJumped = false;
        mc.timer.timerSpeed = 1;
        mc.gameSettings.keyBindJump.pressed = false;
    }
}
