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
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.TG.library.CallBack.Common;
import com.TG.library.CallBack.PermissionCallBack;
import com.TG.library.pojos.FingerBean;
import com.TG.library.pojos.MatchN;
import com.TG.library.service.GetFileTask;
import com.TG.library.utils.AlertDialogUtil;
import com.TG.library.utils.ArrayUtil;
import com.TG.library.utils.AudioProvider;
import com.TG.library.utils.FileUtil;
import com.TG.library.utils.ImgExechangeBMP;
import com.TG.library.utils.LogUtils;
import com.TG.library.utils.RegularUtil;
import com.TG.library.utils.TGDialogUtil;
import com.TG.library.utils.ToastUtil;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created By pq
 * on 2019/5/28
 * 小特征
 */
public class TG661JBAPI {

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
    public static final int DEV_IMG = 0xf29;
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
    //public static final int WRITE_LICENSE = 0xf44;
    public static final int DELETE_HOST_ID_TEMPL = 0xf45;//删除主机指定模板
    public static final int DELETE_HOST_ALL_TEMPL = 0xf46;//删除主机所有模板
    public static final int UPDATE_HOST_TEMPL = 0xf47;//更新主机中的模板
    public static final int SET_DEV_MODEL = 0xf48;//设置设备模式
    public static final int DEV_IMG_LISTENER = 0xf49;//指静脉图像设备监听
    public static final int INIT_MATCH_DATA = 0xf50;//初始化或更新科比对模板的数据
    public static final int READ_AND_EXCHANGED = 0xf51;//读取数据模板并将模板转换成可比对的模板

    public static final String COMPARE_N_TEMPL = "update_templ";//1:N验证的模板
    public static final String INDEX = "index";//模板的索引
    public static final String COMPARE_N_SCORE = "n_score";//1:N验证的分数
    public static final String COMPARE_NAME = "templ_name";//1:N验证的分数
    //public static final String TEMP_LIST = "templ_list";//模板列表
    public static final String FINGER_DATA = "finger_data";//模板数据

    //完整的特征大小
    //private static final int PERFECT_FEATURE_17682 = 17682;
    //private static final int PERFECT_FEATURE_35058 = 35058;

    //特征模板
    public static final int PERFECT_FEATURE_3 = 3248;//3特征
    public static final int PERFECT_FEATURE_6 = 6464;//6特征

    //可比对的模板大小
    public static final int WAIT_COMPARE_FEATURE_3 = 3216;//6特征
    public static final int WAIT_COMPARE_FEATURE_6 = 6432;//3特征

    public static final int IMG_SIZE = 500 * 200 + 208;
    public static final int IMG_W = 500;
    public static final int IMG_H = 200;
    public static final int FEATURE_SIZE = 2384;
    //UUID的byte占位大小
    public static final int UUID_SIZE = 33;
    //时间值的占位大小
    public static final int TIME_SIZE = 15;
    //一次转换多少的可比对模板的数据常量
    public static final int READ_COMPARE_COUNT = 3500;

    //临时加的图片大小
    public static final int T_SIZE = 1024 * 500;
    public static final int GET_IMG_OUT_TIME = 15;//默认设置抓图超时的时间为15S
    //public static final int GET_IMG_OUT_TIME_5000 = 5000;//默认设置抓图超时的时间为5000
    //增加模板的标识
    public static final int INCREASE_FINGER_TEMPL = 0x2;
    //减少模板的标识
    public static final int REDUCE_FINGER_TEMPL = 0x3;

    public TG661JBAPI() {
        //创建线程池
        executors = Executors.newCachedThreadPool();
        ecs = new ExecutorCompletionService<Object>(executors);


        int startSrc = 0;
    }

    //获取代理对象
    private static TG661JBAPI tg661JBAPI = null;

    public static TG661JBAPI getTg661JBAPI() {
        if (tg661JBAPI == null) {
            synchronized (TG661JBAPI.class) {
                if (tg661JBAPI == null) {
                    tg661JBAPI = new TG661JBAPI();
                }
            }
        }
        return tg661JBAPI;
    }

    //获取算法代理对象
    public TGFV getTGFV() {
        return TGFV.TGFV_INSTANCE;
    }

