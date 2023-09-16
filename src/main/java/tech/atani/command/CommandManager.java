package tech.atani.command;

import tech.atani.Client;
import tech.atani.event.IEventListener;
import tech.atani.event.annotations.SubscribeEvent;
import tech.atani.event.impl.input.ChatInputEvent;
import tech.atani.utils.misc.ChatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CommandManager extends ArrayList<Command> {

    public void init() {
        Client.INSTANCE.getEventBus().register(this);
    }

    @SubscribeEvent
    private final IEventListener<ChatInputEvent> onChatInput = e -> {
        String message = e.getMessage();

        if (!message.startsWith("."))
            return;

        message = message.substring(1);
        final String[] args = message.split(" ");

        final AtomicBoolean commandFound = new AtomicBoolean(false);

        try {
            this.stream().filter(cmd ->
                    Arrays.stream(cmd.getExpressions())
                            .anyMatch(exp -> exp.equalsIgnoreCase(args[0])))
                    .forEach(cmd -> {
                        commandFound.set(true);
                        cmd.execute(args);
                    });
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        if (!commandFound.get())
            ChatUtil.display("Not found.");

        e.setCancelled(true);
    };
}
