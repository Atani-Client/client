package tech.atani.event;

@FunctionalInterface
public interface IEventListener<Event> {
    void call(Event event);
}