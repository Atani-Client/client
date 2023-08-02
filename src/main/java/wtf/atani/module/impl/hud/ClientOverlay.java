package wtf.atani.module.impl.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.StringUtils;
import tv.twitch.chat.Chat;
import wtf.atani.event.events.DisableModuleEvent;
import wtf.atani.event.events.EnableModuleEvent;
import wtf.atani.event.events.Render2DEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.java.StringUtil;
import wtf.atani.utils.math.atomic.AtomicFloat;
import wtf.atani.utils.render.GradientUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.RoundedUtil;
import wtf.atani.utils.render.animation.Direction;
import wtf.atani.utils.render.animation.impl.DecelerateAnimation;
import wtf.atani.utils.render.shader.render.ingame.RenderableShaders;
import wtf.atani.value.Value;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.interfaces.ValueChangeListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

@ModuleInfo(name = "ClientOverlay", description = "A nice little overlay that shows you info about the client", category = Category.HUD)
public class ClientOverlay extends Module {

    private StringBoxValue watermarkMode = new StringBoxValue("Watermark Mode", "Which watermark will be displayed?", this, new String[]{"None", "Simple", "Golden", "Augustus 2.6", "Ryu", "Icarus"});
    private StringBoxValue moduleListMode = new StringBoxValue("Module List Mode", "Which module list will be displayed?", this, new String[]{"None", "Simple", "Golden", "Augustus 2.6", "Ryu", "Icarus"}, new ValueChangeListener[]{new ValueChangeListener() {
        @Override
        public void onChange(Stage stage, Value value, Object oldValue, Object newValue) {
            moduleHashMap.clear();
        }
    }});

    private LinkedHashMap<Module, DecelerateAnimation> moduleHashMap = new LinkedHashMap<>();

