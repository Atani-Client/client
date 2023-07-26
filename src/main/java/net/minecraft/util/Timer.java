package net.minecraft.util;

import net.minecraft.client.Minecraft;
import wtf.atani.event.events.TimeEvent;

public class Timer
{
    /**
     * How many full ticks have turned over since the last call to updateTimer(), capped at 10.
     */
    public int elapsedTicks;
    public float partialTicks;
    public float field_194148_c;
    public float renderPartialTicks;

    /**
     * The time reported by the system clock at the last sync, in milliseconds
     */
    private long lastSyncSysClock;
    private float field_194149_e;
    public float timerSpeed;

    public Timer(float tps)
    {
        this.field_194149_e = 1000.0F / tps;
        this.lastSyncSysClock = Minecraft.getSystemTime();
        this.timerSpeed = 1.0F;
    }

    /**
     * Updates all fields of the Timer using the current time
     */
    public void updateTimer()
    {
        TimeEvent timeEvent = new TimeEvent(Minecraft.getSystemTime()).onFire();
        long i = timeEvent.getTime();
        this.field_194148_c = (float)(i - this.lastSyncSysClock) / this.field_194149_e * this.timerSpeed;
        this.lastSyncSysClock = i;
        this.partialTicks += this.field_194148_c;
        this.elapsedTicks = (int)this.partialTicks;
        this.partialTicks -= (float)this.elapsedTicks;
        this.renderPartialTicks = this.partialTicks;
    }
}