package iquldev.fpsoverlay;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FPSOverlay implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("FPS Overlay");

	@Override
	public void onInitialize() {
		LOGGER.info("FPS Overlay loaded!");
	}
}