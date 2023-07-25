package wtf.atani.event.events;

import wtf.atani.event.Event;

public class KnockbackModifierEvent extends Event {
    private boolean flag;

    public KnockbackModifierEvent(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
