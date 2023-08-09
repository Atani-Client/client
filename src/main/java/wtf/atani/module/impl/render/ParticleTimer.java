package wtf.atani.module.impl.render;

import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "ParticleTimer", description = "Increases particle speed", category = Category.RENDER)
public class ParticleTimer extends Module {
    private final SliderValue<Float> timerSpeed = new SliderValue<>("Time Speed", "How fast should the particle speed be?", this, 0.2f, 0.1f, 5f, 1);

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        mc.particleTimer.timerSpeed = timerSpeed.getValue();
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        mc.particleTimer.timerSpeed = 1F;
    }

}