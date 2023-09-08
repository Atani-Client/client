package tech.atani.client.feature.guis.screens.mainmenu.atani;

import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.guis.elements.background.ShaderBackground;
import tech.atani.client.feature.guis.screens.mainmenu.atani.button.AtaniButton;
import tech.atani.client.feature.guis.screens.mainmenu.atani.guis.AtaniAltManager;
import tech.atani.client.feature.guis.screens.mainmenu.atani.guis.AtaniMultiPlayerMenu;
import tech.atani.client.feature.guis.screens.mainmenu.atani.guis.AtaniSinglePlayerMenu;
import tech.atani.client.feature.module.impl.hud.PostProcessing;
import tech.atani.client.utility.discord.DiscordRP;
import tech.atani.client.utility.interfaces.ClientInformationAccess;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.animation.advanced.Direction;
import tech.atani.client.utility.render.animation.advanced.impl.DecelerateAnimation;
import tech.atani.client.utility.render.shader.render.ingame.RenderableShaders;
import tech.atani.client.utility.render.shader.shaders.RoundedShader;

import java.awt.*;
import java.io.IOException;

public class AtaniLoginScreen extends GuiScreen implements GuiYesNoCallback, ClientInformationAccess
{
    private String input = "";
    public static ShaderBackground shaderBackground;

    public AtaniLoginScreen() {
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
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if(isFocused(this.width / 2 - 100, ( this.height / 2 - (4 * 30) / 2) - 40,200, 20, mouseX, mouseY)) {
            if (keyCode == Keyboard.KEY_BACK && input.length() > 0) {
                input = input.substring(0, input.length() - 1);
            } else if (Character.isDigit(typedChar) && input.length() < 4) {
                input += typedChar;
            }
        }

        if (keyCode == Keyboard.KEY_ESCAPE) {
            return;
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        DiscordRP.update("Login Screen...", String.format("Running " + CLIENT_VERSION));

        this.buttonList.clear();

        int fullButtonHeight = 4 * 30;

        int buttonX = this.width / 2 - 100;
        int buttonY = this.height / 2 - fullButtonHeight / 2;

        this.buttonList.add(new AtaniButton(0, buttonX, buttonY, "Login"));

        this.mc.func_181537_a(false);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
       switch (button.id) {
           case 0:

               break;
       }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer frBig = FontStorage.getInstance().findFont("Roboto Medium", 16);
        FontRenderer frSmall = FontStorage.getInstance().findFont("Roboto", 16);
        RenderUtil.drawRect(0, 0, this.width, this.height, new Color(16, 16, 16).getRGB());

        RenderableShaders.render(true, true, () -> {
            RoundedShader.drawRound((float) this.width / 2 - 100, ((float) this.height / 2 - (float) (4 * 30) / 2) - 40, 200, 20, 5, new Color(255,0,0,205));
        });
        if(!PostProcessing.getInstance().isEnabled() || !PostProcessing.getInstance().bloom.getValue())
            RoundedShader.drawRound((float) this.width / 2 - 100, ((float) this.height / 2 - (float) (4 * 30) / 2) - 40, 200, 20, 5, new Color(0,0,0,50));
        this.drawString(frBig,"UID", this.width / 2 - 100, (this.height / 2 - (4 * 30) / 2) - 55, Color.white.getRGB());
        this.drawString(frSmall,input, this.width / 2 - 95, (this.height / 2 - (4 * 30) / 2) - 32, Color.white.getRGB());

        shaderBackground.render();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean isFocused(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

}
