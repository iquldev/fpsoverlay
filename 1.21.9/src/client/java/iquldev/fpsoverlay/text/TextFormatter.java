package iquldev.fpsoverlay.text;

import iquldev.fpsoverlay.stats.FpsStats;
import net.minecraft.client.MinecraftClient;

public class TextFormatter {
    // Cached system stats to avoid frequent updates
    private static volatile long lastSystemStatsTime = 0L;
    private static volatile String cachedCurrentRam = "0";
    private static volatile String cachedRamPercent = "0.0";
    private static final long SYSTEM_STATS_TTL_MS = 2000L; // 2 seconds

    // Session tracking
    private static volatile long sessionStartTime = 0L; // millis when player entered world

    public static String formatText(String text, MinecraftClient client, FpsStats fpsStats) {
        if (client.player == null) {
            return text;
        }

        String systemTime = String.format("%tH:%tM", System.currentTimeMillis(), System.currentTimeMillis());
        int hour = Integer.parseInt(String.format("%tH", System.currentTimeMillis()));

        String clockSymbol = getClockSymbol(hour);

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
        String dayTime = (client.world.getTimeOfDay() % 24000L < 13000L) ? "Day 🌞" : "Night 🌙";

        text = text.replace("{fps}", maybeColorNumber("{fps}", fpsStats.getCurrentFps()));
        text = text.replace("{x}", x);
        text = text.replace("{y}", y);
        text = text.replace("{z}", z);
        text = text.replace("{systemTime}", timeWithClock);
        text = text.replace("{minFps}", maybeColorNumber("{minFps}", fpsStats.getMinFps()));
        text = text.replace("{maxFps}", maybeColorNumber("{maxFps}", fpsStats.getMaxFps()));
        text = text.replace("{currentRam}", getCurrentRam());
        text = text.replace("{maxRam}", getMaxRam());
        text = text.replace("{ramPercent}", getRamPercent());
        text = text.replace("{facing}", facing);
        text = text.replace("{playerSpeed}", playerSpeedStr);
        text = text.replace("{dayTime}", dayTime);
        text = text.replace("{worldTime}", getWorldTime(client));
        text = text.replace("{weather}", getWeather(client));
        text = text.replace("{sessionTime}", getSessionTime());

        return text;
    }

    private static String maybeColorNumber(String placeholder, int value) {
        if (!iquldev.fpsoverlay.FPSOverlayConfig.colorOnThreshold) return String.valueOf(value);
        int threshold = iquldev.fpsoverlay.FPSOverlayConfig.colorThreshold;
        if (value < threshold) {
            return "§c" + value + "§r";
        }
        return String.valueOf(value);
    }

    private static String getClockSymbol(int hour) {
        if (hour >= 6 && hour < 12) {
            return "🕖";
        } else if (hour >= 12 && hour < 18) {
            return "🕓";
        } else if (hour >= 18 && hour < 22) {
            return "🕒";
        } else {
            return "🕰️";
        }
    }

    private static String getCurrentRam() {
    updateSystemStatsIfNeeded();
    return cachedCurrentRam;
    }

    private static String getMaxRam() {
        Runtime rt = Runtime.getRuntime();
        long max = rt.maxMemory();
    return String.valueOf(bytesToMb(max));
    }

    private static String getRamPercent() {
        updateSystemStatsIfNeeded();
        return cachedRamPercent;
    }

    private static void updateSystemStatsIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastSystemStatsTime <= SYSTEM_STATS_TTL_MS) return;
        synchronized (TextFormatter.class) {
            if (now - lastSystemStatsTime <= SYSTEM_STATS_TTL_MS) return;
            try {
                Runtime rt = Runtime.getRuntime();
                long used = rt.totalMemory() - rt.freeMemory();
                long max = rt.maxMemory();
                long usedMb = bytesToMb(used);
                cachedCurrentRam = String.valueOf(usedMb);
                if (max <= 0) {
                    cachedRamPercent = "0.0";
                } else {
                    double pct = (double) used / (double) max * 100.0;
                    cachedRamPercent = String.format("%.1f", pct);
                }
            } catch (Throwable t) {
                // ignore
            } finally {
                lastSystemStatsTime = now;
            }
        }
    }

    private static String getWorldTime(MinecraftClient client) {
        try {
            if (client.world == null) return "00:00";
            long time = client.world.getTimeOfDay() % 24000L;
            long hourOffset = (time / 1000) % 24;
            long hours = (6 + hourOffset) % 24;
            long ticksIntoHour = time % 1000;
            long minutes = (ticksIntoHour * 60) / 1000;
            String clock = getClockSymbol((int) hours);
            return String.format("%02d:%02d %s", hours, minutes, clock);
        } catch (Throwable t) {
            return "00:00";
        }
    }

    private static String getWeather(MinecraftClient client) {
        try {
            if (client.world == null) return "Clear ☀️";
            boolean raining = client.world.isRaining();
            boolean thunder = client.world.isThundering();
            if (thunder) return "Thunder ⛈️";
            if (raining) return "Rain 🌧️";
            return "Clear ☀️";
        } catch (Throwable t) {
            return "Clear ☀️";
        }
    }

    private static String getSessionTime() {
    long now = System.currentTimeMillis();
    if (sessionStartTime == 0L) sessionStartTime = now;
    long diff = now - sessionStartTime;
    long totalSeconds = diff / 1000;
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024 / 1024;
    }

    private static String getFacingDirection(MinecraftClient client) {
        assert client.player != null;
        float yaw = client.player.getYaw();

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 315 || yaw < 45) {
            return "South 🧭";
        } else if (yaw >= 45 && yaw < 135) {
            return "West 🧭";
        } else if (yaw >= 135 && yaw < 225) {
            return "North 🧭";
        } else if (yaw >= 225 && yaw < 315) {
            return "East 🧭";
        }
        
        return "Unknown";
    }
}