    //获取通信库代理对象
    public TGXG661API getTG661() {
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

    //启动目标service的Action
    private static final String DevServiceAction = "com.example.mylibrary.DevService.action";
    //存储数据的根文件夹路径
    private String tgDirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + (File.separator + "TG_TEMPLATE");
    //后比模板的文件夹
    private String behindDatDir = tgDirPath + File.separator + "BehindTemplate";
    //后比的3，6模板路径
    private String behindTempl3Path = behindDatDir + File.separator + "TEMPL_3";
    private String behindTempl6Path = behindDatDir + File.separator + "TEMPL_6";
    //证书所在的文件夹路径
    private String licenceDir = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "TG_VEIN";
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
    public static final int WORK_BEHIND = 2;
    //模板数据来源  外部
    public static final int EXTERNAL_TEMPL_SOURCES = 0xa1;
    //模板数据来源  主机文件夹
    public static final int DIR_TEMPL_SOURCES = 0xa2;
    //需要融合的特征的个数
    private int FEA_LENGTH = 0;
    //SDK的当前版本号
    private static final String SDK_VERSION = "1.2.0_190531_Beta";
    //标记 网络下发证书
    private boolean netLoadLicence;
    //证书的数据流
    private InputStream inputStream;
    //发送消息的Handler
    private Handler handler;
    //上下文对象
    private Context context;
    //当前的activity
    private Activity mActivity;
    //工作的线程池
    private ExecutorService executors;
    private ExecutorCompletionService ecs;

    private int templModelType;//标记特征模式  3/6
    private int workType = WORK_BEHIND;//默认是后比
    private int templSources = DIR_TEMPL_SOURCES;//模板数据默认来源是文件夹
    public boolean devOpen = false;//设备是否已经打开
    public boolean devClose = false;//设备关闭的标志
    private int devStatus = -1;//默认设备未开启的状态

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

    private boolean isCancelRegister = false;

    /**
     * 设置图片是否发送出去，内部测试用
     *
     * @param sImg
     */
    public void setsImg(boolean sImg) {
        this.sImg = sImg;
    }

    /*----------------------对外暴露的API-  Start-------------------*/

    /**
     * 获取SDK的版本
     */
    public String getSDKVersion() {
        return SDK_VERSION;
    }

    /**
     * 检查权限，权限写入成功后写入证书。
     * 无需权限的情况，调用接口写入证书
     */
    //检查权限
    public boolean checkPermissions(Handler handler, int type, Activity activity) {
        boolean pers = false;
        this.handler = handler;
        this.mActivity = activity;
        this.context = activity;
        for (int i = 0; i < perms.length; i++) {
            String perm = perms[i];
            int checkResult = ContextCompat.checkSelfPermission(mActivity, perm);
            if (checkResult == PackageManager.PERMISSION_DENIED) {
                //权限没有同意，需要申请该权限
                Intent intent = new Intent("com.tg.m661j.vein.api");
                Bundle bundle = new Bundle();
                intent.addCategory("com.tg.m661j.vein.api");
//                bundle.putInt("type", type);
                bundle.putString("flag", Common.TG661JB);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
                //没有获取权限
                pers = false;
            } else {
                if (i == perms.length - 1) {
                    InitLicense(context);
                    //已经获取权限
                    pers = true;
                }
            }
        }
//        if (type == 1) {
//            saveTemplHost();
//        }
        return pers;
    }

    //初始化算法开始
    public void initFV(Handler handler, Activity context,
                       InputStream inputStream,
                       boolean netLoadLicence) {
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
        writeCMD();
        work(handler, INIT_FV);
//        FV_InitAct(handler);
//        InitLicense();
    }

    //标记算法是否已经初始化
    private boolean isInitFV = false;

    public boolean getisInitFV() {
        return isInitFV;
    }

    //调用算法接口初始化算法
    private void FV_InitAct(Handler handler) {
        int tgInitFVProcessRes = getTGFV().TGInitFVProcess(licensePath);
        if (handler != null) {
            Message initFvMsg = handler.obtainMessage();
            initFvMsg.what = INIT_FV;
            if (tgInitFVProcessRes == 0) {
                initFvMsg.arg1 = 1;
                isInitFV = true;
            } else if (tgInitFVProcessRes == 1) {
                initFvMsg.arg1 = 2;
            } else if (tgInitFVProcessRes == 2) {
                initFvMsg.arg1 = 3;
            } else if (tgInitFVProcessRes == 3) {
                initFvMsg.arg1 = 4;
            }
            handler.sendMessage(initFvMsg);
        }
    }

    /**
     * 初始化证书
     */
    private void InitLicense(Context context) {
        createDirPath();
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
                            //存在证书历史,初始化算法,调用算法接口初始化算法
                            FV_InitAct(handler);
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

    public void FV(Context context) {
        if (!isInitFV) {
            InitLicense(context);
        }

    }
    //算法初始化结束

    //检测设备的连接状态
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
    //设置监听设备状态结束

    //所有的已存在的模板数据
    private List<FingerBean> allTemplData = null;

    //外部调用，更新缓存中的模板数据
    public void updateAllTemplData(List<FingerBean> newCountTemplData, int templSources) {
        this.allTemplData = newCountTemplData;
        this.templSources = templSources;
//        initMatchTemplDatas();
        tgInitMatchDatas();
    }

    //设置设备的工作状态
    public void setTemplModelType(int templModelType) {
        this.templModelType = templModelType;
        this.hasTemplName = false;
        this.isCheck = false;
        this.lastTemplName = "";
//        initMatchTemplDatas();
        tgInitMatchDatas();
    }

    //打开设备
    public void openDev(Handler mHandler, Activity activity,
                        int workType, int templModelType, int templSources) {
        this.handler = mHandler;
        this.context = activity;
        this.mActivity = activity;
        this.templModelType = templModelType;
        this.templSources = templSources;
        this.workType = workType;
        if (devStatus == -1) {
            devStatus = getTG661().TGGetDevStatus();
            if (devStatus >= 0) {
                return;
            }
            //模板数据初始化
            if (allFingerMatchDatas == null)
////                initMatchTemplDatas();
                tgInitMatchDatas();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialogUtil.Instance().showWaitDialog(context, "正在打开设备...");
                }
            });
            work(handler, OPEN_DEV);
        }

    }

    public boolean isDevOpen() {
        return devOpen;
    }

    //关闭设备
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
        //allFingerMatchDatas = new byte[0];
    }

    /**
     * 提取特征(注册使用)
     *
     * @param handler        信使
     * @param templModelType 特征模式3/6
     * @param templId        模板名称
     */
    public void extractFeatureRegister(Handler handler, int templModelType, String templId) {
//        if (TextUtils.isEmpty(templId)) {
//            Message extractFeatureRegisterMsg = handler.obtainMessage();
//            extractFeatureRegisterMsg.what = EXTRACT_FEATURE_REGISTER;
//            extractFeatureRegisterMsg.arg1 = -6;
//            handler.sendMessage(extractFeatureRegisterMsg);
//            return;
//        }
        if (!TextUtils.isEmpty(templId)) {
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
            this.lastTemplName = this.templNameID = templId;
        }
        this.templModelType = templModelType;
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
     * 取消注册
     *
     * @param templModelType 特征模式 3/6
     */
    public void cancelRegister(Handler handler, int templModelType) {
        isCancelRegister = true;
        this.handler = handler;
        this.templModelType = templModelType;
        work(handler, CANCEL_REGISTER);
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
     * @param handler  信使
     */
    private byte[] templData_1;
    private boolean backUpdateFingerData = false;//默认不返回更新的模板数据

    public void featureCompare1_1(Handler handler, String templName, byte[] templData_1,
                                  boolean backUpdateFingerData) {
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
        this.templData_1 = templData_1;
        this.backUpdateFingerData = backUpdateFingerData;
        work(handler, FEATURE_COMPARE1_1);
    }

    /**
     * 特征1:N验证
     *
     * @param handler 信使
     */
//    private String userFingerName;
    public void featureCompare1_N(Handler handler, boolean backUpdateFingerData/*, String userName*/) {
        this.handler = handler;
        this.backUpdateFingerData = backUpdateFingerData;
//        this.userFingerName = userName;
        work(handler, FEATURE_COMPARE1_N);
    }

    /**
     * 获取模板对应的算法的版本
     *
     * @param handler 信使
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
     * @param handler 信使
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
     * @param handler 信使
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
     * @param handler 信使
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
     * @param handler 信使
     */
    public void getDevWorkModel(Handler handler) {
        this.handler = handler;
        work(handler, DEV_WORK_MODEL);
    }

    /**
     * 取消验证
     *
     * @param handler 信使
     */
    public void cancelVerify(Handler handler) {
        this.handler = handler;
        work(handler, CANCEL_VERIFY);
    }

    /**
     * 取消获取设备图像
     *
     * @param handler 信使
     */
    public void cancelDevImg(Handler handler) {
        this.handler = handler;
        work(handler, CANCEL_DEV_IMG);
    }

    /**
     * 获取设备图像
     *
     * @param handler 信使
     */
    public void getDevImg(Handler handler) {
        this.handler = handler;
        work(handler, DEV_IMG);
    }

    /**
     * 监听设备是否有指静脉图像返回
     *
     * @param handler 信使
     */
    public void DevImgListener(Handler handler) {
        this.handler = handler;
        tgDevBackImg(handler);
//        work(handler, DEV_IMG_LISTENER);
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
        boolean b = getAP(context).increaceVolume();
        return b;
    }

    /**
     * 后比音量减
     */
    public boolean descreaseVolume(Handler handler) {
        this.handler = handler;
        boolean b = getAP(context).decreaseVolume();
        return b;
    }

    /**
     * 获取当前的音量
     */
    public String getCurrentVolume(Handler handler) {
        this.handler = handler;
        float currentVolume = getAP(context).getCurrentVolume();
        return String.valueOf(currentVolume);
    }

    /**
     * 获取最大的音量值
     */
    public String getMaxVolume(Handler handler) {
        this.handler = handler;
        float streamVolumeMax = getAP(context).getStreamVolumeMax();
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

    private byte[] templateData;
    private String templateName;
    private Message writeFileMsg;

    public void writeFileHost(Handler handler, byte[] templateData, String templateName, int index) {
        this.handler = handler;
        writeFileMsg = handler.obtainMessage();
        writeFileMsg.what = WRITE_FILE;
        if (TextUtils.isEmpty(templateName) || templateData == null) {
            writeFileMsg.arg1 = -9;
            handler.sendMessage(writeFileMsg);
            return;
        }
        this.templateData = templateData;
        this.templateName = templateName;
        work(handler, WRITE_FILE);
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

    //获取证书文件的路径
    public String getLicensePath() {
        return licensePath;
    }

    //获取目标路径下所有文件列表
    public ArrayList<String> scanAimDirFileName(String path) {
        ArrayList<String> finerFileList = FileUtil.getInitFinerFileList(path);
        return finerFileList;
    }

    /**
     * 写入文件到主机
     *
     * @param templData 文件数据
     * @return 返回写入是否成功的结果
     */
    public boolean writeTemplHost(byte[] templData, String tmlID) {
        String templId = tmlID.substring(0, tmlID.indexOf(".dat"));
        String datFilesPath = getAimPath() + File.separator + templId + ".dat";
        boolean writeFile = FileUtil.writeFile(templData, datFilesPath);
        return writeFile;
    }

    /*----------------------对外暴露的API-  End-------------------*/

    private void work(final Handler handler, final int flag) {
        ecs.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                getThreadName("执行功能");
                switch (flag) {
                    case INIT_FV:
                        //初始化算法
                        InitLicense(context);
                        break;
                    case READ_AND_EXCHANGED:
                        //读取模板并转换成可比对模板

                        break;
                    case OPEN_DEV:
                        //打开设备
                        tgOpenDev(handler);
                        break;
                    case INIT_MATCH_DATA:
                        //更新科比对的模板
                        initMatchTemplDatas();
                        break;
                    case CLOSE_DEV:
                        //关闭设备
                        tgCloseDev(handler);
                        break;
                    case SET_DEV_MODEL:
                        //设置设备的工作模式
                        tgSetDevModel(handler);
                        break;
                    case DEV_WORK_MODEL:
                        //获取设备的工作模式
                        tgGetDevModel(handler);
                        break;
                    case DEV_STATUS:
                        //获取设备的链接状态
                        tgDevStatus(handler);
                        break;
                    case CANCEL_REGISTER:
                        //取消注册
                        tgCancelRegister(handler);
                        break;
                    case CANCEL_DEV_IMG:
                        //取消获取图像
                        tgCancelGetImg(handler);
                        break;
                    case EXTRACT_FEATURE_REGISTER:
                        // 注册
                        tgDevRegister(handler);
                        break;
                    case EXTRACT_FEATURE_VERIFY:
                        //提取特征
                        tgGetFeature(handler);
                        break;
                    case DEV_IMG:
                        //设备抓取图像
                        tgDevGetImg(handler);
                        break;
//                    case DEV_IMG_LISTENER:
//                        //监听指静脉设备是否有图返回
//                        tgDevBackImg(handler);
//                        break;
                    case FEATURE_FUSION:
                        //特征融合
                        tgFeatureFusion(handler);
                        break;
                    case RESOLVE_COMPARE_TEMPL:
                        //将模板转换成比对模板
                        tgExechangeMatchModel(handler);
                        break;
                    case FEATURE_COMPARE1_1:
                        //1:1
                        tgTempl1_1(handler);
                        break;
                    case FEATURE_COMPARE1_N:
                        //1:N
                        tgTempl1_N(handler);
                        break;
                    case TEMPL_FV_VERSION:
                        //获取模板对应算法的版本
                        tgGetFVVersion(handler);
                        break;
                    case TEMPL_SN:
                        //获取模板对应算法的序列号
                        tgGetTemplSN(handler);
                        break;
                    case TEMPL_FW:
                        //获取模板对应算法的固件号
                        tgGetTemplFW(handler);
                        break;
                    case TEMPL_TIME:
                        //获取模板对应的时间
                        tgGetTemplTime(handler);
                        break;
                    case DELETE_HOST_ID_TEMPL:
                        //删除主机指定的ID模板
                        tgDelHostIdTempl(handler);
                        break;
                    case DELETE_HOST_ALL_TEMPL:
                        //删除主机中所有的模板
                        tgDelAllHostTempl(handler);
                        break;
                    case UPDATE_HOST_TEMPL:
                        //更新主机中指定的模板
                        tgUpdateHostIdTempl(handler);
                        break;
                    case WRITE_FILE:
                        boolean writeB = writeTemplHost(templateData, templateName);
                        if (writeB) {
                            writeFileMsg.arg1 = 1;
                        } else {
                            writeFileMsg.arg1 = -1;
                        }
                        handler.sendMessage(writeFileMsg);
                        break;
                }
                return null;
            }
        });
    }

    /*----------------------封装的SDK内部功能性方法 Start----------------------*/

    private void tgOpenDev(Handler handler) {
//        showWaitDialog(1, "正在打开设备...");

        /*
         * 打开设备：默认前比3特征模板的工作模式,不支持连续验证
         * 1：后比工作模式  0：前比工作模式
         */
        IntByReference mode = new IntByReference();
        int openDevRes = getTG661().TGOpenDev(mode);
        Message openDevMsg = handler.obtainMessage();
        openDevMsg.what = OPEN_DEV;
        if (openDevRes >= 0) {
            //设置工作模式
            setDevWorkModel(handler, workType, templModelType);
            devOpen = true;
            devClose = false;
            //发送打开设备的结果:
            openDevMsg.arg1 = 1;
        } else {
            openDevMsg.arg1 = -1;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogUtil.Instance().disDialog();
            }
        });
        handler.sendMessage(openDevMsg);
