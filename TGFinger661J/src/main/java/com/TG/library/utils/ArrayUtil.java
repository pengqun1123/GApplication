package com.TG.library.utils;

/**
 * Created by WangFeng on 2017/6/28 0028 18:30.
 *
 * @desc 字节数组工具类
 */
public class ArrayUtil {

    /**
     * int转字节数组
     *
     * @param paramInt
     * @return
     */
    public static byte[] IntToLBytes(int paramInt) {
        return new byte[]{(byte) (paramInt & 0xFF), (byte) (paramInt >> 8 & 0xFF), (byte) (paramInt >> 16 & 0xFF), (byte) (paramInt >> 24 & 0xFF)};
    }

    /**
     * 拼接字节到字节数组中
     *
     * @param paramArrayOfByte 原始字节数组
     * @param paramByte        要拼接的字节
     * @return 拼接后的数组
     */
    public static byte[] MergerArray(byte[] paramArrayOfByte, byte paramByte) {
        byte[] arrayOfByte = new byte[paramArrayOfByte.length + 1];
        System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
        arrayOfByte[paramArrayOfByte.length] = paramByte;
        return arrayOfByte;
    }

    /**
     * 两个字节数组拼接
     *
     * @param paramArrayOfByte1 字节数组1
     * @param paramArrayOfByte2 字节数组2
     * @return 拼接后的数组
     */
    public static byte[] MergerArray(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
        byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramArrayOfByte2.length];
        System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
        System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.length, paramArrayOfByte2.length);
        return arrayOfByte;
    }

    /**
     * 字节数组拆分
     *
     * @param paramArrayOfByte 原始数组
     * @param paramInt1        起始下标
     * @param paramInt2        要截取的长度
     * @return 处理后的数组
     */
    public static byte[] SubArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        byte[] arrayOfByte = new byte[paramInt2];
        int i = 0;
        while (true) {
            if (i >= paramInt2)
                return arrayOfByte;
            arrayOfByte[i] = paramArrayOfByte[(i + paramInt1)];
            i += 1;
        }
    }

    /**
     * 截取字节数组
     * @param sourceByte
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static byte[] subArrays(byte[] sourceByte,int startIndex,int endIndex){
        byte[] aimByte = new byte[50];
        System.arraycopy(sourceByte,startIndex,aimByte,0,endIndex);
        return aimByte;
    }

    /**
     * int数组转byte数组
     *
     * @param paramArrayOfInt int数组
     * @return 转换后的byte数组
     */
    public static byte[] intsToBytes(int[] paramArrayOfInt) {
        byte[] arrayOfByte = new byte[paramArrayOfInt.length];
        int i = 0;
        while (true) {
            if (i >= paramArrayOfInt.length)
                return arrayOfByte;
            arrayOfByte[i] = (byte) paramArrayOfInt[i];
            i += 1;
        }
    }

    /**
     * 字符串转byte数组
     *
     * @param paramString 字符串
     * @param paramInt    字符串数组长度
     * @return 转换后的数组
     */
    public static byte[] stringToBytes(String paramString, int paramInt) {
        while (true) {
            if (paramString.getBytes().length >= paramInt)
                return paramString.getBytes();
            paramString = paramString + " ";
        }
    }
}