package tech.atani.client.feature.theme;

import net.minecraft.client.gui.GuiScreen;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.theme.data.ThemeData;
import tech.atani.client.feature.theme.elements.RenderElement;

public abstract class Theme {

    private final String name, description;

    public Theme() {
        ThemeData themeData = this.getClass().getAnnotation(ThemeData.class);
        if(themeData == null)
            throw new RuntimeException();
        this.name = themeData.name();
        this.description = themeData.description();
    }

    public abstract Class<GuiScreen> getClickGui();

    public abstract Class<GuiScreen> getMainMenu();

    public abstract RenderElement getWatermark();

    public abstract RenderElement getModuleList();

    public abstract RenderElement getTargetHUD();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
