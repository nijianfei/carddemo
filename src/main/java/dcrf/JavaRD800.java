package dcrf;

import cn.hutool.json.JSONUtil;
import com.jobcard.demo.bean.DeviceState;
import com.jobcard.demo.common.DeviceManage;
import com.jobcard.demo.enums.DeviceStateEnum;
import com.jobcard.demo.service.impl.AcTransStatus;
import com.jobcard.demo.util.CardReader;
import com.jobcard.demo.util.TemplateAdapter;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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
        int tryCount = 3;
        while (initDevice() < 0 && tryCount-- > 0) {
            DeviceManage.sleep(500);
        }
        if (initDevice() < 0) {
            log.error("设备 initDevice，deviceNo：{},lDevice：{}，返回结果cardId：{}", deviceNo, lDevice, "-1");
            return "-1";
        }
        long cardNum;
        int[] pSnr = new int[20];
        if (this.dc_card(this.getlDevice(), (short) 0, pSnr) != 0) {
            log.error("lDevice:{}, readCardId_dc_card error!", lDevice);
            System.out.print(this.getClass().getSimpleName() + "\n");
            this.dc_exit(this.getlDevice());
        }
        if (pSnr[0] < 0) {
            cardNum = -(-4294967296L - ((long) pSnr[0]));
        } else {
            cardNum = pSnr[0];
        }
        String cardId = String.valueOf(cardNum);
        log.info("设备dc_card，deviceNo：{},lDevice：{}，返回结果cardId：{}", deviceNo, lDevice, cardId);
        this.dcExit();
        return cardId;
    }

    public void dcExit() {
        if (this.dc_exit(this.getlDevice()) != 0) {
            System.out.print("dc_exit error!\n");
            this.dc_exit(this.getlDevice());
        }
        this.initDevice();
    }

    public int initDevice() {
        int lDevice = this.dc_init(deviceNo, 115200);
        log.debug("设备dc_init，deviceNo：{},返回结果lDevice：{},打开读卡器端口:{}", deviceNo, lDevice, lDevice > 0);
        if (lDevice <= 0) {
            short dcexitResult = this.dc_exit(lDevice);
//            System.out.println("打开读卡器端口失败!" + deviceNo);
            log.error("打开读卡器端口失败!设备dc_reset，deviceNo：{},lDevice：{}，返回结果dcexitResult：{}", deviceNo, lDevice, dcexitResult);
            return lDevice;
        }
        short resetResult = this.dc_reset(lDevice, 1);
        log.debug("设备dc_reset，deviceNo：{},lDevice：{}，返回结果resetResult：{},重置设备：{}", deviceNo, lDevice, resetResult, resetResult == 0);
        if (resetResult != 0) {
            System.out.print(String.format("dc_reset error! %s\n", deviceNo));
            short dcexitResult = this.dc_exit(lDevice);
            log.error("设备dc_reset，deviceNo：{},lDevice：{}，返回结果dcexitResult：{}", deviceNo, lDevice, dcexitResult);
        }
        this.lDevice = lDevice;
        return lDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaRD800 javaRD800 = (JavaRD800) o;
        return lDevice == javaRD800.lDevice && deviceNo == javaRD800.deviceNo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lDevice, deviceNo);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
