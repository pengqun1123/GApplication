package com.sd.tgfinger.tgApi.tgb1;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.sd.tgfinger.CallBack.CancelImgCallBack;
import com.sd.tgfinger.CallBack.DataSaveCallBack;
import com.sd.tgfinger.CallBack.DevCloseCallBack;
import com.sd.tgfinger.CallBack.DevFwCallBack;
import com.sd.tgfinger.CallBack.DevModelCallBack;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.DevWorkModelCallBack;
import com.sd.tgfinger.CallBack.FVVersionCallBack;
import com.sd.tgfinger.CallBack.FingerTimeCallBcak;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.FwCallBack;
import com.sd.tgfinger.CallBack.MsgCallBack;
import com.sd.tgfinger.CallBack.MultipleVerifyCallBack;
import com.sd.tgfinger.CallBack.OnStartDevStatusServiceListener;
import com.sd.tgfinger.CallBack.ReadDataCallBack;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.CallBack.SnCallBack;
import com.sd.tgfinger.CallBack.Verify1_1CallBack;
import com.sd.tgfinger.CallBack.Verify1_NCallBack;
import com.sd.tgfinger.CallBack.VerifyGetFingerImgListener;
import com.sd.tgfinger.CallBack.VerifyMsg;
import com.sd.tgfinger.R;
import com.sd.tgfinger.tgApi.TGBApi;
import com.sd.tgfinger.tgApi.TGFV;
import com.sd.tgfinger.tgApi.TGXG661API;
import com.sd.tgfinger.pojos.FingerFeatureBean;
import com.sd.tgfinger.pojos.FingerImgBean;
import com.sd.tgfinger.pojos.FusionFeatureBean;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.pojos.RegisterResult;
import com.sd.tgfinger.pojos.VerifyNBean;
import com.sd.tgfinger.pojos.VerifyResult;
import com.sd.tgfinger.tgApi.Constant;
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
import java.util.concurrent.Executor;

/**
 * 小特征专用类
 * 使用Handler来切换线程
 */
public class TGB1API {

    private Handler myHandler = new Handler(Looper.getMainLooper());

    //获取代理对象
    @SuppressLint("StaticFieldLeak")
    private static TGB1API INSTANCE = null;

    public synchronized static TGB1API getTGAPI() {
        if (INSTANCE == null) {
            INSTANCE = new TGB1API();
        }
        return INSTANCE;
    }

