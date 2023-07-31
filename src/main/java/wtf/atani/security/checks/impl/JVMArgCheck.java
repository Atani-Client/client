package wtf.atani.security.checks.impl;

import wtf.atani.security.checks.manager.ProtectionManager;
import wtf.atani.security.checks.ProtectionCheck;
import wtf.atani.security.checks.enums.TriggerType;

import java.util.List;

public final class JVMArgCheck extends ProtectionCheck {

    public JVMArgCheck() {
        super(TriggerType.INITIALIZE, true);
    }

    @Override
    public boolean check() {
        final List<String> arguments = ProtectionManager.getInstance().getJvmArguments();

        final boolean malicious = arguments.stream().anyMatch(s -> s.contains("javaagent")
                || s.contains("agentlib") || s.contains("Xdebug")
                || s.contains("Xrunjdwp:") || s.contains("noverify")
        );
        final boolean required = arguments.contains("-XX:+DisableAttachMechanism");

        return malicious || !required;
    }
}