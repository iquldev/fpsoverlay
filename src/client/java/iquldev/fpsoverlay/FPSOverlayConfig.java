package iquldev.fpsoverlay;

import eu.midnightdust.lib.config.MidnightConfig;

public class FPSOverlayConfig extends MidnightConfig {
    @Comment(category = "text", centered = true) public static Comment display;
    @Entry(category = "text") public static boolean isShowed = true;
    @Entry(category = "text") public static boolean isAdvancedShowed = false;
}