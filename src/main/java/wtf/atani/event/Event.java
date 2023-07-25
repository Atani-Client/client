package wtf.atani.event;

import wtf.atani.event.handling.EventHandling;

public class Event {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public <T extends Event> T onFire() {
        EventHandling.getInstance().publishEvent(this);
        return (T) this;
    }

}
