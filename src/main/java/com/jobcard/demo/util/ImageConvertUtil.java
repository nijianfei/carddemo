package com.jobcard.demo.util;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageConvertUtil {
    public static final int MODE_74 = 2;
    public static final int MODE_MASTER_SLAVE = 1;
    public static final int MODE_NORMAL = 0;

    static byte[] getImageBytes(BufferedImage image, int mode) {
        if (image == null) {
            return null;
        }
        switch (mode) {
            case 0:
                return getNormalBytes(image, false);
            case 1:
                return getMasterSlaveBytes(image, false);
            case 2:
                return get74Bytes(image, false);
            default:
                return null;
        }
    }

    static byte[] getPartialImageBytes(BufferedImage image, int mode) {
        if (image == null) {
            return null;
        }
        switch (mode) {
            case 0:
                return getNormalBytes(image, true);
            case 1:
                return getMasterSlaveBytes(image, true);
            case 2:
                return get74Bytes(image, true);
            default:
                return null;
        }
    }

    private static byte[] getNormalBytes(BufferedImage bmp, boolean partial) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w * h;
        byte[] imageData = new byte[(size >> 2)];
        int[] pixels = new int[(w * h)];
        bmp.getRGB(0, 0, w, h, pixels, 0, w);
        pixel2byte(w, h, imageData, size >> 3, pixels, partial);
        return imageData;
    }

    private static void pixel2byte(int w, int h, byte[] imageData, int offset, int[] pixels, boolean partial) {
        int dataIndex = 0;
        int t = 7;
        byte bwByte = 0;
        byte rByte = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = pixels[(i * w) + j];
                int r = (16711680 & pixel) >> 16;
                int g = (65280 & pixel) >> 8;
                int b = pixel & 255;
                int t1 = (g * 4 * g) + (b * 2 * b);
                int t2 = (r - 255) * 3 * (r - 255);
                int db = t1 + (r * 3 * r);
                int dr = t1 + t2;
                int m = Math.min(Math.min(((g - 255) * 4 * (g - 255)) + t2 + ((b - 255) * 2 * (b - 255)), db), dr);
                if (partial) {
                    if (m == dr) {
                        bwByte = (byte) ((1 << t) | bwByte);
                    } else if (m == db) {
                        rByte = (byte) ((1 << t) | rByte);
                    }
                } else if (m == db) {
                    bwByte = (byte) ((1 << t) | bwByte);
                } else if (m == dr) {
                    rByte = (byte) ((1 << t) | rByte);
                }
                t--;
                if (t < 0) {
                    t = 7;
                    imageData[dataIndex] = bwByte;
                    imageData[dataIndex + offset] = rByte;
                    rByte = 0;
                    bwByte = 0;
                    dataIndex++;
                }
            }
        }
    }

    private static byte[] getMasterSlaveBytes(BufferedImage bmp, boolean partial) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w * h;
        byte[] imageData = new byte[(size >> 2)];
        int offset = size >> 3;
        int[] pixels = new int[(w * h)];
        bmp.getRGB(0, 0, w / 2, h, pixels, 0, w / 2);
        bmp.getRGB(w / 2, 0, w / 2, h, pixels, size / 2, w / 2);
        pixel2byte(w, h, imageData, offset, pixels, partial);
        int offset2 = offset >> 1;
        byte[] bwSlave = Arrays.copyOfRange(imageData, offset2, offset2 << 1);
        System.arraycopy(Arrays.copyOfRange(imageData, offset2 << 1, offset2 * 3), 0, imageData, offset2, offset2);
        System.arraycopy(bwSlave, 0, imageData, offset2 << 1, offset2);
        return imageData;
    }

    private static byte[] get74Bytes(BufferedImage bmp, boolean partial) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = w * h;
        byte[] imageData = new byte[(size >> 2)];
        int offset = size >> 3;
        int[] pixels = new int[(w * h)];
        bmp.getRGB(0, 0, w, h, pixels, 0, w);
        int dataIndex = 0;
        int t = 7;
        byte bwByte = 0;
        byte rByte = 0;
        for (int i = w - 1; i >= 0; i--) {
            for (int j = 0; j < h; j++) {
                int pixel = pixels[(j * w) + i];
                int r = (16711680 & pixel) >> 16;
                int g = (65280 & pixel) >> 8;
                int b = pixel & 255;
                int t1 = (g * 4 * g) + (b * 2 * b);
                int t2 = (r - 255) * 3 * (r - 255);
                int db = t1 + (r * 3 * r);
                int dr = t1 + t2;
                int m = Math.min(Math.min(((g - 255) * 4 * (g - 255)) + t2 + ((b - 255) * 2 * (b - 255)), db), dr);
                if (partial) {
                    if (m == dr) {
                        bwByte = (byte) ((1 << t) | bwByte);
                    } else if (m == db) {
                        rByte = (byte) ((1 << t) | rByte);
                    }
                } else if (m == db) {
                    bwByte = (byte) ((1 << t) | bwByte);
                } else if (m == dr) {
                    rByte = (byte) ((1 << t) | rByte);
                }
                t--;
                if (t < 0) {
                    t = 7;
                    imageData[dataIndex] = bwByte;
                    imageData[dataIndex + offset] = rByte;
                    rByte = 0;
                    bwByte = 0;
                    dataIndex++;
                }
            }
        }
        return imageData;
    }
}
