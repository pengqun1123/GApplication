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
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.sd.tgfinger.CallBack.DevModelCallBack;
import com.sd.tgfinger.R;
import com.sd.tgfinger.tgApi.Constant;
import com.sd.tgfinger.tgApi.TGFV;
import com.sd.tgfinger.tgApi.TGXG661API;
import com.sd.tgfinger.tgexecutor.TgExecutor;
import com.sd.tgfinger.utils.AudioProvider;
import com.sd.tgfinger.utils.DevRootUtil;
import com.sd.tgfinger.utils.FileUtil;
import com.sd.tgfinger.utils.LogUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * Created By pq
 * on 2019/7/18
 * 后比带有回调接口的API
 *
 */
public class TG661J {

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
    public static final int PERFECT_FEATURE_3 = 3248;//3特征
    public static final int PERFECT_FEATURE_6 = 6464;//6特征
    public static final int IMG_SIZE = 500 * 200 + 208;
    public static final int IMG_W = 500;
    public static final int IMG_H = 200;
    public static final int FEATURE_SIZE = 2384;
    //UUID的byte占位大小
    public static final int UUID_SIZE = 33;
    //时间值的占位大小
    public static final int TIME_SIZE = 15;

    //临时加的图片大小
    public static final int T_SIZE = 1024 * 500;
    public static final int GET_IMG_OUT_TIME = 15;//默认设置抓图超时的时间为15S

    //启动目标service的Action

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
    private static final String SDK_VERSION = "1.2.2_190703_Beta";
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
    //标记是否连续验证
    private boolean continueVerify = false;
    //1:N验证 N的基数
    private int verifyBaseCount;

    //标记 网络下发证书
    private boolean netLoadLicence;
    //证书的数据流
    private InputStream inputStream;

    private TG661J() {
        //创建线程池
        executor = TgExecutor.getExecutor();
//        ecs = new ExecutorCompletionService<>(executor);
    }

    //获取代理对象
    private static TG661J tgapi = null;

    public static TG661J getTGAPI() {
        if (tgapi == null) {
            synchronized (TG661J.class) {
                if (tgapi == null) {
                    tgapi = new TG661J();
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

    //权限：读写文件----》调用者请自行申请文件读写权限，获取权限后进行初始化
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
     * 初始化:请申请权限后再调用初始化接口
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
        CMD();
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
     * 创建相关的文件夹,获取到相关的路径
     */
    private void createDirPath() {
        File dat3File = new File(behindTempl3Path);
        if (!dat3File.exists()) {
            boolean mkdirs = dat3File.mkdirs();
        }
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
     * 初始化证书
     * 1: 初始化成功,算法接口有效
     * 2: 初始化失败,因证书路径错误,算法接口无效
     * 3: 初始化失败,因证书内容无效,算法接口无效
     * 4: 初始化失败,因证书内容过期,算法接口无效
     * -1:算法初始化失败
     */
    private int InitLicense(Context context) {
        int licenceRes = -1;
        if (netLoadLicence) {
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

    /*****************************以上是算法，本地存储，初始化*******************************/

    /**
     * 后比音量加
     */
    public boolean increaseVolume() {
        return getAP(mContext).increaceVolume();
    }

    /**
     * 后比音量减
     */
    public boolean descreaseVolume() {
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
    /*以上是与主机设备的音量相关*/

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
    public void setDevWorkModel(int workType, final DevModelCallBack setDevModelCallBack) {
        this.workType = workType;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final int setDevModelRes = tgSetDevModel();
                    if (mActivity != null) {

                    }
                }
            });
        }
    }

    /**
     * 获取设备的工作模式
     *
     */
    public void getDevWorkModel() {

//        work(mHandler, DEV_WORK_MODEL, null);
    }

    /*以下是内部方法*/

    /**
     * 设置设备的工作模式
     * 1：设置成功
     * 2：设置失败，该设备不支持6特征模板注册
     * 3：请先删除设备中的三模板
     * 4：请先删除设备中的六模板
     * -1：设置失败
     * -2 ：入参错误
     */
    private int tgSetDevModel() {
        int setDevModeRes1 = getTG661().TGSetDevMode(1);
        if (setDevModeRes1 == 0) {
            setDevModeRes1 = 1;
        }
        return setDevModeRes1;
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
//        if (mHandler != null) {
//            Bundle data = msg.getData();
//            if (data != null) {
//                int devServiceArg = data.getInt("status");
//                Message tg661JMsg = new Message();
//                tg661JMsg.what = DEV_STATUS;
//                LogUtils.d("接收到的设备状态：" + devServiceArg);
//                if (devServiceArg == 0) {
//                    if (!this.isLink)
//                        openDev(this.mHandler, this.workType, this.templModelType);
//                    tg661JMsg.arg1 = 1;
//                } else if (devServiceArg == -2) {
//                    this.isLink = false;
//                    this.devOpen = false;
//                    tg661JMsg.arg1 = -2;
//                }
//                mHandler.sendMessage(tg661JMsg);
//            }
//        }
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

    /******************************以上是指静脉与主机重连的部分***************************/


    private void CMD() {
        writeCMD();//USB 节点通信方式
        writeCMD2();//USB 节点通信方式
    }

    /**
     * 修改系统USB权限
     */
    private void writeCMD() {
        String command = "chmod -R 777 /dev/bus/usb/*/*";
        String command1 = "chmod -R 777 /dev/usb/*";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            int i = process.waitFor();
            Process process1 = Runtime.getRuntime().exec(new String[]{"su", "-c", command1});
            int i1 = process1.waitFor();
            LogUtils.i("CDM写入su1111命令:" + i + "    su2222: " + i1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为661j设备授权，如果设备连接方式为usb-hid模式
     */
    private void writeCMD2() {
        String command1 = "chmod -R 777 /dev/*";
        String command = "chmod -R 777 /dev/hidraw0 \nchmod -R 777 /dev/hidraw1" +
                " \nchmod -R 777 /dev/hidraw2 \nchmod -R 777 /dev/hidraw3 \nchmod -R 777 /dev/hidraw4";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            int i = process.waitFor();
            Process process1 = Runtime.getRuntime().exec(new String[]{"su", "-c", command1});
            int i1 = process1.waitFor();
            Log.i("===TAG===", "   执行CMD2222的结果：" + i + "   33333: " + i1);
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

}
