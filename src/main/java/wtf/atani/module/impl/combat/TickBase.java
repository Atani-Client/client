package wtf.atani.module.impl.combat;

import wtf.atani.event.events.TimeEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.combat.FightUtil;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "TickBase", description = "Shifts time in combat", category = Category.COMBAT)
public class TickBase extends Module {

    private SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the minimum delay between shifting?", this, 500L, 0L, 2000L, 1);
    private CheckBoxValue onlyWhenOutOfReach = new CheckBoxValue("Only When Out of Reach", "Shift only if the target is out of reach?", this, false);
    private SliderValue<Integer> chargeTicks = new SliderValue<>("Charge Ticks", "How many ticks will TickBase charge?", this, 5, 1, 80, 0);
    private SliderValue<Integer> minimumDischargeTicks = new SliderValue<>("Minimum Discharge Ticks", "How many ticks will TickBase discharge at minimum?", this, 5, 1, 80, 0);
    private SliderValue<Integer> maximumDischargeTicks = new SliderValue<>("Maximum Discharge Ticks", "How many ticks will TickBase discharge at maximum?", this, 5, 1, 80, 0);

    private long shifted, previousTime;
    private KillAura killAura;
    private TimeHelper delayTimer = new TimeHelper();

    @Listen
    public final void onTime(TimeEvent timeEvent) {
        if(shouldCharge()) {
            shifted += timeEvent.getTime() - previousTime;
            this.delayTimer.reset();
        }

        if(shouldDischarge()) {
            shifted = Math.max(0, shifted - maximumDischargeTicks.getValue().intValue() * 20);
        }

        previousTime = timeEvent.getTime();
        timeEvent.setTime(timeEvent.getTime() - shifted);
    }

    private boolean shouldCharge() {
        return  killAura.isEnabled() && killAura.curEntity != null && this.delayTimer.hasReached(delay.getValue().longValue()) && (!this.onlyWhenOutOfReach.getValue() || FightUtil.getRange(KillAura.curEntity) > 3.0);
    }

    private boolean shouldDischarge() {
        return this.shifted > this.minimumDischargeTicks.getValue() * 20;
    }

    @Override
    public void onEnable() {
        killAura = ModuleStorage.getInstance().getByClass(KillAura.class);
    }

    @Override
    public void onDisable() {

    }

}
