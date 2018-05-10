package xdt.dto.hfb;

/**
 * 十六进制功能类
 */
public class Hex
{
    /**
     * 16进制字符串转换为字节数组
     *
     * @param data 字符串
     * @return 转换后字节数组
     * @throws CodecException 异常
     */
    public static byte[] decode(String data) throws Exception
    {
        try
        {
            return org.bouncycastle.util.encoders.Hex.decode(data);
        } catch (Exception e)
        {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 字节数组转换为16进制字符串
     *
     * @param data 字节数组
     * @return 转换后字符串
     */
    public static String encode(byte[] data)
    {
        return new String(org.bouncycastle.util.encoders.Hex.encode(data));
    }

    public static void main(String[] args) throws Exception
    {
//        System.out.println(Hex.encode(new BigInteger("51813431104616102202539509023935904941").toByteArray()));
        System.out.println(Hex.encode("a张y".getBytes()));
        System.out.println(new String(Hex.decode("")));
    }
}
