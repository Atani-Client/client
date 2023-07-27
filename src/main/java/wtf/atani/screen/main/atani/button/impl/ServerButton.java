package wtf.atani.screen.main.atani.button.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.ServerData;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.animation.Direction;

import java.awt.*;
import java.util.Date;
import java.util.List;

public class ServerButton extends MenuButton {

    private ServerData serverData;
    public boolean selected = false;

    public ServerButton(ServerData serverData, float posX, float posY, float width, Runnable action) {
        super(serverData.serverName, posX, posY, width, 40, action);
        this.serverData = serverData;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if(RenderUtil.isHovered(mouseX, mouseY, posX, posY, width, height) || selected) {
            this.hoveringAnimation.setDirection(Direction.FORWARDS);
        } else {
            this.hoveringAnimation.setDirection(Direction.BACKWARDS);
        }
        RenderUtil.drawRect(posX, posY, width, height, new Color(255, 255, 255, (int) (20 * hoveringAnimation.getOutput())).getRGB());
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 19);
        normal.drawStringWithShadow(String.format("%s", serverData.serverName), posX + 10, posY + 2 + 1, -1);
        String rawMOTD = serverData.serverMOTD == null ? "Waiting" : serverData.serverMOTD;
        List<String> list = normal.listFormattedStringToWidth(rawMOTD, 220);

        for (int i = 0; i < Math.min(list.size(), 2); ++i)
        {
            normal.drawString((String)list.get(i), posX + 10, posY + 15 + 1 + i * (normal.FONT_HEIGHT + 1), 8421504);
        }
    }


    public ServerData getServerData() {
        return serverData;
    }
}
