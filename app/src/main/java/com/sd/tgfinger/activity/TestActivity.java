package com.sd.tgfinger.activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sd.tgfinger.CallBack.CancelImgCallBack;
import com.sd.tgfinger.CallBack.DataSaveCallBack;
import com.sd.tgfinger.CallBack.DevCloseCallBack;
import com.sd.tgfinger.CallBack.DevFwCallBack;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FVVersionCallBack;
import com.sd.tgfinger.CallBack.FingerVerifyResultListener;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.OnStartDevStatusServiceListener;
import com.sd.tgfinger.CallBack.ReadDataCallBack;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.CallBack.Verify1_1CallBack;
import com.sd.tgfinger.CallBack.Verify1_NCallBack;
import com.sd.tgfinger.gapplication.R;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;
import com.sd.tgfinger.tgApi.tgb1.TGB1API;
import com.sd.tgfinger.utils.AlertDialogUtil;
import com.sd.tgfinger.utils.FileUtil;
import com.sd.tgfinger.utils.LogUtils;
import com.sd.tgfinger.utils.ToastUtil;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created By pq
 * on 2019/9/19
 * 小特征示例demo
 */
public class TestActivity extends AppCompatActivity implements DevOpenCallBack, DevStatusCallBack,
        View.OnClickListener, FingerVerifyResultListener {

    private TextView tipTv, devStatus, connectStatusTv, FvVersion;
    @SuppressLint("InlinedApi")
    private String[] perms = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
//    private AlertDialog alertDialog;

    private int readDataType = -1;
    private int templType = -1;
    private TextView volumeTt, SDKVersion, devFw;
    private Button closeDevBtn;
    private Button openDevBtn;
    private Button voiceDecreaceBtn;
    private Button voiceIncreaceBtn;
    private Button cancelRegisterBtnBehind;
    private Button registerBtnBehind;
    private Button ver1_nBtn;
    private Button ver1_1Btn;
    private Button getTemplFW;
    private Button getTemplSN;
    private Button templTimeBtn;
    private Button getTemplAlgorVersionBtn;
    private CheckBox autoUpdateTempl;
    private RadioButton templ3Rb;
    private RadioButton templ6Rb;
    private RadioGroup templSumModel;
    private TGB1API tgapi;
    private byte[] finger6Data;
    private Integer finger6Size;
    private EditText et;
    private SwitchCompat switchVoice;
    private AppCompatCheckBox cbVerify;
    private Timer timer;
    private MyTask myTask;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behind);

        tgapi = TGB1API.getTGAPI();
        timer = new Timer();
        myTask = new MyTask();
        initView();
        pers();//权限申请
        String sdkVersion = tgapi.getSDKVersion();
        SDKVersion.setText(sdkVersion);

