package com.sd.tgfinger.api;

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

import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.PermissionCallBack;
import com.sd.tgfinger.R;
import com.sd.tgfinger.pojos.FingerFeatureBean;
import com.sd.tgfinger.pojos.FingerImgBean;
import com.sd.tgfinger.pojos.FusionFeatureBean;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.pojos.TaskBean;
import com.sd.tgfinger.pojos.VerifyNBean;
import com.sd.tgfinger.tgApi.Constant;
import com.sd.tgfinger.tgApi.TGFV;
import com.sd.tgfinger.tgApi.TGXG661API;
import com.sd.tgfinger.tgexecutor.TgExecutor;
import com.sd.tgfinger.utils.AudioProvider;
import com.sd.tgfinger.utils.DevRootUtil;
import com.sd.tgfinger.utils.FileUtil;
import com.sd.tgfinger.utils.LogUtils;
import com.sun.jna.ptr.IntByReference;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * Created By pq
 * on 2019/6/23
 * 后比兼容大/小特征算法，替换算法库和特征模板参数长度即可
 *
 * 目前对应的算法版本是  v3.0.0.4
 */
public class TGAPI {

    public static final int OPEN_DEV = 0xf2;
    public static final int CLOSE_DEV = 0xf3;
    public static final int DEV_WORK_MODEL = 0xf8;
    public static final int WAIT_DIALOG = 0xf16;
    public static final int WRITE_FILE = 0xf20;
    public static final int READ_FILE = 0xf21;
    public static final int CANCEL_VERIFY = 0xf22;
    public static final int CANCEL_REGISTER = 0xf27;
    public static final int DEV_STATUS = 0xf28;
    public static final int DEV_IMG = 0xf29;
    public static final int CANCEL_DEV_IMG = 0xf30;
    public static final int INIT_FV = 0xf31;
    public static final int EXTRACT_FEATURE_REGISTER = 0xf32;
    public static final int EXTRACT_FEATURE_VERIFY = 0xf33;
    public static final int FEATURE_COMPARE1_1 = 0xf36;
    public static final int FEATURE_COMPARE1_N = 0xf37;
    public static final int TEMPL_FV_VERSION = 0xf38;
    public static final int TEMPL_SN = 0xf39;
    public static final int TEMPL_FW = 0xf40;
    public static final int TEMPL_TIME = 0xf41;
    public static final int DELETE_HOST_ID_TEMPL = 0xf45;//删除主机指定模板
    public static final int DELETE_HOST_ALL_TEMPL = 0xf46;//删除主机所有模板
    public static final int UPDATE_HOST_TEMPL = 0xf47;//更新主机中的模板
    public static final int SET_DEV_MODEL = 0xf48;//设置设备模式
    public static final int CONTINUE_VERIFY = 0xf51;//连续验证
    public static final int SHUNT_VERIFY = 0xf52;//分流验证
    public static final int RESOLVE_COMPARE_TEMPL = 0xf35;

    public static final String COMPARE_N_TEMPL = "update_templ";//1:N验证的模板
    public static final String INDEX = "index";//模板的索引
    public static final String COMPARE_SCORE = "score";//1:N验证的分数
    public static final String ALONE_VERIFY_N = "alone";//单次1:N验证
    public static final String COMPARE_NAME = "templ_name";//1:N验证的分数
    public static final String FINGER_DATA = "finger_data";//模板数据
    public static final String DATA = "data";//模板数据
    public static final String UPDATE_FINGER = "update_data";//模板数据
    public static final String FINGER_SIZE = "size";//模板数据
    public static final String TN_STR = "tn_str";//模板的附带信息
    //特征模板
    public static final int PERFECT_FEATURE_3 =
//            17632;//大特征的大小
            3248;//3特征  小特征的大小
    public static final int PERFECT_FEATURE_6 =
//            35008;//大特征的大小
            6464;//6特征

    //可比对的特征大小
    public static final int WAIT_COMPARE_FEATURE_6 = 34784;//6特征
    public static final int WAIT_COMPARE_FEATURE_3 = 17408;//3特征

    public static final int IMG_SIZE = 500 * 200 + 208;
    public static final int IMG_W = 500;
    public static final int IMG_H = 200;
    public static final int FEATURE_SIZE =
//            6016;
            2384;
    //UUID的byte占位大小
    public static final int UUID_SIZE = 33;
    //时间值的占位大小
    public static final int TIME_SIZE = 15;

    //临时加的图片大小
    public static final int T_SIZE = 1024 * 500;
    public static final int GET_IMG_OUT_TIME = 15;//默认设置抓图超时的时间为15S
    //public static final int GET_IMG_OUT_TIME_5000 = 5000;//默认设置抓图超时的时间为5000

    //存储数据的根文件夹路径
    private String tgDirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + (File.separator + "TG_TEMPLATE");
    //后比模板的文件夹
    private String behindDatDir = tgDirPath + File.separator + "BehindTemplate";
    //后比的3，6模板路径
    private String behindTempl3Path = behindDatDir + File.separator + "TEMPL_3";
    private String behindTempl6Path = behindDatDir + File.separator + "TEMPL_6";
    //模拟外部数据的存储路径---》后续应该用数据库替代
    public String moniExter3Path = behindDatDir + File.separator + "EX3";
    public String moniExter6Path = behindDatDir + File.separator + "EX6";
    //证书所在的文件夹路径
    private String licenceDir = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "TG_TEMPLATE" + File.separator + "TG_VEIN";

    //证书所在的路径
    private String licensePath = licenceDir + File.separator + "license.dat";
    //日志的路径
    private String logDir = tgDirPath + File.separator + "Log";
    //图片存储的路径
    private String imgPath = tgDirPath + File.separator + "IMGS";
    //3模板
    public static final int TEMPL_MODEL_3 = 3;
    //6模板
    public static final int TEMPL_MODEL_6 = 6;
    //后比模式
    public static final int WORK_BEHIND = 1;
    //SDK的当前版本号
    private static final String SDK_VERSION = "1.2.4_190820_Beta";
    //证书的数据流
    private InputStream inputStream;
    //发送消息的Handler
    private Handler mHandler;
    //这个是ApplicationContext对象
    private Context mContext;
    //当前的activity
    private Activity mActivity;

    private int templModelType;//标记特征模式  3/6
    private int workType = WORK_BEHIND;//默认是后比
    public boolean devOpen = false;//设备是否已经打开
    private int devStatus = -1;//默认设备未开启的状态

    //是否发送图片数据对外显示
    private boolean sImg = false;
    private byte[] aimByte = null;
    private int templIndex = 0;

    //工作的线程池
    private Executor executor;
    //    private ExecutorCompletionService<Object> ecs;
    //标记是否连续验证
    private boolean continueVerify = false;
    //1:N验证 N的基数
    private int verifyBaseCount;
    //标记是否是大特征的模板
    boolean bigFeature = false;

    //获取是否在连续验证
    public boolean isContinueVerify() {
        return continueVerify;
    }

    private TGAPI() {
        //创建线程池
        executor = TgExecutor.getExecutor();
//        ecs = new ExecutorCompletionService<>(executor);
    }

    //获取代理对象
    private static TGAPI tgapi = null;

    public static TGAPI getTGAPI() {
        if (tgapi == null) {
            synchronized (TGAPI.class) {
                if (tgapi == null) {
                    tgapi = new TGAPI();
                }
            }
        }
        return tgapi;
    }

    //获取算法代理对象
    private TGFV getTGFV() {
        return TGFV.TGFV_INSTANCE;
    }

    //获取通信库代理对象
    public static TGXG661API getTG661() {
        return TGXG661API.TGXG_661_API;
    }

    //获取音频对象
    public AudioProvider getAP(Context context) {
        return AudioProvider.getInstance(context);
    }

    //权限：读写文件
    @SuppressLint("InlinedApi")
    private String[] perms = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 设置图片是否发送出去，内部测试用
     *
     * @param sImg
     */
    private void setsImg(boolean sImg) {
        this.sImg = sImg;
    }

    /**
     * 获取SDK的版本
     */
    public String getSDKVersion() {
        return SDK_VERSION;
    }

    /**
     * 初始化:可在Application中调用
     *
     * @param context 1: 初始化成功,算法接口有效
     *                2: 初始化失败,因证书路径错误,算法接口无效
     *                3: 初始化失败,因证书内容无效,算法接口无效
     *                4: 初始化失败,因证书内容过期,算法接口无效
     *                -1:算法初始化失败
     */
    public int init(Context context, InputStream inputStream) {
        this.mContext = context;
        this.inputStream = inputStream;
        //检测设备是否已经root，通信节点初始化，初始化算法
        checkDevIsRoot();
        //CMD();
        return InitLicense(context);
        //work(handler, INIT_FV);
    }

    /**
     * 检查设备是否已经Root
     */
    private void checkDevIsRoot() {
        boolean rootSystem = DevRootUtil.isRootSystem();
        if (rootSystem) {
            LogUtils.d("设备已经Root");
        } else {
            LogUtils.d("设备没有Root，无法使用");
            return;
        }
        createDirPath();
    }

