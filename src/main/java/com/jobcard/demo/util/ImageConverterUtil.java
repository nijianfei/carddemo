package com.jobcard.demo.util;

import java.awt.image.BufferedImage;

public class ImageConverterUtil {
    private static int[] floydSteinbergDither(RGBTriple[] image, RGBTriple[] palette, int width, int height) {
        int[] result = new int[image.length];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                RGBTriple currentPixel = image[(y * height) + x];
                int index = findNearestColor(currentPixel, palette);
                result[(y * height) + x] = index;
                for (int i = 0; i < 3; i++) {
                    int error = (currentPixel.channels[i] & 255) - (palette[index].channels[i] & 255);
                    if (x + 1 < height) {
                        image[(y * height) + x + 1].channels[i] = plus_truncate_uchar(image[(y * height) + x + 1].channels[i], (error * 7) >> 4);
                    }
                    if (y + 1 < width) {
                        if (x - 1 > 0) {
                            image[(((y + 1) * height) + x) - 1].channels[i] = plus_truncate_uchar(image[(((y + 1) * height) + x) - 1].channels[i], (error * 3) >> 4);
                        }
                        image[((y + 1) * height) + x].channels[i] = plus_truncate_uchar(image[((y + 1) * height) + x].channels[i], (error * 5) >> 4);
                        if (x + 1 < height) {
                            image[((y + 1) * height) + x + 1].channels[i] = plus_truncate_uchar(image[((y + 1) * height) + x + 1].channels[i], error >> 4);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static int[] floydSteinbergDither1(RGBTriple[] image, RGBTriple[] palette, int width, int height) {
        int[] result = new int[image.length];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                RGBTriple currentPixel = image[(y * height) + x];
                int index = findNearestColor(currentPixel, palette);
                result[(y * height) + x] = index;
                for (int i = 0; i < 3; i++) {
                    int error = (currentPixel.channels[i] & 255) - (palette[index].channels[i] & 255);
                    if (x + 1 < height) {
                        image[(y * height) + x + 1].channels[i] = plus_truncate_uchar(image[(y * height) + x + 1].channels[i], (error * 4) >> 4);
                    }
                    if (y + 1 < width) {
                        if (x - 1 > 0) {
                            image[(((y + 1) * height) + x) - 1].channels[i] = plus_truncate_uchar(image[(((y + 1) * height) + x) - 1].channels[i], (error * 2) >> 4);
                        }
                        image[((y + 1) * height) + x].channels[i] = plus_truncate_uchar(image[((y + 1) * height) + x].channels[i], (error * 3) >> 4);
                        if (x + 1 < height) {
                            image[((y + 1) * height) + x + 1].channels[i] = plus_truncate_uchar(image[((y + 1) * height) + x + 1].channels[i], error >> 4);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static BufferedImage ditherImage(BufferedImage bufferedImage, RGBTriple[] palette) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        RGBTriple[] image = new RGBTriple[(width * height)];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = bufferedImage.getRGB(i, j);
                image[(i * height) + j] = new RGBTriple((16711680 & pixel) >> 16, (65280 & pixel) >> 8, pixel & 255);
            }
        }
        int[] to = floydSteinbergDither1(image, palette, width, height);
        BufferedImage newBufferedImage = new BufferedImage(width, height, 4);
        for (int i2 = 0; i2 < width; i2++) {
            for (int j2 = 0; j2 < height; j2++) {
                byte[] channels = palette[to[(i2 * height) + j2]].channels;
                newBufferedImage.setRGB(i2, j2, ((channels[0] & 255) << 16) + ((channels[1] & 255) << 8) + (channels[2] & 255));
            }
        }
        return newBufferedImage;
    }

    public static int[] ditherPixes(BufferedImage bufferedImage, RGBTriple[] palette) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        RGBTriple[] image = new RGBTriple[(height * width)];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = bufferedImage.getRGB(j, i);
                image[(i * width) + j] = new RGBTriple((16711680 & pixel) >> 16, (65280 & pixel) >> 8, pixel & 255);
            }
        }
        int[] to = floydSteinbergDither(image, palette, height, width);
        int[] pixes = new int[(width * height)];
        int index = 0;
        for (int i2 = 0; i2 < width; i2++) {
            for (int j2 = 0; j2 < height; j2++) {
                byte[] channels = palette[to[(i2 * height) + j2]].channels;
                pixes[index] = ((channels[0] & 255) << 16) + ((channels[1] & 255) << 8) + (channels[2] & 255);
                index++;
            }
        }
        return pixes;
    }

    private static byte plus_truncate_uchar(byte a, int b) {
        if ((a & 255) + b < 0) {
            return 0;
        }
        if ((a & 255) + b > 255) {
            return -1;
        }
        return (byte) (a + b);
    }

    private static int findNearestColor(RGBTriple color, RGBTriple[] palette) {
        int minDistanceSquared = 196609;
        int bestIndex = 0;
        for (int i = 0; i < palette.length; i = (byte) (i + 1)) {
            int Rdiff = (color.channels[0] & 255) - (palette[i].channels[0] & 255);
            int Gdiff = (color.channels[1] & 255) - (palette[i].channels[1] & 255);
            int Bdiff = (color.channels[2] & 255) - (palette[i].channels[2] & 255);
            int distanceSquared = (Rdiff * Rdiff) + (Gdiff * Gdiff) + (Bdiff * Bdiff);
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public static class RGBTriple {
        final byte[] channels;

        public RGBTriple(int R, int G, int B) {
            this.channels = new byte[]{(byte) R, (byte) G, (byte) B};
        }
    }
}