    private TGB1API() {
        //创建线程池
        executor = TgExecutor.getExecutor();
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

    //这个是ApplicationContext对象
    private Context mContext;
    //证书的数据流
    private InputStream inputStream;
    //工作的线程池
    private Executor executor;
    private int templModelType;//标记特征模式  3/6
    private int workType = Constant.WORK_BEHIND;//默认是后比
    public boolean devOpen = false;//设备是否已经打开
    private int devStatus = -1;//默认设备未开启的状态
    //设备是否连接上的标志
    private boolean isLink = false;
    //标记是否连续验证
    private boolean continueVerify = false;
    private boolean isCancelRegister = false;
    private boolean equalData = false;
    private boolean aloneVerifyN = false;
    //1:N验证 N的基数
    private int verifyBaseCount;
    //是否开启设备的声音
    private boolean sound = false;
    //默认是6特征模式
    private boolean isType6 = true;
    //是否对外发送图片
    private boolean sendImg = false;
    private byte[] aimByte = null;
    private int templIndex = 0;

    private VerifyGetFingerImgListener verifyGetImgListener;
    /**
     * 所有的回调接口
     */
    private DevStatusCallBack devStatusCallBack;
    private DevOpenCallBack devOpenCallBack;
    //SDK的当前版本号
    private static final String SDK_VERSION = "1.2.5_191114_Beta";

    /**
     * 获取SDK的版本
     */
    public String getSDKVersion() {
        return SDK_VERSION;
    }

    /*设备音量相关的调节*/

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
    /*设备音量相关的调节结束*/

    /*以下是开启设备连接状态的服务*/

    /**
     * 检测设备的连接状态
     * 解绑service
     */
    public void unbindDevService(Context context) {
        context.unbindService(serviceConnection);
    }

    /**
     * 启动devService,监听设备连接的状态
     *
     * @param context
     */
    public void startDevService(Context context, OnStartDevStatusServiceListener statusServiceListener) {
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
                statusServiceListener.startDevServiceStatus(true);
            } else {
                statusServiceListener.startDevServiceStatus(false);
            }
        } else {
            statusServiceListener.startDevServiceStatus(false);
        }
    }


    private Messenger devServiceMessenger = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            devServiceMessenger = new Messenger(iBinder);
            //如果设备开启
            Message tg661JMessage = new Message();
            tg661JMessage.what = Constant.SEND_MESSAGE_CODE;
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

    @SuppressLint("HandlerLeak")
    private Messenger tg661JMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constant.RECEIVE_MESSAGE_CODE) {
                sendDevStatusToView(msg);
            }
        }
    });

    private void sendDevStatusToView(Message msg) {
        Bundle data = msg.getData();
        if (data != null) {
            int devServiceArg = data.getInt(Constant.STATUS);
            LogUtils.d("接收到的设备状态：" + devServiceArg);
            if (devServiceArg == 0) {
                if (devStatusCallBack != null)
                    devStatusCallBack.devStatus(new Msg(1, "设备已连接"));
            } else if (devServiceArg == -2) {
                if (devStatusCallBack != null) {
                    LogUtils.d("设备连接已断开。。。");
                    devStatusCallBack.devStatus(new Msg(-2, "设备已断开,重新连接中"));
                }
                this.isLink = false;
                this.devOpen = false;
                openDev(TGB1API.this.workType,TGB1API.this.templModelType,
                        TGB1API.this.sound, TGB1API.this.devOpenCallBack,devStatusCallBack);
            }
        }
    }
    /*以上是开启检测设备连接状态的服务*/

    //获取算法的版本号
    public void fvVersion(@NonNull final FVVersionCallBack fvVersionCallBack) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = getFvVersion();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fvVersionCallBack.fvVersionCallBack(msg);
                        }
                    });
                }
            });
        }
    }

    //初始化
    public void init(Context context, InputStream inputStream, FvInitCallBack fvInitCallBack) {
        this.mContext = context;
        this.inputStream = inputStream;
        //检测设备是否已经root，通信节点初始化，初始化算法
        Boolean devIsRoot = checkDevIsRoot();
        if (devIsRoot) {
            InitLicense(fvInitCallBack);
        } else {
            //CMD();
            fvInitCallBack.fvInitResult(new Msg(-2, context.getString(R.string.device_no_root)));
        }
    }

    /**
     * 设置设备状态的接口
     *
     * @param devStatusCallBack devStatusCallBack
     */
    public void setDevStatusCallBack(@NonNull DevStatusCallBack devStatusCallBack) {
        this.devStatusCallBack = devStatusCallBack;
    }

    /**
     * 设置是否播放声音
     *
     * @param isSound isSound
     */
    public void setSound(boolean isSound) {
        this.sound = isSound;
    }

    //打开设备
    public void openDev(@NonNull Integer workType, @NonNull Integer templModelType, boolean sound
            , @NonNull final DevOpenCallBack callBack, @NonNull DevStatusCallBack devStatusCallBack) {
        this.continueVerify = true;
        this.isLink = true;
        this.workType = workType;
        this.templModelType = templModelType;
        this.sound = sound;
        this.devOpenCallBack = callBack;
        this.devStatusCallBack = devStatusCallBack;
        if (templModelType == Constant.TEMPL_MODEL_6) {
            isType6 = true;
        } else if (templModelType == Constant.TEMPL_MODEL_3) {
            isType6 = false;
        }
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    writeCMD();
                    createAimDirs();
                    final Msg msg = tgOpenDev();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.devOpenResult(msg);
                        }
                    });
                }
            });
        }
    }

    /**
     * 关闭指静脉设备
     */
    public void closeDev(@NonNull final DevCloseCallBack callBack) {
        this.continueVerify = false;
        this.isLink = false;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = tgCloseDev();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.devCloseResult(msg);
                        }
                    });
                }
            });
        }
    }

    //设备的开启状态
    public boolean isDevOpen() {
        return devOpen;
    }

    /**
     * 设置3/6特征模式
     */
    public void setTemplModelType(int templModelType) {
        this.templModelType = templModelType;
        if (templModelType == Constant.TEMPL_MODEL_6) {
            isType6 = true;
        } else if (templModelType == Constant.TEMPL_MODEL_3) {
            isType6 = false;
        }
    }

    /**
     * 设置设备得模式
     * 0:activity不能为null
     * 1：设置成功
     * 3：设置失败，该设备不支持6特征模板注册
     * 4：请先删除设备中的三模板
     * 5：请先删除设备中的六模板
     * -2：设置失败
     * -3 ：入参错误
     */
    public void setDevWorkModel(@NonNull Integer workType,
                                @NonNull final DevModelCallBack callBack) {
        this.workType = workType;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = tgSetDevModel();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.devSetModelResult(msg);
                        }
                    });
                }
            });
        }
    }

    public void setVerifyGetImgListener(@NonNull VerifyGetFingerImgListener verifyGetImgListener) {
        this.verifyGetImgListener = verifyGetImgListener;
    }

    /**
     * 获取设备的工作模式
     */
    public void getDevWorkModel(@NonNull final DevWorkModelCallBack callBack) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = tgGetDevModel();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.devWorkModelCallBack(msg);
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取设备的连接状态
     */
    public void getDevStatus(@NonNull final DevStatusCallBack callBack) {
        this.devStatusCallBack = callBack;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = tgDevStatus();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.devStatus(msg);
                        }
                    });
                }
            });
        }
    }

    /**
     * 注册
     */
    public void extractFeatureRegister(@NonNull final byte[] verifyFingerData
            , @NonNull final Integer verifyFingerSize, @NonNull final RegisterCallBack registerCallBack) {
        //检测抓图是否在进行
        this.continueVerify = false;
        this.isCancelRegister = false;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = tgDevRegister(verifyFingerData, verifyFingerSize);
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            registerCallBack.registerResult(msg);
                        }
                    });
                }
            });
        }
    }

    //取消抓图或注册
    public void cancelRegisterGetImg(@NonNull final CancelImgCallBack callBack) {
        this.isCancelRegister = true;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = tgCancelRegister();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.cancelImgCallBack(msg);
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取设备的固件号
     */
    public void devFw(@NonNull final DevFwCallBack callBack) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = devFW();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.devFwCallBack(msg.getTip());
                        }
                    });
                }
            });
        }
    }

    /**
     * 写入文件到主机
     */
    private byte[] saveData;
    private String savePath;

    public void saveDataToHost(@NonNull byte[] saveData, @NonNull String SavePath
            , @NonNull final DataSaveCallBack callBack) {
        this.saveData = saveData;
        this.savePath = SavePath;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = tgSaveFileToHost();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.dataSaveCallBack(msg);
                        }
                    });
                }
            });
        }
    }

    /**
     * 从主机设备读取数据
     */
    public void readDataFromHost(@NonNull String sourcePath
            , @NonNull final ReadDataCallBack callBack) {
        this.savePath = sourcePath;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Msg msg = tgReadDataFromHost();
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.readData(msg);
                        }
                    });
                }
            });
        }
    }

    /**
     * 1:N验证
     */
    public void featureCompare1_N(@NonNull final byte[] verifyData, @NonNull final int verifySize,
                                  @NonNull final Verify1_NCallBack callBack) {
        this.continueVerify = false;
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    aloneVerifyN(verifyData, verifySize, TGB1API.this.sound, new VerifyMsg() {
                        @Override
                        public void verifyMsg(final Msg msg) {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.verify1_NCallBack(msg);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 1:1验证
     * templData 模板数据
     */
    public void featureCompare1_1(@NonNull final byte[] verifyData
            , @NonNull final Verify1_1CallBack callBack) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    tgTempl1_1(verifyData, new VerifyMsg() {
                        @Override
                        public void verifyMsg(final Msg msg) {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.verify1CallBack(msg);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取模板对应的算法的版本
     */
    public void getTemplVersion(@NonNull final byte[] fingerData,
                                @NonNull final FVVersionCallBack fvVersionCallBack) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    tgGetFVVersion(fingerData, new MsgCallBack() {
                        @Override
                        public void msgCallBack(final Msg msg) {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fvVersionCallBack.fvVersionCallBack(msg);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取模板的SN序列号
     */
    public void getTemplSN(@NonNull final byte[] fingerData, @NonNull final SnCallBack snCallBack) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    tgGetTemplSN(fingerData, new MsgCallBack() {
                        @Override
                        public void msgCallBack(final Msg msg) {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    snCallBack.snCallBack(msg);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取模板的FW固件号
     */
    public void getTemplFW(@NonNull final byte[] fingerData, @NonNull final FwCallBack fwCallBack) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    tgGetTemplFW(fingerData, new MsgCallBack() {
                        @Override
                        public void msgCallBack(final Msg msg) {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fwCallBack.fwCallBack(msg);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取模板对应的时间
     */
    public void getTemplTime(@NonNull final byte[] fingerData, @NonNull final FingerTimeCallBcak fingerTimeCallBcak) {
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    tgGetTemplTime(fingerData, new MsgCallBack() {
                        @Override
                        public void msgCallBack(final Msg msg) {
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fingerTimeCallBcak.fingerTimeCallBack(msg);
                                }
                            });
                        }
                    });
                }
            });
        }
    }
    /*  内部接口  */

    /**
     * 单独1:N验证
     * 1:验证成功,Output数据有效
     * -1:特征比对（1：N）失败，因参数不合法，Output数据无效
     * -2:特征比对（1：N）失败，仅Output的matchScore数据有效
     * -3:抓图超时
     * -4:设备断开
     * -5:操作取消
     * -6:入参错误
     * -7:抓图未知错误
     * -8:验证提取特征失败
     */
    private void aloneVerifyN(@NonNull byte[] fingerTemplData, int fingerSize, Boolean isSound, VerifyMsg verifyMsg) {
        if (!TGB1API.this.continueVerify && fingerSize > 0) {
            this.aloneVerifyN = true;
            if (isType6) {
                tgAloneFinger1N(fingerTemplData, fingerSize, isSound, verifyMsg);
            } else {
                tgAloneFinger1N_3(fingerTemplData, fingerSize, isSound, verifyMsg);
            }
        }
    }

    /******************************单次1:N与连续1:N接口请勿同时使用********************************/

    //获取模板生成的时间
    private void tgGetTemplTime(byte[] templData, MsgCallBack msgCallBack) {
        byte[] snData = new byte[Constant.TIME_SIZE];
        int tgGetSNFromTmplRes = getTGFV().TGGetTimeFromTmpl(templData, snData);
        if (tgGetSNFromTmplRes == 0) {
            try {
                String time = new String(snData, "UTF-8");
                msgCallBack.msgCallBack(new Msg(1, time));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (tgGetSNFromTmplRes == -1) {
            msgCallBack.msgCallBack(new Msg(-1, "获取失败，参数错误，Output数据无效"));
        }
    }

    //获取模板的FW固件号
    private void tgGetTemplFW(byte[] templData, MsgCallBack msgCallBack) {
        byte[] snData = new byte[17];
        int tgGetSNFromTmplRes = getTGFV().TGGetFWFromTmpl(templData, snData);
        if (tgGetSNFromTmplRes == 0) {
            try {
                String fw = new String(snData, "UTF-8");
                msgCallBack.msgCallBack(new Msg(1, fw));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (tgGetSNFromTmplRes == -1) {
            msgCallBack.msgCallBack(new Msg(-1, "获取失败，参数错误，Output数据无效"));
        }
    }

    //获取模板的SN号
    private void tgGetTemplSN(byte[] templData, MsgCallBack msgCallBack) {
        byte[] snData = new byte[17];
        int tgGetSNFromTmplRes = getTGFV().TGGetSNFromTmpl(templData, snData);
        if (tgGetSNFromTmplRes == 0) {
            try {
                String sn = new String(snData, "UTF-8");
                msgCallBack.msgCallBack(new Msg(1, sn));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (tgGetSNFromTmplRes == -1) {
            msgCallBack.msgCallBack(new Msg(-1, "获取失败，参数错误，Output数据无效"));
        }
    }

    //获取模板算法的版本
    private void tgGetFVVersion(byte[] templData, MsgCallBack msgCallBack) {
        byte[] snData = new byte[5];
        int tgGetSNFromTmplRes = getTGFV().TGGetAPIVerFromTmpl(templData, snData);
        if (tgGetSNFromTmplRes == 0) {
            try {
                String snVersion = new String(snData, "UTF-8");
                msgCallBack.msgCallBack(new Msg(1, snVersion));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (tgGetSNFromTmplRes == -1) {
            msgCallBack.msgCallBack(new Msg(-1, "获取失败，参数错误，Output数据无效"));
        }
    }

    private void tgAloneFinger1N(byte[] fingerTemplData, int fingerSize, Boolean isSound, final VerifyMsg verifyMsg) {
        //3特征模式的话
        if (!TGB1API.this.isLink) return;
        if (!TGB1API.this.isType6) return;
        if (isSound && TGB1API.this.isLink) {
            getAP(mContext).play_inputDownGently();
        }
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x21);
        int imgResultCode = fingerImgBean.getImgResultCode();
        int res;
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
                shuntNVerify(fingerFeatureData, fingerTemplData, fingerSize/*, 5*/
                        , new MultipleVerifyCallBack() {
                            @Override
                            public void verifyResultCallBack(VerifyResult verifyResult) {
                                LogUtils.d("返回的数据：" + verifyResult);
                                Integer result = verifyResult.getResult();
                                if (result == 1) {
                                    //验证成功
                                    Integer compareScore = verifyResult.getCompareScore();
                                    Integer index = verifyResult.getIndex();
                                    byte[] updateFingerData = verifyResult.getUpdateFingerData();
                                    verifyMsg.verifyMsg(new Msg(1, "验证成功",
                                            updateFingerData, index, compareScore));
                                } else if (result == -1) {
                                    //验证失败
                                    Integer compareScore = verifyResult.getCompareScore();
                                    verifyMsg.verifyMsg(new Msg(-1, "验证失败", compareScore));
                                } else if (result == -2) {
                                    //1:N比对失败，参数不合法
                                    verifyMsg.verifyMsg(new Msg(-2, "1:N比对失败，参数不合法"));
                                }
                            }
                        });
            } else {
                //验证提取特征失败
                res = -8;
                verifyMsg.verifyMsg(new Msg(res, "验证提取特征失败"));
            }
        } else if (imgResultCode == -1) {
            //抓图超时
            res = -3;
            verifyMsg.verifyMsg(new Msg(res, "抓图超时"));
        } else if (imgResultCode == -2) {
            //设备断开
            res = -4;
            TGB1API.this.isLink = false;
            verifyMsg.verifyMsg(new Msg(res, "设备断开"));
        } else if (imgResultCode == -3) {
            //操作取消
            //res = -5;
        } else if (imgResultCode == -4) {
            //入参错误
            res = -6;
            verifyMsg.verifyMsg(new Msg(res, "入参错误"));
        } else if (imgResultCode == -5) {
            //抓图未知错误
            res = -7;
            verifyMsg.verifyMsg(new Msg(res, "抓图未知错误"));
        }
    }

    //3特征模式下1:N验证
    private void tgAloneFinger1N_3(byte[] fingerTemplData, int fingerSize, Boolean isSound, final VerifyMsg verifyMsg) {
        //3特征模式的话
        if (!isLink) return;
        if (isType6) return;
        if (isSound && TGB1API.this.isLink) {
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
                shuntNVerify(fingerFeatureData,
                        fingerTemplData, fingerSize, new MultipleVerifyCallBack() {
                            @Override
                            public void verifyResultCallBack(VerifyResult verifyResult) {
                                Integer result = verifyResult.getResult();
                                if (result == 1) {
                                    //验证成功
                                    Integer compareScore = verifyResult.getCompareScore();
                                    Integer index = verifyResult.getIndex();
                                    byte[] updateFingerData = verifyResult.getUpdateFingerData();
                                    verifyMsg.verifyMsg(new Msg(1, "验证成功",
                                            updateFingerData, index, compareScore));
                                } else if (result == -1) {
                                    //验证失败
                                    verifyMsg.verifyMsg(new Msg(-1, "验证失败",
                                            verifyResult.getCompareScore()));
                                } else if (result == -2) {
                                    //1:N比对失败，参数不合法
                                    verifyMsg.verifyMsg(new Msg(-2,
                                            "1:N比对失败，参数不合法"));
                                }
                            }
                        });
            } else {
                //验证提取特征失败
                res = -8;
                verifyMsg.verifyMsg(new Msg(res, "验证提取特征失败"));
            }
        } else if (imgResultCode == -1) {
            //抓图超时
            res = -3;
            verifyMsg.verifyMsg(new Msg(res, "抓图超时"));
        } else if (imgResultCode == -2) {
            //设备断开
            res = -4;
            TGB1API.this.isLink = false;
            verifyMsg.verifyMsg(new Msg(res, "设备断开"));
        } else if (imgResultCode == -3) {
            //操作取消
            //res = -5;
        } else if (imgResultCode == -4) {
            //入参错误
            res = -6;
            verifyMsg.verifyMsg(new Msg(res, "入参错误"));
        } else if (imgResultCode == -5) {
            //抓图未知错误
            res = -7;
            verifyMsg.verifyMsg(new Msg(res, "抓图未知错误"));
        }
    }

    /**
     * 1:1验证的方法实体
     *
     * @param templData 指静脉数据模板
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
    private void tgTempl1_1(byte[] templData, final VerifyMsg verifyMsg) {
        if (TGB1API.this.sound && TGB1API.this.isLink)
            getAP(mContext).play_inputDownGently();
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x20);
        int imgResultCode = fingerImgBean.getImgResultCode();
        int res;
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
                int match1Res = getTGFV().TGFeatureMatchTmpl11(fingerFeatureData, templData,
                            updateTempl, intByReference);
                if (match1Res == 0) {
                    res = 1;
                    //1:1验证分数
                    int score = intByReference.getValue();
                    if (TGB1API.this.sound && this.isLink) {
                        getAP(mContext).play_verifySuccess();
                    }
                    Msg msg = new Msg();
                    msg.setResult(res);
                    msg.setTip(mContext.getString(R.string.verify_success));
                    msg.setFingerData(updateTempl);
                    msg.setScore(score);
                    verifyMsg.verifyMsg(msg);
                } else if (match1Res == 7) {
                    res = 2;
                    //1:1验证分数
                    int score = intByReference.getValue();
                    if (TGB1API.this.sound && this.isLink) {
                        getAP(mContext).play_verifyFail();
                    }
                    verifyMsg.verifyMsg(new Msg(res, mContext.getString(R.string.verify_fail), score));
                } else if (match1Res == -1) {
                    res = -1;
                    if (TGB1API.this.sound && this.isLink) {
                        getAP(mContext).play_verifyFail();
                    }
                    verifyMsg.verifyMsg(new Msg(res,
                            "特征比对（1:1）失败，因参数不合法,Output数据无效"));
                }
            } else {
                //验证提取特征失败
                res = -8;
                verifyMsg.verifyMsg(new Msg(res,
                        "特征比对（1:1）失败，因参数不合法,Output数据无效"));
            }
        } else if (imgResultCode == -1) {
            //抓图超时
            res = -2;
            if (TGB1API.this.sound && this.isLink) {
                getAP(mContext).play_time_out();
            }
            verifyMsg.verifyMsg(new Msg(res, "抓图超时"));
        } else if (imgResultCode == -2) {
            //设备断开
            res = -3;
            verifyMsg.verifyMsg(new Msg(res, "设备断开"));
        } else if (imgResultCode == -3) {
            //操作取消
            res = -4;
            verifyMsg.verifyMsg(new Msg(res, "操作取消"));
        } else if (imgResultCode == -4) {
            //入参错误
            res = -5;
            verifyMsg.verifyMsg(new Msg(res, "入参错误"));
        } else if (imgResultCode == -5) {
            //未知错误
            res = -6;
            verifyMsg.verifyMsg(new Msg(res, "未知错误"));
        }
    }

    private int n = -1;

    /**
     * 分流比对
     *
     * @param fingerSize //     * @param              单次1:N验证  5   连续1:N验证   10
     */
    private void shuntNVerify(@NonNull byte[] fingerFeatureData, @NonNull byte[] fingerTemplData
            , int fingerSize/*, int type*/, final MultipleVerifyCallBack callBack) {
        if (verifyBaseCount == 0) {
            this.verifyBaseCount = 2500;
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
                this.verifySuccess = false;
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
                            multipleVerify(fingerFeatureData, cellFingerData,
                                    compareFingerCount, i, (count - 1), callBack);
                        }
                    }
                }
            }
        }
    }

    private boolean verifySuccess;

    private void multipleVerify(final byte[] fingerFeatureData, final byte[] cellFingerData
            , final Integer compareFingerCount, final Integer i, final Integer count
            , final MultipleVerifyCallBack callBack) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                shuntVerifyMethod(fingerFeatureData, cellFingerData,
                        compareFingerCount, i, count, new MultipleVerifyCallBack() {
                            @Override
                            public void verifyResultCallBack(VerifyResult verifyResult) {
                                Integer result = verifyResult.getResult();
                                if (result == 1) {
                                    verifySuccess = true;
                                    callBack.verifyResultCallBack(verifyResult);
                                } else {
                                    LogUtils.d("   i:" + i + "   n:" + n);
                                    if (!verifySuccess /*&& i.equals(count)*/ && n == count)
                                        callBack.verifyResultCallBack(verifyResult);
                                }
                            }
                        });
            }
        });
    }

    /**
     * 分流比对的实体
     *
     * @param index 返回可更新的模板下标
     */
    private void shuntVerifyMethod(@NonNull byte[] fingerFeatureData, @NonNull byte[] fingerTemplData
            , @NonNull int fingerSize, int index, @NonNull int cellDataCount
            /*, int type*/, MultipleVerifyCallBack callBack) {
        VerifyResult verifyResult;
        int res;
        //小特征
        VerifyNBean verifyNBean = tgTempl1_N(fingerFeatureData, fingerTemplData, fingerSize);
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
            verifyResult = new VerifyResult(res, score, (fingerIndex - 1), updateFingerData);
            if (TGB1API.this.sound && TGB1API.this.isLink) {
                getAP(mContext).play_verifySuccess();
            }
            //存在相同的模板
            TGB1API.this.equalData = true;
            callBack.verifyResultCallBack(verifyResult);
        } else if (res1N == 2) {
            //验证失败，分数不及格
            n++;
            res = -2;
            //验证的分数
            int score = verifyNBean.getVerifyScore();
            verifyResult = new VerifyResult(res, score, null, null);
            if (!TGB1API.this.equalData && TGB1API.this.sound
                    && TGB1API.this.n == cellDataCount && TGB1API.this.isLink) {
                getAP(mContext).play_verifyFail();
            }
            callBack.verifyResultCallBack(verifyResult);
        } else if (res1N == -1) {
            //特征比对（1：N）失败，因参数不合法，Output数据无效
            res = -1;
            verifyResult = new VerifyResult(res, null, null, null);
            if (!TGB1API.this.equalData && TGB1API.this.sound && TGB1API.this.isLink) {
                getAP(mContext).play_verifyFail();
            }
            callBack.verifyResultCallBack(verifyResult);
        }
    }

    //从主机读取数据
    private Msg tgReadDataFromHost() {
        Msg msg = null;
        byte[] data;
        if (!TextUtils.isEmpty(TGB1API.this.savePath)) {
            File file = new File(savePath);
            if (savePath.contains(".dat")) {
                byte[] fileData = FileUtil.readBytes(file);
                msg = new Msg(1, "读取成功", fileData, 1);
            } else {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    data = cellDataSpace(files.length);
                    try {
                        for (int i = 0; i < files.length; i++) {
                            File file1 = files[i];
                            byte[] fileData = FileUtil.readBytes(file1);
                            if (fileData != null)
                                System.arraycopy(fileData, 0, data, (int) file1.length() * i
                                        , (int) file1.length());
                        }
                    } catch (Exception e) {
                        LogUtils.e("TGBApi-class,1214-line,ArrayOutIndex-err");
                        e.printStackTrace();
                    }
                    msg = new Msg(1, "读取成功", data, files.length);
                } else {
                    msg = new Msg(-1, "暂无数据", null, 0);
                }
            }
        }
        return msg;
    }

    //存入文件到主机
    private Msg tgSaveFileToHost() {
        Msg msg = null;
        if (TGB1API.this.saveData != null && TGB1API.this.saveData.length > 0
                && !TextUtils.isEmpty(TGB1API.this.savePath)) {
            boolean writeFile = FileUtil.writeFile(this.saveData, this.savePath);
            if (writeFile) {
                msg = new Msg(1, "数据写入成功");
            } else {
                msg = new Msg(-1, "数据写入成功");
            }
        } else {
            msg = new Msg(-2, "数据或存储路径不可为空");
        }
        return msg;
    }

    /**
     * 取消注册/抓图接口
     */
    private Msg tgCancelRegister() {
        Msg msg;
        int tgCancelRegisterRes = getTG661().TGCancelGetImage();
        if (tgCancelRegisterRes == 0) {
            TGB1API.this.isCancelRegister = true;
            msg = new Msg(1, "取消成功");
            setTemplModelType(templModelType);
            aimByte = null;
        } else {
            msg = new Msg(-1, "取消失败");
        }
        return msg;
    }

    /**
     * 获取设备的固件号
     *
     * @return
     */
    private Msg devFW() {
        Msg msg;
        byte[] bytes = new byte[16];
        int i = getTG661().TGGetDevFW(bytes);
        String s = "";
        if (i == 0) {
            i = 1;
            try {
                s = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            s = "获取设备固件号失败";
        }
        msg = new Msg(i, s);
        return msg;
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
    private Msg tgDevRegister(@NonNull byte[] templData, @NonNull Integer templSize) {
        Msg msg = null;
        if (continueVerify)
            return null;
        //首先检查主机中已注册的模板文件名是否已存在
        //注册前核对当前指静脉是否已经注册,模板数量为0，不查重直接注册
//        this.isCancelRegister = false;
        RegisterResult registerResult;
        if (templSize > 0) {
            //查重
            int res = checkRepeat(templData, templSize);
            if (res == 1) {
                if (templModelType == Constant.TEMPL_MODEL_3) {
                    registerResult = registerFinger(Constant.TEMPL_MODEL_3 - 1);
                    Integer result = registerResult.getResult();
                    byte[] registerFinger = registerResult.getRegisterFinger();
                    String s = resTip(result);
                    msg = new Msg(result, s, registerFinger);
                } else if (templModelType == Constant.TEMPL_MODEL_6) {
                    registerResult = registerFinger(Constant.TEMPL_MODEL_6 - 1);
                    Integer result = registerResult.getResult();
                    byte[] registerFinger = registerResult.getRegisterFinger();
                    String s = resTip(result);
                    msg = new Msg(result, s, registerFinger);
                }
                templIndex = 0;
                aimByte = null;
            } else {
                String s = resTip(res);
                msg = new Msg(res, s);
            }
        } else {
            if (templModelType == Constant.TEMPL_MODEL_3) {
                registerResult = registerFinger(Constant.TEMPL_MODEL_3);
                Integer result = registerResult.getResult();
                byte[] registerFinger = registerResult.getRegisterFinger();
                String s = resTip(result);
                msg = new Msg(result, s, registerFinger);
            } else if (templModelType == Constant.TEMPL_MODEL_6) {
                registerResult = registerFinger(Constant.TEMPL_MODEL_6);
                Integer result = registerResult.getResult();
                byte[] registerFinger = registerResult.getRegisterFinger();
                String s = resTip(result);
                msg = new Msg(result, s, registerFinger);
            }
            templIndex = 0;
            aimByte = null;
        }
        return msg;
    }

    private String resTip(int code) {
        String resTip = null;
        if (code == 1) {
            resTip = "特征提取成功, Output数据有效";
        } else if (code == 2) {
            resTip = "特征提取失败, 因证书路径错误, Output数据无效";
        } else if (code == 3) {
            resTip = "特征提取失败, 因证书内容无效, Output数据无效";
        } else if (code == 4) {
            resTip = "特征提取失败, 因证书内容过期, Output数据无效";
        } else if (code == 5) {
            resTip = "特征提取失败, 因 \"图像\" 数据无效, Output数据无效";
        } else if (code == 6) {
            resTip = "特征提取失败, 因 \"图像\" 质量较差, Output数据无效";
        } else if (code == 7) {
            resTip = "模板登记重复";
        } else if (code == 8) {
            resTip = "登记成功";
        } else if (code == 9) {
            resTip = "特征融合失败，因 \"特征\" 数据一致性差，Output数据无效";
        } else if (code == -1) {
            resTip = "特征提取失败,因参数不合法,Output数据无效";
        } else if (code == -2) {
            resTip = "注册特征提取失败";
        } else if (code == -3) {
            resTip = "抓图超时";
        } else if (code == -4) {
            resTip = "设备断开";
        } else if (code == -5) {
            resTip = "操作取消";
        } else if (code == -6) {
            resTip = "入参错误";
        } else if (code == -7) {
            resTip = "抓图未知错误";
        } else if (code == -8) {
            resTip = "特征比对（1：N）失败，因参数不合法，Output数据无效";
        } else if (code == -9) {
            resTip = "验证提取特征失败";
        } else if (code == -10) {
            resTip = "登记失败";
        }
        return resTip;
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
    private int checkRepeat(byte[] templData, int templSize) {
        //抓图，请自然轻放手指
        int res = 0;
        if (TGB1API.this.sound && TGB1API.this.isLink)
            getAP(mContext).play_inputDownGently();
        FingerImgBean fingerImgBean = tgDevGetFingerImg(0x20);
        int imgResultCode = fingerImgBean.getImgResultCode();
        if (imgResultCode >= 0) {
            byte[] imgData = fingerImgBean.getImgData();
            //int imgDataLength = fingerImgBean.getImgDataLength();
            //提取图片的特征
            FingerFeatureBean fingerFeatureBean = extractImgFeatureVerify(imgData);
            int featureResult = fingerFeatureBean.getFeatureResult();
            if (featureResult == 1) {
                if (this.verifyGetImgListener != null) {
                    this.verifyGetImgListener.verifyGetFingerImgSuccess();
                }
                //提取特征成功
                byte[] fingerFeatureData = fingerFeatureBean.getFingerFeatureData();
                //1:N验证，查重
                VerifyNBean verifyNBean = tgTempl1_N(fingerFeatureData, templData, templSize);
                int res1N = verifyNBean.getVerifyNResult();
                if (res1N == 1) {
                    //验证成功，模板登记重复
                    res = 7;
                    if (TGB1API.this.isLink && TGB1API.this.sound)
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
        return res;
    }

    //抓取图片
    private FingerImgBean tgDevGetFingerImg(int soundType) {
        byte[] imgData;
        if (sendImg) {
            imgData = new byte[Constant.IMG_SIZE + Constant.T_SIZE];
            imgData[0] = ((byte) 0xfe);
        } else {
            imgData = new byte[Constant.IMG_SIZE];
        }
        //抓图
        int DevImageLength = getTG661().TGGetDevImage(imgData, Constant.GET_IMG_OUT_TIME);
        if (DevImageLength >= 0) {
            return new FingerImgBean(imgData, DevImageLength, 1);
        } else if (DevImageLength == -1) {
            if (soundType == 0x20) {
                if (TGB1API.this.sound && this.isLink) {
                    isCancelRegister = true;
                    getAP(mContext).play_time_out();
                }
            }
            return new FingerImgBean(null, -1, -1);
        } else if (DevImageLength == -2) {
            return new FingerImgBean(null, -1, -2);
        } else if (DevImageLength == -3) {
            return new FingerImgBean(null, -1, -3);
        } else if (DevImageLength == -4) {
            return new FingerImgBean(null, -1, -4);
        } else {
            return new FingerImgBean(null, -1, -5);
        }
    }

    //提取指静脉图像的特征--->注册专用（提取特征）
    private FingerFeatureBean extractImgFeature(byte[] fingerImgData) {
        byte[] fingerFeature = new byte[Constant.FEATURE_SIZE];
        int extractFeatureRes = getTGFV().TGImgExtractFeatureRegister(fingerImgData,
                Constant.IMG_W, Constant.IMG_H, fingerFeature);
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
        byte[] fingerFeature = new byte[Constant.FEATURE_SIZE];
        int extractFeatureRes = getTGFV().TGImgExtractFeatureVerify(fingerImgData,
                Constant.IMG_W, Constant.IMG_H, fingerFeature);
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

    //所有模板全部解析，执行
    public byte[] resolveAllTempl(byte[] fingerTemplData, int fingerSize) {
        byte[] matchData = comparableTemplData(fingerSize);
        for (int i = 0; i < fingerSize; i++) {
            //将模板一一解析
            byte[] finger = null;
            if (templModelType == Constant.TEMPL_MODEL_3) {
                finger = new byte[Constant.PERFECT_FEATURE_3];
            } else if (templModelType == Constant.TEMPL_MODEL_6) {
                finger = new byte[Constant.PERFECT_FEATURE_6];
            }
            int fingerLength = 0;
            if (templModelType == Constant.TEMPL_MODEL_3) {
                fingerLength = Constant.PERFECT_FEATURE_3;
            } else if (templModelType == Constant.TEMPL_MODEL_6) {
                fingerLength = Constant.PERFECT_FEATURE_6;
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

    //1:N验证抽取类
    private VerifyNBean tgTempl1_N(byte[] fingerFeature, byte[] fingerTemplData,
                                   int fingerSize) {
        IntByReference intB1 = new IntByReference();
        IntByReference intB2 = new IntByReference();
        byte[] uuId = new byte[Constant.UUID_SIZE];
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

    //拼接缓存模板
    private byte[] jointTempl(byte[] newFeature) {
        if (aimByte == null) {
            if (templModelType == Constant.TEMPL_MODEL_3) {
                aimByte = new byte[Constant.FEATURE_SIZE * templModelType];
            } else if (templModelType == Constant.TEMPL_MODEL_6) {
                aimByte = new byte[Constant.FEATURE_SIZE * templModelType];
            }
        }
        int length = templIndex * Constant.FEATURE_SIZE;
        System.arraycopy(newFeature, 0, aimByte, length, newFeature.length);
        templIndex++;
        return aimByte;
    }

    //特征融合完成登记的方法
    private RegisterResult registerFinger(int templTypeSize) {
        RegisterResult registerResult = null;
        for (int i = 0; i < templTypeSize; i++) {
            //取消注册的终止跳出符
            if (isCancelRegister) {
                break;
            }
            if (templIndex == 0 && TGB1API.this.isLink && TGB1API.this.sound) {
                getAP(mContext).play_inputDownGently();
            } else if (templIndex > 0 && this.isLink && TGB1API.this.sound) {
                getAP(mContext).play_inputAgain();
            }
            FingerImgBean fingerImgBean = tgDevGetFingerImg(0x20);
            int imgResultCode = fingerImgBean.getImgResultCode();
            if (imgResultCode >= 0) {
                byte[] imgData = fingerImgBean.getImgData();
                //int imgDataLength = fingerImgBean.getImgDataLength();
                FingerFeatureBean fingerFeatureBeanRegister = extractImgFeature(imgData);
                int result = fingerFeatureBeanRegister.getFeatureResult();
                if (result == 1) {
                    //提取成功
                    byte[] jointTempl = jointTempl(fingerFeatureBeanRegister.getFingerFeatureData());
                    int size = templModelType == Constant.TEMPL_MODEL_6 ? 6 : 3;
                    if (templIndex == size) {
                        //特征融合
                        FusionFeatureBean fusionFeatureBean = fusionFeature(jointTempl, size);
                        int fusionResult = fusionFeatureBean.getFusionResult();
                        if (fusionResult == 1) {
                            //登记成功
                            byte[] fusionTempl = fusionFeatureBean.getFusionTempl();
                            if (TGB1API.this.sound && TGB1API.this.isLink) {
                                getAP(mContext).play_checkInSuccess();
                            }
                            registerResult = new RegisterResult(8, fusionTempl);
                        } else if (fusionResult == 6) {
                            //特征融合失败，因"特征"数据一致性差，Output数据无效
                            if (TGB1API.this.sound && TGB1API.this.isLink) {
                                getAP(mContext).play_checkInFail();
                            }
                            registerResult = new RegisterResult(9, null);
                        } else if (fusionResult == -1) {
                            //登记失败
                            if (TGB1API.this.sound && TGB1API.this.isLink) {
                                getAP(mContext).play_checkInFail();
                            }
                            registerResult = new RegisterResult(-10, null);
                        }
                    }
                } else if (result == 2) {
                    registerResult = new RegisterResult(result, null);
                } else if (result == 3) {
                    registerResult = new RegisterResult(result, null);
                } else if (result == 4) {
                    registerResult = new RegisterResult(result, null);
                } else if (result == 5) {
                    registerResult = new RegisterResult(result, null);
                } else if (result == 6) {
                    registerResult = new RegisterResult(result, null);
                } else if (result == -1) {
                    registerResult = new RegisterResult(result, null);
                } else {
                    registerResult = new RegisterResult(-2, null);
                }
            } else if (imgResultCode == -1) {
                //抓图超时
                if (TGB1API.this.sound && TGB1API.this.isLink) {
                    getAP(mContext).play_time_out();
                }
                registerResult = new RegisterResult(-3, null);
            } else if (imgResultCode == -2) {
                //设备断开
                TGB1API.this.isLink = false;
                registerResult = new RegisterResult(-4, null);
            } else if (imgResultCode == -3) {
                //操作取消
                registerResult = new RegisterResult(-5, null);
            } else if (imgResultCode == -4) {
                //入参错误
                if (TGB1API.this.sound && TGB1API.this.isLink) {
                    getAP(mContext).play_checkInFail();
                }
                registerResult = new RegisterResult(-6, null);
            } else if (imgResultCode == -5) {
                //未知错误
                if (TGB1API.this.sound && TGB1API.this.isLink) {
                    getAP(mContext).play_checkInFail();
                }
                registerResult = new RegisterResult(-7, null);
            }
        }
        return registerResult;
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

    //解析模板 ==>大特征解析模板专用
    private byte[] resolveTempl(byte[] oldMatchTemplData) {
        /**
         *      （1） 1：模板解析成功， Output数据有效
         *      （2）-1：模板解析失败，因参数不合法，Output数据无效
         *      -2:待解析的模板数据为null
         */
        //将模板解析为比对模板，实际上就是去掉前208位
        if (oldMatchTemplData != null) {
            byte[] matchTemplData = null;
            if (templModelType == Constant.TEMPL_MODEL_3) {
                matchTemplData = new byte[Constant.WAIT_COMPARE_FEATURE_3];
            } else if (templModelType == Constant.TEMPL_MODEL_6) {
                matchTemplData = new byte[Constant.WAIT_COMPARE_FEATURE_6];
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

    //获取完整模板的大小
    private byte[] perfectTemplData() {
        byte[] updateTempl = null;
        if (templModelType == Constant.TEMPL_MODEL_3) {
            updateTempl = new byte[Constant.PERFECT_FEATURE_3];
        } else if (templModelType == Constant.TEMPL_MODEL_6) {
            updateTempl = new byte[Constant.PERFECT_FEATURE_6];
        }
        return updateTempl;
    }

    //缓存解析后的模板
    private byte[] comparableTemplData(int featureSize) {
        byte[] matchData = null;
        if (templModelType == Constant.TEMPL_MODEL_3) {
            matchData = new byte[Constant.WAIT_COMPARE_FEATURE_3 * featureSize];
        } else if (templModelType == Constant.TEMPL_MODEL_6) {
            matchData = new byte[Constant.WAIT_COMPARE_FEATURE_6 * featureSize];
        }
        return matchData;
    }

    /**
     * 1:设备打开成功，后比设置成功
     * 后比不存在3/6切换不兼容的问题，前比存在
     * 2:设备打开成功，模式设置失败，该设备不支持6特征模板注册
     * 3:设备打开成功，模式设置失败，请先删除设备中的三模板
     * 4:设备打开成功，模式设置失败，请先删除设备中的六模板
     * -1:设备打开失败
     * -2:设备打开成功，模式设置失败
     * -3:设备打开成功，入参错误
     */
    public Msg tgOpenDev() {
        IntByReference mode = new IntByReference();
        int openDevRes = getTG661().TGOpenDev(mode);
        if (openDevRes >= 0) {
            //设置工作模式
            devOpen = true;
            openDevRes = 1;
            TGB1API.this.isLink = true;
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
                            openDevRes = 1;
                            TGB1API.this.isLink = true;
                        }
                        loopOpen = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Msg msg;
        if (openDevRes == 1) {
            msg = new Msg(openDevRes, "设备打开成功，后比设置成功");
        } else if (openDevRes == -1) {
            msg = new Msg(openDevRes, "设备打开失败");
        } else if (openDevRes == -2) {
            msg = new Msg(openDevRes, "设备打开成功，模式设置失败");
        } else if (openDevRes == -3) {
            msg = new Msg(openDevRes, "设备打开成功，入参错误");
        } else {
            msg = new Msg(openDevRes, "设备打开失败");
        }
        return msg;
    }

    /**
     * 1:指静脉设备关闭成功
     * -1:指静脉设备关闭失败
     */
    public Msg tgCloseDev() {
        int closeDevRes = getTG661().TGCloseDev();
        devOpen = false;
        if (closeDevRes == 0) {
            //传出关闭成功的结果：
            getAP(mContext).release();//释放声音资源
            closeDevRes = devStatus = 1;
            TGB1API.this.isLink = false;
        }
        Msg msg;
        if (closeDevRes == 1) {
            msg = new Msg(closeDevRes, "指静脉设备关闭成功");
        } else if (closeDevRes == -1) {
            msg = new Msg(closeDevRes, "指静脉设备关闭失败");
        } else {
            msg = new Msg(closeDevRes, "指静脉设备关闭失败");
        }
        return msg;
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
    private Msg tgSetDevModel() {
        int setDevModeRes1 = getTG661().TGSetDevMode(1);
        Msg msg;
        if (setDevModeRes1 == 0) {
            msg = new Msg(1, "设置成功");
        } else if (setDevModeRes1 == 2) {
            msg = new Msg(setDevModeRes1, "设置失败，该设备不支持6特征模板注册");
        } else if (setDevModeRes1 == 3) {
            msg = new Msg(setDevModeRes1, "请先删除设备中的三模板");
        } else if (setDevModeRes1 == 4) {
            msg = new Msg(setDevModeRes1, "请先删除设备中的六模板");
        } else if (setDevModeRes1 == -1) {
            msg = new Msg(setDevModeRes1, "设置失败");
        } else if (setDevModeRes1 == -2) {
            msg = new Msg(setDevModeRes1, "入参错误");
        } else {
            msg = new Msg(setDevModeRes1, "设置失败");
        }
        return msg;
    }

    /**
     * 获取设备的工作模式
     * 0：获取成功
     * IntByReference 0：前比3特征模板
     * 1：后比
     * 2：前比6特征模板
     * -1：超时
     */
    private Msg tgGetDevModel() {
        IntByReference ibr = new IntByReference();
        int devWorkModelRes = getTG661().TGGetDevMode(ibr);
        Msg msg;
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
        } else {
            devWorkModelRes = 1;
        }
        if (devWorkModelRes < 0) {
            devWorkModelRes = -1;
        } else {
            if (ibr.getValue() == 0) {
                devWorkModelRes = 1;
            } else if (ibr.getValue() == 1) {
                devWorkModelRes = 2;
            } else if (ibr.getValue() == 2) {
                devWorkModelRes = 3;
            }
        }
        if (devWorkModelRes == 1) {
            msg = new Msg(devWorkModelRes, "前比3特征模板");
        } else if (devWorkModelRes == 2) {
            msg = new Msg(devWorkModelRes, "后比");
        } else if (devWorkModelRes == 3) {
            msg = new Msg(devWorkModelRes, "前比6特征模板");
        } else if (devWorkModelRes == -1) {
            msg = new Msg(devWorkModelRes, "获取设备的工作模式超时");
        } else {
            msg = new Msg(devWorkModelRes, "获取设备的工作模式失败");
        }
        return msg;
    }

    /**
     * 获取设备的连接状态
     * 1:设备已连接
     * -1:设备未连接
     */
    public Msg tgDevStatus() {
        devStatus = getTG661().TGGetDevStatus();
        //传递出设备的链接状态
        Msg msg;
        if (devStatus >= 0) {
            devStatus = 1;
            msg = new Msg(devStatus, "设备已连接");
        } else if (devStatus == -1) {
            msg = new Msg(devStatus, "设备未连接");
        } else {
            msg = new Msg(devStatus, "设备连接获取失败");
        }
        return msg;
    }


    /**
     * 初始化证书
     * 1: 初始化成功,算法接口有效
     * 2: 初始化失败,因证书路径错误,算法接口无效
     * 3: 初始化失败,因证书内容无效,算法接口无效
     * 4: 初始化失败,因证书内容过期,算法接口无效
     * -1:算法初始化失败
     * -2:设备没有Root
     */
    private void InitLicense(final FvInitCallBack fvInitCallBack) {
        if (executor != null && mContext != null) {
            executor.execute(new Runnable() {
                int licenceRes = -1;
                String tip;

                @Override
                public void run() {
                    if (inputStream != null) {
                        //如果是由网络下发证书流，先写入指定路径的文件
                        licenceRes = writeLicenseToFile(inputStream);
                    } else {
                        //检测指定的文件夹下是否存在证书
                        ArrayList<String> licenseList = FileUtil.getInitFinerFileList(Constant.LICENSE_DIR);
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
                                            licenceRes = writeLicense();
                                        }
                                    }
                                }
                            } else {
                                licenceRes = writeLicense();
                            }
                        } else {
                            createDirPath(Constant.LICENSE_DIR);
                            licenceRes = writeLicense();
                        }
                    }
                    if (licenceRes == 1) {
                        tip = "初始化成功,算法接口有效";
                    } else if (licenceRes == 2) {
                        tip = "初始化失败,因证书路径错误,算法接口无效";
                    } else if (licenceRes == 3) {
                        tip = "初始化失败,因证书内容无效,算法接口无效";
                    } else if (licenceRes == 4) {
                        tip = "初始化失败,因证书内容过期,算法接口无效";
                    } else if (licenceRes == -1) {
                        tip = "算法初始化失败";
                    }
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        Handler mainThread = new Handler(Looper.getMainLooper());
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (fvInitCallBack != null) {
                                    fvInitCallBack.fvInitResult(new Msg(licenceRes, tip));
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private int writeLicense() {
        //不存在证书历史，将SDK的证书写入指定文件
        InputStream LicenseIs = mContext.getResources().openRawResource(R.raw.license);
        return writeLicenseToFile(LicenseIs);
    }

    //获取算法版本号
    private Msg getFvVersion() {
        byte[] fvVersion = new byte[Constant.FV_VERSION];
        int i = getTGFV().TGGetFVAPIVer(fvVersion);
        if (i == 0) {
            return new Msg(1, "算法版本号获取成功", fvVersion);
        } else {
            return new Msg(-1, "算法版本号获取失败");
        }
    }

    //调用算法接口初始化算法
    private int FV_InitAct() {
        int tgInitFVProcessRes = getTGFV().TGInitFVProcess(Constant.LICENSE_PATH);
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

    //模板的数据长度缓存
//    private byte[] templSizeData(int templCount) {
//        byte[] allFingerData = null;
//        if (templModelType == Constant.TEMPL_MODEL_3) {
//            allFingerData = new byte[Constant.PERFECT_FEATURE_3 * templCount];
//        } else if (templModelType == Constant.TEMPL_MODEL_6) {
//            allFingerData = new byte[Constant.PERFECT_FEATURE_6 * templCount];
//        }
//        return allFingerData;
//    }

    //每一个比对单元verifyBaseCount的数据存储空间
    private byte[] cellDataSpace(int size) {
        byte[] cellFingerData = null;
        if (templModelType == Constant.TEMPL_MODEL_3) {
            cellFingerData = new byte[size * Constant.PERFECT_FEATURE_3];
        } else if (templModelType == Constant.TEMPL_MODEL_6) {
            cellFingerData = new byte[size * Constant.PERFECT_FEATURE_6];
        }
        return cellFingerData;
    }

    //截取的起始位置
    private int startPos(int verifyBaseCount, int index) {
        if (templModelType == Constant.TEMPL_MODEL_3) {
            return verifyBaseCount * Constant.PERFECT_FEATURE_3 * index;
        } else if (templModelType == Constant.TEMPL_MODEL_6) {
            return verifyBaseCount * Constant.PERFECT_FEATURE_6 * index;
        } else {
            return 0;
        }
    }

    /**
     * 检查设备是否已经Root
     */
    private Boolean checkDevIsRoot() {
        boolean rootSystem = DevRootUtil.isRootSystem();
        createAimDirs();
        if (rootSystem) {
            LogUtils.d("设备已经Root");
            return true;
        } else {
            LogUtils.d("设备没有Root，无法使用");
            return false;
        }
    }

    //创建需要的文件夹
    private void createAimDirs() {
//        createDirPath(Constant.BEHIND_TEMPL_3_PATH);
//        createDirPath(Constant.BEHIND_TEMPL_6_PATH);
        createDirPath(Constant.LOG_DIR);
        createDirPath(Constant.LICENSE_DIR);
        createDirPath(Constant.IMG_PATH);
        createDirPath(Constant.MONI_EXTER_3_PATH);
        createDirPath(Constant.MONI_EXTER_6_PATH);
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
                output = new FileOutputStream(Constant.LICENSE_PATH);
                // 拷贝到输出流
                byte[] buffer = new byte[1024];
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
     * 创建相关的文件夹,获取到相关的路径
     */
    private void createDirPath(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
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
