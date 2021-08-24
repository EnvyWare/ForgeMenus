package com.envyful.menus.forge;

import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.concurrency.ForgeUpdateBuilder;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.menus.forge.command.MenuCommand;
import com.envyful.menus.forge.config.MenuConfig;
import com.envyful.menus.forge.data.Menu;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.bstats.forge.Metrics;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Mod(
        modid = "menus",
        name = "Menus Forge",
        version = MenusForge.VERSION,
        acceptableRemoteVersions = "*"
)
public class MenusForge {

    protected static final String VERSION = "0.1.0";

    private static MenusForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private Map<String, Menu> loadedMenus = Maps.newHashMap();

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        instance = this;

        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

        Metrics metrics = new Metrics(
                Loader.instance().activeModContainer(),
                event.getModLog(),
                Paths.get("config/"),
                12565 //TODO:
        );

        ForgeUpdateBuilder.instance()
                .name("ForgeMenus")
                .requiredPermission("menus.update.notify")
                .owner("Pixelmon-Development")
                .repo("ForgeMenus")
                .version(VERSION)
                .start();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.commandFactory.registerCommand(event.getServer(), new MenuCommand());
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
        this.loadedMenus.put(menu.getIdentifier().toLowerCase(), menu);
    }

    public Menu getMenu(String id) {
        return this.loadedMenus.get(id);
    }
}
