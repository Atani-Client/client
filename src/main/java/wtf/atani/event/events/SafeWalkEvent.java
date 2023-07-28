package wtf.atani.event.events;

import wtf.atani.event.Event;

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
