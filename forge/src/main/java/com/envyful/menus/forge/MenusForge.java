package com.envyful.menus.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.menus.forge.command.MenuCommand;
import com.envyful.menus.forge.config.MenuConfig;
import com.envyful.menus.forge.data.Menu;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

import java.io.File;
import java.util.List;
import java.util.Map;

@Mod(
        modid = "menus",
        name = "Menus Forge",
        version = MenusForge.VERSION,
        acceptableRemoteVersions = "*"
)
public class MenusForge {

    protected static final String VERSION = "0.3.0";

    private static MenusForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private Map<String, Menu> loadedMenus = Maps.newHashMap();

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartedEvent event) {
        instance = this;

        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

        UtilConcurrency.runAsync(() -> {
            File file = new File(MenuConfig.PATH);

            if (!file.exists()) {
                file.mkdirs();
            }

            this.handleDirectory(new File(MenuConfig.PATH));

            if (!this.loadedMenus.containsKey("example")) {
                this.addMenu(new Menu("example"));
            }
        });

        this.commandFactory.registerCommand(FMLCommonHandler.instance().getMinecraftServerInstance(), new MenuCommand());
    }

    public static MenusForge getInstance() {
        return instance;
    }

    public ForgePlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public List<String> getLoadedNames() {
        return Lists.newArrayList(this.loadedMenus.keySet());
    }


    public void handleDirectory(File file) {
        if(file.listFiles() == null) {
            return;
        }

        for (File listFile : file.listFiles()) {
            if(listFile.isDirectory()) {
                handleDirectory(listFile);
                continue;
            }

            if(!listFile.getName().endsWith(".yml")) {
                continue;
            }

            String name = listFile.getPath().replace((MenuConfig.PATH + File.separator), "")
                    .replace(".yml", "");
            Menu menu = new Menu(name);

            this.addMenu(menu);
        }
    }

    public void addMenu(Menu menu) {
        if (menu == null || menu.getIdentifier() == null) {
            return;
        }

        this.loadedMenus.put(menu.getIdentifier().toLowerCase(), menu);
    }

    public Menu getMenu(String id) {
        return this.loadedMenus.get(id);
    }

    public void unloadAll() {
        this.loadedMenus.clear();
    }
}
