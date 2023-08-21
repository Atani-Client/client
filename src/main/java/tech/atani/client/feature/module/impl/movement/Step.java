package tech.atani.client.feature.module.impl.movement;

import com.google.common.base.Supplier;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.player.movement.MoveUtil;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;

@ModuleData(name = "Step", description = "Makes you walk up blocks.", category = Category.MOVEMENT)
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
                    Methods.mc.thePlayer.stepHeight = height.getValue();
                    break;
                case "NCP":
                    Methods.mc.thePlayer.stepHeight = 0.6F;

                    if(Methods.mc.thePlayer.onGround) {
                        hasStepped = false;
                    }

                    if(!hasStepped && this.isMoving() &&  Methods.mc.thePlayer.onGround && Methods.mc.thePlayer.isCollidedHorizontally) {
                        Methods.mc.thePlayer.jump();
                        hasStepped = true;
                    } else {
                        if (!Methods.mc.thePlayer.isCollidedHorizontally && hasStepped && this.isMoving()) {
                            hasStepped = false;
                            Methods.mc.thePlayer.motionY = 0;
                            if (Methods.mc.thePlayer.moveForward > 0) {
                                MoveUtil.setMoveSpeed(MoveUtil.getBaseMoveSpeed());
                            }
                        }
                    }
                    break;
                case "Motion":
                    Methods.mc.thePlayer.stepHeight = 0.6F;

                    if(Methods.mc.thePlayer.isCollidedHorizontally && Methods.mc.thePlayer.onGround) {
                        Methods.mc.thePlayer.motionY = .39;
                    }
                    break;
                case "Spartan":
                    Methods.mc.thePlayer.stepHeight = 1;
                    break;
            }
        }
    }


    @Override
    public void onEnable() {}

    @Override
    public void onDisable() { Methods.mc.thePlayer.stepHeight = 0.6F; }
}
