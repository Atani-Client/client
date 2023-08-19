package tech.atani.client.listener.event.events.minecraft.player.movement;

import tech.atani.client.listener.event.Event;

public class SafeWalkEvent extends Event {
    boolean safe;

    public SafeWalkEvent(boolean safe) {
        this.safe = safe;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }
}
