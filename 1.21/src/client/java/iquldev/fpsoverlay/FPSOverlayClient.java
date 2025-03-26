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
    private boolean useOverlayDynamicText = false;
    private boolean useAdvancedDynamicText = false;
    private long lastOverlaySwitchTime = System.currentTimeMillis();
    private long lastAdvancedSwitchTime = System.currentTimeMillis();

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

            long currentTime = System.currentTimeMillis();

            if (FPSOverlayConfig.overlayDynamicInterval > 0 && !FPSOverlayConfig.overlayDynamicText.isEmpty() && currentTime - lastOverlaySwitchTime >= FPSOverlayConfig.overlayDynamicInterval * 1000L) {
                useOverlayDynamicText = !useOverlayDynamicText;
                lastOverlaySwitchTime = currentTime;
            }

            if (FPSOverlayConfig.advancedDynamicInterval > 0 && !FPSOverlayConfig.advancedDynamicText.isEmpty() &&  currentTime - lastAdvancedSwitchTime >= FPSOverlayConfig.advancedDynamicInterval * 1000L) {
                useAdvancedDynamicText = !useAdvancedDynamicText;
                lastAdvancedSwitchTime = currentTime;
            }

            String text = (FPSOverlayConfig.overlayDynamicInterval > 0 && useOverlayDynamicText) ?
                          formatText(FPSOverlayConfig.overlayDynamicText, client) :
                          formatText(FPSOverlayConfig.overlayText, client);

            String minMaxText = (FPSOverlayConfig.advancedDynamicInterval > 0 && useAdvancedDynamicText) ?
                                formatText(FPSOverlayConfig.advancedDynamicText, client) :
                                formatText(FPSOverlayConfig.advancedText, client);

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
        int hour = Integer.parseInt(String.format("%tH", System.currentTimeMillis()));

        String clockSymbol;
        if (hour >= 6 && hour < 12) {
            clockSymbol = "🕖";
        } else if (hour >= 12 && hour < 18) {
            clockSymbol = "🕓";
        } else if (hour >= 18 && hour < 22) {
            clockSymbol = "🕒";
        } else {
            clockSymbol = "🕰️";
        }

        String timeWithClock = String.format("%s %s", systemTime, clockSymbol);

        String x = String.format("%.1f", client.player.getX());
        String y = String.format("%.1f", client.player.getY());
        String z = String.format("%.1f", client.player.getZ());

        String facing = getFacingDirection(client);

        double playerSpeed = Math.sqrt(Math.pow(client.player.getVelocity().x, 2) +
                                       Math.pow(client.player.getVelocity().y, 2) +
                                       Math.pow(client.player.getVelocity().z, 2));
        String playerSpeedStr = String.format("%.2f ⚡", playerSpeed);

        assert client.world != null;
        String dayTime = (client.world.getTimeOfDay() % 24000L < 13000L) ? "Day \uD83C\uDF1E" : "Night \uD83C\uDF19";

        text = text.replace("{fps}", String.valueOf(client.getCurrentFps()));
        text = text.replace("{x}", x);
        text = text.replace("{y}", y);
        text = text.replace("{z}", z);
        text = text.replace("{systemTime}", timeWithClock);
        text = text.replace("{minFps}", String.valueOf(minFps));
        text = text.replace("{maxFps}", String.valueOf(maxFps));
        text = text.replace("{facing}", facing);
        text = text.replace("{playerSpeed}", playerSpeedStr);
        text = text.replace("{dayTime}", dayTime);

        return text;
    }

    private String getFacingDirection(MinecraftClient client) {
        assert client.player != null;
        float yaw = client.player.getYaw();

        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return "South \uD83E\uDDED";
        } else if (yaw >= 45 && yaw < 135) {
            return "West \uD83E\uDDED";
        } else if (yaw >= 135 && yaw < 225) {
            return "North \uD83E\uDDED";
        } else if (yaw >= 225 && yaw < 315) {
            return "East \uD83E\uDDED";
        }
        return "Unknown";
    }

    private void drawRoundedRect(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int paddingWidth = 3;
        int paddingHeight = 3;

        context.fill(x1, y1, x2, y2, color);

        context.fill(x1 - paddingWidth, y1 + paddingHeight, x1, y2 - paddingHeight, color);

        context.fill(x2, y1 + paddingHeight, x2 + paddingWidth, y2 - paddingHeight, color);
    }
}