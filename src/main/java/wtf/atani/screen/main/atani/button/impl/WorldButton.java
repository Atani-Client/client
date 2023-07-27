package wtf.atani.screen.main.atani.button.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.world.storage.SaveFormatComparator;
import org.lwjgl.Sys;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.animation.Direction;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorldButton extends MenuButton {

    private final SaveFormatComparator saveFormatComparator;
    public boolean selected = false;

    public WorldButton(SaveFormatComparator saveFormatComparator, float posX, float posY, float width, Runnable action) {
        super(saveFormatComparator.getFileName(), posX, posY, width, 40, action);
        this.saveFormatComparator = saveFormatComparator;
    }

    private final DateFormat field_146633_h = new SimpleDateFormat();

    @Override
    public void draw(int mouseX, int mouseY) {
        if(RenderUtil.isHovered(mouseX, mouseY, posX, posY, width, height) || selected) {
            this.hoveringAnimation.setDirection(Direction.FORWARDS);
        } else {
            this.hoveringAnimation.setDirection(Direction.BACKWARDS);
        }
        RenderUtil.drawRect(posX, posY, width, height, new Color(255, 255, 255, (int) (20 * hoveringAnimation.getOutput())).getRGB());
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 19);
        normal.drawStringWithShadow(String.format("%s (%s)", saveFormatComparator.getDisplayName(), saveFormatComparator.getFileName()), posX + 10, posY + 2 + 1, -1);
        normal.drawStringWithShadow(String.format("Last Played: %s", this.field_146633_h.format(new Date(saveFormatComparator.getLastTimePlayed()))), posX + 10, posY + 15 + 1, -1);
        normal.drawStringWithShadow(saveFormatComparator.getEnumGameType().getName().substring(0, 1).toUpperCase() + saveFormatComparator.getEnumGameType().getName().substring(1), posX + 10, posY + 15 + 15 - 2 + 1, -1);
    }

    public SaveFormatComparator getSaveFormatComparator() {
        return saveFormatComparator;
    }
}
