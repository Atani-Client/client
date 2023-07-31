package wtf.atani.security.checks.impl;

import wtf.atani.security.checks.manager.ProtectionManager;
import wtf.atani.security.checks.ProtectionCheck;
import wtf.atani.security.checks.enums.TriggerType;

public final class SecurityManagerCheck extends ProtectionCheck {

    public SecurityManagerCheck() {
        super(TriggerType.REPETITIVE, false);

        // Make an attempt at removing the security manager if one is present for some reason.
        System.setSecurityManager(null);
    }

    @Override
    public boolean check() {
        if (System.getSecurityManager() != null) {
            ProtectionManager.getInstance().crash();
            return true;
        }

        return false;
    }
}