/*        JavaRD800 rd = new JavaRD800();
        rd.setDeviceNo(100);
        rd.readCardId();
        DeviceState deviceState = new DeviceState();
        deviceState.setRd(rd);
        deviceState.setStateEnum(DeviceStateEnum.FREE);
        DeviceManage.deviceState.put(rd.getlDevice(), deviceState);*/


        String template = "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" +
                "cQsVZO7Qou7CagLTNR54ISJGbB5yoofFopArKcVjSH7U/lzTiXX1lSysfcXlXHhzF4jA7k21lhPeqwJvYvjjZZKnHgNxlIEiWWrUbrzQPopChC88A2JHHghJjL6JHu0gjverqHo8kb3qnxDv3Ayr2i3x/kVm6/qhR6YCFZJBKF8x8xis9rbhseb4pRih57wNqbdf+92UnP02JC45B+L4GKriZy49W56wNjVf6t8DLmEm4X05QQDJSW8GSU0J7xvB5dItmGWJ6cFVsPLnnMNCOtdJL7RjBPZQYC68/npgnYlVtxHYDgUgNh6roIN7yqlrUAaa7RR/902Iz/+Yh40ytLC+ww6QeO2/5lHrkjn3VI6+9NgktnN/G3qrakIFouLUo3mqJp2qJ4Ny/lms11+CMD4m3rU1ZC45y2wl1v09fyuwRHHzlkuWERPv+u9KjVavkZePq9yO//AxWMGWYcJzZDmhi21g/l5Agamw0kwszt88i/d7p5kqtI4Db3riSz6HchoawgibnjwSklg1DdE0xi8WsVQRaofuim1ObHeGp43l4YbfIorV8PQu7SHUuTmEHsEPbx3oLaK0LizwKSJwgqLwPbfxGmU7GKhMTrXk5+mY9F23QAwfs47A6/9Vyp+EgrOTo0uoUSsaBT7mRC6c7vmm1/nKiznvXHixROhZt4nAp57Fhdm5+14RTgXedKRXLIPwiN0g3XtHjp6BCUMhG6zgP4bap3FsB9O5siJtt4VkteibWb0Kyl9+HhL4h6VWrYDIB+9BePdBkBnWm1c1xV961hxZHXpW841fi6oI3J6HheI0tEQnp5syqirhl0ntFMQvlRgeS4+J4ZdP5p+/UYX+W7K+d7oaf7CbZnB5Hk23pMKPTZ67H4D4rrtB9klzPeHLLxRhL5JeaGvWLC78zkyNwHVahaarHVQ02RyCE4eBHYvytvARB0K8Q2OOygmUOHzsIfwCfdJla2E1Ruowtjsjo/tBBO/TiiY6i0Yco6FjzkX1x0gpYoFPO4Z7sJMoKy2aiLVsBLFWW/5+atkQEljFT+FjWOAaVWjlfwIFpn4uXkeMArCkWfkCprJgX3ZpDYFpQT8VftusB9ZhpDok+tJzYJeW8FTGNYfvDxF8EZlRKCT1Bu/aGazas6qQFPU6t92GpzfaGHktPDbaeDMI7D8ZXIcqVX/8Je8fQR946Lh/QJKGiahKf+IRkLr1ekjfcu160jWSZmG69TpI05LDfl0g1bkZWDOP5edTNgIsMBZ8Lpn0owr6wrUuvcwIfLER+GYR7LSTpHBrJ1bdQ2cwTH/9JcRuv4nX/Ya2qQeJt181e2uSXrQAojtsBsn220N5v52hYsIQiJ4wA5Krlhst/4zAAguMVWE6yXwU34DUI/jh/0lSj90Ppfi4QIsGEJ2xr9mq/K/IxA+sQmPhpx5qz6xiYlkx2SgwF3azGsmfSunOPEUKs1ah65qauiVmvw2hGtMQa+53Q0EIXncFWE89CgmsMiciIbNnXWo+cl1o1UzwgTuhAttV5RedBclPPzZ7/hhnza+Q+nAupD95H7JLvzBblf8VB4W1sQ3sujZ+PihAzsGILOuHqbBSFZoKqy+we4t0YrkzzTCSClx3VAlG96py6yL7/SPWeyR1X8qdavf/CDmOfv7ZR/wW/+vjFQHLIORsrEbTaiXM02RMF+9xQcVknYmiUmE/2az/veUuFtbBNjA1xex7dTGDouQNHjXmDixsLmGersZBJwPwZ3hlHrgPojt1gezd93DeuVkWMzinnSZLTe9RgRUlb1CBFlVJXBKZbjFPmxCa2eX4ImCyb7iWhc2dejpHZOfuu0U4FVhR8gUVawdv6CweGHtZYtpOxVUwR4Bzw/VceKkTiwXWU+ooSj7x1aQWLZ2e1gbXiMCTLWmdMbaR6QJrVGXO3HADWFh4rdOlDZw1bWBPBVaUfEBFkgeMsHBj2xer0XSlQ5lDjTrQWT5chyMya379jVx46bzQrPx90qlVFYEfV4EVpc7hqjOJK6L6SdrFWdKCC7/9z39y4XWPOFy0v+9+rUIrSr6gAk1JSrZ0YPkS0zw7UtqDBKYk5rAK/cijEO/YEbI33MyF2bnn3yqwouQHWYFkobQeXRAN27tcV2aJc9jmpUuX0pk8sBBTFPZscCwbMqEwuHQxcnysc9es3xU4pwIryqaDIy0KK+eUxhxKgBksqHRyHoelcOnMlnib86omeBAUqd07b+fC7Bx9tOSffJILu3PggZy3X32F97tU6Gn/nDkijy/wZ5+KLP36yXOrwIryV6Ci7afa1Nzm5+jWvfPffA4t65hjpLA/9VSlwNTJlUGBbcy7U6byfmvuXM47o0aIDAvniRwqsKLULSzwXTIX2jnqGIm4T/5HBDzwII7uziuvsODOlElc+G2sQpPA9qiRIsPC+UZgrUIrSp1CMuTuuIMFhiNnsMDZpx4TOQ6QKrT38otc+Elgyjtz3xGhsQrNcsxfaATWCKwodU7s1tthORbmwGEHsxyZp/7DEz3cAw+RCPzySyKDaQM7c99mge1RY0QGFJj3q8CKUves+O+r8ETrLvD5NTdx3n3icZHDVKGzr7wmQk7ZW2TAKjTtt1EKlqEyAmsVWlHqHDsSgdiS7yFTEZHC/oi0gR0UmPLOKy+JkFOmcxXaH0ZyRpsq9IIFIocKrCj5hQv7Q4/JuPABh0qn1ivSBvam7CNt4rkisD1GBVaUvINlGAs1x1aWw33oUS7czv6H8VbLjAN7U6aJDHNRAtrvC6xVaEWpY8gCA1/OJZfCTTLJw3n4ERkXNlVo9zVThZ6KhR7z9uw5IsPI0RKRF+g4sKLkERKY5KWT42HERYFZjoMOkML/yn+5sDtTprKwzpy5IujIUdzJZS/4WAVWlHwhly2l6zrwyXnAueMeLtzuxD0k//LLkp88jQWmqZQkgzV6JC+WqBR4NxVYUeocWftA2lACyD7zPES23hpSJ53Mhd1+qUpglsEIbKPAPE6sVWhFyR9UoG20wvNo+QMW9kwasst+ATseZ1mc556RiRtTpQptzXlX5PGnUmovtKLkD5YSxaHVTHS5UhKExaWdiPvsMyyLPXUab8vMeYfzuRE6jKQoeYeUoev+5zwLQzHqjP9J6CytL6b9zz0t48JTpA1skwSYd8xUShVYUfIJryEmgfHWpUiMN1ydlnFh+5knReDJUoX2Z2JlR43miOwt1HFgRckfKA24Nt5S0aa+aPxJPVucA8hiFZrawLQemAR2UGCSwcIIzAJXRmDtxFKUOod7oPlsHhRxfYFFXko2VqGpsNMZOUhYd64IbI8aKxF5wacqsKLkD5QWxaGrHNpYnXbo9DwssIwL248/wlVob8QIibhz3ufCb4+W1UiuTuRQlPxCUtBUjixGY7m2kuhL2zMvvwCx+ptD+hC5eLz3zmxpE1cOI32kAitKPqFCTbOqLLSDruDAAtMt7nFTMch88x3YFRXSBn79FYnIKHDNKrR2YilK3uCCTrckLpdyUsXi7T7cBn5NBHawCs3Hz/fPiaURWFHyBBVr0pOqzhR9MZ/jmIz36cLhVUdYr7/O8tCCfjrC0U4sRcknpASpyTpiwrjKY8OUp0uy0Mll/fNM497XXhcZRo7lfHbhxyKLCqwodQ8VaO64EjVQXLqHupLEKC/3T+Mu3oZb3FfNObLMeaGdhZ+IHCqwotQ9LAz+Y4GxdEt1mf5xFxUnGlqi2VmEgwKzPGNlGMlZoOeFVpQ8QvJmMGF7l6dSYqw1UVjiroMVaRejNKmFwr5olhfuZoT1r43k51VgRalLRGDPpYT30RCSRECdsCpNq5So4NN26823IIGFP7XHRJFpoXZiKUoeQWVoKqWLURdrzDyMxNsxn7MxT1c1JLEpKjvgxmOQXvg5ZFeuYhnsuXNFFhVYUfIAiek3d7F006VIqZBT5ZkuR0qXLfVYYBu3UY+0wCJhst+TE72rwIqSD0hgrjfTLVWVKc5KO5i20aVJba5mY/TlZYdU1caWsUMRGdvEc81ZKlVgRckHpIQfgklVh3+KwOQw9UfTFvyHUdhxMSI7KDRFZXyEg1VoFlZ7oRUlH5ASVYmrzpwoT7AylXkJzP4+lOOdd3lqZW7nrqg45r/5Vq5eiFLRVBASmPe3E4G98hKRqXFzySejLJPbsKEKrCh1jf3+B5BEGVKDB7Gw1lffshwkMDerly2XxQ/tO4jgFb7ALUUmFVhR8oeTTEDsk/ch9cOXLIflDyt1685dXtbyX0QmI7BTUSpX+G/cWoROxmV/Q61CK0odUlWNpnt+zvlEhpVcI3Bm5U9ySh6sQrPAKCFdf9hu3EoETpg2sAqsKHWJaEszpukM0hZXoLFKPU8isNutF29xli8Todu1FWHL18p5pps047wXR0lJLhVYUeoeEiKNP2yUj3BQYJLL6d6X5baxCs3yoMAsU3mJCE1SYt5LJIzA2gZWlDwg48c0zEQR1573Cbdx3R49OcI6y5aJjO3ai0y+wE0ai0wJbQMrSp6xePQ4jffsefNE0J5dWRZn2XIj8I4iE1aheX+THUSmpB+BVWBFyQOoVM5C+WiyB8ox72MeNgIUmPL2ilUsj9O+I2Qxb5WvFvlMFbqqE0ur0IpS99D0S48WPJhOrI8/hjTKke3cRcaFV/xsBG7PAmdRYJYLpeQqdgIlpbwKrCh1Dwti5kzT/dWLl8Dz9evD3GHDWeDcT9+LTO1lGMlGCSnvNhGBaXUT71eBFaXuIaHICsfNQRLvRgMR+Ok/T8Ov777H+9xvlvKwEQnMMpVXsHxW06YikwqsKPkElcplUD4LLKxO83rEathfLhGBzVxot7RCxoGbNpc2c1yHkRQlj9AwUhar0Bm8i21hlNCHZLOXfMHC5lA6lqlEIrDd1EzkqBwH1l5oRalzeP0SWuG6LkZgWXboB2IR+CuRrTICl7M8bmMjsA4jKUo+IJUo+qIQaKKDycIIbOXSQKfBsyHFR1lf+ALLTCyvrMwI3JRnarmVAmsVWlHyAvVA01X9MQbzon+PFv3TNYcR54tFLJvTyawHLjNzoZvuwBHYTiZFVl3Qryh1Dwnl8lRKlJb04A0mId4Xn/Pqo/SOZj1wWSkL7DaTTiw7FhVZNQIrSn5AD1AsVI8av3RaS7zxT9rhfrGY5cm17cLCWiVShfaatBShE1Herwv6FSVPkMAkLotM9/lW9rlffCHyNJe50HZpGWQxbzeRceActoFVYEXJI77A1SUmUYjY0m/gjXqbwfvdevBUSsAqNAnsNkUpMWv5bWDthVaUuoeFIi3YDEkkISXalw1F4fv7HoTlzz0rVebly7lNbDUTge14wgisEVhR6hyS1+LLr1AERqExClN3Fp10lmSpjf3Djyyf3bwZ789FYyyXVqEVJQ+QuFkUmHqiyQ6a1CGrg+W80agjJhIxzRHY+c4IbOZCuwmdC60oeYOFwn/U7mWHMQK7LCJN08ANuN3Cf1kaI8Yt7nc/yDBSYzkjh39WSo3AilLXkE0GqjqTHXQr86GNwAgv5Od7KNP3P4hcJCXmq06powIrSh6QXmcxg7LY9kURqersI26L7dUF5oisAitKPjER14/AVJXmbaSKgbaR5AgJzPI1kQisAitKXiE5HRaW5WVR/UQ3KCQnybo//MjyOGYc2PUvbqbjwIqSD6jKXCWwv80XmLbZuM82+5wfTQQ248C6HlhR8ghVl6nHuabAVdA2XuyAiXC+/55lym27jcikEVhR8kdtgTnhdhKFbjnv4j4aIEbsb7/js1amN0OhMO+l0iqwouSXqsjrC1wdX2zCi8Ug/fCDkHn2WT7ONQv8vUYksEwIcRqJwC6dcpYeYwR2y8ul57pDe8kvWy77u6nAipIX3JUrWa5co4bgeS44KLovsEcnfcdjfIGd8jIZXfYF/mW5RGQUmIVVgRWlbnFXrUa5toBcg234zB7UW+023FIiMJ1uh46pFLiUIzCdII+FXLZCBVaUfCICN0SBtwMX7aUhpxxGYBKMBOYqc+Om6whMl2+prEJrG1hR8oOz6leUqz7kGm7NVWiPOsOoOk3CJVMsrN2kOV/x0K6o4Cq0274dC+liBGbBVWBFyQ8uC4xyobSuSwsePPC2wohM22iICY8hgfksHhUBvlyLjQLTgghn+XIRWgVWlPzg/rpKVic1agCO64LjocBbShWaFzrQMXQ1f8w75eUibLt2nLeWr+BFEo4KrCj5gQSm6rG1ZUOwsP1ro2Fuoy2NwFEWLtekCQvI1xbGPF0snPI2CkwRWQVWlDzhrlrFHVJO/fooMM3YymF1ehsWzE3GpNOqcQsRsKxM2rwdZCKHs3yZ5P1x4K9VYEXZtMh8jkrcFVgNRpm4jZujNrALuYbbs3BuMsUR1mvSiiX3Sk0E7tCOoza1gTmvw0iKUreYiVngxeMQu/VWiN95J7qN7d94FOwtt+Z2sZVM8MkA3CZNReCyNSIgCkz77WUrJQJrFVpR8guJRbglZdgebmQEjnAEtpo2lplZfhu4fQepQuswkqLkGzqjFlWcqX8ZZVtbDk6DrVgwWuxPW9PNmvICCJoLLQK3k/0qsKLkH766IZpFctnlZWA33EqEi6c5Aqebt4AU5u3ygAjZroMI/IvOxFKUvEOnpqU50FxdLivBNvCWLFwuHmHhss2ac0eXU1EhEbdd1WoknQutKHnGpckbNI2S7petBGurBpCtj4ImQyyo06SpaQOXyTEdTBW62mokjcCKkhdyvNifT0uLeIE14G7dkAW2U3GeaWXT+bNI2JISEbC9RGBajcT57t1UYEXJD3QCADoPh+BiNTm3lbSB7WSah5H4BHiYd9eWcBXaa2uq0Cu0DawoeQc9QoFJTZS1bC24jbZFweqDl5DLsXhNGouQpbgP8zQXmseB11nM8KUKrCh1DZ3vjs6rRXLZZWvAargNtnlJYDmlTq5S4FIRsn07ucLhsmW6mEFR8g1FYP9aDlZFKVhbmsUM8aRE3CZNuBPLLZMF/f5J7WwUmPdrJ5ai5AuqPjssMMll0ZrfrWQYiQTmbdgGprnPnhFYT2qnKAUDCmxOJ0sdWXZZOThbNRLhzHrgSoHLZTWS3wtdOZFDx4EVJX9QFVrir3RieY18gaMsrNN0BxGyokpg2k9TKVnI7j3keO3EUpS6hSdicPSVgSS3bDXkGmzFEZcvfIbbnGbSBvbMXGjXHwfWE7srSv6hqZT+MJJbVgJOo4YisDmtrNO0uZx2BwVm4duZmVgrzGllVWBFyRdYeXYx0VgS4paWg7tFAxE0IaeV9Ro3kzy2gVlY0wtNAtN+V2diKUr+8PwTuuN9t6SUoy9H4IicUsdr3NwI7F8byawHXmEmcnRTgRUlT2D1GaMvVaNZtngUkheeBsmzTgLXccCzLXAxAlMVmi5uxseYNnD1U+qowIqSFzCm5mxONJwkA0oonewBNxoEu2lTnnlFvdAisL+gX5cTKkpe4QGkHFaEaSwYE58XGivGNirMAsfiKLBEYHc9ArOQGoEVJT+wpFx9Rmk9BywMvS4mh1b3I14stM71gf3TyrrLV4qg2gutKPlC2sA5rELTebF4SqWHP80Fwd1oALwmMpGDOrFYwA7SiUUX+OaOL12NpCiFAMdXvMHwi9GY8CIx8LANzMsHsQpNgZkisFydUCdyKEoBwV1SeMNxlbd4URS4SSsZNiqXqxPSBb5JSNf0QmsVWlHyDDaDMZHA1RPKF41CrnE7c2UGc1ZKfzXSimWS104sRckvJC/3SNN92cS4GIFzjVsbgeWslF5Hfyrl8lptYBVYUeoe7sSi4SP5Vz0GO7EouE3NVMq1JVKF3lEuL1o1lVIFVpT8kXNQYBeT3JLClETgGFhmNZJbtpYFdjECc5t4+S8ipJnI4arAilLXoKZmIgclWpVECtPcaD8CJ5s34asX0uVFSUCnfXvulXaW/yhCahtYUfKIX4X2T/DuOTwri6A2cLbJDnL5UbMe2GnXQQT+5WcRUqvQipJf0CVwXYy8DkViqihTQvlCEZEVk712tZzovV1nqVIvk15oFVhR8gyNINHFGWQ4SarShJdMQ+yEEyF24L5yxX48yOnQSdrAP5tOLK1CK0r+4E4rEtjkUWGRmIeWUEBMZmo0eOksL2ZggX9axtsdXQ+sKPmD5SVTEVJWFjfQP8pLotO+k8hOOgW2WQ/s/myq0BqBFSV/iLuiKQ0luTnqhZboS2DTmPPUKrbTSXDa78gC2j+tNFdm8M9KqQIrSt3DplJ8zeJdlJh7o6uir1w/WCKwnc1gBN6Rq9D2sl+MwF1VYEXJG9x7ha3ZHPcxYzLtX94uGlO3Fl+pMJ0Bq4MMI3m/fM9R2e2mAitK3pA2MI39ksCkKY0Lk7vUmYXK8S1pjHtQYDonFkVgb/mPHJW1F1pR8gRJaeEPiyZyYIXYxNnKfSQciex3crmZdOWVGbzl/jiw9kIrSt5wUE6X5kFTi5aiMFenSTUD7pcWMN5mkuC125GFrFrQr1VoRckbsnTBtIErBaZ2sCxpkOo0tY1Rxmwcq9CdjMByTiydiaUoeYW1Yml5ZZLpxKLeZ8rRXtzBP12KwO07isDLV5hOLBVYUfIOKeonEo1iLlWvKQr7uOkst4FJQDqxOx2j64EVJe9IR5VcYkWGjSi6SvW5SuGaAi+TCKwCK0r+oPFeXk5YKTC1fanyjInkpeq0UdjNpPi0siqwohQEJKi0easS5UleGg+myExKy9CSm0qC266jLGZYZtrAKrCi5A9UlkWle6gXC8wLGlhmiswmEuNeN5GtPCMHXVqFBpe0F1pR8ghVnVlQHjai6CsRV4aWHD5Th4PmsYzYBqYzcrCgK5axwBqBFSWPiMByhxYySPuXplXKuLCDEmdRau6VTmXB27yRCPmDnNROBVaUPELKUvSl2VdVAlP0tTHvcI6uWsjt3WwGMgftD6nBg8AtD4qgOg6sKPlDBo3o+sDU1pUqNFWpOSJjHZkjNPlN2z0bRc7i0Wl8DEZkF9vKXXUutKLkEYm4JDD3OKO8FIe5E4sFlio1CczjTAj9FIFdjcCKkl9YKxaU1iSxdKgnRWHqwLJyacxnJI81axer1bZDR4jcVW1gvbyoouQNEpSlxJ+UOMrSNt4uedKWBKboTNAqJrdbNxF26dci6IABIuyCBb8pMOVVYEXJM1zdxghMArtLl8qwEgrM1w+eP88IPJKFdVFg3j9NBVaUgoAEpio0C/y1COwM6C/nzFrwKec9E4Hd94zAU6fyfhVYUfKM54nALOjXX1cKzFdyWDBfxo5Hj2bBHROBnalTVGBFKQRIYH8qpR+BXRSY8vb8BTUE9iMwCUx5FVhR8owvMF8AvFobmIWet1Dyo0Zz3vOr0NqJpSiFQU2BZRw45/dCf+L3QovA7uzZLLD2QitKgeD3QrOw/kSOgQOkyvypCGyjwNIGniMCYxWahFeBFSXP1OyF/kKqyIOkFzo7/1NuA9ujx8j6YROB3WlTtQ2sKIUAVaGzvXpAhgT9ejELmhvUjyNyZsHHfJZpisAksPueCGyjwJYKrCj5hyKw1bsnpFFI+2upQtMwEleZF3zIAmfHjDRVaJmJpQIrSt6RqZR0Pi0Hq9AkpPOV9ELbAwdKlfmT900VWgSmYSQWHNvANE6sAitKnuFOrJ49RdgvvmKBnSFDJOLO+1A6tUYMl15oM4zkTZmsvdCKUgh4tFC4267cqwwmAmcGDeA2sTX/Y47TOX8udKXAOoykKAUBdWJVdOgJa1HIzJdLeZs1eACkWOBPJCLXElgncihKgYD+wkeHToeXdm4KoZW/8Da3v8yFzsyfB0nMW6NGcYS2Z8/m0/J4U6aqwIpSEKDAyZWLIPr9m7w2mKrMJDAJas2fLxcrNTOxnDlztQqtKIWMP4zEnVooMAvrL+ifM1s6tVBgncihKAWICDxAJm7Ml/XAlYsZKs/IoTOxFKWgkFHhmhHYNRFYz4mlKAWOCqwoRYwKrChFjAqsKEWMCqwoRYwKrChFjAqsKEWMCqwoRYwKrChFjAqsKEWMCqwoRYwKrChFjAqsKMWMMZgEpWsjkaC/LbCuRlKUgiKHpmbwls5CSddGogjsmeWE3qjRfEYOuj4wnZHDnbI371eBFaUAyNEV+z0PLLzPacAgPqWO++k8I/BYI/C7LLAzZZpGYEUpGOhUOq7FtWgW1FSh7U/ns9BupcDviNBTJ3KVWgVWlAIAPQTPJTVJYA+r0P1YYAur0Czw6FoCT5msEVhRCgWKvKgt36efTteeIvAH" +
                "87hN7I0ZYwR+m/e7WoVWlEJAup656uxZ3MOMwRjiQ0ZAFAXNLlwkp5EdPbKyDSwC62llFaVgyHk2eJjsXAZFdiD90y+Qmr8QPCsrw0qjh3PEdee8Y3qhVWBFKQww4ubcNEbeDHg5FBbbwiStD0Vce5S5uJkR2Jk6SavQilIoeKhlzteWhKaIbHqluco8aneOuO4cfxhJJ3IoSv7BBi/943YuZc0/rE/z2DDBVehRYyUCV44D76kCK0r+oRgrsdclb3M2Om3zZurMImgfXdzMr0JzLzUKrG1gRSkQyFW+TjBQtTmLiTQVgyUCy9UJHSOwO3WaCqwohUKOrhHMwlLC9m+OJKaEslKqvLiZL7Be3ExRCgZpBZOahIsCZ8D1UhJ9KY0czVVo5z0zDjxVh5EUpXDACCxRmFu8eD+LApsxYEooMEdgsxrJm7onT+xQgRWlAMh5Oci5mKgTi9rBtLaQe6hFYMesB3ZmzzECT1OBFaVQoA4smoHl5iy871JAFnNpH6bcqBG8/td5Xxb0u1Mm83JDFVhRCgCsQKOvtA6Jqs+oaDWBKQo7o0dBBoW15szhXbaeUkdRCgca8yVxRV7MkKWUzI030qxGeu8DXl5oT91bq9CKUiighywu3VZPvA+TPXh3rjI7787mwJyZuifYKrCiFAhkKU/F8qMxTeoQgYnP9toPXkJhSz56n/MWtoEtFVhRCgPRVQTmEGsE9hVe+8FH8ON990MiGOJt9pRJkFWBFaUwsPGfQ+YagalXmhYVsst8RBW0zZk2RavQilIoZFFLm8Z+UWAP28IO3re4V5rawiRxFo9KiryY3ClT9LSyilIoUJSlqEsC04QO16PoS7pSSKYI7UKGlZYt3uTJKrCiFBLoorET76PA1Rf504nt6KTvlGPZJ+liBkUpGLjLis2UROPBIrDAu0yGDnEnTVaBFaVQoLnPMpHDH//1bTbW8na5ywJjFVpnYilKgSACU1q/wNSZ5RiDaStdnVAjsKIUCNUFrkIEpk0ORmebplmarXpGDkUpIGjUt7bA/j3aRhc+c3E/5zE503RBv6IUDDxtgwSuPh/a7KM7ORcr0ZgI+unoGTkUpZCgVi5F2Mq4iwmF9v+R2OKvCKzLCRWlkCAtjaGM5CujcbWITHvcySqwohQQGyiwRmBFKSRUYEUpYlRgRSliVGBFKWJUYEUpYlRgRSliVOCNj/+OKcompwAEppPqpXIep7SfvGr3TT6JtzTHZJNhntqNRyH4zOMQfvEZznuJOAQefxAir74IOU9ewfpSFTVzirLpKACBgx+8B79MHA6l0/eAteOHwNoJw6B0/DC+XbsHJtq230RYPXEEZBbMM4/adORcB0qabg7hwT1lQzoFFZvVg4r9JnE285+HIH7qMZA85xRInnEiJC84HfNHg/3hHN6vKHVHAQgcfvk5iG5XDzJDe0Ng/ykQ2mcCpvGYxvFtEMVJDtoFsk3rg4uyb0rSwQoIvPcmRHfbCVIHToVEWSkkvloCyV06QuDYwyC8cgWk9p8Mia3rQbxXRzyuCyS7tYZI/XqQfewB8yyKUlcUgMCRt16Fiob1wLr2Mj6HD52Aq3qiS0IEzsco17IReF8uxtymI3jDFbAC/8AMRv7EmAGwaudWUIIpM3kUJPruBD8N6A7xKWMgPqQ3pFHsXDIOyTtuhsA29SH7yvPmWRSlrigUgRvUA+f6K8yWdSm/6EyItWgI3heLzJZNQ3reRxA8+1SIY20gPn0CBN54GUJPPgLJwT0gglX84GP3Qxojcwijb9o8JvHS81CBb4qFNQlFqVsKQODwW69BOQmM0e+3KL/4LIg1b7DJBSZocVZwRF9ITRgKHlaL7btnQXyn5lDxz/N4v4Vt32iXFpA67zTIXn4hJI84AMIdmkBWBVbqnAIQOPLmq1DeCCPY1RdLtdmxwbFNch2uVlecOxNirbasE4EtbPeGsAqdGrwLxMcNwTQYkt1bQ8UFZ/CbkD1vJiR7tMU2cAeI9GwHyQHdINa7AwqsVWilrikEgd9+FYI92kFqTH+Ijh8C0dH9ITaKUj+IjcZEAmFETAzsXjcCl66F4JCekNpzLFgfzMaq8fOQ6NYaAmeexG+CdepREOrcDDLffAWQTEDqrlsgsPVmWoVW8kABCJz8eC6EMZqlJwyD9L57QHr/SZgmy+1+mPadCIlxgyC+W5c6EdgOBCA8aSSk+3eFKH6xxLC9m+zeBtvhZ/ObYJ16NIR2bALJVJI72GJPPwaBzddtAyde+y8EbrgS7EjYbFGUjU0BCJyZPw/C21Ev9L/MlprQL664qA7awPjH0u9KOw6Eh/aB1JSRkJj7NiRQ0CS2eakKTdX57LmnQgLz4QMmQ8XRh0AMIzW1iatXoZMfzoGypptDCN+syFknma2KsrERYauQfJ0KnHh/NgS3rgf2tZeZLTWhF1BxYR30QuMfG7juMqg4YAqkhuwCyUkjIHDbDVBxyzWQofzIvlCOEiexVpAa3gcCWN0vnzgc4mMHQBzbyNU7sZJffA6lKHUI2/axay41WxVlY1MAAoffeAXKN6sHDoqyPugFlF96LsSabb6RBMZnxD9Mnrk6OSjbZzys3qUDxPcaB2EUtXTCcIj27QKx/adAyaH7QEW/nSHZuyNP8rAdm6/9Frv3DghuuW4VOhsoh+S3X5ucomwKCkBgGgcu33YLsC4+C7yVy2ulFeDgbXDmcRBrt+0mjsAeZBNxCEcjEB/VHwIo8c9ffQHhyaMgPrQXBKNRiC9aANFtMaqec7J5ENYgHrkPyhvWW6cXOrX0S4jo9Eplk1IgAgf6doLUgK6QaLM1JFDUytR2O9y2DaT7dYHEiF3B+2qJedSmI3Xb9RCvj23Xs0+GVNlaiN9/B0Q2R2kvvwCid94MUYy2qf9WRdvE809BOXVimcUPRPKD2VDWbAue4BG6/UazVVE2NgUicLBDY8gcNA0S2F5M/Os8SFxG6XxIXI7pqksgtvcESHRpCe7CT82jNj52NgsB/J1R/APDe4+vfFvoDYhSNfngfSDRtRVExg4EL5kEJ5OBzCsvQPqisyC0XX3IPP+kPABJYqQu67A9BOi5UHpF2TQUgMCBF56C1fik2UvO4V9WO9HMqNIzTmKxnHdex9ymIYNCBk8/Dir6dILsmtVmq5C0HSjr3wPC+BqSpq3r2TaEDtkbIritvGtLSGCVuTpRrD5XPP4wHlj9DVaUjUkBCBz/cjGs+scBkHj5WbNlXbLffg32Ky9CLlhhtmw6somEuWfAN4FmhSVfehYiWDPw3xDCmfsOBDBaR7HKXH37uvz+XkX5axSAwOugZV1R/iSFKLCiKH8SFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKmP/XAvsvvTa/tV1Rio1CEdhzIb12DWS/W2o21JTMCwUh+e4bYP2PVzqgM1xuKPQmZNPpmq9IvwOUgqBABCZxy7u3hcDuA8Fxfc2qLKHLfK7BXxw59WizxWAOcT0PEnR+6TNPhFyq1lklEQu/HEKnHAWhC2ZusHsuvgnhs06EcL+ukK5xWlt6pg19NkXZmBSIwJ5lQWLaWIg0qgfp1/5rtlbhrFoO4S3qQeL6y82WmlgofWDKaL4aYOLe283WKqxVK/jk8ZGt6kHqrdfM1j8H/eGZow7gk7TH33tTNipKQVAgAhOJB++GUNOtIfjGy5y3X3kBMjdeBdadN0OarpjQdydI/GNfsO64Cay7b4MsXTXw8YcqX2Dm048h1mZriGyNXwJLPuft/p9HKfHiMxDDfaGbr+bLhPrb/0wMzZx7CoSbN4T0ooVmi6IUAn4p9pF8nQnsLf8F4gdPg9DxMyC5zx4QpSsCnnMKlN54NYQO3RuiKFy8eQNIdNgB0nuNg2T/nSHebHOId27GV0SInHi4eSYRMXL+aRD+x3QIXHoelB17KATwucouOQfW3HglRGYey5cJDZx8JFScNxMC52LCbSE8zsUI/XtkzjkZBW4AqYXzzBZFKQTyLLDz3TewqltbWNVrRwgdOA1Sk0ZAdPN6EL3nNkj+exZfCd/+fCFYn38KwU5NIXnRWeD++D1Epu3Olzmx3n/PPFNNyu+7E1bg/tW7doZwz/aQ7NwUKgb1gNXTJ8KaQbvA6l4dYXXvTrCmc3Moab89ON98ZR65LvSHJ/CLItJ6K0h9Pl82KkpBkGeB6fpC8UA5xPF+ZM47GF23gMT+k3lfCqu6pRj1fEq3rQeZB/7N963bboD4kQeAk0lDzspC5rLzIHvpuZBF8TOzroP00i8hseIXiMZjUHHRmRDfbjPILPiEr+drmUTV6HQ6BUm6jCk+x29hey6E95sIEawFJJd8ZrYqSiGQL4H9ZzV4qRTERveDZI92EHzkPrBIymsuhVD77SD04RwIfTAbYju1gNhpx0Ho26+hAkWK/bqKn8YqLYGy9jtAAAWPtN0WQq22BOfnH+SJkYrLL0SB60HyhacgvuZXSGK1PbFiGUS++RocFPiPcH75EeIDu0O03XaQeupRs1VRCoF8CVyN9OpVEB43BBItG0H0+sshfPbJUDG0F0QP3w/SI3eDcqz6lg/sATGsCld0bAKl/bvBytZbQ/CJR/jxbiYDCayKp75fCrFD9sIvgbbgLvuF99GLD2B0TuG2tTs2hWWttoYVzRtCSfe28Cs+R+LZJ/i4Smp9sRDpF56GKH4xpLAKH8M2Og0rEes5tBIbvxjiiz/7S+POivLnyaPA9ITxZ/4D4f5dIYlt3SS2fyPTxkJiWB9IXHkRxE88ApL9uoLVtzNkJg6D8IP/hvCj90FmjyGQ3rkleD99L09Ujchxh0GyS3PIVbs8aOyM47nTK45V7Oi9t0H07lshe9BUiLfbHjKffGiOWj/8Gg/fFyIDukFoxn4QbVIfUs9Vl35djd1gAEL7ToJSPDZ2w5W/K7qi/G+IsFVIvk4EpieOnHwkBBrV4wt4pycMgSxGyijKYkWC4F59CUQG9YTEpJEQx6q1T7RzS+6trv6yCYp2kaMOgiTKCrGobEQy+4yHAEZvJ5sxW/DYC06H0mYNIPYHV/xPzv8Ywvj6kufOhBRW1WPdWkMEq9N2ct3JIj5ZrA3EdtgMbHyjwnsM1SisbELyJbB5Vmv1Sgi89RpkH74HEt1a8RBQrFcHCE8ZDak9hkHq9BMg8szjEMY2bOqReyH16AM8WSNtqs/VIVGiRx4I6X47Q3rxQsgs+xnsinKIYbSOTp/AnVYE/erUYdMhjDJmsD1cA/+vRRzPhegEjPwNUWDT25244hK+0Hj0/JmcXx8ePkfi+SchfMEZYH+12GxVlE1BvgSuhffkoxDCSBd54xWIYLs0jNXeUMemkP3nOeDg/siBUyHRqz3Eu7eG+GF7g+vQ1ppwBJ55DKSwzRweNwjCB06D6L9nsfzJKy+WgxB6bHzKKIgP7gkOVnfXB/2x4XNPhRj+sZGTDq+MolY4hBG4J8S2we333WG2rgv1ckeqRXxF2TQUiMD2w/fyuG4aBSZoOiRN1Eg89gDnk089CqkWW0K6zdYQv/W6Gi/Zx8YXGz5hBqQ7NYNE5+YQxPvR8YMh1rExpBZ+ao4CSGfS3EmWwehKw1i1oR7w8PmnQXyrehDarQtkS6ra00QKo3G06eYQa9GAZ3XV/ipxAuUQPHgvWIuvIfXUujUFRdl4FIjAFlahWeAln4PnOhDpipEW5aNIlpr3ISSxOh3DbeHenSDeqyPEzjkZIlj1Tn2xmIUhIg/ic2yP0XaXDhB942WIPvBvSDapD5Ej9pc/MRrG6q0H6R+/gyBWq+0Dp/DjCPojXXyexOsvQWi/yZBAeSOdmkLq04/lgOp1ayT22P0o8RYQb1wfwkccAKkP3gMPozOR+eZrbAPXgwz+PdFD9+JtirJpKBCBbRQ42qwhBO66GWLYPk2jeCGsNkcuOQfiNLaLYsZffBpiH78P4d0HcjuUOpfKxg2GzIpl/BzWV0sgOHxXCGO0Dj3+MKS7tIRI+x0gs1RmWcWffJiPT+y5O6RJ0FOO4u0EVZErHrybx5LT+LzBAd0g8dFc2VkDeTvoZxRrB6GurSCJx5c32QyCz/yHt+PbB5E7b4Y1e48H60ttAyubkgIROIvV4iA+ccVpx0Lk+MMgdPCekHj+aQh1aQGhPp0g9t4b5kisAkfCEMWqa6hbG8i+8LTZKjj4gtOuC8GzToRAg3oQu/UGswd/x08/cISOYDV87aSRkPp8gdkjuNhmjZ59MpRj+zm17CezdX34bwnWDhbMg8Co3SCGj/MyVW1eOqJ6p5mibBoKRGDr5x8g/szTYFM1F/OZtSW8PfHlIkjV7ik2ZFBkbLCa3LpEP/2oxtBRznMhNeddsLGa/lvdSy7KT18Cf0zVMY5jQw6r/YpS9xSIwH+OPyvWnzlOUf4/UNAC/xURReDffqTsV5T/HxS0wMT/Itv6HqvyKv+fKBCBf0urDdeNHvHbj9rw51OUQqZABFYU5a+gAitKEaMCK0oRowIrShGjAitKEaMCK0oRowIrShGjAitKEaMCK0oRowIrShGjAitKEaMCK0oRowIrShGjAitKEZNXgemp/acXauYURfl98iXwnzQ18cFsKL/ndsjUOjfzumwC9as9pZfNQPKLzyH5zVeQW89J5RUlP+RLYISfGH+46TQ4dK0h/GW1iR60J6zEXxj7cI7ZUgVJZSfi4Dnrnpx940CvR16TV7oWIt3bQOnIvr97XSRFqVvyKLBP7PTjIdiyEcTG9If4uCGYBkN8d0xjB0J690GQ2X0gJPYYxvkEbadzO+NxkUE9oKzttpB+42XzTJsQy4JM/52hbOLwda7EoCj5owAEztxxA8QO3gvCl54HwasugeDlF0LwiosgcN3lEN9vIqQGdofw2SdB4IYrZN+VkqLnnAKxA6eC8+lH5pk2AvgH0yln6bLfaZPofiYWhczQXaB8z3EQxbfE3+fvpzqAvFX+26UodUEBCOwTx0gauegMSF58NqTOPgWSF50Fqb3HQ2LILpA4+UjMnwlJlDZx1cUQveYSyFlZ88iNB13WZe2ZJ8KaPXeH0kkjoXT8ED4JfBldKZFO+I6J7pfuMZyv8rAWX99KzJfff6d5BkWpS/IqcM1oFTz3VIhvVg8Ch+8PJeefDmspwu41DtJDe0Hg2MOg5NyZsPbskyG8aydIt9sOcuuc8B2fD1/wX8M8Dh9vTRsDmVZbQmD6RCg9bB8oO3hPqNh/MqSwOp8YPxjKD5gKZdg2XztjP4igvFm6fvDVl8rjFaVOyZPA69MsjNXnVJPNIX7r9RB69w0Iv/kKJGbsC8leHSF650247U0Iv/YCxCaPgCxugzW/mkduXDIHTIFw386QxGozxXhKlpWBDNYEyjAyx802Sql3XodYw3qQ1gis5IU8CczUsjh42w2wdlQ/CEweCRVjB0H5uKHcaZXGqmpw3BAox+psBeYDGJXL95sETihoHrlxyeBzB3fbiS9XWp3M4J6wFn939U4s58O5EN2iHqTu/e1rBSvKpiOfAlczOPX9NxCcNhYiWG2NHDQNwpQO2RsSk0ZAanQ/iO47kfOR/TE6HjodggdOg/jtN9Z46RuLDP7uCEbg9NKvwMYvCTsYAPun7yE9rBdU4GtMrS0BJ1ABdiQCmScegdg2m60jML2uyMvPQfz9d2WDomwSRNgqJF9HAleR+fYrjLKDIX7CDAiffCSmIyA88xhIoDDp4btC9MgDIHTacRA6/Ti+n2mzDaQwUlanVkD/y6SOOZir7fEebSHavS1EMNFtYsJQSO8+EOJdW0G0WxuI9mwP8X5dING9NaTuq1mFTmK+nC472mxzyLz9utmqKBubAhDYevIRCE8dDeXH/wNKzp8JJedhwtvVF58Fsb3HY9uzF5Sj1KsvOQvT2VB+ylGQHrErJDE6B/FxqacfM8+0cUgfvh/E++wIqVOPhuyZJ3DKnHYsJMcOgNiEYZCm/JknQvaME/DY/SHWvjEKXDMCJ2+6iq9hHNmqHiRffNZsVZSNTQEInMDCHuyKUQwlTRx7KCSPPgSSx2A6bgakpo6BBMoaP2wfSB2PeUyJw6ZDAmVKYps4jI9L/PsW80wbh8z+kyHUtwtYXvU3htrAPaB073FQ/YKm1gdzINqA2sC3my1CznUh/u4bkJ7/yUarGSjKuhSAwLZtQ3LJQh7rdS44A6yLzgILo6/9rwshs88eEENx0liddq64GOwrLsJoeBzEu6HwJx2xzsvfGJDAQRTYqy3woJ5Qute4Gr/PRYEj6xGYiP7yI6QTG7WloSi1qG2A5OtUYCLz+adQsmMTCA/sDsGhvSGCVePIPuMhvsdwnk4ZwXZwYGgvqMAUHtgDktti1bRWG3hjQZ1Y0V07Q3jxQoiUrIbIml8htvRLyAzrDQGsEQRX/AKR1asgXLoWYo/eD/Ht6q8zjERt4FKsPpdjO9r6aonZqigbmwIROJfJgBMMgBOPgRMJQ2rhPF75kzphBkQ67ACJjz/gBQ+U0t8uhShKlvjX+TVe+sYiewxW43t1hJKdW8HKnVpw+rVzM4hi1I9ilX11p2a8bUWXllCOoqd6tIHk3beZR0tnWuK6yyGCb1R4m3qQwSitKJuGAhG4NrE7b4GKHu0ggREs0m57yKypuZzQxbRx5ZU/k/7g9LghEGvRECJnnQSRy86DGFbno/88GwLX/BOCV18C0UvOhhimyBUXYNt8OsTxDYlceh4/3sdLJaHsmssg/NRjlW+gomx8ClTgzNIlEOnaCiLjh0L4kfv4hdQFNBc6cMYJUHLUQbxA4Y/IfLUYVo/oD6EnHjFbCFVWqSsKTGD/lzmeA3bpGvA8t8512JDoTq+NjldllfxQYAIrirIhFKjANSIaZ+hHja2KohSqwDVRcRVl/RSFwIqirB8VWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKGBVYUYoYFVhRihgVWFGKmD8W2N9bQ2DZpChKfvljgatHYGfKZBS4HvwffRnV/xlKjUoAAAAASUVORK5CYIJbDQogIHsNCiAgICAidHlwZSI6IDAsDQogICAgIm5hbWUiOiAiY29tcGFueSIsDQogICAgIngiOiAyNSwNCiAgICAieSI6IDEyNywNCiAgICAid2lkdGgiOiA2OSwNCiAgICAiaGVpZ2h0IjogMTksDQogICAgImNvbG9yIjogLTE2Nzc3MjE2LA0KICAgICJmb250IjogIum7keS9kyIsDQogICAgImZvbnRTaXplIjogMTIuNzUsDQogICAgImZvbnRTdHlsZSI6IDEsDQogICAgImxheW91dCI6IDANCiAgfSwNCiAgew0KICAgICJ0eXBlIjogMCwNCiAgICAibmFtZSI6ICJuYW1lIiwNCiAgICAieCI6IDIxLA0KICAgICJ5IjogMTU2LA0KICAgICJ3aWR0aCI6IDExNiwNCiAgICAiaGVpZ2h0IjogNTIsDQogICAgImNvbG9yIjogLTE2Nzc3MjE2LA0KICAgICJmb250IjogIum7keS9kyIsDQogICAgImZvbnRTaXplIjogMzUuMjUsDQogICAgImZvbnRTdHlsZSI6IDEsDQogICAgImxheW91dCI6IDANCiAgfSwNCiAgew0KICAgICJ0eXBlIjogMCwNCiAgICAibmFtZSI6ICJ2aXNpdERhdGUiLA0KICAgICJ4IjogODEsDQogICAgInkiOiAyODEsDQogICAgIndpZHRoIjogODcsDQogICAgImhlaWdodCI6IDE5LA0KICAgICJjb2xvciI6IC0xNjc3NzIxNiwNCiAgICAiZm9udCI6ICLpu5HkvZMiLA0KICAgICJmb250U2l6ZSI6IDEyLjc1LA0KICAgICJmb250U3R5bGUiOiAxLA0KICAgICJsYXlvdXQiOiAwDQogIH0sDQogIHsNCiAgICAidHlwZSI6IDAsDQogICAgIm5hbWUiOiAiaW50ZXJ2aWV3ZWVzIiwNCiAg" +
                "ICAieCI6IDc5LA0KICAgICJ5IjogMzE4LA0KICAgICJ3aWR0aCI6IDExNCwNCiAgICAiaGVpZ2h0IjogMTksDQogICAgImNvbG9yIjogLTE2Nzc3MjE2LA0KICAgICJmb250IjogIum7keS9kyIsDQogICAgImZvbnRTaXplIjogMTIuNzUsDQogICAgImZvbnRTdHlsZSI6IDEsDQogICAgImxheW91dCI6IDANCiAgfSwNCiAgew0KICAgICJ0eXBlIjogMCwNCiAgICAibmFtZSI6ICJ2aXNpdFJlYXNvbiIsDQogICAgIngiOiA4MSwNCiAgICAieSI6IDM1NiwNCiAgICAid2lkdGgiOiAxMDUsDQogICAgImhlaWdodCI6IDE5LA0KICAgICJjb2xvciI6IC0xNjc3NzIxNiwNCiAgICAiZm9udCI6ICLpu5HkvZMiLA0KICAgICJmb250U2l6ZSI6IDEyLjc1LA0KICAgICJmb250U3R5bGUiOiAxLA0KICAgICJsYXlvdXQiOiAwDQogIH0sDQogIHsNCiAgICAidHlwZSI6IDAsDQogICAgIm5hbWUiOiAidmlzaXRSZWFzb24xIiwNCiAgICAieCI6IDgxLA0KICAgICJ5IjogMzgzLA0KICAgICJ3aWR0aCI6IDExNCwNCiAgICAiaGVpZ2h0IjogMTksDQogICAgImNvbG9yIjogLTE2Nzc3MjE2LA0KICAgICJmb250IjogIum7keS9kyIsDQogICAgImZvbnRTaXplIjogMTIuNzUsDQogICAgImZvbnRTdHlsZSI6IDEsDQogICAgImxheW91dCI6IDANCiAgfQ0KXQ==";
        TemplateAdapter adapter = TemplateAdapter.setTemplate(template);
        //分辨率：240x416
        String json = "{\n" +
                "        \"userId\": \"CSC123AA\", \n" +
                "        \"company\": \"杭州微影软件公司\", \n" +
                "        \"name\": \"赵小明A\", \n" +
                "        \"visitDate\": \"10/10～10/11\", \n" +
                "        \"intervieweesId\": \"CSC167AA\", \n" +
                "        \"intervieweesName\": \"王经理\", \n" +
                "        \"interviewees\": \"CSC167AA-王经理\", \n" +
                "        \"visitReason1\": \"二个\", \n" +
                "        \"visitReason\": \"来访信息技术部\"\n" +
                "    }";
        String json1 = "{\n" +
                "        \"userId\": \"CSC123AA\", \n" +
                "        \"company\": \"杭州微影软件公司\", \n" +
                "        \"name\": \"赵小明B\", \n" +
                "        \"visitDate\": \"10/10～10/11\", \n" +
                "        \"intervieweesId\": \"CSC167BB\", \n" +
                "        \"intervieweesName\": \"王经理\", \n" +
                "        \"interviewees\": \"CSC167BB-王经理\", \n" +
                "        \"visitReason1\": \"二个\", \n" +
                "        \"visitReason\": \"来访信息技术部\"\n" +
                "    }";
        Map<String, String> cardInfo = new HashMap<>();
        cardInfo = JSONUtil.toBean(json, Map.class);
        BufferedImage image = adapter.getImage(cardInfo);

        Map<String, String> cardInfo1 = new HashMap<>();
        cardInfo1 = JSONUtil.toBean(json1, Map.class);
        BufferedImage image1 = adapter.getImage(cardInfo1);
        JavaRD800 rd = new JavaRD800();
        rd.setDeviceNo(100);
        rd.readCardId();
        DeviceState deviceState = new DeviceState();
        deviceState.setRd(rd);
        deviceState.setStateEnum(DeviceStateEnum.FREE);
        DeviceManage.deviceState.put(rd.getlDevice(), deviceState);

        JavaRD800 rd800 = new JavaRD800();
        rd800.setDeviceNo(101);
        rd800.readCardId();
        DeviceState deviceState1 = new DeviceState();
        deviceState1.setRd(rd800);
        deviceState1.setStateEnum(DeviceStateEnum.FREE);
        DeviceManage.deviceState.put(rd800.getlDevice(), deviceState1);

        while (true) {
            CardReader cardReader = new CardReader(rd, new AcTransStatus(rd));
            CardReader cardReader1 = new CardReader(rd800, new AcTransStatus(rd800));
            ConcurrentHashMap<String, String> flag = new ConcurrentHashMap<>();
            if (flag.size() == 0) {

                new Thread(() -> {
                    flag.put("1", "1");
                    cardReader.start(image);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    flag.remove("1");
                }).start();
                new Thread(() -> {
                    flag.put("2", "2");
                    cardReader1.start(image1);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    flag.remove("2");
                }).start();
            }


            Thread.sleep(20000);
        }
    }
}
