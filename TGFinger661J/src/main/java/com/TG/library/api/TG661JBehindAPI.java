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

import com.TG.library.CallBack.Common;
import com.TG.library.pojos.MatchN;
import com.TG.library.service.GetFileTask;
import com.TG.library.utils.AudioProvider;
import com.TG.library.utils.FileUtil;
import com.TG.library.utils.LogUtils;
import com.TG.library.utils.RegularUtil;
import com.example.mylibrary.R;
import com.sun.jna.ptr.IntByReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created By pq
 * on 2019/4/29
 * TG661J后比API
 */
public class TG661JBehindAPI {

    public static final int SUCCESS_FLAG = 0;
    public static final int OPEN_DEV = 0xf2;
    public static final int CLOSE_DEV = 0xf3;
    public static final int DEV_WORK_MODEL = 0xf8;
    public static final int WAIT_DIALOG = 0xf16;
    public static final int WRITE_FILE = 0xf20;
    public static final int READ_FILE = 0xf21;
    public static final int CANCEL_VERIFY = 0xf22;
    public static final int CANCEL_REGISTER = 0xf27;
    public static final int DEV_STATUS = 0xf28;
    public static final int DEV_IMG_REGISTER = 0xf29;
    public static final int CANCEL_DEV_IMG = 0xf30;
    public static final int INIT_FV = 0xf31;
    public static final int EXTRACT_FEATURE_REGISTER = 0xf32;
    public static final int EXTRACT_FEATURE_VERIFY = 0xf33;
    public static final int FEATURE_FUSION = 0xf34;
    public static final int RESOLVE_COMPARE_TEMPL = 0xf35;
    public static final int FEATURE_COMPARE1_1 = 0xf36;
    public static final int FEATURE_COMPARE1_N = 0xf37;
    public static final int TEMPL_FV_VERSION = 0xf38;
    public static final int TEMPL_SN = 0xf39;
    public static final int TEMPL_FW = 0xf40;
    public static final int TEMPL_TIME = 0xf41;
    public static final int WRITE_LICENSE = 0xf44;
    public static final int DELETE_HOST_ID_TEMPL = 0xf45;//删除主机指定模板
    public static final int DELETE_HOST_ALL_TEMPL = 0xf46;//删除主机所有模板
    public static final int UPDATE_HOST_TEMPL = 0xf47;//更新主机中的模板
    public static final int SET_DEV_MODEL = 0xf48;//设置设备模式

    public static final String COMPARE_N_TEMPL = "update_templ";//1:N验证的模板
    public static final String COMPARE_N_INDEX = "n_index";//模板的索引
    public static final String COMPARE_N_SCORE = "n_score";//1:N验证的分数
    public static final String COMPARE_NAME = "templ_name";//1:N验证的分数
    public static final String TEMP_LIST = "templ_list";//模板列表

    @SuppressLint("InlinedApi") //读写文件，定位，手机状态，地理位置
    public String[] perms = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private ExecutorService executorService;
    private ExecutorCompletionService<MatchN> ecs;

    //获取定义的权限数组
    public String[] getPerms() {
        return perms;
    }

    //获取主机存储日志的路径
    public String getLogDir() {
        return logDir;
    }

    //获取算法的路径
    public String getFvPath() {
        return licenceDir + File.separator + "license.dat";
    }

    //完整的特征大小
    private static final int PERFECT_FEATURE_17682 = 17682;
    private static final int PERFECT_FEATURE_35058 = 35058;
    public static final int PERFECT_FEATURE_3 = 17632;//3特征
    public static final int PERFECT_FEATURE_6 = 35008;//6特征

    //可比对的特征大小
    public static final int WAIT_COMPARE_FEATURE_6 = 34784;//6特征
    public static final int WAIT_COMPARE_FEATURE_3 = 17408;//3特征

    public static final int IMG_SIZE = 500 * 200 + 208;
    public static final int FEATURE_SIZE = 6016;
    //临时加的图片大小
    public static final int T_SIZE = 1024 * 500;
    public static final int GET_IMG_OUT_TIME = 15;//默认设置抓图超时的时间为15S
    public static final int GET_IMG_OUT_TIME_5000 = 5000;//默认设置抓图超时的时间为5000
    //启动目标service的Action
    private static final String DevServiceAction = "com.example.mylibrary.DevService.action";
    //标记devService是否已经启动
    private boolean isStart = false;
    private String tgDirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + (File.separator + "TG_TEMPLATE");
    //后比模板的文件夹
    private String behindDatDir = tgDirPath + File.separator + "BehindTemplate";
    //后比的3，6模板路径
    private String behindTempl3Path = behindDatDir + File.separator + "TEMPL_3";
    private String behindTempl6Path = behindDatDir + File.separator + "TEMPL_6";
    //证书路径
    private String licenceDir = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "TG_VEIN";

    //日志的路径
    private String logDir = tgDirPath + File.separator + "Log";
    //图片存储的路径
    private String imgPath = tgDirPath + File.separator + "IMGS";

    //3模板
    public static final int TEMPL_MODEL_3 = 3;
    //6模板
    public static final int TEMPL_MODEL_6 = 6;
    //后比模式
    public static final int WORK_BEHIND = 2;

    //需要融合的特征的个数
    private int FEA_LENGTH = 0;
    //SDK的当前版本号
    private static final String SDK_VERSION = "1.1.0_190417_Beta";
    //算法证书的路径
//    private String licencePath;
//    private String lic_dir;

    /**
     * 获取SDK的版本
     */
    public String getSDKVersion() {
        return SDK_VERSION;
    }

    /**
     * 打开设备
     * 前比不需要证书，后比需要证书
     *
     * @param mHandler
     * @param context
     */
    private int workType = WORK_BEHIND;//默认是后比
    public boolean devOpen = false;//设备是否已经打开
    public boolean devClose = false;//设备关闭的标志
    private int devStatus = -1;//默认设备未开启的状态

    private Handler handler = null;
    private Context context;
    private Activity mActivity;
    private int templModelType;

    //获取通信库的代理对象
    public TGXG661API getTG661() {
        return TGXG661API.TGXG_661_API;
    }

    public boolean isDevOpen() {
        return devOpen;
    }

    public AudioProvider getAP() {
        return AudioProvider.getInstance(context);
    }

    private static TG661JBehindAPI getTG661JBehindAPI = null;

    public static TG661JBehindAPI getTG661JBehindAPI() {
        if (getTG661JBehindAPI == null) {
            synchronized (TG661JBehindAPI.class) {
                if (getTG661JBehindAPI == null) {
                    getTG661JBehindAPI = new TG661JBehindAPI();
                }
            }
        }
        return getTG661JBehindAPI;
    }

    //获取算法库的代理对象
    public TGFV getTGFV() {
        return TGFV.TGFV_INSTANCE;
    }

    /**
     * 设置特征模板的比对类型
     * 切换3/6特征状态时需要调用
     *
     * @param templModelType
     */
    public void setTemplModelType(int templModelType) {
        this.templModelType = templModelType;
        this.hasTemplName = false;
        this.isCheck = false;
        this.lastTemplName = "";
    }

    /**
     * 取消注册
     *
     * @param templModelType
     */
    private boolean isCancelRegister = false;

    public void cancelRegister(Handler handler, int templModelType) {
        isCancelRegister = true;
        this.handler = handler;
        this.templModelType = templModelType;
        work(handler, CANCEL_REGISTER);
    }

