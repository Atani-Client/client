package wtf.atani.screen.click.atani;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.impl.player.ScaffoldWalk;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.processor.impl.SessionProcessor;
import wtf.atani.utils.interfaces.ClientInformationAccess;
import wtf.atani.utils.math.MathUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.shader.shaders.GradientShader;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.storage.ValueStorage;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AtaniClickguiScreen extends GuiScreen implements ClientInformationAccess {

    float width = 450, height = 350;
    float x = 100, y = 100;
    Category selectedCategory;
    ArrayList<StringBoxValue> expanded = new ArrayList<>();
    FontRenderer fontRendererSmall = FontStorage.getInstance().findFont("Arial",  18);
    FontRenderer fontRendererSmaller = FontStorage.getInstance().findFont("Arial",  17);
    Color textColor = new Color(15, 15, 15);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, width, height, new Color(0, 58, 105).brighter().getRGB());
        GradientShader.drawGradientTB(x + 2, y + 2, width - 4, 30, 1, new Color(0, 48, 95).brighter().brighter(), new Color(0, 48, 95).brighter());
        RenderUtil.drawRect(x + 2, y + 32, width - 4, height - 34, new Color(0, 48, 95).brighter().getRGB());
        RenderUtil.drawRect(x + 2, y + 2 + 30 + 2, width - 4, height - (2 + 30 + 2) - 2, new Color(240, 240, 240).getRGB());
        FontRenderer fontRenderer = FontStorage.getInstance().findFont("Arial", 21);
        fontRenderer.drawStringWithShadow(CLIENT_NAME + " Client", x + 10, y + 2 + 30 / 2 - fontRenderer.FONT_HEIGHT, -1);
        fontRenderer.drawStringWithShadow("v" + VERSION, x + 10, y + 2 + 30 / 2 + 2, -1);
        fontRenderer.drawStringWithShadow(mc.isSingleplayer() ? "SinglePlayer" : mc.getCurrentServerData().serverIP, x + width - 10 - fontRenderer.getStringWidth(mc.isSingleplayer() ? "SinglePlayer" : mc.getCurrentServerData().serverIP), y + 2 + 30 / 2 - fontRenderer.FONT_HEIGHT, -1);
        long milliseconds = SessionProcessor.getInstance().getTotalPlayTime();
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes);
        String time = "yes";
        if (days > 0) {
            time = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
        } else if (hours > 0) {
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            time = String.format("%02d:%02d", minutes, seconds);
        }
        fontRenderer.drawStringWithShadow(time, x + width - 10 - fontRenderer.getStringWidth(time), y + 2 + 30 / 2 + 2, -1);
        GradientShader.drawGradientTB(x + 2, y + 2 + 30 + 2, width - 4, 20, 1, new Color(50, 50, 50), new Color(10, 10, 10));
        float spaceWidth = width - 4;
        float categoryWidth = spaceWidth / Category.values().length;
        float categoryX = x + 2;
        for(Category category : Category.values()) {
            fontRendererSmall.drawTotalCenteredStringWithShadow((selectedCategory == category ? ChatFormatting.BOLD.toString() : "") + category.getName(), categoryX + categoryWidth / 2, y + 30 + 4 + 20 / 2, -1);
            if(RenderUtil.isHovered(mouseX, mouseY, categoryX, y + 30 + 4, categoryWidth, 20))
                RenderUtil.drawRect(categoryX, y + 30 + 4, categoryWidth, 20, new Color(0, 0, 0, 40).getRGB());
            categoryX += categoryWidth;
        }
        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(x + 2, y + 2 + 30 + 2, width - 4, height - (2 + 30 + 2) - 2);
        if(selectedCategory != null) {
            float third = spaceWidth / 3;
            int counter = 0;
            float firstY = y + 30 + 4 + 20 + 10 + 10, secondY = y + 30 + 4 + 20 + 10 + 10, thirdY = y + 30 + 4 + 20 + 10 + 10;
            float modX = x + 2 + 10;
            float modWidth = third - 13.5f;
            for(Module module : ModuleStorage.getInstance().getModules(selectedCategory)) {
                float modY = 0;
                switch (counter) {
                    case 0:
                        modY = firstY;
                        break;
                    case 1:
                        modY = secondY;
                        break;
                    case 2:
                        modY = thirdY;
                        break;
                }
                fontRendererSmaller.drawString(ChatFormatting.BOLD + module.getName(), modX + 4, modY - fontRendererSmaller.FONT_HEIGHT - 4, textColor.getRGB());
                float valY = modY + 10;
                // Getting Y
                // Enabled field
                valY += fontRendererSmall.FONT_HEIGHT + 2 ;
                // Values
                for(Value value : ValueStorage.getInstance().getValues(module)) {
                    if(!value.isVisible())
                        continue;
                    if(value instanceof CheckBoxValue) {
                        valY += fontRendererSmall.FONT_HEIGHT + 2 ;
                    } else if(value instanceof SliderValue) {
                        valY += (fontRendererSmall.FONT_HEIGHT + 2) * 2;
                    } else if(value instanceof StringBoxValue) {
                        valY += (fontRendererSmall.FONT_HEIGHT + 2);
                        if(this.expanded.contains(value)) {
                            StringBoxValue stringBoxValue = (StringBoxValue) value;
                            for(String s : stringBoxValue.getValues()) {
                                valY += (fontRendererSmall.FONT_HEIGHT + 2) * 2;
                            }
                        }
                    }
                }
                // Rect
                float height = (valY + 8) - modY;
                RenderUtil.drawBorderedRect(modX, modY, modX + modWidth, modY + height, 1f,-1, new Color(200, 200, 200).getRGB(), true);
                // Rendering
                float valY2 = modY + 10;
                fontRendererSmaller.drawString("Enabled: ", modX + 10, valY2, textColor.getRGB());
                GradientShader.drawGradientTB(modX + 10 + fontRendererSmaller.getStringWidth("Enabled: "), valY2 - 1.5f, 9, 9, 1, new Color(0, 48, 95).brighter().brighter(), new Color(0, 48, 95).brighter());
                if(!module.isEnabled()) {
                    RenderUtil.drawRect(modX + 10 + fontRendererSmaller.getStringWidth("Enabled: ") + 1, valY2 - 1.5f + 1, 9 - 2, 9 - 2, -1);
                }
                valY2 += fontRendererSmall.FONT_HEIGHT + 2 ;
                for(Value value : ValueStorage.getInstance().getValues(module)) {
                    if(!value.isVisible())
                        continue;
                    if(value instanceof CheckBoxValue) {
                        fontRendererSmaller.drawString(value.getName() + ": ", modX + 10, valY2, textColor.getRGB());
                        GradientShader.drawGradientTB(modX + 10 + fontRendererSmaller.getStringWidth(value.getName() + ": "), valY2 - 1.5f, 9, 9, 1, new Color(0, 48, 95).brighter().brighter(), new Color(0, 48, 95).brighter());
                        if(!((CheckBoxValue) value).getValue()) {
                            RenderUtil.drawRect(modX + 10 + fontRendererSmaller.getStringWidth(value.getName() + ": ") + 1, valY2 - 1.5f + 1, 9 - 2, 9 - 2, -1);
                        }
                        valY2 += fontRendererSmall.FONT_HEIGHT + 2;
                    } else if(value instanceof SliderValue) {
                        SliderValue sliderValue = (SliderValue) value;
                        fontRendererSmaller.drawString(value.getName() + ": " + ((SliderValue)value).getValue().floatValue(), modX + 10, valY2, textColor.getRGB());
                        valY2 += fontRendererSmall.FONT_HEIGHT + 2;
                        float sliderWidth = modWidth - 20, sliderX = modX + 10, sliderY = valY2 - 1.5f;
                        GradientShader.drawGradientTB(sliderX, sliderY, sliderWidth, 9, 1, new Color(0, 48, 95).brighter().brighter(), new Color(0, 48, 95).brighter());
                        RenderUtil.drawRect(modX + 10 + 1, valY2 - 1.5f + 1, sliderWidth - 2, 9 - 2, -1);
                        float length = MathHelper
                                .floor_double(((sliderValue.getValue()).floatValue() - sliderValue.getMinimum().floatValue())
                                        / (sliderValue.getMaximum().floatValue() - sliderValue.getMinimum().floatValue()) * sliderWidth);
                        GradientShader.drawGradientTB(sliderX, sliderY, length, 9, 1, new Color(0, 48, 95).brighter().brighter(), new Color(0, 48, 95).brighter());
                        if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, modX, valY2 - 2.5f, modWidth, 11)) {
                            double min1 = sliderValue.getMinimum().floatValue();
                            double max1 = sliderValue.getMaximum().floatValue();
                            double newValue = MathUtil.round((mouseX - sliderX) * (max1 - min1) / (sliderWidth - 1.0f) + min1, sliderValue.getDecimalPlaces());
                            sliderValue.setValue(newValue);
                        }
                        valY2 += fontRendererSmall.FONT_HEIGHT + 2;
                    } else if(value instanceof StringBoxValue) {
                        fontRendererSmaller.drawString(value.getName() + ": " + value.getValue(), modX + 10, valY2, textColor.getRGB());
                        valY2 += fontRendererSmall.FONT_HEIGHT + 2;
                        if(this.expanded.contains(value)) {
                            StringBoxValue stringBoxValue = (StringBoxValue) value;
                            for(String s : stringBoxValue.getValues()) {
                                fontRendererSmaller.drawString(" - " + s, modX + 10, valY2, textColor.getRGB());
                                valY2 += (fontRendererSmall.FONT_HEIGHT + 2);
                            }
                        }
                    }
                }
                counter++;
                switch (counter - 1) {
                    case 0:
                        firstY += height + 20;
                        break;
                    case 1:
                        secondY += height + 20;
                        break;
                    case 2:
                        thirdY += height + 20;
                        break;
                }
                if(counter != 0 && counter % 3 == 0) {
                    modX = x + 2 + 10;
                    counter = 0;
                } else {
                    modX += modWidth + 10;
                }
            }
        }
        RenderUtil.endScissorBox();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float spaceWidth = width - 4;
        float categoryWidth = spaceWidth / Category.values().length;
        float categoryX = x + 2;
        for(Category category : Category.values()) {
            if(RenderUtil.isHovered(mouseX, mouseY, categoryX, y + 30 + 4, categoryWidth, 20))
                selectedCategory = category;
            categoryX += categoryWidth;
        }
        if(!RenderUtil.isHovered(mouseX, mouseY, x + 2, y + 2 + 30 + 2, width - 4, height - (2 + 30 + 2) - 2))
            return;
        if(selectedCategory != null) {
            float third = spaceWidth / 3;
            int counter = 0;
            float firstY = y + 30 + 4 + 20 + 10 + 10, secondY = y + 30 + 4 + 20 + 10 + 10, thirdY = y + 30 + 4 + 20 + 10 + 10;
            float modX = x + 2 + 10;
            float modWidth = third - 13.5f;
            for(Module module : ModuleStorage.getInstance().getModules(selectedCategory)) {
                float modY = 0;
                switch (counter) {
                    case 0:
                        modY = firstY;
                        break;
                    case 1:
                        modY = secondY;
                        break;
                    case 2:
                        modY = thirdY;
                        break;
                }
                float valY = modY + 10;
                // Getting Y
                // Enabled field
                valY += fontRendererSmall.FONT_HEIGHT + 2 ;
                // Values
                for(Value value : ValueStorage.getInstance().getValues(module)) {
                    if(!value.isVisible())
                        continue;
                    if(value instanceof CheckBoxValue) {
                        valY += fontRendererSmall.FONT_HEIGHT + 2 ;
                    } else if(value instanceof SliderValue) {
                        valY += (fontRendererSmall.FONT_HEIGHT + 2) * 2;
                    } else if(value instanceof StringBoxValue) {
                        valY += (fontRendererSmall.FONT_HEIGHT + 2);
                        if(this.expanded.contains(value)) {
                            StringBoxValue stringBoxValue = (StringBoxValue) value;
                            for(String s : stringBoxValue.getValues()) {
                                valY += (fontRendererSmall.FONT_HEIGHT + 2) * 2;
                            }
                        }
                    }
                }
                // Rect
                float height = (valY + 8) - modY;
                // Rendering
                float valY2 = modY + 10;
                if(RenderUtil.isHovered(mouseX, mouseY, modX, valY2 - 2.5f, modWidth, 11))
                    module.setEnabled(!module.isEnabled());
                valY2 += fontRendererSmall.FONT_HEIGHT + 2 ;
                for(Value value : ValueStorage.getInstance().getValues(module)) {
                    if(!value.isVisible())
                        continue;
                    if(value instanceof CheckBoxValue) {
                        if(RenderUtil.isHovered(mouseX, mouseY, modX, valY2 - 2.5f, modWidth, 11))
                            value.setValue(!((CheckBoxValue)value).getValue());
                        valY2 += fontRendererSmall.FONT_HEIGHT + 2;
                    } else if(value instanceof SliderValue) {

                        valY2 += fontRendererSmall.FONT_HEIGHT + 2;
                    } else if(value instanceof StringBoxValue) {
                        if(RenderUtil.isHovered(mouseX, mouseY, modX, valY2 - 2.5f, modWidth, 11)) {
                             if(this.expanded.contains(value))
                                 this.expanded.remove(value);
                             else
                                 this.expanded.add((StringBoxValue) value);
                        }
                        valY2 += fontRendererSmall.FONT_HEIGHT + 2;
                        if(this.expanded.contains(value)) {
                            StringBoxValue stringBoxValue = (StringBoxValue) value;
                            for(String s : stringBoxValue.getValues()) {
                                if(RenderUtil.isHovered(mouseX, mouseY, modX, valY2 - 2.5f, modWidth, 11)) {
                                    stringBoxValue.setValue(s);
                                }
                                valY2 += (fontRendererSmall.FONT_HEIGHT + 2);
                            }
                        }
                    }
                }
                counter++;
                switch (counter - 1) {
                    case 0:
                        firstY += height + 20;
                        break;
                    case 1:
                        secondY += height + 20;
                        break;
                    case 2:
                        thirdY += height + 20;
                        break;
                }
                if(counter != 0 && counter % 3 == 0) {
                    modX = x + 2 + 10;
                    counter = 0;
                } else {
                    modX += modWidth + 10;
                }
            }
        }
    }

}
