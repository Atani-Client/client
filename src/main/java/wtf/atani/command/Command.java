package wtf.atani.command;

import wtf.atani.command.data.CommandInfo;
import wtf.atani.utils.interfaces.Methods;

public abstract class Command implements Methods {

    final String name, description;
    final String[] aliases;

    public Command() {
        final CommandInfo info = getClass().getAnnotation(CommandInfo.class);
        name = info.name();
        description = info.description();
        aliases = info.aliases().length != 0 ? info.aliases() : new String[] {name};
    }

    public abstract boolean execute(String[] args);

    public String getPrefix() {
        return ".";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }
}
