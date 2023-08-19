package tech.atani.client.protection.checks.manager;

import sun.misc.Unsafe;
import tech.atani.client.listener.event.events.minecraft.world.WorldLoadEvent;
import tech.atani.client.listener.event.handling.EventHandling;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.protection.checks.ProtectionCheck;
import tech.atani.client.protection.checks.enums.TriggerType;
import tech.atani.client.protection.checks.impl.*;
import tech.atani.client.utility.interfaces.ClientInformationAccess;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.List;

public final class ProtectionManager implements ClientInformationAccess {

    private static ProtectionManager instance;
    private List<String> jvmArguments;

    private ProtectionCheck[] checks;

    private Thread repetitiveHandlerThread;

    private boolean initialized;

    public void init() {
        EventHandling.getInstance().registerListener(this);
        try {
            this.jvmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

            this.checks = new ProtectionCheck[]{
                new ClassLoaderCheck(),
                    new HostsModCheck(),
                    new JVMArgCheck(),
                    new RepetetiveHandlerCheck(),
                    new SecurityManagerCheck()
            };
            this.run(TriggerType.INITIALIZE);

            this.repetitiveHandlerThread = new Thread(() -> {
                while (true) {
                    try {
                        run(TriggerType.REPETITIVE);
                        Thread.sleep(1000L);
                    } catch (final Throwable throwable) {
                        crash();
                    }
                }
            });

            this.repetitiveHandlerThread.start();

            if (this.initialized) {
                crash();
            } else {
                this.initialized = true;
            }

            this.run(TriggerType.POST_INITIALIZE);
        } catch (final Throwable ignored) {
            crash();
        }
    }

    @Listen
    public void onLoad(WorldLoadEvent worldLoadEvent) {
        this.run(TriggerType.JOIN);
    }

    public void run(final TriggerType trigger) {
        try {
            for (final ProtectionCheck module : checks) {
                if (module.getTrigger() == trigger && !(DEVELOPMENT_SWITCH && module.isExemptDev())) {
                    if (module.check()) {
                        System.out.println(module.check());
                        hang();
                    }
                }
            }
        } catch (final Throwable ignored) {
            crash();
        }
    }

    public void hang() {
        while (true) ;
    }

    public void crash() {
        for (; ; ) {
            try {
                final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);

                final Unsafe unsafe = (Unsafe) unsafeField.get(null);

                for (long address = 0; true; ++address) {
                    unsafe.setMemory(address, Long.MAX_VALUE, Byte.MIN_VALUE);
                }
            } catch (final Throwable t) {
                crash();
            }

            hang();
        }
    }

    public List<String> getJvmArguments() {
        return jvmArguments;
    }

    public ProtectionCheck[] getChecks() {
        return checks;
    }

    public Thread getRepetitiveHandlerThread() {
        return repetitiveHandlerThread;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public static ProtectionManager getInstance() {
        return instance;
    }

    public static void setInstance(ProtectionManager instance) {
        ProtectionManager.instance = instance;
    }
}
