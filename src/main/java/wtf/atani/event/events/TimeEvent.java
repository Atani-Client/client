package wtf.atani.event.events;

import wtf.atani.event.Event;

public class TimeEvent extends Event {
    private long time;

    public TimeEvent(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
