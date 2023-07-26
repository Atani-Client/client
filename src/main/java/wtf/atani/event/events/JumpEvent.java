package wtf.atani.event.events;

import wtf.atani.event.Event;

public final class JumpEvent
extends Event {
    private float yaw;

    public JumpEvent(float f) {
        this.yaw = f;
    }

    public final float getYaw() {
        return this.yaw;
    }

    public final void setYaw(float f) {
        this.yaw = f;
    }

}
