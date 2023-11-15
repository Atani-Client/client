package tech.atani.client.feature.guis.screens.mainmenu.atani;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tech.atani.client.feature.account.Account;
import tech.atani.client.feature.account.storage.AccountStorage;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.guis.screens.mainmenu.atani.button.AtaniButton;
import tech.atani.client.feature.guis.screens.mainmenu.atani.guis.*;
import tech.atani.client.protection.GithubAPI;
import tech.atani.client.utility.discord.DiscordRP;
import tech.atani.client.utility.interfaces.ClientInformationAccess;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.feature.guis.elements.background.ShaderBackground;
import tech.atani.client.utility.render.shader.render.ingame.RenderableShaders;
import tech.atani.client.utility.render.shader.shaders.RoundedShader;

import java.awt.*;
import java.io.IOException;
public class AtaniMainMenu extends GuiScreen implements GuiYesNoCallback, ClientInformationAccess
{
    public static ShaderBackground shaderBackground;
    public AtaniMainMenu() {
        if(shaderBackground == null) {
            shaderBackground = new ShaderBackground(new ResourceLocation("atani/shaders/fragment/ataniWave.glsl"));
            shaderBackground.init();
        }
    }
    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }


    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
3     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        DiscordRP.update("In Main Menu", String.format("Logged in as %s (%s)", GithubAPI.username, GithubAPI.uid));
        this.buttonList.clear();
        int fullButtonHeight = 4 * 30;
        int buttonX = this.width / 2 - 100;
        int buttonY = this.height / 2 - fullButtonHeight / 2;
        this.buttonList.add(new AtaniButton(0, buttonX, buttonY, "SinglePlayer"));
        buttonY += 30;
        this.buttonList.add(new AtaniButton(1, buttonX, buttonY, "MultiPlayer"));
        buttonY += 30;
        this.buttonList.add(new AtaniButton(2, buttonX, buttonY, "Alts"));
        buttonY += 30;
        this.buttonList.add(new AtaniButton(3, buttonX, buttonY, 95, 20, "Options"));
        this.buttonList.add(new AtaniButton(4, buttonX, buttonY + 30, "Changelog"));
        this.buttonList.add(new AtaniButton(5, buttonX + 105, buttonY, 95, 20, "Quit"));
        this.mc.func_181537_a(false);
    }
    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new AtaniSinglePlayerMenu(this));
                break;
            case 1:
                mc.displayGuiScreen(new AtaniMultiPlayerMenu(this));
                break;
            case 2:
                mc.displayGuiScreen(new AtaniAltManager());
                break;
            case 3:
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case 4:
                mc.displayGuiScreen(new AtaniChangeLog());
                break;
            case 5:
                mc.shutdown();
                break;
        }
    }
    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(0, 0, this.width, this.height, new Color(16, 16, 16).getRGB());
        shaderBackground.render();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
