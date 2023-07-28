package wtf.atani.screen.main.atani;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.page.impl.FirstPage;
import wtf.atani.screen.main.atani.page.impl.SecondPage;
import wtf.atani.screen.main.atani.page.impl.second.*;
import wtf.atani.utils.interfaces.ClientInformationAccess;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.RoundedUtil;
import wtf.atani.utils.render.shader.shaders.GLSLShader;

public class AtaniMainMenu extends GuiScreen implements GuiYesNoCallback, ClientInformationAccess
{

    private GLSLShader glslShader = new GLSLShader("/assets/minecraft/atani/shaders/shader.fsh");
    private final long timer;

    public AtaniMainMenu() {
        this.timer = System.currentTimeMillis();
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
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */

    private FirstPage firstPage;
    private SecondPage secondPage;

    public void initGui() {
        if(firstPage != null) {
            firstPage.update(this.width, this.height);
            firstPage.refresh();
        }
        if(secondPage != null) {
            secondPage.update(this.width, this.height);
            secondPage.refresh();
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(0, 0, this.width, this.height, new Color(16, 16, 16).getRGB());

        GL11.glPushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();

        glslShader.drawShader((int) (width * 1.5f), height, (System.currentTimeMillis() - timer) / 1000F);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);
        GL11.glEnd();
        GL20.glUseProgram(0);

        GlStateManager.color(1, 1, 1);
        GL11.glPopMatrix();

        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float fourthX = this.width / 4F;
        float sixthY = this.height / 6F;
        RoundedUtil.drawRoundOutline(fourthX - 7, sixthY * 0.5f, fourthX, sixthY * 3, 15, 3, new Color(16, 16, 16).brighter().brighter(), new Color(16, 16, 16).brighter());
        RoundedUtil.drawRoundOutline(fourthX * 2 + 7, sixthY * 0.5f, fourthX, sixthY * 3,15, 3, new Color(16, 16, 16).brighter().brighter(), new Color(16, 16, 16).brighter());
        if(this.firstPage == null) {
            float rectX = fourthX - 7;
            float rectY = sixthY * 0.5f;
            float rectWidth = fourthX;
            float rectHeight = sixthY * 3;
            this.firstPage = new FirstPage(this, rectX, rectY, rectWidth, rectHeight, this.width, this.height);
            this.firstPage.refresh();
        }
        this.firstPage.draw(mouseX, mouseY);
        second: {
            float rectX = fourthX * 2 + 7;
            float rectY = sixthY * 0.5f;
            float rectWidth = fourthX;
            float rectHeight = sixthY * 3;
            bigger.drawCenteredStringWithShadow(firstPage.getSelected(), rectX + rectWidth / 2, rectY + 7, -1);
            if(this.secondPage == null || this.secondPage.getName() != firstPage.getSelected()) {
                switch (firstPage.getSelected()) {
                    case "License":
                        secondPage = new LicensePage(this, rectX, rectY, rectWidth, rectHeight, this.width, this.height);
                        secondPage.refresh();
                        break;
                    case "SinglePlayer":
                        secondPage = new SinglePlayerPage(this, rectX, rectY, rectWidth, rectHeight, this.width, this.height);
                        secondPage.refresh();
                        break;
                    case "MultiPlayer":
                        secondPage = new MultiPlayerPage(this, rectX, rectY, rectWidth, rectHeight, this.width, this.height);
                        secondPage.refresh();
                        break;
                    case "Options":
                        secondPage = new OptionsPage(this, rectX, rectY, rectWidth, rectHeight, this.width, this.height);
                        secondPage.refresh();
                        break;
                    case "Account Manager":
                        secondPage = new AccountPage(this, rectX, rectY, rectWidth, rectHeight, this.width, this.height);
                        secondPage.refresh();
                        break;
                    default:
                        secondPage = null;
                        break;
                }
            }
            if(this.secondPage != null) {
                secondPage.draw(mouseX, mouseY);
            }
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(firstPage != null)
            firstPage.mouseClicked(mouseX, mouseY, mouseButton);
        if(secondPage != null)
            secondPage.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
