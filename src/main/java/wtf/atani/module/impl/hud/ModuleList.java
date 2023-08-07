package wtf.atani.module.impl.hud;

import com.google.common.base.Supplier;
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
import wtf.atani.module.impl.hud.clientOverlay.IClientOverlayComponent;
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
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.interfaces.ValueChangeListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleInfo(name = "ModuleList", description = "A nice little overlay that shows you info about the client", category = Category.HUD)
public class ModuleList extends Module implements ColorPalette, IClientOverlayComponent {
    private StringBoxValue moduleListMode = new StringBoxValue("Module List Mode", "Which module list will be displayed?", this, new String[]{"None", "Simple", "Golden", "Augustus 2.6", "Xave", "Ryu", "Icarus", "Fatality", "Custom"}, new ValueChangeListener[]{new ValueChangeListener() {
        @Override
        public void onChange(Stage stage, Value value, Object oldValue, Object newValue) {
            moduleHashMap.clear();
        }
    }});
    private StringBoxValue arrayListPosition = new StringBoxValue("Module List Position", "Where will the module list be?", this, new String[]{"Left", "Right"}, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom")});
    private StringBoxValue customColorMode = new StringBoxValue("Custom Color Mode", "How will the modules be colored in custom mode?", this, new String[]{"Static", "Random", "Fade", "Gradient", "Rainbow", "Astolfo Sky"}, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom")});
    private SliderValue<Integer> red = new SliderValue<>("Red", "What'll be the red of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> green = new SliderValue<>("Green", "What'll be the green of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> blue = new SliderValue<>("Blue", "What'll be the blue of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> red2 = new SliderValue<>("Second Red", "What'll be the red of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> green2 = new SliderValue<>("Second Green", "What'll be the green of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> blue2 = new SliderValue<>("Second Blue", "What'll be the blue of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Float> darkFactor = new SliderValue<>("Dark Factor", "How much will the color be darkened?", this, 0.49F, 0F, 1F, 2, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && customColorMode.getValue().equalsIgnoreCase("Fade")});
    private SliderValue<Float> rectWidth = new SliderValue<>("Rect Width", "How long will be the rect (in addition to string width)?", this, 2f, 0f, 10f, 1, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom")});
    private SliderValue<Float> rectHeight = new SliderValue<>("Rect Height", "How big will be the rect (in addition to string height)?", this, 1f, 0f, 10f, 1, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom")});
    private CheckBoxValue renderShaders = new CheckBoxValue("Render Shaders", "Render shaders on the module list?", this, true, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom")});
    private CheckBoxValue renderBlur = new CheckBoxValue("Render Blur", "Render blur on the module list?", this, true, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && renderShaders.getValue()});
    private CheckBoxValue renderBloom = new CheckBoxValue("Render Bloom", "Render bloom on the module list?", this, true, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && renderShaders.getValue()});
    private CheckBoxValue suffix = new CheckBoxValue("Suffix", "Display module's mode?", this, true);
    private StringBoxValue suffixMode = new StringBoxValue("Suffix Mode", "How will modes be displayed?", this, new String[] {"nm sfx", "nm - sfx", "nm # sfx", "nm (sfx)", "nm [sfx]", "nm {sfx}", "nm - (sfx)", "nm - [sfx]", "nm - {sfx}", "nm # (sfx)", "nm # [sfx]", "nm # {sfx}"}, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && suffix.getValue()});
    private StringBoxValue suffixColor = new StringBoxValue("Suffix Color", "How will modes be colored?", this, new String[] {"Gray", "Dark Gray", "White", "None"}, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom")});
    private StringBoxValue fontMode = new StringBoxValue("Font", "Which font will render the module name?", this, new String[]{"Minecraft", "Roboto", "Roboto Medium"}, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && suffix.getValue()});
    private SliderValue<Integer> fontSize = new SliderValue<>("Font Size", "How large will the font be?", this, 19, 17, 21, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom") && !fontMode.getValue().equalsIgnoreCase("Minecraft")});
    private SliderValue<Integer> brightness = new SliderValue<>("Background Brightness", "What will be the brightness of the background?", this, 0, 0, 255, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom")});
    private SliderValue<Integer> opacity = new SliderValue<>("Background Opacity", "What will be the opacity of the background?", this, 180, 0, 255, 0, new Supplier[]{() -> moduleListMode.getValue().equalsIgnoreCase("Custom")});
    private SliderValue<Float> xOffset = new SliderValue<>("X Offset", "How much will the module list offset on X?", this, 0F, 0F, 20F, 1);
    private SliderValue<Float> yOffset = new SliderValue<>("Y Offset", "How much will the module list offset on Y?", this, 0F, 0F, 20F, 1);
    private CheckBoxValue hideRenderModules = new CheckBoxValue("Hide Render Modules", "Should the module list hide visual modules?", this, false);

    private LinkedHashMap<Module, DecelerateAnimation> moduleHashMap = new LinkedHashMap<>();
    private LinkedHashMap<Module, Color> moduleColorHashMap = new LinkedHashMap<>();
    final Calendar calendar = Calendar.getInstance();

    @Override
    public void draw(Render2DEvent render2DEvent, AtomicFloat leftY, AtomicFloat rightY) {
        if(this.isEnabled()) {
            ScaledResolution sr = render2DEvent.getScaledResolution();

            List<Module> modulesToShow = new ArrayList<>();
            for (Module module : ModuleStorage.getInstance().getList()) {
                if (!hideRenderModules.getValue() || module.getCategory() != Category.RENDER) {
                    modulesToShow.add(module);
                }
                if (module.getName().equalsIgnoreCase("ClickGui")) {
                    modulesToShow.remove(module);
                }
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
                    case "Xave":
                        fontRenderer = FontStorage.getInstance().findFont("Roboto", 18);
                        break;
                    case "Ryu":
                        fontRenderer = FontStorage.getInstance().findFont("Roboto Medium", 17);
                        break;
                    case "Custom":
                        switch (fontMode.getValue()) {
                            case "Roboto":
                                fontRenderer = FontStorage.getInstance().findFont("Roboto", this.fontSize.getValue());
                                break;
                            case "Roboto Medium":
                                fontRenderer = FontStorage.getInstance().findFont("Roboto Medium", this.fontSize.getValue());
                                break;
                            default:
                                fontRenderer = mc.fontRendererObj;
                                break;
                        }
                        break;
                    default:
                        fontRenderer = FontStorage.getInstance().findFont("Roboto", 17);
                        break;
                }
                String name1 = moduleListMode.is("Custom") ? suffixMode.getValue().replace("nm", mod1.getName()).replace(" sfx", mod1.getSuffix() == null ? "" : " " + mod1.getSuffix()) : mod1.getName();
                String name2 = moduleListMode.is("Custom") ? suffixMode.getValue().replace("nm", mod2.getName()).replace(" sfx", mod2.getSuffix() == null ? "" : " " + mod2.getSuffix()) : mod2.getName();
                return fontRenderer.getStringWidth(name2) - fontRenderer.getStringWidth(name1);
            });
            LinkedHashMap<Module, DecelerateAnimation> sortedMap = new LinkedHashMap<>();
            for (Module module : sortedModules) {
                sortedMap.put(module, moduleHashMap.get(module));
            }
            moduleHashMap = sortedMap;

            switch (moduleListMode.getValue()) {
                case "Custom": {
                    switch (this.arrayListPosition.getValue()) {
                        case "Left":
                            leftY.set(leftY.get() + yOffset.getValue());
                            break;
                        case "Right":
                            rightY.set(rightY.get() + yOffset.getValue());
                            break;
                    }
                    RenderableShaders.renderAndRun(renderShaders.getValue() && renderBloom.getValue(), renderShaders.getValue() && renderBlur.getValue(), () -> {
                        FontRenderer fontRenderer;
                        switch (fontMode.getValue()) {
                            case "Roboto":
                                fontRenderer = FontStorage.getInstance().findFont("Roboto", this.fontSize.getValue());
                                break;
                            case "Roboto Medium":
                                fontRenderer = FontStorage.getInstance().findFont("Roboto Medium", this.fontSize.getValue());
                                break;
                            default:
                                fontRenderer = mc.fontRendererObj;
                                break;
                        }
                        float moduleY = arrayListPosition.is("Left") ? leftY.get() : rightY.get();
                        int counter = 0;
                        for (Module module : moduleHashMap.keySet()) {
                            float moduleHeight = fontRenderer.FONT_HEIGHT + rectHeight.getValue();
                            if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                                int color = 0;
                                switch (this.customColorMode.getValue()) {
                                    case "Static":
                                        color = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                                        break;
                                    case "Random":
                                        if(!moduleColorHashMap.containsKey(module)) {
                                            int baseHue = 15;
                                            int minValue = 150;
                                            int maxValue = 255;
                                            int alpha = 255;
                                            moduleColorHashMap.put(module, ColorUtil.generateRandomTonedColor(baseHue, minValue, maxValue, alpha));
                                        }
                                        color = moduleColorHashMap.get(module).getRGB();
                                        break;
                                    case "Fade": {
                                        int firstColor = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                                        color = ColorUtil.fadeBetween(firstColor, ColorUtil.darken(firstColor, darkFactor.getValue()), counter * 150L);
                                        break;
                                    }
                                    case "Gradient": {
                                        int firstColor = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                                        int secondColor = new Color(red2.getValue(), green2.getValue(), blue2.getValue()).getRGB();
                                        color = ColorUtil.fadeBetween(firstColor, secondColor, counter * 150L);
                                        break;
                                    }
                                    case "Rainbow":
                                        color = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                                        break;
                                    case "Astolfo Sky":
                                        color = ColorUtil.blendRainbowColours(counter * 150L);
                                        break;
                                }
                                if(calendar.get(Calendar.DAY_OF_MONTH) == 28 && calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
                                    color = ColorUtil.blendCzechiaColours(counter * 150L);
                                }
                                if(calendar.get(Calendar.DAY_OF_MONTH) == 3 && calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
                                    color = ColorUtil.blendGermanColours(counter * 150L);
                                }
                                String name = module.getName();
                                if(module.getSuffix() != null && suffix.getValue()) {
                                	ChatFormatting chatFormatting = null;
                                	switch(this.suffixColor.getValue()) {
                                	case "White":
                                		chatFormatting = ChatFormatting.WHITE;
                                		break;
                                	case "Gray":
                                		chatFormatting = ChatFormatting.GRAY;
                                		break;
                                	case "Dark Gray":
                                		chatFormatting = ChatFormatting.DARK_GRAY;
                                		break;
                                	case "None":
                                		chatFormatting = ChatFormatting.RESET;
                                		break;
                                	}
                                	name = suffixMode.getValue().replace("nm", module.getName()).replace("sfx", chatFormatting.toString() + module.getSuffix());
                                }
                                float rectWidth = (fontRenderer.getStringWidth(name) + this.rectWidth.getValue());
                                float moduleX = this.arrayListPosition.getValue().equalsIgnoreCase("Left") ? (0 - rectWidth + (float) (moduleHashMap.get(module).getOutput() * rectWidth) + xOffset.getValue()) : sr.getScaledWidth() - ((float) (moduleHashMap.get(module).getOutput() * rectWidth) + xOffset.getValue());
                                RenderUtil.drawRect(moduleX, moduleY, rectWidth, moduleHeight, new Color(brightness.getValue(), brightness.getValue(), brightness.getValue(), opacity.getValue()).getRGB());
                                fontRenderer.drawTotalCenteredStringWithShadow(name, moduleX + rectWidth / 2, moduleY + moduleHeight / 2 + 0.5f, color);
                                moduleY += moduleHeight;
                                counter++;
                            }
                        }
                    });
                    break;
                }
                case "Xave": {
                    if (leftY.get() == 0)
                        leftY.set(3);
                    FontRenderer roboto18 = FontStorage.getInstance().findFont("Roboto", 18);
                    float moduleY = leftY.get();
                    for (Module module : moduleHashMap.keySet()) {
                        float moduleHeight = roboto18.FONT_HEIGHT + 1;
                        if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                            if(!moduleColorHashMap.containsKey(module)) {
                                int baseHue = 15;
                                int minValue = 150;
                                int maxValue = 255;
                                int alpha = 255;
                                moduleColorHashMap.put(module, ColorUtil.generateRandomTonedColor(baseHue, minValue, maxValue, alpha));
                            }
                            int color = moduleColorHashMap.get(module).getRGB();
                            String name = module.getName();
                            float rectWidth = (roboto18.getStringWidth(name) + 4);
                            float moduleX = 2 - rectWidth + (float) (moduleHashMap.get(module).getOutput() * rectWidth) - 2;
                            RenderUtil.drawRect(moduleX, moduleY, rectWidth, moduleHeight, new Color(0, 0, 0, 180).getRGB());
                            roboto18.drawTotalCenteredStringWithShadow(name, moduleX + rectWidth / 2, moduleY + moduleHeight / 2 + 0.5f, color);
                            moduleY += moduleHeight;
                        }
                    }
                    break;
                }
                case "Fatality": {
                    if (rightY.get() == 0)
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
                    if (rightY.get() == 0)
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
                    if (leftY.get() == 0)
                        leftY.set(6);
                    System.out.println("DSJFJHS");

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
                        if (leftY.get() == 0)
                            leftY.set(8);
                        float moduleY = leftY.get();
                        for (Module module : moduleHashMap.keySet()) {
                            float moduleHeight = roboto17.FONT_HEIGHT + 8;
                            if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
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
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Listen
    public final void onModuleEnable(EnableModuleEvent enableModuleEvent) {
        if (enableModuleEvent.getType() == EnableModuleEvent.Type.PRE)
            return;
        if (this.moduleHashMap.containsKey(enableModuleEvent.getModule())) {
            this.moduleHashMap.get(enableModuleEvent.getModule()).setDirection(Direction.FORWARDS);
        } else {
            switch (this.moduleListMode.getValue()) {
                case "Augustus 2.6":
                    moduleHashMap.put(enableModuleEvent.getModule(), new DecelerateAnimation(1, 1, Direction.FORWARDS));
                    break;
                default:
                    moduleHashMap.put(enableModuleEvent.getModule(), new DecelerateAnimation(200, 1, Direction.FORWARDS));
                    break;
            }
        }
    }

    @Listen
    public final void onModuleDisable(DisableModuleEvent disableModuleEvent) {
        if (disableModuleEvent.getType() == DisableModuleEvent.Type.PRE)
            return;
        if (this.moduleHashMap.containsKey(disableModuleEvent.getModule())) {
            this.moduleHashMap.get(disableModuleEvent.getModule()).setDirection(Direction.BACKWARDS);
        } else {
            switch (this.moduleListMode.getValue()) {
                case "Augustus 2.6":
                    moduleHashMap.put(disableModuleEvent.getModule(), new DecelerateAnimation(1, 1, Direction.BACKWARDS));
                    break;
                default:
                    moduleHashMap.put(disableModuleEvent.getModule(), new DecelerateAnimation(200, 1, Direction.BACKWARDS));
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
