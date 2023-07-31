package wtf.atani.module.impl.hud;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import wtf.atani.event.events.DisableModuleEvent;
import wtf.atani.event.events.EnableModuleEvent;
import wtf.atani.event.events.Render2DEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.math.atomic.AtomicFloat;
import wtf.atani.utils.render.GradientUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.animation.Direction;
import wtf.atani.utils.render.animation.impl.DecelerateAnimation;
import wtf.atani.utils.render.shader.render.ingame.RenderableShaders;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;
import java.util.*;

@ModuleInfo(name = "ClientOverlay", description = "A nice little overlay that shows you info about the client", category = Category.HUD)
public class ClientOverlay extends Module {

    private StringBoxValue watermarkMode = new StringBoxValue("Watermark Mode", "Which watermark will be displayed?", this, new String[]{"None", "Simple", "Golden"});
    private StringBoxValue moduleListMode = new StringBoxValue("Module List Mode", "Which module list will be displayed?", this, new String[]{"None", "Simple", "Golden"});

    private LinkedHashMap<Module, DecelerateAnimation> moduleHashMap = new LinkedHashMap<>();

    @Listen
    public final void on2D(Render2DEvent render2DEvent) {
        String text = CLIENT_NAME + " v" + VERSION + " | " + mc.getDebugFPS() + " fps";
        // When making a watermark update these values (including the margins!) so that we can have arraylist and watermark on the same side without
        // having 100 if else statements
        // - Tabio
        AtomicFloat leftY = new AtomicFloat(0);
        AtomicFloat rightY = new AtomicFloat(0);
        switch (watermarkMode.getValue()) {
            case "Simple":
                RenderableShaders.renderAndRun(() -> {
                    FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
                    float length = roboto17.getStringWidth(text);
                    float rectX = 10, rectY = 10;
                    float textX = rectX + 4, textY = rectY + 4.5f;
                    float rectWidth = 8 + length, rectHeight = roboto17.FONT_HEIGHT + 8;
                    RenderUtil.drawRect(rectX, rectY, rectWidth, rectHeight, new Color(0, 0, 0, 180).getRGB());
                    roboto17.drawStringWithShadow(text, textX, textY, -1);
                    leftY.set(rectY + rectHeight + 10);
                });
                break;
            case "Golden":
                RenderableShaders.renderAndRun(() -> {
                    FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
                    float length = roboto17.getStringWidth(text);
                    float x = 5 + 2, y = 5, lineHeight = 2;
                    GradientUtil.drawGradientLR(x, y, length + 5, lineHeight, 1, new Color(255, 202, 3), new Color(255, 84, 3));
                    RenderUtil.drawRect(x - 2, y, 2, roboto17.FONT_HEIGHT + 4, new Color(255, 202, 3).getRGB());
                    RenderUtil.drawRect(x, y + lineHeight, length + 5, roboto17.FONT_HEIGHT + 4 - lineHeight, new Color(20, 20, 20).getRGB());
                    roboto17.drawStringWithShadow(text, x + 2.5f, y + lineHeight + 2, -1);
                    leftY.set(y + lineHeight + roboto17.FONT_HEIGHT + 4 + y);
                });
                break;
        }
        for(Module module : ModuleStorage.getInstance().getList()) {
            if(!moduleHashMap.containsKey(module))
                moduleHashMap.put(module, new DecelerateAnimation(200, 1, module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS));
        }

        LinkedHashMap<Module, DecelerateAnimation> sortedMap = new LinkedHashMap<>();
        ArrayList<Module> keys = new ArrayList(moduleHashMap.keySet());
        Collections.sort(keys, new Comparator<Module>() {
            public int compare(Module mod1, Module mod2) {
                FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
                String name1 = mod1.getName();
                String name2 = mod2.getName();
                return roboto17.getStringWidth(name2) - roboto17.getStringWidth(name1);
            }
        });
        for (Module module : keys) {
            sortedMap.put(module, moduleHashMap.get(module));
        }
        moduleHashMap = sortedMap;

        switch (moduleListMode.getValue()) {
            case "Golden": {
                if(leftY.get() == 0)
                    leftY.set(6);
                FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
                RenderableShaders.renderAndRun(() -> {
                    float moduleY = leftY.get();
                    RenderUtil.startScissorBox();
                    RenderUtil.drawScissorBox(7, 0, render2DEvent.getScaledResolution().getScaledWidth() - 7, render2DEvent.getScaledResolution().getScaledHeight());
                    for (Module module : moduleHashMap.keySet()) {
                        float moduleHeight = roboto17.FONT_HEIGHT + 4;
                        if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                            String name = module.getName();
                            float rectWidth = roboto17.getStringWidth(name) + 5;
                            float moduleX = 7 - (rectWidth + 7) + (float) (moduleHashMap.get(module).getOutput() * (rectWidth + 7));
                            RenderUtil.drawRect(moduleX, moduleY, rectWidth, moduleHeight, new Color(20, 20, 20).getRGB());
                            roboto17.drawTotalCenteredStringWithShadow(name, moduleX + rectWidth / 2, moduleY + moduleHeight / 2 + 0.5f, -1);
                            moduleY += moduleHeight;
                        }
                    }
                    RenderUtil.endScissorBox();
                    GradientUtil.drawGradientTB(5, leftY.get(), 2, moduleY - leftY.get(), 1, new Color(255, 202, 3), new Color(255, 84, 3));
                });
                break;
            }
            case "Simple":
                FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
                RenderableShaders.renderAndRun(() -> {
                    if(leftY.get() == 0)
                        leftY.set(8);
                    float moduleY = leftY.get();
                    for(Module module : moduleHashMap.keySet()) {
                        float moduleHeight = roboto17.FONT_HEIGHT + 8;
                        if(!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                            String name = module.getName();
                            float rectWidth = roboto17.getStringWidth(name) + 10;
                            float moduleX = 10 - (rectWidth + 10) + (float) (moduleHashMap.get(module).getOutput() * (rectWidth + 10));
                            RenderUtil.drawRect(moduleX, moduleY, rectWidth, moduleHeight, new Color(0, 0, 0, 180).getRGB());
                            roboto17.drawTotalCenteredStringWithShadow(name, moduleX + rectWidth / 2, moduleY + moduleHeight / 2 + 0.5f, -1);
                            moduleY += moduleHeight;
                        }
                    }
                });
                break;
        }
    }

    @Listen
    public final void onModuleEnable(EnableModuleEvent enableModuleEvent) {
        if(enableModuleEvent.getType() == EnableModuleEvent.Type.PRE)
            return;
        if(this.moduleHashMap.containsKey(enableModuleEvent.getModule())) {
            this.moduleHashMap.get(enableModuleEvent.getModule()).setDirection(Direction.FORWARDS);
        } else {
            this.moduleHashMap.put(enableModuleEvent.getModule(), new DecelerateAnimation(200, 1, Direction.FORWARDS));
        }
    }

    @Listen
    public final void onModuleDisable(DisableModuleEvent disableModuleEvent) {
        if(disableModuleEvent.getType() == DisableModuleEvent.Type.PRE)
            return;
        if(this.moduleHashMap.containsKey(disableModuleEvent.getModule())) {
            this.moduleHashMap.get(disableModuleEvent.getModule()).setDirection(Direction.BACKWARDS);
        } else {
            this.moduleHashMap.put(disableModuleEvent.getModule(), new DecelerateAnimation(200, 1, Direction.BACKWARDS));
        }
    }


    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
