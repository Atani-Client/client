package wtf.atani.screen.main.atani.button.impl;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.account.Account;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.animation.Direction;

import java.awt.*;

public class AccountButton extends MenuButton {

    private final Account account;
    public int scroll = 0;
    public boolean selected;

    public AccountButton(Account account, float posX, float posY, float width, Runnable action) {
        super(account == null ? "null" : account.getName(), posX, posY, width, 15, action);
        this.account = account;
    }

    public void draw(int mouseX, int mouseY) {
        if (RenderUtil.isHovered(mouseX, mouseY, posX, posY + scroll, width, height) || selected) {
            this.hoveringAnimation.setDirection(Direction.FORWARDS);
        } else {
            this.hoveringAnimation.setDirection(Direction.BACKWARDS);
        }
        RenderUtil.drawRect(posX, posY + scroll, width, height, new Color(255, 255, 255, (int) (20 * hoveringAnimation.getOutput())).getRGB());
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 19);
        normal.drawTotalCenteredStringWithShadow(String.format("%s (%s§r)", account.getName(), account.isCracked() ? "§4Cracked" : "§6Premium"), posX + width / 2, posY + height / 2 + scroll, -1);
    }

    public Account getAccount() {
        return account;
    }
}
