package wtf.atani.screen.main.atani.page.impl.second;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiRenameWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.screen.main.atani.button.impl.SimpleButton;
import wtf.atani.screen.main.atani.button.impl.WorldButton;
import wtf.atani.screen.main.atani.page.impl.SecondPage;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.render.RenderUtil;

import java.util.List;

public class SinglePlayerPage extends SecondPage implements Methods {
    public SinglePlayerPage(GuiScreen guiScreen, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        super(guiScreen, "SinglePlayer", pageX, pageY, pageWidth, pageHeight, screenWidth, screenHeight);
    }

    private SaveFormatComparator selected;

    public void launchWorld(SaveFormatComparator saveFormatComparator) {
        this.mc.displayGuiScreen(null);

        SaveFormatComparator data = saveFormatComparator;
        String s = data.getFileName();
        String s1 = data.getDisplayName();

        if (this.mc.getSaveLoader().canLoadWorld(s))
        {
            this.mc.launchIntegratedServer(s, s1, null);
        }
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        float sixthY = this.screenHeight / 6F;
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        for(MenuButton menuButton : this.menuButtons) {
            if(menuButton instanceof WorldButton) {
                WorldButton worldButton = (WorldButton) menuButton;
                if(worldButton.getSaveFormatComparator() == selected)
                    worldButton.selected = true;
                else
                    worldButton.selected = false;
            }
            menuButton.draw(mouseX, mouseY);
        }
    }

    private List<SaveFormatComparator> list;

    @Override
    public void refresh() {
        float sixthY = this.screenHeight / 6F;
        float fourthX = this.screenWidth/ 4F;
        this.pageX = fourthX * 2 + 7;
        this.pageY = sixthY * 0.5f;
        this.pageWidth = fourthX;
        this.pageHeight = sixthY * 3;
        this.menuButtons.clear();
        if(list != null)
            list.clear();
        try {
            list = Minecraft.getMinecraft().getSaveLoader().getSaveList();
        } catch (AnvilConverterException e) {
            throw new RuntimeException(e);
        }
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        float buttonY = usableAreaY;
        for(SaveFormatComparator saveFormatComparator : list) {
            this.menuButtons.add(new WorldButton(saveFormatComparator, pageX + 1, buttonY, pageWidth - 2, () -> selected = saveFormatComparator));
            buttonY += 40;
        }
        this.menuButtons.add(new SimpleButton("Play", usableAreaX, usableAreaY + usableAreaHeight - 30, usableAreaWidth / 2, () -> {
            if(selected != null)
                launchWorld(selected);
        }));
        this.menuButtons.add(new SimpleButton("Create", usableAreaX + usableAreaWidth / 2, usableAreaY + usableAreaHeight - 30, usableAreaWidth / 2, () -> mc.displayGuiScreen(new GuiCreateWorld(guiScreen))));
        this.menuButtons.add(new SimpleButton("Rename", usableAreaX, usableAreaY + usableAreaHeight - 15, usableAreaWidth / 2, () -> {
            if(selected != null)
                mc.displayGuiScreen(new GuiRenameWorld(guiScreen, selected.getFileName()));
        }));
        this.menuButtons.add(new SimpleButton("Delete", usableAreaX + usableAreaWidth / 2, usableAreaY + usableAreaHeight - 15, usableAreaWidth / 2, () -> {
            if(selected != null) {
                ISaveFormat isaveformat = this.mc.getSaveLoader();
                isaveformat.flushCache();
                isaveformat.deleteWorldDirectory(selected.getFileName());
                refresh();
            }
        }));
    }
}
