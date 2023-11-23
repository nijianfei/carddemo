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
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Map<String,CustomBlock> blockMap = blockList.stream().collect(Collectors.toMap(CustomBlock::getName, Function.identity()));
//        Optional.ofNullable(blockMap.get("buildingName")).ifPresent(bb->{
//            if (StringUtils.isNotBlank(map.get("buildingName")) && map.get("buildingName").length()>4) {
//                Optional.ofNullable(blockMap.get("floorNames")).ifPresent(fb->{
//                    fb.setX(fb.getX() + 13);
//                });
//            }
//        });

        for (CustomBlock customBlock : this.blockList) {
            try {
                int type2 = customBlock.getType();//快类型  0：文字
                String blockName = customBlock.getName();
                String blockContent = map.get(blockName);
                int x = customBlock.getX();//块x坐标
                int y = customBlock.getY();//块y坐标
                int layout = customBlock.getLayout();
                if (type2 == 0) {
                    String fontName = customBlock.getFont();//字体名称
                    int fontStyle = customBlock.getFontStyle();//文字风格:加粗 斜体
                    float fontSize = customBlock.getFontSize();//字体大小
//                    Font.BOLD
                    //指定 字体 是否加粗 大小
                    graphics.setFont(new Font(fontName, fontStyle, (int) fontSize).deriveFont(fontSize * 1.3f));
                    //指定字体颜色
                    graphics.setColor(new Color(customBlock.getColor()));
//                    graphics.setColor(new Color(0));

                    //字体规格
                    FontMetrics fontMetrics = graphics.getFontMetrics();
                    int ascent = fontMetrics.getAscent();
                    int stringWidth = fontMetrics.stringWidth(blockContent);
                    customBlock.setContentWidth(stringWidth);
                    customBlock.setContent(blockContent);
                    if (StringUtils.isNotBlank(customBlock.getBeforeBlockName()) && Objects.nonNull(blockMap.get(customBlock.getBeforeBlockName()))) {
                        CustomBlock block = blockMap.get(customBlock.getBeforeBlockName());
                        x = block.getX() + block.getContentWidth() + (block.getContentWidth()/block.getContent().length()/2);
                    }

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
