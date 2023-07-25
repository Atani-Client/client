package wtf.atani.event.handling;

import wtf.atani.event.Event;
import wtf.atani.event.radbus.PubSub;

public class EventHandling {

    // Modified version of radbus from nevalackin on github
    private final PubSub<Event> eventPubSub = PubSub.newInstance(System.err::println);
    private static EventHandling instance;

    public EventHandling() {
        instance = this;
    }

    public void publishEvent(Event event) {
        eventPubSub.publish(event);
    }

    public void registerListener(Object object) {
        eventPubSub.subscribe(object);
    }

    public void unregisterListener(Object object) {
        eventPubSub.unsubscribe(object);
    }

    public static EventHandling getInstance() {
        return instance;
    }
}
