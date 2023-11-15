package tech.atani.client.feature.guis.screens.mainmenu.atani.guis;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.ResourceLocation;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.guis.elements.background.ShaderBackground;
import tech.atani.client.feature.guis.screens.mainmenu.atani.AtaniMainMenu;
import tech.atani.client.feature.guis.screens.mainmenu.atani.button.AtaniButton;
import tech.atani.client.protection.GithubAPI;
import tech.atani.client.utility.discord.DiscordRP;
import tech.atani.client.utility.interfaces.ClientInformationAccess;
import tech.atani.client.utility.render.RenderUtil;
import java.awt.*;
import java.io.IOException;
public class AtaniChangeLog extends GuiScreen implements GuiYesNoCallback, ClientInformationAccess
{
    public static ShaderBackground shaderBackground;
    public AtaniChangeLog() {
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
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        DiscordRP.update("Looking At ChangeLog", String.format("Logged in as %s (%s)", GithubAPI.username, GithubAPI.uid));
        this.buttonList.add(new AtaniButton(1, this.width / 2 - 100, 500, "Back"));
    }
    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 1) {
            mc.displayGuiScreen(new AtaniMainMenu());
        }
    }
    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Imma make it ACTUALLY have a proper font soon, too lazy now :,)
        RenderUtil.drawRect(0, 0, this.width, this.height, new Color(16, 16, 16).getRGB());
        FontStorage.getInstance().findFont("Roboto", 19).drawString("Changelog - " + CLIENT_VERSION + ":", (float) (this.width / 2) - 50, 20, -1);
        //mc.fontRendererObj.drawCenteredString("Changelog - " + ClientInformationAccess.CLIENT_VERSION + ":", (float) this.width / 2, 20, -1);
        int yellow = Color.HSBtoRGB(60F / 360.0f, 1.0F, 1.0F);
        int green = Color.HSBtoRGB(120F / 360.0f, 1.0F, 1.0F);
        FontStorage.getInstance().findFont("Roboto", 19).drawString("- Made KillAura better", (float) (this.width / 2) - 50, 100, yellow);
        FontStorage.getInstance().findFont("Roboto", 19).drawString("- Fixed Hold AutoBlock (KillAura)", (float) (this.width / 2) - 50, 112, yellow);
        FontStorage.getInstance().findFont("Roboto", 19).drawString("- Fixed Intave Strafe", (float) (this.width / 2) - 50, 124, yellow);
        FontStorage.getInstance().findFont("Roboto", 19).drawString("+ Add ChangeLog Menu", (float) (this.width / 2) - 50, 136, green);
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