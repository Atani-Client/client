package wtf.atani.event.events;

import wtf.atani.event.Event;
import wtf.atani.utils.interfaces.Methods;

public final class RayTraceRangeEvent extends Event implements Methods
{
    public float blockReachDistance;
    public float range;
    public float rayTraceRange;
    
    public RayTraceRangeEvent(final float range) {
        this.blockReachDistance = RayTraceRangeEvent.mc.playerController.getBlockReachDistance();
        this.range = range;
    }

    public float getBlockReachDistance() {
        return blockReachDistance;
    }

    public float getRange() {
        return range;
    }

    public float getRayTraceRange() {
        return rayTraceRange;
    }

    public void setBlockReachDistance(float blockReachDistance) {
        this.blockReachDistance = blockReachDistance;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setRayTraceRange(float rayTraceRange) {
        this.rayTraceRange = rayTraceRange;
    }
}
