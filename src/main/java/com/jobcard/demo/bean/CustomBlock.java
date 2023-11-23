package com.jobcard.demo.bean;

public class CustomBlock {
    //颜色
    private int color;
    //内容
    private String content;
    //字体
    private String font;
    //文字大小
    private float fontSize;
    //文字风格
    private int fontStyle;
    //元素高度
    private int height;
    //元素布局
    private int layout;
    //元素名称
    private String name;
    //元素类型 2:文字
    private int type;
    //元素宽度
    private int width;
    //元素坐标X
    private int x;
    //元素坐标Y
    private int y;

    private int contentWidth;

    private String beforeBlockName;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content2) {
        this.content = content2;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type2) {
        this.type = type2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x2) {
        this.x = x2;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y2) {
        this.y = y2;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width2) {
        this.width = width2;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height2) {
        this.height = height2;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color2) {
        this.color = color2;
    }

    public String getFont() {
        return this.font;
    }

    public void setFont(String font2) {
        this.font = font2;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float fontSize2) {
        this.fontSize = fontSize2;
    }

    public int getFontStyle() {
        return this.fontStyle;
    }

    public void setFontStyle(int fontStyle2) {
        this.fontStyle = fontStyle2;
    }

    public int getLayout() {
        return this.layout;
    }

    public void setLayout(int layout2) {
        this.layout = layout2;
    }

    public int getContentWidth() {
        return contentWidth;
    }

    public void setContentWidth(int contentWidth) {
        this.contentWidth = contentWidth;
    }

    public String getBeforeBlockName() {
        return beforeBlockName;
    }

    public void setBeforeBlockName(String beforeBlockName) {
        this.beforeBlockName = beforeBlockName;
    }
}
