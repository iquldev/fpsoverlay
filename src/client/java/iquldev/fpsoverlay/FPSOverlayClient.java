package iquldev.fpsoverlay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.BoolArgumentType;

public class FPSOverlayClient implements ClientModInitializer {
	private int minFps = Integer.MAX_VALUE;
	private int maxFps = Integer.MIN_VALUE;
	private int fps = 0;
	private boolean isShowed = true;
	private boolean isAdvancedShowed = false;
	private long lastUpdateTime = System.currentTimeMillis();
    private static final int UPDATE_INTERVAL = 30000;
	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
			MinecraftClient client = MinecraftClient.getInstance();

			int x = 10;
			int y = 10;

			int padding = 5;

			fps = client.getCurrentFps();
			String text = fps + " FPS";

			int backgroundColor = 0x80000000;

			int textWidthFps = client.textRenderer.getWidth(text);
			int textHeightFps = client.textRenderer.fontHeight;

			if (isShowed) {
				context.fill(x - padding, y - padding, x + textWidthFps + padding, y + textHeightFps + padding, backgroundColor);
				context.drawText(client.textRenderer, text, x, y, 0xFFFFFFFF, false);

				if (isAdvancedShowed) {
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

                    String minMaxText = minFps + " ▼ " + maxFps + " ▲";

                    int advancedX = x + textWidthFps + 15;
                    int textWidthMinMax = client.textRenderer.getWidth(minMaxText);
                    int textHeightMinMax = client.textRenderer.fontHeight;

                    context.fill(advancedX - padding, y - padding, advancedX + textWidthMinMax + padding, y + textHeightFps + padding, backgroundColor);
                    context.drawText(client.textRenderer, minMaxText, advancedX, y, 0xFFFFFFFF, false);
                }
			} else if (isAdvancedShowed) {
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

                String minMaxText = minFps + " ▼ " + maxFps + " ▲";

                int textWidthMinMax = client.textRenderer.getWidth(minMaxText);
                int textHeightMinMax = client.textRenderer.fontHeight;

                context.fill(x - padding, y - padding, x + textWidthMinMax + padding, y + textHeightFps + padding, backgroundColor);
                context.drawText(client.textRenderer, minMaxText, x, y, 0xFFFFFFFF, false);
            }
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("fpsoverlay")
				.then(ClientCommandManager.literal("showFPS")
					.then(ClientCommandManager.argument("value", BoolArgumentType.bool())
						.executes(context -> {
							isShowed = BoolArgumentType.getBool(context, "value");
							return 1;
						})))
				.then(ClientCommandManager.literal("showAdvanced")
					.then(ClientCommandManager.argument("value", BoolArgumentType.bool())
						.executes(context -> {
							isAdvancedShowed = BoolArgumentType.getBool(context, "value");
							return 1;
						})))
				.executes(context -> {
					context.getSource().sendFeedback(Text.literal("Mod commands\n" +
						"\n" +
						"/fpsoverlay showFPS [true/false] - Show FPS Overlay\n" +
						"/fpsoverlay showAdvanced [true/false] - Show Advanced FPS Overlay"));
					return 1;
				}));
		});
	}
}