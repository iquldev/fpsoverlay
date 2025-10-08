package iquldev.fpsoverlay.stats;

public class FpsStats {
    private int minFps = Integer.MAX_VALUE;
    private int maxFps = Integer.MIN_VALUE;
    private int currentFps = 0;
    private long lastUpdateTime = System.currentTimeMillis();
    private static final int UPDATE_INTERVAL = 30000;

    public void updateFps(int fps) {
        this.currentFps = fps;
        
        if (fps < minFps) {
            minFps = fps;
        }
        if (fps > maxFps) {
            maxFps = fps;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
            resetStats();
            lastUpdateTime = currentTime;
        }
    }

    public void resetStats() {
        minFps = currentFps;
        maxFps = currentFps;
    }

    public int getCurrentFps() {
        return currentFps;
    }

    public int getMinFps() {
        return minFps;
    }

    public int getMaxFps() {
        return maxFps;
    }
}
