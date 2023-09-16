package tech.atani.event.impl.render;

import tech.atani.event.Event;

public class RenderTickEvent implements Event {
    private final float ticks;

    public RenderTickEvent(float ticks) {
        this.ticks = ticks;
    }

    public float getTicks() {
        return ticks;
    }
}
