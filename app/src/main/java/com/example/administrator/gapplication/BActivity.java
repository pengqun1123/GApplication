package com.example.administrator.gapplication;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.TG.library.api.TG661JBAPI;
import com.TG.library.api.TG661JBehindAPI;

public class BActivity extends AppCompatActivity {

    private TemplAdapter templAdapter;
    private Button registerBtnBehind;
    private Button ver1_1Btn;
    private Button ver1_nBtn;
    private Button getTemplFW;
    private Button getTemplSN;
    private Button templTimeBtn;
    private Button voiceIncreaceBtn;
    private Button delIDHostTemplBtn;
    private Button delAllHostTemplBtn;
    private Button voiceDecreaceBtn;
    private Button getTemplAlgorVersionBtn;
    private ImageView clearEt, iv;
    private EditText templIDBehind;
    private TextView volumeTt, tipTv, devStatus, devModelTv;
    private boolean devStatu;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TG661JBehindAPI.INIT_FV:
                    /*
                     * 初始化算法接口
                     * 返回值:initFvArg
                     *  （1）1：初始化成功,算法接口有效
                     *  （2）2: 初始化失败,因证书路径错误,算法接口无效
                     *  （3）3: 初始化失败,因证书内容无效,算法接口无效
                     *  （4）4: 初始化失败,因证书内容过期,算法接口无效
                     *  （5）-1: 初始化失败,context不可为null
                     *  （6）-2: 初始化失败,证书字节流不可为null
                     */
                    int initFvArg = msg.arg1;
                    if (initFvArg == 1) {
                        tipTv.setText("初始化成功,算法接口有效");
                    } else if (initFvArg == 2) {
                        tipTv.setText("初始化失败,因证书路径错误,算法接口无效");
                    } else if (initFvArg == 3) {
                        tipTv.setText("初始化失败,因证书内容无效,算法接口无效");
                    } else if (initFvArg == 4) {
                        tipTv.setText("初始化失败,因证书内容过期,算法接口无效");
                    } else if (initFvArg == -1) {
                        tipTv.setText("初始化失败,context不可为null");
                    } else if (initFvArg == -2) {
                        tipTv.setText("初始化失败,证书字节流不可为null");
                    }
                    break;
            }
        }
    };

    private TG661JBAPI tg661JBAPI = TG661JBAPI.getTg661JBAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behind);

        initView();
        init();

    }

    private void init() {
        //检查权限，如果同意权限后会初始化一次算法；如果不需要权限的，则需要另行初始化算法
        tg661JBAPI.checkPermissions(handler, 0, this);
        if (!tg661JBAPI.getisInitFV())
            //初始化算法
            tg661JBAPI.initFV(handler, this, null, false);
    }

    private void initView() {
        //后比
        Button closeDevBtn = findViewById(R.id.closeDevBtn);
        Button openDevBtn = findViewById(R.id.openDevBtn);
        Button getDevModel = findViewById(R.id.getDevModel);
        Button getSDKVersion = findViewById(R.id.getSDKVersion);

        templIDBehind = findViewById(R.id.templIDBehind);
        tipTv = findViewById(R.id.tipTv);
        devModelTv = findViewById(R.id.devModelTv);
        devStatus = findViewById(R.id.devStatus);
        registerBtnBehind = findViewById(R.id.registerBtnBehind);
        Button cancelRegisterBtnBehind = findViewById(R.id.cancelRegisterBtnBehind);
        RadioGroup templSumModel = findViewById(R.id.templSumModel);
        final RadioButton templ3Rb = findViewById(R.id.templ3Rb);
        final RadioButton templ6Rb = findViewById(R.id.templ6Rb);
        ver1_1Btn = findViewById(R.id.ver1_1Btn);
        ver1_nBtn = findViewById(R.id.ver1_NBtn);
        getTemplFW = findViewById(R.id.getTemplFW);
        getTemplSN = findViewById(R.id.getTemplSN);
        templTimeBtn = findViewById(R.id.templTimeBtn);
        voiceIncreaceBtn = findViewById(R.id.voiceIncreaceBtn);
        delIDHostTemplBtn = findViewById(R.id.delIDHostTemplBtn);
        delAllHostTemplBtn = findViewById(R.id.delAllHostTemplBtn);
        volumeTt = findViewById(R.id.volumeTt);
        voiceDecreaceBtn = findViewById(R.id.voiceDecreaceBtn);
        //测试
        iv = findViewById(R.id.iv);
        Button getImg = findViewById(R.id.getImg);
        Button getImgFeature = findViewById(R.id.getImgFeature);
        Button getFeatureTempl = findViewById(R.id.getFeatureTempl);
        Button getMatchTempl = findViewById(R.id.getMatchTempl);
        Button get1_1 = findViewById(R.id.get1_1);
        Button get1_N = findViewById(R.id.get1_N);
        clearEt = findViewById(R.id.clearEt);

        getTemplAlgorVersionBtn = findViewById(R.id.getTemplAlgorVersionBtn);
        CheckBox autoUpdateTempl = findViewById(R.id.autoUpdateTempl);
        RecyclerView templFileRv = findViewById(R.id.templFileRv);
    }


}
