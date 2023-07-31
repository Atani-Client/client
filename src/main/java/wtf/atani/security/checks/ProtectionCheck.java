package wtf.atani.security.checks;

import wtf.atani.security.checks.enums.TriggerType;

public abstract class ProtectionCheck {

    private final TriggerType trigger;
    private final boolean exemptDev;

    public ProtectionCheck(TriggerType trigger, boolean exemptDev) {
        this.trigger = trigger;
        this.exemptDev = exemptDev;
    }

    public TriggerType getTrigger() {
        return trigger;
    }

    public boolean isExemptDev() {
        return exemptDev;
    }

    public abstract boolean check() throws Throwable;
}