package tech.atani.event.impl.input;

import tech.atani.event.CancellableEvent;

public final class ChatInputEvent extends CancellableEvent {
    private String message;

    public ChatInputEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}