package wtf.atani.module.impl.combat;

import com.sun.org.apache.xpath.internal.operations.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.ServerStatusResponse;
import org.lwjgl.Sys;
import wtf.atani.event.events.TickEvent;
import wtf.atani.event.events.TimeEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.combat.FightUtil;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "TickBase", description = "Tick Base Manipulation", category = Category.COMBAT)
public class TickBase extends Module {

	public final SliderValue<Long> maxBalance = new SliderValue<>("Max Balance", "What will be the maximum balance?", this, 100L, 0L, 5000L, 0);
	public final SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the delay between shifting?", this, 300l, 0l, 1000l, 0);
	public final SliderValue<Float> range = new SliderValue<>("Range", "At what range will the module operate?", this, 3f, 0.1f,7f, 1);

	private KillAura killAura;
	private long shifted, previousTime;
	private TimeHelper timeHelper = new TimeHelper();

	@Listen
	public void onTime(TimeEvent timeEvent) {
		if(killAura == null)
			killAura = ModuleStorage.getInstance().getByClass(KillAura.class);

		if(shouldCharge() && this.timeHelper.hasReached(delay.getValue())) {
			shifted += timeEvent.getTime() - previousTime;
		}

		if(shouldDischarge()) {
			shifted = 0;
			this.timeHelper.reset();
		}

		previousTime = timeEvent.getTime();
		timeEvent.setTime(timeEvent.getTime() - shifted);
	};

	private boolean shouldCharge() {
		return killAura.isEnabled() && KillAura.curEntity != null && this.shifted < maxBalance.getValue();
	}

	private boolean shouldDischarge() {
		return this.shifted >= this.maxBalance.getValue() && killAura.isEnabled() && killAura.curEntity != null && FightUtil.getRange(KillAura.curEntity) > range.getValue();
	}

	@Override
	public void onDisable() {
		this.shifted = 0;
	}

	@Override
	public void onEnable() {
		this.shifted = 0;
		this.previousTime = (System.nanoTime() / 1000000L) / 1000L;
	}

}
