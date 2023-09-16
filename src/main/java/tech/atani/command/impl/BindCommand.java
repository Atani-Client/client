package tech.atani.command.impl;

import org.lwjglx.input.Keyboard;
import tech.atani.Client;
import tech.atani.command.Command;
import tech.atani.module.Module;
import tech.atani.utils.misc.ChatUtil;

public final class BindCommand extends Command {
    public BindCommand() {
        super("bind", "keybind");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 3) {
            final Module module = Client.INSTANCE.getModuleManager().get(args[1]);

            if (module == null) {
                ChatUtil.display("Could not find the module");
                return;
            }

            final String input = args[2].toUpperCase();
            final int code = Keyboard.getKeyIndex(input);

            module.setKeyBind(code);
            ChatUtil.display("Bound " + module.getName() + " to " + Keyboard.getKeyName(code) + ".");
        } else {
            ChatUtil.display("Invalid arguments");
        }
    }
}
