package wtf.atani.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;

import com.google.common.base.Supplier;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
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
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"NCP", "Test"});

    // NCP
    private int ncpTicks;
    private double ncpSpeed;

    @Override
    public String getSuffix() {
        return mode.getValue();
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
        }
    }

    @Override
    public void onEnable() {

        if(mc.thePlayer.onGround && mode.getValue().equals("Damage")) {
            this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
            this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
        ticks = 0;
        boosted = false;
        startY = mc.thePlayer.posY;

    //    mc.timer.timerSpeed = 0.6F;

    }

    @Override
    public void onDisable() {

        ticks = 0;
        boosted = false;
        mc.timer.timerSpeed = 1.0F;

        mc.timer.timerSpeed = 1;

    }
}
