package iquldev.fpsoverlay;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.midnightdust.lib.config.MidnightConfig;

public class FPSOverlay implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("FPS Overlay");

    @Override
    public void onInitialize() {
        MidnightConfig.init("fpsoverlay", FPSOverlayConfig.class);
        LOGGER.info("FPS Overlay loaded!");
    }
}