package com.jobcard.demo.util;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobcard.demo.bean.CustomBlock;
import com.jobcard.demo.bean.TempleteBean;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
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
    private final Type type = new TypeToken<List<CustomBlock>>() {
    }.getType();

    private TemplateAdapter() {
    }

    public static TemplateAdapter setTemplate(String styleData) throws IOException {
        TemplateAdapter adapter = new TemplateAdapter();
        TempleteBean templeteBean = JSONUtil.toBean(styleData, TempleteBean.class);
        byte[] picBytes = Base64.getDecoder().decode(templeteBean.getPicEncode());
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(picBytes);
        adapter.background = ImageIO.read(arrayInputStream);
//        adapter.background = ImageIO.read(new File("C:\\Users\\njf\\Desktop\\背景new.jpg"));
        arrayInputStream.close();
        String blockStr = templeteBean.getElementProperties();
        adapter.blockList = adapter.gson.fromJson(blockStr, adapter.type);
        return adapter;
    }

    public BufferedImage getImage(Map<String, String> map) {
        //分辨率：240x416
        BufferedImage image = this.background;
//        BufferedImage image = copyImage(this.background);
        int width = image.getWidth();
        image.getHeight();
        Graphics2D graphics = image.createGraphics();
        //消除文字锯齿
//        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
//        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (CustomBlock customBlock : this.blockList) {
            try {
                int type2 = customBlock.getType();
                String blockName = customBlock.getName();
                String blockContent = map.get(blockName);
                int x = customBlock.getX();
                int y = customBlock.getY();
                int layout = customBlock.getLayout();
                if (type2 == 0) {
                    String fontName = customBlock.getFont();
                    int fontStyle = customBlock.getFontStyle();
                    float fontSize = customBlock.getFontSize();
//                    Font.BOLD
                    graphics.setFont(new Font(fontName, fontStyle, (int) fontSize).deriveFont(fontSize * 1.3f));
                    graphics.setColor(new Color(customBlock.getColor()));
//                    graphics.setColor(new Color(0));

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
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(image, 0, 0, (ImageObserver) null);
        graphics.dispose();
        return bufferedImage;
    }

    public void addBlock(CustomBlock customBlock) {
        blockList.add(customBlock);
    }

    public List<CustomBlock> getBlockList() {
        return blockList;
    }
}
