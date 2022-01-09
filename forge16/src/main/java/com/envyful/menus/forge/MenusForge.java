package com.envyful.menus.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.concurrency.ForgeTaskBuilder;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.menus.forge.command.MenuCommand;
import com.envyful.menus.forge.config.MenuConfig;
import com.envyful.menus.forge.config.MenusConfig;
import com.envyful.menus.forge.config.MenusLocale;
import com.envyful.menus.forge.data.Menu;
import com.envyful.menus.forge.data.MenuTabCompleter;
import com.envyful.menus.forge.data.RequirementFactory;
import com.envyful.menus.forge.data.task.MenuUpdateTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mod(MenusForge.MOD_ID)
@Mod.EventBusSubscriber
public class MenusForge {

    protected static final String MOD_ID = "menus";
    protected static final String VERSION = "2.0.0";

    private static MenusForge instance;

    public MenusForge() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private Map<String, Menu> loadedMenus = Maps.newHashMap();

    private MenusConfig config;
    private MenusLocale locale;

    @SubscribeEvent
    public void onServerStarting(FMLServerStartedEvent event) {
        instance = this;
        this.reloadConfig();
        RequirementFactory.init();

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

        new ForgeTaskBuilder()
                .task(new MenuUpdateTask())
                .interval(1)
                .async(true)
                .delay(20L)
                .start();
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(MenusConfig.class);
            this.locale = YamlConfigFactory.getInstance(MenusLocale.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onCommandRegistration(RegisterCommandsEvent event) {
        this.commandFactory.registerCompleter(new MenuTabCompleter());

        this.commandFactory.registerInjector(Menu.class, (sender, args) -> {
            Menu menu = this.getMenu(args[0]);

            if(menu == null) {
                sender.sendMessage(new StringTextComponent("Menu doesn't exist!"), Util.DUMMY_UUID);
                return null;
            }

            return menu;
        });

        this.commandFactory.registerCommand(event.getDispatcher(), new MenuCommand());
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

    public MenusConfig getConfig() {
        return this.config;
    }

    public MenusLocale getLocale() {
        return this.locale;
    }
}
