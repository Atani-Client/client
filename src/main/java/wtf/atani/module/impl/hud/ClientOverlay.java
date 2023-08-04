package wtf.atani.module.impl.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import wtf.atani.event.events.DisableModuleEvent;
import wtf.atani.event.events.EnableModuleEvent;
import wtf.atani.event.events.Render2DEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.interfaces.ColorPalette;
import wtf.atani.utils.java.StringUtil;
import wtf.atani.utils.math.atomic.AtomicFloat;
import wtf.atani.utils.render.GradientUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.RoundedUtil;
import wtf.atani.utils.render.animation.Direction;
import wtf.atani.utils.render.animation.impl.DecelerateAnimation;
import wtf.atani.utils.render.color.ColorUtil;
import wtf.atani.utils.render.shader.render.ingame.RenderableShaders;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.interfaces.ValueChangeListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@ModuleInfo(name = "ClientOverlay", description = "A nice little overlay that shows you info about the client", category = Category.HUD)
public class ClientOverlay extends Module implements ColorPalette {

    private StringBoxValue watermarkMode = new StringBoxValue("Watermark Mode", "Which watermark will be displayed?", this, new String[]{"None", "Simple", "Golden", "Augustus 2.6", "Ryu", "Icarus", "Fatality", "Vestige 2.0.2"});
    private StringBoxValue moduleListMode = new StringBoxValue("Module List Mode", "Which module list will be displayed?", this, new String[]{"None", "Simple", "Golden", "Augustus 2.6", "Ryu", "Icarus", "Fatality", "Vestige 2.0.2"}, new ValueChangeListener[]{new ValueChangeListener() {
        @Override
        public void onChange(Stage stage, Value value, Object oldValue, Object newValue) {
            moduleHashMap.clear();
        }
    }});
    private StringBoxValue releaseString = new StringBoxValue("Release String Mode", "Which release string will be displayed?", this, new String[]{"None", "Fatality"});
    private CheckBoxValue hideRenderModules = new CheckBoxValue("Hide Render Modules", "Should the module list hide visual modules?", this, false);

    private LinkedHashMap<Module, DecelerateAnimation> moduleHashMap = new LinkedHashMap<>();

