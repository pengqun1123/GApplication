package com.TG.library.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.TG.library.utils.AudioProvider;
import com.TG.library.utils.DevRootUtil;
import com.TG.library.utils.FileUtil;
import com.TG.library.utils.LogUtils;
import com.sun.jna.ptr.IntByReference;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created By pq
 * on 2019/4/29
 * TG661J前比API
 */
public class TG661JFrontAPI {

    /*设备的音量级别*/
    private static final int VOICE_VOLUME0 = 0xF0;//静音
    private static final int VOICE_VOLUME1 = 0xF2;
    private static final int VOICE_VOLUME2 = 0xF4;
    private static final int VOICE_VOLUME3 = 0xF6;
    private static final int VOICE_VOLUME4 = 0xF8;//设备初始化默认的音量
    private static final int VOICE_VOLUME5 = 0xFA;
    private static final int VOICE_VOLUME6 = 0xFC;
    private static final int VOICE_VOLUME7 = 0xFE;
    public static final int VOICE_REG_SUCCESS = 0x02;//登记成功
    public static final int VOICE_REG_FAIL = 0x07;//登记失败
    public static final int VOICE_IDENT_SUCCESS = 0x07;//验证成功
    public static final int VOICE_IDENT_FAIL = 0x08;//验证失败

    public static final int SUCCESS_FLAG = 0;

    public static final int OPEN_DEV = 0xf2;
    public static final int CLOSE_DEV = 0xf3;
    public static final int DEV_TEMPL_NUM = 0xf4;
    public static final int DEV_FW = 0xf6;
    public static final int DEV_SN = 0xf7;
    public static final int DEV_WORK_MODEL = 0xf8;
    public static final int DEV_TEMPL_LIST = 0xf9;
    public static final int DEV_TEMPL_CLEAR = 0xf10;
    public static final int DEV_REGISTER = 0xf11;
    public static final int DEV_VERIFY_TEMPL = 0xf12;
    public static final int DEV_DEL_ID_TEMPL = 0xf13;
    public static final int UP_TEMPL_HOST = 0xf14;
    public static final int DOWN_TEMPL_DEV = 0xf15;
    public static final int WAIT_DIALOG = 0xf16;
    public static final int UP_TEMPL_PAC_HOST = 0xf18;
    public static final int DOWN_TEMPL_PAC_DEV = 0xf19;
    public static final int WRITE_FILE = 0xf20;
    public static final int READ_FILE = 0xf21;
    public static final int CANCEL_VERIFY = 0xf22;
    public static final int CONTINUE_VERIFY = 0xf23;
    public static final int DEV_VOICE = 0xf24;
    public static final int DEV_VERIFY1_1 = 0xf25;
    public static final int WRITE_DEV_INFO = 0xf26;
    public static final int READ_DEV_INFO = 0xf27;
    public static final int DEV_STATUS = 0xf28;
    public static final int DEV_IMG_REGISTER = 0xf29;
    public static final int CANCEL_DEV_IMG = 0xf30;
    public static final int WRITE_LICENSE = 0xf44;
    public static final int DELETE_HOST_ID_TEMPL = 0xf45;//删除主机指定模板
    public static final int DELETE_HOST_ALL_TEMPL = 0xf46;//删除主机所有模板
    public static final int UPDATE_HOST_TEMPL = 0xf47;//更新主机中的模板
    public static final int SET_DEV_MODEL = 0xf48;//设置设备模式
    public static final int DEV_PLAY_VOICE = 0xf50;//设备播放声音资源

    public static final String TEMP_LIST = "templ_list";//模板列表

    //3模板
    public static final int TEMPL_MODEL_3 = 3;
    //6模板
    public static final int TEMPL_MODEL_6 = 6;
    //前比模式
    public static final int WORK_FRONT = 1;

    private static final int IMG_SIZE = 500 * 200 + 208;
    private static final int FEATURE_SIZE = 6016;
    //临时加的图片大小
    private static final int T_SIZE = 1024 * 500;
    //完整的特征大小
    private static final int PERFECT_FEATURE_17682 = 17682;
    private static final int PERFECT_FEATURE_35058 = 35058;
    private static final int PERFECT_FEATURE_3 = 17632;//3特征
    private static final int PERFECT_FEATURE_6 = 35008;//6特征

    private static final int GET_IMG_OUT_TIME = 15;//默认设置抓图超时的时间为15S
    private static final int GET_IMG_OUT_TIME_5000 = 5000;//默认设置抓图超时的时间为5000

    //SDK的当前版本号
    private static final String SDK_VERSION = "1.1.0_190417_Beta";

    //启动目标service的Action
    private static final String DevServiceAction = "com.example.mylibrary.DevService.action";
    //标记devService是否已经启动
    private boolean isStart = false;
    private String tgDirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + (File.separator + "TG_TEMPLATE");
    //前比模板的文件夹
    private String frontDatDir = tgDirPath + File.separator + "FrontTemplate";
    //日志的路径
    private String logDir = tgDirPath + File.separator + "Log";

    //前比的3，6模板路径
    private String frontTempl3Path = frontDatDir + File.separator + "TEMPL_3";
    private String frontTempl6Path = frontDatDir + File.separator + "TEMPL_6";

    //图片存储的路径
    private String imgPath = tgDirPath + File.separator + "IMGS";

    @SuppressLint("InlinedApi") //读写文件，定位，手机状态，地理位置
    public String[] perms = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    //获取前比主机存储3模版的路径
    public String getFrontHost3TemplPath() {
        return frontTempl3Path;
    }

    //获取前比主机存储6模板的路径
    public String getFrontHost6TemplPath() {
        return frontTempl6Path;
    }

    //获取主机存储日志的路径
    public String getLogDir() {
        return logDir;
    }

    /**
     * 检查设备是否已经Root
     */
    private void checkDevIsRoot() {
        boolean rootSystem = DevRootUtil.isRootSystem();
        if (rootSystem) {
            LogUtils.d("设备已经Root");
        } else {
//            ToastUtil.toast(context, "设备没有Root，无法使用");
            LogUtils.d("设备没有Root，无法使用");
            return;
        }
        writeCMD();
        createDirPath();
    }

