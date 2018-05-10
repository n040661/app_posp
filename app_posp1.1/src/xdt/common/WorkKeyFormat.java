package xdt.common;

public class WorkKeyFormat {

    // 工作密钥转换，为音频POS使用
    public static String getWorkKey(String theSecretKey, String workKeyHex) {
/**
 * 当39域为“00”时必选，终端参数下载无该域
 Pink(32+16)+mack(16+16)
 前面为key，后面部分为key校验值

 */

        //主密钥只在第一次下发
        //主密钥32、截取工作秘药32、校验值

        String workKey = workKeyHex.substring(0, 32);

        return workKey;
    }
}
