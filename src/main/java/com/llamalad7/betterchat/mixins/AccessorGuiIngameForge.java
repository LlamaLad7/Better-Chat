package com.llamalad7.betterchat.mixins;

import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiIngameForge.class)
public interface AccessorGuiIngameForge {
    @Accessor(remap = false)
    RenderGameOverlayEvent getEventParent();

    @Invoker(remap = false)
    void invokePost(RenderGameOverlayEvent.ElementType type);
}
