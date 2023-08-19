package wtf.atani.module.impl.movement;

import com.google.common.base.Supplier;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Step", description = "Makes you walk up blocks.", category = Category.MOVEMENT)
public class Step extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Vanilla", "NCP", "Motion", "Spartan"});
    private final SliderValue<Integer> height = new SliderValue<>("Height", "How high will the step go?", this, 2, 0, 10, 1, new Supplier[]{() -> mode.is("Vanilla")});

    // NCP
    private boolean hasStepped;

    @Override
    public String getSuffix() {
    	return mode.getValue();
    }
    
    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            switch(mode.getValue()) {
                case "Vanilla":
                    mc.thePlayer.stepHeight = height.getValue();
                    break;
                case "NCP":
                    mc.thePlayer.stepHeight = 0.6F;

                    if(mc.thePlayer.onGround) {
                        hasStepped = false;
                    }

                    if(!hasStepped && this.isMoving() &&  mc.thePlayer.onGround && mc.thePlayer.isCollidedHorizontally) {
                        mc.thePlayer.jump();
                        hasStepped = true;
                    } else {
                        if (!mc.thePlayer.isCollidedHorizontally && hasStepped && this.isMoving()) {
                            hasStepped = false;
                            mc.thePlayer.motionY = 0;
                            if (mc.thePlayer.moveForward > 0) {
                                MoveUtil.setMoveSpeed(MoveUtil.getBaseMoveSpeed());
                            }
                        }
                    }
                    break;
                case "Motion":
                    mc.thePlayer.stepHeight = 0.6F;

                    if(mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = .39;
                    }
                    break;
                case "Spartan":
                    mc.thePlayer.stepHeight = 1;
                    break;
            }
        }
    }


    @Override
    public void onEnable() {}

    @Override
    public void onDisable() { mc.thePlayer.stepHeight = 0.6F; }
}
