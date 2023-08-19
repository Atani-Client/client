package tech.atani.client.protection.checks.impl;

import tech.atani.client.protection.checks.enums.TriggerType;
import tech.atani.client.protection.checks.manager.ProtectionManager;
import tech.atani.client.protection.checks.ProtectionCheck;

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