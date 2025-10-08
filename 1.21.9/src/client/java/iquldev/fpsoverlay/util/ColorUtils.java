package iquldev.fpsoverlay.util;

public class ColorUtils {
    public static int parseColor(String colorStr, int transparency) {
        try {
            if (colorStr == null || colorStr.isEmpty()) {
                return 0x80000000;
            }

            if (colorStr.charAt(0) == '#') {
                colorStr = colorStr.substring(1);
            }

            int color = Integer.parseInt(colorStr, 16);

            int alpha = (int) (transparency * 2.55);

            return (alpha << 24) | color;
        } catch (NumberFormatException e) {
            return 0x80000000;
        }
    }
    
    public static int createColor(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    
    public static int createColor(int red, int green, int blue) {
        return (red << 16) | (green << 8) | blue;
    }
}