    @Listen
    public final void on2D(Render2DEvent render2DEvent) {
        ScaledResolution sr = render2DEvent.getScaledResolution();

        // When making a watermark update these values (including the margins!) so that we can have arraylist and watermark on the same side without
        // having 100 if else statements
        // - Tabio
        AtomicFloat leftY = new AtomicFloat(0);
        AtomicFloat rightY = new AtomicFloat(0);
        switch (watermarkMode.getValue()) {
            case "Icarus": {
                FontRenderer fontRenderer = FontStorage.getInstance().findFont("Pangram Bold", 80);
                fontRenderer.drawStringWithShadow(CLIENT_NAME, 8, 0, -1);
                leftY.set(fontRenderer.FONT_HEIGHT + 8);
                break;
            }
            case "Ryu": {
                FontRenderer fontRenderer = FontStorage.getInstance().findFont("Roboto Medium", 17);
                String text = CLIENT_NAME + " " + ChatFormatting.GRAY + " # " + ChatFormatting.WHITE + " " + mc.getDebugFPS() + "fps";
                // Rendering bloom on text is really performance fucking due to all the geometry around letter so we're trying to not hurt it as much
                // by disabling the blur shader
                RenderableShaders.renderAndRun(true, false, () -> {
                    // Rendering with the regular version of the font to fix a weird bug with the medium font where it looks shitty
                    FontStorage.getInstance().findFont("Roboto", 17).drawString(StringUtil.removeFormattingCodes(text), 3, 3, new Color(0, 0, 0).getRGB());
                });
                fontRenderer.drawString(text, 3, 3, new Color(81, 81, 255).getRGB());
                leftY.set(fontRenderer.FONT_HEIGHT + 6);
                break;
            }
            case "Augustus 2.6": {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Date now = new Date();
                String time = dateFormat.format(now);
                String text = String.format("%s b%s" + ChatFormatting.GRAY + " (%s)", CLIENT_NAME, VERSION, time);
                RenderUtil.drawRect(0, 0, 2 + mc.fontRendererObj.getStringWidth(text), 2 + mc.fontRendererObj.FONT_HEIGHT, new Color(0, 0, 0, 100).getRGB());
                mc.fontRendererObj.drawStringWithShadow(text, 1, 2, -1);
                leftY.set(2 + mc.fontRendererObj.FONT_HEIGHT);
            }
            break;
            case "Simple":
                RenderableShaders.renderAndRun(() -> {
                    String text = CLIENT_NAME + " v" + VERSION + " | " + mc.getDebugFPS() + " fps";
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
                    String text = CLIENT_NAME + " v" + VERSION + " | " + mc.getDebugFPS() + " fps";
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
            if(!moduleHashMap.containsKey(module)) {
                switch (this.moduleListMode.getValue()) {
                    case "Augustus 2.6":
                        moduleHashMap.put(module, new DecelerateAnimation(1, 1, module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS));
                        break;
                    default:
                        moduleHashMap.put(module, new DecelerateAnimation(200, 1, module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS));
                        break;
                }
            }
        }

        LinkedHashMap<Module, DecelerateAnimation> sortedMap = new LinkedHashMap<>();
        ArrayList<Module> keys = new ArrayList(moduleHashMap.keySet());
        Collections.sort(keys, new Comparator<Module>() {
            public int compare(Module mod1, Module mod2) {
                FontRenderer fontRenderer;
                switch (moduleListMode.getValue()) {
                    case "Icarus":
                        fontRenderer = FontStorage.getInstance().findFont("Pangram Regular", 17);
                        break;
                    case "Augustus 2.6":
                        fontRenderer = mc.fontRendererObj;
                        break;
                    default:
                        fontRenderer = FontStorage.getInstance().findFont("Roboto", 17);
                        break;
                }
                String name1 = mod1.getName();
                String name2 = mod2.getName();
                return fontRenderer.getStringWidth(name2) - fontRenderer.getStringWidth(name1);
            }
        });
        for (Module module : keys) {
            sortedMap.put(module, moduleHashMap.get(module));
        }
        moduleHashMap = sortedMap;

        switch (moduleListMode.getValue()) {
            case "Icarus": {
                FontRenderer fontRenderer = FontStorage.getInstance().findFont("Pangram Regular", 17);
                float moduleY = rightY.get();
                float gradientWidth = 1f;
                for (Module module : moduleHashMap.keySet()) {
                    if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                        float moduleHeight = fontRenderer.FONT_HEIGHT + 4;
                        float rectLength = (float) ((fontRenderer.getStringWidth(module.getName() + 3) * moduleHashMap.get(module).getOutput()) - gradientWidth);
                        RoundedUtil.drawRound(sr.getScaledWidth() - rectLength, moduleY, rectLength + 20, moduleHeight, 2, new Color(21, 21, 21));
                        fontRenderer.drawString(module.getName(), sr.getScaledWidth() - rectLength + 1.5f, moduleY + moduleHeight / 2 - fontRenderer.FONT_HEIGHT / 2, -1);
                        // The 20 is there so the rect goes out of the screen and therefore the right part's not rounded
                        moduleY += moduleHeight;
                    }
                }
                GradientUtil.drawGradientTB(sr.getScaledWidth() - gradientWidth, rightY.get(), gradientWidth, moduleY, 1, new Color(255, 0, 127), new Color(127, 0, 255));
                break;
            }
            case "Augustus 2.6": {
                if(rightY.get() == 0)
                    rightY.set(1);
                FontRenderer fontRenderer = mc.fontRendererObj;
                float moduleY = rightY.get();
                for (Module module : moduleHashMap.keySet()) {
                    if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                        float moduleHeight = fontRenderer.FONT_HEIGHT + 2;
                        float rectLength = (float) ((fontRenderer.getStringWidth(module.getName()) + 1) * moduleHashMap.get(module).getOutput());
                        RenderUtil.drawRect(sr.getScaledWidth() - rectLength, moduleY, rectLength, moduleHeight, new Color(0, 0, 0, 100).getRGB());
                        fontRenderer.drawStringWithShadow(module.getName(), sr.getScaledWidth() - rectLength + 0.5f, moduleY + moduleHeight / 2 - fontRenderer.FONT_HEIGHT / 2, new Color(0, 0, 255).getRGB());
                        // The 20 is there so the rect goes out of the screen and therefore the right part's not rounded
                        moduleY += moduleHeight;
                    }
                }
                break;
            }
            case "Ryu": {
                if (leftY.get() == 0)
                    leftY.set(6);
                FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto Medium", 17);
                // This is disgusting
                RenderableShaders.renderAndRun(false, true, () -> {
                    float moduleY = leftY.get();
                    for (Module module : moduleHashMap.keySet()) {
                        float moduleHeight = roboto17.FONT_HEIGHT + 3;
                        if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                            String name = module.getName();
                            float rectWidth = (roboto17.getStringWidth(name) + 4);
                            float moduleX = 2 - rectWidth + (float) (moduleHashMap.get(module).getOutput() * rectWidth);
                            RenderUtil.drawRect(moduleX, moduleY, rectWidth, moduleHeight, new Color(0, 0, 0, 80).getRGB());
                            moduleY += moduleHeight;
                        }
                    }
                });
                RenderableShaders.renderAndRun(true, false, () -> {
                    float moduleY = leftY.get();
                    for (Module module : moduleHashMap.keySet()) {
                        float moduleHeight = roboto17.FONT_HEIGHT + 3;
                        if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                            String name = module.getName();
                            float rectWidth = (roboto17.getStringWidth(name) + 4);
                            float moduleX = 2 - rectWidth + (float) (moduleHashMap.get(module).getOutput() * rectWidth);
                            FontStorage.getInstance().findFont("Roboto", 17).drawTotalCenteredString(name, moduleX + rectWidth / 2, moduleY + moduleHeight / 2 + 0.5f, new Color(0, 0, 0).getRGB());
                            moduleY += moduleHeight;
                        }
                    }
                });
                float moduleY = leftY.get();
                for (Module module : moduleHashMap.keySet()) {
                    float moduleHeight = roboto17.FONT_HEIGHT + 3;
                    if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                        String name = module.getName();
                        float rectWidth = (roboto17.getStringWidth(name) + 4);
                        float moduleX = 2 - rectWidth + (float) (moduleHashMap.get(module).getOutput() * rectWidth);
                        roboto17.drawTotalCenteredString(name, moduleX + rectWidth / 2, moduleY + moduleHeight / 2 + 0.5f, -1);
                        moduleY += moduleHeight;
                    }
                }
                break;
            }
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
            switch (this.moduleListMode.getValue()) {
                case "Augustus 2.6":
                    moduleHashMap.put(enableModuleEvent.getModule(), new DecelerateAnimation(1, 1, Direction.FORWARDS));
                    break;
                default:
                    moduleHashMap.put(enableModuleEvent.getModule(), new DecelerateAnimation(200, 1,Direction.FORWARDS));
                    break;
            }
        }
    }

    @Listen
    public final void onModuleDisable(DisableModuleEvent disableModuleEvent) {
        if(disableModuleEvent.getType() == DisableModuleEvent.Type.PRE)
            return;
        if(this.moduleHashMap.containsKey(disableModuleEvent.getModule())) {
            this.moduleHashMap.get(disableModuleEvent.getModule()).setDirection(Direction.BACKWARDS);
        } else {
            switch (this.moduleListMode.getValue()) {
                case "Augustus 2.6":
                    moduleHashMap.put(disableModuleEvent.getModule(), new DecelerateAnimation(1, 1, Direction.BACKWARDS));
                    break;
                default:
                    moduleHashMap.put(disableModuleEvent.getModule(), new DecelerateAnimation(200, 1,Direction.BACKWARDS));
                    break;
            }
        }
    }


    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
