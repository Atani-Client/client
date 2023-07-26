package wtf.atani.event.events;

import net.minecraft.client.gui.ScaledResolution;
import wtf.atani.event.Event;

public class Render2DEvent extends Event {
    private final ScaledResolution scaledResolution;

    public Render2DEvent(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}
