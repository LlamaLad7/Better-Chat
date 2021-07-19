package com.llamalad7.betterchat;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;


public class ChatSettings {
    private Configuration config;
    public boolean smooth;
    public boolean clear;
    public int xOffset;
    public int yOffset;

    public ChatSettings(Configuration config) {
        this.config = config;
    }

    public void saveConfig() {
        updateConfig(false);
        config.save();
    }

    public void loadConfig() {
        config.load();
        updateConfig(true);
    }

    public void resetConfig() {
        Property prop;

        prop = config.get("All", "Clear", false);
        prop.set(clear = false);

        prop = config.get("All", "Smooth", true);
        prop.set(smooth = true);

        prop = config.get("All", "xOffset", 0);
        prop.set(xOffset = 0);

        prop = config.get("All", "yOffset", 0);
        prop.set(yOffset = 0);
        Minecraft.getMinecraft().gameSettings.chatScale = 1.0f;
        Minecraft.getMinecraft().gameSettings.chatWidth = 1.0f;
        config.save();
    }

    private void updateConfig(boolean load) {
        Property prop;

        prop = config.get("All", "Clear", false);
        if (load) clear = prop.getBoolean();
        else prop.set(clear);

        prop = config.get("All", "Smooth", true);
        if (load) smooth = prop.getBoolean();
        else prop.set(smooth);

        prop = config.get("All", "xOffset", 0);
        if (load) xOffset = prop.getInt();
        else prop.set(xOffset);

        prop = config.get("All", "yOffset", 0);
        if (load) yOffset = prop.getInt();
        else prop.set(yOffset);
    }
}
