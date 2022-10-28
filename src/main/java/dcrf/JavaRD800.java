package dcrf;

import java.util.Objects;

public class JavaRD800 {
    private int lDevice = 0;
    private int deviceNo = 0;

    public native short dc_anticoll(int i, short s, int[] iArr);

    public native short dc_authentication(int i, short s, short s2);

    public native short dc_beep(int i, short s);

    /**
     * 卡片初始化
     */
    public native short dc_card(int i, short s, int[] iArr);

    public native short dc_config_card(int i, char c);

    public native short dc_cpuapdu(int i, short s, char[] cArr, short[] sArr, char[] cArr2);

    public native short dc_cpuapdusource(int i, short s, char[] cArr, short[] sArr, char[] cArr2);

    public native short dc_cpudown(int i);

    public native short dc_cpureset(int i, short[] sArr, char[] cArr);

    public native short dc_ctl_mode(int i, short s);

    public native short dc_decrement(int i, short s, int i2);

    public native short dc_disp_mode(int i, short s);

    public native short dc_disp_str(int i, char[] cArr);

    /**
     * 退出设备
     */
    public native short dc_exit(int i);

    public native short dc_gettime(int i, char[] cArr);

    public native short dc_gettimehex(int i, char[] cArr);

    public native short dc_halt(int i);

    public native short dc_high_disp(int i, short s, short s2, char[] cArr);

    public native short dc_increment(int i, short s, int i2);

    /**
     * 设备初始化
     */
    public native int dc_init(int i, int i2);

    public native short dc_initval(int i, short s, int i2);

    public native short dc_load_key(int i, short s, short s2, char[] cArr);

    /**
     * 写卡
     */
    public native short dc_pro_command(int i, short s, char[] cArr, short[] sArr, char[] cArr2, short s2);

    public native short dc_pro_reset(int i, short[] sArr, char[] cArr);

    public native String dc_read(int i, short s, String str);

    public native short dc_read(int i, short s, char[] cArr);

    public native short dc_readval(int i, short s, int[] iArr);

    public native short dc_request(int i, short s, int[] iArr);

    /**
     * 重置设备
     */
    public native short dc_reset(int i, int i2);

    public native short dc_restore(int i, short s);

    public native short dc_select(int i, int i2, short[] sArr);

    public native short dc_setbright(int i, short s);

    public native short dc_settime(int i, char[] cArr);

    public native short dc_settimehex(int i, char[] cArr);

    public native short dc_srd_eeprom(int i, int i2, int i3, char[] cArr);

    public native short dc_swr_eeprom(int i, int i2, int i3, char[] cArr);

    public native short dc_transfer(int i, short s);

    public native short dc_write(int i, short s, String str);

    public native short dc_write(int i, short s, char[] cArr);

    static {
        System.loadLibrary("javaRD800");
    }

    public int getlDevice() {
        return lDevice;
    }

    public void setlDevice(int lDevice) {
        this.lDevice = lDevice;
    }

    public int getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(int deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String readCardId() {
        initDevice();
        long cardNum;
        int[] pSnr = new int[20];
        if (this.dc_card(this.getlDevice(), (short) 0, pSnr) != 0) {
            System.out.print(this.getClass().getSimpleName() + "_readCardId_dc_card error!\n");
            this.dc_exit(this.getlDevice());
        }
        this.dc_exit(this.getlDevice());
//        System.out.print("dc_card ok!\n");
//        System.out.println("cardnum:" + pSnr[0]);
        if (pSnr[0] < 0) {
            cardNum = -(-4294967296L - ((long) pSnr[0]));
        } else {
            cardNum = pSnr[0];
        }
        String cardId = String.valueOf(cardNum);
//        System.out.println("cardId:" + cardId);
        return cardId;
    }

    public int initDevice() {
        JavaRD800 rd = new JavaRD800();
        int lDevice = rd.dc_init(deviceNo, 115200);
        if (lDevice <= 0) {
            lDevice = rd.dc_init(++deviceNo, 115200);
            if (lDevice <= 0) {
                rd.dc_exit(lDevice);
                System.out.println("打开读卡器端口失败!" + deviceNo);
            }
        } else {
//            System.out.print(String.format("dc_init ok! %s\n", deviceNo));
        }
        if (rd.dc_reset(lDevice, 1) != 0) {
            System.out.print(String.format("dc_reset error! %s\n", deviceNo));
            rd.dc_exit(lDevice);
        }
//        System.out.print(String.format("dc_reset ok! %s\n", deviceNo));
        return lDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaRD800 javaRD800 = (JavaRD800) o;
        return lDevice == javaRD800.lDevice;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lDevice);
    }
}
