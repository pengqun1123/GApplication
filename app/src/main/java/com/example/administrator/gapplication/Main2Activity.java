package com.example.administrator.gapplication;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.TG.library.api.TG661JAPI;
import com.TG.library.utils.AlertDialogUtil;
import com.TG.library.utils.LogUtils;
import com.TG.library.utils.ToastUtil;
import com.example.administrator.adapters.TabAdapter;
import com.example.administrator.fragments.BehindFrag;
import com.example.administrator.fragments.FrontFrags;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements TabLayout.OnTabSelectedListener
        , View.OnClickListener, ViewPager.OnPageChangeListener {

    private List<String> titles;
    private List<Fragment> fragments;
    private FrontFrags frontFrags;
    private BehindFrag behindFrag;
    private AlertDialog waitDialog;
//    private TextView devStatusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TabLayout tab = findViewById(R.id.tab);
        ViewPager viewPager = findViewById(R.id.vp);


//        devStatusTv = findViewById(R.id.devStatus);

        //初始化
        TG661JAPI.getTG661JAPI().init(Main2Activity.this);

        addInitData();
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(tabAdapter);
        viewPager.setCurrentItem(0);
        this.position = 0;
        viewPager.addOnPageChangeListener(this);
        tab.setupWithViewPager(viewPager);
        tab.addOnTabSelectedListener(this);




    }

    private void addInitData() {
        if (fragments == null) {
            fragments = new ArrayList<>();
        }
        if (fragments.size() > 0) {
            fragments.clear();
        }
        if (titles == null) {
            titles = new ArrayList<>();
        }
        if (titles.size() > 0) {
            titles.clear();
        }
        frontFrags = FrontFrags.instance();
        behindFrag = BehindFrag.instance();
        fragments.add(frontFrags);
        fragments.add(behindFrag);
        titles.add("前比");
        titles.add("后比");
    }

    public void initScanHostTempls() {
        //获取主机中对应的模板列表
        frontFrags.getAimDirListToAdapter();
        behindFrag.getTemplList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //常亮屏幕
        tg661JAPI.keepScreenLight(Main2Activity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消常量屏幕
        tg661JAPI.clearScreenLight(Main2Activity.this);
    }

    private TG661JAPI tg661JAPI = TG661JAPI.getTG661JAPI();
    //3模板还是6模板
    private int templModelType = TG661JAPI.TEMPL_MODEL_3;//默认3模板

    private int workType = TG661JAPI.WORK_FRONT;//设备工作模式--》前比
    private boolean devOpen;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case TG661JAPI.WAIT_DIALOG:
//                    int typeDialog = msg.arg1;
//                    if (typeDialog == 1) {
//                        String tipStr = (String) msg.obj;
//                        waitDialog = AlertDialogUtil.Instance()
//                                .showWaitDialog(Main2Activity.this, tipStr);
//                    } else if (typeDialog == -1) {
//                        if (waitDialog != null && waitDialog.isShowing()) {
//                            waitDialog.dismiss();
//                        }
//                    }
//                    break;
//                case TG661JAPI.OPEN_DEV:
//                    int openDevArg = msg.arg1;
//                    if (openDevArg == 1) {
//                        initScanHostTempls();
//                        //获取设备固件号，序列号,连接状态
//                        tg661JAPI.getDevFW(handler);
//                        tg661JAPI.getDevSN(handler);
//                        //获取设备已注册的模板列表
//                        tg661JAPI.getDevTemplNum(handler, 0);
//                        tg661JAPI.getDevStatus(handler);
//
//                        frontFrags.consoleAddData("设备打开成功,工作模式设置成功");
//                    } else if (openDevArg == -1) {
//                        frontFrags.consoleAddData("设备打开失败");
//                    }
//                    break;
//                case TG661JAPI.CLOSE_DEV:
//                    int closeDevArg = msg.arg1;
//                    if (closeDevArg == -1) {
////                        devStatusTv.setText("设备状态:设备关闭失败");
//                        frontFrags.consoleAddData("设备关闭失败");
//                    } else if (closeDevArg == 1) {
////                        devStatusTv.setText("设备状态:设备关闭成功");
//                        frontFrags.consoleAddData("设备关闭成功");
//                    } else if (closeDevArg == 2) {
////                        devStatusTv.setText("设备状态:设备已关闭");
//                        frontFrags.consoleAddData("设备已关闭");
//                    }
//                    break;
//                case TG661JAPI.DEV_FW:
//                    int devFWArg = msg.arg1;
//                    if (devFWArg == -1) {
//                        frontFrags.consoleAddData("获取设备固件号失败");
//                    } else if (devFWArg == 1) {
//                        String fw = (String) msg.obj;
//                        frontFrags.setDevFW(fw);
//                        frontFrags.consoleAddData("获取设备固件号成功");
//                    }
//                    break;
//                case TG661JAPI.DEV_SN:
//                    int devSNArg = msg.arg1;
//                    if (devSNArg == 1) {
//                        String sn = (String) msg.obj;
//                        frontFrags.setDevSN(sn);
//                        frontFrags.consoleAddData("获取设备序列号成功");
//                    } else if (devSNArg == -1) {
//                        frontFrags.consoleAddData("获取设备序列号失败");
//                    }
//                    break;
//                case TG661JAPI.DEV_STATUS:
//                    int devStatus = msg.arg1;
//                    ToastUtil.toast(Main2Activity.this," 设备状态："+devStatus);
//                    if (devStatus >= 0) {
////                        devStatusTv.setText("设备状态:已连接");
//                        frontFrags.setDevStatus("状态:已连接");
//                        behindFrag.setDevStatus("状态:已连接");
//                    } else if (devStatus == -1) {
//                        frontFrags.setDevStatus("状态:未连接");
//                        behindFrag.setDevStatus("状态:未连接");
//                    } else if (devStatus == -2) {
////                        devStatusTv.setText("设备状态:已断开,重新连接中...");
//                        frontFrags.setDevStatus("状态:未连接/已断开");
//                        behindFrag.setDevStatus("状态:未连接/已断开");
//                    }
//                    break;
//                case TG661JAPI.DEV_TEMPL_NUM:
//                    int devTemplNumArg = msg.arg1;
//                    LogUtils.d("   设备可注册的模板数量 ：" + devTemplNumArg);
//                    if (devTemplNumArg >= 0) {
//                        int type = (int) msg.obj;
//                        if (type == 0) {
//                            frontFrags.setYiRegTempNum(String.valueOf(devTemplNumArg));
//                        } else if (type == 1) {
//                            frontFrags.setKeRegTempNum(String.valueOf(devTemplNumArg));
//                        }
//                    } else {
//                        frontFrags.setKeRegTempNum("获取失败");
//                        frontFrags.consoleAddData("设备可注册的模板数量获取失败");
//                    }
//                    break;
//                case TG661JAPI.DEV_WORK_MODEL:
//                    int devWorkModelArg = msg.arg1;
//                    if (devWorkModelArg == 1) {
//                        frontFrags.setDevWorkModel("前比3模板");
//                    } else if (devWorkModelArg == 2) {
//                        frontFrags.setDevWorkModel("后比");
//                    } else if (devWorkModelArg == 3) {
//                        frontFrags.setDevWorkModel("前比6模板");
//                    } else if (devWorkModelArg == -1) {
//                        frontFrags.setDevWorkModel("获取工作模式失败");
//                    }
//                    break;
//                case TG661JAPI.DEV_TEMPL_LIST:
//                    int devTemplListArg = msg.arg1;
//                    if (devTemplListArg == 1) {
//                        ArrayList<String> templList = msg.getData().getStringArrayList(TG661JAPI.TEMP_LIST);
//                        Log.d("===LOG","  设备列表模板数量："+templList.size());
//                        frontFrags.setInitDevTemplAdapter(templList);
//                    } else if (devTemplListArg == 2) {
//                        frontFrags.setDevWorkModel("获取设备端模板信息列表数量为:0");
//                        frontFrags.setYiRegTempNum("0");
//                        frontFrags.setInitDevTemplAdapter(null);
//                    } else if (devTemplListArg == -1) {
//                        frontFrags.setDevWorkModel("调用获取设备端模板信息列表接口超时");
//                    }
//                    break;
//                case TG661JAPI.SET_DEV_MODEL:
//                    int setDevModelArg = msg.arg1;
//                    Log.d("===OOO","   设置工作模式的结果码："+setDevModelArg);
//                    if (setDevModelArg == 1) {
//                        frontFrags.devWrokModel();
//                        frontFrags.setDevWorkModel("设备工作模式设置成功");
//                    } else if (setDevModelArg == 2) {
//                        frontFrags.setDevWorkModel("设置失败，该设备不支持6特征模板注册");
//                    } else if (setDevModelArg == 3) {
//                        frontFrags.setDevWorkModel("请先删除设备中的3模板");
//                    } else if (setDevModelArg == 4) {
//                        frontFrags.setDevWorkModel("请先删除设备中的6模板");
//                    } else if (setDevModelArg == -1) {
//                        frontFrags.setDevWorkModel("设置失败");
//                    } else if (setDevModelArg == -2) {
//                        frontFrags.setDevWorkModel("入参错误");
//                    }
//                    break;
//            }
//        }
//    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    private int position;

    @Override
    public void onPageSelected(int position) {
//        if (position == 0) {
//            workType = TG661JAPI.WORK_FRONT;
//            templModelType = TG661JAPI.TEMPL_MODEL_3;//前比默认是三特征模板
//        } else if (position == 1) {
//            workType = TG661JAPI.WORK_BEHIND;
//            templModelType = TG661JAPI.TEMPL_MODEL_6;//后比默认是六特征模板
//        }
//        tg661JAPI.setDevWorkModel(handler, workType, templModelType);
//        initScanHostTempls();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
