package iquldev.fpsoverlay;

import iquldev.fpsoverlay.render.OverlayRenderer;
import iquldev.fpsoverlay.stats.FpsStats;
import iquldev.fpsoverlay.text.DynamicTextManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.render.RenderTickCounter;

public class FPSOverlayClient implements ClientModInitializer {
    private static KeyBinding keyBinding;
    private boolean isF1Pressed = false;
    
    private final FpsStats fpsStats = new FpsStats();
    private final DynamicTextManager dynamicTextManager = new DynamicTextManager();

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CROSSHAIR,
            Identifier.of("fpsoverlay", "fps_overlay"),
            this::renderFpsOverlay
        );

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "fpsoverlay.keys.show",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F1,
            "fpsoverlay.category.keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                isF1Pressed = !isF1Pressed;
            }
        });
    }

    private void renderFpsOverlay(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        dynamicTextManager.updateDynamicText();
        
        OverlayRenderer.renderOverlay(context, client, fpsStats, dynamicTextManager, isF1Pressed);
    }
}