    public void openDev(Handler mHandler, Activity activity,
                        int workType, int templModelType,int type) {
        this.handler = mHandler;
        this.context = activity;
        this.mActivity = activity;
        this.templModelType = templModelType;
        this.workType = workType;
        if (devStatus == -1) {
            devStatus = getTG661().TGGetDevStatus();
            if (devStatus >= 0) {
                return;
            }
            writeCMD();
            work(mHandler, OPEN_DEV);
        }
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
     * 取消获取设备图像
     *
     * @param handler
     */
    public void cancelDevImg(Handler handler) {
        this.handler = handler;
        work(handler, CANCEL_DEV_IMG);
    }

    /**
     * 初始化算法
     *
     * @param handler        信使
     * @param context        上下文对象
     * @param inputStream    证书字节流
     * @param netLoadLicence 标记是否由网络下发证书流
     */
    private boolean netLoadLicence;
    private InputStream inputStream;

    public void initFV(Handler handler, Activity context, InputStream inputStream, boolean netLoadLicence) {
        this.handler = handler;
        Message message = handler.obtainMessage();
        message.what = INIT_FV;
        if (context == null) {
            message.arg1 = -1;
            handler.sendMessage(message);
            return;
        }
        this.mActivity = context;
        this.context = context;
        this.netLoadLicence = netLoadLicence;
        if (netLoadLicence) {
            if (inputStream == null) {
                message.arg1 = -2;
                handler.sendMessage(message);
                return;
            }
        }
        this.inputStream = inputStream;
        checkPermissions(handler,2,context);
        //创建线程池
        executorService = Executors.newCachedThreadPool();
        ecs = new ExecutorCompletionService<MatchN>(executorService);
    }

    //创建相关的文件夹,获取到相关的路径
    private void createDirPath() {
        File dat3File = new File(behindTempl3Path);
        if (!dat3File.exists())
            dat3File.mkdirs();
        File dat6File = new File(behindTempl6Path);
        if (!dat6File.exists())
            dat6File.mkdirs();
        File logFile = new File(logDir);
        if (!logFile.exists())
            logFile.mkdirs();
        File licenceFile = new File(licenceDir);
        if (!licenceFile.exists())
            licenceFile.mkdirs();
    }

    private String lastTemplName = "";
    private String templNameID;
    //标记是否已经注册了模板名称
    private boolean hasTemplName = false;
    //标记是否已经注册了模板
    private boolean hasTempl = false;
    //是否检测的标记
    private boolean isCheck = false;
    //是否发送图片数据对外显示
    private boolean sImg = false;
    private byte[] aimByte = null;
    private int templIndex = 0;
    private int templSize = 0;//模板的个数
    //特征融合后模板的存储路径
    private String templSavePath;

    //设置图片是否发送出去，内部测试用
    public void setsImg(boolean sImg) {
        this.sImg = sImg;
    }

    /**
     * 提取特征(注册使用)
     *
     * @param handler
     * @param templModelType
     * @param templId
     */
    public void extractFeatureRegister(Handler handler, int templModelType, String templId) {
        if (TextUtils.isEmpty(templId)) {
            Message extractFeatureRegisterMsg = handler.obtainMessage();
            extractFeatureRegisterMsg.what = EXTRACT_FEATURE_REGISTER;
            extractFeatureRegisterMsg.arg1 = -6;
            handler.sendMessage(extractFeatureRegisterMsg);
            return;
        }
        boolean b = RegularUtil.strContainsNumOrAlpOrChin(templId);
        if (!b) {
            Message extractFeatureRegisterMsg = handler.obtainMessage();
            extractFeatureRegisterMsg.what = EXTRACT_FEATURE_REGISTER;
            extractFeatureRegisterMsg.arg1 = -7;
            handler.sendMessage(extractFeatureRegisterMsg);
            return;
        }
        if (!lastTemplName.equals(templId)) {
            hasTempl = false;
            hasTemplName = false;
            isCheck = false;
            templIndex = 0;
        }
        this.lastTemplName = templId;
        this.templModelType = templModelType;
        this.templNameID = templId;
        this.handler = handler;
        if (templModelType == TEMPL_MODEL_3) {
            templSize = 3;
            templSavePath = behindTempl3Path;
            if (TextUtils.isEmpty(templSavePath)) {
                File file3 = new File(templSavePath);
                if (!file3.exists()) {
                    file3.mkdirs();
                }
            }
        } else if (templModelType == TEMPL_MODEL_6) {
            templSize = 6;
            templSavePath = behindTempl6Path;
            if (TextUtils.isEmpty(templSavePath)) {
                File file6 = new File(templSavePath);
                if (!file6.exists()) {
                    file6.mkdirs();
                }
            }
        }
        isCancelRegister = false;
        work(handler, EXTRACT_FEATURE_REGISTER);
    }


    /**
     * 提取特征(验证使用)
     *
     * @param handler
     * @param imgData 获取到的图片的数据
     */
    private byte[] imageData;

    public void extractFeatureVerify(Handler handler, byte[] imgData) {
        if (imgData == null) {
            Message extractFeatureVerifyMsg = handler.obtainMessage();
            extractFeatureVerifyMsg.what = EXTRACT_FEATURE_VERIFY;
            extractFeatureVerifyMsg.arg1 = -9;
            handler.sendMessage(extractFeatureVerifyMsg);
            return;
        }
        this.handler = handler;
        this.imageData = imgData;
        work(handler, EXTRACT_FEATURE_VERIFY);
    }

    /**
     * 特征融合
     *
     * @param handler
     * @param waitFusionFeature 待融合的特征的集合
     * @param featureSize       待融合的特征的数量
     */
    private List<byte[]> waitFusionFeature;

    public void fusionFeature(Handler handler, List<byte[]> waitFusionFeature) {
        if (waitFusionFeature == null || waitFusionFeature.size() == 0) {
            //传进来的数据不合规范
            Message fuMsg = handler.obtainMessage();
            fuMsg.what = FEATURE_FUSION;
            fuMsg.arg1 = -3;
            handler.sendMessage(fuMsg);
            return;
        }
        this.handler = handler;
        this.waitFusionFeature = waitFusionFeature;
        this.templSize = waitFusionFeature.size();
        this.FEA_LENGTH = waitFusionFeature.size();
        work(handler, FEATURE_FUSION);
    }

    /**
     * 将模板转换成比对模板
     *
     * @param handler
     * @param oldMatchTemplData 带有头的模板数据
     */
    private byte[] oldMatchTemplData;

    public void resolveCompareTempl(Handler handler, byte[] oldMatchTemplData) {
        if (oldMatchTemplData == null) {
            Message resolveMsg = handler.obtainMessage();
            resolveMsg.what = RESOLVE_COMPARE_TEMPL;
            resolveMsg.arg1 = -9;
            handler.sendMessage(resolveMsg);
            return;
        }
        this.handler = handler;
        this.oldMatchTemplData = oldMatchTemplData;
        work(handler, RESOLVE_COMPARE_TEMPL);
    }

    /**
     * 特征1:1验证
     *
     * @param handler
     */
    public void featureCompare1_1(Handler handler, String templName,byte[] data,boolean b) {
        if (TextUtils.isEmpty(templName)) {
            Message compare1_1Msg = handler.obtainMessage();
            compare1_1Msg.what = FEATURE_COMPARE1_1;
            compare1_1Msg.arg1 = -9;
            handler.sendMessage(compare1_1Msg);
            return;
        }
        this.handler = handler;
        if (!templName.contains(".dat")) {
            this.templNameID = templName + ".dat";
        } else {
            this.templNameID = templName;
        }
        work(handler, FEATURE_COMPARE1_1);
    }

    /**
     * 特征1:N验证
     *
     * @param handler
     */
    public void featureCompare1_N(Handler handler,boolean b) {
        this.handler = handler;
        work(handler, FEATURE_COMPARE1_N);
    }

    /**
     * 获取模板对应的算法的版本
     *
     * @param handler
     */
    public void getTemplVersion(Handler handler, String templName) {
        if (TextUtils.isEmpty(templName)) {
            Message fvVMsg = handler.obtainMessage();
            fvVMsg.what = TEMPL_FV_VERSION;
            fvVMsg.arg1 = -9;
            handler.sendMessage(fvVMsg);
            return;
        }
        this.handler = handler;
        if (!templName.contains(".dat")) {
            this.templNameID = templName + ".dat";
        } else {
            this.templNameID = templName;
        }
        work(handler, TEMPL_FV_VERSION);
    }

    /**
     * 获取模板的SN序列号
     *
     * @param handler
     */
    public void getTemplSN(Handler handler, String templName) {
        if (TextUtils.isEmpty(templName)) {
            Message fvSNMsg = handler.obtainMessage();
            fvSNMsg.what = TEMPL_SN;
            fvSNMsg.arg1 = -9;
            handler.sendMessage(fvSNMsg);
            return;
        }
        this.handler = handler;
        if (!templName.contains(".dat")) {
            this.templNameID = templName + ".dat";
        } else {
            this.templNameID = templName;
        }
        work(handler, TEMPL_SN);
    }

    /**
     * 获取模板的FW固件号
     *
     * @param handler
     */
    public void getTemplFW(Handler handler, String templName) {
        if (TextUtils.isEmpty(templName)) {
            Message fvFWMsg = handler.obtainMessage();
            fvFWMsg.what = TEMPL_FW;
            fvFWMsg.arg1 = -9;
            handler.sendMessage(fvFWMsg);
            return;
        }
        this.handler = handler;
        if (!templName.contains(".dat")) {
            this.templNameID = templName + ".dat";
        } else {
            this.templNameID = templName;
        }
        work(handler, TEMPL_FW);
    }

    /**
     * 获取模板对应的时间
     *
     * @param handler
     */
    public void getTemplTime(Handler handler, String templName) {
        if (TextUtils.isEmpty(templName)) {
            Message fvTimeMsg = handler.obtainMessage();
            fvTimeMsg.what = TEMPL_TIME;
            fvTimeMsg.arg1 = -9;
            handler.sendMessage(fvTimeMsg);
            return;
        }
        this.handler = handler;
        if (!templName.contains(".dat")) {
            this.templNameID = templName + ".dat";
        } else {
            this.templNameID = templName;
        }
        work(handler, TEMPL_TIME);
    }

    /**
     * 初始化证书
     */
    public void InitLicense() {
        createDirPath();
        File file = new File(licenceDir);
        if (!file.exists())
            file.mkdirs();
        if (netLoadLicence) {
            //如果是由网络下发证书流，先写入指定路径的文件
            writeLicenseToFile(inputStream);
        } else {
            //检测指定的文件夹下是否存在证书
            ArrayList<String> licenseList = FileUtil.getInitFinerFileList(licenceDir);
            if (licenseList != null) {
                if (licenseList.size() > 0) {
                    for (int i = 0; i < licenseList.size(); i++) {
                        String name = licenseList.get(i);
                        if (name.equals("license.dat")) {
                            //存在证书历史,初始化算法
                            work(handler, INIT_FV);
                        } else {
                            if (i == licenseList.size() - 1) {
                                //不存在证书历史，将SDK的证书写入指定文件
                                InputStream LicenseIs = context.getResources().openRawResource(R.raw.license);
                                writeLicenseToFile(LicenseIs);
                            }
                        }
                    }
                } else {
                    //不存在证书，将SDK的证书写入指定文件
                    InputStream LicenseIs = context.getResources().openRawResource(R.raw.license);
                    writeLicenseToFile(LicenseIs);
                }
            }
        }
    }

    //写入证书到指定文件
    private void writeLicenseToFile(InputStream inputStream) {
        String licensePath = licenceDir + File.separator + "license.dat";
        OutputStream output = null;
        try {
            if (inputStream != null) {
                output = new FileOutputStream(licensePath);
                // 拷贝到输出流
                byte[] buffer = new byte[2048];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("===license", e.getMessage());
        } finally {
            try {
                if (output != null)
                    output.flush();
                if (output != null)
                    output.close();
                if (inputStream != null) {
                    inputStream.close();
                }
                //初始化算法
                work(handler, INIT_FV);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取特征数量类型下对应路径下的文件名列表
     */
    public ArrayList<String> getAimFileList() {
        String aimPath = getAimPath();
        ArrayList<String> fileNameList = scanAimDirFileName(aimPath);
        return fileNameList;
    }

    //获取目标路径下所有文件列表
    public ArrayList<String> scanAimDirFileName(String path) {
        ArrayList<String> finerFileList = FileUtil.getInitFinerFileList(path);
        return finerFileList;
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
     * 后比音量加
     */
    public boolean increaseVolume(Handler handler) {
        this.handler = handler;
        boolean b = getAP().increaceVolume();
        return b;
    }

    /**
     * 后比音量减
     */
    public boolean descreaseVolume(Handler handler) {
        this.handler = handler;
        boolean b = getAP().decreaseVolume();
        return b;
    }

    /**
     * 获取当前的音量
     */
    public String getCurrentVolume(Handler handler) {
        this.handler = handler;
        float currentVolume = getAP().getCurrentVolume();
        return String.valueOf(currentVolume);
    }

    /**
     * 获取最大的音量值
     */
    public String getMaxVolume(Handler handler) {
        this.handler = handler;
        float streamVolumeMax = getAP().getStreamVolumeMax();
        return String.valueOf(streamVolumeMax);
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

    //获取后比6特征模板文件路径
    public String getBehind6TemplPath() {
        File file6 = new File(behindTempl3Path);
        if (!file6.exists()) {
            file6.mkdirs();
        }
        return behindTempl6Path;
    }

    //获取后比3特征模板文件路径
    public String getBehind3TemplPath() {
        File file3 = new File(behindTempl3Path);
        if (!file3.exists()) {
            file3.mkdirs();
        }
        return behindTempl3Path;
    }

    //获取目标文件夹得路径
    private String getAimPath() {
        String aimPath = "";
        if (templModelType == TEMPL_MODEL_3) {
            aimPath = getBehind3TemplPath();
        } else if (templModelType == TEMPL_MODEL_6) {
            aimPath = getBehind6TemplPath();
        }
        return aimPath;
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

    //检查权限
    public void checkPermissions(Handler handler,int type,Context context) {
        for (int i = 0; i < perms.length; i++) {
            String perm = perms[i];
            int checkResult = ContextCompat.checkSelfPermission(mActivity, perm);
            if (checkResult == PackageManager.PERMISSION_DENIED) {
                //权限没有同意，需要申请该权限
//                Intent intent = new Intent("com.tg.m661j.vein.api");
//                intent.addCategory("com.tg.m661j.vein.api");
//                intent.putExtra("type", type);
//                intent.putExtra("workModel", "b");

                Intent intent = new Intent("com.tg.m661j.vein.api");
                Bundle bundle = new Bundle();
                intent.addCategory("com.tg.m661j.vein.api");
//                bundle.putInt("type", type);
                bundle.putString("flag", Common.TG661JB);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
                break;
            } else {
                if (i == perms.length - 1) {
                    InitLicense();
                }
            }
        }
        if (type == 1) {
            saveTemplHost();
        }
    }

    private byte[] templateData;
    private String templateName;
    //    private int index;
    private byte[] upTemplData;
    private int devTmplNumUpRes;
    private int saveTemplateNum;//要上传到主机存储的模板的数量
//    private int type;

    //存储模板到主机
    public void saveTemplHost() {
        if (upTemplData != null) {
            subAndWriteSave(upTemplData, devTmplNumUpRes);
        } else {
            LogUtils.d("====>>>   upTemplData  为  null ");
        }
    }

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

    private void showWaitDialog(int type, String tip) {
        Message dialogMsg = handler.obtainMessage();
        dialogMsg.what = WAIT_DIALOG;
        dialogMsg.arg1 = type;
        dialogMsg.obj = tip;
        handler.sendMessage(dialogMsg);
    }


    private void work(final Handler handler, final int flag) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                switch (flag) {
                    case OPEN_DEV:
                        //打开设备
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
//                            cancelVerify(handler);
                            //设置工作模式
                            setDevWorkModel(handler, workType, templModelType);
                            devOpen = true;
                            devClose = false;
                            //发送打开设备的结果:
                            openDevMsg.arg1 = 1;
                            //启动后台devService
//                            if (!isStart) {
//                                startDevService(context);
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
                            getAP().release();//释放声音资源
                            devStatus = -1;
                            closeDevMsg.arg1 = 1;
                        } else {
                            closeDevMsg.arg1 = -1;
                        }
                        handler.sendMessage(closeDevMsg);
//                        showWaitDialog(-1, "");
                        break;
                    case INIT_FV:
                        //初始化算法
                        String licensePath = licenceDir + File.separator + "license.dat";
                        int tgInitFVProcessRes = getTGFV().TGInitFVProcess(licensePath);
                        Message initFvMsg = handler.obtainMessage();
                        initFvMsg.what = INIT_FV;
                        if (tgInitFVProcessRes == 0) {
                            initFvMsg.arg1 = 1;
                        } else if (tgInitFVProcessRes == 1) {
                            initFvMsg.arg1 = 2;
                        } else if (tgInitFVProcessRes == 2) {
                            initFvMsg.arg1 = 3;
                        } else if (tgInitFVProcessRes == 3) {
                            initFvMsg.arg1 = 4;
                        }
                        handler.sendMessage(initFvMsg);
                        break;
                    case SET_DEV_MODEL:
                        //设置设备的工作模式
                        int setDevModeRes1 = -1;
                        if (templModelType == TEMPL_MODEL_3) {
                            setDevModeRes1 = getTG661().TGSetDevMode(1);
                        } else if (templModelType == TEMPL_MODEL_6) {
                            setDevModeRes1 = getTG661().TGSetDevMode(1);
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
                    case DEV_WORK_MODEL:
                        //获取设备的工作模式
                        IntByReference ibr = new IntByReference();
                        int devWorkModelRes = getTG661().TGGetDevMode(ibr);
                        if (devWorkModelRes < 0) {
                            int n = 0;
                            boolean loop = true;
                            while (loop) {
                                devWorkModelRes = getTG661().TGGetDevMode(ibr);
                                n++;
                                if (devWorkModelRes < 0 && n < 3) {
                                    loop = true;
                                } else {
                                    n = 0;
                                    loop = false;
                                }
                            }
                        }
                        Message devWorkMsg = handler.obtainMessage();
                        devWorkMsg.what = DEV_WORK_MODEL;
                        if (devWorkModelRes < 0) {
                            devWorkMsg.arg1 = -1;
                        } else {
//                            type = 0;
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
                    case CANCEL_REGISTER:
                        int tgCancelRegisterRes = getTG661().TGCancelGetImage();
                        Log.d("===啊啊啊", "  取消注册： " + tgCancelRegisterRes);
                        Message cancelRegisterMsg = handler.obtainMessage();
                        cancelRegisterMsg.what = CANCEL_DEV_IMG;
                        if (tgCancelRegisterRes == 0) {
                            isCancelRegister = true;
                            cancelRegisterMsg.arg1 = 1;
                            setTemplModelType(templModelType);
                            aimByte = null;
                        } else {
                            cancelRegisterMsg.arg1 = -1;
                        }
                        handler.sendMessage(cancelRegisterMsg);
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
                    case EXTRACT_FEATURE_REGISTER:
                        //首先检查主机中已注册的模板文件名是否已存在
                        if (!hasTemplName) {
                            checkTemplName(templNameID);
                        }
                        //注册前核对当前指静脉是否已经注册
                        if (!isCheck && !hasTemplName) {
                            checkFingetRegister();
                        }
                        Message imgFeaMsg = handler.obtainMessage();
                        imgFeaMsg.what = EXTRACT_FEATURE_REGISTER;
                        //该指静脉已经注册
                        if (hasTemplName) {
                            imgFeaMsg.arg1 = -8;
                        } else if (hasTempl) {
                            imgFeaMsg.arg1 = -5;
                        } else {
                            if (!isCancelRegister) {
                                if (templIndex == 0) {
                                    getAP().play_inputDownGently();
                                } else if (templIndex > 0) {
                                    getAP().play_inputAgain();
                                }
//                            byte[] imgDataFea = new byte[IMG_SIZE];
                                byte[] imgDataFea = new byte[IMG_SIZE + T_SIZE];
                                imgDataFea[0] = ((byte) 0xfe);
                                int tgGetDevImageFeaRes = getTG661().TGGetDevImage(imgDataFea, GET_IMG_OUT_TIME);
                                if (tgGetDevImageFeaRes >= 0) {
                                    //提取特征  --- 注册
                                    byte[] regFeature = new byte[FEATURE_SIZE];
                                    int tgImgExtractFeatureRegRes = getTGFV().TGImgExtractFeatureRegister(imgDataFea,
                                            500, 200, regFeature);
                                    if (tgImgExtractFeatureRegRes == 0) {
                                        byte[] aimFeatures = null;
                                        if (templIndex < templSize) {
                                            imgFeaMsg.arg1 = 10;
                                            aimFeatures = jointTempl(regFeature);
                                            extractFeatureRegister(handler, templModelType,
                                                    TG661JBehindAPI.this.templNameID);
                                        }
                                        if (templSize == templIndex) {
                                            templIndex = 0;
                                            aimByte = null;
                                            isCheck = false;
                                            hasTemplName = true;
                                            //融合
                                            byte[] fusionTempl = null;
                                            if (templModelType == TEMPL_MODEL_3) {
                                                fusionTempl = new byte[PERFECT_FEATURE_3];
                                            } else if (templModelType == TEMPL_MODEL_6) {
                                                fusionTempl = new byte[PERFECT_FEATURE_6];
                                            }
                                            int fusionFeatureRes = getTGFV().TGFeaturesFusionTmpl(aimFeatures,
                                                    templSize, fusionTempl);
                                            if (fusionFeatureRes == 0) {
                                                //模板融合成功--存储
                                                String templSavePath = getAimPath();
                                                String savePath = templSavePath + File.separator + templNameID + ".dat";
                                                boolean writeFile = FileUtil.writeFile(fusionTempl, savePath);
                                                if (writeFile) {
                                                    hasTempl = true;
                                                    //登记成功
                                                    imgFeaMsg.arg1 = 1;
                                                    getAP().play_checkInSuccess();
                                                } else {
                                                    hasTempl = false;
                                                    getAP().play_checkInFail();
                                                }
                                            } else if (fusionFeatureRes == 6) {
                                                templIndex = 0;
                                                aimByte = null;
                                                hasTempl = false;
                                                hasTemplName = false;
                                                imgFeaMsg.arg1 = 2;
                                                getAP().play_checkInFail();
                                            } else if (fusionFeatureRes == -1) {
                                                templIndex = 0;
                                                aimByte = null;
                                                hasTempl = false;
                                                hasTemplName = false;
                                                imgFeaMsg.arg1 = 3;
                                                getAP().play_checkInFail();
                                            }
                                        }
                                    } else if (tgImgExtractFeatureRegRes == 1) {
                                        templIndex = 0;
                                        aimByte = null;
                                        hasTempl = false;
                                        hasTemplName = false;
                                        imgFeaMsg.arg1 = 4;
                                    } else if (tgImgExtractFeatureRegRes == 2) {
                                        templIndex = 0;
                                        aimByte = null;
                                        hasTempl = false;
                                        hasTemplName = false;
                                        imgFeaMsg.arg1 = 5;
                                    } else if (tgImgExtractFeatureRegRes == 3) {
                                        templIndex = 0;
                                        aimByte = null;
                                        hasTempl = false;
                                        hasTemplName = false;
                                        imgFeaMsg.arg1 = 6;
                                    } else if (tgImgExtractFeatureRegRes == 4) {
                                        templIndex = 0;
                                        aimByte = null;
                                        hasTempl = false;
                                        hasTemplName = false;
                                        imgFeaMsg.arg1 = 7;
                                    } else if (tgImgExtractFeatureRegRes == 5) {
                                        templIndex = 0;
                                        aimByte = null;
                                        hasTempl = false;
                                        hasTemplName = false;
                                        imgFeaMsg.arg1 = 8;
                                    } else if (tgImgExtractFeatureRegRes == -1) {
                                        templIndex = 0;
                                        aimByte = null;
                                        hasTempl = false;
                                        hasTemplName = false;
                                        imgFeaMsg.arg1 = 9;
                                    }
                                    if (sImg) {
                                        img(imgFeaMsg, imgDataFea, tgGetDevImageFeaRes);
//                                    imgFeaMsg.arg1=1;
                                        saveImg(templNameID + templIndex,
                                                imgDataFea, tgGetDevImageFeaRes);

                                    }
                                } else if (tgGetDevImageFeaRes == -1) {
                                    templIndex = 0;
                                    aimByte = null;
                                    hasTempl = false;
                                    hasTemplName = false;
                                    getAP().play_time_out();
                                    imgFeaMsg.arg1 = -1;
                                } else if (tgGetDevImageFeaRes == -2) {
                                    templIndex = 0;
                                    aimByte = null;
                                    hasTempl = false;
                                    hasTemplName = false;
                                    imgFeaMsg.arg1 = -2;
                                } else if (tgGetDevImageFeaRes == -3) {
                                    templIndex = 0;
                                    aimByte = null;
                                    hasTempl = false;
                                    hasTemplName = false;
                                    imgFeaMsg.arg1 = -3;
                                } else if (tgGetDevImageFeaRes == -4) {
                                    templIndex = 0;
                                    aimByte = null;
                                    hasTempl = false;
                                    hasTemplName = false;
                                    imgFeaMsg.arg1 = -4;
                                }
                            }
                        }
                        handler.sendMessage(imgFeaMsg);
                        break;
                    case EXTRACT_FEATURE_VERIFY:
                        //提取特征  ---  验证
                        byte[] verFeature = new byte[FEATURE_SIZE];
                        int tgImgExtractFeatureVerRes = getTGFV().TGImgExtractFeatureVerify(imageData,
                                500, 200, verFeature);
                        Message extractFeatureVerifyMsg = handler.obtainMessage();
                        extractFeatureVerifyMsg.what = EXTRACT_FEATURE_VERIFY;
                        if (tgImgExtractFeatureVerRes == 0) {
                            extractFeatureVerifyMsg.arg1 = 1;
                            extractFeatureVerifyMsg.obj = verFeature;
                        } else if (tgImgExtractFeatureVerRes == 1) {
                            extractFeatureVerifyMsg.arg1 = 2;
                        } else if (tgImgExtractFeatureVerRes == 2) {
                            extractFeatureVerifyMsg.arg1 = 3;
                        } else if (tgImgExtractFeatureVerRes == 3) {
                            extractFeatureVerifyMsg.arg1 = 4;
                        } else if (tgImgExtractFeatureVerRes == 4) {
                            extractFeatureVerifyMsg.arg1 = 5;
                        } else if (tgImgExtractFeatureVerRes == 5) {
                            extractFeatureVerifyMsg.arg1 = 6;
                        } else if (tgImgExtractFeatureVerRes == -1) {
                            extractFeatureVerifyMsg.arg1 = -1;
                        }
                        handler.sendMessage(extractFeatureVerifyMsg);
                        break;
                    case DEV_IMG_REGISTER:
                        getAP().play_inputDownGently();
//                        byte[] imgData = new byte[IMG_SIZE];
                        byte[] imgData = new byte[IMG_SIZE + T_SIZE];
                        imgData[0] = ((byte) 0xfe);
                        int tgGetDevImageRes = getTG661().TGGetDevImage(imgData, GET_IMG_OUT_TIME);
                        Message imgMsg = handler.obtainMessage();
                        imgMsg.what = DEV_IMG_REGISTER;
                        if (tgGetDevImageRes >= 0) {
                            if (sImg) {
                                img(imgMsg, imgData, tgGetDevImageRes);
                            }
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
                    case FEATURE_FUSION:
                        /**
                         *      （1） 0：特征融合成功，Output数据有效
                         *      （2） 6：特征融合失败，因"特征"数据一致性差，Output数据无效
                         *      （3）-1: 特征融合失败，因参数不合法,Output数据无效
                         */
                        Message waitFusionMsg = handler.obtainMessage();
                        waitFusionMsg.what = FEATURE_FUSION;
                        //拼接待融合的模板
                        if (waitFusionFeature != null && waitFusionFeature.size() > 0) {
                            byte[] fusionByte = new byte[FEATURE_SIZE * FEA_LENGTH];
                            for (int i = 0; i < waitFusionFeature.size(); i++) {
                                byte[] bytes = waitFusionFeature.get(i);
                                int i1 = bytes.length * i;
                                System.arraycopy(bytes, 0, fusionByte, i1, bytes.length);
                            }
                            byte[] fusionTemplData = null;
                            if (templModelType == TEMPL_MODEL_3) {
                                fusionTemplData = new byte[PERFECT_FEATURE_3];
                            } else if (templModelType == TEMPL_MODEL_6) {
                                fusionTemplData = new byte[PERFECT_FEATURE_6];
                            }
                            int tgFeaturesFusionTmplRes = getTGFV().TGFeaturesFusionTmpl(fusionByte,
                                    FEA_LENGTH, fusionTemplData);
                            if (tgFeaturesFusionTmplRes == 0) {
                                waitFusionMsg.arg1 = 1;
                                waitFusionMsg.obj = fusionTemplData;
                            } else if (tgFeaturesFusionTmplRes == 6) {
                                waitFusionMsg.arg1 = 2;
                            } else if (tgFeaturesFusionTmplRes == -1) {
                                waitFusionMsg.arg1 = -1;
                            }
                        } else {
                            waitFusionMsg.arg1 = -2;
                        }
                        handler.sendMessage(waitFusionMsg);
                        break;
                    case RESOLVE_COMPARE_TEMPL:
                        /**
                         *      （1） 1：模板解析成功， Output数据有效
                         *      （2）-1：模板解析失败，因参数不合法，Output数据无效
                         *      -2:待解析的模板数据为null
                         */
                        //将模板解析为比对模板，实际上就是去掉前208位
                        Message resolveCommpareTemplMsg = handler.obtainMessage();
                        resolveCommpareTemplMsg.what = RESOLVE_COMPARE_TEMPL;
                        if (oldMatchTemplData != null) {
                            byte[] matchTemplData = null;
                            if (templModelType == TEMPL_MODEL_3) {
                                matchTemplData = new byte[WAIT_COMPARE_FEATURE_3];
                            } else if (templModelType == TEMPL_MODEL_6) {
                                matchTemplData = new byte[WAIT_COMPARE_FEATURE_6];
                            }
                            int tgTmplToMatchTmplRes = getTGFV().TGTmplToMatchTmpl(oldMatchTemplData
                                    , matchTemplData);
                            if (tgTmplToMatchTmplRes == 0) {
                                resolveCommpareTemplMsg.arg1 = 1;
                                resolveCommpareTemplMsg.obj = matchTemplData;
                            } else if (tgTmplToMatchTmplRes == -1) {
                                resolveCommpareTemplMsg.arg1 = -1;
                            }
                        } else {
                            resolveCommpareTemplMsg.arg1 = -2;
                        }
                        handler.sendMessage(resolveCommpareTemplMsg);
                        break;
                    case FEATURE_COMPARE1_1:
                        /**
                         * 带比对的特征，带比对的比对模板，比对之后更新的模板数据，比对分数
                         */
                        String aimTemplPath = getAimPath() + File.separator + templNameID;
                        //读取模板
                        byte[] aim1_1Byte = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            aim1_1Byte = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            aim1_1Byte = new byte[PERFECT_FEATURE_6];
                        }
                        FileUtil.readFile(aimTemplPath, aim1_1Byte);
                        //转换成比对模板
                        byte[] matchTempll = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            matchTempll = new byte[WAIT_COMPARE_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            matchTempll = new byte[WAIT_COMPARE_FEATURE_6];
                        }
                        int tgTmplToMatchTmplRes = getTGFV().TGTmplToMatchTmpl(aim1_1Byte, matchTempll);
                        Message msg1_1 = handler.obtainMessage();
                        msg1_1.what = FEATURE_COMPARE1_1;
                        Bundle match1Bundle = new Bundle();
                        if (tgTmplToMatchTmplRes == 0) {
                            getAP().play_inputDownGently();
                            //抓取图片
//                            byte[] match1_1ImgData = new byte[IMG_SIZE];
                            byte[] match1_1ImgData = new byte[IMG_SIZE + T_SIZE];
                            match1_1ImgData[0] = ((byte) 0xfe);
                            int tgGetDevImageMatchRes = getTG661().TGGetDevImage(match1_1ImgData,
                                    GET_IMG_OUT_TIME);
                            if (tgGetDevImageMatchRes >= 0) {
                                //提取特征
                                byte[] match1_1Feature = new byte[FEATURE_SIZE];
                                int tgImgExtractFeatureVerifyRes = getTGFV().TGImgExtractFeatureVerify(
                                        match1_1ImgData, 500, 200, match1_1Feature);
                                if (tgImgExtractFeatureVerifyRes == 0) {
                                    byte[] update = null;
                                    if (templModelType == TEMPL_MODEL_3) {
                                        update = new byte[PERFECT_FEATURE_3];
                                    } else if (templModelType == TEMPL_MODEL_6) {
                                        update = new byte[PERFECT_FEATURE_6];
                                    }
                                    IntByReference int1_1 = new IntByReference();
                                    int tgFeatureMatchTmpl11Res = getTGFV().TGFeatureMatchTmpl11(
                                            match1_1Feature, matchTempll, update, int1_1);
                                    if (tgFeatureMatchTmpl11Res == 0) {
                                        getAP().play_verifySuccess();
                                        msg1_1.arg1 = 1;
                                        msg1_1.obj = update;
                                        int match1Score = int1_1.getValue();
                                        match1Bundle.putByteArray(COMPARE_N_TEMPL, update);
                                        match1Bundle.putString(COMPARE_NAME, templNameID);
                                        match1Bundle.putInt(COMPARE_N_SCORE, match1Score);
                                        //传出抓取图片的数据
                                        if (sImg) {
                                            match1Bundle.putInt("imgLength", tgGetDevImageMatchRes);
                                            match1Bundle.putByteArray("imgData", match1_1ImgData);
                                            //存储图片
                                            saveImg(templNameID,
                                                    match1_1ImgData, tgGetDevImageMatchRes);
                                        }
                                        msg1_1.setData(match1Bundle);
                                    } else if (tgFeatureMatchTmpl11Res == 7) {
                                        getAP().play_verifyFail();
                                        int match1Score = int1_1.getValue();
                                        msg1_1.arg1 = 2;
                                        match1Bundle.putInt(COMPARE_N_SCORE, match1Score);
                                        //传出抓取图片的数据
                                        if (sImg) {
                                            match1Bundle.putInt("imgLength", tgGetDevImageMatchRes);
                                            match1Bundle.putByteArray("imgData", match1_1ImgData);
                                            img(msg1_1, match1_1ImgData, tgGetDevImageMatchRes);
                                            //存储图片
                                            long l = System.currentTimeMillis();
                                            saveImg(String.valueOf(l),
                                                    match1_1ImgData, tgGetDevImageMatchRes);
                                        }
                                        msg1_1.setData(match1Bundle);
                                    } else if (tgFeatureMatchTmpl11Res == -1) {
                                        getAP().play_verifyFail();
                                        msg1_1.arg1 = 3;
                                        //传出抓取图片的数据
                                        if (sImg) {
                                            img(msg1_1, match1_1ImgData, tgGetDevImageMatchRes);
                                            long l = System.currentTimeMillis();
                                            //存储图片
                                            saveImg(String.valueOf(l),
                                                    match1_1ImgData, tgGetDevImageMatchRes);
                                        }
                                    }
                                } else if (tgImgExtractFeatureVerifyRes == 1) {
                                    getAP().play_verifyFail();
                                    msg1_1.arg1 = 4;
                                    //传出抓取图片的数据
                                    if (sImg) {
                                        img(msg1_1, match1_1ImgData, tgGetDevImageMatchRes);
                                        //存储图片
                                        long l = System.currentTimeMillis();
                                        saveImg(String.valueOf(l),
                                                match1_1ImgData, tgGetDevImageMatchRes);
                                    }
                                } else if (tgImgExtractFeatureVerifyRes == 2) {
                                    getAP().play_verifyFail();
                                    msg1_1.arg1 = 5;
                                    //传出抓取图片的数据
                                    if (sImg) {
                                        img(msg1_1, match1_1ImgData, tgGetDevImageMatchRes);
                                        long l = System.currentTimeMillis();
                                        //存储图片
                                        saveImg(String.valueOf(l),
                                                match1_1ImgData, tgGetDevImageMatchRes);
                                    }
                                } else if (tgImgExtractFeatureVerifyRes == 3) {
                                    getAP().play_verifyFail();
                                    msg1_1.arg1 = 6;
                                    //传出抓取图片的数据
                                    if (sImg) {
                                        img(msg1_1, match1_1ImgData, tgGetDevImageMatchRes);
                                        long l = System.currentTimeMillis();
                                        //存储图片
                                        saveImg(String.valueOf(l),
                                                match1_1ImgData, tgGetDevImageMatchRes);
                                    }
                                } else if (tgImgExtractFeatureVerifyRes == 4) {
                                    getAP().play_verifyFail();
                                    msg1_1.arg1 = 7;
                                    //传出抓取图片的数据
                                    if (sImg) {
                                        img(msg1_1, match1_1ImgData, tgGetDevImageMatchRes);
                                        long l = System.currentTimeMillis();
                                        //存储图片
                                        saveImg(String.valueOf(l),
                                                match1_1ImgData, tgGetDevImageMatchRes);
                                    }
                                } else if (tgImgExtractFeatureVerifyRes == 5) {
                                    getAP().play_verifyFail();
                                    msg1_1.arg1 = 8;
                                    //传出抓取图片的数据
                                    if (sImg) {
                                        img(msg1_1, match1_1ImgData, tgGetDevImageMatchRes);
                                        long l = System.currentTimeMillis();
                                        //存储图片
                                        saveImg(String.valueOf(l),
                                                match1_1ImgData, tgGetDevImageMatchRes);
                                    }
                                } else if (tgImgExtractFeatureVerifyRes == -1) {
                                    getAP().play_time_out();
                                    msg1_1.arg1 = 9;
                                    //传出抓取图片的数据
                                    if (sImg) {
                                        img(msg1_1, match1_1ImgData, tgGetDevImageMatchRes);
                                        long l = System.currentTimeMillis();
                                        //存储图片
                                        saveImg(String.valueOf(l),
                                                match1_1ImgData, tgGetDevImageMatchRes);
                                    }
                                }
                            } else if (tgGetDevImageMatchRes == -1) {
                                getAP().play_time_out();
                                msg1_1.arg1 = 10;
                            } else if (tgGetDevImageMatchRes == -2) {
                                getAP().play_verifyFail();
                                msg1_1.arg1 = 11;
                            } else if (tgGetDevImageMatchRes == -3) {
                                //这里播放的语音应该是操作取消
//                                getAP().play_vetify_fail();
                                msg1_1.arg1 = 12;
                            } else if (tgGetDevImageMatchRes == -4) {
                                getAP().play_verifyFail();
                                msg1_1.arg1 = 13;
                            }
                        } else if (tgTmplToMatchTmplRes == -1) {
                            getAP().play_verifyFail();
                            msg1_1.arg1 = -1;
                        }
                        handler.sendMessage(msg1_1);
                        break;
                    case FEATURE_COMPARE1_N:
                        Message matchNMsg = handler.obtainMessage();
                        matchNMsg.what = FEATURE_COMPARE1_N;
                        Bundle matchNBundle = new Bundle();
                        String aimPath = getAimPath();

                        getDevImgData(handler, matchNMsg, matchNBundle, aimPath);

                        break;
                    case TEMPL_FV_VERSION:
                        //获取模板对应算法的版本
                        String templVersionPath = "";
                        Message versionMsg = handler.obtainMessage();
                        versionMsg.what = TEMPL_FV_VERSION;
                        byte[] versionTempl = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            templVersionPath = getBehind3TemplPath() + File.separator + templNameID;
                            versionTempl = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            templVersionPath = getBehind6TemplPath() + File.separator + templNameID;
                            versionTempl = new byte[PERFECT_FEATURE_6];
                        }
                        if (!TextUtils.isEmpty(templVersionPath)) {
                            FileUtil.readFile(templVersionPath, versionTempl);
                            byte[] snData = new byte[5];
                            int tgGetSNFromTmplRes = getTGFV().TGGetAPIVerFromTmpl(versionTempl, snData);
                            if (tgGetSNFromTmplRes == 0) {
                                versionMsg.arg1 = 1;
                                try {
                                    String snVersion = new String(snData, "UTF-8");
                                    versionMsg.obj = snVersion;
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else if (tgGetSNFromTmplRes == -1) {
                                versionMsg.arg1 = -1;
                            }
                        } else {
                            versionMsg.arg1 = -2;
                        }
                        handler.sendMessage(versionMsg);
                        break;
                    case TEMPL_SN:
                        //获取模板对应算法的序列号
                        String templSnPath = "";
                        Message snMsg = handler.obtainMessage();
                        snMsg.what = TEMPL_SN;
                        byte[] snTempl = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            templSnPath = getBehind3TemplPath() + File.separator + templNameID;
                            snTempl = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            templSnPath = getBehind6TemplPath() + File.separator + templNameID;
                            snTempl = new byte[PERFECT_FEATURE_6];
                        }
                        if (!TextUtils.isEmpty(templSnPath)) {
                            FileUtil.readFile(templSnPath, snTempl);
                            byte[] snData = new byte[17];
                            int tgGetSNFromTmplRes = getTGFV().TGGetSNFromTmpl(snTempl, snData);
                            if (tgGetSNFromTmplRes == 0) {
                                snMsg.arg1 = 1;
                                try {
                                    String sn = new String(snData, "UTF-8");
                                    snMsg.obj = sn;
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else if (tgGetSNFromTmplRes == -1) {
                                snMsg.arg1 = -1;
                            }
                        } else {
                            snMsg.arg1 = -2;
                        }
                        handler.sendMessage(snMsg);
                        break;
                    case TEMPL_FW:
                        //获取模板对应算法的固件号
                        String templFwPath = "";
                        Message fwMsg = handler.obtainMessage();
                        fwMsg.what = TEMPL_FW;
                        byte[] fwTempl = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            templFwPath = getBehind3TemplPath() + File.separator + templNameID;
                            fwTempl = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            templFwPath = getBehind6TemplPath() + File.separator + templNameID;
                            fwTempl = new byte[PERFECT_FEATURE_6];
                        }
                        if (!TextUtils.isEmpty(templFwPath)) {
                            FileUtil.readFile(templFwPath, fwTempl);
                            byte[] fwData = new byte[17];
                            int tgGetSNFromTmplRes = getTGFV().TGGetFWFromTmpl(fwTempl, fwData);
                            if (tgGetSNFromTmplRes == 0) {
                                fwMsg.arg1 = 1;
                                try {
                                    String fw = new String(fwData, "UTF-8");
                                    fwMsg.obj = fw;
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else if (tgGetSNFromTmplRes == -1) {
                                fwMsg.arg1 = -1;
                            }
                        } else {
                            fwMsg.arg1 = -2;
                        }
                        handler.sendMessage(fwMsg);
                        break;
                    case TEMPL_TIME:
                        //获取模板对应的时间
                        String templTimePath = "";
                        Message timeMsg = handler.obtainMessage();
                        timeMsg.what = TEMPL_TIME;
                        byte[] timeTempl = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            templTimePath = getBehind3TemplPath() + File.separator + templNameID;
                            timeTempl = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            templTimePath = getBehind6TemplPath() + File.separator + templNameID;
                            timeTempl = new byte[PERFECT_FEATURE_6];
                        }
                        if (!TextUtils.isEmpty(templTimePath)) {
                            FileUtil.readFile(templTimePath, timeTempl);
                            byte[] timeData = new byte[15];
                            int tgGetSNFromTmplRes = getTGFV().TGGetTimeFromTmpl(timeTempl, timeData);
                            if (tgGetSNFromTmplRes == 0) {
                                timeMsg.arg1 = 1;
                                try {
                                    String time = new String(timeData, "UTF-8");
                                    timeMsg.obj = time;
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else if (tgGetSNFromTmplRes == -1) {
                                timeMsg.arg1 = -1;
                            }
                        } else {
                            timeMsg.arg1 = -2;
                        }
                        handler.sendMessage(timeMsg);
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
                    case WRITE_FILE:
                        boolean writeTemplHost = writeTemplHost(templateData, templateName);
                        break;
                }
            }
        });
        thread.start();
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

    private byte[] jointTempl(byte[] newFeature) {
        if (aimByte == null) {
            if (templSize == 0) {
                LogUtils.d("要拼接的模板数量是0");
            } else if (templSize == 3) {
                aimByte = new byte[FEATURE_SIZE * templSize];
            } else if (templSize == 6) {
                aimByte = new byte[FEATURE_SIZE * templSize];
            }
        }
        int length = templIndex * FEATURE_SIZE;
        System.arraycopy(newFeature, 0, aimByte, length, newFeature.length);
        templIndex++;
        return aimByte;
    }

    //获取图片数据
    private void getDevImgData(Handler handler, Message msg, Bundle bundle, String aimPath) {
        TG661JBehindAPI.getTG661JBehindAPI().getAP().play_inputDownGently();
//                        byte[] match1_NImgData = new byte[IMG_SIZE];
        byte[] match1_NImgData = new byte[TG661JBehindAPI.IMG_SIZE + TG661JBehindAPI.T_SIZE];
        match1_NImgData[0] = ((byte) 0xfe);
        int tgGetDevImageMatchNRes = TG661JBehindAPI.getTG661JBehindAPI().getTG661()
                .TGGetDevImage(match1_NImgData, TG661JBehindAPI.GET_IMG_OUT_TIME);
        if (tgGetDevImageMatchNRes >= 0) {
            //提取特征
            byte[] match1_NFeature = new byte[TG661JBehindAPI.FEATURE_SIZE];
            int tgImgExtractFeatureVerifyNRes = TG661JBehindAPI.getTG661JBehindAPI()
                    .getTGFV().TGImgExtractFeatureVerify(match1_NImgData, 500,
                            200, match1_NFeature);
            if (tgImgExtractFeatureVerifyNRes == 0) {
                finger1N(msg,bundle);
//                //分流比对
//                excutorsFile(aimPath, handler, msg, bundle, match1_NImgData,
//                        tgGetDevImageMatchNRes, match1_NFeature);
            } else if (tgImgExtractFeatureVerifyNRes == 1) {
                getAP().play_verifyFail();
                msg.arg1 = 4;
                //传出抓取图片的数据
                if (sImg) {
                    img(msg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            } else if (tgImgExtractFeatureVerifyNRes == 2) {
                getAP().play_verifyFail();
                msg.arg1 = 5;
                //传出抓取图片的数据
                if (sImg) {
                    img(msg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            } else if (tgImgExtractFeatureVerifyNRes == 3) {
                getAP().play_verifyFail();
                msg.arg1 = 6;
                //传出抓取图片的数据
                if (sImg) {
                    img(msg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            } else if (tgImgExtractFeatureVerifyNRes == 4) {
                getAP().play_verifyFail();
                msg.arg1 = 7;
            } else if (tgImgExtractFeatureVerifyNRes == 5) {
                getAP().play_verifyFail();
                msg.arg1 = 8;
                //传出抓取图片的数据
                if (sImg) {
                    img(msg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            } else if (tgImgExtractFeatureVerifyNRes == -1) {
                getAP().play_verifyFail();
                msg.arg1 = 9;
                //传出抓取图片的数据
                if (sImg) {
                    img(msg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            }
        } else if (tgGetDevImageMatchNRes == -1) {
            getAP().play_time_out();
            msg.arg1 = -1;
        } else if (tgGetDevImageMatchNRes == -2) {
            getAP().play_verifyFail();
            msg.arg1 = -2;
        } else if (tgGetDevImageMatchNRes == -3) {
            getAP().play_verifyFail();
            msg.arg1 = -3;
        } else if (tgGetDevImageMatchNRes == -4) {
            getAP().play_verifyFail();
            msg.arg1 = -4;
        }
        handler.sendMessage(msg);
    }

    //创建线程池
    private int cardinal = 1000;//1:N的基数设定为300，提高比对的效率

    private void excutorsFile(String datPath, Handler handler, Message message, Bundle bundle,
                              byte[] matchNImgData, int tgGetNImgRes, byte[] matchImgFeature) {
        File file = new File(datPath);
        if (!file.exists()) {
//            return null;
        } else {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files.length > 0) {
                    int datSize = files.length;
                    double v = (double) datSize / cardinal;
                    //四舍五入向上取整，1：N会比对的次数
                    double ceil = Math.ceil(v);
                    //四舍五入向下取整
                    double floor = Math.floor(v);
                    double value;
                    if (ceil != floor) {
                        value = ceil;
                    } else {
                        //如果ceil和floor相等，则正好除尽
                        value = floor;
                    }
                    //为线程池添加事件
                    if (value > 0) {
                        int count = 0;
                        for (int i = 0; i < 1; i++) {
                            File[] files1 = null;
                            int srcLength = 0;
                            if (value == ceil && i == floor) {
                                //除不尽，最后一项
                                srcLength = files.length - 1000 * i;
                                files1 = new File[srcLength];
                            } else {
                                files1 = new File[1000];
                                srcLength = files1.length;
                            }
                            Log.d("===HHH", "     srcLength ； " + srcLength);
                            System.arraycopy(files, 1000 * i, files1, 0, srcLength);
                            GetFileTask getContentTask = new GetFileTask(files1, templModelType
                                    , datPath, message, bundle, matchNImgData, tgGetNImgRes,
                                    matchImgFeature);
                            //添加任务
                            ecs.submit(getContentTask);
                            count++;
                        }
                        for (int i = 0; i < count; i++) {
                            try {
                                Future<MatchN> take = ecs.take();
                                MatchN matchN = take.get();
                                int resultCode = matchN.getResultCode();
                                if (resultCode == 1) {
                                    TG661JBehindAPI.getTG661JBehindAPI()
                                            .getAP().play_verifySuccess();
                                    break;
                                } else {
                                    if (i == count - 1) {
                                        if (resultCode == 2) {
                                            TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
                                        } else if (resultCode == 3) {
                                            TG661JBehindAPI.getTG661JBehindAPI().getAP().play_time_out();
                                        }
                                    }
                                }
                                Log.d("===HHH", "   数量：" + count + "  结果码：" + resultCode);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }


    private int v = 0;

    //读取所有文件模板
    private ArrayList<byte[]> readAllTempl(String aimPath) {
        ArrayList<byte[]> templsByte = null;
        File file = new File(aimPath);
        if (!file.exists()) {
            return null;
        } else {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files.length > 0) {
                    templsByte = new ArrayList<>();
                    for (File file1 : files) {
                        byte[] bytes = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            bytes = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            bytes = new byte[PERFECT_FEATURE_6];
                        }
                        v++;
                        String name = file1.getName();
                        long length = file1.length();
                        Log.d("===LLL", "   文件:" + name + " 的长度 ：" + length + "   数量：" + v);
//                        byte[] file1Data = FileUtil.readFile(file1);
                        FileUtil.readFileToArray(file1, bytes);
//                        FileUtil.readFile(file1, bytes);
//                        byte[] bytes1 = FileUtil.readFileToArray(file1);
                        templsByte.add(bytes);
                    }
                    v = 0;
                }
            }
        }
        return templsByte;
    }

    public void finger1N(Message matchNMsg, Bundle matchNBundle) {
        //获取模板的所有地址
        String templsPath = getAimPath();
//        Message matchNMsg = handler.obtainMessage();
//        matchNMsg.what = FEATURE_COMPARE1_N;
//        Bundle matchNBundle = new Bundle();
//        //读取所有文件模板
        ArrayList<byte[]> allTemplByteList = readAllTempl(templsPath);

        byte[] allWaitTempl = null;
        ArrayList<byte[]> allMatchTemplList = null;
        //转成比对模板
        if (allTemplByteList != null && allTemplByteList.size() > 0) {
            allMatchTemplList = new ArrayList<>();
            for (byte[] bytes : allTemplByteList) {
                byte[] matchTempll_N = null;
                if (templModelType == TEMPL_MODEL_3) {
                    matchTempll_N = new byte[WAIT_COMPARE_FEATURE_3];
                } else if (templModelType == TEMPL_MODEL_6) {
                    matchTempll_N = new byte[WAIT_COMPARE_FEATURE_6];
                }
                int tgTmplToMatchTmpl1_NRes = getTGFV().TGTmplToMatchTmpl(bytes, matchTempll_N);
                if (tgTmplToMatchTmpl1_NRes == 0) {
                    allMatchTemplList.add(matchTempll_N);
                } else if (tgTmplToMatchTmpl1_NRes == -1) {
                    int k = 0;
                    boolean continueMatch = true;
                    while (continueMatch) {
                        if (k < 3) {
                            tgTmplToMatchTmpl1_NRes = getTGFV().
                                    TGTmplToMatchTmpl(bytes, matchTempll_N);
                            k++;
                            if (tgTmplToMatchTmpl1_NRes == 0) {
                                allMatchTemplList.add(matchTempll_N);
                                k = 0;
                                continueMatch = false;
                            }
                        }
                    }
                }
            }
            if (allMatchTemplList.size() > 0) {
                if (templModelType == TEMPL_MODEL_3) {
                    allWaitTempl = new byte[WAIT_COMPARE_FEATURE_3
                            * allMatchTemplList.size()];
                } else if (templModelType == TEMPL_MODEL_6) {
                    allWaitTempl = new byte[WAIT_COMPARE_FEATURE_6
                            * allMatchTemplList.size()];
                }
                for (int i = 0; i < allMatchTemplList.size(); i++) {
                    int i1 = 0;
                    if (templModelType == TEMPL_MODEL_3) {
                        i1 = WAIT_COMPARE_FEATURE_3 * i;
                    } else if (templModelType == TEMPL_MODEL_6) {
                        i1 = WAIT_COMPARE_FEATURE_6 * i;
                    }
                    byte[] bytes = allMatchTemplList.get(i);
                    System.arraycopy(bytes, 0, allWaitTempl, i1, bytes.length);
                }
            }
        }
        //用转换好的模板等待比对，--》抓取图片
        getAP().play_inputDownGently();
//                        byte[] match1_NImgData = new byte[IMG_SIZE];
        byte[] match1_NImgData = new byte[IMG_SIZE + T_SIZE];
        match1_NImgData[0] = ((byte) 0xfe);
        int tgGetDevImageMatchNRes = getTG661().TGGetDevImage(match1_NImgData,
                GET_IMG_OUT_TIME);
        if (tgGetDevImageMatchNRes >= 0) {
            //提取特征
            byte[] match1_NFeature = new byte[FEATURE_SIZE];
            int tgImgExtractFeatureVerifyNRes = getTGFV().TGImgExtractFeatureVerify(
                    match1_NImgData, 500, 200, match1_NFeature);
            if (tgImgExtractFeatureVerifyNRes == 0) {
                if (allWaitTempl != null) {
                    IntByReference intB1 = new IntByReference();
                    IntByReference intB2 = new IntByReference();
                    byte[] uuId = new byte[33];
                    byte[] updateTempl = null;
                    if (templModelType == TEMPL_MODEL_3) {
                        updateTempl = new byte[PERFECT_FEATURE_3];
                    } else if (templModelType == TEMPL_MODEL_6) {
                        updateTempl = new byte[PERFECT_FEATURE_6];
                    }
                    int tgFeatureMatchTmpl1NRes = getTGFV().TGFeatureMatchTmpl1N(match1_NFeature,
                            allWaitTempl, allMatchTemplList.size(), intB1, uuId,
                            intB2, updateTempl);
                    if (tgFeatureMatchTmpl1NRes == 0) {
                        getAP().play_verifySuccess();
                        int templIndex = intB1.getValue();//模板的指针位置
                        int templScore = intB2.getValue();//验证的分数
                        //根据返回的指针获取主机中模板文件的名字
                        String fileName = FileUtil.getFileName(templsPath, templIndex - 1);
                        matchNMsg.arg1 = 1;
                        matchNBundle.putByteArray(COMPARE_N_TEMPL, updateTempl);
                        matchNBundle.putString(COMPARE_NAME, fileName);
                        matchNBundle.putInt(COMPARE_N_SCORE, templScore);
                        //传出抓取图片的数据
                        if (sImg) {
                            matchNBundle.putInt("imgLength", tgGetDevImageMatchNRes);
                            matchNBundle.putByteArray("imgData", match1_NImgData);
//                                            img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                            //存储图片
                            saveImg(fileName, match1_NImgData, tgGetDevImageMatchNRes);
                        }
                        matchNMsg.setData(matchNBundle);
                    } else if (tgFeatureMatchTmpl1NRes == 8) {
                        int templIndex = intB1.getValue();//模板的指针位置
                        int templScore = intB2.getValue();//验证的分数
                        getAP().play_verifyFail();
                        matchNMsg.arg1 = 2;
                        matchNBundle.putInt(COMPARE_N_SCORE, templScore);
                        //传出抓取图片的数据
                        if (sImg) {
                            matchNBundle.putInt("imgLength", tgGetDevImageMatchNRes);
                            matchNBundle.putByteArray("imgData", match1_NImgData);
//                                            img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                            //存储图片
                            long l = System.currentTimeMillis();
                            saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                        }
                        matchNMsg.setData(matchNBundle);
                    } else if (tgFeatureMatchTmpl1NRes == -1) {
                        getAP().play_time_out();
                        matchNMsg.arg1 = 3;
                        //传出抓取图片的数据
                        if (sImg) {
                            img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                            //存储图片
                            long l = System.currentTimeMillis();
                            saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                        }
                    }
                }
            } else if (tgImgExtractFeatureVerifyNRes == 1) {
                getAP().play_verifyFail();
                matchNMsg.arg1 = 4;
                //传出抓取图片的数据
                if (sImg) {
                    img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            } else if (tgImgExtractFeatureVerifyNRes == 2) {
                getAP().play_verifyFail();
                matchNMsg.arg1 = 5;
                //传出抓取图片的数据
                if (sImg) {
                    img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            } else if (tgImgExtractFeatureVerifyNRes == 3) {
                getAP().play_verifyFail();
                matchNMsg.arg1 = 6;
                //传出抓取图片的数据
                if (sImg) {
                    img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            } else if (tgImgExtractFeatureVerifyNRes == 4) {
                getAP().play_verifyFail();
                matchNMsg.arg1 = 7;
            } else if (tgImgExtractFeatureVerifyNRes == 5) {
                getAP().play_verifyFail();
                matchNMsg.arg1 = 8;
                //传出抓取图片的数据
                if (sImg) {
                    img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            } else if (tgImgExtractFeatureVerifyNRes == -1) {
                getAP().play_verifyFail();
                matchNMsg.arg1 = 9;
                //传出抓取图片的数据
                if (sImg) {
                    img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    long l = System.currentTimeMillis();
                    saveImg(String.valueOf(l), match1_NImgData, tgGetDevImageMatchNRes);
                }
            }
        } else if (tgGetDevImageMatchNRes == -1) {
            getAP().play_time_out();
            matchNMsg.arg1 = -1;
        } else if (tgGetDevImageMatchNRes == -2) {
            getAP().play_verifyFail();
            matchNMsg.arg1 = -2;
        } else if (tgGetDevImageMatchNRes == -3) {
            getAP().play_verifyFail();
            matchNMsg.arg1 = -3;
        } else if (tgGetDevImageMatchNRes == -4) {
            getAP().play_verifyFail();
            matchNMsg.arg1 = -4;
        }
//        handler.sendMessage(matchNMsg);
    }

    private boolean checkTemplName(String newTemplName) {
        //检查模板文件的名称是否重复
        newTemplName = newTemplName + ".dat";
        String templsPath = getAimPath();
        ArrayList<String> allTemplName = scanAimDirFileName(templsPath);
        if (allTemplName != null && allTemplName.size() > 0) {
            for (int i = 0; i < allTemplName.size(); i++) {
                String templName = allTemplName.get(i);
                if (templName.equals(newTemplName)) {
                    hasTemplName = true;
                    getAP().play_registerRepeat();
                    break;
                }
                if (i == allTemplName.size() - 1 && templName.equals(newTemplName)) {
                    hasTemplName = false;
                }
            }
        }
        return hasTemplName;
    }

    //检测当前指静脉是否已经注册
    private void checkFingetRegister() {
        //获取模板的所有地址
        String templsPath = getAimPath();
        Message imgFeaNMsg = handler.obtainMessage();
        imgFeaNMsg.what = FEATURE_COMPARE1_N;
        //读取所有文件模板
        ArrayList<byte[]> allTemplByteList = readAllTempl(templsPath);
        if (allTemplByteList != null && allTemplByteList.size() > 0) {
            byte[] allWaitTempl = null;
            //转成比对模板
            ArrayList<byte[]> allMatchTemplList = new ArrayList<>();
            for (byte[] bytes : allTemplByteList) {
                byte[] matchTempll_N = null;
                if (templModelType == TEMPL_MODEL_3) {
                    matchTempll_N = new byte[WAIT_COMPARE_FEATURE_3];
                } else if (templModelType == TEMPL_MODEL_6) {
                    matchTempll_N = new byte[WAIT_COMPARE_FEATURE_6];
                }
                int tgTmplToMatchTmpl1_NRes = getTGFV().TGTmplToMatchTmpl(bytes, matchTempll_N);
                if (tgTmplToMatchTmpl1_NRes == 0) {
                    allMatchTemplList.add(matchTempll_N);
                } else if (tgTmplToMatchTmpl1_NRes == -1) {
                    int k = 0;
                    boolean continueMatch = true;
                    while (continueMatch) {
                        if (k < 3) {
                            tgTmplToMatchTmpl1_NRes = getTGFV().
                                    TGTmplToMatchTmpl(bytes, matchTempll_N);
                            k++;
                            if (tgTmplToMatchTmpl1_NRes == 0) {
                                allMatchTemplList.add(matchTempll_N);
                                k = 0;
                                continueMatch = false;
                            }
                        }
                    }
                }
            }
            if (allMatchTemplList.size() > 0) {
                if (templModelType == TEMPL_MODEL_3) {
                    allWaitTempl = new byte[WAIT_COMPARE_FEATURE_3
                            * allMatchTemplList.size()];
                } else if (templModelType == TEMPL_MODEL_6) {
                    allWaitTempl = new byte[WAIT_COMPARE_FEATURE_6
                            * allMatchTemplList.size()];
                }
                for (int i = 0; i < allMatchTemplList.size(); i++) {
                    int i1 = 0;
                    if (templModelType == TEMPL_MODEL_3) {
                        i1 = WAIT_COMPARE_FEATURE_3 * i;
                    } else if (templModelType == TEMPL_MODEL_6) {
                        i1 = WAIT_COMPARE_FEATURE_6 * i;
                    }
                    byte[] bytes = allMatchTemplList.get(i);
                    System.arraycopy(bytes, 0, allWaitTempl, i1, bytes.length);
                }
            }
            //用转换好的模板等待比对，--》抓取图片
            getAP().play_inputDownGently();
//            byte[] match1_NImgData = new byte[IMG_SIZE];
            byte[] match1_NImgData = new byte[IMG_SIZE + T_SIZE];
            match1_NImgData[0] = ((byte) 0xfe);
            int tgGetDevImageMatchNRes = getTG661().TGGetDevImage(match1_NImgData,
                    GET_IMG_OUT_TIME);
            if (tgGetDevImageMatchNRes >= 0) {
                //传出抓取图片的数据
                if (sImg) {
                    img(imgFeaNMsg, match1_NImgData, tgGetDevImageMatchNRes);
                    //存储图片
                    imgFeaNMsg.arg1 = 1;
                    saveImg(templNameID + 0,
                            match1_NImgData, tgGetDevImageMatchNRes);
                }
                //提取特征
                byte[] match1_NFeature = new byte[FEATURE_SIZE];
                int tgImgExtractFeatureVerifyNRes = getTGFV().TGImgExtractFeatureVerify(
                        match1_NImgData, 500, 200, match1_NFeature);
                if (tgImgExtractFeatureVerifyNRes == 0) {
                    if (allWaitTempl != null) {
                        IntByReference intB1 = new IntByReference();
                        IntByReference intB2 = new IntByReference();
                        byte[] uuId = new byte[33];
                        byte[] updateTempl = null;
                        if (templModelType == TEMPL_MODEL_3) {
                            updateTempl = new byte[PERFECT_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            updateTempl = new byte[PERFECT_FEATURE_6];
                        }
                        int tgFeatureMatchTmpl1NRes = getTGFV().TGFeatureMatchTmpl1N(match1_NFeature,
                                allWaitTempl, allMatchTemplList.size(), intB1, uuId,
                                intB2, updateTempl);
                        if (tgFeatureMatchTmpl1NRes == 0) {
                            //该指静脉已经注册
                            getAP().play_registerRepeat();
                            TG661JBehindAPI.this.lastTemplName = "";
                            isCheck = true;
                            hasTempl = true;
                            imgFeaNMsg.arg1 = -5;
                        } else if (tgFeatureMatchTmpl1NRes == 8) {
//                            imgFeaNMsg.arg1 = 1;
                            //该指静脉尚未注册，记录特征
                            isCheck = true;
                            hasTempl = false;
                            int firstRegister = getTGFV().TGImgExtractFeatureRegister(
                                    match1_NImgData, 500, 200, match1_NFeature);
                            if (firstRegister == 0) {
                                jointTempl(match1_NFeature);
                            }
                        } else if (tgFeatureMatchTmpl1NRes == -1) {
                            //参数错误
                            isCheck = false;
                            imgFeaNMsg.arg1 = 2;
                        }
                    }
                } else if (tgImgExtractFeatureVerifyNRes == 1) {
                    imgFeaNMsg.arg1 = 3;
                } else if (tgImgExtractFeatureVerifyNRes == 2) {
                    imgFeaNMsg.arg1 = 4;
                } else if (tgImgExtractFeatureVerifyNRes == 3) {
                    imgFeaNMsg.arg1 = 5;
                } else if (tgImgExtractFeatureVerifyNRes == 4) {
                    imgFeaNMsg.arg1 = 6;
                } else if (tgImgExtractFeatureVerifyNRes == 5) {
                    imgFeaNMsg.arg1 = 7;
                } else if (tgImgExtractFeatureVerifyNRes == -1) {
                    imgFeaNMsg.arg1 = 8;
                }
            } else if (tgGetDevImageMatchNRes == -1) {
                imgFeaNMsg.arg1 = -1;
            } else if (tgGetDevImageMatchNRes == -2) {
                imgFeaNMsg.arg1 = -2;
            } else if (tgGetDevImageMatchNRes == -3) {
                imgFeaNMsg.arg1 = -3;
            } else if (tgGetDevImageMatchNRes == -4) {
                imgFeaNMsg.arg1 = -4;
            }
            handler.sendMessage(imgFeaNMsg);
        } else {
            isCheck = true;
        }
    }

    //解绑service
    public void unbindDevService(Context context) {
        context.unbindService(serviceConnection);
    }

    //启动devService
    public void startDevService(Context context) {
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

    //对外传图
    public void img(Message msg, byte[] imgData, int length) {
//        msg.arg1 = 0;
        msg.obj = imgData;
        //传出抓取图片的数据
        Bundle bundle = new Bundle();
        bundle.putInt("imgLength", length);
        bundle.putByteArray("imgData", imgData);
        msg.setData(bundle);
    }

    //存储图片
    public void saveImg(String imgName, byte[] imgData, int imgLength) {
        if (imgName.contains(".dat")) {
            imgName = imgName.substring(0, imgName.indexOf(".dat"));
        }
        String imgPath = this.imgPath + File.separator + imgName + ".jpg";
        File file = new File(this.imgPath);
        if (!file.exists()) file.mkdirs();
        byte[] jpegData = new byte[imgLength];
        System.arraycopy(imgData, 1024 * 256, jpegData, 0, imgLength);
        boolean imgSave = FileUtil.writeFile(jpegData, imgPath);
    }

    @SuppressLint("HandlerLeak")
    private Messenger tg661JMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RECEIVE_MESSAGE_CODE) {
                if (handler != null) {
                    int devServiceArg = msg.arg1;
                    Message tg661JMsg = handler.obtainMessage();
                    tg661JMsg.what = DEV_STATUS;
                    LogUtils.d("接收到的设备状态：" + devServiceArg);
                    if (devServiceArg == 0) {
                        tg661JMsg.arg1 = 1;
                    } else if (devServiceArg == -2) {
                        tg661JMsg.arg1 = -2;
                    }
                    handler.sendMessage(tg661JMsg);
                }
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
//            if (devOpen) {
            //如果设备开启
            Message tg661JMessage = Message.obtain();
            tg661JMessage.what = SEND_MESSAGE_CODE;
            tg661JMessage.obj = getTG661();
            tg661JMessage.replyTo = tg661JMessenger;
            try {
                devServiceMessenger.send(tg661JMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d("===TAG===", "  TG661JAPI向DevService发送信息失败 !");
            }
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            devServiceMessenger = null;
            isStart = false;
        }
    };


}