package wtf.atani.module.impl.render;

import net.minecraft.util.EnumParticleTypes;
import wtf.atani.event.events.AttackEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "AttackParticles", description = "Spawns particles after attacking someone", category = Category.RENDER)
public class AttackParticles extends Module {

    public SliderValue<Integer> crit = new SliderValue<>("Critical", "How many critical particles to spawn?", this, 0, 0, 20, 0);
    public SliderValue<Integer> critMagic = new SliderValue<>("Critical Magic", "How many critical magic particles to spawn?", this, 0, 0, 20, 0);

    @Listen
    public void onAttack(AttackEvent attackEvent) {
        for (int i = 0; i < this.crit.getValue(); ++i) {
            mc.effectRenderer.emitParticleAtEntity(attackEvent.getEntity(), EnumParticleTypes.CRIT);
        }
        for (int i = 0; i < this.critMagic.getValue(); ++i) {
            mc.effectRenderer.emitParticleAtEntity(attackEvent.getEntity(), EnumParticleTypes.CRIT_MAGIC);
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
