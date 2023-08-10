package wtf.atani.module.impl.movement;


import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "LongJump", description = "Jump even longer or higher", category = Category.MOVEMENT)
public class LongJump extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "What mode should this module use?", this, new String[] {"Damage", "NCP"});

    private final SliderValue<Double> height = new SliderValue("Height", "How high should the player jump?", this, 0.41, 0.41, 2, 0);
    private final SliderValue<Double> speed = new SliderValue("Speed", "How fast should the player move?", this, 0.6, 0.2, 6, 0);

    private boolean boosted;

    private int ticks;
    private double startY;

    @Listen
    public void onUpdate(UpdateMotionEvent updateMotionEvent) {
        if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            switch(mode.getValue()) {
            case "Damage":
                if(mc.thePlayer.hurtTime >= 8 && mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = height.getValue();
                    MoveUtil.strafe(speed.getValue());
                    this.setEnabled(false);
                }
                break;
            case "NCP":
                if(mc.thePlayer.onGround) {
                    if(boosted) {
                        this.setEnabled(false);
                    } else {
                        mc.thePlayer.jump();
                    }
                } else {
                    if(!boosted) {
                        MoveUtil.strafe(speed.getValue());
                        boosted = true;
                    }
                }
                break;
            }
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
    }

    @Override
    public void onDisable() {
        ticks = 0;
        boosted = false;
        mc.timer.timerSpeed = 1.0F;
    }
}
