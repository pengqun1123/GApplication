package com.TG.library.api;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

/**
 * Created By pq
 * on 2019/3/19
 */
public interface TGXG661API extends Library {

    //"TGVM661JComAPI"
    TGXG661API TGXG_661_API = (TGXG661API) Native.loadLibrary("TGVM661J", TGXG661API.class);

    /**
     * Function：
     * 打开设备：PC 端打开下位机设备
     * Input：
     * （1）int* mode:保留参数（初值置 0）
     * Output：
     * （1）int* mode:保留参数
     * Return：
     * （1）0：设备打开成功
     * （2）-1：超时
     * Others：
     *
     * @param mode
     * @return
     */
    int TGOpenDev(IntByReference mode);

    /**
     * unction: 关闭设备：PC 端关闭下位机设备
     * Input：
     * （1）无
     * Output：
     * （1）无
     * Return：
     * （1）0：设备关闭成功
     * （2）-1：超时
     * Others：
     * （1）无
     *
     * @return
     */
    int TGCloseDev();

    /**
     * Function: 获取设备状态
     * Input：
     * （1）无
     * Output：
     * （1）无
     * Return：
     * （1）>=0：设备已连接
     * （2）<0：设备未连接
     * Others：
     * （1）无
     *
     * @return
     */
    int TGGetDevStatus();

    /**
     * unction: 获取设备固件号，即 firmware
     * Input：
     * （1）无
     * Output：
     * （1）char* fw：获取到的固件号（16 Bytes）
     * Return：
     * （1）0：获取设备固件号成功
     * （2）-1：超时
     *
     * @param fw
     * @return
     */
    int TGGetDevFW(byte[] fw);

    /**
     * Function: 获取设备序列号，即 sn
     * Input：
     * （1）无
     * Output：
     * （1）char* sn：获取到的序列号（16 Bytes）
     * Return：
     * （1）0：获取设备序列号成功
     * （2）-1：超时
     * Others：
     * （1）序列号为字符串
     *
     * @param sn
     * @return
     */
    int TGGetDevSN(byte[] sn);

    /**
     Function:
     获取设备的工作模式
     Input：
     无
     Output：
     int* mode：
     0：前比3特征模板
     1：后比
     2：前比6特征模板
     Return：
     0：获取成功
     -1：超时
     Others：
     无
     */
    int TGGetDevMode(IntByReference mode);

    /**
     Function:
     设置设备的工作模式
     Input：
     int* mode：
     0：前比3特征模板
     1：后比
     2：前比6特征模板
     Output：
     无
     Return：
     0：设置成功
     2：设置失败，该设备不支持6特征模板注册
     3：请先删除设备中的三模板
     4：请先删除设备中的六模板
     -1：设置失败
     -2 ：入参错误
     Others：
     立即生效
     * @return
     */
    int TGSetDevMode(int mode);

    /**
     * Function:
     * 写设备信息
     * Input：
     * char* devInfo;待写入设备的信息
     * int writeLen:带写入的信息长度。0<writeLen<=1024 Bytes。
     * Output：
     * 无;
     * Return：
     * >0：成功,实际写入长度
     * -1：超时
     * -2：入参错误
     * Others：
     * 无
     *
     * @param devInfo
     * @param writeLen
     * @return
     */
    int TGWriteDevInfo(byte[] devInfo, int writeLen);

    /**
     * Function:
     * 读设备信息
     * Input：
     * char* devInfo;待读出的信息
     * int readLen:待读出的信息长度。0<readLen<=1024 Bytes。
     * Output：
     * 无;
     * Return：
     * >=0：成功,实际读取长度
     * -1：超时
     * -2：入参错误
     * Others：
     * 无
     *
     * @param devInfo
     * @param readLen
     * @return
     */
    int TGReadDevInfo(byte[] devInfo, int readLen);


    /**
     * Function: 请求设备端注册
     * Input：
     * （1） int retImgMode：1：设备返回加密图片数据 ；0：设备不返回加密图片数据
     * （2）char* userName:用户 ID (<50Byte)
     * Output：
     * （1）无
     * Return：
     * （1）0：请求成功，需循环调用 TGGetDevRegIdentReturn 获取注册过程返回值
     * （2）-1：请求失败
     * Others：
     * （1）循环调用 TGGetDevRegIdentReturn,获取注册过程返回值。
     * （2）retImgMode 为 1 时，当 TGGetDevRegIdentReturn 的返回值为 VOICE_BI 时，
     * 调用 TGGetDevImage 获取加密图片数据
     *
     * @param retImgMode
     * @param userName
     * @return
     */
    int TGDevRegFinger(int retImgMode, byte[] userName);

