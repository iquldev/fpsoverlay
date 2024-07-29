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
            FPSOverlayConfig.OverlayPosition overlayPosition = FPSOverlayConfig.overlayPosition;
            int overlayBackgroundColor = parseColor(FPSOverlayConfig.overlayBackgroundColor, FPSOverlayConfig.overlayTransparency);
            int advancedBackgroundColor = parseColor(FPSOverlayConfig.advancedBackgroundColor, FPSOverlayConfig.advancedTransparency);

            MinecraftClient client = MinecraftClient.getInstance();

            int padding = 5;
            int x = 10;
            int y = 10;

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            fps = client.getCurrentFps();
            String text = fps + " FPS";
            int textWidthFps = client.textRenderer.getWidth(text);
            int textHeightFps = client.textRenderer.fontHeight;

            String minMaxText = minFps + " ▼ " + maxFps + " ▲";
            int textWidthMinMax = client.textRenderer.getWidth(minMaxText);

            switch (overlayPosition) {
                case TOP_RIGHT:
                    x = screenWidth - 10 - textWidthFps - padding;
                    y = 10;
                    break;
                case BOTTOM_LEFT:
                    x = 10;
                    y = screenHeight - 10 - textHeightFps - padding;
                    break;
                case BOTTOM_RIGHT:
                    x = screenWidth - 10 - textWidthFps - padding;
                    y = screenHeight - 10 - textHeightFps - padding;
                    break;
                case TOP_LEFT:
                default:
                    x = 10;
                    y = 10;
                    break;
            }

            int advancedX = x;
            int advancedY = y;

            if (isShowed && !isF1Pressed) {
                context.fill(x - padding, y - padding, x + textWidthFps + padding, y + textHeightFps + padding, overlayBackgroundColor);
                context.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, false);

                switch (overlayPosition) {
                    case TOP_RIGHT:
                    case BOTTOM_RIGHT:
                        advancedX = x - textWidthMinMax - padding * 3;
                        break;
                    case TOP_LEFT:
                    case BOTTOM_LEFT:
                    default:
                        advancedX = x + textWidthFps + padding * 3;
                        break;
                }
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

                if (!isShowed) {
                    switch (overlayPosition) {
                        case TOP_RIGHT:
                            advancedX = screenWidth - 10 - textWidthMinMax - padding * 2;
                            advancedY = 10;
                            break;
                        case BOTTOM_RIGHT:
                            advancedX = screenWidth - 10 - textWidthMinMax - padding * 2;
                            advancedY = screenHeight - 10 - textHeightFps - padding;
                            break;
                        case BOTTOM_LEFT:
                            advancedX = 10;
                            advancedY = screenHeight - 10 - textHeightFps - padding;
                            break;
                        case TOP_LEFT:
                        default:
                            advancedX = 10;
                            advancedY = 10;
                            break;
                    }
                }

                context.fill(advancedX - padding, advancedY - padding, advancedX + textWidthMinMax + padding, advancedY + textHeightFps + padding, advancedBackgroundColor);
                context.drawText(client.textRenderer, minMaxText, advancedX, advancedY, 0xFFFFFFFF, false);
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

    private int parseColor(String colorStr, int transparency) {
        try {
            if (colorStr.startsWith("#")) {
                colorStr = colorStr.substring(1);
            }
            int color = Integer.parseInt(colorStr, 16);
            int alpha = (int) ((transparency / 100.0) * 255);
            return (alpha << 24) | color;
        } catch (NumberFormatException e) {
            return 0x80000000;
        }
    }
}