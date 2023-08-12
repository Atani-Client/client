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
    public final void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        mc.gameSettings.keyBindJump.pressed = MoveUtil.getSpeed() != 0;
        switch(mode.getValue()) {
            case "NCP":
                if (mc.thePlayer.onGround) {
                    ncpTicks = 0;
                } else {
                    ncpTicks++;
                }

                if(mc.thePlayer.onGround) {
                    ncpSpeed = 0.4 ;
                } else {
                    ncpSpeed -= 0.007;
                }

                if(mc.thePlayer.moveForward > 0 && mc.thePlayer.moveStrafing == 0 && !mc.thePlayer.onGround) {
                    if(ncpSpeed < 0.2875) {
                    //    MoveUtil.strafe(0.2875);
                    } else {
                        MoveUtil.strafe(ncpSpeed);
                    }
                }

                switch(ncpTicks) {
                    case 4:
                    //    ncpSpeed += 0.02;
                        break;
                    case 30:
                        mc.thePlayer.motionY += 0.10;
                        break;
                }
                break;
        }
    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {

    }

    @Listen
    public final void onMove(MoveEntityEvent moveEntityEvent) {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
