package iquldev.fpsoverlay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class FPSOverlayClient implements ClientModInitializer {
	private int minFps = Integer.MAX_VALUE;
	private int maxFps = Integer.MIN_VALUE;
	private int fps = 0;
	private long lastUpdateTime = System.currentTimeMillis();
    private static final int UPDATE_INTERVAL = 30000;
	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			fps = client.getCurrentFps();

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

			int x = 10;
            int y = 10;

			String text = fps + " FPS";
			String minMaxText = minFps + " ▼ " + maxFps + " ▲";

			int textWidthFps = client.textRenderer.getWidth(text);
			int textHeightFps = client.textRenderer.fontHeight;

			int backgroundColor = 0x80000000;

			int padding = 5;

			context.fill(x - padding, y - padding, x + textWidthFps + padding, y + textHeightFps + padding, backgroundColor);
			context.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, false);

			x += textWidthFps + 15;

			int textWidthMinMax = client.textRenderer.getWidth(minMaxText);
			int textHeightMinMax = client.textRenderer.fontHeight;

			context.fill(x - padding, y - padding, x + textWidthMinMax + padding, y + textHeightFps + padding, backgroundColor);
			context.drawText(client.textRenderer, minMaxText, x, y, 0xFFFFFFFF, false);
		});
	}
}