    /**
     * 执行耗时工作
     *
     * @param handler
     * @param flag
     */
    private void work(final Handler handler, final int flag,
                      final TaskBean taskBean) {
        if (executor != null)
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    switch (flag) {
                        case INIT_FV:
                            //算法初始化

                            break;
                        case OPEN_DEV:
                            //打开设备
                            //getThreadName("打开设备");
                            tgOpenDev(handler);
                            break;
                        case CLOSE_DEV:
                            //关闭设备
                            //getThreadName("关闭设备");
                            tgCloseDev(handler);
                            break;
                        case DEV_WORK_MODEL:
                            //getThreadName("获取工作模式");
                            tgGetDevModel(handler);
                            break;
                        case SET_DEV_MODEL:
                            //设置设备的模式
                            //getThreadName("设置工作模式");
                            tgSetDevModel(handler);
                            break;
                        case EXTRACT_FEATURE_REGISTER:
                            //注册
                            //getThreadName("注册");
                            tgDevRegister(handler, taskBean.getTemplData(), taskBean.getFingerSize());
                            break;
                        case CANCEL_REGISTER:
                            //消息注册
                            //getThreadName("消息注册");
                            tgCancelRegister(handler);
                            break;
                        case FEATURE_COMPARE1_1:
                            //1:1验证
                            //getThreadName("1:1验证");
                            tgTempl1_1(handler, taskBean.getTemplData(), sound, bigFeature);
                            break;
                        case FEATURE_COMPARE1_N:
                            //1:N验证
                            //getThreadName("1:N");
                            aloneVerifyN(handler, taskBean.getTemplData(), taskBean.getFingerSize());
                            break;
                        case CONTINUE_VERIFY:
                            //1:N连续验证
                            //getThreadName("1:N连续验证");
                            tgContinueVerify(handler, taskBean.getTemplData(),
                                    taskBean.getFingerSize());
                            break;
                        case SHUNT_VERIFY:
                            //分流比对
                            //getThreadName("分流比对");
                            shuntVerifyMethod(handler, taskBean.getImgFeature(), taskBean.getTemplData()
                                    , taskBean.getFingerSize(), taskBean.getIndex(), taskBean.getCellDataCount()
                                    , sound, taskBean.getType(), bigFeature);
                            break;
                        case DEV_STATUS:
                            //获取设备的链接状态
                            //getThreadName("设备连接状态");
                            tgDevStatus(handler);
                            break;
                        case WRITE_FILE:
                            //写入文件到主机指定路径存储
                            //getThreadName("写入文件");
                            tgSaveFileToHost(handler);
                            break;
                        case READ_FILE:
                            //从主机设备中读取数据
                            //getThreadName("读取数据");
                            tgReadDataFromHost(handler);
                            break;
                        case TEMPL_FV_VERSION:
                            //获取算法的版本
                            tgGetFVVersion(handler, taskBean.getTemplData());
                            break;
                        case TEMPL_SN:
                            //获取模板的SN号
                            tgGetTemplSN(handler, taskBean.getTemplData());
                            break;
                        case TEMPL_FW:
                            //获取模板的FW号
                            tgGetTemplFW(handler, taskBean.getTemplData());
                            break;
                        case TEMPL_TIME:
                            //获取模板生成的时间
                            tgGetTemplTime(handler, taskBean.getTemplData());
                            break;
                    }
                }
            });
    }

    /**
     * 初始化证书
     * 1: 初始化成功,算法接口有效
     * 2: 初始化失败,因证书路径错误,算法接口无效
     * 3: 初始化失败,因证书内容无效,算法接口无效
     * 4: 初始化失败,因证书内容过期,算法接口无效
     * -1:算法初始化失败
     */
    private int InitLicense(Context context) {
        int licenceRes = -1;
        if (inputStream != null) {
            //如果是由网络下发证书流，先写入指定路径的文件
            licenceRes = writeLicenseToFile(inputStream);
        } else {
            //检测指定的文件夹下是否存在证书
            ArrayList<String> licenseList = FileUtil.getInitFinerFileList(licenceDir);
            if (licenseList != null) {
                if (licenseList.size() > 0) {
                    for (int i = 0; i < licenseList.size(); i++) {
                        String name = licenseList.get(i);
                        if (name.equals("license.dat")) {
                            //存在证书历史,初始化算法,调用算法接口初始化算法
                            licenceRes = FV_InitAct();
                            break;
                        } else {
                            if (i == licenseList.size() - 1) {
                                //不存在证书历史，将SDK的证书写入指定文件
                                InputStream LicenseIs = context.getResources().openRawResource(R.raw.license);
                                licenceRes = writeLicenseToFile(LicenseIs);
                            }
                        }
                    }
                } else {
                    //不存在证书，将SDK的证书写入指定文件
                    InputStream LicenseIs = context.getResources().openRawResource(R.raw.license);
                    licenceRes = writeLicenseToFile(LicenseIs);
                }
            }
        }
        return licenceRes;
    }

    //调用算法接口初始化算法
    private int FV_InitAct() {
        int tgInitFVProcessRes = getTGFV().TGInitFVProcess(licensePath);
        if (tgInitFVProcessRes == 0) {
            tgInitFVProcessRes = 1;
        } else if (tgInitFVProcessRes == 1) {
            tgInitFVProcessRes = 2;
        } else if (tgInitFVProcessRes == 2) {
            tgInitFVProcessRes = 3;
        } else if (tgInitFVProcessRes == 3) {
            tgInitFVProcessRes = 4;
        }
        return tgInitFVProcessRes;
    }

    /**
     * 检测设备的连接状态
     * 解绑service
     *
     * @param context
     */
    public void unbindDevService(Context context) {
        context.unbindService(serviceConnection);
    }

    /**
     * 启动devService,监听设备连接的状态
     *
     * @param context
     */
    public void startDevService(Context context) {
        if (context != null) {
            Intent intent = new Intent();
            intent.setAction(Constant.DEV_SERVICE_ACTION);
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

    /**
     * 后比音量加
     */
    public boolean increaseVolume() {
        return getAP(mContext).increaceVolume();
    }

    /**
     * 后比音量减
     */
    public boolean decreaseVolume() {
        return getAP(mContext).decreaseVolume();
    }

    /**
     * 设置音量的大小
     *
     * @param volume 音量值
     */
    public void setVolume(Context context, int volume) {
        getAP(context).setVolume(volume);
    }

    /**
     * 获取当前的音量
     */
    public String getCurrentVolume(Context context) {
        this.mContext = context;
        float currentVolume = getAP(context).getCurrentVolume();
        return String.valueOf(currentVolume);
    }

    /**
     * 获取最大的音量值
     */
    public String getMaxVolume() {
        float streamVolumeMax = getAP(mContext).getStreamVolumeMax();
        return String.valueOf(streamVolumeMax);
    }

    /**
     * 设置3/6特征模式
     *
     * @param templModelType
     */
    //默认是6特征模式
    private boolean isType6 = true;

    public void setTemplModelType(int templModelType) {
        this.templModelType = templModelType;
        if (templModelType == TEMPL_MODEL_6) {
            isType6 = true;
        } else if (templModelType == TEMPL_MODEL_3) {
            isType6 = false;
        }
    }

    /**
     * 设置设备得模式
     * 1：设置成功
     * 3：设置失败，该设备不支持6特征模板注册
     * 4：请先删除设备中的三模板
     * 5：请先删除设备中的六模板
     * -2：设置失败
     * -3 ：入参错误
     */
    public void setDevWorkModel(Handler handler, int workType) {
        this.mHandler = handler;
        this.workType = workType;
        work(mHandler, SET_DEV_MODEL, null);
    }

    /**
     * 获取设备的工作模式
     *
     * @param handler 信使
     */
    public void getDevWorkModel(Handler handler) {
        this.mHandler = handler;
        work(mHandler, DEV_WORK_MODEL, null);
    }

    /**
     * 打开指静脉设备
     *
     * @return -1.1
     * -1:指静脉设备未开启
     * 1:指静脉设备开启成功
     */
    private boolean sound = false;

    public void openDev(Handler handler, int workType, int templModelType, boolean sound) {
        this.continueVerify = true;
        this.isLink = true;
        this.mHandler = handler;
        this.workType = workType;
        this.templModelType = templModelType;
        this.sound = sound;
        if (templModelType == TEMPL_MODEL_6) {
            isType6 = true;
        } else if (templModelType == TEMPL_MODEL_3) {
            isType6 = false;
        }
        writeCMD();
        work(mHandler, OPEN_DEV, null);
    }

    /**
     * 关闭指静脉设备
     *
     * @return
     */
    public void closeDev(Handler handler) {
        this.continueVerify = false;
        this.isLink = false;
        this.mHandler = handler;
        work(mHandler, CLOSE_DEV, null);
    }

    //设备的开启状态
    public boolean isDevOpen() {
        return devOpen;
    }

    /**
     * 获取设备的工作状态
     */
    public void getDevStatus(Handler handler) {
        this.mHandler = handler;
        work(mHandler, DEV_STATUS, null);
    }

    /**
     * 注册
     */
    public void extractFeatureRegister(Handler handler, byte[] templData, int templSize, boolean bigFeature) {
        //检测抓图是否在进行
      /*  if (this.continueVerify){
            tgCancelRegister(handler);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        this.bigFeature = bigFeature;
        this.continueVerify = false;
        this.mHandler = handler;
        work(mHandler, EXTRACT_FEATURE_REGISTER, new TaskBean(templData, templSize, null,
                0, 0, 0));
    }

    /**
     * 取消注册
     */
    public void cancelRegisterGetImg(Handler handler) {
        this.isCancelRegister = true;
        this.mHandler = handler;
        work(mHandler, CANCEL_REGISTER, null);
    }

    /**
     * 写入文件到主机
     */
    private byte[] saveData;
    private String savePath;

    public void saveDataToHost(Handler handler, byte[] templData, String templSavePath) {
        this.saveData = templData;
        this.savePath = templSavePath;
        work(handler, WRITE_FILE, null);
    }

    /**
     * 从主机设备读取数据
     */
    public void readDataFromHost(Handler handler, String dataPath) {
        this.mHandler = handler;
        this.savePath = dataPath;
        work(mHandler, READ_FILE, null);
    }

    /**
     * 1:N验证
     *
     * @param handler
     */
    public void featureCompare1_N(Handler handler, byte[] templData, int templSize, boolean bigFeature) {
        this.mHandler = handler;
        this.bigFeature = bigFeature;
        this.continueVerify = false;
        work(mHandler, FEATURE_COMPARE1_N, new TaskBean(templData, templSize, null,
                0, 0, 0));
    }

    /**
     * 1:N连续验证
     * verifyCount : 验证的基数
     */
    public void continueVerifyN(Handler handler, byte[] templData, int templSize,
                                int verifyCount, boolean bigFeature) {
        this.mHandler = handler;
        this.continueVerify = true;
        this.verifyBaseCount = verifyCount;
        this.bigFeature = bigFeature;
        work(mHandler, CONTINUE_VERIFY, new TaskBean(templData, templSize, null,
                0, 0, 0));
    }

    /**
     * 1:1验证
     * templData 模板数据
     */
    public void featureCompare1_1(Handler handler, byte[] templData, boolean bigFeature) {
        this.mHandler = handler;
        this.bigFeature = bigFeature;
        work(mHandler, FEATURE_COMPARE1_1, new TaskBean(templData, 1, null,
                0, 0, 0));
    }

    /**
     * 获取模板对应的算法的版本
     *
     * @param handler 信使
     */
    public void getTemplVersion(Handler handler, byte[] templData) {
        this.mHandler = handler;
        work(mHandler, TEMPL_FV_VERSION, new TaskBean(templData, 1, null,
                0, 0, 0));
    }

    /**
     * 获取模板的SN序列号
     *
     * @param handler 信使
     */
    public void getTemplSN(Handler handler, byte[] templData) {
        this.mHandler = handler;
        work(mHandler, TEMPL_SN, new TaskBean(templData, 1, null,
                0, 0, 0));
    }

    /**
     * 获取模板的FW固件号
     *
     * @param handler 信使
     */
    public void getTemplFW(Handler handler, byte[] templData) {
        this.mHandler = handler;
        work(mHandler, TEMPL_FW, new TaskBean(templData, 1, null,
                0, 0, 0));
    }

    /**
     * 获取模板对应的时间
     *
     * @param handler 信使
     */
    public void getTemplTime(Handler handler, byte[] templData) {
        this.mHandler = handler;
        work(mHandler, TEMPL_TIME, new TaskBean(templData, 1, null,
                0, 0, 0));
    }

    /**********************  内部接口  ***********************/
    /**
     * 1:设备打开成功，后比设置成功
     * 2:设备打开成功，模式设置失败，该设备不支持6特征模板注册
     * 3:设备打开成功，模式设置失败，请先删除设备中的三模板
     * 4:设备打开成功，模式设置失败，请先删除设备中的六模板
     * -1:设备打开失败
     * -2:设备打开成功，模式设置失败
     * -3:设备打开成功，入参错误
     */
    private void tgOpenDev(Handler handler) {
        IntByReference mode = new IntByReference();
        int openDevRes = getTG661().TGOpenDev(mode);
        if (handler != null) {
            Message openDevMsg = new Message();
            openDevMsg.what = OPEN_DEV;
            if (openDevRes >= 0) {
                //设置工作模式
                devOpen = true;
                openDevMsg.arg1 = 1;
                this.isLink = true;
            } else {
                boolean loopOpen = true;
                int n = 0;
                while (loopOpen) {
                    try {
                        Thread.sleep(100);
                        openDevRes = getTG661().TGOpenDev(mode);
                        n++;
                        if (n >= 9) {
                            if (openDevRes >= 0) {
                                devOpen = true;
                                openDevMsg.arg1 = 1;
                                this.isLink = true;
                            }
                            loopOpen = false;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //设备打开失败
                openDevMsg.arg1 = -1;
            }
            handler.sendMessage(openDevMsg);
        }
    }

    /**
     * 1:指静脉设备关闭成功
     * -1:指静脉设备关闭失败
     */
    private void tgCloseDev(Handler handler) {
        int closeDevRes = getTG661().TGCloseDev();
        if (handler != null) {
            Message closeDevMsg = new Message();
            closeDevMsg.what = CLOSE_DEV;
            devOpen = false;
            if (closeDevRes == 0) {
                //传出关闭成功的结果：
                getAP(mContext).release();//释放声音资源
                devStatus = -1;
                this.isLink = false;
                closeDevMsg.arg1 = 1;
            } else {
                closeDevMsg.arg1 = -1;
            }
            handler.sendMessage(closeDevMsg);
        }
    }

    /**
     * 设置设备的工作模式
     * 1：设置成功
     * 2：设置失败，该设备不支持6特征模板注册
     * 3：请先删除设备中的三模板
     * 4：请先删除设备中的六模板
     * -1：设置失败
     * -2 ：入参错误
     */
    private void tgSetDevModel(Handler handler) {
        int setDevModeRes1 = getTG661().TGSetDevMode(1);
        if (handler != null) {
            Message devModelMsg = new Message();
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
        }
    }

    /**
     * 获取设备的工作模式
     * 0：获取成功  0：前比3特征模板
     * 1：后比
     * 2：前比6特征模板
     * -1：超时
     */
    private void tgGetDevModel(Handler handler) {
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
        if (handler != null) {
            Message devWorkMsg = new Message();
            devWorkMsg.what = DEV_WORK_MODEL;
            if (devWorkModelRes < 0) {
                devWorkMsg.arg1 = -1;
            } else {
                if (ibr.getValue() == 0) {
                    devWorkMsg.arg1 = 1;
                } else if (ibr.getValue() == 1) {
                    devWorkMsg.arg1 = 2;
                } else if (ibr.getValue() == 2) {
                    devWorkMsg.arg1 = 3;
                }
            }
            handler.sendMessage(devWorkMsg);
        }
    }

    /**
     * 查重方法
     * 1:特征提取成功,Output数据有效
     * 2:特征提取失败,因证书路径错误,Output数据无效
     * 3:特征提取失败,因证书内容无效,Output数据无效
     * 4:特征提取失败,因证书内容过期,Output数据无效
     * 5:特征提取失败,因"图像"数据无效,Output数据无效
     * 6:特征提取失败,因"图像"质量较差,Output数据无效
     * 7:模板登记重复
     * -1:特征提取失败,因参数不合法,Output数据无效
     * -2:注册特征提取失败
     * -3:抓图超时
     * -4:设备断开
     * -5:操作取消
     * -6:入参错误
     * -7:抓图未知错误
     * -8:特征比对（1：N）失败，因参数不合法，Output数据无效
     * -9:验证提取特征失败
     */
    private int checkRepeat(Handler handler, byte[] templData, int templSize, boolean bigFeature) {
        //抓图，请自然轻放手指
        int res = 0;
        Message msg = new Message();
        msg.what = EXTRACT_FEATURE_REGISTER;
        if (this.isLink)
            getAP(mContext).play_inputDownGently();
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x20);
        int imgResultCode = fingerImgBean.getImgResultCode();
        Log.d("===KKK", "  返回查重抓图的结果" + imgResultCode);
        if (imgResultCode >= 0) {
            byte[] imgData = fingerImgBean.getImgData();
            //int imgDataLength = fingerImgBean.getImgDataLength();
            //提取图片的特征
            FingerFeatureBean fingerFeatureBean = extractImgFeatureVerify(imgData);
            int featureResult = fingerFeatureBean.getFeatureResult();
            if (featureResult == 1) {
                //提取特征成功
                byte[] fingerFeatureData = fingerFeatureBean.getFingerFeatureData();
                //1:N验证，查重
                VerifyNBean verifyNBean;
                if (bigFeature) {
                    //如果是大特征模板，首先要解析模板
                    byte[] matchData = resolveAllTempl(templData, templSize);
                    verifyNBean = tgTempl1_N(fingerFeatureData, matchData, templSize);
                } else {
                    verifyNBean = tgTempl1_N(fingerFeatureData, templData, templSize);
                }
                int res1N = verifyNBean.getVerifyNResult();
                if (res1N == 1) {
                    //验证成功，模板登记重复
                    res = 7;
                    if (this.isLink)
                        getAP(mContext).play_registerRepeat();
                } else if (res1N == 2) {
                    //模板尚未注册--->注册,提取注册特征
                    FingerFeatureBean fingerFeatureBeanRegister = extractImgFeature(imgData);
                    res = fingerFeatureBeanRegister.getFeatureResult();
                    if (res == 1) {
                        //特征提取成功
                        byte[] fingerFeatureDataRegister = fingerFeatureBeanRegister
                                .getFingerFeatureData();
                        //拼接缓存
                        templIndex = 0;
                        jointTempl(fingerFeatureDataRegister);
                        //存储图片
//                        if (sImg) {
//                            tgSaveImg()
//                        }
                    }
                } else if (res1N == -1) {
                    //特征比对（1：N）失败，因参数不合法，Output数据无效
                    res = -8;
                }
            } else {
                //验证提取特征失败
                res = -9;
            }
        } else if (imgResultCode == -1) {
            //抓图超时
            res = -3;
        } else if (imgResultCode == -2) {
            //设备断开
            res = -4;
            this.isLink = false;
        } else if (imgResultCode == -3) {
            //操作取消
            res = -5;
        } else if (imgResultCode == -4) {
            //入参错误
            res = -6;
        } else if (imgResultCode == -5) {
            //抓图未知错误
            res = -7;
        }
        msg.arg1 = res;
        if (handler != null) {
            handler.sendMessage(msg);
        }
        return res;
    }

    //抓取图片
    private FingerImgBean tgDevGetFingerImg(int soundType) {
        byte[] imgData;
        if (sImg) {
            imgData = new byte[IMG_SIZE + T_SIZE];
            imgData[0] = ((byte) 0xfe);
        } else {
            imgData = new byte[IMG_SIZE];
        }
        //抓图
        int DevImageLength = getTG661().TGGetDevImage(imgData, GET_IMG_OUT_TIME);
        if (DevImageLength >= 0) {
            return new FingerImgBean(imgData, DevImageLength, 1);
        } else if (DevImageLength == -1) {
            if (soundType == 0x20) {
                if (sound && this.isLink) {
                    isCancelRegister = true;
                    getAP(mContext).play_time_out();
                }
            }
            return new FingerImgBean(null, -1, -1);
        } else if (DevImageLength == -2) {
//            if (sound && this.isLink) {
//                getAP(mContext).play_verifyFail();
//            }
            //LogUtils.d("抓图   设备断开");
            return new FingerImgBean(null, -1, -2);
        } else if (DevImageLength == -3) {
            return new FingerImgBean(null, -1, -3);
        } else if (DevImageLength == -4) {
//            if (sound && this.isLink) {
//                getAP(mContext).play_verifyFail();
//            }
            //LogUtils.d("抓图   参数错误");
            return new FingerImgBean(null, -1, -4);
        } else {
            return new FingerImgBean(null, -1, -5);
        }
    }

    //提取指静脉图像的特征--->注册专用（提取特征）
    private FingerFeatureBean extractImgFeature(byte[] fingerImgData) {
        byte[] fingerFeature = new byte[FEATURE_SIZE];
        int extractFeatureRes = getTGFV().TGImgExtractFeatureRegister(fingerImgData,
                IMG_W, IMG_H, fingerFeature);
        if (extractFeatureRes == 0) {
            return new FingerFeatureBean(1, fingerFeature);
        } else if (extractFeatureRes == 1) {
            return new FingerFeatureBean(2, null);
        } else if (extractFeatureRes == 2) {
            return new FingerFeatureBean(3, null);
        } else if (extractFeatureRes == 3) {
            return new FingerFeatureBean(4, null);
        } else if (extractFeatureRes == 4) {
            return new FingerFeatureBean(5, null);
        } else if (extractFeatureRes == 5) {
            return new FingerFeatureBean(6, null);
        } else if (extractFeatureRes == -1) {
            return new FingerFeatureBean(-1, null);
        } else {
            return new FingerFeatureBean(-2, null);
        }
    }

    //验证专用---->提取特征
    private FingerFeatureBean extractImgFeatureVerify(byte[] fingerImgData) {
        byte[] fingerFeature = new byte[FEATURE_SIZE];
        int extractFeatureRes = getTGFV().TGImgExtractFeatureVerify(fingerImgData,
                IMG_W, IMG_H, fingerFeature);
        if (extractFeatureRes == 0) {
            return new FingerFeatureBean(1, fingerFeature);
        } else if (extractFeatureRes == 1) {
            return new FingerFeatureBean(2, null);
        } else if (extractFeatureRes == 2) {
            return new FingerFeatureBean(3, null);
        } else if (extractFeatureRes == 3) {
            return new FingerFeatureBean(4, null);
        } else if (extractFeatureRes == 4) {
            return new FingerFeatureBean(5, null);
        } else if (extractFeatureRes == 5) {
            return new FingerFeatureBean(6, null);
        } else if (extractFeatureRes == -1) {
            return new FingerFeatureBean(-1, null);
        } else {
            return new FingerFeatureBean(-2, null);
        }
    }

    //特征融合
    private FusionFeatureBean fusionFeature(byte[] features, int featureSize) {
        byte[] fusionTempl = perfectTemplData();
        int fusionRes = getTGFV().TGFeaturesFusionTmpl(features,
                featureSize, fusionTempl);
        if (fusionRes == 0) {
            fusionRes = 1;
        }
        return new FusionFeatureBean(fusionRes, fusionTempl);
    }

    //拼接缓存模板
    private byte[] jointTempl(byte[] newFeature) {
        if (aimByte == null) {
            if (templModelType == TEMPL_MODEL_3) {
                aimByte = new byte[FEATURE_SIZE * templModelType];
            } else if (templModelType == TEMPL_MODEL_6) {
                aimByte = new byte[FEATURE_SIZE * templModelType];
            }
        }
        int length = templIndex * FEATURE_SIZE;
        System.arraycopy(newFeature, 0, aimByte, length, newFeature.length);
        templIndex++;
        return aimByte;
    }

    /**
     * 1:N连续验证
     */
    public void tgContinueVerify(Handler handler, byte[] fingerTemplData, int fingerSize) {
        if (continueVerify && fingerTemplData != null && fingerSize > 0) {
            this.mHandler = handler;
            this.aloneVerifyN = false;
            //抓图
            if (isType6) {
                tgFinger1N(mHandler, fingerTemplData, fingerSize);
            } else {
                tgFinger1N_3(mHandler, fingerTemplData, fingerSize);
            }
        }
    }

    /**
     * 单独1:N验证
     *
     * @param handler 信使
     */
    private void aloneVerifyN(Handler handler, byte[] fingerTemplData, int fingerSize) {
        if (!continueVerify && fingerTemplData != null && fingerSize > 0) {
            this.mHandler = handler;
            this.aloneVerifyN = true;
            if (isType6) {
                tgAloneFinger1N(mHandler, fingerTemplData, fingerSize, sound);
            } else {
                tgAloneFinger1N_3(mHandler, fingerTemplData, fingerSize, sound);
            }
        }
    }

    //每一个比对单元verifyBaseCount的数据存储空间
    private byte[] cellDataSpace(int size) {
        byte[] cellFingerData = null;
        if (templModelType == TEMPL_MODEL_3) {
            cellFingerData = new byte[size * PERFECT_FEATURE_3];
        } else if (templModelType == TEMPL_MODEL_6) {
            cellFingerData = new byte[size * PERFECT_FEATURE_6];
        }
        return cellFingerData;
    }

    //截取的起始位置
    private int startPos(int verifyBaseCount, int index) {
        if (templModelType == TEMPL_MODEL_3) {
            return verifyBaseCount * PERFECT_FEATURE_3 * index;
        } else if (templModelType == TEMPL_MODEL_6) {
            return verifyBaseCount * PERFECT_FEATURE_6 * index;
        } else {
            return 0;
        }
    }

    /**
     * 1:N指静脉模板验证
     * 1:N接口的参数含义：
     * handler  信使
     * fingerTemplData  要比对的模板数据的数组
     * fingerSize  要比对的模板数据的数量
     * sound 是否播放声音
     * continueVerifyType 验证的类型：0x11 单次的1:N验证   0x12 连续的1:N验证
     * index 连续1:N验证时，单元模板数据组在整个模板数据组中的指针，单元模板数据组的大小为verifyBaseCount
     * cellDataCount 连续1:N验证时，单元模板数据组的数量
     *
     * @param fingerSize 1:验证成功,Output数据有效
     * -1:特征比对（1：N）失败，因参数不合法，Output数据无效
     * -2:特征比对（1：N）失败，仅Output的matchScore数据有效
     * -3:抓图超时
     * -4:设备断开
     * -5:操作取消
     * -6:入参错误
     * -7:抓图未知错误
     * -8:验证提取特征失败
     */
    private boolean equalData = false;
    private boolean aloneVerifyN = false;

    private void tgFinger1N(Handler handler, byte[] fingerTemplData, int fingerSize) {
        if (!this.isLink || !this.continueVerify || !isType6) return;
        Message message = new Message();
        Bundle bundle = new Bundle();
        message.what = CONTINUE_VERIFY;
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x21);
        int imgResultCode = fingerImgBean.getImgResultCode();
        int res = 0;
        if (imgResultCode >= 0) {
            byte[] imgData = fingerImgBean.getImgData();
            //int imgDataLength = fingerImgBean.getImgDataLength();
            //提取图片的特征
            FingerFeatureBean fingerFeatureBean = extractImgFeatureVerify(imgData);
            int featureResult = fingerFeatureBean.getFeatureResult();
            if (featureResult == 1) {
                //提取特征成功
                byte[] fingerFeatureData = fingerFeatureBean.getFingerFeatureData();
                //1:N验证
                //此处才能分流
                shuntNVerify(handler, fingerFeatureData, fingerTemplData, fingerSize, 10);
            } else {
                //验证提取特征失败
                res = -8;
            }
        } else if (imgResultCode == -1) {
            if (!this.continueVerify)
                //抓图超时
                res = -3;
        } else if (imgResultCode == -2) {
            //设备断开
            res = -4;
            this.isLink = false;
        } else if (imgResultCode == -3) {
            if (!continueVerify) {
                //操作取消
                res = -5;
            }
        } else if (imgResultCode == -4) {
            //入参错误
            res = -6;
        } else if (imgResultCode == -5) {
            //抓图未知错误
            res = -7;
        }
        if (res != 0) {
            message.arg1 = res;
            message.setData(bundle);
            if (handler != null) {
                handler.sendMessage(message);
            }
        }
        if (this.continueVerify && this.isLink) {
            try {
                Thread.sleep(150);
                tgFinger1N(handler, fingerTemplData, fingerSize);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //3特征模式下1:N验证
    private void tgFinger1N_3(Handler handler, byte[] fingerTemplData, int fingerSize) {
        if (!this.isLink || !this.continueVerify || isType6) return;
        Message message = new Message();
        Bundle bundle = new Bundle();
        message.what = CONTINUE_VERIFY;
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x21);
        int imgResultCode = fingerImgBean.getImgResultCode();
        int res = 0;
        if (imgResultCode >= 0) {
            byte[] imgData = fingerImgBean.getImgData();
            //int imgDataLength = fingerImgBean.getImgDataLength();
            //提取图片的特征
            FingerFeatureBean fingerFeatureBean = extractImgFeatureVerify(imgData);
            int featureResult = fingerFeatureBean.getFeatureResult();
            if (featureResult == 1) {
                //提取特征成功
                byte[] fingerFeatureData = fingerFeatureBean.getFingerFeatureData();
                //1:N验证
                //此处才能分流
                shuntNVerify(handler, fingerFeatureData, fingerTemplData, fingerSize, 10);
            } else {
                //验证提取特征失败
                res = -8;
            }
        } else if (imgResultCode == -1) {
            if (!this.continueVerify) {
                //抓图超时
                res = -3;
            }
        } else if (imgResultCode == -2) {
            //设备断开
            res = -4;
            this.isLink = false;
        } else if (imgResultCode == -3) {
            if (!continueVerify) {
                //操作取消
                res = -5;
            }
        } else if (imgResultCode == -4) {
            //入参错误
            res = -6;
        } else if (imgResultCode == -5) {
            //抓图未知错误
            res = -7;
        }
        if (res != 0) {
            message.arg1 = res;
            message.setData(bundle);
            if (handler != null) {
                handler.sendMessage(message);
            }
        }
        if (this.continueVerify && this.isLink) {
            try {
                Thread.sleep(150);
                tgFinger1N_3(handler, fingerTemplData, fingerSize);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /******************************单次1:N与连续1:N接口请勿同时使用********************************/
    private void tgAloneFinger1N(Handler handler, byte[] fingerTemplData, int fingerSize,
                                 boolean sound) {
        //3特征模式的话
        if (!isLink) return;
        if (!isType6) return;
        Message message = new Message();
        Bundle bundle = new Bundle();
        message.what = FEATURE_COMPARE1_N;
        if (sound && this.isLink) {
            getAP(mContext).play_inputDownGently();
        }
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x21);
        int imgResultCode = fingerImgBean.getImgResultCode();
        int res = 0;
        if (imgResultCode >= 0) {
            byte[] imgData = fingerImgBean.getImgData();
            //int imgDataLength = fingerImgBean.getImgDataLength();
            //提取图片的特征
            FingerFeatureBean fingerFeatureBean = extractImgFeatureVerify(imgData);
            int featureResult = fingerFeatureBean.getFeatureResult();
            if (featureResult == 1) {
                //提取特征成功
                byte[] fingerFeatureData = fingerFeatureBean.getFingerFeatureData();
                //1:N验证
                //此处才能分流
                shuntNVerify(handler, fingerFeatureData, fingerTemplData, fingerSize, 5);
            } else {
                //验证提取特征失败
                res = -8;
            }
        } else if (imgResultCode == -1) {
            //抓图超时
            res = -3;
        } else if (imgResultCode == -2) {
            //设备断开
            res = -4;
            this.isLink = false;
        } else if (imgResultCode == -3) {
            //操作取消
            //res = -5;
        } else if (imgResultCode == -4) {
            //入参错误
            res = -6;
        } else if (imgResultCode == -5) {
            //抓图未知错误
            res = -7;
        }
        if (res != 0) {
            message.arg1 = res;
            message.setData(bundle);
            if (handler != null) {
                handler.sendMessage(message);
            }
        }
    }

    //3特征模式下1:N验证
    private void tgAloneFinger1N_3(Handler handler, byte[] fingerTemplData, int fingerSize,
                                   boolean sound) {
        //3特征模式的话
        if (!isLink) return;
        if (isType6) return;
        Message message = new Message();
        Bundle bundle = new Bundle();
        message.what = FEATURE_COMPARE1_N;
        if (sound && this.isLink) {
            getAP(mContext).play_inputDownGently();
        }
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x21);
        int imgResultCode = fingerImgBean.getImgResultCode();
        int res = 0;
        if (imgResultCode >= 0) {
            byte[] imgData = fingerImgBean.getImgData();
            //int imgDataLength = fingerImgBean.getImgDataLength();
            //提取图片的特征
            FingerFeatureBean fingerFeatureBean = extractImgFeatureVerify(imgData);
            int featureResult = fingerFeatureBean.getFeatureResult();
            if (featureResult == 1) {
                //提取特征成功
                byte[] fingerFeatureData = fingerFeatureBean.getFingerFeatureData();
                //1:N验证
                //此处才能分流
                shuntNVerify(handler, fingerFeatureData, fingerTemplData, fingerSize, 5);
            } else {
                //验证提取特征失败
                res = -8;
            }
        } else if (imgResultCode == -1) {
            //抓图超时
            res = -3;
        } else if (imgResultCode == -2) {
            //设备断开
            res = -4;
            this.isLink = false;
        } else if (imgResultCode == -3) {
            //操作取消
            //res = -5;
        } else if (imgResultCode == -4) {
            //入参错误
            res = -6;
        } else if (imgResultCode == -5) {
            //抓图未知错误
            res = -7;
        }
        if (res != 0) {
            message.arg1 = res;
            message.setData(bundle);
            if (handler != null) {
                handler.sendMessage(message);
            }
        }
    }

    /**
     * 分流比对
     *
     * @param handler
     * @param fingerFeatureData
     * @param fingerTemplData
     * @param fingerSize
     * @param type              单次1:N验证  5   连续1:N验证   10
     */
    private void shuntNVerify(Handler handler, byte[] fingerFeatureData, byte[] fingerTemplData
            , int fingerSize, int type) {
        if (verifyBaseCount == 0) {
            this.verifyBaseCount = 3500;
        }
        if (fingerSize > 0 && verifyBaseCount > 0) {
            double v = (double) fingerSize / verifyBaseCount;
            //四舍五入向上取整，1：N会比对的次数, 整数对上对下取整都是自身
            int ceil = (int) Math.ceil(v);
            //四舍五入向下取整
            int floor = (int) Math.floor(v);
            int count;
            if (ceil != floor) {
                count = ceil;
            } else {
                //如果ceil和floor相等，则正好除尽
                count = floor;
            }
            if (count > 0) {
                //初始化默认没有相同模板数据
                this.equalData = false;
                this.n = -1;
                for (int i = 0; i < count; i++) {
                    int compareFingerCount;
                    if (i == floor) {
                        //除不尽，最后一项
                        compareFingerCount = fingerSize - verifyBaseCount * i;
                    } else {
                        compareFingerCount = verifyBaseCount;
                    }
                    if (compareFingerCount > 0) {
                        byte[] cellFingerData = cellDataSpace(compareFingerCount);
                        int startPos = startPos(verifyBaseCount, i);
                        if (cellFingerData != null) {
                            System.arraycopy(fingerTemplData, startPos, cellFingerData,
                                    0, cellFingerData.length);
                            //进行单元数据组的1:N比对
                            work(handler, SHUNT_VERIFY, new TaskBean(cellFingerData, compareFingerCount
                                    , fingerFeatureData, i, (count - 1), type));
                        }
                    }
                }
            }
        }
    }

    //标记验证失败情况下，比对线路完成的次数。只用当所有的比对线路都得出结果，才能获知比对失败的结果
    private int n = -1;

    /**
     * 分流比对的实体
     *
     * @param fingerFeatureData
     * @param fingerTemplData
     * @param fingerSize
     * @param index             返回可更新的模板下标
     * @param cellDataCount
     * @param sound
     * @param type
     * @param bigFeature
     */
    private void shuntVerifyMethod(Handler handler, byte[] fingerFeatureData,
                                   byte[] fingerTemplData, int fingerSize, int index,
                                   int cellDataCount, boolean sound, int type, boolean bigFeature) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        if (type == 10) {
            message.what = CONTINUE_VERIFY;
        } else if (type == 5) {
            message.what = FEATURE_COMPARE1_N;
        }
        int res = 0;
        VerifyNBean verifyNBean;
        if (bigFeature) {
            //如果是大特征模板，首先要解析模板
            byte[] matchData = resolveAllTempl(fingerTemplData, fingerSize);
            verifyNBean = tgTempl1_N(fingerFeatureData, matchData, fingerSize);
        } else {
            //小特征
            verifyNBean = tgTempl1_N(fingerFeatureData, fingerTemplData, fingerSize);
        }
        int res1N = verifyNBean.getVerifyNResult();
        if (res1N == 1) {
            //验证成功
            res = 1;
            //验证的分数
            int score = verifyNBean.getVerifyScore();
            //验证的模板位置
            int fingerIndex = verifyNBean.getFingerIndex();
            //可更新的模板数据
            byte[] updateFingerData = verifyNBean.getUpdateFingerData();
            bundle.putInt(COMPARE_SCORE, score);
            bundle.putInt(INDEX, (fingerIndex - 1));
            bundle.putByteArray(UPDATE_FINGER, updateFingerData);
            if (sound && this.isLink) {
                getAP(mContext).play_verifySuccess();
            }
        } else if (res1N == 2) {
            //验证失败，分数不及格
            n++;
            res = -2;
            //验证的分数
            int score = verifyNBean.getVerifyScore();
            bundle.putInt(COMPARE_SCORE, score);
            if (!equalData && sound && n == cellDataCount && this.isLink) {
                getAP(mContext).play_verifyFail();
            }
        } else if (res1N == -1) {
            //特征比对（1：N）失败，因参数不合法，Output数据无效
            res = -1;
            if (!equalData && sound && this.isLink) {
                getAP(mContext).play_verifyFail();
            }
        }
        if (res == 1) {
            //存在相同的模板
            this.equalData = true;
            message.arg1 = res;
            if (aloneVerifyN) {
                //单一1:N验证
                bundle.putBoolean(ALONE_VERIFY_N, true);
            }
            message.setData(bundle);
            if (handler != null) {
                handler.sendMessage(message);
            }
        } else if (!equalData && n == cellDataCount) {
            //不存在相同的模板，并且所有模板数据已经比对完成
            message.arg1 = res;
            message.setData(bundle);
            if (handler != null) {
                handler.sendMessage(message);
            }
        }
    }

    //1:N验证抽取类
    private VerifyNBean tgTempl1_N(byte[] fingerFeature, byte[] fingerTemplData,
                                   int fingerSize) {
        IntByReference intB1 = new IntByReference();
        IntByReference intB2 = new IntByReference();
        byte[] uuId = new byte[UUID_SIZE];
        byte[] updateTempl = perfectTemplData();
        int match1NRes = getTGFV().TGFeatureMatchTmpl1N(fingerFeature, fingerTemplData,
                fingerSize, intB1, /*uuId,*/ intB2, updateTempl);
        if (match1NRes == 0) {
            match1NRes = 1;
        } else if (match1NRes == 8) {
            match1NRes = 2;
        }
        return new VerifyNBean(match1NRes, intB2.getValue(), intB1.getValue(), updateTempl);
    }

    /**
     * 1:1验证的方法实体
     *
     * @param handler   信使
     * @param templData 指静脉数据模板
     * @param sound     是否播放声音
     *                  1:特征比对（1:1）成功，Output数据有效
     *                  2:特征比对（1:1）失败，因比对失败,仅Output的matchScore数据有效
     *                  -1:特征比对（1:1）失败，因参数不合法,Output数据无效
     *                  -2:注册特征提取失败
     *                  -3:抓图超时
     *                  -4:设备断开
     *                  -5:操作取消
     *                  -6:入参错误
     *                  -7:未知错误
     *                  -8:特征比对（1：N）失败，因参数不合法，Output数据无效
     *                  -9:验证提取特征失败
     *                  -10:登记失败
     */
    private void tgTempl1_1(Handler handler, byte[] templData, boolean sound, boolean bigFeature) {
        Message message = new Message();
        message.what = FEATURE_COMPARE1_1;
        Bundle bundle = new Bundle();
        if (this.isLink)
            getAP(mContext).play_inputDownGently();
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x20);
        int imgResultCode = fingerImgBean.getImgResultCode();
        int res = 0;
        if (imgResultCode >= 0) {
            byte[] imgData = fingerImgBean.getImgData();
            //int imgDataLength = fingerImgBean.getImgDataLength();
            //提取图片的特征
            FingerFeatureBean fingerFeatureBean = extractImgFeatureVerify(imgData);
            int featureResult = fingerFeatureBean.getFeatureResult();
            if (featureResult == 1) {
                //提取特征成功
                byte[] fingerFeatureData = fingerFeatureBean.getFingerFeatureData();
                //1:1验证
                IntByReference intByReference = new IntByReference();
                byte[] updateTempl = perfectTemplData();
                int match1Res;
                if (bigFeature) {
                    //如果是大特征模板，首先要解析模板
                    byte[] matchData = resolveAllTempl(templData, 1);
                    match1Res = getTGFV().TGFeatureMatchTmpl11(fingerFeatureData, matchData,
                            updateTempl, intByReference);
                } else {
                    match1Res = getTGFV().TGFeatureMatchTmpl11(fingerFeatureData, templData,
                            updateTempl, intByReference);
                }
                if (match1Res == 0) {
                    res = 1;
                    //1:1验证分数
                    int score = intByReference.getValue();
                    bundle.putInt(COMPARE_SCORE, score);
                    bundle.putByteArray(UPDATE_FINGER, updateTempl);
                    if (sound && this.isLink) {
                        getAP(mContext).play_verifySuccess();
                    }
                } else if (match1Res == 7) {
                    res = 2;
                    //1:1验证分数
                    int score = intByReference.getValue();
                    bundle.putInt(COMPARE_SCORE, score);
                    if (sound && this.isLink) {
                        getAP(mContext).play_verifyFail();
                    }
                } else if (match1Res == -1) {
                    res = -1;
                    if (sound && this.isLink) {
                        getAP(mContext).play_verifyFail();
                    }
                }
            } else {
                //验证提取特征失败
                res = -8;
            }
        } else if (imgResultCode == -1) {
            //抓图超时
            res = -2;
            if (sound && this.isLink) {
                getAP(mContext).play_time_out();
            }
        } else if (imgResultCode == -2) {
            //设备断开
            res = -3;
        } else if (imgResultCode == -3) {
            //操作取消
            res = -4;
        } else if (imgResultCode == -4) {
            //入参错误
            res = -5;
        } else if (imgResultCode == -5) {
            //未知错误
            res = -6;
        }
        message.arg1 = res;
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    //特征融合完成登记的方法
    private void registerFinger(int templTypeSize, int arg, Handler handler,
                                Message message, Bundle bundle, boolean sound) {
        for (int i = 0; i < templTypeSize; i++) {
            //取消注册的终止跳出符
            if (isCancelRegister) {
                break;
            }
            if (templIndex == 0 && this.isLink) {
                getAP(mContext).play_inputDownGently();
            } else if (templIndex > 0 && this.isLink) {
                getAP(mContext).play_inputAgain();
            }
            //取消注册的终止跳出符
            if (isCancelRegister) {
                break;
            }
            FingerImgBean fingerImgBean = tgDevGetFingerImg(0x20);
            int imgResultCode = fingerImgBean.getImgResultCode();
            if (imgResultCode >= 0) {
                byte[] imgData = fingerImgBean.getImgData();
                //int imgDataLength = fingerImgBean.getImgDataLength();
                FingerFeatureBean fingerFeatureBeanRegister = extractImgFeature(imgData);
                arg = fingerFeatureBeanRegister.getFeatureResult();
                if (arg == 1) {
                    //提取成功
                    byte[] jointTempl = jointTempl(fingerFeatureBeanRegister.getFingerFeatureData());
                    int size = templModelType == TEMPL_MODEL_6 ? 6 : 3;
                    if (templIndex == size) {
                        //特征融合
                        FusionFeatureBean fusionFeatureBean = fusionFeature(jointTempl, size);
                        int fusionResult = fusionFeatureBean.getFusionResult();
                        if (fusionResult == 1) {
                            //登记成功
                            byte[] fusionTempl = fusionFeatureBean.getFusionTempl();
                            arg = 8;
                            bundle.putByteArray(FINGER_DATA, fusionTempl);
                            if (sound && this.isLink) {
                                getAP(mContext).play_checkInSuccess();
                            }
                        } else if (fusionResult == 6) {
                            //特征融合失败，因"特征"数据一致性差，Output数据无效
                            arg = 9;
                            if (sound && this.isLink) {
                                getAP(mContext).play_checkInFail();
                            }
                        } else if (fusionResult == -1) {
                            //登记失败
                            arg = -10;
                            if (sound && this.isLink) {
                                getAP(mContext).play_checkInFail();
                            }
                        }
                    }
                }
            } else if (imgResultCode == -1) {
                //抓图超时
                arg = -3;
                if (sound && this.isLink) {
                    getAP(mContext).play_time_out();
                }
            } else if (imgResultCode == -2) {
                //设备断开
                arg = -4;
                this.isLink = false;
            } else if (imgResultCode == -3) {
                //操作取消
                arg = -5;
            } else if (imgResultCode == -4) {
                //入参错误
                arg = -6;
                if (sound && this.isLink) {
                    getAP(mContext).play_checkInFail();
                }
            } else if (imgResultCode == -5) {
                //未知错误
                arg = -7;
                if (sound && this.isLink) {
                    getAP(mContext).play_checkInFail();
                }
            }
        }
        message.arg1 = arg;
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    /**
     * 取消抓图接口
     */
    private boolean isCancelRegister = false;

    private void tgCancelRegister(Handler handler) {
        int tgCancelRegisterRes = getTG661().TGCancelGetImage();
        Message cancelRegisterMsg = new Message();
        cancelRegisterMsg.what = CANCEL_REGISTER;
        if (tgCancelRegisterRes == 0) {
            this.isCancelRegister = true;
            cancelRegisterMsg.arg1 = 1;
            setTemplModelType(templModelType);
            aimByte = null;
        } else {
            cancelRegisterMsg.arg1 = -1;
        }
        if (handler != null)
            handler.sendMessage(cancelRegisterMsg);
    }

    /**
     * 获取设备的连接状态
     */
    private void tgDevStatus(Handler handler) {
        devStatus = getTG661().TGGetDevStatus();
        //传递出设备的链接状态
        Message devStatusMsg = new Message();
        devStatusMsg.what = DEV_STATUS;
        if (devStatus >= 0) {
            devStatusMsg.arg1 = 1;
        } else {
            devStatusMsg.arg1 = -1;
        }
        if (handler != null) {
            handler.sendMessage(devStatusMsg);
        }
    }

    /**
     * 注册
     * 1:特征提取成功,Output数据有效
     * 2:特征提取失败,因证书路径错误,Output数据无效
     * 3:特征提取失败,因证书内容无效,Output数据无效
     * 4:特征提取失败,因证书内容过期,Output数据无效
     * 5:特征提取失败,因"图像"数据无效,Output数据无效
     * 6:特征提取失败,因"图像"质量较差,Output数据无效
     * 7:模板登记重复
     * 8:登记成功
     * 9:特征融合失败，因"特征"数据一致性差，Output数据无效
     * -1:特征提取失败,因参数不合法,Output数据无效
     * -2:注册特征提取失败
     * -3:抓图超时
     * -4:设备断开
     * -5:操作取消
     * -6:入参错误
     * -7:抓图未知错误
     * -8:特征比对（1：N）失败，因参数不合法，Output数据无效
     * -9:验证提取特征失败
     * -10:登记失败
     */
    private void tgDevRegister(Handler handler, byte[] templData, int templSize) {
        if (continueVerify)
            return;
        //首先检查主机中已注册的模板文件名是否已存在
        Message registerMsg = new Message();
        registerMsg.what = EXTRACT_FEATURE_REGISTER;
        Bundle bundle = new Bundle();
        //注册前核对当前指静脉是否已经注册,模板数量为0，不查重直接注册
        this.isCancelRegister = false;
        int arg = 0;

        Log.d("===KKK", "  continueVerify: " + continueVerify);

        if (templData != null && templSize > 0) {
            //查重
            int res = checkRepeat(handler, templData, templSize, bigFeature);
            if (res == 1) {
                if (templModelType == TEMPL_MODEL_3) {
                    registerFinger(TEMPL_MODEL_3 - 1, arg, handler, registerMsg, bundle, sound);
                } else if (templModelType == TEMPL_MODEL_6) {
                    registerFinger(TEMPL_MODEL_6 - 1, arg, handler, registerMsg, bundle, sound);
                }
                templIndex = 0;
                aimByte = null;
            }
        } else {
            if (templModelType == TEMPL_MODEL_3) {
                registerFinger(TEMPL_MODEL_3, arg, handler, registerMsg, bundle, sound);
            } else if (templModelType == TEMPL_MODEL_6) {
                registerFinger(TEMPL_MODEL_6, arg, handler, registerMsg, bundle, sound);
            }
            templIndex = 0;
            aimByte = null;
        }
    }

    //获取完整模板的大小
    private byte[] perfectTemplData() {
        byte[] updateTempl = null;
        if (templModelType == TEMPL_MODEL_3) {
            updateTempl = new byte[PERFECT_FEATURE_3];
        } else if (templModelType == TEMPL_MODEL_6) {
            updateTempl = new byte[PERFECT_FEATURE_6];
        }
        return updateTempl;
    }

    //缓存解析后的模板
    private byte[] comparableTemplData(int featureSize) {
        byte[] matchData = null;
        if (templModelType == TEMPL_MODEL_3) {
            matchData = new byte[WAIT_COMPARE_FEATURE_3 * featureSize];
        } else if (templModelType == TEMPL_MODEL_6) {
            matchData = new byte[WAIT_COMPARE_FEATURE_6 * featureSize];
        }
        return matchData;
    }

    //模板的数据长度缓存
    private byte[] templSizeData(int templCount) {
        byte[] allFingerData = null;
        if (templModelType == TEMPL_MODEL_3) {
            allFingerData = new byte[PERFECT_FEATURE_3 * templCount];
        } else if (templModelType == TEMPL_MODEL_6) {
            allFingerData = new byte[PERFECT_FEATURE_6 * templCount];
        }
        return allFingerData;
    }

    //存入文件到主机
    private void tgSaveFileToHost(Handler handler) {
        if (this.saveData != null && this.saveData.length > 0 && !TextUtils.isEmpty(this.savePath)) {
            boolean writeFile = FileUtil.writeFile(this.saveData, this.savePath);
            Message msg = new Message();
            msg.what = WRITE_FILE;
            if (writeFile) {
                msg.arg1 = 1;
            } else {
                msg.arg1 = -1;
            }
            handler.sendMessage(msg);
        }
    }

    //所有模板全部解析，执行
    public byte[] resolveAllTempl(byte[] fingerTemplData, int fingerSize) {
        byte[] matchData = comparableTemplData(fingerSize);
        for (int i = 0; i < fingerSize; i++) {
            //将模板一一解析
            byte[] finger = null;
            if (templModelType == TEMPL_MODEL_3) {
                finger = new byte[PERFECT_FEATURE_3];
            } else if (templModelType == TEMPL_MODEL_6) {
                finger = new byte[PERFECT_FEATURE_6];
            }
            int fingerLength = 0;
            if (templModelType == TEMPL_MODEL_3) {
                fingerLength = PERFECT_FEATURE_3;
            } else if (templModelType == TEMPL_MODEL_6) {
                fingerLength = PERFECT_FEATURE_6;
            }
            System.arraycopy(fingerTemplData, fingerLength * i, finger, 0, fingerLength);
            byte[] waitTempDatas = resolveTempl(finger);
            if (waitTempDatas != null) {
                int i1 = waitTempDatas.length * i;
                System.arraycopy(waitTempDatas, 0,
                        matchData, i1, waitTempDatas.length);
            }
        }
        return matchData;
    }

    //解析模板 ==>大特征解析模板专用
    private byte[] resolveTempl(byte[] oldMatchTemplData) {
        /**
         *      （1） 1：模板解析成功， Output数据有效
         *      （2）-1：模板解析失败，因参数不合法，Output数据无效
         *      -2:待解析的模板数据为null
         */
        //将模板解析为比对模板，实际上就是去掉前208位
        Message resolveCommpareTemplMsg = new Message();
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
                return matchTemplData;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //从主机读取数据
    private void tgReadDataFromHost(Handler handler) {
        Message msg = new Message();
        msg.what = READ_FILE;
        Bundle bundle = new Bundle();
        byte[] data;
        if (!TextUtils.isEmpty(this.savePath)) {
            File file = new File(savePath);
            if (savePath.contains(".dat")) {
                byte[] fileData = FileUtil.readBytes(file);
                bundle.putInt(FINGER_SIZE, 1);
                bundle.putByteArray(DATA, fileData);
            } else {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    data = templSizeData(files.length);
                    try {
                        for (int i = 0; i < files.length; i++) {
                            File file1 = files[i];
                            byte[] fileData = FileUtil.readBytes(file1);
                            System.arraycopy(fileData, 0, data, (int) file1.length() * i
                                    , (int) file1.length());
                        }
                    } catch (Exception e) {
                        LogUtils.e("TGAPI-class,1370-line,ArrayOutIndex-err");
                        e.printStackTrace();
                    }
                    bundle.putInt(FINGER_SIZE, files.length);
                    bundle.putByteArray(DATA, data);
                }
            }
        }
        msg.setData(bundle);
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }

    //获取模板算法的版本
    private void tgGetFVVersion(Handler handler, byte[] templData) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        byte[] snData = new byte[5];
        int tgGetSNFromTmplRes = getTGFV().TGGetAPIVerFromTmpl(templData, snData);
        if (tgGetSNFromTmplRes == 0) {
            message.arg1 = 1;
            try {
                String snVersion = new String(snData, "UTF-8");
                bundle.putString(TN_STR, snVersion);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (tgGetSNFromTmplRes == -1) {
            message.arg1 = -1;
        }
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    //获取模板的SN号
    private void tgGetTemplSN(Handler handler, byte[] templData) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        byte[] snData = new byte[17];
        int tgGetSNFromTmplRes = getTGFV().TGGetSNFromTmpl(templData, snData);
        if (tgGetSNFromTmplRes == 0) {
            message.arg1 = 1;
            try {
                String sn = new String(snData, "UTF-8");
                bundle.putString(TN_STR, sn);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (tgGetSNFromTmplRes == -1) {
            message.arg1 = -1;
        }
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    //获取模板的FW固件号
    private void tgGetTemplFW(Handler handler, byte[] templData) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        byte[] snData = new byte[17];
        int tgGetSNFromTmplRes = getTGFV().TGGetFWFromTmpl(templData, snData);
        if (tgGetSNFromTmplRes == 0) {
            message.arg1 = 1;
            try {
                String fw = new String(snData, "UTF-8");
                bundle.putString(TN_STR, fw);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (tgGetSNFromTmplRes == -1) {
            message.arg1 = -1;
        }
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    //获取模板生成的时间
    private void tgGetTemplTime(Handler handler, byte[] templData) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        byte[] snData = new byte[TIME_SIZE];
        int tgGetSNFromTmplRes = getTGFV().TGGetTimeFromTmpl(templData, snData);
        if (tgGetSNFromTmplRes == 0) {
            message.arg1 = 1;
            try {
                String time = new String(snData, "UTF-8");
                bundle.putString(TN_STR, time);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (tgGetSNFromTmplRes == -1) {
            message.arg1 = -1;
        }
        message.setData(bundle);
        if (handler != null) {
            handler.sendMessage(message);
        }
    }

    /**
     * 权限申请
     *
     * @param activity
     * @return -1,1
     * 1: 权限获取成功
     * -1:否则权限获取失败
     */
    private PermissionCallBack permissionCallBack;

    public void checkPermissions(Activity activity, PermissionCallBack permissionCallBack) {
        this.mActivity = activity;
        this.mContext = activity;
        this.permissionCallBack = permissionCallBack;
        for (int i = 0; i < perms.length; i++) {
            String perm = perms[i];
            int checkSelfPerm = ContextCompat.checkSelfPermission(activity, perm);
            if (checkSelfPerm == PackageManager.PERMISSION_DENIED) {
                Intent intent = new Intent("com.tg.m661j.vein.api");
                Bundle bundle = new Bundle();
                intent.addCategory("com.tg.m661j.vein.api");
                bundle.putString("flag", Constant.TG661JB);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            } else {
                if (i == perms.length - 1 && permissionCallBack != null) {
                    createDirPath();
                    //CMD();
                    permissionCallBack.permissionResult(InitLicense(activity));
                }
            }
        }
    }

    /**
     * 供外部调用，进行算法的初始化：例如 权限界面申请成功后调用算法初始化
     */
    public void FV(Context context) {
        createDirPath();
        InitLicense(context);
        if (permissionCallBack != null) {
            permissionCallBack.permissionResult(InitLicense(mContext));
        }
    }

    //存储图片
    public void tgSaveImg(Message message, Bundle bundle, String fileName,
                          byte[] imageData, int imgLength) {
        //传出抓取图片的数据
        if (sImg) {
            img(message, bundle, imageData, imgLength);
            //存储图片
            if (TextUtils.isEmpty(fileName)) {
                long l = System.currentTimeMillis();
                fileName = String.valueOf(l);
            }
            saveImg(fileName, imageData, imgLength);
        }
    }

    //对外传图
    private void img(Message msg, Bundle bundle, byte[] imgData, int length) {
        msg.obj = imgData;
        //传出抓取图片的数据
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt("imgLength", length);
        bundle.putByteArray("imgData", imgData);
        msg.setData(bundle);
    }

    //存储图片
    private boolean saveImg(String imgName, byte[] imgData, int imgLength) {
        if (imgName.contains(".dat")) {
            imgName = imgName.substring(0, imgName.indexOf(".dat"));
        }
        String imgPath = this.imgPath + File.separator + imgName + ".jpg";
        byte[] jpegData = new byte[imgLength];
        System.arraycopy(imgData, 1024 * 256, jpegData, 0, imgLength);
        return FileUtil.writeFile(jpegData, imgPath);
    }

    /**
     * 写入证书到指定文件
     *
     * @param inputStream 数据输入流
     * @return
     */
    private int writeLicenseToFile(InputStream inputStream) {
        int res = -1;
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
                res = FV_InitAct();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 调用算法接口初始化算法
     *
     * @return
     */
    private void FV_InitAct(final FvInitCallBack fvInitCallBack) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int i = getTGFV().TGInitFVProcess(licensePath);
                if (i == 0) {
                    i = 1;
                } else if (i == 1) {
                    i = 2;
                } else if (i == 2) {
                    i = 3;
                } else if (i == 3) {
                    i = 4;
                }
                final int res = i;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fvInitCallBack != null) {
                            fvInitCallBack.fvInitResult(new Msg(res, ""));
                        }
                    }
                });
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Messenger tg661JMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RECEIVE_MESSAGE_CODE) {
                sendDevStatusToView(msg);
            }
        }
    });
    //设备是否连接上的标志
    private boolean isLink = false;

    private void sendDevStatusToView(Message msg) {
        if (mHandler != null) {
            Bundle data = msg.getData();
            if (data != null) {
                int devServiceArg = data.getInt("status");
                Message tg661JMsg = new Message();
                tg661JMsg.what = DEV_STATUS;
                LogUtils.d("接收到的设备状态：" + devServiceArg);
                if (devServiceArg == 0) {
                    if (!this.isLink)
                        openDev(this.mHandler, this.workType, this.templModelType, sound);
                    tg661JMsg.arg1 = 1;
                } else if (devServiceArg == -2) {
                    this.isLink = false;
                    this.devOpen = false;
                    tg661JMsg.arg1 = -2;
                }
                mHandler.sendMessage(tg661JMsg);
            }
        }
    }

    private static final int RECEIVE_MESSAGE_CODE = 0x0002;
    private static final int SEND_MESSAGE_CODE = 0x0001;
    private Messenger devServiceMessenger = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            devServiceMessenger = new Messenger(iBinder);
            //如果设备开启
            Message tg661JMessage = new Message();
            tg661JMessage.what = SEND_MESSAGE_CODE;
            tg661JMessage.replyTo = tg661JMessenger;
            try {
                devServiceMessenger.send(tg661JMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            devServiceMessenger = null;
        }
    };

    /**
     * 获取int类型的结果
     *
     * @param future
     * @return
     */
    private int getRes(Future<Integer> future) {
        Integer result = -1;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取目标文件夹得路径
     *
     * @return
     */
    private String getAimPath() {
        String aimPath = "";
        if (templModelType == TEMPL_MODEL_3) {
            aimPath = getBehind3TemplPath();
        } else if (templModelType == TEMPL_MODEL_6) {
            aimPath = getBehind6TemplPath();
        }
        return aimPath;
    }

    /**
     * 获取后比6特征模板文件路径
     *
     * @return
     */
    public String getBehind6TemplPath() {
        File file6 = new File(behindTempl3Path);
        if (!file6.exists()) {
            file6.mkdirs();
        }
        return behindTempl6Path;
    }

    /**
     * 获取后比3特征模板文件路径
     *
     * @return
     */
    public String getBehind3TemplPath() {
        File file3 = new File(behindTempl3Path);
        if (!file3.exists()) {
            file3.mkdirs();
        }
        return behindTempl3Path;
    }

    /**
     * 创建相关的文件夹,获取到相关的路径
     */
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
        File imgFile = new File(imgPath);
        if (!imgFile.exists())
            imgFile.mkdirs();

        File exter3F = new File(moniExter3Path);
        if (!exter3F.exists())
            exter3F.mkdirs();
        File exter6F = new File(moniExter6Path);
        if (!exter6F.exists())
            exter6F.mkdirs();
    }

    /**
     * 修改系统USB权限
     */
    private void writeCMD() {
        String command1 = "chmod -R 777 /dev/*";
        String command = "chmod -R 777 /dev/bus/usb/*";
        String command2 = "chmod -R 777 /dev/hidraw0 \nchmod -R 777 /dev/hidraw1" +
                " \nchmod -R 777 /dev/hidraw2 \nchmod -R 777 /dev/hidraw3 \nchmod -R 777 /dev/hidraw4";
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[]{"su", "-c", command});
            int i = process.waitFor();
            process = runtime.exec(new String[]{"su", "-c", command2});
            int i1 = process.waitFor();
            LogUtils.i("CDM写入su1111命令:" + i + "  hidraw :" + i1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void wCMD() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("mount -oremount,rw /dev/block/mtdblock3 /system\n");
            os.writeBytes("busybox cp /data/data/com.koushikdutta.superuser/su /system/bin/su\n");
            os.writeBytes("busybox chown 0:0 /system/bin/su\n");
            os.writeBytes("chmod 4755 /system/bin/su\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前线程的名字
     */
    private void getThreadName(String workName) {
        String name = Thread.currentThread().getName();
        long id = Thread.currentThread().getId();
        Log.d("===HHH", workName + "   当前线程的名称：" + name + "  id:" + id);
    }

}
