package iquldev.fpsoverlay;

import eu.midnightdust.lib.config.MidnightConfig;

public class FPSOverlayConfig extends MidnightConfig {
    @Comment(category = "a", centered = true) public static Comment display;
    @Entry(category = "a") public static boolean isShowed = true;
    @Entry(category = "a") public static boolean isAdvancedShowed = false;
    @Comment(category = "a", centered = true) public static Comment position;
    @Entry(category = "a") public static OverlayPosition overlayPosition = OverlayPosition.TOP_LEFT;
    public enum OverlayPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
    @Entry(category = "b") public static String overlayText = "{fps} FPS";
    @Comment(category = "b", centered = true) public static Comment overlayColor;
    @Entry(category = "b", width = 7, min = 7, isColor = true) public static String overlayBackgroundColor = "#000000";
    @Entry(category = "b", isSlider = true, min = 0, max = 100, precision = 1) public static int overlayTransparency = 50;
    @Entry(category = "c") public static String advancedText = "{minFps} ▼ {maxFps} ▲";
    @Comment(category = "c", centered = true) public static Comment advancedColor;
    @Entry(category = "c", width = 7, min = 7, isColor = true) public static String advancedBackgroundColor = "#000000";
    @Entry(category = "c", isSlider = true, min = 0, max = 100, precision = 1) public static int advancedTransparency = 50;
}