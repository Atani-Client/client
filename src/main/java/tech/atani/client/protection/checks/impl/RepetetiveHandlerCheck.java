package tech.atani.client.protection.checks.impl;

import tech.atani.client.protection.checks.enums.TriggerType;
import tech.atani.client.protection.checks.manager.ProtectionManager;
import tech.atani.client.protection.checks.ProtectionCheck;

public class RepetetiveHandlerCheck extends ProtectionCheck {

    public RepetetiveHandlerCheck() {
        super(TriggerType.JOIN, false);
    }

    @Override
    public boolean check() throws Throwable {
        // Every time we join a world, check whether the repetitive
        // check handler thread has died, if it has, immediately crash the client.
        // I would do this on a repetitive trigger but... well, you know.
        final Thread repetitiveHandlerThread = ProtectionManager.getInstance().getRepetitiveHandlerThread();

        if (!repetitiveHandlerThread.isAlive() || repetitiveHandlerThread.isInterrupted()) {
            ProtectionManager.getInstance().crash();
            return true;
        }

        return false;
    }
}