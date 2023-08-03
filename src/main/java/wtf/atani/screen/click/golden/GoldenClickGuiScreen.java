package wtf.atani.screen.click.golden;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.impl.hud.ClickGui;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.interfaces.ColorPalette;
import wtf.atani.utils.math.MathUtil;
import wtf.atani.utils.render.GradientUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.animation.Direction;
import wtf.atani.utils.render.animation.impl.DecelerateAnimation;
import wtf.atani.utils.render.color.ColorUtil;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.storage.ValueStorage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class GoldenClickGuiScreen extends GuiScreen implements ColorPalette {

    final float width = 480;
    final float height = 300;
    float x = -1337;
    float y = -1337;
    Category selectedCategory;
    Module selectedModule;
    ArrayList<Value> expandedValues = new ArrayList<>();
    int moduleScroll = 0;
    int valueScroll = 0;
    DecelerateAnimation openingAnimation = new DecelerateAnimation(2000, 1, Direction.BACKWARDS);
    ClickGui clickGui;

    @Override
    public void initGui() {
        this.clickGui = ModuleStorage.getInstance().getByClass(ClickGui.class);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, super.width - 60, super.height - 25, 55, 20, "Reset Gui"));
        openingAnimation.setDirection(Direction.FORWARDS);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.x == -1337 || this.y == -1337) {
            this.x = super.width / 2 - this.width / 2;
            this.y = super.height / 2 - this.height / 2;
        }
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimation.getOutput().floatValue());
        FontRenderer fontRenderer = FontStorage.getInstance().findFont("Roboto", 21);
        RenderUtil.drawRect(x, y, width, height, BACK_GRAY_20);
        GradientUtil.drawGradientLR(x, y, width, 2, 1, new Color(GOLDEN_FIRST), new Color(GOLDEN_SECOND));
        fontRenderer.drawTotalCenteredStringWithShadow("Atani Client", x + width / 2, y + 13, -1);
        RenderUtil.drawRect(x + 30, y + 23 + fontRenderer.FONT_HEIGHT / 2 - 3, width - 60, 1, new Color(40, 40, 40).getRGB());
        float categoryX = 0;
        for (Category category : Category.values()) {
            categoryX += fontRenderer.getStringWidth(category.getName().toUpperCase()) + 10;
        }
        categoryX -= 10;
        categoryX = x + width / 2 - categoryX / 2;
        float startX = categoryX;
        for (Category category : Category.values()) {
            fontRenderer.drawStringWithShadow(category.getName().toUpperCase(), categoryX, y + 23 + fontRenderer.FONT_HEIGHT / 2 - 3 + 10, -1);
            categoryX += fontRenderer.getStringWidth(category.getName().toUpperCase()) + 10;
        }
        float endX = categoryX;
        float moduleY = y + 23 + fontRenderer.FONT_HEIGHT / 2 - 3 + 10 + fontRenderer.FONT_HEIGHT + 10;
        float startY = moduleY;
        moduleY += moduleScroll;
        ArrayList<Module> modules = ModuleStorage.getInstance().getModules(this.selectedCategory);
        modules.sort(Comparator.comparingInt(o -> fontRenderer.getStringWidth(((Module) o).getName())).reversed());
        int counter = 0;
        int dWheel = Mouse.getDWheel();
        if (RenderUtil.isHovered(mouseX, mouseY, x, startY, this.width / 2, height - (startY - y) + 0.5f)) {
            moduleScroll += dWheel / 10F;
        }
        if (RenderUtil.isHovered(mouseX, mouseY, x + width / 2, startY, this.width / 2, height - (startY - y) + 0.5f)) {
            valueScroll += dWheel / 10F;
        }
        float scissorY = startY;
        float scissorHeight = height - (scissorY - y) - 0.5f;
        RenderUtil.drawRect(0, scissorY, super.width, height - (scissorY - y), new Color(255, 0, 0, 100).getRGB());
        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(0, scissorY, super.width, scissorHeight);
        for (Module module : modules) {
            fontRenderer.drawStringWithShadow(module.getName(), startX, moduleY, module.isEnabled() ? ColorUtil.fadeBetween(GOLDEN_FIRST, GOLDEN_SECOND, counter * 150L) : -1);
            moduleY += fontRenderer.FONT_HEIGHT + 2;
            counter++;
        }
        RenderUtil.drawRect(x + width / 2 - 0.5f, startY, 1f, y + height - startY - 15, new Color(40, 40, 40).getRGB());
        float valueY = startY + valueScroll;
        int counterVal = 0;
        float halfX = x + width / 2 - 0.5f + 10;
        for (Value value : ValueStorage.getInstance().getValues(selectedModule)) {
            if (!value.isVisible())
                continue;
            if (value instanceof CheckBoxValue) {
                CheckBoxValue checkBoxValue = (CheckBoxValue) value;
                fontRenderer.drawStringWithShadow(value.getName() + ": " + (checkBoxValue.isEnabled() ? "X" : ""), halfX, valueY, -1);
            }
            if (value instanceof SliderValue) {
                SliderValue sliderValue = (SliderValue) value;
                fontRenderer.drawStringWithShadow(value.getName() + ": " + sliderValue.getValue().floatValue(), halfX, valueY, -1);
                valueY += fontRenderer.FONT_HEIGHT + 2;
                fontRenderer.drawStringWithShadow(sliderValue.getMinimum().floatValue() + "", halfX, valueY, -1);
                fontRenderer.drawStringWithShadow(sliderValue.getMaximum().floatValue() + "", endX - fontRenderer.getStringWidth(sliderValue.getMaximum().floatValue() + ""), valueY, -1);
                float sliderX = halfX + fontRenderer.getStringWidth(sliderValue.getMinimum().floatValue() + "") + 2;
                float sliderWidth = (endX - fontRenderer.getStringWidth(sliderValue.getMaximum().floatValue() + "") - 2) - sliderX;
                float sliderY = valueY + fontRenderer.FONT_HEIGHT / 2 - 2;
                float sliderHeight = 4;
                RenderUtil.drawRect(sliderX, sliderY, sliderWidth, sliderHeight, new Color(50, 50, 50).getRGB());
                float length = MathHelper
                        .floor_double(((sliderValue.getValue()).floatValue() - sliderValue.getMinimum().floatValue())
                                / (sliderValue.getMaximum().floatValue() - sliderValue.getMinimum().floatValue()) * sliderWidth);
                GradientUtil.drawGradientLR(sliderX, sliderY, length, sliderHeight, 1, new Color(GOLDEN_FIRST), new Color(GOLDEN_SECOND));
                if (Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, halfX, valueY, endX - halfX, fontRenderer.FONT_HEIGHT + 2)) {
                    double min1 = sliderValue.getMinimum().floatValue();
                    double max1 = sliderValue.getMaximum().floatValue();
                    double newValue = MathUtil.round((mouseX - sliderX) * (max1 - min1) / (sliderWidth - 1.0f) + min1, sliderValue.getDecimalPlaces());
                    sliderValue.setValue(newValue);
                }
            }
            if (value instanceof StringBoxValue) {
                StringBoxValue stringBoxValue = (StringBoxValue) value;
                fontRenderer.drawStringWithShadow(value.getName() + ": " + stringBoxValue.getValue(), halfX, valueY, -1);
                if (this.expandedValues.contains(value)) {
                    valueY += fontRenderer.FONT_HEIGHT + 2;
                    for (String string : stringBoxValue.getValues()) {
                        fontRenderer.drawStringWithShadow(string, halfX + fontRenderer.getStringWidth(value.getName() + ": "), valueY, -1);
                        valueY += fontRenderer.FONT_HEIGHT + 2;
                    }
                    valueY -= fontRenderer.FONT_HEIGHT + 2;
                }
            }
            valueY += fontRenderer.FONT_HEIGHT + 2;
            counterVal++;
        }
        RenderUtil.endScissorBox();
        RenderUtil.scaleEnd();
        if (clickGui.openingAnimation.getValue()) {
            if (this.openingAnimation.finished(Direction.BACKWARDS))
                mc.displayGuiScreen(null);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(null);
                ClickGui.clickGuiScreenGolden = null;
                ModuleStorage.getInstance().getByClass(ClickGui.class).toggle();
                break;
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        FontRenderer fontRenderer = FontStorage.getInstance().findFont("Roboto", 21);
        float categoryX = 0;
        for (Category category : Category.values()) {
            categoryX += fontRenderer.getStringWidth(category.getName().toUpperCase()) + 10;
        }
        categoryX -= 10;
        categoryX = x + width / 2 - categoryX / 2;
        float startX = categoryX;
        for (Category category : Category.values()) {
            if (RenderUtil.isHovered(mouseX, mouseY, categoryX, y + 23 + fontRenderer.FONT_HEIGHT / 2 - 3 + 10, fontRenderer.getStringWidth(category.getName().toUpperCase()), fontRenderer.FONT_HEIGHT) && mouseButton == 0) {
                selectedCategory = category;
                moduleScroll = 0;
                valueScroll = 0;
                selectedModule = null;
            }
            categoryX += fontRenderer.getStringWidth(category.getName().toUpperCase()) + 10;
        }
        float endX = categoryX;
        float moduleY = y + 23 + fontRenderer.FONT_HEIGHT / 2 - 3 + 10 + fontRenderer.FONT_HEIGHT + 10;
        float startY = moduleY;
        moduleY += moduleScroll;
        ArrayList<Module> modules = ModuleStorage.getInstance().getModules(this.selectedCategory);
        modules.sort(Comparator.comparingInt(o -> fontRenderer.getStringWidth(((Module) o).getName())).reversed());
        int counter = 0;
        for (Module module : modules) {
            if (RenderUtil.isHovered(mouseX, mouseY, startX, moduleY, fontRenderer.getStringWidth(module.getName()), fontRenderer.FONT_HEIGHT)) {
                if (mouseButton == 1) {
                    this.selectedModule = module;
                    valueScroll = 0;
                } else if (mouseButton == 0) {
                    module.toggle();
                }
            }
            moduleY += fontRenderer.FONT_HEIGHT + 2;
            counter++;
        }
        float valueY = startY + valueScroll;
        int counterVal = 0;
        float halfX = x + width / 2 - 0.5f + 10;
        for (Value value : ValueStorage.getInstance().getValues(selectedModule)) {
            if (!value.isVisible())
                continue;
            if (value instanceof CheckBoxValue) {
                CheckBoxValue checkBoxValue = (CheckBoxValue) value;
                if (RenderUtil.isHovered(mouseX, mouseY, halfX, valueY, endX - halfX, fontRenderer.FONT_HEIGHT + 2)) {
                    checkBoxValue.setValue(!checkBoxValue.getValue());
                }
            }
            if (value instanceof SliderValue) {
                SliderValue sliderValue = (SliderValue) value;
                fontRenderer.drawStringWithShadow(value.getName() + ": " + sliderValue.getValue().floatValue(), halfX, valueY, -1);
                valueY += fontRenderer.FONT_HEIGHT + 2;
                if (RenderUtil.isHovered(mouseX, mouseY, halfX, valueY, endX - halfX, fontRenderer.FONT_HEIGHT + 2)) {
                    if (this.expandedValues.contains(value)) {
                        this.expandedValues.remove(value);
                    } else {
                        this.expandedValues.add(value);
                    }
                }
            }
            if (value instanceof StringBoxValue) {
                StringBoxValue stringBoxValue = (StringBoxValue) value;
                if (RenderUtil.isHovered(mouseX, mouseY, halfX, valueY, endX - halfX, fontRenderer.FONT_HEIGHT + 2)) {
                    if (this.expandedValues.contains(value)) {
                        this.expandedValues.remove(value);
                    } else {
                        this.expandedValues.add(value);
                    }
                }
                if (this.expandedValues.contains(value)) {
                    valueY += fontRenderer.FONT_HEIGHT + 2;
                    for (String string : stringBoxValue.getValues()) {
                        if (RenderUtil.isHovered(mouseX, mouseY, halfX, valueY, endX - halfX, fontRenderer.FONT_HEIGHT + 2)) {
                            stringBoxValue.setValue(string);
                        }
                        valueY += fontRenderer.FONT_HEIGHT + 2;
                    }
                    valueY -= fontRenderer.FONT_HEIGHT + 2;
                }
            }
            valueY += fontRenderer.FONT_HEIGHT + 2;
            counterVal++;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char key, int code) {
        if (code == 1) {
            if (clickGui.openingAnimation.getValue()) {
                this.openingAnimation.setDirection(Direction.BACKWARDS);
            } else {
                mc.displayGuiScreen(null);
            }
        }
    }

}
