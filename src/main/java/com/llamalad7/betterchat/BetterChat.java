/*
 *       Copyright (C) 2020-present LlamaLad7 <https://github.com/lego3708>
 *
 *       This program is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published
 *       by the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.llamalad7.betterchat;

import com.llamalad7.betterchat.command.CommandConfig;
import com.llamalad7.betterchat.handlers.InjectHandler;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BetterChat.MODID, version = BetterChat.VERSION, name = "Better Chat")
public class BetterChat {
    public static final String MODID = "betterchat";
    public static final String VERSION = "1.0";
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
        MinecraftForge.EVENT_BUS.register(new InjectHandler());
        ClientCommandHandler.instance.registerCommand(new CommandConfig());
    }
    public static ChatSettings getSettings() {
        return settings;
    }
}
