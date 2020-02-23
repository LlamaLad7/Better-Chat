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
package com.llamalad7.betterchat.gui;

import com.llamalad7.betterchat.BetterChat;
import com.llamalad7.betterchat.ChatSettings;
import com.llamalad7.betterchat.handlers.InjectHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.GuiNewChat.calculateChatboxWidth;

public class GuiConfig extends GuiScreen {
    private ChatSettings settings;
    private List<String> exampleChat = new ArrayList<>();
    private boolean dragging = false;
    private int chatLeft, chatRight, chatTop, chatBottom, dragStartX, dragStartY;
    private GuiButton clearButton, smoothButton;
    private GuiSlider scaleSlider, widthSlider;


    public GuiConfig() {
        this.settings = BetterChat.getSettings();
        exampleChat.add("Example Chat");
        exampleChat.add("Testing, testing, 1, 2, 3");
        exampleChat.add("Players' messages will look like this");
    }

    @Override
    public void initGui() {
        InjectHandler.chatGUI.configuring = true;
        buttonList.add(clearButton = new GuiButton(0, width / 2 - 90, height / 2 - 50, 180, 20, "Clear Chat Background: " + getColoredBool(settings.clear)));
        buttonList.add(smoothButton = new GuiButton(1, width / 2 - 90, height / 2 - 25, 180, 20, "Smooth Chat: " + getColoredBool(settings.smooth)));
        buttonList.add(scaleSlider = new GuiSlider(3, width / 2 - 90, height / 2, 180, 20, "Scale: ", "%", 0, 100, this.mc.gameSettings.chatScale*100, false, true));
        buttonList.add(widthSlider = new GuiSlider(3, width / 2 - 90, height / 2 + 25, 180, 20, "Width: ", "px", 40, 320, calculateChatboxWidth(this.mc.gameSettings.chatWidth), false, true));
        buttonList.add(new GuiButton(2, width / 2 - 90, height / 2 + 50, 180, 20, "Reset Config"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(mc.fontRenderer, TextFormatting.GREEN + TextFormatting.BOLD.toString() + "Better Chat " + TextFormatting.RESET + "by " + TextFormatting.AQUA + TextFormatting.BOLD.toString() + "LlamaLad7", width / 2, height / 2 - 75, 0xFFFFFF);
        drawCenteredString(mc.fontRenderer, "Drag the chat to reposition it", width / 2, height / 2 - 63, 0xFFFFFF);
        if (dragging) {
            settings.xOffset += mouseX - dragStartX;
            settings.yOffset += mouseY - dragStartY;
            dragStartX = mouseX;
            dragStartY = mouseY;
        }
        this.mc.gameSettings.chatScale = (float) scaleSlider.getValueInt()/100;
        this.mc.gameSettings.chatWidth = ((float) widthSlider.getValueInt()-40)/280;
        drawExampleChat();
    }

    public void drawExampleChat() {
        GlStateManager.pushMatrix();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        GlStateManager.translate(2.0F + settings.xOffset, 8.0F + settings.yOffset + scaledresolution.getScaledHeight() - 48, 0.0F);
        float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
        float f1 = this.mc.gameSettings.chatScale;
        int k = MathHelper.ceil(InjectHandler.chatGUI.getChatWidth() / f1);
        GlStateManager.scale(f1, f1, 1.0F);
        int i1 = 0;
        double d0 = 1.0D;
        int l1 = (int)(255.0D * d0);
        l1 = (int)((float)l1 * f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        chatLeft = settings.xOffset;
        chatRight = (int) (settings.xOffset + (k+4)*f1);
        chatTop = (int) (8 + settings.yOffset + scaledresolution.getScaledHeight() - 48 + (-3*9)*f1);
        chatBottom = 8 + settings.yOffset + scaledresolution.getScaledHeight() - 48;
        for (String message : exampleChat) {
            int j2 = -i1 * 9;
            if (!settings.clear) drawRect(-2, j2 - 9, k + 4, j2, l1 / 2 << 24);
            this.mc.fontRenderer.drawStringWithShadow(message, 0.0F, (float)(j2 - 8), 16777215 + (l1 << 24));
            ++i1;
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (mouseX >= chatLeft && mouseX <= chatRight && mouseY >= chatTop && mouseY <= chatBottom) {
                dragging = true;
                dragStartX = mouseX;
                dragStartY = mouseY;
            }
        }

    }
    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        dragging = false;
    }

    @Override
    public void onGuiClosed() {
        settings.saveConfig();
        InjectHandler.chatGUI.configuring = false;
        this.mc.gameSettings.saveOptions();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                settings.clear = !settings.clear;
                button.displayString = "Clear Chat Background: " + getColoredBool(settings.clear);
                break;
            case 1:
                settings.smooth = !settings.smooth;
                button.displayString = "Smooth Chat: " + getColoredBool(settings.smooth);
                break;
            case 2:
                settings.resetConfig();
                clearButton.displayString = "Clear Chat Background: " + getColoredBool(settings.clear);
                smoothButton.displayString = "Smooth Chat: " + getColoredBool(settings.smooth);
                this.mc.gameSettings.chatScale = 1.0f;
                this.mc.gameSettings.chatWidth = 1.0f;
                scaleSlider.setValue(this.mc.gameSettings.chatScale*100);
                scaleSlider.updateSlider();
                widthSlider.setValue(calculateChatboxWidth(this.mc.gameSettings.chatWidth));
                widthSlider.updateSlider();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String getColoredBool(boolean bool) {
        if (bool) {
            return TextFormatting.GREEN + "Enabled";
        }

        return TextFormatting.RED + "Disabled";
    }
}
