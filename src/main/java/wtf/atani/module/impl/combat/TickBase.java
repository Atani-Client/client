package wtf.atani.module.impl.combat;

import wtf.atani.event.events.TickEvent;
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
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "TickBase", description = "Tick Base Manipulation", category = Category.COMBAT)
public class TickBase extends Module {

	private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"Normal", "Legit"});
	private final SliderValue<Integer> ticks = new SliderValue<Integer>("Ticks", "How many ticks will be tickbase charge?", this, 5, 0, 40, 0);
    private SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the minimum delay between shifting?", this, 500L, 0L, 2000L, 1);
    private CheckBoxValue onlyWhenOutOfReach = new CheckBoxValue("Only When Out of Reach", "Shift only if the target is out of reach?", this, false);
    
	private KillAura killAura;
    private long shifted, previousTime;

    private TimeHelper delayTimer = new TimeHelper();

    @Override
    public String getSuffix() {
    	return mode.getValue();
    }
    
	@Listen
	public void onTick(TickEvent tickEvent) {
		if(mode.is("Legit")) {
	        if(shouldCharge()) {
	        	try {
					Thread.sleep(ticks.getValue() * 20);
		            this.delayTimer.reset();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		}
	}
    
	@Listen
	public void onTime(TimeEvent timeEvent) {
		if(mode.is("Normal")) {
	        if(shouldCharge()) {
	            shifted += timeEvent.getTime() - previousTime;
	            this.delayTimer.reset();
	        }

	        if(shouldDischarge()) {
	            shifted = 0;
	        }

	        previousTime = timeEvent.getTime();
	        timeEvent.setTime(timeEvent.getTime() - shifted);
		}
	}
	
    private boolean shouldCharge() {
        return  killAura.isEnabled() && KillAura.curEntity != null && this.delayTimer.hasReached(delay.getValue().longValue()) && (!this.onlyWhenOutOfReach.getValue() || FightUtil.getRange(KillAura.curEntity) > 3.0);
    }

    private boolean shouldDischarge() {
        return this.shifted > this.ticks.getValue() * 20;
    }

	
    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
    	this.killAura = ModuleStorage.getInstance().getByClass(KillAura.class);
    }

}
