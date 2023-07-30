package wtf.atani.module.impl.misc;

import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "Timer", description = "Increases game speed", category = Category.MISCELLANEOUS)
public class Timer extends Module {
    private final SliderValue<Float> timerSpeed = new SliderValue<>("Time Speed", "How fast should the game speed be?", this, 1f, 0.1f, 5f, 1);

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        mc.timer.timerSpeed = timerSpeed.getValue();
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }

}