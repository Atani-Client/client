package wtf.atani.file;

public class Processor {

    private final String name;
    private boolean running = true;

    public Processor(String name, boolean running) {
        this.name = name;
        this.running = running;
    }

    public String getName() {
        return name;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
