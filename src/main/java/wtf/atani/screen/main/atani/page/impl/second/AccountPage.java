package wtf.atani.screen.main.atani.page.impl.second;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Mouse;
import wtf.atani.account.Account;
import wtf.atani.account.storage.AccountStorage;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.screen.main.atani.button.impl.AccountButton;
import wtf.atani.screen.main.atani.button.impl.ServerButton;
import wtf.atani.screen.main.atani.button.impl.SimpleButton;
import wtf.atani.screen.main.atani.button.impl.WorldButton;
import wtf.atani.screen.main.atani.page.impl.SecondPage;
import wtf.atani.screen.main.atani.page.impl.second.multiplayer.GuiScreenAddServer;
import wtf.atani.screen.misc.GuiAltLogin;
import wtf.atani.utils.render.RenderUtil;

public class AccountPage extends SecondPage {
    public AccountPage(GuiScreen guiScreen, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        super(guiScreen, "Account Manager", pageX, pageY, pageWidth, pageHeight, screenWidth, screenHeight);
    }

    private Account selected;
    private int scroll;
    private boolean addingAccounts = false, directingLogging = false;

    @Override
    public void draw(int mouseX, int mouseY) {
        if(RenderUtil.isHovered(mouseX, mouseY, this.pageX, this.pageY, this.pageWidth, this.pageHeight)) {
            scroll = (int) (Math.min(scroll + Mouse.getDWheel() / 10.0f, 0));
        }
        float sixthY = this.screenHeight / 6F;
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(usableAreaX, usableAreaY, usableAreaWidth, usableAreaHeight - 30);
        for(MenuButton menuButton : this.menuButtons) {
            if(menuButton instanceof AccountButton) {
                AccountButton accountButton = (AccountButton) menuButton;
                accountButton.scroll = scroll;
                if(accountButton.getAccount() == selected)
                    accountButton.selected = true;
                else
                    accountButton.selected = false;
                menuButton.draw(mouseX, mouseY);
            }
        }
        RenderUtil.endScissorBox();
        for(MenuButton menuButton : this.menuButtons) {
            if(!(menuButton instanceof AccountButton)) {
                menuButton.draw(mouseX, mouseY);
            }
        }
    }

    @Override
    public void refresh() {
        scroll = 0;
        float sixthY = this.screenHeight / 6F;
        float fourthX = this.screenWidth/ 4F;
        this.pageX = fourthX * 2 + 7;
        this.pageY = sixthY * 0.5f;
        this.pageWidth = fourthX;
        this.pageHeight = sixthY * 3;
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        this.menuButtons.clear();
        int i = 0;
        for(Account account : AccountStorage.getInstance().getList()) {
            this.menuButtons.add(new AccountButton(account, usableAreaX, usableAreaY + i * 15, usableAreaWidth, () -> selected = account));
            i++;
        }
        this.menuButtons.add(new SimpleButton("Use", usableAreaX, usableAreaY + usableAreaHeight - 30, usableAreaWidth / 2, () -> {
            if(selected != null)
                selected.login();
        }));
        this.menuButtons.add(new SimpleButton("Add", usableAreaX + usableAreaWidth / 2, usableAreaY + usableAreaHeight - 30, usableAreaWidth / 2, () -> {
            addingAccounts = true;
            Minecraft.getMinecraft().displayGuiScreen(new GuiAltLogin(this));
        }));
        this.menuButtons.add(new SimpleButton("Direct", usableAreaX, usableAreaY + usableAreaHeight - 15, usableAreaWidth / 2, () -> {
            directingLogging = true;
            Minecraft.getMinecraft().displayGuiScreen(new GuiAltLogin(this));
        }));
        this.menuButtons.add(new SimpleButton("Delete", usableAreaX + usableAreaWidth / 2, usableAreaY + usableAreaHeight - 15, usableAreaWidth / 2, () -> {
            AccountStorage.getInstance().remove(this.selected);
            this.selected = null;
            this.refresh();
        }));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for(MenuButton menuButton : menuButtons) {
            FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
            float usableAreaX = pageX + 1;
            float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
            float usableAreaWidth = pageWidth - 2;
            float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
            if(RenderUtil.isHovered(mouseX, mouseY, menuButton.getPosX(), menuButton.getPosY() + menuButton.scroll, menuButton.getWidth(), menuButton.getHeight())) {
                if(RenderUtil.isHovered(mouseX, mouseY, usableAreaX, usableAreaY, usableAreaWidth, usableAreaHeight - 30) || !(menuButton instanceof AccountButton))
                    menuButton.getAction().run();
            }
        }
    }

    public void confirmClicked(Account acc, boolean result, int id)
    {
        if (this.addingAccounts)
        {
            this.addingAccounts = false;

            if (result)
            {
                AccountStorage.getInstance().add(acc);
                refresh();
            }

            Minecraft.getMinecraft().displayGuiScreen(guiScreen);
        }
        if (this.directingLogging)
        {
            this.directingLogging = false;

            if (result)
            {
                acc.login();
            }

            Minecraft.getMinecraft().displayGuiScreen(guiScreen);
        }
    }
}
