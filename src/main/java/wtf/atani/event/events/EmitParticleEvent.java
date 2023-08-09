package wtf.atani.event.events;

import net.minecraft.util.EnumParticleTypes;
import wtf.atani.event.Event;

public class EmitParticleEvent extends Event {
	public int multiplier;
	public EnumParticleTypes particleTypes;

	public EmitParticleEvent(int multiplier, EnumParticleTypes particleTypes) {
		this.multiplier = multiplier;
		this.particleTypes = particleTypes;
	}

	public EnumParticleTypes getParticleTypes() {
		return particleTypes;
	}

	public void setParticleTypes(EnumParticleTypes particleTypes) {
		this.particleTypes = particleTypes;
	}

}