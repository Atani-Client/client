package wtf.atani.module.impl.movement;

import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Spider", description = "Climb up walls", category = Category.MOVEMENT)
public class Spider extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"Jump"});
    private final CheckBoxValue jumpOnly = new CheckBoxValue("Jump Only", "Should the module only work when pressing the jump key?", this, false);

    @Override
    public String getSuffix() {
    	return mode.getValue();
    }
    
    @Listen
    public void onMotion(UpdateMotionEvent updateMotionEvent) {
        if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            if(jumpOnly.getValue() && !isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                return;
            }

            if(this.canClimbWall()) {
                switch (mode.getValue()) {
                case "Jump":
                    mc.thePlayer.jump();
                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    private boolean canClimbWall() {
        return mc.thePlayer != null && mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && mc.thePlayer.fallDistance < 1.0F;
    }

}
