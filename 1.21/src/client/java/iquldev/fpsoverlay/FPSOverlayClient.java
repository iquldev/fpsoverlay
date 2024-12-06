package iquldev.fpsoverlay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            boolean isShowed = FPSOverlayConfig.isShowed;
            boolean isAdvancedShowed = FPSOverlayConfig.isAdvancedShowed;
            FPSOverlayConfig.OverlayPosition overlayPosition = FPSOverlayConfig.overlayPosition;

            int overlayBackgroundColor = parseColor(FPSOverlayConfig.overlayBackgroundColor, FPSOverlayConfig.overlayTransparency);
            int advancedBackgroundColor = parseColor(FPSOverlayConfig.advancedBackgroundColor, FPSOverlayConfig.advancedTransparency);

            int overlayTextColor = parseColor(FPSOverlayConfig.overlayTextColor, 100);
            int advancedTextColor = parseColor(FPSOverlayConfig.advancedTextColor, 100);

            MinecraftClient client = MinecraftClient.getInstance();

            int padding = 5;
            int x;
            int y;

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            fps = client.getCurrentFps();

            String text = formatText(FPSOverlayConfig.overlayText, client);
            String minMaxText = formatText(FPSOverlayConfig.advancedText, client);

            int textWidthFps = client.textRenderer.getWidth(text);
            int textHeightFps = client.textRenderer.fontHeight;

            int textWidthMinMax = client.textRenderer.getWidth(minMaxText);

            y = switch (overlayPosition) {
                case TOP_RIGHT -> {
                    x = screenWidth - 15 - textWidthFps - padding;
                    yield 10;
                }
                case BOTTOM_LEFT -> {
                    x = 15;
                    yield screenHeight - 10 - textHeightFps - padding;
                }
                case BOTTOM_RIGHT -> {
                    x = screenWidth - 15 - textWidthFps - padding;
                    yield screenHeight - 10 - textHeightFps - padding;
                }
                default -> {
                    x = 15;
                    yield 10;
                }
            };

            int advancedX = x;
            int advancedY = y;

            if (isShowed && !isF1Pressed) {
                drawRoundedRect(context, x - padding, y - padding, x + textWidthFps + padding, y + textHeightFps + padding, overlayBackgroundColor);
                context.drawText(client.textRenderer, text, x, y, overlayTextColor, false);

                advancedX = switch (overlayPosition) {
                    case TOP_RIGHT, BOTTOM_RIGHT -> x - textWidthMinMax - padding * 4;
                    default -> x + textWidthFps + padding * 4;
                };
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
                    advancedY = switch (overlayPosition) {
                        case TOP_RIGHT -> {
                            advancedX = screenWidth - 10 - textWidthMinMax - padding * 2;
                            yield 10;
                        }
                        case BOTTOM_RIGHT -> {
                            advancedX = screenWidth - 10 - textWidthMinMax - padding * 2;
                            yield screenHeight - 10 - textHeightFps - padding;
                        }
                        case BOTTOM_LEFT -> screenHeight - 10 - textHeightFps - padding;
                        default -> 10;
                    };
                }

                drawRoundedRect(context, advancedX - padding, advancedY - padding, advancedX + textWidthMinMax + padding, advancedY + textHeightFps + padding, advancedBackgroundColor);
                context.drawText(client.textRenderer, minMaxText, advancedX, advancedY, advancedTextColor, false);
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
            if (colorStr == null || colorStr.isEmpty()) {
                return 0x80000000;
            }

            if (colorStr.charAt(0) == '#') {
                colorStr = colorStr.substring(1);
            }

            int color = Integer.parseInt(colorStr, 16);

            int alpha = (int) (transparency * 2.55);

            return (alpha << 24) | color;
        } catch (NumberFormatException e) {
            return 0x80000000;
    }
}


    private String formatText(String text, MinecraftClient client) {
        if (client.player == null) {
            return text;
        }

        String systemTime = String.format("%tH:%tM", System.currentTimeMillis(), System.currentTimeMillis());

        String x = String.format("%.1f", client.player.getX());
        String y = String.format("%.1f", client.player.getY());
        String z = String.format("%.1f", client.player.getZ());

        text = text.replace("{fps}", String.valueOf(client.getCurrentFps()));
        text = text.replace("{x}", x);
        text = text.replace("{y}", y);
        text = text.replace("{z}", z);
        text = text.replace("{systemTime}", systemTime);
        text = text.replace("{minFps}", String.valueOf(minFps));
        text = text.replace("{maxFps}", String.valueOf(maxFps));

        return text;
    }

    private void drawRoundedRect(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int paddingWidth = 3; // Ширина боковых выступов
        int paddingHeight = 3; // Высота выступов

        context.fill(x1, y1, x2, y2, color);

        context.fill(x1 - paddingWidth, y1 + paddingHeight, x1, y2 - paddingHeight, color);

        context.fill(x2, y1 + paddingHeight, x2 + paddingWidth, y2 - paddingHeight, color);
    }
}