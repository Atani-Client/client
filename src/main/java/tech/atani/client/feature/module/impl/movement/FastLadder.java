package tech.atani.client.feature.module.impl.movement;

import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.feature.value.impl.SliderValue;

@ModuleData(name = "FastLadder", description = "Climb up ladders faster", category = Category.MOVEMENT)
public class FastLadder extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Normal", "Clip"});
    private final SliderValue<Float> speed = new SliderValue<Float>("Speed", "How fast will the player climb?", this, 1.2f, 1f, 5f, 0);

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Listen
    public void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            if(Methods.mc.thePlayer.isOnLadder() && this.isMoving()) {
                switch (mode.getValue()) {
                    case "Normal":
                        Methods.mc.thePlayer.motionY = speed.getValue();
                        break;
                    case "Clip":
                        // 0.25 so that it isnt too fast.
                        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + speed.getValue() / 0.25, mc.thePlayer.posZ);
                        break;
                }
            }
        }
    }

}
