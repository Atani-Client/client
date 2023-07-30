package wtf.atani.security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.realmsclient.util.UploadTokenCache;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Bootstrap;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.util.*;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.OpenGLException;
import wtf.atani.loader.ModificationLoader;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.UUID;

// mc whole class will be obfuscated natively
public class ProtectedLaunch {

    public void runMain(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(25565), new Integer[0]);
        OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
        OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().<File>ofType(File.class);
        OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().<File>ofType(File.class);
        OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).<Integer>ofType(Integer.class);
        OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L, new String[0]);
        OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
        OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(854), new Integer[0]);
        OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(480), new Integer[0]);
        OptionSpec<String> optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
        OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
        OptionSpec<String> optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
        OptionSpec<String> optionspec19 = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(args);
        List<String> list = optionset.valuesOf(optionspec19);

        if (!list.isEmpty())
        {
            System.out.println("Completely ignored arguments: " + list);
        }

        String s = (String)optionset.valueOf(optionspec5);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null)
        {
            try
            {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(s, ((Integer)optionset.valueOf(optionspec6)).intValue()));
            }
            catch (Exception var46)
            {
                ;
            }
        }

        final String s1 = (String)optionset.valueOf(optionspec7);
        final String s2 = (String)optionset.valueOf(optionspec8);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2))
        {
            Authenticator.setDefault(new Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        UploadTokenCache.uploadValidRealmsToken();
        int i = ((Integer)optionset.valueOf(optionspec13)).intValue();
        int j = ((Integer)optionset.valueOf(optionspec14)).intValue();
        boolean fullscreen = optionset.has("fullscreen");
        boolean checkGlErrors = optionset.has("checkGlErrors");
        boolean demo = optionset.has("demo");

        String valueOfOptionSpec12 = (String) optionset.valueOf(optionspec12);
        String valueOfOptionSpec15 = (String) optionset.valueOf(optionspec15);
        String valueOfOptionSpec16 = (String) optionset.valueOf(optionspec16);

        Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
        PropertyMap propertyMap1 = gson.fromJson(valueOfOptionSpec15, PropertyMap.class);
        PropertyMap propertyMap2 = gson.fromJson(valueOfOptionSpec16, PropertyMap.class);

        File gameDirectory = (File) optionset.valueOf(optionspec2);
        File assetsDirectory = optionset.has(optionspec3) ? (File) optionset.valueOf(optionspec3) : new File(gameDirectory, "assets/");
        File resourcePacksDirectory = optionset.has(optionspec4) ? (File) optionset.valueOf(optionspec4) : new File(gameDirectory, "resourcepacks/");

        String version = optionset.has(optionspec10) ? (String) optionspec10.value(optionset) : (String) optionspec9.value(optionset);
        String serverAddress = optionset.has(optionspec17) ? (String) optionspec17.value(optionset) : null;
        String proxyHost = (String) optionset.valueOf(optionspec);
        int proxyPort = (Integer) optionset.valueOf(optionspec1);

        Session session = new Session((String) optionspec9.value(optionset), version, (String) optionspec11.value(optionset), (String) optionspec18.value(optionset));

        GameConfiguration.UserInformation userInformation = new GameConfiguration.UserInformation(session, propertyMap1, propertyMap2, proxy);
        GameConfiguration.DisplayInformation displayInformation = new GameConfiguration.DisplayInformation(i, j, fullscreen, checkGlErrors);
        GameConfiguration.FolderInformation folderInformation = new GameConfiguration.FolderInformation(gameDirectory, resourcePacksDirectory, assetsDirectory, serverAddress);
        GameConfiguration.GameInformation gameInformation = new GameConfiguration.GameInformation(demo, valueOfOptionSpec12);
        GameConfiguration.ServerInformation serverInformation = new GameConfiguration.ServerInformation(proxyHost, proxyPort);

        GameConfiguration gameConfiguration = new GameConfiguration(userInformation, displayInformation, folderInformation, gameInformation, serverInformation);

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        createMinecraft(gameConfiguration);
        run();
    }

    private static boolean isNullOrEmpty(String str)
    {
        return str != null && !str.isEmpty();
    }

    private static Minecraft mc;

    private void createMinecraft(GameConfiguration gameConfig) {
        mc = new Minecraft();
        Minecraft.theMinecraft = mc;
        GameConfiguration.FolderInformation folderInfo = gameConfig.folderInfo;
        GameConfiguration.GameInformation gameInfo = gameConfig.gameInfo;
        GameConfiguration.DisplayInformation displayInfo = gameConfig.displayInfo;
        GameConfiguration.UserInformation userInfo = gameConfig.userInfo;
        GameConfiguration.ServerInformation serverInfo = gameConfig.serverInfo;

        mc.mcDataDir = folderInfo.mcDataDir;
        mc.fileAssets = folderInfo.assetsDir;
        mc.fileResourcepacks = folderInfo.resourcePacksDir;
        mc.launchedVersion = gameInfo.version;
        mc.twitchDetails = userInfo.userProperties;
        mc.field_181038_N = userInfo.field_181172_c;
        mc.mcDefaultResourcePack = new DefaultResourcePack(new ResourceIndex(folderInfo.assetsDir, folderInfo.assetIndex).getResourceMap());
        mc.proxy = userInfo.proxy == null ? Proxy.NO_PROXY : userInfo.proxy;
        mc.sessionService = new YggdrasilAuthenticationService(userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        mc.session = userInfo.session;
        mc.logger.info("Setting user: " + mc.session.getUsername());
        mc.logger.info("(Session ID is " + mc.session.getSessionID() + ")");
        mc.isDemo = gameInfo.isDemo;

        int width = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        int height = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;

        mc.displayWidth = width;
        mc.displayHeight = height;
        mc.tempDisplayWidth = width;
        mc.tempDisplayHeight = height;
        mc.fullscreen = displayInfo.fullscreen;
        mc.jvm64bit = mc.isJvm64bit();
        mc.theIntegratedServer = new IntegratedServer(mc);

        if (serverInfo.serverName != null) {
            mc.serverName = serverInfo.serverName;
            mc.serverPort = serverInfo.serverPort;
        }

        ImageIO.setUseCache(false);
        Bootstrap.register();
    }

    public void run()
    {
        mc.running = true;

        try
        {
            startGame();
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
            crashreport.makeCategory("Initialization");
            mc.displayCrashReport(mc.addGraphicsAndWorldToCrashReport(crashreport));
            return;
        }

        while (true)
        {
            try
            {
                while (mc.running)
                {
                    if (!mc.hasCrashed || mc.crashReporter == null)
                    {
                        try
                        {
                            mc.runGameLoop();
                        }
                        catch (OutOfMemoryError var10)
                        {
                            mc.freeMemory();
                            mc.displayGuiScreen(new GuiMemoryErrorScreen());
                            System.gc();
                        }
                    }
                    else
                    {
                        mc.displayCrashReport(mc.crashReporter);
                    }
                }
            }
            catch (MinecraftError var12)
            {
                break;
            }
            catch (ReportedException reportedexception)
            {
                mc.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
                mc.freeMemory();
                mc.logger.fatal((String)"Reported exception thrown!", (Throwable)reportedexception);
                mc.displayCrashReport(reportedexception.getCrashReport());
                break;
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport1 = mc.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
                mc.freeMemory();
                mc.logger.fatal("Unreported exception thrown!", throwable1);
                mc.displayCrashReport(crashreport1);
                break;
            }
            finally
            {
                mc.shutdownMinecraftApplet();
            }

            return;
        }
    }

    public void startGame() throws LWJGLException, IOException
    {
        mc.gameSettings = new GameSettings(mc, mc.mcDataDir);
        mc.defaultResourcePacks.add(mc.mcDefaultResourcePack);
        mc.startTimerHackThread();

        if (mc.gameSettings.overrideHeight > 0 && mc.gameSettings.overrideWidth > 0)
        {
            mc.displayWidth = mc.gameSettings.overrideWidth;
            mc.displayHeight = mc.gameSettings.overrideHeight;
        }

        mc.logger.info("LWJGL Version: " + Sys.getVersion());
        mc.setWindowIcon();
        mc.setInitialDisplayMode();
        mc.createDisplay();
        OpenGlHelper.initializeTextures();
        mc.framebufferMc = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        mc.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        mc.registerMetadataSerializers();
        mc.mcResourcePackRepository = new ResourcePackRepository(mc.fileResourcepacks, new File(mc.mcDataDir, "server-resource-packs"), mc.mcDefaultResourcePack, mc.metadataSerializer_, mc.gameSettings);
        mc.mcResourceManager = new SimpleReloadableResourceManager(mc.metadataSerializer_);
        mc.mcLanguageManager = new LanguageManager(mc.metadataSerializer_, mc.gameSettings.language);
        mc.mcResourceManager.registerReloadListener(mc.mcLanguageManager);
        mc.refreshResources();
        mc.renderEngine = new TextureManager(mc.mcResourceManager);
        mc.mcResourceManager.registerReloadListener(mc.renderEngine);
        mc.drawSplashScreen(mc.renderEngine);
        mc.initStream();
        mc.skinManager = new SkinManager(mc.renderEngine, new File(mc.fileAssets, "skins"), mc.sessionService);
        mc.saveLoader = new AnvilSaveConverter(new File(mc.mcDataDir, "saves"));
        mc.mcSoundHandler = new SoundHandler(mc.mcResourceManager, mc.gameSettings);
        mc.mcResourceManager.registerReloadListener(mc.mcSoundHandler);
        mc.mcMusicTicker = new MusicTicker(mc);
        mc.fontRendererObj = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);

        if (mc.gameSettings.language != null)
        {
            mc.fontRendererObj.setUnicodeFlag(mc.isUnicode());
            mc.fontRendererObj.setBidiFlag(mc.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        mc.standardGalacticFontRenderer = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), mc.renderEngine, false);
        mc.mcResourceManager.registerReloadListener(mc.fontRendererObj);
        mc.mcResourceManager.registerReloadListener(mc.standardGalacticFontRenderer);
        mc.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        mc.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat()
        {
            public String formatString(String p_74535_1_)
            {
                try
                {
                    return String.format(p_74535_1_, new Object[] {GameSettings.getKeyDisplayString(mc.gameSettings.keyBindInventory.getKeyCode())});
                }
                catch (Exception exception)
                {
                    return "Error: " + exception.getLocalizedMessage();
                }
            }
        });
        mc.mouseHelper = new MouseHelper();
        mc.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.cullFace(1029);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        mc.checkGLError("Startup");
        mc.textureMapBlocks = new TextureMap("textures");
        mc.textureMapBlocks.setMipmapLevels(mc.gameSettings.mipmapLevels);
        mc.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, mc.textureMapBlocks);
        mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        mc.textureMapBlocks.setBlurMipmapDirect(false, mc.gameSettings.mipmapLevels > 0);
        mc.modelManager = new ModelManager(mc.textureMapBlocks);
        mc.mcResourceManager.registerReloadListener(mc.modelManager);
        mc.renderItem = new RenderItem(mc.renderEngine, mc.modelManager);
        mc.renderManager = new RenderManager(mc.renderEngine, mc.renderItem);
        mc.itemRenderer = new ItemRenderer(mc);
        mc.mcResourceManager.registerReloadListener(mc.renderItem);
        mc.entityRenderer = new EntityRenderer(mc, mc.mcResourceManager);
        mc.mcResourceManager.registerReloadListener(mc.entityRenderer);
        mc.blockRenderDispatcher = new BlockRendererDispatcher(mc.modelManager.getBlockModelShapes(), mc.gameSettings);
        mc.mcResourceManager.registerReloadListener(mc.blockRenderDispatcher);
        mc.renderGlobal = new RenderGlobal(mc);
        mc.mcResourceManager.registerReloadListener(mc.renderGlobal);
        mc.guiAchievement = new GuiAchievement(mc);
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
        mc.effectRenderer = new EffectRenderer(mc.theWorld, mc.renderEngine);
        mc.checkGLError("Post startup");
        new ModificationLoader().start();
        mc.ingameGUI = new GuiIngame(mc);

        if (mc.serverName != null)
        {
            mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, mc.serverName, mc.serverPort));
        }
        else
        {
            mc.displayGuiScreen(new GuiMainMenu());
        }

        mc.renderEngine.deleteTexture(mc.mojangLogo);
        mc.mojangLogo = null;
        mc.loadingScreen = new LoadingScreenRenderer(mc);

        if (mc.gameSettings.fullScreen && !mc.fullscreen)
        {
            mc.toggleFullscreen();
        }

        try
        {
            Display.setVSyncEnabled(mc.gameSettings.enableVsync);
        }
        catch (OpenGLException var2)
        {
            mc.gameSettings.enableVsync = false;
            mc.gameSettings.saveOptions();
        }

        mc.renderGlobal.makeEntityOutlineShader();
    }

}
