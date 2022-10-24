package com.jobcard.demo.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobcard.demo.bean.CustomBlock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class TemplateAdapter {
    //背景图 BufferedImage 对象
    private BufferedImage background;
    //所有元素
    private List<CustomBlock> blockList;
    private final Gson gson = new Gson();
    //序列化相关
    private final Type type = new TypeToken<List<CustomBlock>>() {}.getType();
    private TemplateAdapter(){}
    public static TemplateAdapter setTemplate(String styleData) throws IOException {
        TemplateAdapter adapter = new TemplateAdapter();
        System.out.println("字符长度：" + styleData.length());
        byte[] bytes = Base64.getDecoder().decode(styleData);
        String decode = new String(bytes);
        System.out.println("总数据长度：" + bytes.length);
        System.out.println(bytes[0]);
        System.out.println(bytes[1]);
        System.out.println(bytes[2]);
        System.out.println(bytes[3]);
        int picLen = ((bytes[0] & 255) << 24) | ((bytes[1] & 255) << 16) | ((bytes[2] & 255) << 8) | (bytes[3] & 255);
        System.out.println("图片长度：" + picLen);
        byte[] picBytes = Arrays.copyOfRange(bytes, 4, picLen + 4);
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(picBytes);
        adapter.background = ImageIO.read(arrayInputStream);
        arrayInputStream.close();
        System.out.println("图片长度：" + picBytes.length);
        String blockStr = new String(Arrays.copyOfRange(bytes, picLen + 4, bytes.length), StandardCharsets.UTF_8);
        System.out.println(blockStr);
        adapter.blockList = adapter.gson.fromJson(blockStr, adapter.type);
        return adapter;
    }

    public BufferedImage getImage(Map<String, String> map) {
        BufferedImage image = copyImage(this.background);
        int width = image.getWidth();
        image.getHeight();
        Graphics2D graphics = image.createGraphics();
        //消除文字锯齿
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (CustomBlock customBlock : this.blockList) {
            try {
                int type2 = customBlock.getType();
                String blockName = customBlock.getName();
                String blockContent = map.get(blockName);
                int x = customBlock.getX();
                int y = customBlock.getY();
                int layout = customBlock.getLayout();
                System.out.println(blockName + "-x:" + x);
                System.out.println(blockName + "-y:" + y);
                if (type2 == 0) {
                    String fontName = customBlock.getFont();
                    int fontStyle = customBlock.getFontStyle();
                    float fontSize = customBlock.getFontSize();
                    System.out.println(blockName + "-字号：" + fontSize);
                    graphics.setFont(new Font(fontName, fontStyle, (int) fontSize).deriveFont(fontSize * 1.3f));
                    graphics.setColor(new Color(customBlock.getColor()));

                    //字体规格
                    FontMetrics fontMetrics = graphics.getFontMetrics();
                    int ascent = fontMetrics.getAscent();
                    int stringWidth = fontMetrics.stringWidth(blockContent);
                    switch (layout) {
                        case 1:
                            x = width - stringWidth;
                            break;
                        case 2:
                            x = (width - stringWidth) / 2;
                            break;
                    }
                    graphics.drawString(blockContent, x, y + ascent);
                } else {
                    int blockWidth = customBlock.getWidth();
                    int blockHeight = customBlock.getHeight();
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    QRCodeUtil.createCodeToOutputStream(blockContent, bout);
//                    BufferedImage read = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(bout.toByteArray())));
                    BufferedImage read = ImageIO.read(new ByteArrayInputStream(bout.toByteArray()));
                    graphics.drawImage(read, x, y, blockWidth, blockHeight, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        graphics.dispose();
        return image;
    }

    private String removeExtension(String fname) {
        int pos = fname.lastIndexOf(46);
        if (pos > -1) {
            return fname.substring(0, pos);
        }
        return fname;
    }

    private BufferedImage copyImage(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "image", "sample/utils/TemplateAdapter", "copyImage"));
        }
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), 1);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image, 0, 0, (ImageObserver) null);
        graphics.dispose();
        return bufferedImage;
    }
}
