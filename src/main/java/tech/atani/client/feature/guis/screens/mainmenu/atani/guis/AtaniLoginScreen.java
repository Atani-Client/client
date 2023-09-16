package tech.atani.client.feature.guis.screens.mainmenu.atani.guis;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.guis.elements.background.ShaderBackground;
import tech.atani.client.feature.guis.screens.mainmenu.atani.AtaniMainMenu;
import tech.atani.client.feature.guis.screens.mainmenu.atani.button.AtaniButton;
import tech.atani.client.feature.module.impl.hud.PostProcessing;
import tech.atani.client.protection.GithubAPI;
import tech.atani.client.protection.antitamper.impl.Destruction;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.internet.NetUtils;
import tech.atani.client.utility.render.animation.advanced.Direction;
import tech.atani.client.utility.render.animation.advanced.impl.DecelerateAnimation;
import tech.atani.client.utility.system.HWIDUtil;
import tech.atani.client.utility.discord.DiscordRP;
import tech.atani.client.utility.interfaces.ClientInformationAccess;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.shader.render.ingame.RenderableShaders;
import tech.atani.client.utility.render.shader.shaders.RoundedShader;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

public class AtaniLoginScreen extends GuiScreen implements GuiYesNoCallback, ClientInformationAccess
{
    private String input = "", status = "Please login with your UID.";
    private int cursorCounter;
    private boolean isCursorVisible;
    private ShaderBackground shaderBackground;
    private final DecelerateAnimation decelerateAnimation = new DecelerateAnimation(200, 1, Direction.BACKWARDS);

    public AtaniLoginScreen() {
        if(shaderBackground == null) {
            shaderBackground = new ShaderBackground(new ResourceLocation("atani/shaders/fragment/ataniWave.glsl"));
            shaderBackground.init();
        }

        this.cursorCounter = 0;
        this.isCursorVisible = true;
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if (RenderUtil.isHovered(mouseX, mouseY, (float) this.width / 2 - 100, ((float) this.height / 2 - (float) (4 * 30) / 2) - 10, 200, 20)) {
            if (keyCode == Keyboard.KEY_BACK && input.length() > 0) {
                input = input.substring(0, input.length() - 1);
            } else if (Character.isDigit(typedChar) && input.length() < 4) {
                input += typedChar;
            }
        }

        if (keyCode == Keyboard.KEY_ESCAPE) return;

        cursorCounter = 0;
        isCursorVisible = true;
    }


    public void initGui() {
        DiscordRP.update("Logging in", String.format("Running " + CLIENT_VERSION));

        this.buttonList.clear();

        int fullButtonHeight = 4 * 30;

        int buttonX = this.width / 2 - 100;
        int buttonY = this.height / 2 - fullButtonHeight / 2;

        this.buttonList.add(new AtaniButton(0, buttonX, buttonY + 30, "Login"));
        buttonY += 30;
        this.buttonList.add(new AtaniButton(1, buttonX, buttonY + 30, "Copy HWID"));

        this.mc.func_181537_a(false);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
       switch (button.id) {
           case 0:
               if (input.length() != 4) return;

               GithubAPI.login(input);

               switch ((GithubAPI.login(input))) {
                   case 4:
                       status = "Please enter in a valid UID!";
                       break;
                   case 3:
                       status = "Couldn't connect to the internet.";
                       break;
                   case 2:
                       NetUtils.sendToWebhook("**Someone failed to authorize on Atani!** \n Error: HWID is not whitelisted \n" + "Used UUID: ``" + input + "``\n" + "HWID: ``" + HWIDUtil.getHashedHWID() + "...``\n");
                       try {
                           Destruction.selfDestructJARFile();
                       } catch (Exception e) {
                           throw new RuntimeException(e);
                       }
                       Minecraft.getMinecraft().shutdownMinecraftApplet();
                   case 1:
                       status = "Invalid UID!";
                       break;
                   case 0:
                       mc.displayGuiScreen(new AtaniMainMenu());
                       status = String.format("Welcome to Atani, %s!", GithubAPI.username);
                       break;
               }
               break;
           case 1:
               StringSelection stringSelection = new StringSelection(HWIDUtil.getHashedHWID());
               Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
               clipboard.setContents(stringSelection, null);
               status = "Your HWID has been copied to the clipboard!";
               break;
       }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer frBigBig = FontStorage.getInstance().findFont("Roboto Medium", 24);
        FontRenderer frBig = FontStorage.getInstance().findFont("Roboto Medium", 16);

        RenderUtil.drawRect(0, 0, this.width, this.height, new Color(16, 16, 16).getRGB());

        if(RenderUtil.isHovered(mouseX, mouseY, (float) this.width / 2 - 100, ( (float) this.height / 2 - (float) (4 * 30) / 2) - 10,200, 20)) {
            this.decelerateAnimation.setDirection(Direction.FORWARDS);
        } else {
            this.decelerateAnimation.setDirection(Direction.BACKWARDS);
        }

        RenderableShaders.render(true, true, () -> {
            RoundedShader.drawRound((float) this.width / 2 - 100, ((float) this.height / 2 - (float) (4 * 30) / 2) - 10, 200, 20, 5, new Color(0, 0, 0, 205 + (int) (this.decelerateAnimation.getOutput() * 50)));
        });
        if(!PostProcessing.getInstance().isEnabled() || !PostProcessing.getInstance().bloom.getValue())
            RoundedShader.drawRound((float) this.width / 2 - 100, ((float) this.height / 2 - (float) (4 * 30) / 2) - 10, 200, 20, 5, new Color(0,0,0,50));

        this.drawString(frBigBig,"Welcome!", this.width / 2 - 100, (this.height / 2 - (4 * 30) / 2) - 40, Color.white.getRGB());
        this.drawString(frBig,status, this.width / 2 - 100, (this.height / 2 - (4 * 30) / 2) - 25, new Color(230,230,230).getRGB());

        String displayedText = input;
        if (input.length() < 4) {
            if (cursorCounter >= 20) {
                isCursorVisible = !isCursorVisible;
                cursorCounter = 0;
            }

            cursorCounter++;

            if (isCursorVisible) {
                displayedText += "_";
            }
        }

        this.drawString(frBig, displayedText, this.width / 2 - 95, (this.height / 2 - (4 * 30) / 2) - 2, Color.white.getRGB());

        shaderBackground.render();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
