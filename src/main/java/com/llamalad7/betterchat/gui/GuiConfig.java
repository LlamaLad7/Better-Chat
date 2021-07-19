package com.llamalad7.betterchat.gui;

import com.llamalad7.betterchat.BetterChat;
import com.llamalad7.betterchat.ChatSettings;
import com.llamalad7.betterchat.ducks.Configurable;
import com.llamalad7.betterchat.mixins.AccessorGuiIngameForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiConfig extends GuiScreen {
    private ChatSettings settings;
    private List<IChatComponent> exampleChat = new ArrayList<>();
    private boolean dragging = false;
    private int chatLeft, chatRight, chatTop, chatBottom, dragStartX, dragStartY;
    private GuiButton clearButton, smoothButton;
    private GuiSlider scaleSlider, widthSlider;

    public GuiConfig() {
        this.settings = BetterChat.getSettings();
        exampleChat.add(new ChatComponentText(I18n.format("gui.betterchat.text.example3")));
        exampleChat.add(new ChatComponentText(I18n.format("gui.betterchat.text.example2")));
        exampleChat.add(new ChatComponentText(I18n.format("gui.betterchat.text.example1")));
    }

    @Override
    public void initGui() {
        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        if (chat instanceof Configurable) {
            ((Configurable) chat).setConfiguring(true);
        }
        buttonList.add(clearButton = new GuiButton(0, width / 2 - 120, height / 2 - 50, 240, 20, getPropName("clear") + " " + getColoredBool("clear", settings.clear)));
        buttonList.add(smoothButton = new GuiButton(1, width / 2 - 120, height / 2 - 25, 240, 20, getPropName("smooth") + " " + getColoredBool("smooth", settings.smooth)));
        buttonList.add(scaleSlider = new GuiSlider(3, width / 2 - 120, height / 2, 240, 20, getPropName("scale") + " ", "%", 0, 100, this.mc.gameSettings.chatScale*100, false, true));
        buttonList.add(widthSlider = new GuiSlider(4, width / 2 - 120, height / 2 + 25, 240, 20, getPropName("width") + " ", "px", 40, 320, GuiNewChat.calculateChatboxWidth(this.mc.gameSettings.chatWidth), false, true));
        buttonList.add(new GuiButton(2, width / 2 - 120, height / 2 + 50, 240, 20, getPropName("reset")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(mc.fontRendererObj, I18n.format("gui.betterchat.text.title", EnumChatFormatting.GREEN + EnumChatFormatting.BOLD.toString() + "Better Chat" + EnumChatFormatting.RESET, EnumChatFormatting.AQUA + EnumChatFormatting.BOLD.toString() + "LlamaLad7"), width / 2, height / 2 - 75, 0xFFFFFF);
        drawCenteredString(mc.fontRendererObj, I18n.format("gui.betterchat.text.drag"), width / 2, height / 2 - 63, 0xFFFFFF);
        if (dragging) {
            settings.xOffset += mouseX - dragStartX;
            settings.yOffset += mouseY - dragStartY;
            dragStartX = mouseX;
            dragStartY = mouseY;
        }
        this.mc.gameSettings.chatScale = (float) scaleSlider.getValueInt()/100;
        this.mc.gameSettings.chatWidth = ((float) widthSlider.getValueInt()-40)/280;

        AccessorGuiIngameForge guiIngameForge = (AccessorGuiIngameForge) Minecraft.getMinecraft().ingameGUI;

        RenderGameOverlayEvent.Chat event = new RenderGameOverlayEvent.Chat(guiIngameForge.getEventParent(), 0, 0);
        if (MinecraftForge.EVENT_BUS.post(event)) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)event.posX, (float)event.posY, 0.0F);
        drawExampleChat();
        GlStateManager.popMatrix();

        guiIngameForge.invokePost(RenderGameOverlayEvent.ElementType.CHAT);
    }

    public void drawExampleChat() {
        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        List<IChatComponent> lines = new ArrayList<>();
        int i = MathHelper.floor_float((float) chat.getChatWidth() / chat.getChatScale());
        for (IChatComponent line : exampleChat) {
            lines.addAll(GuiUtilRenderComponents.splitText(line, i, this.mc.fontRendererObj, false, false));
        }
        Collections.reverse(lines);
        GlStateManager.pushMatrix();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        GlStateManager.translate(2.0F + settings.xOffset, 20.0F + settings.yOffset + scaledresolution.getScaledHeight() - 48, 0.0F);
        float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
        float f1 = this.mc.gameSettings.chatScale;
        int k = MathHelper.ceiling_float_int(chat.getChatWidth() / f1);
        GlStateManager.scale(f1, f1, 1.0F);
        int i1 = 0;
        double d0 = 1.0D;
        int l1 = (int)(255.0D * d0);
        l1 = (int)((float)l1 * f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        chatLeft = settings.xOffset+2;
        chatRight = (int) (settings.xOffset + (k+4)*f1);
        chatBottom = 20 + settings.yOffset + scaledresolution.getScaledHeight() - 48;
        for (IChatComponent message : lines) {
            int j2 = -i1 * 9;
            if (!settings.clear) drawRect(0, j2 - 9, k + 4, j2, l1 / 2 << 24);
            this.mc.fontRendererObj.drawStringWithShadow(message.getFormattedText(), 0.0F, (float)(j2 - 8), 16777215 + (l1 << 24));
            ++i1;
        }
        chatTop = (int) (20 + settings.yOffset + scaledresolution.getScaledHeight() - 48 + (-i1*9)*f1);
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
        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        if (chat instanceof Configurable) {
            ((Configurable) chat).setConfiguring(false);
        }
        this.mc.gameSettings.saveOptions();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                settings.clear = !settings.clear;
                button.displayString = getPropName("clear") + " " + getColoredBool("clear", settings.clear);
                break;
            case 1:
                settings.smooth = !settings.smooth;
                button.displayString = getPropName("smooth") + " " + getColoredBool("smooth", settings.smooth);
                break;
            case 2:
                settings.resetConfig();
                clearButton.displayString = getPropName("clear") + " " + getColoredBool("clear", settings.clear);
                smoothButton.displayString = getPropName("smooth") + " " + getColoredBool("smooth", settings.smooth);
//                this.mc.gameSettings.chatScale = 1.0f;
//                this.mc.gameSettings.chatWidth = 1.0f;
                scaleSlider.setValue(this.mc.gameSettings.chatScale*100);
                scaleSlider.updateSlider();
                widthSlider.setValue(GuiNewChat.calculateChatboxWidth(this.mc.gameSettings.chatWidth));
                widthSlider.updateSlider();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String getColoredBool(String prop, boolean bool) {
        if (bool) {
            return EnumChatFormatting.GREEN + I18n.format("gui.betterchat.text." + prop + ".enabled");
        }

        return EnumChatFormatting.RED + I18n.format("gui.betterchat.text." + prop + ".disabled");
    }
    private String getPropName(String prop) {
        return I18n.format("gui.betterchat.text." + prop + ".name");
    }
}