//        showWaitDialog(-1, "");
    }

    private void tgCloseDev(Handler handler) {
//                        showWaitDialog(1, "正在关闭设备");
        int closeDevRes = getTG661().TGCloseDev();
        Message closeDevMsg = handler.obtainMessage();
        closeDevMsg.what = CLOSE_DEV;
        devClose = closeDevRes == SUCCESS_FLAG;
        devOpen = false;
        if (closeDevRes == 0) {
            //传出关闭成功的结果：
            getAP(context).release();//释放声音资源
            devStatus = -1;
            closeDevMsg.arg1 = 1;
        } else {
            closeDevMsg.arg1 = -1;
        }
        handler.sendMessage(closeDevMsg);
//                        showWaitDialog(-1, "");
    }

    private void tgSetDevModel(Handler handler) {
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
    }

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
    }

    private void tgDevStatus(Handler handler) {
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
    }

    private void tgCancelRegister(Handler handler) {
        int tgCancelRegisterRes = getTG661().TGCancelGetImage();
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
    }

    private void tgCancelGetImg(Handler handler) {
        int tgCancelGetImageRes = getTG661().TGCancelGetImage();
        Message cancelImgMsg = handler.obtainMessage();
        cancelImgMsg.what = CANCEL_DEV_IMG;
        if (tgCancelGetImageRes == 0) {
            cancelImgMsg.arg1 = 1;
        } else {
            cancelImgMsg.arg1 = -1;
        }
        handler.sendMessage(cancelImgMsg);
    }

    private void tgDevRegister(Handler handler) {
        //首先检查主机中已注册的模板文件名是否已存在
        Message imgFeaMsg = handler.obtainMessage();
        imgFeaMsg.what = EXTRACT_FEATURE_REGISTER;
        Bundle bundle = new Bundle();
        if (templSources == DIR_TEMPL_SOURCES) {
            if (!hasTemplName) {
                hasTemplName = checkTemplName(templNameID);
                if (hasTemplName) imgFeaMsg.arg1 = -8;
            }
        } else if (templSources == EXTERNAL_TEMPL_SOURCES) {
            if (TextUtils.isEmpty(templNameID))
                hasTemplName = false;
        }
        //注册前核对当前指静脉是否已经注册
        if (!isCheck && !hasTemplName) {
            checkFingetRegister(handler);
        }
        //该指静脉已经注册
        if (hasTemplName) {
            getAP(context).play_registerRepeat();
            imgFeaMsg.arg1 = -8;
        } else if (hasTempl) {
            imgFeaMsg.arg1 = -5;
        } else {
            if (!isCancelRegister) {
                if (templIndex == 0) {
                    getAP(context).play_inputDownGently();
                } else if (templIndex > 0) {
                    getAP(context).play_inputAgain();
                }
                byte[] imgDataFea = tgDevGetFingerImg(handler, imgFeaMsg);
                if (DevImageMatchLength > 0) {
                    if (imgDataFea != null) {
                        //提取特征  --- 注册
                        byte[] regFeature = new byte[FEATURE_SIZE];
                        int tgImgExtractFeatureRegRes = getTGFV().TGImgExtractFeatureRegister(imgDataFea,
                                IMG_W, IMG_H, regFeature);
                        if (tgImgExtractFeatureRegRes == 0) {
//                            byte[] aimFeatures = null;
//                            if (templIndex < templSize) {
//                                Log.d("===LLL","   注册  ");
//                                imgFeaMsg.arg1 = 10;
//                                aimFeatures = jointTempl(regFeature);
//                                extractFeatureRegister(handler, templModelType, templNameID);
//                            }
                            imgFeaMsg.arg1 = 10;
                            byte[] aimFeatures = jointTempl(regFeature);
                            if (templIndex < templSize) {
                                extractFeatureRegister(handler, templModelType, this.templNameID);
                            }
                            if (templSize == templIndex) {
                                templIndex = 0;
                                aimByte = null;
                                isCheck = false;
                                hasTemplName = true;
                                //融合
                                byte[] fusionTempl = perfectTemplData();
                                int fusionFeatureRes = getTGFV().TGFeaturesFusionTmpl(aimFeatures,
                                        templSize, fusionTempl);
                                if (fusionFeatureRes == 0) {
                                    if (templSources == DIR_TEMPL_SOURCES) {
                                        //模板融合成功--存储
                                        String templSavePath = getAimPath();
                                        String savePath = templSavePath + File.separator + templNameID + ".dat";
                                        boolean writeFile = FileUtil.writeFile(fusionTempl, savePath);
                                        if (writeFile) {
                                            hasTempl = true;
                                            //登记成功
                                            imgFeaMsg.arg1 = 1;
                                            getAP(context).play_checkInSuccess();
                                        } else {
                                            hasTempl = false;
                                            getAP(context).play_checkInFail();
                                        }
                                    } else if (templSources == EXTERNAL_TEMPL_SOURCES) {
                                        imgFeaMsg.arg1 = 1;
                                        //传出去的模板数据是融合后的模板数据
                                        bundle.putByteArray(FINGER_DATA, fusionTempl);
                                        imgFeaMsg.setData(bundle);
                                        getAP(context).play_checkInSuccess();
                                    }
                                    //如果是首次注册则需要将初始占位的模板数据给清空
                                    if (firstOpen) {
                                        allFingerMatchDatas = new byte[0];
                                        templCount = 0;
                                        firstOpen = false;
                                    }
                                    //将融合好的模板转换成可比对的模板
                                    byte[] matchTempl = templExechangeMatchTempl(fusionTempl);
                                    //更新代码缓存区的模板数据，新增指静脉模板数据
//                                updateCodeTemplDataCount(INCREASE_FINGER_TEMPL, matchTempl, -1);
                                    addTemlpData(matchTempl);
                                } else if (fusionFeatureRes == 6) {
                                    templIndex = 0;
                                    aimByte = null;
                                    hasTempl = false;
                                    hasTemplName = false;
                                    imgFeaMsg.arg1 = 2;
                                    getAP(context).play_checkInFail();
                                } else if (fusionFeatureRes == -1) {
                                    templIndex = 0;
                                    aimByte = null;
                                    hasTempl = false;
                                    hasTemplName = false;
                                    imgFeaMsg.arg1 = 3;
                                    getAP(context).play_checkInFail();
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
                        //存储图片
                        tgSaveImg(imgFeaMsg, bundle, templNameID + templIndex
                                , imgDataFea, DevImageMatchLength);
                    } else {
                        imgFeaMsg.arg1 = -9;
                    }
                }
            }
        }
        handler.sendMessage(imgFeaMsg);
    }

    private void tgGetFeature(Handler handler) {
        byte[] verFeature = new byte[FEATURE_SIZE];
        int tgImgExtractFeatureVerRes = getTGFV().TGImgExtractFeatureVerify(imageData,
                IMG_W, IMG_H, verFeature);
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
    }

    private void tgDevGetImg(Handler handler) {
        Message imgMsg = handler.obtainMessage();
        imgMsg.what = DEV_IMG;
        getAP(context).play_inputDownGently();
        byte[] imgData = tgDevGetFingerImg(handler, imgMsg);
        //存储图片
        if (DevImageMatchLength > 0) {
            tgSaveImg(imgMsg, null, ""
                    , imgData, DevImageMatchLength);
            imgMsg.arg1 = 1;
            imgMsg.arg2 = DevImageMatchLength;
            imgMsg.obj = imgData;
            handler.sendMessage(imgMsg);
        }
    }

    //监听是否有指静脉图像返回
    private void tgDevBackImg(Handler handler) {
        Message imgMsg = handler.obtainMessage();
        imgMsg.what = DEV_IMG_LISTENER;
        byte[] imgData = tgDevGetFingerImg(handler, imgMsg);
        //存储图片
        if (DevImageMatchLength > 0) {
            tgSaveImg(imgMsg, null, ""
                    , imgData, DevImageMatchLength);
            imgMsg.arg1 = 1;
            imgMsg.arg2 = DevImageMatchLength;
            imgMsg.obj = imgData;
            handler.sendMessage(imgMsg);
        }
    }

    private void tgFeatureFusion(Handler handler) {
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
            byte[] fusionTemplData = perfectTemplData();
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
    }

    private void tgExechangeMatchModel(Handler handler) {
        //将模板解析为比对模板，实际上就是去掉前208位
        Message resolveCommpareTemplMsg = handler.obtainMessage();
        resolveCommpareTemplMsg.what = RESOLVE_COMPARE_TEMPL;
        if (oldMatchTemplData != null) {
            byte[] matchTemplData = compareTemplSizeData();
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
    }

    //1:1将模板转换成可比对模板
    private byte[] matchTempl1_1Data() {
        byte[] match_1Templ = null;
        if (templSources == EXTERNAL_TEMPL_SOURCES) {
            //1:1模板数据外部传入
            if (templData_1 != null) {
                match_1Templ = templExechangeMatchTempl(templData_1);
            } else {
                return null;
            }
        } else if (templSources == DIR_TEMPL_SOURCES) {
            //1:1模板数据本地文件夹读取
            String dirPath = getAimPath();
            File file = new File(dirPath);
            File[] files = file.listFiles();
            for (File file1 : files) {
                String name = file1.getName();
                if (!name.contains(".dat")) {
                    name = name + ".dat";
                }
                if (name.equals(templNameID)) {
                    String path = file1.getAbsolutePath();
                    templData_1 = FileUtil.readFileToArray(new File(path));
                    //读取模板
                    templData_1 = perfectTemplData();
                    FileUtil.readFile(path, templData_1);
                    break;
                }
            }
//            String aimTemplPath = getAimPath() + File.separator + templNameID;
//            templData_1 = FileUtil.readFileToArray(new File(aimTemplPath));
            if (templData_1 != null) {
                match_1Templ = templExechangeMatchTempl(templData_1);
            } else {
//                templData_1 = null;
                return null;
            }
        }
        return match_1Templ;
    }

    private int DevImageMatchLength = 0;//图片数据的长度

    //抓取图片
    private byte[] tgDevGetFingerImg(Handler handler, Message message) {
        //message.what = DEV_IMG;
        byte[] match1_1ImgData;
        if (sImg) {
            match1_1ImgData = new byte[IMG_SIZE + T_SIZE];
            match1_1ImgData[0] = ((byte) 0xfe);
//            match1_1ImgData = new byte[IMG_SIZE];
        } else {
            match1_1ImgData = new byte[IMG_SIZE];
        }
        DevImageMatchLength = getTG661().TGGetDevImage(match1_1ImgData, GET_IMG_OUT_TIME);
        if (DevImageMatchLength >= 0) {
            message.arg1 = 11;
        } else if (DevImageMatchLength == -1) {
            getAP(context).play_time_out();
            message.arg1 = -1;
            handler.sendMessage(message);
        } else if (DevImageMatchLength == -2) {
            getAP(context).play_verifyFail();
            message.arg1 = -2;
            handler.sendMessage(message);
        } else if (DevImageMatchLength == -3) {
            //这里播放的语音应该是操作取消
            //getAP().play_vetify_fail();
            message.arg1 = -3;
            handler.sendMessage(message);
        } else if (DevImageMatchLength == -4) {
            getAP(context).play_verifyFail();
            message.arg1 = -4;
            handler.sendMessage(message);
        }
        return match1_1ImgData;
    }

    private void tgTempl1_1(Handler handler) {
        Message msg1_1 = handler.obtainMessage();
        msg1_1.what = FEATURE_COMPARE1_1;
        Bundle match1Bundle = new Bundle();
        byte[] matchTempl1_1Data = matchTempl1_1Data();
        if (matchTempl1_1Data != null) {
            getAP(context).play_inputDownGently();
            byte[] match1_1ImgData = tgDevGetFingerImg(handler, msg1_1);
            if (match1_1ImgData != null) {
                //提取特征
                byte[] match1_1Feature = new byte[FEATURE_SIZE];
                int tgImgExtractFeatureVerifyRes = getTGFV().TGImgExtractFeatureVerify(
                        match1_1ImgData, IMG_W, IMG_H, match1_1Feature);
                if (tgImgExtractFeatureVerifyRes == 0) {
                    byte[] update = perfectTemplData();
                    IntByReference int1_1 = new IntByReference();
                    int tgFeatureMatchTmpl11Res = getTGFV().TGFeatureMatchTmpl11(
                            match1_1Feature, matchTempl1_1Data, update, int1_1);
                    if (tgFeatureMatchTmpl11Res == 0) {
                        getAP(context).play_verifySuccess();
                        msg1_1.arg1 = 1;
                        msg1_1.obj = update;
                        int match1Score = int1_1.getValue();
                        if (backUpdateFingerData) {
                            //返回可更新的模板数据
                            match1Bundle.putByteArray(COMPARE_N_TEMPL, update);
                        }
                        match1Bundle.putString(COMPARE_NAME, templNameID);
                        match1Bundle.putInt(COMPARE_N_SCORE, match1Score);
                        //存储图片
                        tgSaveImg(msg1_1, match1Bundle, templNameID
                                , match1_1ImgData, DevImageMatchLength);
                        msg1_1.setData(match1Bundle);
                    } else if (tgFeatureMatchTmpl11Res == 7) {
                        getAP(context).play_verifyFail();
                        int match1Score = int1_1.getValue();
                        msg1_1.arg1 = 2;
                        match1Bundle.putInt(COMPARE_N_SCORE, match1Score);
                        //存储图片
                        tgSaveImg(msg1_1, match1Bundle, ""
                                , match1_1ImgData, DevImageMatchLength);
                        msg1_1.setData(match1Bundle);
                    } else if (tgFeatureMatchTmpl11Res == -1) {
                        getAP(context).play_verifyFail();
                        msg1_1.arg1 = 3;
                        //存储图片
                        tgSaveImg(msg1_1, match1Bundle, ""
                                , match1_1ImgData, DevImageMatchLength);
                    }
                } else if (tgImgExtractFeatureVerifyRes == 1) {
                    getAP(context).play_verifyFail();
                    msg1_1.arg1 = 4;
                    //存储图片
                    tgSaveImg(msg1_1, match1Bundle, ""
                            , match1_1ImgData, DevImageMatchLength);
                } else if (tgImgExtractFeatureVerifyRes == 2) {
                    getAP(context).play_verifyFail();
                    msg1_1.arg1 = 5;
                    //存储图片
                    tgSaveImg(msg1_1, match1Bundle, ""
                            , match1_1ImgData, DevImageMatchLength);
                } else if (tgImgExtractFeatureVerifyRes == 3) {
                    getAP(context).play_verifyFail();
                    msg1_1.arg1 = 6;
                    //存储图片
                    tgSaveImg(msg1_1, match1Bundle, ""
                            , match1_1ImgData, DevImageMatchLength);
                } else if (tgImgExtractFeatureVerifyRes == 4) {
                    getAP(context).play_verifyFail();
                    msg1_1.arg1 = 7;
                    //存储图片
                    tgSaveImg(msg1_1, match1Bundle, ""
                            , match1_1ImgData, DevImageMatchLength);
                } else if (tgImgExtractFeatureVerifyRes == 5) {
                    getAP(context).play_verifyFail();
                    msg1_1.arg1 = 8;
                    //存储图片
                    tgSaveImg(msg1_1, match1Bundle, ""
                            , match1_1ImgData, DevImageMatchLength);
                } else if (tgImgExtractFeatureVerifyRes == -1) {
                    getAP(context).play_time_out();
                    msg1_1.arg1 = 9;
                    //存储图片
                    tgSaveImg(msg1_1, match1Bundle, ""
                            , match1_1ImgData, DevImageMatchLength);
                }
            } else {
                getAP(context).play_verifyFail();
                msg1_1.arg1 = -2;
            }
        }
        handler.sendMessage(msg1_1);
    }

    private void tgTempl1_N(Handler handler) {
        Message msg = handler.obtainMessage();
        msg.what = FEATURE_COMPARE1_N;
        Bundle bundle = new Bundle();
        getAP(context).play_inputDownGently();
        byte[] match1_NImgData = tgDevGetFingerImg(handler, msg);
        if (DevImageMatchLength > 0) {
            //提取特征
            byte[] match1_NFeature = new byte[FEATURE_SIZE];
            int tgImgExtractFeatureVerifyNRes = getTGFV().TGImgExtractFeatureVerify(
                    match1_NImgData, IMG_W, IMG_H, match1_NFeature);
            if (tgImgExtractFeatureVerifyNRes == 0) {
                //分流比对
//                excutorsFile(match1_NFeature, DevImageMatchLength, match1_NImgData,
//                        handler, matchNMsg, matchNBundle);
                compareN(templCount, READ_COMPARE_COUNT, match1_NFeature,
                        handler, match1_NImgData);
//                if (allFingerMatchDatas != null) {
//
//
//                    IntByReference intB1 = new IntByReference();
//                    IntByReference intB2 = new IntByReference();
//                    byte[] uuId = new byte[UUID_SIZE];
//                    byte[] updateTempl = perfectTemplData();
//                    int tgFeatureMatchTmpl1NRes = getTGFV().TGFeatureMatchTmpl1N(match1_NFeature,
//                            allFingerMatchDatas, templCount, intB1, uuId, intB2, updateTempl);
//                    if (tgFeatureMatchTmpl1NRes == 0) {
//                        getAP(context).play_verifySuccess();
//                        int templIndex = intB1.getValue();//模板的指针位置
//                        int templScore = intB2.getValue();//验证的分数
//                        matchNMsg.arg1 = 1;
//                        if (backUpdateFingerData) {
//                            //返回可更新的模板数据
//                            matchNBundle.putByteArray(COMPARE_N_TEMPL, updateTempl);
//                        }
//                        //根据返回的指针获取主机中模板文件的名字
//                        String fileName = FileUtil.getFileName(templsPath, templIndex - 1);
//                        if (templSources == DIR_TEMPL_SOURCES) {
//                            matchNBundle.putString(COMPARE_NAME, fileName);
//                        } else if (templSources == EXTERNAL_TEMPL_SOURCES) {
//                            matchNBundle.putInt(INDEX, templIndex);
//                        }
//                        matchNBundle.putInt(COMPARE_N_SCORE, templScore);
//                        //存储图片
//                        tgSaveImg(matchNMsg, matchNBundle, fileName, match1_NImgData, DevImageMatchLength);
//                        matchNMsg.setData(matchNBundle);
//                    } else if (tgFeatureMatchTmpl1NRes == 8) {
//                        int templIndex = intB1.getValue();//模板的指针位置
//                        int templScore = intB2.getValue();//验证的分数
//                        getAP(context).play_verifyFail();
//                        matchNMsg.arg1 = 2;
//                        matchNBundle.putInt(COMPARE_N_SCORE, templScore);
//                        //存储图片
//                        tgSaveImg(matchNMsg, matchNBundle, "", match1_NImgData, DevImageMatchLength);
//                        matchNMsg.setData(matchNBundle);
//                    } else if (tgFeatureMatchTmpl1NRes == -1) {
//                        getAP(context).play_time_out();
//                        matchNMsg.arg1 = 3;
//                        //存储图片
//                        tgSaveImg(matchNMsg, matchNBundle, "", match1_NImgData, DevImageMatchLength);
//                    }
//                }
            } else if (tgImgExtractFeatureVerifyNRes == 1) {
                getAP(context).play_verifyFail();
                msg.arg1 = 4;
                //存储图片
                tgSaveImg(msg, bundle, "", match1_NImgData, DevImageMatchLength);
            } else if (tgImgExtractFeatureVerifyNRes == 2) {
                getAP(context).play_verifyFail();
                msg.arg1 = 5;
                //存储图片
                tgSaveImg(msg, bundle, "", match1_NImgData, DevImageMatchLength);
            } else if (tgImgExtractFeatureVerifyNRes == 3) {
                getAP(context).play_verifyFail();
                msg.arg1 = 6;
                //存储图片
                tgSaveImg(msg, bundle, "", match1_NImgData, DevImageMatchLength);
            } else if (tgImgExtractFeatureVerifyNRes == 4) {
                getAP(context).play_verifyFail();
                msg.arg1 = 7;
            } else if (tgImgExtractFeatureVerifyNRes == 5) {
                getAP(context).play_verifyFail();
                msg.arg1 = 8;
                //存储图片
                tgSaveImg(msg, bundle, "", match1_NImgData, DevImageMatchLength);
            } else if (tgImgExtractFeatureVerifyNRes == -1) {
                getAP(context).play_verifyFail();
                msg.arg1 = 9;
                //存储图片
                tgSaveImg(msg, bundle, "", match1_NImgData, DevImageMatchLength);
            }
        }
        handler.sendMessage(msg);
    }


    private void compareN(int fingerCount, int readBase, byte[] match1_NFeature,
                          Handler handler, byte[] imgData) {
        if (fingerCount > 0 && readBase > 0) {
            double v = (double) fingerCount / readBase;
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
                k = 0;
                for (int i = 0; i < count; i++) {
                    byte[] cellFingerData = null;
                    int compareFingerCount = 0;
                    if (i == floor) {
                        //除不尽，最后一项
                        compareFingerCount = fingerCount - readBase * i;
                        if (templModelType == TEMPL_MODEL_3) {
                            cellFingerData = new byte[compareFingerCount * WAIT_COMPARE_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            cellFingerData = new byte[compareFingerCount * WAIT_COMPARE_FEATURE_6];
                        }
                    } else {
                        compareFingerCount = readBase;
                        if (templModelType == TEMPL_MODEL_3) {
                            cellFingerData = new byte[readBase * WAIT_COMPARE_FEATURE_3];
                        } else if (templModelType == TEMPL_MODEL_6) {
                            cellFingerData = new byte[readBase * WAIT_COMPARE_FEATURE_6];
                        }
                    }
                    if (cellFingerData != null) {
                        System.arraycopy(allFingerMatchDatas, readBase * i,
                                cellFingerData, 0, cellFingerData.length);
                        workCompareData(match1_NFeature, cellFingerData, compareFingerCount,
                                handler, imgData, count);
                    }
                }
            }
        }
    }

    //创建线程池
//    private int cardinal = 2000;//1:N的基数设定为300，提高比对的效率
//
//    private void excutorsFile(byte[] newFingerData,
//                              int imgLength, byte[] imgData,
//                              Handler handler, Message message,
//                              Bundle bundle) {
////        File file = new File(datPath);
////        if (!file.exists()) {
////            return null;
////        } else {
////            if (file.isDirectory()) {
////                File[] files = file.listFiles();
////                if (files.length > 0) {
////                    int datSize = files.length;
//
//        double v = (double) templCount / cardinal;
//        //四舍五入向上取整，1：N会比对的次数
//        int ceil = (int) Math.ceil(v);
//        //四舍五入向下取整
//        int floor = (int) Math.floor(v);
//        int value;
//        if (ceil != floor) {
//            value = ceil;
//        } else {
//            //如果ceil和floor相等，则正好除尽
//            value = floor;
//        }
//        //为线程池添加事件
//        if (value > 0) {
//            int count = 0;
//            Log.d("===LLL", "   value:" + value);
//            for (int i = 0; i < value; i++) {
//                byte[] cellFingerData = null;
//                int srcLength = 0;
//                if (value == ceil && i == floor) {
//                    //除不尽，最后一项
//                    srcLength = templCount - cardinal * i;
//                    cellFingerData = new byte[srcLength * WAIT_COMPARE_FEATURE_6];
//                } else {
//                    srcLength = cardinal;
//                    cellFingerData = new byte[cardinal * WAIT_COMPARE_FEATURE_6];
//                }
//                Log.d("===LLL", "     srcLength ； " + cellFingerData.length);
//                System.arraycopy(allFingerMatchDatas, cardinal * i,
//                        cellFingerData, 0, cellFingerData.length);
//                String aimPath = getAimPath();
//                byte[] updateTempl = perfectTemplData();
//
////                IntByReference intB1 = new IntByReference();
////                IntByReference intB2 = new IntByReference();
////                byte[] uuId = new byte[UUID_SIZE];
////                byte[] updateTempl1 = perfectTemplData();
////                int tgFeatureMatchTmpl1NRes = getTGFV().TGFeatureMatchTmpl1N(newFingerData,
////                        cellFingerData, srcLength, intB1, uuId, intB2, updateTempl);
////                Log.d("===LLL","  N比对结果："+tgFeatureMatchTmpl1NRes);
//
//                GetFileTask getContentTask = new GetFileTask(newFingerData, cellFingerData,
//                        srcLength, aimPath, imgLength, imgData, updateTempl,
//                        /*handler, */message, bundle, sImg, value);
//                //添加任务
//                ecs.submit(getContentTask);
//                count++;
//            }
//            for (int i = 0; i < value; i++) {
//                try {
//                    Future<MatchN> take = ecs.take();
//                    MatchN matchN = take.get();
//                    int resultCode = matchN.getResultCode();
//                    if (resultCode == 1) {
//                        getAP(context).play_verifySuccess();
//                        break;
//                    } else {
//                        if (i == count - 1) {
//                            if (resultCode == 2) {
//                                getAP(context).play_verifyFail();
//                            } else if (resultCode == 3) {
//                                getAP(context).play_time_out();
//                            }
//                        }
//                    }
//                    Log.d("===HHH", "   数量：" + count + "  结果码：" + resultCode);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private void tgGetFVVersion(Handler handler) {
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
    }

    private void tgGetTemplSN(Handler handler) {
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
    }

    private void tgGetTemplFW(Handler handler) {
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
    }

    private void tgGetTemplTime(Handler handler) {
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
            byte[] timeData = new byte[TIME_SIZE];
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
    }

    private void tgDelHostIdTempl(Handler handler) {
        Message removeMsg = handler.obtainMessage();
        removeMsg.what = DELETE_HOST_ID_TEMPL;
        String path = getAimPath();
        boolean removeFile = FileUtil.removeFile(path, templNameID);
        if (removeFile) {
            removeMsg.arg1 = 1;
            //删除模板后更新缓存中的所有模板数据
            allFingerMatchDatas = new byte[0];
//            initMatchTemplDatas();
            tgInitMatchDatas();
        } else {
            removeMsg.arg1 = -1;
        }
        handler.sendMessage(removeMsg);
    }

    private void tgDelAllHostTempl(Handler handler) {
        Message deleteAllMsg = handler.obtainMessage();
        deleteAllMsg.what = DELETE_HOST_ALL_TEMPL;
        String deletePath = getAimPath();
        boolean removeAllFile = FileUtil.removeAllFile(deletePath);
        if (removeAllFile) {
            deleteAllMsg.arg1 = 1;
            //删除模板后更新缓存中的所有模板数据
            allFingerMatchDatas = new byte[0];
//            initMatchTemplDatas();
            tgInitMatchDatas();
        } else {
            deleteAllMsg.arg1 = -1;
        }
        handler.sendMessage(deleteAllMsg);
    }

    private void tgUpdateHostIdTempl(Handler handler) {
        if (updateHostTempl != null) {
            String updateFilePath = getAimPath();
            boolean updateFile = FileUtil.updateFile(updateFilePath,
                    templNameID, updateHostTempl);
            Message updateHostMsg = handler.obtainMessage();
            updateHostMsg.what = UPDATE_HOST_TEMPL;
            updateHostMsg.obj = updateFile;
            handler.sendMessage(updateHostMsg);
        }
    }

    /*----------------------封装的SDK内部功能性方法 End----------------------*/

    //更新可比对的数据模板
    private void readFingerExchangedFinger(int fingerSourceType, int readBase) {
        int fingerCount;
        if (fingerSourceType == DIR_TEMPL_SOURCES) {
            String aimPath = getAimPath();
            File file = new File(aimPath);
            File[] files = file.listFiles();
            templCount = fingerCount = files.length;
            if (fingerCount > 0 && readBase > 0) {
                double v = (double) fingerCount / readBase;
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
                    c = 0;
                    allFingerMatchDatas = templSizeData(fingerCount);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TGDialogUtil.Instance().showWaitDialog(context, "数据更新中...");
                        }
                    });
                    for (int i = 0; i < count; i++) {
                        int startSrc = readBase * i;
                        int readLength = 0;
                        if (i == floor) {
                            //除不尽，最后一项
                            readLength = fingerCount - readBase * i;
                        } else {
                            readLength = readBase;
                        }
                        // work(handler, READ_AND_EXCHANGED);
                        workReadData(files, startSrc, readLength);
                    }
                }
            }
        } else if (fingerSourceType == EXTERNAL_TEMPL_SOURCES) {
            allFingerMatchDatas = new byte[0];
            workReadData(null, -1, -1);
        }
    }

    private void readFingerAndExchanged(File[] files, int startRead, int readLength
            , ReadFingerExchangedResult readFingerExchangedResult) {
        if (files.length > 0) {
            File[] iFile = new File[readLength];
            System.arraycopy(files, startRead, iFile, 0, readLength);
            byte[] lengthByte = templSizeData(readLength);
            for (int i = 0; i < readLength; i++) {
                File file = iFile[i];
                byte[] bytes = perfectTemplData();
                FileUtil.readFileToArray(file, bytes);
                byte[] matchFingerData = templExechangeMatchTempl(bytes);
                int compareTemplLength = templLength() * i;
                System.arraycopy(matchFingerData, 0, lengthByte,
                        compareTemplLength, matchFingerData.length);
            }
            readFingerExchangedResult.fingerCompareTemplResult(lengthByte);
        }
    }

    private ExecutorService workExecutors;
    private ExecutorCompletionService<Object> workEsc;
    private int c = 0;

    private void workReadData(final File[] files, final int startRead, final int readLength) {
        if (workExecutors == null) {
            workExecutors = Executors.newCachedThreadPool();
            workEsc = new ExecutorCompletionService<>(workExecutors);
        }
        workEsc.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                getThreadName("更新模板数据");
                if (templSources == DIR_TEMPL_SOURCES) {
                    readFingerAndExchanged(files, startRead, readLength,
                            new ReadFingerExchangedResult() {
                                @Override
                                public void fingerCompareTemplResult(final byte[] lengthByte) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (lengthByte != null && lengthByte.length > 0) {
                                                System.arraycopy(lengthByte, 0,
                                                        allFingerMatchDatas, c,
                                                        lengthByte.length);
                                                c = c + lengthByte.length;
                                                if (c == allFingerMatchDatas.length) {
                                                    TGDialogUtil.Instance().disDialog();
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                } else if (templSources == EXTERNAL_TEMPL_SOURCES) {
                    getAllFingerData1();
                }
                return null;
            }
        });
    }

    private int k = 0;

    private void workCompareData(final byte[] matchNFeature, final byte[] cellCompareData
            , final int compareFingerCount, final Handler handler
            , final byte[] imgData, final int count) {
        if (workExecutors == null) {
            workExecutors = Executors.newCachedThreadPool();
            workEsc = new ExecutorCompletionService<>(executors);
        }
        workEsc.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                compareFinger(cellCompareData, compareFingerCount, matchNFeature, new CompareNResult() {
                    @Override
                    public void compareResult(final IntByReference intB1, final IntByReference intB2,
                                              final int tgFeatureMatchTmpl1NRes, final byte[] updateTempl) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = handler.obtainMessage();
                                msg.what = FEATURE_COMPARE1_N;
                                Bundle bundle = new Bundle();
                                Log.d("===LLL", "  验证的结果N:" + tgFeatureMatchTmpl1NRes);
                                if (tgFeatureMatchTmpl1NRes == 0) {
                                    getAP(context).play_verifySuccess();
                                    int templIndex = intB1.getValue();//模板的指针位置
                                    int templScore = intB2.getValue();//验证的分数
                                    msg.arg1 = 1;
                                    if (backUpdateFingerData) {
                                        //返回可更新的模板数据
                                        bundle.putByteArray(COMPARE_N_TEMPL, updateTempl);
                                    }
                                    //根据返回的指针获取主机中模板文件的名字
                                    String templsPath = getAimPath();
                                    String fileName = FileUtil.getFileName(templsPath, templIndex - 1);
                                    if (templSources == DIR_TEMPL_SOURCES) {
                                        bundle.putString(COMPARE_NAME, fileName);
                                    } else if (templSources == EXTERNAL_TEMPL_SOURCES) {
                                        bundle.putInt(INDEX, templIndex);
                                    }
                                    bundle.putInt(COMPARE_N_SCORE, templScore);
                                    //存储图片
                                    tgSaveImg(msg, bundle, fileName, imgData, DevImageMatchLength);
                                    msg.setData(bundle);
                                } else if (tgFeatureMatchTmpl1NRes == 8) {
                                    if (k == count - 1) {
                                        //int templIndex = intB1.getValue();//模板的指针位置
                                        int templScore = intB2.getValue();//验证的分数
                                        getAP(context).play_verifyFail();
                                        msg.arg1 = 2;
                                        bundle.putInt(COMPARE_N_SCORE, templScore);
                                        //存储图片
                                        tgSaveImg(msg, bundle, "", imgData, DevImageMatchLength);
                                        msg.setData(bundle);
                                    }
                                } else if (tgFeatureMatchTmpl1NRes == -1) {
                                    if (k == count - 1) {
                                        getAP(context).play_time_out();
                                        msg.arg1 = 3;
                                        //存储图片
                                        tgSaveImg(msg, bundle, "", imgData, DevImageMatchLength);
                                    }
                                }
                                k++;
                                handler.sendMessage(msg);
                            }
                        });
                    }
                });
                return null;
            }
        });
    }

    private void compareFinger(byte[] cellCompareData, int compareFingerCount, byte[] match1_NFeature
            , CompareNResult compareNResult) {
        if (allFingerMatchDatas != null && allFingerMatchDatas.length > 0) {
            IntByReference intB1 = new IntByReference();
            IntByReference intB2 = new IntByReference();
            byte[] uuId = new byte[UUID_SIZE];
            byte[] updateTempl = perfectTemplData();
            int tgFeatureMatchTmpl1NRes = getTGFV().TGFeatureMatchTmpl1N(match1_NFeature,
                    cellCompareData, compareFingerCount, intB1, uuId, intB2, updateTempl);
            if (compareNResult != null) {
                compareNResult.compareResult(intB1, intB2, tgFeatureMatchTmpl1NRes, updateTempl);
            }
        }
    }

    interface ReadFingerExchangedResult {
        void fingerCompareTemplResult(byte[] lengthByte);
    }

    interface CompareNResult {
        void compareResult(IntByReference intB1, IntByReference intB2, int tgFeatureMatchTmpl1NRes,
                           byte[] updateTempl);
    }

    //模板数据初始化
    private byte[] allFingerMatchDatas = null;//可比对的指静脉模板数据
    private boolean firstOpen = false;//标记是否是第一次注册模板

    private void initMatchTemplDatas() {
        if (templSources == DIR_TEMPL_SOURCES) {
            allFingerMatchDatas = new byte[0];
            getAllFingerData2();
        } else if (templSources == EXTERNAL_TEMPL_SOURCES) {
            allFingerMatchDatas = new byte[0];
            getAllFingerData1();
        }
        if (allFingerMatchDatas != null && allFingerMatchDatas.length > 0) {
            Log.d("===LLL", "模板数据初始化成功,  模板数量：" + templCount);
        } else {
            //初始没有模板的情况下赋值
            allFingerMatchDatas = compareTemplSizeData();
            templCount = 1;
            firstOpen = true;
        }
    }

    //更新代码中缓存的数据
    private void tgInitMatchDatas() {
        readFingerExchangedFinger(templSources, READ_COMPARE_COUNT);
        if (allFingerMatchDatas != null && allFingerMatchDatas.length > 0) {
            Log.d("===LLL", "模板数据初始化成功,  模板数量：" + templCount);
        } else {
            //初始没有模板的情况下赋值
            allFingerMatchDatas = compareTemplSizeData();
            templCount = 1;
            firstOpen = true;
        }
//        work(null, INIT_MATCH_DATA);
    }

    //更新代码缓存中的模板数据以及数量
    private void updateCodeTemplDataCount(int flag, byte[] fusionTempl, int delIndex) {
        //更新代码缓存中的模板数据
        //1.将融合后的模板数据转成可比对的模板数据
        //2.将可比对的模板数据更新到代码缓存中的模板数据中
        if (flag == INCREASE_FINGER_TEMPL) {
            int templLength = templLength();
            //增加一个模板
            byte[] matchFingerData = templExechangeMatchTempl(fusionTempl);
            allFingerMatchDatas = ArrayUtil.subIncreaseAtIndexBytes(allFingerMatchDatas,
                    templCount, matchFingerData, templLength);
            templCount = templCount + 1;
        } else if (flag == REDUCE_FINGER_TEMPL) {
            int templLength = templLength();
//            if (firstOpen) {
//                //是第一次注册模板，删掉初始去重的占位空模板
//                allFingerMatchDatas = ArrayUtil.subReduceAtIndexBytes(allFingerMatchDatas, templCount,
//                        delIndex, templLength);
//                firstOpen = false;
//            } else {
            //减少一个模板
            allFingerMatchDatas = ArrayUtil.subReduceAtIndexBytes(allFingerMatchDatas, templCount,
                    delIndex, templLength);
//            }
            templCount = templCount - 1;
        }
    }

    /**
     * 更新增加的模板数据
     *
     * @param matchTempl 新的可比对的模板数据
     */
    private void addTemlpData(byte[] matchTempl) {
        int templLength = templLength();
        byte[] newByte = new byte[templLength * (templCount + 1)];
        System.arraycopy(allFingerMatchDatas, 0, newByte, 0, allFingerMatchDatas.length);
        System.arraycopy(matchTempl, 0, newByte, allFingerMatchDatas.length, matchTempl.length);
        templCount = templCount + 1;
        allFingerMatchDatas = newByte;
    }

    //将传递进来的实体数据进行提取,,缓存的是可比对模板的数据
    private int templCount = 0;//模板的数量

    //将外部传进来的数据全部转换成可比对模板
    private void getAllFingerData1() {
        if (allTemplData != null && allTemplData.size() > 0) {
            templCount = allTemplData.size();
            allFingerMatchDatas = templSizeData(templCount);
            for (int i = 0; i < templCount; i++) {
                byte[] fingerData = allTemplData.get(i).getFingerData();
                //获取可比对的模板
                byte[] matchFingerData = templExechangeMatchTempl(fingerData);
                final byte[] allFingerMatchDatas1 = jointTempls(matchFingerData, i);
                if (i == templCount - 1) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            allFingerMatchDatas = allFingerMatchDatas1;
                        }
                    });
                }
            }
        }
