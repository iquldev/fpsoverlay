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

    @Comment(category = "b", centered = true) public static Comment fpsOverlayPlaceholder;
    @Comment(category = "b", centered = true) public static Comment coordsOverlayPlaceholder;
    @Comment(category = "b", centered = true) public static Comment systemTimeOverlayPlaceholder;
    @Comment(category = "b", centered = true) public static Comment dayTimeOverlayPlaceholder;
    @Comment(category = "b", centered = true) public static Comment minMaxFpsOverlayPlaceholder;
    @Comment(category = "b", centered = true) public static Comment facingOverlayPlaceholder;
    @Comment(category = "b", centered = true) public static Comment speedOverlayPlaceholder;
    @Entry(category = "b") public static String overlayText = "{fps} FPS";
    @Comment(category = "b", centered = true) public static Comment overlayColor;
    @Entry(category = "b", width = 7, min = 7, isColor = true) public static String overlayBackgroundColor = "#000000";
    @Entry(category = "b", width = 7, min = 7, isColor = true) public static String overlayTextColor = "#ffffff";
    @Entry(category = "b", isSlider = true, min = 0, max = 100, precision = 1) public static int overlayTransparency = 50;
    @Comment(category = "b", centered = true) public static Comment overlayDynamicTitle;
    @Comment(category = "b", centered = true) public static Comment overlayDynamicDesc;
    @Entry(category = "b") public static String overlayDynamicText = "";
    @Entry(category = "b") public static int overlayDynamicInterval = 3;

    @Comment(category = "c", centered = true) public static Comment fpsAdvancedPlaceholder;
    @Comment(category = "c", centered = true) public static Comment coordsAdvancedPlaceholder;
    @Comment(category = "c", centered = true) public static Comment systemTimeAdvancedPlaceholder;
    @Comment(category = "c", centered = true) public static Comment dayTimeAdvancedPlaceholder;
    @Comment(category = "c", centered = true) public static Comment minMaxFpsAdvancedPlaceholder;
    @Comment(category = "c", centered = true) public static Comment facingAdvancedPlaceholder;
    @Comment(category = "c", centered = true) public static Comment speedAdvancedPlaceholder;
    @Entry(category = "c") public static String advancedText = "{minFps} / {maxFps}";
    @Comment(category = "c", centered = true) public static Comment advancedColor;
    @Entry(category = "c", width = 7, min = 7, isColor = true) public static String advancedBackgroundColor = "#000000";
    @Entry(category = "c", width = 7, min = 7, isColor = true) public static String advancedTextColor = "#ffffff";
    @Entry(category = "c", isSlider = true, min = 0, max = 100, precision = 1) public static int advancedTransparency = 50;
    @Comment(category = "c", centered = true) public static Comment advancedDynamicTitle;
    @Comment(category = "c", centered = true) public static Comment advancedDynamicDesc;
    @Entry(category = "c") public static String advancedDynamicText = "";
    @Entry(category = "c") public static int advancedDynamicInterval = 3;
}