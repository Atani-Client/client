package wtf.atani.module.impl.render;

import wtf.atani.event.events.EmitParticleEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "ParticleMultiplier", description = "Multiplies particles", category = Category.RENDER)
public class ParticleMultiplier extends Module {

    private final SliderValue<Integer> multiplier = new SliderValue<>("Multiplier", "What will be the multiplier for particles?", this, 1, 1, 10, 0);

    @Listen
    public void onParticle(EmitParticleEvent e) {
        e.multiplier = multiplier.getValue();
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}