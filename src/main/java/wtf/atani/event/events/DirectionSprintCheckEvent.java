package wtf.atani.event.events;

import wtf.atani.event.Event;

public class DirectionSprintCheckEvent extends Event {
    boolean sprintCheck;

    public DirectionSprintCheckEvent(boolean sprintCheck) {
        this.sprintCheck = sprintCheck;
    }

    public boolean isSprintCheck() {
        return sprintCheck;
    }

    public void setSprintCheck(boolean sprintCheck) {
        this.sprintCheck = sprintCheck;
    }
}