package wtf.atani.screen.main.atani.page.impl.second;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.screen.main.atani.button.impl.ServerButton;
import wtf.atani.screen.main.atani.button.impl.SimpleButton;
import wtf.atani.screen.main.atani.button.impl.WorldButton;
import wtf.atani.screen.main.atani.page.impl.SecondPage;
import wtf.atani.screen.main.atani.page.impl.second.multiplayer.GuiScreenAddServer;
import wtf.atani.utils.interfaces.Methods;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class MultiPlayerPage extends SecondPage implements Methods {
    public MultiPlayerPage(GuiScreen guiScreen, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        super(guiScreen, "MultiPlayer", pageX, pageY, pageWidth, pageHeight, screenWidth, screenHeight);
    }

    private static final ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private final OldServerPinger oldServerPinger = new OldServerPinger();
    private ServerData selected;
    private boolean addingServer;

    @Override
    public void draw(int mouseX, int mouseY) {
        float sixthY = this.screenHeight / 6F;
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        for(MenuButton menuButton : this.menuButtons) {
            if(menuButton instanceof WorldButton) {
                ServerButton serverButton = (ServerButton) menuButton;
                if(serverButton.getServerData() == selected)
                    serverButton.selected = true;
                else
                    serverButton.selected = false;
            }
            menuButton.draw(mouseX, mouseY);
        }
    }

    @Override
    public void refresh() {
        float sixthY = this.screenHeight / 6F;
        float fourthX = this.screenWidth/ 4F;
        this.pageX = fourthX * 2 + 7;
        this.pageY = sixthY * 0.5f;
        this.pageWidth = fourthX;
        this.pageHeight = sixthY * 3;
        this.menuButtons.clear();
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        float buttonY = usableAreaY;
        for(ServerData serverData : loadServerList()) {
            this.menuButtons.add(new ServerButton(serverData, pageX + 1, buttonY, pageWidth - 2, () -> selected = serverData));
            buttonY += 40;
        }
        pingServers();
        this.menuButtons.add(new SimpleButton("Connect", usableAreaX, usableAreaY + usableAreaHeight - 30, usableAreaWidth / 2, () -> {
            if(selected != null)
                mc.displayGuiScreen(new GuiConnecting(guiScreen, mc, selected));
        }));
        this.menuButtons.add(new SimpleButton("Add", usableAreaX + usableAreaWidth / 2, usableAreaY + usableAreaHeight - 30, usableAreaWidth / 2, () -> {
            this.addingServer = true;
            mc.displayGuiScreen(new GuiScreenAddServer(this, this.selected = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "", false)));
        }));
        this.menuButtons.add(new SimpleButton("Refresh", usableAreaX, usableAreaY + usableAreaHeight - 15, usableAreaWidth / 2, () -> {
            this.refresh();
        }));
        this.menuButtons.add(new SimpleButton("Delete", usableAreaX + usableAreaWidth / 2, usableAreaY + usableAreaHeight - 15, usableAreaWidth / 2, () -> {
            if(selected != null) {
                MenuButton found = null;
                for (MenuButton button : this.menuButtons) {
                    if(button instanceof ServerButton) {
                        ServerButton serverButton = (ServerButton) button;
                        if (serverButton.getServerData() == selected) {
                            found = serverButton;
                        }
                    }
                }
                this.menuButtons.remove(found);
                selected = null;
                saveServerList();
            }
        }));
    }

    public void confirmClicked(boolean result, int id)
    {
        if (this.addingServer)
        {
            this.addingServer = false;

            if (result)
            {
                ServerData serverData = selected;
                this.toBeAdded.add(new ServerButton(serverData, pageX + 1, (this.menuButtons.isEmpty() ? 0 : this.menuButtons.get(this.menuButtons.size() - 1).getPosY()) + 40, pageWidth - 2, () -> selected = serverData));
                this.saveServerList();
                this.refresh();
            }

            this.mc.displayGuiScreen(guiScreen);
        }
    }

    public void pingServers() {
        for (MenuButton button : this.menuButtons) {
            if(button instanceof ServerButton) {
                ServerButton serverButton = (ServerButton) button;
                if (serverButton.getServerData() != null) {
                    executor.submit(() -> {
                        try {
                            oldServerPinger.ping(serverButton.getServerData());
                        } catch (UnknownHostException var2) {
                            serverButton.getServerData().pingToServer = -1L;
                            serverButton.getServerData().serverMOTD = "§4Can't resolve hostname";
                        } catch (Exception var3) {
                            serverButton.getServerData().pingToServer = -1L;
                            serverButton.getServerData().serverMOTD = "§4Can't connect to server.";
                        }
                    });
                }
            }
        }
    }

    private ArrayList<MenuButton> toBeAdded = new ArrayList<>();

    public void saveServerList() {
        try {
            NBTTagList nbttaglist = new NBTTagList();
            for (MenuButton button : this.toBeAdded) {
                ServerButton serverButton = (ServerButton) button;
                if (serverButton.getServerData() != null) {
                    System.out.println(serverButton.getServerData().serverIP);
                    nbttaglist.appendTag(((ServerButton) button).getServerData().getNBTCompound());
                }
            }
            for (MenuButton button : this.menuButtons) {
                ServerButton serverButton = (ServerButton) button;
                if (serverButton.getServerData() != null) {
                    nbttaglist.appendTag(((ServerButton) button).getServerData().getNBTCompound());
                }
            }

            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("servers", nbttaglist);
            CompressedStreamTools.write(nbttagcompound, new File(this.mc.mcDataDir, "servers.dat"));
        } catch (Exception exception) {

        }
    }

    public ArrayList<ServerData> loadServerList() {
        try {
            ArrayList<ServerData> servers = new ArrayList<>();
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));

            if (nbttagcompound == null) {
                return servers;
            }

            NBTTagList nbttaglist = nbttagcompound.getTagList("servers", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i)));
            }
            return servers;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ArrayList<ServerData>();
    }

}
