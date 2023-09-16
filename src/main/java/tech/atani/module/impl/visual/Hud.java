package tech.atani.module.impl.visual;

import tech.atani.Client;
import tech.atani.event.IEventListener;
import tech.atani.event.annotations.SubscribeEvent;
import tech.atani.event.impl.other.TickEvent;
import tech.atani.event.impl.render.Render2DEvent;
import tech.atani.module.Module;
import tech.atani.theme.Theme;
import tech.atani.value.impl.ModeValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

@Module.Info(name = "Hud", category = Module.Category.VISUAL, autoEnabled = true)
public class Hud extends Module {

    public ModeValue theme = new ModeValue("Theme", this, "Cosmic",
            "Cosmic", "Fiery", "Flawless", "Lightweight", "Moonada", "Pleasant", "Raspberry", "Violet", "Bloody");

    @SubscribeEvent
    private final IEventListener<TickEvent> onTick = e -> {
        for (Theme theme : Theme.values())
            if (theme.getThemeName().equals(this.theme.getMode()))
                Client.INSTANCE.getThemeManager().setTheme(theme);
    };

    @SubscribeEvent
    private final IEventListener<Render2DEvent> onRender2D = e -> {
        ScaledResolution sr = new ScaledResolution(mc);
    };
}