    /**
     * Function: 请求设备端验证    1:N  验证
     * Input：
     * （1）int retImgMode：1：设备返回加密图片数据 ；0：设备不返回加密图片数据
     * Output：
     * （1）无
     * Return：
     * （1）0：请求成功,需调用 TGGetDevRegIdentReturn 获取验证返回值
     * （2）-1：请求失败
     * Others：
     * （1）调用 TGGetDevRegIdentReturn 获取验证结果
     * （2）retImgMode 为 1 时，当 TGGetDevRegIdentReturn 的返回值为 VOICE_BI 时，
     * 调用 TGGetDevImage 获取加密图片数据。
     *
     * @return
     */
    int TGDevIdentFinger(int retImgMode);

    /**
     * Function:
     * 请求设备端验证   1:1验证
     * Input：
     * int retImgMode：1：设备返回加密图片数据 ；0：设备不返回加密图片数据
     * char *userName:  待验证的用户名
     * Output：
     * 无
     * Return：
     * 0：请求成功,需调用 TGGetDevRegIdentReturn获取验证返回值
     * 2:    待验证的用户名不存在
     * else：请求失败
     * Others：
     * 调用 TGGetDevRegIdentReturn获取验证结果
     * retImgMode为1时，当TGGetDevRegIdentReturn的返回值为VOICE_BI时，
     * 调用TGGetDevImage获取加密图片数据。
     */
    int TGChangeIdentMode(int retImgMode, byte[] userName);

    /**
     * Function:
     * 获取设备注册和设备连续验证时的返回值
     * Input：
     * 无
     * Output：
     * char* templId：注册成功或验证成功时返回的ID(<50 Bytes)
     * Return：
     * 返回值为语音和CMD的宏定义值,根据宏名表示相应情况。
     * Others：
     */
    int TGGetDevRegIdentReturn(byte[] tmplId, int timeOutMs);

    /**
     * Function: 获取设备模板数。
     * Input：
     * （1）int maxTemplNumMode：0：设备中已注册的模板数；1：设备中可注册的最大模板数
     * Output：
     * （1）无
     * Return：
     * （1）>=0：模板数
     * （2）-1:超时
     * Others：
     * （1）默认设备中可注册的最大模板数为 300
     *
     * @param maxTemplNumMode
     * @return
     */
    int TGGetDevTmplNum(int maxTemplNumMode);

    /**
     * Function: 获取设备模板信息列表
     * Input：（1）无
     * Output：
     * （1）int* templNum：设备中已注册的模板数
     * （2）char* templNameList：设备中模板信息列表 （templNum * 50 Bytes）
     * Return：
     * （1）0：读取设备模板信息列表成功
     * （2）-1：超时
     * Others：（1) 无
     *
     * @param templNum
     * @param templNameList
     * @return
     */
    int TGGetDevTmplInfo(IntByReference templNum, byte[] templNameList);

    /**
     * Function: 清空设备中的模板。
     * Input：
     * （1）无
     * Output：
     * （1）无
     * Return：
     * （1）0：清空成功
     * （2）1：设备中不存在模板
     * （3）-1：超时
     * Others：
     * （1）无
     *
     * @return
     */
    int TGEmptyDevTmpl();

    /**
     * Function: 删除设备中指定的 ID 模板。
     * Input：
     * （1）char* templId：待删除的 ID 号,例如"0" Output：
     * （1）无
     * Return：
     * （1）0：删除成功
     * （2）1：设备中不存在待删除的 ID
     * （3）-1：超时
     * Others：
     * （1）无
     *
     * @param templId
     * @return
     */
    int TGDelDevTmpl(byte[] templId);

    /**
     * Function: 上传设备中指定 ID 模板到主机。
     * Input：
     * （1）char* templId：设备中待上传的模板 ID 名，例如"0" Output：
     * （1）unsigned char* tmplData：接收到的模板数据（17632 Bytes）
     * （1）int* tmplSize：接收到的模板大小
     * Return：
     * （1）0：上传成功
     * （2）1：设备中不存在待上传的模板
     * （3）-1：超时
     * Others：
     * （1）无
     *
     * @return
     */
    int TGUpDevTmpl(byte[] tmplId, byte[] tmplData, IntByReference tmplSize);

