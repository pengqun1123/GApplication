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
     *
     * @param sourceByte
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static byte[] subArrays(byte[] sourceByte, int startIndex, int endIndex) {
        byte[] aimByte = new byte[50];
        System.arraycopy(sourceByte, startIndex, aimByte, 0, endIndex);
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

    /*===============================本人所写============================*/

    /**
     * 从一个源数组中剔除一个单元数组的数据,减小
     *
     * @param sourceByte       源数据数组
     * @param fingerTemplCount 模板的数量
     * @param index            要剔除的单元数组在源数组下的索引下标
     * @param cellByteLength   单元数组的长度
     * @return 返回剔除目标数据后的数组
     */
    public static byte[] subReduceAtIndexBytes(byte[] sourceByte, int fingerTemplCount,
                                               int index, int cellByteLength) {
        byte[] newByte = new byte[cellByteLength * (fingerTemplCount - 1)];
        if (index == 0) {
            System.arraycopy(sourceByte, cellByteLength, newByte, 0, sourceByte.length);
        } else if (index == cellByteLength - 1) {
            System.arraycopy(sourceByte, 0, newByte, 0, (sourceByte.length - cellByteLength));
        } else {
            int middleStart = index * cellByteLength;
            int middleEnd = (index + 1) * cellByteLength;
            System.arraycopy(sourceByte, 0, newByte, 0, middleStart);
            System.arraycopy(sourceByte, middleEnd, newByte, middleStart, sourceByte.length);
        }
        return newByte;
    }

    /**
     * 在一个byte[]数组的末尾增加一个新的单元数组数据
     *
     * @param sourceByte       源数组
     * @param fingerTemplCount 模板的数量
     * @param increaseData     新增加的数组单元
     * @param cellByteLength   数组单元的长度
     * @return 返回添加新单元数据之后的数组
     */
    public static byte[] subIncreaseAtIndexBytes(byte[] sourceByte, int fingerTemplCount,
                                                 byte[] increaseData, int cellByteLength) {
        byte[] newByte = new byte[cellByteLength * (fingerTemplCount + 1)];
        System.arraycopy(sourceByte, 0, newByte, 0, sourceByte.length);
        System.arraycopy(increaseData, 0, newByte, sourceByte.length, increaseData.length);
        return newByte;
    }

}