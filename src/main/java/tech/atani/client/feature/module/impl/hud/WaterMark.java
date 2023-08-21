package tech.atani.client.feature.module.impl.hud;

import com.google.common.base.Supplier;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import tech.atani.client.feature.module.value.Value;
import tech.atani.client.feature.module.value.interfaces.ValueChangeListener;
import tech.atani.client.feature.theme.data.enums.ElementType;
import tech.atani.client.feature.theme.storage.ThemeStorage;
import tech.atani.client.listener.event.minecraft.render.Render2DEvent;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.module.impl.hud.clientOverlay.IClientOverlayComponent;
import tech.atani.client.utility.interfaces.ColorPalette;
import tech.atani.client.utility.java.StringUtil;
import tech.atani.client.utility.math.atomic.AtomicFloat;
import tech.atani.client.utility.render.color.ColorUtil;
import tech.atani.client.utility.render.shader.shaders.GradientShader;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.shader.render.ingame.RenderableShaders;
import tech.atani.client.utility.render.shader.shaders.RoundedShader;
import tech.atani.client.feature.module.value.impl.SliderValue;
import tech.atani.client.feature.module.value.impl.StringBoxValue;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

@ModuleData(name = "WaterMark", description = "A nice little overlay that shows you info about the client", category = Category.HUD)
public class WaterMark extends Module implements ColorPalette, IClientOverlayComponent {

    private StringBoxValue watermarkMode = new StringBoxValue("Watermark Mode", "Which watermark will be displayed?", this, new String[]{"None", "Modern", "Simple", "Golden", "Augustus 2.6", "Xave", "Ryu", "Icarus", "Fatality"}, new ValueChangeListener[]{new ValueChangeListener() {
        @Override
        public void onChange(Stage stage, Value value, Object oldValue, Object newValue) {
            if(stage == Stage.POST) {
                if(oldValue != null && !((String) oldValue).equalsIgnoreCase("None"))
                    ThemeStorage.getInstance().getThemeObject(((String) oldValue), ElementType.WATERMARK).onDisable();
                if(newValue != null && !((String) newValue).equalsIgnoreCase("None"))
                    ThemeStorage.getInstance().getThemeObject(((String) newValue), ElementType.WATERMARK).onEnable();
            }
        }
    }});

    @Override
    public void draw(Render2DEvent render2DEvent, AtomicFloat leftY, AtomicFloat rightY) {
        if(this.isEnabled() && !this.watermarkMode.getValue().equalsIgnoreCase("None")) {
            ThemeStorage.getInstance().getThemeObject(watermarkMode.getValue(), ElementType.WATERMARK).onDraw(render2DEvent.getScaledResolution(), render2DEvent.getPartialTicks(), leftY, rightY);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
