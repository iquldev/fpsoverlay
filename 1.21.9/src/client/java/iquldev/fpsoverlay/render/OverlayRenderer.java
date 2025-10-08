package iquldev.fpsoverlay.render;

import iquldev.fpsoverlay.FPSOverlayConfig;
import iquldev.fpsoverlay.stats.FpsStats;
import iquldev.fpsoverlay.text.DynamicTextManager;
import iquldev.fpsoverlay.util.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class OverlayRenderer {
    private static final int PADDING = 5;

    public static void renderOverlay(DrawContext context, MinecraftClient client, 
                                   FpsStats fpsStats, DynamicTextManager dynamicTextManager, 
                                   boolean isF1Pressed) {
        boolean isShowed = FPSOverlayConfig.isShowed;
        boolean isAdvancedShowed = FPSOverlayConfig.isAdvancedShowed;
        FPSOverlayConfig.OverlayPosition overlayPosition = FPSOverlayConfig.overlayPosition;

        int overlayBackgroundColor = ColorUtils.parseColor(FPSOverlayConfig.overlayBackgroundColor, FPSOverlayConfig.overlayTransparency);
        int advancedBackgroundColor = ColorUtils.parseColor(FPSOverlayConfig.advancedBackgroundColor, FPSOverlayConfig.advancedTransparency);
        int overlayTextColor = ColorUtils.parseColor(FPSOverlayConfig.overlayTextColor, 100);
        int advancedTextColor = ColorUtils.parseColor(FPSOverlayConfig.advancedTextColor, 100);

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        fpsStats.updateFps(client.getCurrentFps());

        String overlayText = dynamicTextManager.getOverlayText(client, fpsStats);
        String advancedText = dynamicTextManager.getAdvancedText(client, fpsStats);

        int overlayTextWidth = client.textRenderer.getWidth(overlayText);
        int overlayTextHeight = client.textRenderer.fontHeight;
        int advancedTextWidth = client.textRenderer.getWidth(advancedText);

        Position overlayPosition_ = calculateOverlayPosition(overlayPosition, screenWidth, screenHeight, overlayTextWidth, overlayTextHeight);

        if (isShowed && !isF1Pressed) {
            renderOverlayBox(context, overlayPosition_.x(), overlayPosition_.y(), 
                           overlayTextWidth, overlayTextHeight, overlayBackgroundColor);
            context.drawText(client.textRenderer, overlayText, overlayPosition_.x(), 
                           overlayPosition_.y(), overlayTextColor, false);
        }

        if (isAdvancedShowed && !isF1Pressed) {
            Position advancedPosition = calculateAdvancedPosition(overlayPosition, overlayPosition_, 
                                                                advancedTextWidth, overlayTextWidth, overlayTextHeight, 
                                                                screenWidth, screenHeight, isShowed);
            
            renderOverlayBox(context, advancedPosition.x(), advancedPosition.y(), 
                           advancedTextWidth, overlayTextHeight, advancedBackgroundColor);
            context.drawText(client.textRenderer, advancedText, advancedPosition.x(), 
                           advancedPosition.y(), advancedTextColor, false);
        }
    }

    private static Position calculateOverlayPosition(FPSOverlayConfig.OverlayPosition position, 
                                                   int screenWidth, int screenHeight, 
                                                   int textWidth, int textHeight) {
        return switch (position) {
            case TOP_RIGHT -> new Position(screenWidth - 15 - textWidth - PADDING, 10);
            case BOTTOM_LEFT -> new Position(15, screenHeight - 10 - textHeight - PADDING);
            case BOTTOM_RIGHT -> new Position(screenWidth - 15 - textWidth - PADDING, 
                                            screenHeight - 10 - textHeight - PADDING);
            default -> new Position(15, 10);
        };
    }

    private static Position calculateAdvancedPosition(FPSOverlayConfig.OverlayPosition position, 
                                                    Position overlayPos, int advancedTextWidth, int overlayTextWidth,
                                                    int textHeight, int screenWidth, int screenHeight, 
                                                    boolean isShowed) {
        if (isShowed) {
            int x = switch (position) {
                case TOP_RIGHT, BOTTOM_RIGHT -> overlayPos.x() - advancedTextWidth - PADDING * 4;
                default -> overlayPos.x() + overlayTextWidth + PADDING * 4;
            };
            return new Position(x, overlayPos.y());
        } else {
            return switch (position) {
                case TOP_RIGHT -> new Position(screenWidth - 10 - advancedTextWidth - PADDING * 2, 10);
                case BOTTOM_RIGHT -> new Position(screenWidth - 10 - advancedTextWidth - PADDING * 2, 
                                                screenHeight - 10 - textHeight - PADDING);
                case BOTTOM_LEFT -> new Position(15, screenHeight - 10 - textHeight - PADDING);
                default -> new Position(15, 10);
            };
        }
    }

    private static void renderOverlayBox(DrawContext context, int x, int y, int width, int height, int color) {
        drawRoundedRect(context, x - PADDING, y - PADDING, 
                       x + width + PADDING, y + height + PADDING, color);
    }

    private static void drawRoundedRect(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int paddingWidth = 3;
        int paddingHeight = 3;

        context.fill(x1, y1, x2, y2, color);

        context.fill(x1 - paddingWidth, y1 + paddingHeight, x1, y2 - paddingHeight, color);

        context.fill(x2, y1 + paddingHeight, x2 + paddingWidth, y2 - paddingHeight, color);
    }

    private record Position(int x, int y) {}
}
