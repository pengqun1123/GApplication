package com.sd.tgfinger.gapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sd.tgfinger.CallBack.DevCloseCallBack;
import com.sd.tgfinger.CallBack.DevOpenCallBack;
import com.sd.tgfinger.CallBack.FvInitCallBack;
import com.sd.tgfinger.CallBack.PermissionCallBack;
import com.sd.tgfinger.api.TGAPI;
import com.sd.tgfinger.utils.LogUtils;
import com.sd.tgfinger.utils.ToastUtil;


/**
 * Created By pq
 * on 2019/6/23
 */
public class BTestActivity extends AppCompatActivity implements View.OnClickListener
        , PermissionCallBack {

    private TGAPI tgapi = new TGAPI();
    private TextView tipTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behind);


        initView();

    }

    private void initView() {
        Button closeDevBtn = findViewById(R.id.closeDevBtn);
        Button openDevBtn = findViewById(R.id.openDevBtn);
        Button voiceDecreaceBtn = findViewById(R.id.voiceDecreaceBtn);
        Button voiceIncreaceBtn = findViewById(R.id.voiceIncreaceBtn);
        Button cancelRegisterBtnBehind = findViewById(R.id.cancelRegisterBtnBehind);
        Button registerBtnBehind = findViewById(R.id.registerBtnBehind);
        Button ver1_NBtn = findViewById(R.id.ver1_NBtn);
        CheckBox autoUpdateTempl = findViewById(R.id.autoUpdateTempl);
        TextView volumeTt = findViewById(R.id.volumeTt);
        RadioGroup templSumModel = findViewById(R.id.templSumModel);


        tipTv = findViewById(R.id.tipTv);

        openDevBtn.setOnClickListener(this);
        closeDevBtn.setOnClickListener(this);
        voiceDecreaceBtn.setOnClickListener(this);
        voiceIncreaceBtn.setOnClickListener(this);
        cancelRegisterBtnBehind.setOnClickListener(this);
        registerBtnBehind.setOnClickListener(this);
        ver1_NBtn.setOnClickListener(this);
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
                Log.i("===AAA", " i:" + i);

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeDevBtn:
                tgapi.openDev(handler, TGAPI.WORK_BEHIND, TGAPI.TEMPL_MODEL_6);
                break;
            case R.id.openDevBtn:
                tgapi.closeDev(handler);
                break;
            case R.id.voiceDecreaceBtn:
                tgapi.descreaseVolume(handler);
                break;
            case R.id.voiceIncreaceBtn:
                tgapi.increaseVolume(handler);
                break;
            case R.id.cancelRegisterBtnBehind:
                tgapi.cancelRegister(handler);
                break;
            case R.id.registerBtnBehind:
                tgapi.extractFeatureRegister(handler, null, 0);
                break;
            case R.id.ver1_NBtn:
                tgapi.featureCompare1_N(handler, null, 0);
                break;
        }
    }

    @Override
    public void permissionResult(int result) {
        LogUtils.i("权限的结果：" + result);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TGAPI.OPEN_DEV:
                    /**
                     * 1:设备打开成功，后比设置成功
                     * 2:设备打开成功，模式设置失败，该设备不支持6特征模板注册
                     * 3:设备打开成功，模式设置失败，请先删除设备中的三模板
                     * 4:设备打开成功，模式设置失败，请先删除设备中的六模板
                     * -1:设备打开失败
                     * -2:设备打开成功，模式设置失败
                     * -3:设备打开成功，入参错误
                     */
                    int openArg = msg.arg1;
                    if (openArg == 1) {
                        tipTv.setText("设备打开成功，后比设置成功");
                    } else if (openArg == 2) {
                        tipTv.setText("设备打开成功，模式设置失败，该设备不支持6特征模板注册");
                    } else if (openArg == 3) {
                        tipTv.setText("设备打开成功，模式设置失败，请先删除设备中的三模板");
                    } else if (openArg == 4) {
                        tipTv.setText("设备打开成功，模式设置失败，请先删除设备中的六模板");
                    } else if (openArg == -1) {
                        tipTv.setText("设备打开失败");
                    } else if (openArg == -2) {
                        tipTv.setText("设备打开成功，模式设置失败");
                    } else if (openArg == -3) {
                        tipTv.setText("设备打开成功，入参错误");
                    }
                    break;
                case TGAPI.CLOSE_DEV:
                    /**
                     * 1:指静脉设备关闭成功
                     * -1:指静脉设备关闭失败
                     */
                    int closeArg = msg.arg1;
                    if (closeArg == 1) {
                        tipTv.setText("指静脉设备关闭成功");
                    } else if (closeArg == -1) {
                        tipTv.setText("指静脉设备关闭失败");
                    }
                    break;
                case TGAPI.SET_DEV_MODEL:
                    /**
                     * 设置设备的工作模式
                     * 1：设置成功
                     * 2：设置失败，该设备不支持6特征模板注册
                     * 3：请先删除设备中的三模板
                     * 4：请先删除设备中的六模板
                     * -1：设置失败
                     * -2 ：入参错误
                     */
                    int setModelArg = msg.arg1;
                    if (setModelArg == 1) {
                        tipTv.setText("设置成功");
                    } else if (setModelArg == 2) {
                        tipTv.setText("设置失败，该设备不支持6特征模板注册");
                    } else if (setModelArg == 3) {
                        tipTv.setText("请先删除设备中的三模板");
                    } else if (setModelArg == 4) {
                        tipTv.setText("请先删除设备中的六模板");
                    } else if (setModelArg == -1) {
                        tipTv.setText("设置失败");
                    } else if (setModelArg == -2) {
                        tipTv.setText("入参错误");
                    }
                    break;
                case TGAPI.DEV_WORK_MODEL:
                    /**
                     * 1:前比3特征模板
                     * 2:后比
                     * 3:前比6特征模板
                     * -1:超时
                     */
                    int devModelArg = msg.arg1;
                    if (devModelArg == 1) {
                        tipTv.setText("前比3特征模板");
                    } else if (devModelArg == 2) {
                        tipTv.setText("后比");
                    } else if (devModelArg == 3) {
                        tipTv.setText("前比6特征模板");
                    } else if (devModelArg == -1) {
                        tipTv.setText("超时");
                    }
                    break;
                case TGAPI.CANCEL_REGISTER://取消注册接口也可取消验证
                    /**
                     * 1:取消注册成功
                     * -1:取消注册失败
                     */
                    int cancelRegisterArg = msg.arg1;
                    if (cancelRegisterArg == 1) {
                        tipTv.setText("取消注册成功");

                    } else if (cancelRegisterArg == -1) {
                        tipTv.setText("取消注册失败");
                    }
                    break;
                case TGAPI.EXTRACT_FEATURE_REGISTER:
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
                    int registerArg = msg.arg1;
                    if (registerArg == 1) {
                        tipTv.setText("特征提取成功,Output数据有效");
                        Bundle data = msg.getData();
                        if (data!=null){
                            byte[] fingerData = data.getByteArray(TGAPI.FINGER_DATA);
                            if (fingerData!=null&&fingerData.length>0){
                                ToastUtil.toast(BTestActivity.this,"获取到了注册的模板那数据");
                            }
                        }
                    }else if (registerArg==2){
                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
                    }else if (registerArg==3){
                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
                    }else if (registerArg==4){
                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
                    }else if (registerArg==5){
                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
                    }else if (registerArg==6){
                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
                    }else if (registerArg==7){
                        tipTv.setText("模板登记重复");
                    }else if (registerArg==8){
                        tipTv.setText("登记成功");
                    }else if (registerArg==9){
                        tipTv.setText("特征融合失败，因\"特征\"数据一致性差，Output数据无效");
                    }else if (registerArg==-1){
                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
                    }else if (registerArg==-2){
                        tipTv.setText("注册特征提取失败");
                    }else if (registerArg==-3){
                        tipTv.setText("抓图超时");
                    }else if (registerArg==-4){
                        tipTv.setText("设备断开");
                    }else if (registerArg==-5){
                        tipTv.setText("操作取消");
                    }else if (registerArg==-6){
                        tipTv.setText("入参错误");
                    }else if (registerArg==-7){
                        tipTv.setText("抓图未知错误");
                    }else if (registerArg==-8){
                        tipTv.setText("特征比对（1：N）失败，因参数不合法，Output数据无效");
                    }else if (registerArg==-9){
                        tipTv.setText("验证提取特征失败");
                    }else if (registerArg==-10){
                        tipTv.setText("登记失败");
                    }
                case TGAPI.FEATURE_COMPARE1_N:
                    /**
                     * 1:特征比对（1：N）成功，Output数据有效
                     * 2:特征比对（1：N）失败，仅Output的matchScore数据有效
                     * -1:特征比对（1：N）失败，因参数不合法，Output数据无效
                     */
                    int compareNArg = msg.arg1;
                    if (compareNArg == 1) {
                        tipTv.setText("特征比对（1：N）成功，Output数据有效");
                    }else if (compareNArg==-2){
                        tipTv.setText("特征比对（1：N）失败，仅Output的matchScore数据有效");
                    }else if (compareNArg==-3){
                        tipTv.setText("特征比对（1：N）失败，因参数不合法，Output数据无效");
                    }
                    break;
            }
        }
    };
}