//        allTemplData = null;
    }

    //获取目标文件夹下的所有模板,转换成可比对模板
    private void getAllFingerData2() {
        //获取模板的所有地址
        String templsPath = getAimPath();
        //读取所有模板文件
        ArrayList<byte[]> allTemplByteList = readAllTempl(templsPath);
        //一一转换成可比对模板
        if (allTemplByteList != null && allTemplByteList.size() > 0) {
            templCount = allTemplByteList.size();
            allFingerMatchDatas = templSizeData(templCount);
            for (int i = 0; i < templCount; i++) {
                byte[] templData = allTemplByteList.get(i);
                byte[] matchFingerData = templExechangeMatchTempl(templData);
                final byte[] allFingerMatchDatas1 = jointTempls(matchFingerData, i);
                if (i == templCount - 1) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            allFingerMatchDatas = allFingerMatchDatas1;
                        }
                    });
                }
            }
        }
//        allTemplByteList = null;
    }

    //将模板装换成可比对模板
    private byte[] templExechangeMatchTempl(byte[] templData) {
        byte[] comPareTemplData = compareTemplSizeData();
        int tgTmplToMatchTmpl1_NRes = getTGFV().TGTmplToMatchTmpl(templData, comPareTemplData);
        if (tgTmplToMatchTmpl1_NRes == 0) {
            return comPareTemplData;
        } else if (tgTmplToMatchTmpl1_NRes == -1) {
            int k = 0;
            boolean continueMatch = true;
            while (continueMatch) {
                if (k < 3) {
                    tgTmplToMatchTmpl1_NRes = getTGFV().
                            TGTmplToMatchTmpl(templData, comPareTemplData);
                    k++;
                    if (tgTmplToMatchTmpl1_NRes == 0) {
                        k = 0;
                        continueMatch = false;
                    }
                }
            }
        }
        return comPareTemplData;
    }

    //拼接模板
    private byte[] jointTempls(byte[] matchFingerData, int index) {
        if (matchFingerData != null) {
            int destStart = 0;
            if (templModelType == TEMPL_MODEL_3) {
                destStart = WAIT_COMPARE_FEATURE_3 * index;
            } else if (templModelType == TEMPL_MODEL_6) {
                destStart = WAIT_COMPARE_FEATURE_6 * index;
            }
            System.arraycopy(matchFingerData, 0, allFingerMatchDatas,
                    destStart, matchFingerData.length);
        }
        return allFingerMatchDatas;
    }

    //模板的数据长度缓存
    private byte[] templSizeData(int templCount) {
        byte[] allFingerData = null;
        if (templModelType == TEMPL_MODEL_3) {
            allFingerData = new byte[WAIT_COMPARE_FEATURE_3 * templCount];
        } else if (templModelType == TEMPL_MODEL_6) {
            allFingerData = new byte[WAIT_COMPARE_FEATURE_6 * templCount];
        }
        return allFingerData;
    }

    ///获取当前特征模式下的模板长度
    private int templLength() {
        int cellTemplLength = 0;
        if (templModelType == TEMPL_MODEL_3) {
            cellTemplLength = WAIT_COMPARE_FEATURE_3;
        } else if (templModelType == TEMPL_MODEL_6) {
            cellTemplLength = WAIT_COMPARE_FEATURE_6;
        }
        return cellTemplLength;
    }

    //比对模板的数据长度
    private byte[] compareTemplSizeData() {
        byte[] matchTemplData = null;
        if (templModelType == TEMPL_MODEL_3) {
            matchTemplData = new byte[WAIT_COMPARE_FEATURE_3];
        } else if (templModelType == TEMPL_MODEL_6) {
            matchTemplData = new byte[WAIT_COMPARE_FEATURE_6];
        }
        return matchTemplData;
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

    //获取当前线程的名字
    private void getThreadName(String workName) {
        String name = Thread.currentThread().getName();
        long id = Thread.currentThread().getId();
        Log.d("===HHH", workName + "   当前线程的名称：" + name + "  id:" + id);
    }


    private void showWaitDialog(int type, String tip) {
        Message dialogMsg = handler.obtainMessage();
        dialogMsg.what = WAIT_DIALOG;
        dialogMsg.arg1 = type;
        dialogMsg.obj = tip;
        handler.sendMessage(dialogMsg);
    }

    /**
     * 获取特征数量类型下对应路径下的文件名列表
     */
    public ArrayList<String> getAimFileList() {
        String aimPath = getAimPath();
        ArrayList<String> fileNameList = scanAimDirFileName(aimPath);
        return fileNameList;
    }

    private boolean checkTemplName(String newTemplName) {
        //检查模板文件的名称是否重复
        if (!TextUtils.isEmpty(newTemplName) && !newTemplName.contains(".dat")) {
            newTemplName = newTemplName + ".dat";
        }
        String templsPath = getAimPath();
        ArrayList<String> allTemplName = scanAimDirFileName(templsPath);
        if (allTemplName != null && allTemplName.size() > 0) {
            for (int i = 0; i < allTemplName.size(); i++) {
                String templName = allTemplName.get(i);
                if (templName.equals(newTemplName)) {
                    hasTemplName = true;
                    break;
                }
                if (i == allTemplName.size() - 1 && templName.equals(newTemplName)) {
                    hasTemplName = false;
                }
            }
        }
        return hasTemplName;
    }

    //读取所有文件模板
    private ArrayList<byte[]> readAllTempl(String aimPath) {
        ArrayList<byte[]> templsByte = null;
        File file = new File(aimPath);
        if (!file.exists()) {
            return null;
        } else {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    if (files.length > 0) {
                        templsByte = new ArrayList<>();
                        for (File file1 : files) {
                            byte[] bytes = perfectTemplData();
                            String name = file1.getName();
                            long length = file1.length();
//                            Log.d("===LLL", "   文件:" + name + " 的长度 ：" + length + "   数量：" + v);
//                        byte[] file1Data = FileUtil.readFile(file1);
                            FileUtil.readFileToArray(file1, bytes);
//                        FileUtil.readFile(file1, bytes);
//                        byte[] bytes1 = FileUtil.readFileToArray(file1);
                            templsByte.add(bytes);
                        }
                    }
                }
            }
        }
        return templsByte;
    }

    //检测当前指静脉是否已经注册
    private void checkFingetRegister(Handler handler) {
        Message imgFeaNMsg = handler.obtainMessage();
        imgFeaNMsg.what = FEATURE_COMPARE1_N;
        getAP(context).play_inputDownGently();
        byte[] match1_NImgData = tgDevGetFingerImg(handler, imgFeaNMsg);
        if (DevImageMatchLength > 0) {
            //传出抓取图片的数据
            if (sImg) {
                imgFeaNMsg.arg1 = 1;
                //存储图片
                tgSaveImg(imgFeaNMsg, null, "", match1_NImgData, DevImageMatchLength);
            }
            //提取特征
            byte[] match1_NFeature = new byte[FEATURE_SIZE];
            int tgImgExtractFeatureVerifyNRes = getTGFV().TGImgExtractFeatureVerify(
                    match1_NImgData, IMG_W, IMG_H, match1_NFeature);
            if (tgImgExtractFeatureVerifyNRes == 0) {
                if (allFingerMatchDatas != null) {
                    IntByReference intB1 = new IntByReference();
                    IntByReference intB2 = new IntByReference();
                    byte[] uuId = new byte[UUID_SIZE];
                    byte[] updateTempl = null;
                    if (templModelType == TEMPL_MODEL_3) {
                        updateTempl = new byte[PERFECT_FEATURE_3];
                    } else if (templModelType == TEMPL_MODEL_6) {
                        updateTempl = new byte[PERFECT_FEATURE_6];
                    }
                    int tgFeatureMatchTmpl1NRes = getTGFV().TGFeatureMatchTmpl1N(match1_NFeature,
                            allFingerMatchDatas, templCount, intB1, uuId,
                            intB2, updateTempl);
                    if (tgFeatureMatchTmpl1NRes == 0) {
                        //该指静脉已经注册
                        getAP(context).play_registerRepeat();
                        this.lastTemplName = "";
                        isCheck = true;
                        hasTempl = true;
                        imgFeaNMsg.arg1 = -5;
                    } else if (tgFeatureMatchTmpl1NRes == 8) {
//                            imgFeaNMsg.arg1 = 1;
                        //该指静脉尚未注册，记录特征
                        isCheck = true;
                        hasTempl = false;
                        int firstRegister = getTGFV().TGImgExtractFeatureRegister(
                                match1_NImgData, IMG_W, IMG_H, match1_NFeature);
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
            handler.sendMessage(imgFeaNMsg);
        }
//        } else {
//            isCheck = true;
//        }
    }


    //对外传图
    private void img(Message msg, Bundle bundle, byte[] imgData, int length) {
        //String imgPath = this.imgPath + File.separator + imgName + ".jpg";
//        long l = System.currentTimeMillis();
//        String iIMG = userFingerDirPath + File.separator + userFingerName + "_" + imgName + "_" + l + ".jpg";
//        byte[] jpegData = new byte[IMG_W * IMG_H/*imgLength*/];
//        System.arraycopy(imgData, /*1024 * 256*/208, jpegData, 0, jpegData.length);
//
//        //内部测试用
//        byte[] imgDataBMP = ImgExechangeBMP.instance().imgExechangeBMP(jpegData, jpegData.length, 1, iIMG);

//        msg.arg1 = 0;
        msg.obj = imgData;
        //传出抓取图片的数据
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt("imgLength", length);
        bundle.putByteArray("imgData", imgData/*imgDataBMP*/);
        msg.setData(bundle);
    }

    //存储图片
    private boolean saveImg(String imgName, byte[] imgData, int imgLength) {
        if (imgName.contains(".dat")) {
            imgName = imgName.substring(0, imgName.indexOf(".dat"));
        }
        String imgPath = this.imgPath + File.separator + imgName + ".jpg";
//        long l = System.currentTimeMillis();
//        String iIMG = userFingerDirPath + File.separator + userFingerName + "_" + imgName + "_" + l + ".jpg";
        byte[] jpegData = new byte[imgLength];
        System.arraycopy(imgData, 1024 * 256, jpegData, 0, imgLength);
        boolean imgSave = FileUtil.writeFile(jpegData, imgPath);
        return imgSave;
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

//    private String userFingerDirPath;

    //存储图片
    public void tgSaveImg(Message message, Bundle bundle, String fileName,
                          byte[] imageData, int imgLength) {
        //传出抓取图片的数据
        if (sImg) {
            //bundle.putInt("imgLength", imgLength);
            //bundle.putByteArray("imgData", imageData);
//            userFingerDirPath = imgPath + File.separator + userFingerName;
//            File file = new File(userFingerDirPath);
//            if (!file.exists()) file.mkdirs();
//            File[] files = file.listFiles();
//            if (files.length >= 10) {
//                bundle.putString("tip", "验证采图的数量已经达到10");
//                return;
//            }
            img(message, bundle, imageData, imgLength);
            //存储图片
            if (TextUtils.isEmpty(fileName)) {
                long l = System.currentTimeMillis();
                fileName = String.valueOf(l);
            }
            saveImg(fileName, imageData, imgLength);
        }
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

    //写入证书到指定文件
    private void writeLicenseToFile(InputStream inputStream) {
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
                FV_InitAct(handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    //修改系统USB权限
    private void writeCMD() {
        String command = "chmod -R 777 /dev/bus/usb";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            int i = process.waitFor();
            Log.d("===LLL", "    写入su命令  " + i);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    //private boolean isStart = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            devServiceMessenger = new Messenger(iBinder);
            //isStart = true;
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
            }
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            devServiceMessenger = null;
            //isStart = false;
        }
    };


}
