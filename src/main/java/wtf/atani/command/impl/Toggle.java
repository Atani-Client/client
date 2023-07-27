package wtf.atani.command.impl;

import wtf.atani.command.Command;
import wtf.atani.command.data.CommandInfo;
import wtf.atani.module.Module;
import wtf.atani.module.storage.ModuleStorage;

@CommandInfo(name = "toggle", aliases = {"t"}, description = "toggle a module")
public class Toggle extends Command {

    @Override
    public boolean execute(String[] args) {
        if(args.length == 1) {
            final Module module = ModuleStorage.getInstance().getModule(args[0]);
            if(module != null) {
                module.toggle();
                sendMessage((module.isEnabled() ? "§a" : "§c") + "Toggled §e" + module.getName());
            } else
                sendError("DOES NOT EXIST", "§aModule §l" + args[0] + " §anot found!");
        } else if (args.length == 0){
            sendHelp(this, "[Module]");
        } else {
            return false;
        }
        return true;
    }
}