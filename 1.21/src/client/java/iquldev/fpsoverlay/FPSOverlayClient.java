package iquldev.fpsoverlay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class FPSOverlayClient implements ClientModInitializer {
	private int minFps = Integer.MAX_VALUE;
	private int maxFps = Integer.MIN_VALUE;
	private int fps = 0;
	private long lastUpdateTime = System.currentTimeMillis();
    private static final int UPDATE_INTERVAL = 30000;
    private static KeyBinding keyBinding;
    private boolean isF1Pressed = false;
	@Override
	public void onInitializeClient() {
        MidnightConfig.init("fpsoverlay", FPSOverlayConfig.class);
		HudRenderCallback.EVENT.register((context, tickDelta) -> {
            boolean isShowed = FPSOverlayConfig.isShowed;
            boolean isAdvancedShowed = FPSOverlayConfig.isAdvancedShowed;
            // FPSOverlayConfig.OverlayPosition overlayPosition = FPSOverlayConfig.overlayPosition;

            MinecraftClient client = MinecraftClient.getInstance();

            int x = 10;
            int y = 10;
            int padding = 5;

            fps = client.getCurrentFps();
            String text = fps + " FPS";
            int backgroundColor = 0x80000000;
            int textWidthFps = client.textRenderer.getWidth(text);
            int textHeightFps = client.textRenderer.fontHeight;

            if (isShowed && !isF1Pressed) {
                context.fill(x - padding, y - padding, x + textWidthFps + padding, y + textHeightFps + padding, backgroundColor);
                context.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, false);
            }

            if (isAdvancedShowed && !isF1Pressed) {
                long currentTime = System.currentTimeMillis();

                if (fps < minFps) {
                    minFps = fps;
                }
                if (fps > maxFps) {
                    maxFps = fps;
                }

                if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
                    minFps = fps;
                    maxFps = fps;
                    lastUpdateTime = currentTime;
                }

                String minMaxText = minFps + " ▼ " + maxFps + " ▲";
                int advancedX = isShowed ? x + textWidthFps + 15 : x;
                int textWidthMinMax = client.textRenderer.getWidth(minMaxText);

                context.fill(advancedX - padding, y - padding, advancedX + textWidthMinMax + padding, y + textHeightFps + padding, backgroundColor);
                context.drawText(client.textRenderer, minMaxText, advancedX, y, 0xFFFFFFFF, false);
            }
        });

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
}