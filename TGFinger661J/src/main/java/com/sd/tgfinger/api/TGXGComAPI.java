package com.sd.tgfinger.api;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

/**
 * Created by WANGFENG on 2017-12-08 17:04.
 *
 * @desc
 */
public interface TGXGComAPI extends Library {

    TGXGComAPI INSTANCE = Native.loadLibrary("TGXGComAPI", TGXGComAPI.class);

    //指静脉模块设备操作相关函数

    /**
     * 获取当前连接的USB指静脉设备的个数，USB驱动必须是HID模式
     *
     * @return
     */
    int TG_DetectUsbDev();

    /**
     * 打开并连接指静脉设备
     *
     * @param pDev     设备类型 usb
     * @param Baud     波特率 115200
     * @param Addr     设备地址
     * @param Password 00000000
     * @param Len      密码长度
     * @param pHandle  返回的
     * @return
     */
    int TG_OpenVeinDev(String pDev, int Baud, int Addr, String Password,
                       int Len, IntByReference pHandle);

    /**
     * 关闭指静脉设备
     *
     * @param Addr   设备地址
     * @param Handle
     * @return
     */
    int TG_CloseVeinDev(int Addr, long Handle);

    /**
     * 发送一个指令包
     *
     * @param Addr   设备地址
     * @param Cmd    指令类型
     * @param Encode 数据编码
     * @param Len    数据长度
     * @param pData  数据
     * @param Handle
     * @return
     */
    int TG_SendPacket(int Addr, int Cmd, String Encode, int Len, byte[] pData, int Handle);

    /**
     * 接收一个指令包
     *
     * @param Addr   设备地址 0
     * @param pData  数据
     * @param Handle
     * @return
     */
    int TG_RecvPacket(int Addr, byte[] pData, int Handle);

    /**
     * 检测手指状态
     *
     * @param iAddr  设备地址 0
     * @param Handle
     * @return
     */
    int TG_GetFingerStatus(int iAddr, int Handle);

    /**
     * 写入数据
     *
     * @param Addr   设备地址
     * @param Cmd    指令类型
     * @param pData  指令数据
     * @param size   数据大小
     * @param Handle
     * @return
     */
    int TG_WriteData(int Addr, int Cmd, byte[] pData, int size, int Handle);

    /**
     * 读取数据
     *
     * @param Addr   设备地址
     * @param Cmd    指令类型
     * @param pData  指令数据
     * @param size   数据长度
     * @param Handle
     * @return
     */
    int TG_ReadData(int Addr, int Cmd, byte[] pData, int size, int Handle);

    /**
     * 更新指静脉固件
     *
     * @param iAddr
     * @param fname
     * @param Handle
     * @return
     */
    int TG_Upgrade(IntByReference iAddr, String fname, int Handle);

    /**
     * 从压缩数据中获取加密的指静脉图片数据
     *
     * @param pVein       加密的指静脉图片的压缩数据
     * @param size        加密的指静脉图片的压缩数据的长度
     * @param pEncryptBmp 加密的指静脉图片数据
     * @param pWidth      加密的指静脉图片宽
     * @param pHeight     加密的指静脉图片高
     * @param pChara      null
     * @param pSize       null
     * @return
     */
    int TG_DecodeVeinBmp(byte[] pVein, int size, byte[] pEncryptBmp,
                         IntByReference pWidth, IntByReference pHeight,
                         byte[] pChara, IntByReference pSize);

    /**
     * 从设备中获取加密的指静脉图片数据
     *
     * @param Handle      设备句柄
     * @param pEncryptBmp 加密的指静脉图片数据
     * @param pWidth      加密的指静脉图片宽
     * @param pHeight     加密的指静脉图片高
     * @param pChara      null
     * @param pSize       null
     * @return
     */
    int TG_GetDeviceVeinBmp(int Handle, byte[] pEncryptBmp,
                            IntByReference pWidth, IntByReference pHeight,
                            byte[] pChara, IntByReference pSize);

    int TG_GetVersion(byte[] ver);

    /**
     * 将解密后的图片转换成BMP
     *
     * @param imageData       加密的图片源数据
     * @param imageWidth      图片的宽度
     * @param imageHeight     图片的高度
     * @param upDownSymmetric 是否反转，默认传0
     * @param bmpData         转换后的图片
     * @param bmpDataSize     转换后图片的长度
     * @param bmpSavePath     转换后图片的保存路径
     * @return
     */
    int TG_DataToSimpleBMP(byte[] imageData, int imageWidth, int imageHeight, int upDownSymmetric,
                           byte[] bmpData, IntByReference bmpDataSize, String bmpSavePath);

}