    /**
     * Function:
     * 设备中上传模板包到主机（模板包包含设备中所有模板）。
     * Input：
     * 无
     * Output：
     * unsigned char* tmplPkgData：接收到的模板包数据(<(17632+50)*300 Bytes)
     * int* tmplPkgSize：接收到的模板包大小
     * Return：
     * 0：上传成功
     * 1：设备中不存在模板
     * -1：超时
     * Others：
     * 无
     *
     * @param tmplPkgData
     * @param tmplPkgSize
     * @return
     */
    int TGUpDevTmplPkg(byte[] tmplPkgData, IntByReference tmplPkgSize);

    /**
     * Function: 下载主机中指定 ID 模板到设备。
     * Input：
     * （1）char* tmplId：待下载模板的 ID 名，例如"0"
     * （2）unsigned char* tmplData：待下载的模板数据
     * （3）int tmplSize：待下载的模板数据的大小
     * Output：
     * （1）无
     * Return：
     * （1）0：下载成功
     * （2）-1:超时
     * （3）-2:模板错误
     * （4）-3:指静脉已满
     * Others：
     * （1）无
     *
     * @param tmplId
     * @param tmplData
     * @param tmplSize
     * @return
     */
    int TGDownDevTmpl(byte[] tmplId, byte[] tmplData, int tmplSize);

    /**
     * Function:
     * 主机下载模板包到设备（会清空设备中的模板）。
     * Input：
     * unsigned char* tmplPkgData:待下载的模板包数据
     * int tmplPkgSize：待下载的模板数据的大小
     * Output：
     * 无
     * Return：
     * 0：下载成功
     * -1:超时
     * -2:模板包错误
     * -3:指静脉已满，设备中仅保存最大模板数
     * Others：
     * 无
     *
     * @param tmplPkgData
     * @param tmplPkgSize
     * @return
     */
    int TGDownDevTmplPkg(byte[] tmplPkgData, int tmplPkgSize);

    /**
     * Function:
     * 请求设备端连续验证
     * Input：
     * int retImgMode：1：设备返回加密图片数据 ；0：设备不返回加密图片数据
     * Output：
     * 无
     * Return：
     * 0：请求成功,需循环调用 TGGetDevRegIdentReturn获取验证结果
     * else：请求失败
     * Others：
     * 循环调用 TGGetDevRegIdentReturn获取验证结果
     * retImgMode为1时，当TGGetDevRegIdentReturn的返回值为VOICE_BI时，
     * 调用TGGetDevImage获取加密图片数据。
     *
     * @param retImgMode
     * @return
     */
    int TGDevContinueIdentFinger(int retImgMode);

    /**
     * Function:
     * 取消设备端注册或验证
     * Input：
     * 无
     * Output：
     * 无
     * Return：
     * 1：请求成功
     * else：请求失败
     * Others：
     * 无
     *
     * @return
     */
    int TGCancelDevRegIdent();

    /**
     * Function:
     * 播放设备语音：设备播放提示语音
     * Input：
     * int voiceValue：语音内容或音量级别
     * Output：
     * 无
     * Return：
     * 1：设备播放语音成功
     * else：设备播放语音失败
     * Others：
     * 语音内容已在宏中定义，设备第一次上电时，上位机要给设备设置一次语音音量，
     * 默认音量为VOICE_VOLUME4。
     */
    int TGPlayDevVoice(int voiceValue);

    /**
     * Function:
     * 设置led灯
     * Input：
     * int ledBlue：蓝灯控制：0：亮；1：灭；
     * int ledGreen：绿灯控制：0：亮；1：灭；
     * int ledRed：红灯控制：0：亮；1：灭；
     * Output：
     * 无
     * Return：
     * 0：设置led灯成功
     * -1：设置led灯失败
     * -2: 入参错误
     * Others：
     * 无
     */
    int TGSetDevLed(int ledBlue, int ledGreen, int ledRed);

    /**
     * Function:
     * 取消获取设备图像
     * Input：
     * 无
     * Output：
     * 无
     * Return：
     * 0：取消成功
     * Others：
     * 无
     */
    int TGCancelGetImage();

    /**
     * Function:
     * 获取设备图像
     * Input：
     * int timeout:获取图像等待的时间（即：超过这个时间没有检测到touch就返回-1）
     * timeout<=50,单位为秒s;  (timeout不能小于1s,否则直接返回-1)
     * timeout>50，单位为毫秒ms;
     * Output：
     * unsigned char* imageData：图像数据(缓存区大小不小于640*480 Bytes)
     * Return：
     * 0：设备获取图像成功
     * -1：抓图超时
     * -2:设备断开
     * -3:操作取消
     * -4:入参错误
     * Others：
     * 无
     */
    int TGGetDevImage(byte[] imageData, int timeOut);


    int TGRegCallBack();

}