    private void writeCMD() {
        String command = "chmod -R 777 /dev/bus/usb";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            int i = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件到主机
     *
     * @param templData
     * @return
     */
    public boolean writeTemplHost(byte[] templData, String tmlID) {
        String templId = tmlID.substring(0, tmlID.indexOf(".dat"));
        String datFilesPath = getAimPath() + File.separator + templId + ".dat";
        boolean writeFile = FileUtil.writeFile(templData, datFilesPath);
        return writeFile;
    }

    //获取目标文件夹得路径
    private String getAimPath() {
        String aimPath = "";
        if (templModelType == TEMPL_MODEL_3) {
            aimPath = getFrontHost3TemplPath();
        } else if (templModelType == TEMPL_MODEL_6) {
            aimPath = getFrontHost6TemplPath();
        }
        return aimPath;
    }

    //创建相关的文件夹,获取到相关的路径
    private void createDirPath() {
        File dat3File = new File(frontTempl3Path);
        if (!dat3File.exists())
            dat3File.mkdirs();
        File dat6File = new File(frontTempl6Path);
        if (!dat6File.exists())
            dat6File.mkdirs();
        File logFile = new File(logDir);
        if (!logFile.exists())
            logFile.mkdirs();
    }

    //获取定义的权限数组
    public String[] getPerms() {
        return perms;
    }

    private static TG661JFrontAPI TG661J_FRONT_API = null;

    public static TG661JFrontAPI getTg661jFrontApi() {
        if (TG661J_FRONT_API == null) {
            synchronized (TG661JFrontAPI.class) {
                if (TG661J_FRONT_API == null) {
                    TG661J_FRONT_API = new TG661JFrontAPI();
                }
            }
        }
        return TG661J_FRONT_API;
    }

    //获取通信库的代理对象
    private TGXG661API getTG661() {
        return TGXG661API.TGXG_661_API;
    }

    private Context context;
    private Activity mActivity;

    private Handler handler = null;
    public boolean devOpen = false;//设备是否已经打开
    public boolean devClose = false;//设备关闭的标志
    //当前的默认音量
    private String currentVoice = "4";
    private int CurrentDevVoiceValue = VOICE_VOLUME4;

    /**
     * 设置通信模式
     */
    private int devStatus = -1;//默认设备未开启的状态

    public boolean isDevOpen() {
        return devOpen;
    }

    public AudioProvider getAP() {
        return AudioProvider.getInstance(context);
    }

    /**
     * 获取SDK的版本
     */
    public String getSDKVersion() {
        return SDK_VERSION;
    }

    private int templModelType = TEMPL_MODEL_6;//默认是6模板
    private int templSize = 0;//模板的个数
    //特征融合后模板的存储路径
    private String templSavePath;
    private String templNameID;

    public void openDev(Handler mHandler, Activity activity, int templModelType) {
        this.handler = mHandler;
        this.context = activity;
        this.mActivity = activity;
        this.templModelType = templModelType;
        if (devStatus == -1) {
            devStatus = getTG661().TGGetDevStatus();
            if (devStatus >= 0) {
                return;
            }
            //检查设备是否已经root
            checkDevIsRoot();
            //检查设备权限
            checkPermissions(2);
            work(mHandler, OPEN_DEV);
        }
    }

    private void initLicense() {
        work(handler, WRITE_LICENSE);
    }

    /**
     * 关闭设备
     */
    public void closeDev(Handler handler) {
        this.handler = handler;
        if (!devOpen) {
            //设备处于关闭状态
            Message closeMsg = handler.obtainMessage();
            closeMsg.what = CLOSE_DEV;
            closeMsg.arg1 = 2;
            handler.sendMessage(closeMsg);
            return;
        }
        //解绑devService
//        if (isStart) {
//            isStart = false;
//            context.unbindService(serviceConnection);
//        }
        //否则，关闭设备
        work(handler, CLOSE_DEV);
    }

    /**
     * 设置设备得模式
     */
    public void setDevWorkModel(Handler handler, int workType, int templModelType) {
        this.handler = handler;
        this.templModelType = templModelType;
        this.workType = workType;
        work(handler, SET_DEV_MODEL);
    }

    /**
     * 获取设备的工作模式
     *
     * @param handler
     */
    public void getDevWorkModel(Handler handler) {
        this.handler = handler;
        work(handler, DEV_WORK_MODEL);
    }

    /**
     * 获取设备固件号
     *
     * @param handler
     */
    public void getDevFW(Handler handler) {
        this.handler = handler;
        work(handler, DEV_FW);
    }

    /**
     * 获取设备序列号
     *
     * @param handler
     */
    public void getDevSN(Handler handler) {
        this.handler = handler;
        work(handler, DEV_SN);
    }

    /**
     * 获取设备链接状态
     *
     * @param handler
     */
    public void getDevStatus(Handler handler) {
        this.handler = handler;
        work(handler, DEV_STATUS);
    }

    /**
     * 清空设备中的模板
     *
     * @param handler
     */
    public void clearDevTempl(Handler handler) {
        this.handler = handler;
        work(handler, DEV_TEMPL_CLEAR);
    }

    /**
     * 获取设备中可注册最大的模板数量或者设备中已注册的模板数量
     * type 0：设备中已注册的模板数；1：设备中可注册的最大模板数
     *
     * @param type  接口类别标志
     * @return
     */
    private int type = 0;
    private int DevTemplNum = 0;

    public void getDevTemplNum(Handler handler, int type) {
        this.handler = handler;
        this.type = type;
        work(handler, DEV_TEMPL_NUM);
    }

    /**
     * 获取设备端模板信息列表
     */
    public void getDevTemplList(Handler handler) {
        this.handler = handler;
        work(handler, DEV_TEMPL_LIST);
    }

    /**
     * 设置设备的音量。
     *
     * @param handler
     * @param type  1:音量加  2:音量减
     */
    private int devVoice;

    public void setDevVoice(Handler handler, int type) {
        this.handler = handler;
        if (type == 1) {
            if (CurrentDevVoiceValue == VOICE_VOLUME0) {
                this.devVoice = VOICE_VOLUME1;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME1) {
                this.devVoice = VOICE_VOLUME2;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME2) {
                this.devVoice = VOICE_VOLUME3;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME3) {
                this.devVoice = VOICE_VOLUME4;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME4) {
                this.devVoice = VOICE_VOLUME5;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME5) {
                this.devVoice = VOICE_VOLUME6;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME6) {
                this.devVoice = VOICE_VOLUME7;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME7) {
                Message voiceMsg = handler.obtainMessage();
                voiceMsg.what = DEV_VOICE;
                voiceMsg.arg1 = 3;
                handler.sendMessage(voiceMsg);
            }
        } else if (type == 2) {
            if (CurrentDevVoiceValue == VOICE_VOLUME7) {
                this.devVoice = VOICE_VOLUME6;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME6) {
                this.devVoice = VOICE_VOLUME5;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME5) {
                this.devVoice = VOICE_VOLUME4;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME4) {
                this.devVoice = VOICE_VOLUME3;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME3) {
                this.devVoice = VOICE_VOLUME2;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME2) {
                this.devVoice = VOICE_VOLUME1;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME1) {
                this.devVoice = VOICE_VOLUME0;
                work(handler, DEV_VOICE);
            } else if (CurrentDevVoiceValue == VOICE_VOLUME0) {
                Message voiceMsg = handler.obtainMessage();
                voiceMsg.what = DEV_VOICE;
                voiceMsg.arg1 = 2;
                handler.sendMessage(voiceMsg);
            }
        }
    }

    /**
     * 设备播放语音
     */
    private int voiceRes;

    public void devPlayVoice(Handler handler, int res) {
        this.handler = handler;
        voiceRes = res;
        work(handler, DEV_PLAY_VOICE);
    }

    /**
     * 注册模板
     */
    public void registerDev(Handler handler, String userID) {
        this.handler = handler;
        if (userID.getBytes().length > 49) {
            Message devRegisterMsg = handler.obtainMessage();
            devRegisterMsg.what = DEV_REGISTER;
            devRegisterMsg.arg1 = -9;
            handler.sendMessage(devRegisterMsg);
        } else {
            this.templNameID = userID;
            work(handler, DEV_REGISTER);
        }
    }

    /**
     * 设备模板1：1验证
     *
     * @param handler
     * @param userNameID
     */
    public void verifyDev1_1(Handler handler, String userNameID) {
        if (TextUtils.isEmpty(userNameID)) {
            Message verDev1_1Msg = handler.obtainMessage();
            verDev1_1Msg.what = DEV_VERIFY1_1;
            verDev1_1Msg.arg1 = -9;
            handler.sendMessage(verDev1_1Msg);
        }
        this.handler = handler;
        this.templNameID = userNameID;
        work(handler, DEV_VERIFY1_1);
    }

    /**
     * 请求设备验证模板1：N
     */
    public void devModelVerify(Handler handler) {
        this.handler = handler;
        work(handler, DEV_VERIFY_TEMPL);
    }

    /**
     * 取消验证
     *
     * @param handler
     */
    public void cancelVerify(Handler handler) {
        this.handler = handler;
        work(handler, CANCEL_VERIFY);
    }

    /**
     * 前比连续验证
     *
     * @param handler
     * @param retImgType
     */
    private int retImgType;

    public void continueVerify(Handler handler, int retImgType) {
        this.handler = handler;
        this.retImgType = retImgType;
        work(handler, CONTINUE_VERIFY);
    }

    /**
     * 写入设备信息
     *
     * @param info
     */
    private byte[] devInfo;

    public void writeDevInfo(Handler handler, byte[] info) {
        Message writeDevInfoMsg = handler.obtainMessage();
        writeDevInfoMsg.what = WRITE_DEV_INFO;
        if (info == null || info.length > 1024) {
            writeDevInfoMsg.arg1 = -9;
            handler.sendMessage(writeDevInfoMsg);
            return;
        }
        this.handler = handler;
        this.devInfo = info;
        work(handler, WRITE_DEV_INFO);
    }

    /**
     * 读取设备信息
     *
     * @param handler
     */
    public void readDevInfo(Handler handler) {
        this.handler = handler;
        work(handler, READ_DEV_INFO);
    }

    /**
     * 从设备上传指定ID的模板到主机
     *
     * @param handler
     * @param datFileName
     */
    public void upTemplHost(Handler handler, String datFileName, int templModelType) {
        if (TextUtils.isEmpty(datFileName)) {
            Message upTemplMsg = handler.obtainMessage();
            upTemplMsg.what = UP_TEMPL_HOST;
            upTemplMsg.arg1 = -9;
            handler.sendMessage(upTemplMsg);
            return;
        }
        if (!TextUtils.isEmpty(datFileName)) {
            String fileName = datFileName.substring(0, datFileName.indexOf(".dat"));
            this.templId = fileName.getBytes();
        }
        this.handler = handler;
        this.templateName = datFileName;
        this.templModelType = templModelType;
        work(handler, UP_TEMPL_HOST);
    }

    private String aimPath;

    /**
     * 下载指定模板到设备
     *
     * @param handler
     * @param datFileName    模板得名字
     * @param templModelType 3或者6模板的类型
     */
    public void downTemplDev(Handler handler, String datFileName, int templModelType) {
        if (TextUtils.isEmpty(datFileName)) {
            Message downTemplMsg = handler.obtainMessage();
            downTemplMsg.what = DOWN_TEMPL_DEV;
            downTemplMsg.arg1 = -9;
            handler.sendMessage(downTemplMsg);
            return;
        }
        this.handler = handler;
        this.templNameID = datFileName;
        this.templModelType = templModelType;
        Log.d("===LOG", "   模板模式类型：" + templModelType);
        work(handler, DOWN_TEMPL_DEV);
    }

    /**
     * 从设备删除指定ID的模板
     *
     * @param handler
     * @param templID
     */
    private byte[] templId;

    public void delIDTemplDev(Handler handler, String datFileName, int templModelType) {
        this.handler = handler;
        if (TextUtils.isEmpty(datFileName)) {
            Message delIdTemplMsg = handler.obtainMessage();
            delIdTemplMsg.what = DEV_DEL_ID_TEMPL;
            delIdTemplMsg.arg1 = -9;
            handler.sendMessage(delIdTemplMsg);
            return;
        } else {
            String fileName = datFileName.substring(0, datFileName.indexOf(".dat"));
            this.templId = fileName.getBytes();
        }
        this.templModelType = templModelType;
        work(handler, DEV_DEL_ID_TEMPL);
    }

    /**
     * 获取设备图像
     *
     * @param handler
     */
    public void getDevImg(Handler handler) {
        this.handler = handler;
        work(handler, DEV_IMG_REGISTER);
    }

    /**
     * 取消获取设备图像
     *
     * @param handler
     */
    public void cancelDevImg(Handler handler) {
        this.handler = handler;
        work(handler, CANCEL_DEV_IMG);
    }

    /**
     * 设备上传模板包数据到主机
     *
     * @param handler
     */
    public void upTemplPacHost(Handler handler, int templModelType) {
        this.handler = handler;
        this.templModelType = templModelType;
        work(handler, UP_TEMPL_PAC_HOST);
    }

    /**
     * 下载模板包到设备
     *
     * @param handler
     */
    public void downTemplPacDev(Handler handler, int templModelType) {
        this.handler = handler;
        this.templModelType = templModelType;
        work(handler, DOWN_TEMPL_PAC_DEV);
    }

    /**
     * 写入文件到主机，子线程中执行
     *
     * @param handler
     * @param templateData 要写入的模板数据
     * @param templateName 模板名字
     * @param index 当前要存储的模板的索引（相对于模板包的数量）
     */
    private byte[] templateData;
    private String templateName;
//    private int index;

    public void writeFileHost(Handler handler, byte[] templateData, String templateName, int index) {
        this.handler = handler;
        if (TextUtils.isEmpty(templateName) || templateData == null) {
            Message writeFileMsg = handler.obtainMessage();
            writeFileMsg.what = WRITE_FILE;
            writeFileMsg.arg1 = -9;
            handler.sendMessage(writeFileMsg);
            return;
        }
        this.templateData = templateData;
        this.templateName = templateName;
//        this.index = index;
        work(handler, WRITE_FILE);
    }

    /**
     * 读取数据,完毕===》》下载模板到设备
     */
    private void readFile(File datFile) {
        work(handler, READ_FILE);
    }

    //获取目标路径下所有文件列表
    public ArrayList<String> scanAimDirFileName(String path) {
        ArrayList<String> finerFileList = FileUtil.getInitFinerFileList(path);
        return finerFileList;
    }

    /**
     * 删除主机指定模板
     */
    public void deleteHostIdTempl(Handler handler, String templID) {
        if (TextUtils.isEmpty(templID)) {
            Message delTemplIdMsg = handler.obtainMessage();
            delTemplIdMsg.what = DELETE_HOST_ID_TEMPL;
            delTemplIdMsg.arg1 = -9;
            handler.sendMessage(delTemplIdMsg);
            return;
        }
        this.handler = handler;
        if (!templID.contains(".dat")) {
            this.templNameID = templID + ".dat";
        } else {
            this.templNameID = templID;
        }
        work(handler, DELETE_HOST_ID_TEMPL);
    }

    /**
     * 删除主机所有模板
     */
    public void deleteHostAllTempl(Handler handler) {
        this.handler = handler;
        work(handler, DELETE_HOST_ALL_TEMPL);
    }

    /**
     * 获取特征数量类型下对应路径下的文件名列表
     */
    public ArrayList<String> getAimFileList() {
        String aimPath = getAimPath();
        ArrayList<String> fileNameList = scanAimDirFileName(aimPath);
        return fileNameList;
//        this.handler = handler;
//        aimPath = getAimPath();
//        work(handler, GET_AIM_FILE_LIST);
    }

    public ArrayList<String> scanHostAimDir() {
        String aimPath = getAimPath();
        ArrayList<String> hostDatFileNameList = scanAimDirFileName(aimPath);
        return hostDatFileNameList;
    }

    private ArrayList<String> templIdList = null;

    public ArrayList<String> subID(int IdNum, byte[] templByte) {
        if (IdNum > 0) {
            if (templIdList == null) {
                templIdList = new ArrayList<>();
            }
            if (templIdList.size() > 0) {
                templIdList.clear();
            }
        }
        for (int i = 0; i < IdNum; i++) {
            int srcStart = i * 50;
            byte[] tem = new byte[50];
            System.arraycopy(templByte, srcStart, tem, 0, 50);
            for (int i1 = 0; i1 < tem.length; i1++) {
                byte b = tem[i1];
                if (b == 0) {
                    byte[] aimByte = new byte[i1];
                    System.arraycopy(tem, 0, aimByte, 0, i1);
                    try {
                        String aimDatID = new String(aimByte, "UTF-8");
                        if (!TextUtils.isEmpty(aimDatID.trim())) {
                            templIdList.add(aimDatID);
                            LogUtils.d("目标模板的名称：" + aimDatID);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        //获取设备可注册的最大模板数量
//        getDevTemplNum(handler, 1);
        return templIdList;
    }

    public void getHostTemplatePagSize(String datDirPath) {
        File datDir = new File(datDirPath);
        byte[] datFileData;
        byte[] templatePag;
        if (!datDir.exists())
            return;
        if (datDir.isDirectory()) {
            File[] datDirList = datDir.listFiles();
            if (datDirList.length <= 0)
                return;
            byte[] totalHostTemplate = null;
            if (templModelType == TEMPL_MODEL_3) {
                totalHostTemplate = new byte[PERFECT_FEATURE_17682 * datDirList.length];
            } else if (templModelType == TEMPL_MODEL_6) {
                totalHostTemplate = new byte[PERFECT_FEATURE_35058 * datDirList.length];
            }
            for (int i = 0; i < datDirList.length; i++) {
                File datFile = datDirList[i];
                //先获取主机模板的ID
                String datFileName = datFile.getName().trim();
                byte[] templateId = new byte[50];
                System.arraycopy(datFileName.getBytes(), 0, templateId, 0, datFileName.getBytes().length);
                datFileData = new byte[PERFECT_FEATURE_3];
                FileUtil.readFile(datFile, datFileData);
                templatePag = new byte[PERFECT_FEATURE_17682];
                System.arraycopy(templateId, 0, templatePag, 0, templateId.length);
                System.arraycopy(datFileData, 0, templatePag, templateId.length, datFileData.length);
                if (templModelType == TEMPL_MODEL_3) {
                    System.arraycopy(templatePag, 0, totalHostTemplate,
                            i * PERFECT_FEATURE_17682, templatePag.length);
                } else if (templModelType == TEMPL_MODEL_6) {
                    System.arraycopy(templatePag, 0, totalHostTemplate,
                            i * PERFECT_FEATURE_35058, templatePag.length);
                }
                if ((i + 1) == datDirList.length) {
                    int downDevTmplPkgRes = getTG661().TGDownDevTmplPkg(totalHostTemplate, totalHostTemplate.length);
                    Message downDevTemplPkgMsg = handler.obtainMessage();
                    downDevTemplPkgMsg.what = DOWN_TEMPL_PAC_DEV;
                    if (downDevTmplPkgRes == 0) downDevTmplPkgRes = 1;
                    downDevTemplPkgMsg.arg1 = downDevTmplPkgRes;
                    handler.sendMessage(downDevTemplPkgMsg);
                    showWaitDialog(-1, "");
                }
            }
        }
    }

    //获取设备中已注册模板的列表
    private void getDevTemplNameList(Handler handler, int devTemplNum) {
        Message templListMsg = handler.obtainMessage();
        Bundle tempListBundle = new Bundle();
        templListMsg.what = DEV_TEMPL_LIST;
        if (devTemplNum > 0) {
            IntByReference ibrTemplList = new IntByReference();
            byte[] templByte = new byte[50 * devTemplNum];
            int devTmplInfoRes = getTG661().TGGetDevTmplInfo(ibrTemplList, templByte);
            if (devTmplInfoRes < 0) {
                int loopTempList = 0;
                boolean loopDevTempls = true;
                while (loopDevTempls) {
                    devTmplInfoRes = getTG661().TGGetDevTmplInfo(ibrTemplList, templByte);
                    loopTempList++;
                    if (loopTempList >= 3 && devTmplInfoRes < 0) {
                        templListMsg.arg1 = -1;
                    }
                    if (devTmplInfoRes >= 0 || loopTempList >= 3) {
                        loopDevTempls = false;
                        loopTempList = 0;
                        ArrayList<String> templIdList = subID(devTemplNum, templByte);
                        templListMsg.arg1 = 1;
                        tempListBundle.putStringArrayList(TEMP_LIST, templIdList);
                        templListMsg.setData(tempListBundle);
                    }
                }
            } else {
                ArrayList<String> templIdList = subID(devTemplNum, templByte);
                templListMsg.arg1 = 1;
                tempListBundle.putStringArrayList(TEMP_LIST, templIdList);
                templListMsg.setData(tempListBundle);
            }
        } else {
            //设备中得模板数为0
            templListMsg.arg1 = 2;
        }
        Log.d("===LOG", "   已注册的模板列表中的数量：" + devTemplNum);
        handler.sendMessage(templListMsg);
    }

    private void showWaitDialog(int type, String tip) {
        Message dialogMsg = handler.obtainMessage();
        dialogMsg.what = WAIT_DIALOG;
        dialogMsg.arg1 = type;
        dialogMsg.obj = tip;
        handler.sendMessage(dialogMsg);
    }

    //检查权限
    public void checkPermissions(int type) {
        for (String perm : perms) {
            int checkResult = ContextCompat.checkSelfPermission(mActivity, perm);
            if (checkResult == PackageManager.PERMISSION_DENIED) {
                //权限没有同意，需要申请该权限
                Intent intent = new Intent("com.tg.m661j.vein.api");
                intent.addCategory("com.tg.m661j.vein.api");
                intent.putExtra("type", type);
                intent.putExtra("workModel", "f");
                mActivity.startActivity(intent);
                break;
            }
        }
        if (type == 1) {
            saveTemplHost();
        }
    }

    private byte[] upTemplData;
    private int devTmplNumUpRes;

    //存储模板到主机
    public void saveTemplHost() {
        if (upTemplData != null) {
            subAndWriteSave(upTemplData, devTmplNumUpRes);
        } else {
            LogUtils.d("====>>>   upTemplData  为  null ");
        }
    }

    private int saveTemplateNum;//要上传到主机存储的模板的数量

    private void subAndWriteSave(byte[] upTemplData, int devTmplNum) {
        int size = 0;
        int sizeTruth = 0;
        if (templModelType == TEMPL_MODEL_3) {
            size = PERFECT_FEATURE_17682;
            sizeTruth = PERFECT_FEATURE_3;
        } else if (templModelType == TEMPL_MODEL_6) {
            size = PERFECT_FEATURE_35058;
            sizeTruth = PERFECT_FEATURE_6;
        }
        this.saveTemplateNum = devTmplNum;
        for (int i = 0; i < devTmplNum; i++) {
            int n = i * size;
            byte[] data = new byte[size];
            System.arraycopy(upTemplData, n, data, 0, size);
            try {
                byte[] templName = new byte[50];
                byte[] templDataEntity = new byte[sizeTruth];
                System.arraycopy(data, 0, templName, 0, 50);
                System.arraycopy(data, 50, templDataEntity, 0, sizeTruth);
                //截取名字
                for (int i1 = 0; i1 < templName.length; i1++) {
                    byte b = templName[i1];
                    if (b == 0) {
                        byte[] datTemplName = new byte[i1];
                        System.arraycopy(templName, 0, datTemplName, 0, i1);
                        String datTmlName = new String(datTemplName, "UTF-8");
                        LogUtils.d("文件的名字：" + datTmlName);
                        writeFileHost(handler, templDataEntity, datTmlName, i);
                    }
                }
                //写入主机文件夹
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 因为平板亮屏幕和熄屏会给设备上电和断电，频繁的上电和断电容易损伤设备
     * 因此，多出这两个方法
     * 保持屏幕常量
     * 在onResume方法中调用
     */
    public void keepScreenLight(Activity activity) {
        if (activity != null) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 取消屏幕常亮
     * 在onPause方法中调用
     */
    public void clearScreenLight(Activity activity) {
        if (activity != null) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 更新主机中的模板
     */
    private byte[] updateHostTempl;

    public void updateHostTempl(byte[] updateHostTempl, Handler handler, String templName) {
        this.handler = handler;
        this.updateHostTempl = updateHostTempl;
        this.templNameID = templName;
//        this.hostIndex = hostIndex;
        work(handler, UPDATE_HOST_TEMPL);
    }

    private int workType = WORK_FRONT;//默认是前比

    private void work(final Handler handler, final int flag) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                switch (flag) {
                    case OPEN_DEV:
                        showWaitDialog(1, "正在打开设备...");
                        /**
                         * 打开设备：默认前比3特征模板的工作模式,不支持连续验证
                         * 1：后比工作模式  0：前比工作模式
                         */
                        IntByReference mode = new IntByReference();
                        int openDevRes = getTG661().TGOpenDev(mode);
                        Message openDevMsg = handler.obtainMessage();
                        openDevMsg.what = OPEN_DEV;
                        if (openDevRes >= 0) {
                            //取消连续验证
                            cancelVerify(handler);
                            //设置工作模式
                            setDevWorkModel(handler, workType, templModelType);
                            //初始化设置设备的音量 4
                            if (workType == WORK_FRONT) {
                                getTG661().TGPlayDevVoice(VOICE_VOLUME4);
                                CurrentDevVoiceValue = VOICE_VOLUME4;
                                currentVoice = "4";
                            }
                            devOpen = true;
                            devClose = false;
                            //发送打开设备的结果:
                            openDevMsg.arg1 = 1;
                            //启动后台devService
//                            if (!isStart) {
//                                startDevService();
//                            }
                        } else {
                            openDevMsg.arg1 = -1;
                        }
                        handler.sendMessage(openDevMsg);
                        showWaitDialog(-1, "");
                        break;
                    case CLOSE_DEV:
                        //关闭设备
//                        showWaitDialog(1, "正在关闭设备");
                        int closeDevRes = getTG661().TGCloseDev();
                        Message closeDevMsg = handler.obtainMessage();
                        closeDevMsg.what = CLOSE_DEV;
                        devClose = closeDevRes == SUCCESS_FLAG;
                        devOpen = false;
                        if (closeDevRes == 0) {
                            //传出关闭成功的结果：
                            devStatus = -1;
                            closeDevMsg.arg1 = 1;
                        } else {
                            closeDevMsg.arg1 = -1;
                        }
                        handler.sendMessage(closeDevMsg);
//                        showWaitDialog(-1, "");

                        break;
                    case SET_DEV_MODEL:
                        int setDevModeRes1 = -5;
                        if (templModelType == TEMPL_MODEL_3) {
                            setDevModeRes1 = getTG661().TGSetDevMode(0);
                        } else if (templModelType == TEMPL_MODEL_6) {
                            setDevModeRes1 = getTG661().TGSetDevMode(2);
                        }
                        if (setDevModeRes1 == -1) {
                            int n = 0;
                            boolean loo = true;
                            while (loo) {
                                if (templModelType == TEMPL_MODEL_3) {
                                    setDevModeRes1 = getTG661().TGSetDevMode(0);
                                } else if (templModelType == TEMPL_MODEL_6) {
                                    setDevModeRes1 = getTG661().TGSetDevMode(2);
                                }
                                n++;
                                if (setDevModeRes1 == -1 && n < 3) {
                                    loo = true;
                                } else {
                                    n = 0;
                                    loo = false;
                                }
                            }
                        }
                        Message devModelMsg = handler.obtainMessage();
                        devModelMsg.what = SET_DEV_MODEL;
                        if (setDevModeRes1 == 0) {
                            devModelMsg.arg1 = 1;
                        } else if (setDevModeRes1 == 2) {
                            devModelMsg.arg1 = 2;
                        } else if (setDevModeRes1 == 3) {
                            devModelMsg.arg1 = 3;
                        } else if (setDevModeRes1 == 4) {
                            devModelMsg.arg1 = 4;
                        } else if (setDevModeRes1 == -1) {
                            devModelMsg.arg1 = -1;
                        } else if (setDevModeRes1 == -2) {
                            devModelMsg.arg1 = -2;
                        }
                        handler.sendMessage(devModelMsg);
                        break;
                    case DEV_FW:
                        //获取设备固件号
                        byte[] fwByte = new byte[16];
                        int devFWres = getTG661().TGGetDevFW(fwByte);
                        Message devFWMsg = handler.obtainMessage();
                        devFWMsg.what = DEV_FW;
                        if (devFWres < 0) {
                            boolean lop = true;
                            int lo = 0;
                            while (lop) {
                                devFWres = getTG661().TGGetDevFW(fwByte);
                                lo++;
                                if (devFWres >= 0 || lo >= 3) {
                                    lo = 0;
                                    lop = false;
                                }
                            }
                            if (devFWres < 0) {
                                devFWMsg.arg1 = -1;
                            } else {
                                try {
                                    String fwStr = new String(fwByte, "UTF-8");
                                    //传出设备固件号：
                                    devFWMsg.arg1 = 1;
                                    devFWMsg.obj = fwStr;
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                String fwStr = new String(fwByte, "UTF-8");
                                //传出设备固件号：
                                devFWMsg.arg1 = 1;
                                devFWMsg.obj = fwStr;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendMessage(devFWMsg);
                        break;
                    case DEV_SN:
                        //获取设备序列号：
                        byte[] snByte = new byte[16];
                        int devSNres = getTG661().TGGetDevSN(snByte);
                        Message devSNMsg = handler.obtainMessage();
                        devSNMsg.what = DEV_SN;
                        if (devSNres < 0) {
                            boolean lop = true;
                            int lo = 0;
                            while (lop) {
                                devSNres = getTG661().TGGetDevSN(snByte);
                                lo++;
                                if (devSNres >= 0 || lo >= 3) {
                                    lo = 0;
                                    lop = false;
                                }
                            }
                            if (devSNres < 0) {
                                devSNMsg.arg1 = -1;
                            } else {
                                try {
                                    String snStr = new String(snByte, "UTF-8");
                                    //传出设备固件号：
                                    devSNMsg.arg1 = 1;
                                    devSNMsg.obj = snStr;
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                String snStr = new String(snByte, "UTF-8");
                                //传出状态码和结果：
                                devSNMsg.arg1 = 1;
                                devSNMsg.obj = snStr;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendMessage(devSNMsg);
                        break;
                    case DEV_STATUS:
                        //获取设备的链接状态
                        devStatus = getTG661().TGGetDevStatus();
                        //传递出设备的链接状态
                        Message devStatusMsg = handler.obtainMessage();
                        devStatusMsg.what = DEV_STATUS;
                        if (devStatus >= 0) {
                            devStatusMsg.arg1 = 1;
                        } else {
                            devStatusMsg.arg1 = -1;
                        }
                        handler.sendMessage(devStatusMsg);
                        break;
                    case DEV_WORK_MODEL:
                        //获取设备的工作模式
                        IntByReference ibr = new IntByReference();
                        int devWorkModelRes = getTG661().TGGetDevMode(ibr);
                        Message devWorkMsg = handler.obtainMessage();
                        devWorkMsg.what = DEV_WORK_MODEL;
                        if (devWorkModelRes < 0) {
                            devWorkMsg.arg1 = -1;
                        } else {
                            type = 0;
                            if (ibr.getValue() == 0) {
                                devWorkMsg.arg1 = 1;
                            } else if (ibr.getValue() == 1) {
                                devWorkMsg.arg1 = 2;
                            } else if (ibr.getValue() == 2) {
                                devWorkMsg.arg1 = 3;
                            }
                        }
                        handler.sendMessage(devWorkMsg);
                        break;
                    case DEV_TEMPL_CLEAR:
                        //清除设备中的所有模板:
                        int clearDevTemplRes = getTG661().TGEmptyDevTmpl();
                        //发送出清除设备中所有模板的结果
                        Message clearDevTmplMsg = handler.obtainMessage();
                        clearDevTmplMsg.what = DEV_TEMPL_CLEAR;
                        if (clearDevTemplRes == 0) {
                            clearDevTmplMsg.arg1 = 1;
                        } else if (clearDevTemplRes == 1) {
                            clearDevTmplMsg.arg1 = 2;
                        } else if (clearDevTemplRes == -1) {
                            clearDevTmplMsg.arg1 = -1;
                        }
                        handler.sendMessage(clearDevTmplMsg);
                        break;
                    case DEV_PLAY_VOICE:
                        //设备播放声音资源
                        int devVoiceRes1 = getTG661().TGPlayDevVoice(voiceRes);
                        Message devVoiceMsg1 = handler.obtainMessage();
                        devVoiceMsg1.what = DEV_PLAY_VOICE;
                        if (devVoiceRes1 == 0) {
                            devVoiceMsg1.arg1 = 1;
                        } else {
                            devVoiceMsg1.arg1 = -1;
                        }
                        handler.sendMessage(devVoiceMsg1);
                        break;
                    case DEV_VOICE:
                        //调节设备音量
                        int devVoiceRes = getTG661().TGPlayDevVoice(TG661JFrontAPI.this.devVoice);
                        Message devVoiceMsg = handler.obtainMessage();
                        devVoiceMsg.what = DEV_VOICE;
                        if (devVoiceRes == 0) {
                            if (TG661JFrontAPI.this.devVoice == VOICE_VOLUME0) {
                                currentVoice = "0";
                            } else if (TG661JFrontAPI.this.devVoice == VOICE_VOLUME1) {
                                currentVoice = "1";
                            } else if (TG661JFrontAPI.this.devVoice == VOICE_VOLUME2) {
                                currentVoice = "2";
                            } else if (TG661JFrontAPI.this.devVoice == VOICE_VOLUME3) {
                                currentVoice = "3";
                            } else if (TG661JFrontAPI.this.devVoice == VOICE_VOLUME4) {
                                currentVoice = "4";
                            } else if (TG661JFrontAPI.this.devVoice == VOICE_VOLUME5) {
                                currentVoice = "5";
                            } else if (TG661JFrontAPI.this.devVoice == VOICE_VOLUME6) {
                                currentVoice = "6";
                            } else if (TG661JFrontAPI.this.devVoice == VOICE_VOLUME7) {
                                currentVoice = "7";
                            }
                            CurrentDevVoiceValue = TG661JFrontAPI.this.devVoice;
                            devVoiceMsg.arg1 = 1;
                            devVoiceMsg.obj = currentVoice;
                        } else {
                            devVoiceMsg.arg1 = -1;
                        }
                        handler.sendMessage(devVoiceMsg);
                        break;
                    case DEV_REGISTER:
                        //用户注册
                        byte[] userIDByte = new byte[49];
                        System.arraycopy(templNameID.getBytes(), 0, userIDByte,
                                0, templNameID.getBytes().length);
                        int devRegFingerRes = getTG661().TGDevRegFinger(0, userIDByte);
                        Message registerMsg = handler.obtainMessage();
                        registerMsg.what = DEV_REGISTER;
                        boolean loopReg = true;
                        byte[] userTempId = new byte[49];
                        if (devRegFingerRes >= 0) {
                            while (loopReg) {
                                int identReturnRes = getTG661().TGGetDevRegIdentReturn(userTempId,
                                        GET_IMG_OUT_TIME_5000);
                                if (identReturnRes == 2) {
                                    //登记成功 发送注册的结果
                                    loopReg = false;
                                    registerMsg.arg1 = 1;
                                } else if (identReturnRes == 3) {
                                    //登记成功 发送注册的结果
                                    loopReg = false;
                                    registerMsg.arg1 = 2;
                                }
                            }
                        } else {
                            loopReg = false;
                            registerMsg.arg1 = -1;
                        }
                        handler.sendMessage(registerMsg);
                        break;
                    case CONTINUE_VERIFY:
                        //用户连续验证
                        int continueIdentFingerRes = getTG661().TGDevContinueIdentFinger(retImgType);
                        Message continueVerifyMsg = handler.obtainMessage();
                        continueVerifyMsg.what = CONTINUE_VERIFY;
                        if (continueIdentFingerRes >= 0) {
                            continueVerifyMsg.arg1 = 1;
                        } else {
                            continueVerifyMsg.arg1 = -1;
                        }
                        handler.sendMessage(continueVerifyMsg);
                        break;
                    case DEV_VERIFY_TEMPL:
                        //用户单次验证  1：N验证
                        int devIdentFingerRes = getTG661().TGDevIdentFinger(0);
                        Message devSingleVerifyMsg = handler.obtainMessage();
                        devSingleVerifyMsg.what = DEV_VERIFY_TEMPL;
                        if (devIdentFingerRes < 0) {
                            devSingleVerifyMsg.arg1 = -1;
                        } else if (devIdentFingerRes == 0) {
                            boolean tgCjangeIdent = true;
                            byte[] userId = new byte[49];
                            while (tgCjangeIdent) {
                                int identReturnRes = getTG661().TGGetDevRegIdentReturn(userId,
                                        GET_IMG_OUT_TIME_5000);
                                Log.d("===LOG", "    1：N验证的结果：" + identReturnRes);
                                if (identReturnRes == VOICE_IDENT_SUCCESS) {
                                    devSingleVerifyMsg.arg1 = 1;
                                    tgCjangeIdent = false;
                                    break;
                                } else if (identReturnRes == VOICE_IDENT_FAIL) {
                                    devSingleVerifyMsg.arg1 = 2;
                                    tgCjangeIdent = false;
                                    break;
                                } else if (identReturnRes == -2) {
                                    devSingleVerifyMsg.arg1 = -2;
                                    tgCjangeIdent = false;
                                    break;
                                }
                            }
                        }
                        handler.sendMessage(devSingleVerifyMsg);
                        break;
                    case DEV_VERIFY1_1:
                        //1：1验证
                        byte[] userID = new byte[49];
                        System.arraycopy(templNameID.getBytes(), 0, userID,
                                0, templNameID.getBytes().length);
                        int tgChangeIdentModeRes = getTG661().TGChangeIdentMode(retImgType, userID);
                        Log.d("===LOG", "   调用1：1验证接口：" + tgChangeIdentModeRes);
                        Message verifyMsg1_1 = handler.obtainMessage();
                        verifyMsg1_1.what = DEV_VERIFY1_1;
                        if (tgChangeIdentModeRes == 0) {
                            boolean tgCjangeIdent = true;
                            byte[] userTempID = new byte[49];
                            while (tgCjangeIdent) {
                                int identReturnRes = getTG661().TGGetDevRegIdentReturn(userTempID,
                                        GET_IMG_OUT_TIME_5000);
                                Log.d("===LOG", "    1：1验证的结果：" + identReturnRes);
                                if (identReturnRes == VOICE_IDENT_SUCCESS) {
                                    verifyMsg1_1.arg1 = 1;
                                    tgCjangeIdent = false;
                                    break;
                                } else if (identReturnRes == VOICE_IDENT_FAIL) {
                                    verifyMsg1_1.arg1 = 2;
                                    tgCjangeIdent = false;
                                    break;
                                } else if (identReturnRes == -2) {
                                    verifyMsg1_1.arg1 = -2;
                                    tgCjangeIdent = false;
                                    break;
                                }
                            }
                        } else if (tgChangeIdentModeRes == -2) {
                            verifyMsg1_1.arg1 = 3;
                        } else {
                            verifyMsg1_1.arg1 = -1;
                        }
                        handler.sendMessage(verifyMsg1_1);
                        break;
                    case CANCEL_VERIFY:
                        //取消验证或者注册
                        int cancelDevRegIdentRes = getTG661().TGCancelDevRegIdent();
                        Message cancelMsg = handler.obtainMessage();
                        cancelMsg.what = CANCEL_VERIFY;
                        if (cancelDevRegIdentRes == 0) {
                            cancelMsg.arg1 = 1;
                        } else {
                            cancelMsg.arg1 = -1;
                        }
                        handler.sendMessage(cancelMsg);
                        break;
                    case WRITE_DEV_INFO:
                        int tgWriteDevInfoRes = getTG661().TGWriteDevInfo(devInfo, devInfo.length);
                        Message writeDevMsg = handler.obtainMessage();
                        writeDevMsg.what = WRITE_DEV_INFO;
                        if (tgWriteDevInfoRes == 0) {
                            writeDevMsg.arg1 = 1;
                        } else if (tgWriteDevInfoRes == -1) {
                            writeDevMsg.arg1 = -1;
                        } else if (tgWriteDevInfoRes == -2) {
                            writeDevMsg.arg1 = -2;
                        }
                        handler.sendMessage(writeDevMsg);
                        break;
                    case READ_DEV_INFO:
                        //获取设备信息
                        byte[] DevInfo = new byte[1024];
                        int DevInfoLength = 1024;
                        int tgReadDevInfoRes = getTG661().TGReadDevInfo(DevInfo, DevInfoLength);
                        Message readMsg = handler.obtainMessage();
                        readMsg.what = READ_DEV_INFO;
                        if (tgReadDevInfoRes >= 0) {
                            try {
                                String info = new String(DevInfo, "UTF-8");
                                readMsg.arg1 = 1;
                                readMsg.obj = info;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else if (tgReadDevInfoRes == -1) {
                            readMsg.arg1 = -1;
                        } else if (tgReadDevInfoRes == -2) {
                            readMsg.arg1 = -2;
                        }
                        handler.sendMessage(readMsg);
                        break;
                    case UP_TEMPL_HOST:
                        //上传单个模板到主机
                        showWaitDialog(1, "正在上传模板到主机");
                        byte[] templData = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            templData = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            templData = new byte[PERFECT_FEATURE_6];
                        }
                        IntByReference inBr = new IntByReference();
                        byte[] templIdData = new byte[49];
                        System.arraycopy(templId, 0, templIdData, 0, templId.length);
                        int upDevTmplRes = getTG661().TGUpDevTmpl(templIdData, templData, inBr);
                        Message upTemplMsg = handler.obtainMessage();
                        upTemplMsg.what = UP_TEMPL_HOST;
                        if (upDevTmplRes < 0) {
                            upTemplMsg.arg1 = -1;
                        } else if (upDevTmplRes == 0) {
                            //成功之后，写入模板数据到主机文件
                            boolean writeTemplHost = writeTemplHost(templData, templateName);
                            if (writeTemplHost) {
                                //上传主机成功，写入主机成功
                                upTemplMsg.arg1 = 1;
                                upTemplMsg.obj = true;
                            } else {
                                //上传主机成功，写入失败
                                upTemplMsg.arg1 = 2;
                            }
                        } else if (upDevTmplRes == 1) {
                            //设备中不存在待上传的模板
                            upTemplMsg.arg1 = 3;
                        }
                        handler.sendMessage(upTemplMsg);
                        showWaitDialog(-1, "");
                        break;
                    case DOWN_TEMPL_DEV:
                        //下载单个模板到设备
                        showWaitDialog(1, "正在下载模板到设备");
                        String aimPath = getAimPath();
                        aimPath = aimPath + File.separator + templNameID;
                        byte[] aimDatByte = null;
                        int dataSize = 0;
                        if (templModelType == TEMPL_MODEL_3) {
                            dataSize = PERFECT_FEATURE_3;
                            aimDatByte = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            dataSize = PERFECT_FEATURE_6;
                            aimDatByte = new byte[PERFECT_FEATURE_6];
                        }
                        byte[] aimDat = FileUtil.readFile(aimPath, aimDatByte);
                        String fileId = templNameID.substring(0, templNameID.indexOf(".dat"));
                        byte[] userIDBytes = new byte[49];
                        System.arraycopy(fileId.getBytes(), 0, userIDBytes, 0, fileId.getBytes().length);
                        int downDevTmplRes = getTG661().TGDownDevTmpl(userIDBytes, aimDat, dataSize);
                        Message downTemplMsg = handler.obtainMessage();
                        downTemplMsg.what = DOWN_TEMPL_DEV;
                        if (downDevTmplRes == 0) {
                            downTemplMsg.arg1 = 1;
                        } else if (downDevTmplRes == -1) {
                            downTemplMsg.arg1 = -1;
                        } else if (downDevTmplRes == -2) {
                            downTemplMsg.arg1 = -2;
                        } else if (downDevTmplRes == -3) {
                            downTemplMsg.arg1 = -3;
                        }
                        handler.sendMessage(downTemplMsg);
                        showWaitDialog(-1, "");
                        break;
                    case UP_TEMPL_PAC_HOST:
                        //上传模板包到主机,先获取设备中模板得数量，然后分配内存大小
                        showWaitDialog(1, "正在上传模板包到主机");
                        type = 0;
                        devTmplNumUpRes = getTG661().TGGetDevTmplNum(type);
                        Message templNumMsg = handler.obtainMessage();
                        templNumMsg.what = UP_TEMPL_PAC_HOST;
                        int nUp = 0;
                        boolean loopTemplUpNum = true;
                        if (devTmplNumUpRes < 0) {
                            while (loopTemplUpNum) {
                                devTmplNumUpRes = getTG661().TGGetDevTmplNum(type);
                                nUp++;
                                if (devTmplNumUpRes >= 0) {
                                    loopTemplUpNum = false;
                                    if (templModelType == TEMPL_MODEL_3) {
                                        upTemplData = new byte[PERFECT_FEATURE_17682 * devTmplNumUpRes];
                                    } else if (templModelType == TEMPL_MODEL_6) {
                                        upTemplData = new byte[PERFECT_FEATURE_35058 * devTmplNumUpRes];
                                    }
                                    IntByReference ibrfUp = new IntByReference();
                                    int upDevTmplPkgRes = getTG661().TGUpDevTmplPkg(upTemplData, ibrfUp);
                                    if (upDevTmplPkgRes == 0) {
                                        checkPermissions(1);
                                        templNumMsg.arg1 = 1;
                                    } else if (upDevTmplPkgRes == 1) {
                                        //设备中不存在模板
                                        templNumMsg.arg1 = 2;
                                    } else if (upDevTmplPkgRes == -1) {
                                        //设备上传模板包超时
                                        templNumMsg.arg1 = 3;
                                    }
                                }
                                if (nUp >= 3 && devTmplNumUpRes < 0) {
                                    //获取模板数量超时
                                    templNumMsg.arg1 = -1;
                                    loopTemplUpNum = false;
                                    nUp = 0;
                                }
                            }
                        } else {
                            //设备中已注册的模板的数量
                            //模板数据17632+模板名字50
                            if (templModelType == TEMPL_MODEL_3) {
                                upTemplData = new byte[PERFECT_FEATURE_17682 * devTmplNumUpRes];
                            } else if (templModelType == TEMPL_MODEL_6) {
                                upTemplData = new byte[PERFECT_FEATURE_35058 * devTmplNumUpRes];
                            }
                            IntByReference ibrfUp = new IntByReference();
                            int upDevTmplPkgRes = getTG661().TGUpDevTmplPkg(upTemplData, ibrfUp);
                            if (upDevTmplPkgRes == 0) {
                                checkPermissions(1);
                                templNumMsg.arg1 = 1;
                            } else if (upDevTmplPkgRes == 1) {
                                //设备中不存在模板
                                templNumMsg.arg1 = 2;
                            } else if (upDevTmplPkgRes == -1) {
                                //设备上传模板包超时
                                templNumMsg.arg1 = 3;
                            }
                        }
                        handler.sendMessage(templNumMsg);
                        showWaitDialog(-1, "");
                        break;
                    case DOWN_TEMPL_PAC_DEV:
                        //下载模板包到设备
                        showWaitDialog(1, "正在下载模板包到设备");
                        String templPath = "";
                        if (templModelType == TEMPL_MODEL_3) {
                            templPath = frontTempl3Path;
                        } else if (templModelType == TEMPL_MODEL_6) {
                            templPath = frontTempl6Path;
                        }
                        getHostTemplatePagSize(templPath);
                        break;
                    case DEV_TEMPL_NUM:
                        //获取模板的数量
                        int devTmplNumRes = getTG661().TGGetDevTmplNum(type);
                        Message templNumMsg1 = handler.obtainMessage();
                        templNumMsg1.what = DEV_TEMPL_NUM;
                        int n = 0;
                        boolean loopTemplNum = true;
                        if (devTmplNumRes < 0) {
                            while (loopTemplNum) {
                                devTmplNumRes = getTG661().TGGetDevTmplNum(type);
                                n++;
                                if (devTmplNumRes >= 0) {
                                    if (type == 0 && devTmplNumRes >= 0) {
                                        DevTemplNum = devTmplNumRes;
                                        getDevTemplList(handler);
                                    }
                                    templNumMsg1.arg1 = devTmplNumRes;
                                    templNumMsg1.obj = type;
                                    loopTemplNum = false;
                                }
                                if (n >= 3 && devTmplNumRes < 0) {
                                    templNumMsg1.arg1 = -1;
                                    loopTemplNum = false;
                                    n = 0;
                                }
                            }
                        } else {
                            if (type == 0 && devTmplNumRes >= 0) {
                                Log.d("===OOO", "  设备中已注册模板的数量：" + devTmplNumRes);
                                DevTemplNum = devTmplNumRes;
                                getDevTemplList(handler);
                            }
                            templNumMsg1.arg1 = devTmplNumRes;
                            templNumMsg1.obj = type;
                        }
                        handler.sendMessage(templNumMsg1);
                        break;
                    case DEV_TEMPL_LIST:
                        //先获取设备中已经注册的模板的数量
                        getDevTemplNameList(handler, DevTemplNum);
                        break;
                    case DEV_DEL_ID_TEMPL:
                        //删除设备的单个模板
                        byte[] delTemplId = new byte[49];
                        System.arraycopy(templId, 0, delTemplId, 0, templId.length);
                        int devTmplRes = getTG661().TGDelDevTmpl(delTemplId);
                        Log.d("===LOG", "   设备删除模板  结果码：" + devTmplRes);
                        Message delTemplMsg = handler.obtainMessage();
                        delTemplMsg.what = DEV_DEL_ID_TEMPL;
                        if (devTmplRes == 0) {
                            //获取设备中已注册的列表数量，更新UI
//                            type = 0;
//                            getDevTemplNum(handler, type);
//                            getDevRegisterTempl();
                            delTemplMsg.arg1 = 1;
                        } else if (devTmplRes == 1) {
                            delTemplMsg.arg1 = 2;
                        } else if (devTmplRes == -1) {
                            delTemplMsg.arg1 = -1;
                        }
                        handler.sendMessage(delTemplMsg);
                        break;
                    case WRITE_FILE:
                        boolean writeTemplHost = writeTemplHost(templateData, templateName);
//                        Message upTemplPacMsg = handler.obtainMessage();
//                        upTemplPacMsg.what = UP_TEMPL_PAC_HOST;
//                        if (writeTemplHost) {
//                            if (index == (saveTemplateNum - 1)) {
//                                upTemplPacMsg.arg1 = 4;
//                                handler.sendMessage(upTemplPacMsg);
//                            }
//                        }
                        break;
                    case DEV_IMG_REGISTER:
                        getAP().play_inputDownGently();
                        byte[] imgData = new byte[IMG_SIZE];
//                        byte[] imgData = new byte[IMG_SIZE + T_SIZE];
//                        imgData[0] = ((byte) 0xfe);
                        int tgGetDevImageRes = getTG661().TGGetDevImage(imgData, GET_IMG_OUT_TIME);
                        Message imgMsg = handler.obtainMessage();
                        imgMsg.what = DEV_IMG_REGISTER;
                        if (tgGetDevImageRes >= 0) {
                            imgMsg.arg1 = 1;
                            imgMsg.arg2 = tgGetDevImageRes;
                            imgMsg.obj = imgData;
                        } else if (tgGetDevImageRes == -1) {
                            imgMsg.arg1 = -1;
                        } else if (tgGetDevImageRes == -2) {
                            imgMsg.arg1 = -2;
                        } else if (tgGetDevImageRes == -3) {
                            imgMsg.arg1 = -3;
                        } else if (tgGetDevImageRes == -4) {
                            imgMsg.arg1 = -4;
                        }
                        handler.sendMessage(imgMsg);
                        break;
                    case CANCEL_DEV_IMG:
                        int tgCancelGetImageRes = getTG661().TGCancelGetImage();
                        Message cancelImgMsg = handler.obtainMessage();
                        cancelImgMsg.what = CANCEL_DEV_IMG;
                        if (tgCancelGetImageRes == 0) {
                            cancelImgMsg.arg1 = 1;
                        } else {
                            cancelImgMsg.arg1 = -1;
                        }
                        handler.sendMessage(cancelImgMsg);
                        break;
                    case DELETE_HOST_ID_TEMPL:
                        Message removeMsg = handler.obtainMessage();
                        removeMsg.what = DELETE_HOST_ID_TEMPL;
                        String path = getAimPath();
                        boolean removeFile = FileUtil.removeFile(path, templNameID);
                        if (removeFile) {
                            removeMsg.arg1 = 1;
                        } else {
                            removeMsg.arg1 = -1;
                        }
                        handler.sendMessage(removeMsg);
                        break;
                    case DELETE_HOST_ALL_TEMPL:
                        Message deleteAllMsg = handler.obtainMessage();
                        deleteAllMsg.what = DELETE_HOST_ALL_TEMPL;
                        String deletePath = getAimPath();
                        boolean removeAllFile = FileUtil.removeAllFile(deletePath);
                        if (removeAllFile) {
                            deleteAllMsg.arg1 = 1;
                        } else {
                            deleteAllMsg.arg1 = -1;
                        }
                        handler.sendMessage(deleteAllMsg);
                        break;
                    case UPDATE_HOST_TEMPL:
                        if (updateHostTempl != null) {
                            String updateFilePath = getAimPath();
                            boolean updateFile = FileUtil.updateFile(updateFilePath,
                                    templNameID, updateHostTempl);
                            Message updateHostMsg = handler.obtainMessage();
                            updateHostMsg.what = UPDATE_HOST_TEMPL;
                            updateHostMsg.obj = updateFile;
                            handler.sendMessage(updateHostMsg);
                        }
                        break;
                }
            }
        });
        thread.start();
    }

    //解绑service
    public void unbindDevService(Context context){
        context.unbindService(serviceConnection);
    }

    //启动devService
    private void startDevService(Context context) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setAction(DevServiceAction);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            PackageManager packageManager = context.getPackageManager();
            ResolveInfo resolveInfo = packageManager.resolveService(intent, 0);
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                //获取service的包名
                String packageName = serviceInfo.packageName;
                //获取service的类名
                String name = serviceInfo.name;
                //构建一个ComponentName，将隐式intent变成一个显示intent，
                // 因为Android5.0后不允许隐式启动service
                ComponentName componentName = new ComponentName(packageName, name);
                intent.setComponent(componentName);
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Messenger tg661JMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RECEIVE_MESSAGE_CODE) {
                int devServiceArg = msg.arg1;
                Message tg661JMsg = handler.obtainMessage();
                tg661JMsg.what = DEV_STATUS;
                if (devServiceArg == 0) {
                    tg661JMsg.arg1 = 1;
                } else if (devServiceArg == -2) {
                    tg661JMsg.arg1 = -2;
                }
                handler.sendMessage(tg661JMsg);
            }
        }
    });

    private static final int RECEIVE_MESSAGE_CODE = 0x0002;
    private static final int SEND_MESSAGE_CODE = 0x0001;
    private Messenger devServiceMessenger = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            devServiceMessenger = new Messenger(iBinder);
            isStart = true;
            if (devOpen) {
                //如果设备开启
                Message tg661JMessage = Message.obtain();
                tg661JMessage.what = SEND_MESSAGE_CODE;
                tg661JMessage.obj = getTG661();
                tg661JMessage.replyTo = tg661JMessenger;
                try {
                    devServiceMessenger.send(tg661JMessage);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            devServiceMessenger = null;
            isStart = false;
        }
    };


}
