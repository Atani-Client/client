package wtf.atani.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import wtf.atani.event.events.AttackEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "TimerRange", description = "Module which mimics TickBase using timer (This is what people that can't make TickBase call 'TickBase')", category = Category.COMBAT)
public class TimerRange extends Module {

    SliderValue<Integer> ticksAmount = new SliderValue<>("Boost Ticks", "", this, 10, 3, 20, 0);
    SliderValue<Float> BoostAmount = new SliderValue<>("Boost Timer", "", this, 10f, 1f, 50f, 1);
    SliderValue<Float> ChargeAmount = new SliderValue<>("Charge Timer", "", this, 0.11f, 0.05f, 1f, 2);

    private int ticks = 0;

    @Listen
    public void onAttack(AttackEvent attackEvent) {
        if (attackEvent.getEntity() instanceof EntityLivingBase && ticks == 0) {
            ticks = ticksAmount.getValue();
        }
    }

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1f;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
    }

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        if (ticks == ticksAmount.getValue()) {
            mc.timer.timerSpeed = ChargeAmount.getValue();
            ticks--;
        } else if (ticks > 1) {
            mc.timer.timerSpeed = BoostAmount.getValue();
            ticks--;
        } else if (ticks == 1) {
            mc.timer.timerSpeed = 1f;
            ticks--;
        }
    }


}
