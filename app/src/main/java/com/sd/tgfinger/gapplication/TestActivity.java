package com.sd.tgfinger.gapplication;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.DevStatusCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.ReadDataCallBack;
import com.sd.tgfinger.CallBack.RegisterCallBack;
import com.sd.tgfinger.CallBack.Verify1_1CallBack;
import com.sd.tgfinger.CallBack.Verify1_NCallBack;
import com.sd.tgfinger.api.TGAPI;
import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;
import com.sd.tgfinger.tgApi.TGBApi;
import com.sd.tgfinger.utils.AlertDialogUtil;
import com.sd.tgfinger.utils.FileUtil;
import com.sd.tgfinger.utils.LogUtils;
import com.sd.tgfinger.utils.ToastUtil;

import java.io.File;
import java.util.List;

/**
 * Created By pq
 * on 2019/9/19
 */
public class TestActivity extends AppCompatActivity implements DevOpenCallBack, DevStatusCallBack,
        View.OnClickListener {

    private TextView tipTv, devStatus;
    @SuppressLint("InlinedApi")
    private String[] perms = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private AlertDialog alertDialog;

    private int readDataType = -1;
    private int templType = -1;
    private TextView volumeTt, SDKVersion;
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
    private TGBApi tgapi;
    private byte[] finger3Data, finger6Data;
    private Integer finger3Size, finger6Size;
    private EditText et;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behind);

        tgapi = TGBApi.getTGAPI();
        initView();
        pers();//权限申请
        String sdkVersion = tgapi.getSDKVersion();
        SDKVersion.setText(sdkVersion);
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
        TGBApi.getTGAPI().init(this, null, new FvInitCallBack() {
            @Override
            public void fvInitResult(Msg msg) {
                if (msg.getResult() == 1) {
                    showTip("初始化成功");
                    openDev();
                }
            }
        });
    }

    private void openDev() {
        if (!tgapi.isDevOpen()) {
            //初始化准备数据
            readData();
            alertDialog = AlertDialogUtil.Instance()
                    .showWaitDialog(this, "设备正在打开...");
            tgapi.openDev(this, TGAPI.WORK_BEHIND, TGAPI.TEMPL_MODEL_6, true,
                    this, this);
        } else {
            toast("设备已经打开");
        }
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
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    //初始化只准备数据
    private void readData() {
        if (templType == Constant.TEMPL_MODEL_3) {
            tgapi.readDataFromHost(this, Constant.MONI_EXTER_3_PATH, new ReadDataCallBack() {
                @Override
                public void readData(Msg msg) {
                    Integer result = msg.getResult();
                    LogUtils.d("读取3数据：" + result);
                    if (result == 1) {
                        finger3Data = msg.getFingerData();
                        finger3Size = msg.getFingerSize();
                    } else {
                        finger3Size = 0;
                    }
                }
            });
        } else if (templType == Constant.TEMPL_MODEL_6) {
            tgapi.readDataFromHost(this, Constant.MONI_EXTER_6_PATH, new ReadDataCallBack() {
                @Override
                public void readData(Msg msg) {
                    Integer result = msg.getResult();
                    LogUtils.d("读取4数据：" + result);
                    if (result == 1) {
                        finger6Data = msg.getFingerData();
                        finger6Size = msg.getFingerSize();
                    } else {
                        finger6Size = 0;
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
        volumeTt = findViewById(R.id.volumeTt);
        SDKVersion = findViewById(R.id.SDKVersion);
        devStatus = findViewById(R.id.devStatus);
        templSumModel = findViewById(R.id.templSumModel);
        templ3Rb = findViewById(R.id.templ3Rb);
        templ6Rb = findViewById(R.id.templ6Rb);
        tipTv = findViewById(R.id.tipTv);
        et = findViewById(R.id.templIDBehind);

        tgapi.setVolume(this, 1);
        getCurrentVoice();
        //默认特征模式为6模板
        templType = TGAPI.TEMPL_MODEL_6;
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
                    //readFingerData(3);
                } else if (templ6Rb.getId() == i) {
                    templType = Constant.TEMPL_MODEL_6;
                    tgapi.setTemplModelType(Constant.TEMPL_MODEL_6);
                    //初始化数据，开启连续验证
                    //readFingerData(3);
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
                if (tgapi.isDevOpen())
                    tgapi.closeDev(this, new DevCloseCallBack() {
                        @Override
                        public void devCloseResult(Msg msg) {
                            Integer result = msg.getResult();
                            LogUtils.d("关闭设备：" + result);
                            toast(msg.getTip());
                            dismissDialog();
                        }
                    });
                else
                    toast("设备已经关闭");
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
                tgapi.cancelRegisterGetImg(this, new CancelImgCallBack() {
                    @Override
                    public void cancelImgCallBack(Msg msg) {
                        Integer result = msg.getResult();
                        LogUtils.d("取消注册的：" + result);
                        toast(msg.getTip());
                    }
                });
                break;
            case R.id.registerBtnBehind:
                //注册前读取数据，查重
//                templSumModel.setEnabled(false);
//                templ3Rb.setEnabled(false);
//                templ6Rb.setEnabled(false);
//                registerBtnBehind.setClickable(false);
//                if (tgapi.isContinueVerify()) {
//                    //处于连续验证状态,先调用取消抓图接口，获取已注册的数据查重，，再调用注册接口
//                    cancelGetImgType = 1;
//                    Log.d("===KKK", " cancelGetImgType: " + cancelGetImgType);
//                    tgapi.cancelRegisterGetImg(handler);
//                } else {
//                    //没有处于验证中，获取已注册的数据查重，调用注册接口
//                    readFingerData(1);
//                }
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
            tgapi.featureCompare1_1(TestActivity.this, verifyData, new Verify1_1CallBack() {
                @Override
                public void verify1CallBack(Msg msg) {
                    Integer result = msg.getResult();
                    Integer score = 0;
                    if (result == 1) {
                        //验证分数
                        score = msg.getScore();
                    }
                    showTip(msg.getTip() + score);
                }
            });
        }else {
            toast("请检查文件数据");
        }
    }


    private void verifyN() {
        if (templType == Constant.TEMPL_MODEL_3) {
            if (finger3Data == null) {
                toast("请先注册");
                return;
            }
            verifyNFinger(finger3Data, finger3Size);
        } else if (templType == Constant.TEMPL_MODEL_6) {
            if (finger6Data == null) {
                toast("请先注册");
                return;
            }
            verifyNFinger(finger6Data, finger6Size);
        }
    }

    //1:N的验证
    private void verifyNFinger(@NonNull byte[] fingerData, Integer fingerSize) {
        tgapi.featureCompare1_N(this, fingerData, fingerSize, new Verify1_NCallBack() {
            @Override
            public void verify1_NCallBack(Msg msg) {
                Integer result = msg.getResult();
                LogUtils.d("验证的结果:" + result);
                showTip(msg.getTip());
                if (result == 1) {
                    //比对的分数
                    Integer score = msg.getScore();
                    //比对模板对应的位置
                    Integer index = msg.getIndex();
                    //可更新的模板数据
                    byte[] updateFingerData = msg.getFingerData();
                    //当比对分数大于某个设定的值，可更新指静脉模板
                    //eg：
                    if (score >= 80) {
                        updateFingerData(index, updateFingerData);
                    }
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
        if (templType == Constant.TEMPL_MODEL_3) {
            tgapi.extractFeatureRegister(this, finger3Data, finger3Size, new RegisterCallBack() {
                @Override
                public void registerResult(Msg msg) {
                    Integer result = msg.getResult();
                    LogUtils.d("注册的结果：" + result);
                    if (result == 1) {
                        toast(msg.getTip());
                        finger3Size++;
                        byte[] fingerData = msg.getFingerData();
//                        byte[] cellDataSpace = cellDataSpace(finger3Size);
//                        if (finger3Data != null && finger3Size > 0) {
//                            System.arraycopy(finger3Data, 0, cellDataSpace, 0, finger3Data.length);
//                            System.arraycopy(fingerData, 0, cellDataSpace, finger3Data.length, fingerData.length);
//                        } else {
//                            System.arraycopy(fingerData, 0, cellDataSpace, 0, fingerData.length);
//                        }
//                        finger3Data = cellDataSpace;
//                        saveData(fingerData, Constant.MONI_EXTER_3_PATH);
                        finger3Data = fingerData(fingerData, finger3Data,
                                finger3Size, Constant.MONI_EXTER_3_PATH);
                    }
                }
            });
        } else if (templType == Constant.TEMPL_MODEL_6) {
            tgapi.extractFeatureRegister(this, finger6Data, finger6Size, new RegisterCallBack() {
                @Override
                public void registerResult(Msg msg) {
                    Integer result = msg.getResult();
                    LogUtils.d("注册的结果：" + result);
                    showTip(msg.getTip());
                    if (result == 8) {
                        toast(msg.getTip());
                        finger6Size++;
                        byte[] fingerData = msg.getFingerData();
//                        byte[] cellDataSpace = cellDataSpace(finger6Size);
//                        if (finger6Data != null && finger6Size > 0){
//                            System.arraycopy(finger6Data, 0, cellDataSpace, 0, finger6Data.length);
//                            System.arraycopy(fingerData, 0, cellDataSpace, finger6Data.length, fingerData.length);
//                        }else {
//                            System.arraycopy(fingerData, 0, cellDataSpace, 0, fingerData.length);
//                        }
//                        finger6Data = cellDataSpace;
//                        saveData(fingerData, Constant.MONI_EXTER_6_PATH);
                        finger6Data = fingerData(fingerData, finger6Data,
                                finger6Size, Constant.MONI_EXTER_6_PATH);
                    }
                }
            });
        }
    }

    private byte[] fingerData(byte[] newFingerData, byte[] oldFingerData, Integer fingerSize, String savePath) {
        byte[] cellDataSpace = cellDataSpace(fingerSize);
        if (oldFingerData != null && fingerSize > 0) {
            System.arraycopy(oldFingerData, 0, cellDataSpace, 0, oldFingerData.length);
            System.arraycopy(newFingerData, 0, cellDataSpace, oldFingerData.length, newFingerData.length);
        } else {
            System.arraycopy(newFingerData, 0, cellDataSpace, 0, newFingerData.length);
        }
        finger6Data = cellDataSpace;
        saveData(newFingerData, savePath);
        return cellDataSpace;
    }

    @Override
    public void devOpenResult(Msg msg) {
        //打开设备的结果
        Integer result = msg.getResult();
        LogUtils.d("设备打开：" + result);
        if (result == 1) {
            toast("设备打开成功");
            showTip(msg.getTip());
            dismissDialog();
        }
    }

    @Override
    public void devStatus(Msg msg) {
        Integer result = msg.getResult();
        LogUtils.d("设备的状态：" + result);

    }

    private void saveData(byte[] saveData, String savePath) {
        long timeMillis = System.currentTimeMillis();
        String path = savePath + File.separator + String.valueOf(timeMillis);
        tgapi.saveDataToHost(this, saveData, path, new DataSaveCallBack() {
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
    protected void onDestroy() {
        super.onDestroy();
        finger6Data = null;
        finger3Data = null;
        finger3Size = 0;
        finger6Size = 0;
    }

}