//        tgapi.setSoundPlay(this);
        //保持屏幕常亮
        if (tgapi != null)
            tgapi.keepScreenLight(this);

    }

    private void pers() {
        int i = ContextCompat.checkSelfPermission(this, perms[0]);
        if (i == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, perms, 0x11);
        } else {
            initFv();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, permissions, 0x11);
        } else {
            initFv();
        }
    }

    private void initFv() {
        //后比算法初始化
        tgapi.init(this, null, new FvInitCallBack() {
            @Override
            public void fvInitResult(Msg msg) {
                if (msg.getResult() == 1) {
                    showTip("初始化成功");
                    openDev();
//                    fvVersion();

                }
            }
        });
    }

    //获取设备的固件号
    private void getDevFw() {
        tgapi.devFw(new DevFwCallBack() {
            @Override
            public void devFwCallBack(String fw) {
                devFw.setText(MessageFormat.format("固件号:{0}", fw));
            }
        });
    }

    //获取算法的版本号
    private void fvVersion() {
        tgapi.fvVersion(new FVVersionCallBack() {
            @Override
            public void fvVersionCallBack(Msg msg) {
                Integer result = msg.getResult();
                if (result == 1) {
                    byte[] fvVersion = msg.getFingerData();
                    StringBuilder sb = new StringBuilder();
                    for (int i1 = 0; i1 < fvVersion.length; i1++) {
                        sb.append(fvVersion[i1]);
                    }
                    FvVersion.setText(MessageFormat.format("算法版本:{0}", sb.toString()));
                }
            }
        });
    }

    private void openDev() {
        if (!tgapi.isDevOpen()) {
            //初始化准备数据
            readData();
//            alertDialog = AlertDialogUtil.Instance()
//                    .showWaitDialog(this, "设备正在打开...");
//            LogUtils.d("打开设备：调用  11");
            boolean sound = true;
            tgapi.openDev(Constant.WORK_BEHIND, Constant.TEMPL_MODEL_6,
                    sound, this, this);
            if (sound) {
                switchVoice.setChecked(false);
            } else {
                switchVoice.setChecked(true);
            }
        } else {
            toast("设备已经打开");
        }
    }

    private void closeDev() {
        if (tgapi.isDevOpen())
            tgapi.closeDev(new DevCloseCallBack() {
                @Override
                public void devCloseResult(Msg msg) {
                    Integer result = msg.getResult();
                    LogUtils.d("关闭设备：" + result);
                    toast(msg.getTip());
                    dismissDialog();
                    showTip(msg.getTip());
                    connectStatusTv.setText(msg.getTip());
                    //解绑Service
                    //后比
                    if (isRegisterService) {
                        tgapi.unbindDevService(TestActivity.this);
                        isRegisterService = false;
                    }
                }
            });
        else
            toast("设备已经关闭");
    }

    private void getCurrentVoice() {
        String currentVolume = tgapi.getCurrentVolume(this);
        volumeTt.setText(currentVolume);
    }

    private void toast(String tip) {
        ToastUtil.toast(this, tip);
    }

    private void showTip(String tip) {
        tipTv.setText(tip);
    }

    private void dismissDialog() {
//        if (alertDialog != null && alertDialog.isShowing()) {
//            alertDialog.dismiss();
//            alertDialog = null;
//        }
    }

    private AlertDialog alertDialog1;

    //初始化只准备数据
    private void readData() {
        alertDialog1 = AlertDialogUtil.Instance().showWaitDialog(TestActivity.this, "数据准备中...");
        if (templType == Constant.TEMPL_MODEL_6) {
            tgapi.readDataFromHost(Constant.MONI_EXTER_6_PATH, new ReadDataCallBack() {
                @Override
                public void readData(Msg msg) {
                    Integer result = msg.getResult();
                    LogUtils.d("读取4数据：" + result);
                    if (result == 1) {
                        finger6Data = msg.getFingerData();
                        finger6Size = msg.getFingerSize();
                        LogUtils.d(" 初始的指静脉数量：" + finger6Size);
                    } else {
                        finger6Size = 0;
                    }
                    if (alertDialog1 != null && alertDialog1.isShowing()) {
                        alertDialog1.dismiss();
                        alertDialog1 = null;
                    }
                }
            });
        }
    }

    private void initView() {
        closeDevBtn = findViewById(R.id.closeDevBtn);
        openDevBtn = findViewById(R.id.openDevBtn);
        voiceDecreaceBtn = findViewById(R.id.voiceDecreaceBtn);
        voiceIncreaceBtn = findViewById(R.id.voiceIncreaceBtn);
        cancelRegisterBtnBehind = findViewById(R.id.cancelRegisterBtnBehind);
        registerBtnBehind = findViewById(R.id.registerBtnBehind);
        ver1_nBtn = findViewById(R.id.ver1_NBtn);
        ver1_1Btn = findViewById(R.id.ver1_1Btn);
        getTemplFW = findViewById(R.id.getTemplFW);
        getTemplSN = findViewById(R.id.getTemplSN);
        templTimeBtn = findViewById(R.id.templTimeBtn);
        getTemplAlgorVersionBtn = findViewById(R.id.getTemplAlgorVersionBtn);
        autoUpdateTempl = findViewById(R.id.autoUpdateTempl);
        devFw = findViewById(R.id.devFw);
        volumeTt = findViewById(R.id.volumeTt);
        SDKVersion = findViewById(R.id.SDKVersion);
        devStatus = findViewById(R.id.devStatus);
        connectStatusTv = findViewById(R.id.connectStatusTv);
        templSumModel = findViewById(R.id.templSumModel);
        templ3Rb = findViewById(R.id.templ3Rb);
        templ6Rb = findViewById(R.id.templ6Rb);
        tipTv = findViewById(R.id.tipTv);
        FvVersion = findViewById(R.id.FvVersion);
        et = findViewById(R.id.templIDBehind);
        switchVoice = findViewById(R.id.switchVoice);
        cbVerify = findViewById(R.id.cbVerify);

        tgapi.setVolume(this, 1);
        getCurrentVoice();
        //默认特征模式为6模板
        templType = Constant.TEMPL_MODEL_6;
        tgapi.setTemplModelType(templType);

        openDevBtn.setOnClickListener(this);
        closeDevBtn.setOnClickListener(this);
        voiceDecreaceBtn.setOnClickListener(this);
        voiceIncreaceBtn.setOnClickListener(this);
        cancelRegisterBtnBehind.setOnClickListener(this);
        registerBtnBehind.setOnClickListener(this);
        ver1_nBtn.setOnClickListener(this);
        ver1_1Btn.setOnClickListener(this);
        getTemplFW.setOnClickListener(this);
        getTemplSN.setOnClickListener(this);
        templTimeBtn.setOnClickListener(this);
        getTemplAlgorVersionBtn.setOnClickListener(this);

        //设置静音和连续验证的状态
        statusSetting();
    }

    private void statusSetting() {
        //验证成功后，自动更新模板
        autoUpdateTempl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        //切换设置3/6特征模板的模式
        templSumModel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (templ3Rb.getId() == i) {
                    templType = Constant.TEMPL_MODEL_3;
                    tgapi.setTemplModelType(Constant.TEMPL_MODEL_3);
                    //初始化数据，开启连续验证
//                    readFingerData(3);
                    readData();

                } else if (templ6Rb.getId() == i) {
                    templType = Constant.TEMPL_MODEL_6;
                    tgapi.setTemplModelType(Constant.TEMPL_MODEL_6);
                    //初始化数据，开启连续验证
                    //readFingerData(3);
                    readData();
                }
            }
        });
        switchVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tgapi.setSound(false);
                } else {
                    tgapi.setSound(true);
                }
            }
        });
        cbVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tgapi.isDevOpen()) {
                    if (isChecked) {
                        isCheck = true;
                        isCancelVerify = false;
                        ver1_nBtn.setVisibility(View.GONE);
                    } else {
                        isCheck = false;
                        pauseFingerVerify();
                        ver1_nBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtil.toast(TestActivity.this, "请先打开设备");
                }
            }
        });
    }

    //取消抓图的标记 0:正常取消抓图 1:注册取消抓图 2:验证取消抓图
    private int cancelGetImgType = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeDevBtn:
                closeDev();
                break;
            case R.id.openDevBtn:
                openDev();
                break;
            case R.id.voiceDecreaceBtn:
                boolean decreaseVolume = tgapi.decreaseVolume();
                if (decreaseVolume) getCurrentVoice();
                break;
            case R.id.voiceIncreaceBtn:
                boolean increaseVolume = tgapi.increaseVolume();
                if (increaseVolume) getCurrentVoice();
                break;
            case R.id.cancelRegisterBtnBehind:
                cancelRegisterBtnBehind.setClickable(false);
                cancelGetImgType = 2;
                tgapi.cancelRegisterGetImg(new CancelImgCallBack() {
                    @Override
                    public void cancelImgCallBack(Msg msg) {
                        Integer result = msg.getResult();
                        LogUtils.d("取消注册的：" + result);
                        toast(msg.getTip());
                        cancelRegisterBtnBehind.setClickable(true);
                    }
                });
                break;
            case R.id.registerBtnBehind:
                //注册
                register();
                break;
            case R.id.ver1_NBtn:
                //1:N
                verifyN();
                break;
            case R.id.ver1_1Btn:
                //验证
                verify();
                break;

        }
    }

    private void verify() {
        String fingerName = et.getText().toString().trim();
        if (TextUtils.isEmpty(fingerName)) {
            toast("请输入模板文件名称");
            return;
        }
        String path = Constant.MONI_EXTER_6_PATH + File.separator + fingerName;
        List<String> allFileName = FileUtil.getAllFileName(Constant.MONI_EXTER_6_PATH);
        boolean no = false;
        for (int i = 0; i < allFileName.size(); i++) {
            String s = allFileName.get(i);
            if (s.equals(fingerName)) {
                break;
            } else {
                if (i == allFileName.size() - 1) {
                    toast("该文件不存在，请核查!");
                    no = true;
                }
            }
        }
        if (no) {
            return;
        }
        byte[] verifyData = FileUtil.readBytes(path);
        if (verifyData != null) {
            tgapi.featureCompare1_1(verifyData, new Verify1_1CallBack() {
                @Override
                public void verify1CallBack(Msg msg) {
                    Integer result = msg.getResult();
                    Integer score = 0;
                    if (result == 1) {
                        //验证分数
                        score = msg.getScore();
                    } else if (result == -3) {
                        tgapi.cancelRegisterGetImg(new CancelImgCallBack() {
                            @Override
                            public void cancelImgCallBack(Msg msg) {
                                Integer result = msg.getResult();
                                LogUtils.d("取消注册的：" + result);
                                toast(msg.getTip());
                            }
                        });
                    }
                    showTip(msg.getTip() + score);
                }
            });
        } else {
            toast("请检查文件数据");
        }
    }


    private void verifyN() {
        if (templType == Constant.TEMPL_MODEL_6) {
            if (finger6Data == null) {
                toast("请先注册");
                return;
            }
            verifyNFinger(finger6Data, finger6Size);
        }
    }

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
    private void verifyNFinger(@NonNull byte[] fingerData, Integer fingerSize) {
        tgapi.featureCompare1_N(fingerData, fingerSize, new Verify1_NCallBack() {
            @Override
            public void verify1_NCallBack(Msg msg) {
                Integer result = msg.getResult();
                LogUtils.d("验证的结果:" + result);
                if (result == 1) {
                    //比对的分数
                    Integer score = msg.getScore();
                    //比对模板对应的位置
                    Integer index = msg.getIndex();
                    //可更新的模板数据
                    byte[] updateFingerData = msg.getFingerData();
                    showTip(msg.getTip() + " 分数:" + score);
                    //当比对分数大于某个设定的值，可更新指静脉模板
                    //eg：
                    //更新库中的模板
                    updateFingerData(index, updateFingerData);
                } else if (result == -3) {
                    showTip(msg.getTip());
                    tgapi.cancelRegisterGetImg(new CancelImgCallBack() {
                        @Override
                        public void cancelImgCallBack(Msg msg) {
                            Integer result = msg.getResult();
                            LogUtils.d("取消注册的：" + result);
                            toast(msg.getTip());
                        }
                    });
                } else {
                    showTip(msg.getTip());
                }
            }
        });
    }

    private void updateFingerData(Integer index, @NonNull byte[] updateFingerData) {
        //根据index更新模板库的模板
        if (templType == Constant.TEMPL_MODEL_3) {

        } else if (templType == Constant.TEMPL_MODEL_6) {

        }
    }

    //注册
    private void register() {
        pauseFingerVerify();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.d("  指静脉数量：" + finger6Size);
        tgapi.extractFeatureRegister(finger6Data, finger6Size, new RegisterCallBack() {
            @Override
            public void registerResult(Msg msg) {
                Integer result = msg.getResult();
                LogUtils.d("注册的结果：" + result);
                showTip(msg.getTip());
                if (result == 8) {
                    toast(msg.getTip());
                    byte[] fingerData = msg.getFingerData();
                    //将指静脉数据存储到文件夹
                    fingerData(fingerData, finger6Data,
                            finger6Size, Constant.MONI_EXTER_6_PATH);
                    //将指静脉数据存到数据库
//                    FingerDataUtil.getInstance().insertOrReplaceFinger(fingerData);
                    if (isCheck) {
                        isCancelVerify = false;
                    }
                } else if (result == -3) {
                    tgapi.cancelRegisterGetImg(new CancelImgCallBack() {
                        @Override
                        public void cancelImgCallBack(Msg msg) {
                            Integer result = msg.getResult();
                            LogUtils.d("取消注册的：" + result);
                            toast(msg.getTip());
                        }
                    });
                }
            }
        });
    }

    private byte[] fingerData(byte[] newFingerData, byte[] oldFingerData, Integer fingerSize, String savePath) {
        finger6Size = fingerSize + 1;
        byte[] cellDataSpace = cellDataSpace(finger6Size);
        if (oldFingerData != null && fingerSize > 0) {
            System.arraycopy(oldFingerData, 0, cellDataSpace, 0, oldFingerData.length);
            System.arraycopy(newFingerData, 0, cellDataSpace, oldFingerData.length, newFingerData.length);
        } else {
            System.arraycopy(newFingerData, 0, cellDataSpace, 0, newFingerData.length);
        }
        this.finger6Data = cellDataSpace;
        saveData(newFingerData, savePath);
        return cellDataSpace;
    }

    private boolean isRegisterService = false;

    @Override
    public void devOpenResult(Msg msg) {
        //打开设备的结果
        Integer result = msg.getResult();
        LogUtils.d("设备状态：" + result);
        if (result == 1) {
            getDevFw();
            toast("设备打开成功");
            showTip(msg.getTip());
            connectStatusTv.setText("设备连接成功");
            dismissDialog();
            //后比
            tgapi.startDevService(this, new OnStartDevStatusServiceListener() {
                @Override
                public void startDevServiceStatus(Boolean aBoolean) {
                    if (aBoolean) {
                        isRegisterService = true;
                    }
                }
            });
        }
    }

    private boolean isCheck = false;
    private boolean isCancelGetImg = false;

    @Override
    public void devStatus(Msg msg) {
        if (connectStatusTv != null)
            connectStatusTv.setText(msg.getTip());
        if (msg.getResult() == 1) {
            if (!isStartLoopVerify && isCheck) {
                taskSchedule();
            }
        } else {
            if (!isCancelGetImg)
                pauseFingerVerify();
        }
    }

    @Override
    public void fingerVerifyResult(int res, String msg, int score,
                                   int index, Long fingerId, byte[] updateFinger) {
        if (res == 1) {
            ToastUtil.toast(this, getString(R.string.verify_success));
        } else {
            if (res == -1 || res == -2)
                ToastUtil.toast(this, getString(R.string.verify_fail));
        }
    }

    private void saveData(byte[] saveData, String savePath) {
        long timeMillis = System.currentTimeMillis();
        String path = savePath + File.separator + String.valueOf(timeMillis);
        tgapi.saveDataToHost(saveData, path, new DataSaveCallBack() {
            @Override
            public void dataSaveCallBack(Msg msg) {
                if (msg.getResult() == 1) {
                    toast("存储成功");
                } else {
                    toast("存储失败");
                }
            }
        });
    }

    private byte[] cellDataSpace(int size) {
        byte[] cellFingerData = null;
        if (templType == Constant.TEMPL_MODEL_3) {
            cellFingerData = new byte[size * Constant.PERFECT_FEATURE_3];
        } else if (templType == Constant.TEMPL_MODEL_6) {
            cellFingerData = new byte[size * Constant.PERFECT_FEATURE_6];
        }
        return cellFingerData;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tgapi != null)
            tgapi.clearScreenLight(this);
        pauseFingerVerify();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finger6Data = null;
        finger6Size = 0;
        isStartLoopVerify = false;
    }


    private void taskSchedule() {
        if (!isCancelVerify) {
            LogUtils.d("启动FingerService  111");
            timer.schedule(myTask, 700, 700);
        }
    }

    private Boolean isCancelVerify = false;
    private Boolean isStartLoopVerify = false;

    private void pauseFingerVerify() {
        isCancelVerify = true;
        tgapi.setSound(true);
        if (tgapi != null) {
            tgapi.cancelRegisterGetImg(new CancelImgCallBack() {
                @Override
                public void cancelImgCallBack(Msg msg) {
                    if (msg.getResult() == 1) {
                        isCancelGetImg = true;
                        LogUtils.d("取消抓图");
                    }
                }
            });
        }
    }

    private class MyTask extends TimerTask {

        @Override
        public void run() {
            isStartLoopVerify = true;
            LogUtils.d("  是否验证：" + isCancelVerify);
            if (!isCancelVerify) {
                tgapi.setSound(false);
                tgapi.featureCompare1_N(finger6Data, finger6Size, new Verify1_NCallBack() {
                    @Override
                    public void verify1_NCallBack(Msg msg) {
                        Integer result = msg.getResult();
                        LogUtils.d("验证的结果:" + result);
                        if (result == 1) {
                            tgapi.getAP(TestActivity.this).play_verifySuccess();
                            //比对的分数
                            Integer score = msg.getScore();
                            //比对模板对应的位置
                            Integer index = msg.getIndex();
                            //可更新的模板数据
                            byte[] updateFingerData = msg.getFingerData();
                            showTip(msg.getTip() + " 分数:" + score);
                            //当比对分数大于某个设定的值，可更新指静脉模板
                            //eg：
                            //更新库中的模板
                            updateFingerData(index, updateFingerData);
                        } else if (result == -3) {
                            tgapi.getAP(TestActivity.this).play_time_out();
                            showTip(msg.getTip());
                            tgapi.cancelRegisterGetImg(new CancelImgCallBack() {
                                @Override
                                public void cancelImgCallBack(Msg msg) {
                                    Integer result = msg.getResult();
                                    LogUtils.d("取消注册的：" + result);
                                    toast(msg.getTip());
                                }
                            });
                        } else {
                            tgapi.getAP(TestActivity.this).play_verifyFail();
                            showTip(msg.getTip());
                        }
                    }
                });
            }
        }
    }

}
