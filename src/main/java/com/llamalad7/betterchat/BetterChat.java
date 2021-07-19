package com.llamalad7.betterchat;

import com.llamalad7.betterchat.command.CommandConfig;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BetterChat.MODID, version = BetterChat.VERSION, name = "Better Chat")
public class BetterChat {
    public static final String MODID = "betterchat";
    public static final String VERSION = "1.5";
    private static ChatSettings settings;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        boolean firstRun = false;
        if (!event.getSuggestedConfigurationFile().exists()) {
            firstRun = true;
        }
        settings = new ChatSettings(new Configuration(event.getSuggestedConfigurationFile()));
        if (firstRun) settings.resetConfig();
        settings.loadConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandConfig());
    }

    public static ChatSettings getSettings() {
        return settings;
    }
}
