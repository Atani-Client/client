package wtf.atani.module.impl.movement;

import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "FastLadder", description = "Climb up ladders faster", category = Category.MOVEMENT)
public class FastLadder extends Module {
    private final SliderValue<Float> speed = new SliderValue<>("Speed", "High fast will the player climb?", this, 1.2F, 1F, 5F, 0);

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Listen
    public void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            if(mc.thePlayer.isOnLadder() && this.isMoving()) {
                mc.thePlayer.motionY = speed.getValue();
            }
        }
    }

}
