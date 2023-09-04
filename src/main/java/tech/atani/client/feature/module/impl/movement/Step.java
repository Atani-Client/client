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
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Vanilla", "Intave", "NCP", "Motion", "Spartan", "WatchDog"});
    private final StringBoxValue ncpMode = new StringBoxValue("NCP Mode", "Which mode will the NCP mode use?", this, new String[]{"Normal", "Fast"});
    private final SliderValue<Integer> height = new SliderValue<Integer>("Height", "How high will the step go?", this, 2, 0, 10, 1, new Supplier[]{() -> mode.is("Vanilla")});

    // Intave
    private boolean timered;

    // NCP
    private boolean hasStepped;

    // WatchDog
    private boolean step;

    @Override
    public String getSuffix() {
    	return mode.getValue();
    }
    
    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            switch(mode.getValue()) {
                case "WatchDog":
                    if(Methods.mc.thePlayer.onGround && Methods.mc.thePlayer.isCollidedHorizontally) {
                        Methods.mc.thePlayer.jump();
                        MoveUtil.setMoveSpeed(0.43d);
                        step = true;
                    }

                    if(!Methods.mc.thePlayer.onGround && !Methods.mc.thePlayer.isCollidedHorizontally && step) {
                        Methods.mc.thePlayer.motionY = -0.078;
                        MoveUtil.setMoveSpeed(0.45d);
                        step = false;
                    }
                    break;
                case "Intave":
                    if(timered) {
                        mc.timer.timerSpeed = 1;
                        timered = false;
                    }

                    Methods.mc.thePlayer.stepHeight = 0.6F;

                    if(Methods.mc.thePlayer.onGround) {
                        hasStepped = false;
                    }

                    if(!hasStepped && this.isMoving() &&  Methods.mc.thePlayer.onGround && Methods.mc.thePlayer.isCollidedHorizontally) {
                        mc.thePlayer.jump();
                        hasStepped = true;
                    } else {
                        if (!Methods.mc.thePlayer.isCollidedHorizontally && hasStepped && this.isMoving()) {
                            mc.timer.timerSpeed = 1.4F;
                            mc.thePlayer.motionY -= 0.0035;
                            timered = true;
                            hasStepped = false;
                        }
                    }


                    break;
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
                        if(mc.thePlayer.moveForward > 0 && ncpMode.is("Fast")) {
                            MoveUtil.strafe(MoveUtil.getBaseMoveSpeed() + 0.197);
                        }
                    } else {
                        if (!Methods.mc.thePlayer.isCollidedHorizontally && hasStepped && this.isMoving()) {
                            hasStepped = false;
                            Methods.mc.thePlayer.motionY = 0;
                            if (Methods.mc.thePlayer.moveForward > 0) {
                                MoveUtil.setMoveSpeed(MoveUtil.getBaseMoveSpeed() + (ncpMode.is("Fast") ? 0.2 : 0));
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
    public void onEnable() {
        step = false;
    }

    @Override
    public void onDisable() {
        Methods.mc.thePlayer.stepHeight = 0.6F;
        step = false;
    }
}
