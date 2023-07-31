package wtf.atani.security.checks.impl;

import wtf.atani.security.checks.manager.ProtectionManager;
import wtf.atani.security.checks.ProtectionCheck;
import wtf.atani.security.checks.enums.TriggerType;
import wtf.atani.utils.os.OSUtil;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;

public final class HostsModCheck extends ProtectionCheck {

    private final File hostsFile;

    public HostsModCheck() {
        super(TriggerType.INITIALIZE, false);

        this.hostsFile = new File(OSUtil.getPlatform() == OSUtil.OS.WINDOWS
                ? System.getenv("WinDir") + "\\System32\\drivers\\etc\\hosts"
                : "/etc/hosts");
    }

    @Override
    public boolean check() throws Throwable {
        new Thread(() -> {
            try {
                for (; ; ) {
                    if (!this.hostsFile.exists() || !this.hostsFile.canRead() || !this.hostsFile.isFile()) {
                        ProtectionManager.getInstance().crash();
                    }

                    for (final String line : Files.readAllLines(this.hostsFile.toPath())) {
                        final String format = line.toLowerCase(Locale.ENGLISH).trim();

                        if (format.contains("github.com") || format.contains("githubusercontent.com") || format.contains("raw.githubusercontent.com")) {
                            ProtectionManager.getInstance().crash();
                        }
                    }

                    Thread.sleep(5000L);
                }
            } catch (final Throwable t) {
                ProtectionManager.getInstance().crash();
            }
        }).start();

        return false;
    }
}
