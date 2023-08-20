package tech.atani.client.feature.module.impl.movement;

import com.google.common.base.Supplier;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

import tech.atani.client.listener.event.events.minecraft.player.movement.MovePlayerEvent;
import tech.atani.client.listener.event.events.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.events.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;
import tech.atani.client.utility.player.movement.MoveUtil;
import tech.atani.client.feature.module.value.impl.SliderValue;
import tech.atani.client.feature.module.value.impl.StringBoxValue;

@ModuleData(name = "LongJump", description = "Jumps long", category = Category.MOVEMENT)
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
                    if(Methods.mc.thePlayer.onGround) {
                        Methods.mc.thePlayer.jump();
                        Methods.mc.thePlayer.jump();
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

                    if(Methods.mc.thePlayer.ticksExisted % 11 == 0) {
                        packet.setOnGround(true);
                    }
                }
                break;
            }
        }
    }

    @Listen
    public final void onMove(MovePlayerEvent movePlayerEvent) {
        Methods.mc.gameSettings.keyBindJump.pressed = MoveUtil.getSpeed() != 0;
        switch(mode.getValue()) {
        case "NCP":
            if (Methods.mc.thePlayer.onGround) {
                ncpTicks = 0;
            } else {
                ncpTicks++;
            }

            if(Methods.mc.thePlayer.onGround) {
                ncpSpeed = Methods.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6 : 0.53;
            } else {
                ncpSpeed -= Methods.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.03 : 0.02;
            }

            if(Methods.mc.thePlayer.moveForward > 0 && Methods.mc.thePlayer.moveStrafing == 0) {
                if(ncpSpeed < 0.2) {
                    MoveUtil.strafe(0.2 + MoveUtil.getSpeedBoost(7));
                }
                    MoveUtil.strafe(ncpSpeed + MoveUtil.getSpeedBoost(7));
            }

            switch(ncpTicks) {
                case 4:
                    ncpSpeed += 0.07;
                    Methods.mc.thePlayer.motionY += 0.02;
                    break;
                case 6:
                    Methods.mc.thePlayer.motionY += 0.02;
                    break;
                case 8:
                    Methods.mc.thePlayer.motionY += 0.1;
                    ncpSpeed += 0.014;
                    break;
            }
            break;
        case "Vulcan":
            if(Methods.mc.thePlayer.onGround) {
                if(!vulcanJumped) {
                    Methods.mc.thePlayer.jump();
                } else {
                    this.setEnabled(false);
                }
            } else if(Methods.mc.thePlayer.fallDistance > 0.25) {
                if(vulcanClips < 3) {
                    Methods.mc.thePlayer.setPosition(Methods.mc.thePlayer.posX, Methods.mc.thePlayer.posY + (vulcanClips == 0 ? 10 : vulcanHeight.getValue()), Methods.mc.thePlayer.posZ);
                    vulcanClips++;
                } else {
                    vulcanJumped = true;

                    if(testTimer.hasReached(145, true)) {
                        Methods.mc.thePlayer.motionY = -0.1476D;
                    } else {
                        Methods.mc.thePlayer.motionY = -0.0975D;
                    }
                }
            }
            break;
            case "Test":
                if(Methods.mc.thePlayer.onGround) {
                    testTicks = 0;
                } else {
                    testTicks++;
                }
                switch (testTicks) {
                    case 0:
                        Methods.mc.thePlayer.jump();
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

                if(Methods.mc.thePlayer.moveForward > 0 && Methods.mc.thePlayer.moveStrafing == 0)
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
        Methods.mc.timer.timerSpeed = 1;
        Methods.mc.gameSettings.keyBindJump.pressed = false;
    }
}