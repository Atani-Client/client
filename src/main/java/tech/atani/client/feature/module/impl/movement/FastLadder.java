package tech.atani.client.feature.module.impl.movement;

import tech.atani.client.listener.event.events.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.feature.module.value.impl.SliderValue;

@ModuleData(name = "FastLadder", description = "Climb up ladders faster", category = Category.MOVEMENT)
public class FastLadder extends Module {
    private final SliderValue<Float> speed = new SliderValue<>("Speed", "High fast will the player climb?", this, 1.2F, 1F, 5F, 0);

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Listen
    public void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            if(Methods.mc.thePlayer.isOnLadder() && this.isMoving()) {
                Methods.mc.thePlayer.motionY = speed.getValue();
            }
        }
    }

}
