package wtf.atani.module.impl.movement;

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
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"NCP", "Test", "Vulcan"});
    private final SliderValue<Float> height = new SliderValue<>("Height", "High high will the player jump?", this, 4F, 0.4F, 10F, 0);

    // NCP
    private int ncpTicks;
    private double ncpSpeed;

    // Vulcan
    private int clips = 0;
    private boolean jumped;

    private final TimeHelper timer = new TimeHelper();

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    @Listen
    public final void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {

    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        if(packetEvent.getType() == PacketEvent.Type.INCOMING) {
            switch(mode.getValue()) {
            case "Vulcan":
                if(jumped && packetEvent.getPacket() instanceof C03PacketPlayer) {
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
                if(!jumped) {
                    mc.thePlayer.jump();
                } else {
                    this.setEnabled(false);
                }
            } else if(mc.thePlayer.fallDistance > 0.25) {
                if(clips < 3) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (clips == 0 ? 10 : height.getValue()), mc.thePlayer.posZ);
                    clips++;
                } else {
                    jumped = true;

                    if(timer.hasReached(145, true)) {
                        mc.thePlayer.motionY = -0.1476D;
                    } else {
                        mc.thePlayer.motionY = -0.0975D;
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onEnable() {
    //    mc.timer.timerSpeed = 0.6F;
        clips = 0;
        jumped = false;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        clips = 0;
        jumped = false;
    }
}
