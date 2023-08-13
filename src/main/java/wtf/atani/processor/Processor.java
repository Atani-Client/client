package wtf.atani.processor;

import wtf.atani.event.Event;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.processor.data.ProcessorInfo;
import wtf.atani.utils.interfaces.Methods;

public class Processor implements Methods {

    private final String name;
    private boolean running = false;

    public Processor() {
        ProcessorInfo processorInfo = this.getClass().getAnnotation(ProcessorInfo.class);
        if(processorInfo == null)
            throw new RuntimeException();
        this.name = processorInfo.name();
    }

    public void launch() {
        EventHandling.getInstance().registerListener(this);
        running = true;
    }

    public void end() {
        EventHandling.getInstance().unregisterListener(this);
        running = true;
    }

}
