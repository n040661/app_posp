package xdt.util;

public final class Base64
{
    private Base64(){}

    private static final byte[] DECODE_TABLE=new byte[]
                {    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,62, 0, 0, 0,63,
                    52,53,54,55,56,57,58,59,60,61, 0, 0, 0, 0, 0, 0,
                     0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,
                    15,16,17,18,19,20,21,22,23,24,25, 0, 0, 0, 0, 0,
                     0,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
                    41,42,43,44,45,46,47,48,49,50,51, 0, 0, 0, 0, 0
                };

    //"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    private static final byte[] ENCODE_TABLE=new byte[]
                {   65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90,
                    97, 98, 99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,
                    48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
                    43, 47
                };

    static
    {
        //create encode table
        //ENCODE_TABLE = new byte[64];
        int index = 0;
        for(char c='A'; c<='Z'; c++)
            ENCODE_TABLE[index++] = (byte) c;
        for(char c='a'; c<='z'; c++)
            ENCODE_TABLE[index++] = (byte) c;
        for(char c='0'; c<='9'; c++)
            ENCODE_TABLE[index++] = (byte) c;
        ENCODE_TABLE[index++] = (byte) '+';
        ENCODE_TABLE[index++] = (byte) '/';

        //create decode table
        for(int i=0; i<64; i++)
            DECODE_TABLE[(int) ENCODE_TABLE[i]] = (byte) i;
    }


    public static byte[] encode(byte[] data)
    {
        if(data==null)
            return null;

        int fullGroups = data.length/3;
        int resultBytes = fullGroups*4;
        if(data.length%3 != 0)
            resultBytes += 4;

        byte[] result = new byte[resultBytes];
        int resultIndex = 0;
        int dataIndex=0;
        int temp = 0;
        for(int i=0; i<fullGroups; i++)
        {
            temp = (data[dataIndex++]&0xff)<<16
                       | (data[dataIndex++]&0xff)<<8
                       | data[dataIndex++]&0xff;

            result[resultIndex++] = ENCODE_TABLE[(temp>>18) & 0x3f];
            result[resultIndex++] = ENCODE_TABLE[(temp>>12) & 0x3f];
            result[resultIndex++] = ENCODE_TABLE[(temp>>6) & 0x3f];
            result[resultIndex++] = ENCODE_TABLE[temp&0x3f];
        }
        temp=0;
        while(dataIndex<data.length)
        {
            temp<<=8;
            temp|=data[dataIndex++]&0xff;
        }
        switch(data.length%3)
        {
        case 1:
            temp <<= 8;
            temp <<= 8;
            result[resultIndex++] = ENCODE_TABLE[(temp>>18) & 0x3f];
            result[resultIndex++] = ENCODE_TABLE[(temp>>12) & 0x3f];
            result[resultIndex++] = 0x3D;
            result[resultIndex++] = 0x3D;
            break;
        case 2:
            temp <<= 8;
            result[resultIndex++] = ENCODE_TABLE[(temp>>18) & 0x3f];
            result[resultIndex++] = ENCODE_TABLE[(temp>>12) & 0x3f];
            result[resultIndex++] = ENCODE_TABLE[(temp>>6) & 0x3f];
            result[resultIndex++] = 0x3D;
            break;
        default:
            break;
        }

        return result;
    }

    public static byte[] decode(byte[] base64Data)
    {
        if(base64Data==null)
            return null;
        if(base64Data.length==0)
            return new byte[0];
        if(base64Data.length%4 !=0)
            throw new IllegalArgumentException("数据不完整，长度为："+base64Data.length);

        byte[] result=null;
        int groupCount = base64Data.length/4;

        int lastData = base64Data.length;
        while(base64Data[lastData-1] == 0x3D)
        {
            if(--lastData == 0)
                return new byte[0];
        }
        result = new byte[lastData-groupCount];

        int temp = 0;
        int resultIndex = 0;
        int dataIndex = 0;
        for(; dataIndex+4 < base64Data.length;)
        {
            temp = DECODE_TABLE[base64Data[dataIndex++]];
            temp = (temp<<6) + DECODE_TABLE[base64Data[dataIndex++]];
            temp = (temp<<6) + DECODE_TABLE[base64Data[dataIndex++]];
            temp = (temp<<6) + DECODE_TABLE[base64Data[dataIndex++]];

            result[resultIndex++] = (byte)((temp>>16) & 0xff);
            result[resultIndex++] = (byte)((temp>>8) & 0xff);
            result[resultIndex++] = (byte)(temp & 0xff);
        }

        temp = 0;
        int j = 0;
        for(; dataIndex<base64Data.length; dataIndex++,j++)
            temp = (temp<<6) + DECODE_TABLE[base64Data[dataIndex]];
        for(; j<4; j++)
            temp <<= 6;

        result[resultIndex++] = (byte)((temp>>16) & 0xff);
        if(base64Data[dataIndex-2] != '=')
            result[resultIndex++] = (byte)((temp>>8) & 0xff);
        if(base64Data[dataIndex-1] != '=')
            result[resultIndex++] = (byte)(temp & 0xff);

        return result;
    }
}