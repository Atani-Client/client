package wtf.atani.security.checks.impl;

import wtf.atani.security.checks.manager.ProtectionManager;
import wtf.atani.security.checks.ProtectionCheck;
import wtf.atani.security.checks.enums.TriggerType;

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