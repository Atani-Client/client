package wtf.atani.screen.click.component.frame;

import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.screen.click.component.Component;
import wtf.atani.screen.click.component.module.ModuleComponent;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.shader.render.ingame.RenderableShaders;

import java.awt.*;
import java.io.IOException;

public class Frame extends Component {

    private Category category;
    private float posX, posY, width, elementHeight;

    public Frame(Category category, float posX, float posY, float width, float elementHeight) {
        this.category = category;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.elementHeight = elementHeight;

        float y = posY + elementHeight;

        for(Module module : ModuleStorage.getInstance().getModules(this.category)) {
            this.subComponents.add(new ModuleComponent(module, posX, y, width, elementHeight));
            y += elementHeight;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderableShaders.renderAndRun(() -> {
            RenderUtil.drawRect(posX, posY, width, getFinalHeight(), new Color(0, 0, 0, 180).getRGB());
        });
        FontStorage.getInstance().findFont("Roboto", 19).drawTotalCenteredStringWithShadow(category.getName(), posX + width / 2, posY + elementHeight / 2, -1);
        for(Component component : this.subComponents) {
            component.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for(Component component : this.subComponents) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public float getFinalHeight() {
        float height = elementHeight;
        for(Component component : this.subComponents) {
            height += component.getFinalHeight();
        }
        return height;
    }

}
