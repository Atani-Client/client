package tech.atani.client.protection.checks.impl;

import tech.atani.client.protection.checks.ProtectionCheck;
import tech.atani.client.protection.checks.enums.TriggerType;
import tech.atani.client.protection.checks.manager.ProtectionManager;

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