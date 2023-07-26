package wtf.atani.module.impl.movement;

import wtf.atani.event.events.MoveEntityEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Speed", description = "Makes you speedy", category = Category.MOVEMENT)
public class Speed extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"BHop", "Strafe"});
    private final SliderValue<Float> boost = new SliderValue<>("Boost", "How much will the bhop boost?", this, 1.2f, 0.1f, 5.0f, 1);
    private SliderValue<Float> jumpheight = new SliderValue<>("Jump Height", "How high will the bhop jump?", this, 0.41f, 0.01f, 1.0f, 2);

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        switch(mode.getValue()) {
            case "Strafe":
                if(isMoving()) {
                    MoveUtil.strafe(null);

                    if (mc.thePlayer.onGround) {
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

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
