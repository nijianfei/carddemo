package com.jobcard.demo.util;

import cn.hutool.core.date.DateUtil;
import com.jobcard.demo.service.impl.WebSocket;
import dcrf.JavaRD800;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CardReader {
    private int lDevice = 0;
    private JavaRD800 rd;
    private char screenIndex = 0;
    private TransStatus transStatus;
    private String cardNum;

    public interface TransStatus {
        void finish(boolean z);

        void getCardId(String str);

        void notifyMessage(String str);

        void progress(double d);

        boolean isSuccess();

        String getMessage();
    }

    public CardReader(JavaRD800 rd, TransStatus transStatus2) {
        this.rd = rd;
        this.transStatus = transStatus2;
        this.lDevice = rd.getlDevice();
    }

    public TransStatus start(BufferedImage image) {
        rd.initDevice();
        long cardNum;
        int[] pSnr = new int[20];
        if (this.rd.dc_card(this.lDevice, (short) 0, pSnr) != 0) {
            System.out.print(this.getClass().getSimpleName() + "_readCardId_dc_card error!\n");
            this.transStatus.notifyMessage("卡片初始化失败");
            this.rd.dc_exit(this.lDevice);
            return transStatus;
        }
        System.out.print("dc_card ok!\n");
        System.out.println("cardnum:" + pSnr[0]);
        if (pSnr[0] < 0) {
            cardNum = -(-4294967296L - ((long) pSnr[0]));
        } else {
            cardNum = pSnr[0];
        }
        String cardId = String.valueOf(cardNum);
        this.cardNum = cardId;
        StringBuilder builder = new StringBuilder(cardId);
        for (int i = 0; i < 10 - cardId.length(); i++) {
            builder.insert(0, "0");
        }
        this.transStatus.getCardId(builder.toString());
        System.out.println("pro_reset:" + ((int) this.rd.dc_pro_reset(this.lDevice, new short[1], new char[20])));
        int width = image.getWidth();
        int height = image.getHeight();
        int size = width * height;
        int[] ints = new int[size];
        image.getRGB(0, 0, width, height, ints, 0, width);
        for (int i2 = 0; i2 < size / 2; i2++) {
            int c = ints[i2];
            ints[i2] = ints[(size - i2) - 1];
            ints[(size - i2) - 1] = c;
        }
        image.setRGB(0, 0, width, height, ints, 0, width);
        char[] imageData = bytetochar(ImageConvertUtil.getImageBytes(image, 0));
        this.transStatus.notifyMessage("数据传输中");
        //写卡
        Date startDate = new Date();
        System.out.println("写卡计时开始：" + DateUtil.formatDateTime(startDate));
        writeTag(imageData);
        Date endDate = new Date();
        System.out.println("写卡计时结束：" + DateUtil.formatDateTime(endDate) + "  历时秒：" + (endDate.getTime() - startDate.getTime()) * 0.001);
//        CardReader.dcExit(this.rd);
        return transStatus;
    }

    private static synchronized void dcExit(JavaRD800 rd) {

        rd.dcExit();
    }

    private char[] hexToChar(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        char[] b = new char[(hex.length() / 2)];
        int j = 0;
        int l = hex.length();
        int i = 0;
        while (i < l) {
            int i2 = i + 1;
            b[j] = (char) (Integer.parseInt("" + arr[i] + arr[i2], 16) & 255);
            j++;
            i = i2 + 1;
        }
        return b;
    }

    private char[] bytetochar(byte[] bytes) {
        char[] a = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            a[i] = (char) (bytes[i] & 255);
        }
        return a;
    }

    private boolean writeDataEx(char[] result, int rowCount) {
        char[] apdu = new char[255];
        apdu[0] = 240;
        apdu[1] = 210;
        apdu[2] = this.screenIndex;
        apdu[4] = 250;
        for (int i = 0; i < rowCount; i++) {
            apdu[3] = (char) i;
            System.arraycopy(result, i * 250, apdu, 5, 250);
            char[] datasw = new char[20];
            this.rd.dc_pro_command(this.lDevice, (short) apdu.length, apdu, new short[2], datasw, (short) 100);
            if (datasw[0] != 144) {
                return false;
            }
            this.transStatus.progress((((double) this.screenIndex) / 2.0d) + ((((double) (i + 1)) * 0.5d) / ((double) rowCount)));
        }
        return true;
    }

    private void writeTag(char[] imageData) {
        int size = imageData.length / 2;
        char[] blackWhiteByte = new char[(size + 62)];
        System.arraycopy(imageData, 0, blackWhiteByte, 0, size);
        char[] redByte = new char[(size + 62)];
        System.arraycopy(imageData, size, redByte, 0, size);
        int rowCount = ((size - 1) / 250) + 1;
        this.screenIndex = 0;
        if (!writeDataEx(blackWhiteByte, rowCount)) {
            System.out.println("黑白图出错");
            this.transStatus.notifyMessage("数据传输失败");
            return;
        }
        this.screenIndex = (char) (this.screenIndex + 1);
        if (!writeDataEx(redByte, rowCount)) {
            System.out.println("红图出错");
            this.transStatus.notifyMessage("数据传输失败");
            return;
        }
        this.screenIndex = (char) (this.screenIndex - 1);
        this.transStatus.notifyMessage("准备刷屏，请勿挪动卡片");
        unlockCard();
        try {
            if (refreshScreenAfterWriteData()) {
                getRefreshResult();
            }
        } finally {
            lockCard();
        }
    }

    public boolean lockCard() {
        return configScreen(Arrays.asList("F0DB020000", "F0DB00003DA70100A006073000F001A0A40108A5020050A4010CA5020064A103000F8DA102E519A102E002A3021013A10104A40103A20110A40103A20102A20207A5", "F0DA000003F00730", "F0DA00010423003048"));
    }

    public boolean unlockCard() {
        return configScreen(Arrays.asList("F0DB020000", "F0DB00003DA70100A006073000F001A0A40108A5020050A4010CA5020064A10300CF0DA102E519A102E002A3021013A10104A40103A20112A40103A20102A20207A5", "F0DA000003F00730", "F0DA00010423003048"));
    }

    private boolean configScreen(List<String> cmdList) {
        char[] result = new char[4];
        short[] len = new short[3];
        for (String cmd : cmdList) {
            char[] tr = hexToChar(cmd);
            this.rd.dc_pro_command(this.lDevice, (short) tr.length, tr, len, result, (short) 100);
            if (!(result[0] == 144 && result[1] == 0)) {
                return false;
            }
//            System.out.println("ok");
        }
        return true;
    }

    private boolean refreshScreenAfterWriteData() {
        char[] refreshcmd = {240, 212, 5, 128, 0};
        char[] datasw = new char[3];
        System.out.println("发送刷图命令");
        this.rd.dc_pro_command(this.lDevice, (short) refreshcmd.length, refreshcmd, new short[1], datasw, (short) 100);
//        System.out.println((int) datasw[0]);
//        System.out.println((int) datasw[1]);
        if (datasw[0] == 144) {
            getRefreshResult();
            return true;
        }
        if (datasw[0] == 'i' && datasw[1] == 'i') {
            System.out.println("刷屏时出错:698A");
            this.transStatus.notifyMessage("刷屏出错：698A");
        } else if (datasw[0] != 'i' || datasw[1] != 134) {
            return true;
        } else {
            System.out.println("刷屏时出错:6986");
            this.transStatus.notifyMessage("刷屏出错：6986");
        }
        return false;
    }

    private void getRefreshResult() {
        char[] refreshcmd = {240, 212, 5, (char) (this.screenIndex | 65408), 0};
        try {
            char[] datasw = new char[10];
            refreshcmd[1] = 222;
            refreshcmd[2] = 0;
            refreshcmd[3] = 0;
            refreshcmd[4] = 1;
            short[] rlen = new short[1];
            for (int i = 0; i < 16; i++) {
                this.rd.dc_pro_command(this.lDevice, (short) refreshcmd.length, refreshcmd, rlen, datasw, (short) 100);
                if (datasw[0] == 0 && datasw[1] == 144) {
                    Thread.sleep(1000);
                    this.transStatus.notifyMessage("刷屏成功");
                    System.out.println("刷屏成功");
                    this.transStatus.finish(true);
                    return;
                }
                if (datasw[0] == 1 && datasw[1] == 144) {
                    Thread.sleep(1000);
                    this.transStatus.notifyMessage("刷屏中，请勿挪动卡片");
                } else if (datasw[0] == 'i' && datasw[1] == 138) {
                    this.transStatus.notifyMessage("能量不足");
                } else if (datasw[0] == 'i' && datasw[1] == 134) {
                    this.transStatus.notifyMessage("屏幕异常");
                }
            }
            this.transStatus.notifyMessage("刷新失败");
            this.transStatus.finish(false);
        } catch (InterruptedException e) {
            this.transStatus.notifyMessage("获取刷屏结果发生错误，指令无响应");
            this.transStatus.finish(false);
        }
    }

    private void tryRefresh() {
        char[] datasw = null;
        char[] refreshcmd = {240, 212, 5, 0, 0};
        this.rd.dc_pro_command(this.lDevice, (short) refreshcmd.length, refreshcmd, new short[1], null, (short) 100);
        if (!datasw.toString().equals("9000")) {
            System.out.println("刷屏出错");
        }
    }

    private void normalConfigScreen() {
        char[] result = new char[4];
        short[] len = new short[3];
        for (String cmd : Arrays.asList("F0DB020000", "F0DB00003DA70100A006073000F001A0A40108A5020050A4010CA5020064A103000F8DA102E519A102E002A3021013A10104A40103A20112A40103A20102A20207A5", "F0DA000003F00730", "F0DA00010423003048")) {
            char[] tr = hexToChar(cmd);
            this.rd.dc_pro_command(this.lDevice, (short) tr.length, tr, len, result, (short) 100);
            if (result[0] == 144 && result[1] == 0) {
                System.out.println("ok");
            } else {
                System.out.println("err");
            }
        }
    }

    private void partialConfig() {
        char[] RTvalue = new char[4];
        short[] len = new short[3];
        int index1 = 0;
        for (String cmd : Arrays.asList("F0DB020000", "F0DB01000BA70100A006073000F001A0", "F0DB01000EA40108A5020050A4010CA5020064", "F0DB010039A10300EF0DA1060103103F3F03A10406979715A10461F001A0A10465000000A1026022A102820EA1023009A1025087A102E388A1042A800000", "F0DB01003BA139200114101410010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", "F0DB01003BA139220114101410010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", "F0DB01003BA139230154509490010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", "F0DB01003BA139240194905450010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", "F0DB000017A3021013A10104A40103A20112A40103A20102A20207A5", "F0DA000003F00730", "F0DA00010423003048")) {
            char[] tr = hexToChar(cmd);
            this.rd.dc_pro_command(this.lDevice, (short) tr.length, tr, len, RTvalue, (short) 100);
            index1++;
            if (RTvalue[0] == 144 && RTvalue[1] == 0) {
                System.out.println("ok: " + index1);
            } else {
                System.out.println("err " + index1);
            }
        }
    }

    public String getCardNum() {
        return cardNum;
    }
}
