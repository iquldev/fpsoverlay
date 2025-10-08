package iquldev.fpsoverlay.text;

import iquldev.fpsoverlay.FPSOverlayConfig;
import iquldev.fpsoverlay.stats.FpsStats;
import net.minecraft.client.MinecraftClient;

public class DynamicTextManager {
    private boolean useOverlayDynamicText = false;
    private boolean useAdvancedDynamicText = false;
    private long lastOverlaySwitchTime = System.currentTimeMillis();
    private long lastAdvancedSwitchTime = System.currentTimeMillis();

    public void updateDynamicText() {
        long currentTime = System.currentTimeMillis();

        if (FPSOverlayConfig.overlayDynamicInterval > 0 && 
            !FPSOverlayConfig.overlayDynamicText.isEmpty() && 
            currentTime - lastOverlaySwitchTime >= FPSOverlayConfig.overlayDynamicInterval * 1000L) {
            useOverlayDynamicText = !useOverlayDynamicText;
            lastOverlaySwitchTime = currentTime;
        }

        if (FPSOverlayConfig.advancedDynamicInterval > 0 && 
            !FPSOverlayConfig.advancedDynamicText.isEmpty() &&  
            currentTime - lastAdvancedSwitchTime >= FPSOverlayConfig.advancedDynamicInterval * 1000L) {
            useAdvancedDynamicText = !useAdvancedDynamicText;
            lastAdvancedSwitchTime = currentTime;
        }
    }

    public String getOverlayText(MinecraftClient client, FpsStats fpsStats) {
        String textToFormat = (FPSOverlayConfig.overlayDynamicInterval > 0 && useOverlayDynamicText) ?
                              FPSOverlayConfig.overlayDynamicText :
                              FPSOverlayConfig.overlayText;
        
        return TextFormatter.formatText(textToFormat, client, fpsStats);
    }

    public String getAdvancedText(MinecraftClient client, FpsStats fpsStats) {
        String textToFormat = (FPSOverlayConfig.advancedDynamicInterval > 0 && useAdvancedDynamicText) ?
                              FPSOverlayConfig.advancedDynamicText :
                              FPSOverlayConfig.advancedText;
        
        return TextFormatter.formatText(textToFormat, client, fpsStats);
    }

    public boolean isUsingOverlayDynamicText() {
        return useOverlayDynamicText;
    }

    public boolean isUsingAdvancedDynamicText() {
        return useAdvancedDynamicText;
    }
}
