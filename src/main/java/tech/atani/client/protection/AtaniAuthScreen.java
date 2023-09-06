package tech.atani.client.protection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.guis.elements.background.ShaderBackground;
import tech.atani.client.loader.Modification;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.shader.ShaderProgram;
import tech.atani.client.utility.render.shader.enums.ShaderType;

import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AtaniAuthScreen extends GuiScreen {

    private final GuiScreen previousScreen;
    private GuiTextField username;
    private String status = "§7Waiting...";
    private final ShaderBackground backgroundShader;
    private long initTime = System.currentTimeMillis();
    FontRenderer text;

    public AtaniAuthScreen(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
        //TODO: Bigger text and better looking buttons
        text =  FontStorage.getInstance().findFont("SF Regular", 21);
        this.backgroundShader = new ShaderBackground(new ResourceLocation("atani/shaders/fragment/radar.glsl"));
        this.backgroundShader.init();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        ScaledResolution sr = new ScaledResolution(this.mc);
        /*GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1.0F, -1.0F);
        GL11.glVertex2f(-1.0F, 1.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glVertex2f(1.0F, -1.0F);
        GL11.glEnd();
        GL20.glUseProgram(0);*/
        RenderUtil.drawRect(0, 0, this.width, this.height, new Color(16, 16, 16).getRGB());
        this.backgroundShader.render();
        this.username.drawTextBox();
        this.text.drawCenteredString("Atani Auth", width / 2, height / 4 + 24, -1);
        this.text.drawCenteredString(status, width / 2, height / 4 + 44, -1);
        if (this.username.getText().isEmpty()) {
            this.drawString(mc.fontRendererObj, "UID", width / 2 - 96, height / 4 + 30 + 80, -7829368);
        }
        this.drawExit(mouseX, mouseY);
        this.drawLogin(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawExit(int mouseX, int mouseY) {
        int l1 = height / 4 + 24;
        int w = 200;
        int h = 25;
        int x1 = width / 2 - 100;
        int x2 = x1 + w;
        int y1 = l1 + 158;
        int y2 = y1 + h;
        boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x1, y1, w, h);
        RenderUtil.drawRect((float)x1, (float)y1, (float)w, (float)h, hovered ? (new Color(255, 0, 0, 80)).getRGB() : (new Color(153, 9, 9, 80)).getRGB());
        RenderUtil.drawRect((float)x1, (float)y1 + 20, (float)w, (float)h - 20, (new Color(200, 0, 0)).getRGB());
        this.text.drawCenteredString("Exit", (float)((x1 + x2) / 2), (float)((y1 + y2 - 15) / 2), Color.black.getRGB());
    }

    private void drawLogin(int mouseX, int mouseY) {
        int l1 = height / 4 + 24;
        int w = 200;
        int h = 25;
        int x1 = width / 2 - 100;
        int x2 = x1 + w;
        int y1 = l1 + 128;
        int y2 = y1 + h;
        boolean hovered = RenderUtil.isHovered(mouseX, mouseY, x1, y1, w, h);
        RenderUtil.drawRect((float)x1, (float)y1, (float)w, (float)h, hovered ? (new Color(255, 0, 0, 80)).getRGB() : (new Color(153, 9, 9, 80)).getRGB());
        RenderUtil.drawRect((float)x1, (float)y1 + 20, (float)w, (float)h - 20, (new Color(200, 0, 0)).getRGB());
        this.text.drawCenteredString("Login", (float)((x1 + x2) / 2), (float)((y1 + y2 - 15) / 2), Color.black.getRGB());
    }

    @Override
    public void initGui() {
        UUIDHandler.validate();
        this.username = new GuiTextField(-1, this.mc.fontRendererObj, width / 2 - 100, height / 4 + 24 + 80, 200, 20);
        this.username.setFocused(true);
        this.initTime = System.currentTimeMillis();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == Keyboard.KEY_TAB) {
            if (!this.username.isFocused()) {
                this.username.setFocused(true);
            }
        } else if (key == Keyboard.KEY_RETURN) {
            login();
        }
        this.username.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int l1 = height / 4 + 24;
        this.username.mouseClicked(mouseX, mouseY, mouseButton);
        if (RenderUtil.isHovered(mouseX, mouseY, width / 2 - 100, l1 + 158, 200, 25) && mouseButton == 0) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc.shutdown();
        }
        if (RenderUtil.isHovered(mouseX, mouseY, width / 2 - 100, l1 + 128, 200, 25) && mouseButton == 0) {
            login();
        }
    }

    private void login() {
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        status = "§eLogging in...";

        AntiVM.run();

        if (username.getText() == null) {
            status = "§cPlease type in your UID.";
            return;
        }

        try {
            Integer.valueOf(username.getText());
        }
        catch (NumberFormatException e) {
            status = "§c" + username.getText() + " is not a number.";
            return;
        }

        try {
            URL url = new URL(Modification.APIURL);
            HttpURLConnection uc = (HttpURLConnection ) url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            uc.setRequestMethod("GET");
            int responseCode = uc.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                status = "§cConnection Failed, could not connect to the backend.";
            }
        } catch (IOException e) {
            status = "§cConnection Failed, could not connect to the backend.";
            e.printStackTrace();
            return;
        }


        if (!HWIDManager.isWhitelisted()) {
            try {
                AntiSkidUtils.terminate("Your HWID is not whitelisted, please contact the admins to get it reset.", 0, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            status = "§cHWID not whitelisted.";
            return;
        }

        if (HWIDManager.getUID() != Integer.parseInt(username.getText())) {
            status = "§cInvalid UID.";
            return;
        }

        HWIDManager.getUser();
        Minecraft.getMinecraft().displayGuiScreen(previousScreen);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

}