    @Listen
    public final void on2D(Render2DEvent render2DEvent) {
        ScaledResolution sr = render2DEvent.getScaledResolution();
        // When making a watermark update these values (including the margins!) so that we can have arraylist and watermark on the same side without
        // having 100 if else statements
        // - Tabio

        List<Module> modulesToShow = new ArrayList<>();
        for (Module module : ModuleStorage.getInstance().getList()) {
            if (!hideRenderModules.getValue() || module.getCategory() != Category.RENDER) {
                modulesToShow.add(module);
            }
        }
        AtomicFloat leftY = new AtomicFloat(0);
        AtomicFloat rightY = new AtomicFloat(0);
        int vCounter = 0;
        switch (watermarkMode.getValue()) {
            case "Vestige 2.0.2": {
                FontRenderer fontRenderer = FontStorage.getInstance().findFont("Product Sans", 17);
                String text = CLIENT_NAME + " " + VERSION + " | " + mc.getDebugFPS() + "FPS | " + mc.session.getUsername();

                final float textWidth = fontRenderer.getStringWidth(text);

                RoundedUtil.drawRound(6,3, textWidth + 4, 15, 2, new Color(0, 0, 0, 150));
                RoundedUtil.drawGradientHorizontal(8,4, textWidth, 2, 1, new Color(VESTIGE_FIRST), new Color(VESTIGE_SECOND));

                fontRenderer.drawString(text.substring(0, 1), 8, 9, ColorUtil.fadeBetween(VESTIGE_FIRST, VESTIGE_SECOND, vCounter * 100L));
                fontRenderer.drawString(text.substring(1), 14, 9, -1);

                vCounter++;
                break;
            }
            case "Fatality": {
                // TODO: implement usernames
                // Pasted this from some random radium paste since this client's not expensive enough for me to do random themes of dead, nn clients like this and do shit like remake the entire style of skeet fucking watermark for it
                final String text = String.format("$$$ %s.vip $$$ | %s | %s", CLIENT_NAME.toLowerCase(), "idk", mc.isSingleplayer() ? "SinglePlayer" : mc.getCurrentServerData().serverIP);
                final float width2 = (float) (FontStorage.getInstance().findFont("Roboto", 15).getStringWidth(text) + 8);
                final int height2 = 20;
                final int posX2 = 2;
                final int posY1 = 2;
                Gui.drawRect(posX2, posY1, posX2 + width2 + 2.0f, posY1 + height2, new Color(5, 5, 5, 255).getRGB());
                RenderUtil.drawBorderedRect(posX2 + 0.5f, posY1 + 0.5f, posX2 + width2 + 1.5f, posY1 + height2 - 0.5f, 0.5f, new Color(40, 40, 40, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
                RenderUtil.drawBorderedRect(posX2 + 2, posY1 + 2, posX2 + width2, posY1 + height2 - 2, 0.5f, new Color(22, 22, 22, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
                Gui.drawRect(posX2 + 2.5, posY1 + 2.5, posX2 + width2 - 0.5, posY1 + 4.5, new Color(9, 9, 9, 255).getRGB());
                GradientUtil.drawGradientLR(4.0f, posY1 + 3, width2 - 2, 1, 1, new Color(FATALITY_FIRST), new Color(FATALITY_SECOND));
                FontStorage.getInstance().findFont("Roboto", 15).drawStringWithShadow(text, 7.5F, 10.0f, Color.white.getRGB());
                break;
            }
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
                fontRenderer.drawString(text, 3, 3, RYU);
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
                break;
            }
            case "Simple":
                RenderableShaders.renderAndRun(() -> {
                    String text = CLIENT_NAME + " v" + VERSION + " | " + mc.getDebugFPS() + " fps";
                    FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
                    float length = roboto17.getStringWidth(text);
                    float rectX = 10, rectY = 10;
                    float textX = rectX + 4, textY = rectY + 4.5f;
                    float rectWidth = 8 + length, rectHeight = roboto17.FONT_HEIGHT + 8;
                    RenderUtil.drawRect(rectX, rectY, rectWidth, rectHeight, BACK_TRANS_180);
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
                    GradientUtil.drawGradientLR(x, y, length + 5, lineHeight, 1, new Color(GOLDEN_FIRST), new Color(GOLDEN_SECOND));
                    RenderUtil.drawRect(x - 2, y, 2, roboto17.FONT_HEIGHT + 4, new Color(255, 202, 3).getRGB());
                    RenderUtil.drawRect(x, y + lineHeight, length + 5, roboto17.FONT_HEIGHT + 4 - lineHeight, BACK_GRAY_20);
                    roboto17.drawStringWithShadow(text, x + 2.5f, y + lineHeight + 2, -1);
                    leftY.set(y + lineHeight + roboto17.FONT_HEIGHT + 4 + y);
                });
                break;
        }

        switch (this.releaseString.getValue()) {
            case "Fatality":
                String releaseType = DEVELOPMENT_SWITCH ? "Dev" : BETA_SWITCH ? "Beta" : "Release";
                // TODO: WE NEED TO IMPLEMENT THE FUCKING USERNAMES
                String text = ChatFormatting.GRAY + releaseType + " - " + ChatFormatting.WHITE + "idk" + ChatFormatting.GRAY + " - " + VERSION;
                mc.fontRendererObj.drawStringWithShadow(text, sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(text) - 2, sr.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2, -1);
                break;
        }

        for (Module module : modulesToShow) {
            if (!moduleHashMap.containsKey(module)) {
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

        List<Module> sortedModules = new ArrayList<>(modulesToShow);
        Collections.sort(sortedModules, (mod1, mod2) -> {
            FontRenderer fontRenderer;
            switch (moduleListMode.getValue()) {
                case "Vestige 2.0.2":
                    fontRenderer = FontStorage.getInstance().findFont("Product Sans", 17);
                    break;
                case "Icarus":
                    fontRenderer = FontStorage.getInstance().findFont("Pangram Regular", 17);
                    break;
                case "Fatality":
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
        });
        LinkedHashMap<Module, DecelerateAnimation> sortedMap = new LinkedHashMap<>();
        for (Module module : sortedModules) {
            sortedMap.put(module, moduleHashMap.get(module));
        }
        moduleHashMap = sortedMap;

        switch (moduleListMode.getValue()) {
            case "Vestige 2.0.2": {
                FontRenderer fontRenderer = FontStorage.getInstance().findFont("Product Sans", 17);
                float moduleY = rightY.get() + 8;
                int counter = 0;
                for (Module module : moduleHashMap.keySet()) {
                    if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                        float moduleHeight = fontRenderer.FONT_HEIGHT + 4;
                        float rectLength = (float) ((fontRenderer.getStringWidth(module.getName() + 3) * moduleHashMap.get(module).getOutput()) - 2F);

                        RenderUtil.drawRect(sr.getScaledWidth() - rectLength - 2F, moduleY, 1.5F, moduleHeight, ColorUtil.fadeBetween(VESTIGE_FIRST, VESTIGE_SECOND, counter * 100L));

                        RenderUtil.drawRect(sr.getScaledWidth() - rectLength, moduleY, rectLength + 20, moduleHeight, new Color(0, 0, 0, 150).getRGB());
                        fontRenderer.drawString(module.getName(), sr.getScaledWidth() - rectLength + 1.5f, moduleY + moduleHeight / 2 - fontRenderer.FONT_HEIGHT / 2, ColorUtil.fadeBetween(VESTIGE_FIRST, VESTIGE_SECOND, counter * 100L));
                        moduleY += moduleHeight;
                        counter++;
                    }
                }
                break;
            }
            case "Fatality": {
                if(rightY.get() == 0)
                    rightY.set(1);
                FontRenderer fontRenderer = mc.fontRendererObj;
                float moduleY = rightY.get();
                int counter = 0;
                for (Module module : moduleHashMap.keySet()) {
                    if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                        float moduleHeight = fontRenderer.FONT_HEIGHT;
                        float rectLength = (float) ((fontRenderer.getStringWidth(module.getName()) + 1) * moduleHashMap.get(module).getOutput());
                        fontRenderer.drawStringWithShadow(module.getName(), sr.getScaledWidth() - rectLength - 1, moduleY + moduleHeight / 2 - fontRenderer.FONT_HEIGHT / 2, ColorUtil.fadeBetween(FATALITY_FIRST, FATALITY_SECOND, counter * 150L));
                        moduleY += moduleHeight;
                        counter++;
                    }
                }
                break;
            }
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
                GradientUtil.drawGradientTB(sr.getScaledWidth() - gradientWidth, rightY.get(), gradientWidth, moduleY, 1, new Color(ICARUS_FIRST), new Color(ICARUS_SECOND));
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
                    GradientUtil.drawGradientTB(5, leftY.get(), 2, moduleY - leftY.get(), 1, new Color(GOLDEN_FIRST), new Color(GOLDEN_SECOND));
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
                            RenderUtil.drawRect(moduleX, moduleY, rectWidth, moduleHeight, BACK_TRANS_180);
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
