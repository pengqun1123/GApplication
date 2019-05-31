package com.example.administrator.gapplication;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.TG.library.CallBack.ActionClickListener;
import com.TG.library.CallBack.CommitCallBack;
import com.TG.library.api.TG661JFrontAPI;
import com.TG.library.utils.AlertDialogUtil;
import com.TG.library.utils.LogUtils;
import com.TG.library.utils.RegularUtil;
import com.TG.library.utils.ToastUtil;
import com.example.administrator.adapters.ConsoleTipAdapter;
import com.example.administrator.adapters.HostTemplAdapter;
import com.example.administrator.adapters.TemplAdapter;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * 前比Activity
 */
public class FrontActivity extends AppCompatActivity implements View.OnClickListener,
        TemplAdapter.ItemClick, HostTemplAdapter.ItemClick, ActionClickListener
        , CheckBox.OnCheckedChangeListener, CommitCallBack {

    private TextView keRegTempNum;
    private TextView yiRegTempNum;
    private TextView devFW;
    private TextView devSN;
    private TextView devStatusTv;
    private TextView devWorkModel;
    private TextView tipTv;
    private boolean devOpen;
    private EditText userNameEt;
    private ConsoleTipAdapter consoleTipAdapter;
    private TemplAdapter templAdapter;
    private HostTemplAdapter hostTemplAdapter;
    private CheckBox openContinueVerifyCb;
    private TextView voiceTv, dev_StatusTv;

    private String delHostDatFileName;

    private boolean devStatus;
    private AlertDialog waitDialog;
    //标记区分设备中最大的模板数或者已注册的模板数
    private int devTempls = 0;
    //3模板还是6模板
    private int templType = TG661JFrontAPI.TEMPL_MODEL_3;//默认3模板
    private TG661JFrontAPI tg661JFrontAPI = TG661JFrontAPI.getTg661jFrontApi();
    private ImageView clearEt;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TG661JFrontAPI.OPEN_DEV:
                    int openDevArg = msg.arg1;
                    if (openDevArg == 1) {
                        //初始化获取主机文件夹下模板列表
                        getAimDirListToAdapter();
                        //获取设备固件号，序列号,连接状态
                        tg661JFrontAPI.getDevFW(handler);
                        tg661JFrontAPI.getDevSN(handler);
                        //获取设备已注册的模板列表
                        tg661JFrontAPI.getDevTemplNum(handler, 0);
                        tg661JFrontAPI.getDevStatus(handler);

                        tipTv.setText("设备打开成功,工作模式设置成功");
                    } else if (openDevArg == -1) {
                        tipTv.setText("设备打开失败");
                    }
                    break;
                case TG661JFrontAPI.CLOSE_DEV:
                    int closeDevArg = msg.arg1;
                    if (closeDevArg == -1) {
                        tipTv.setText("设备关闭失败");
                    } else if (closeDevArg == 1) {
                        tipTv.setText("设备关闭成功");
                    } else if (closeDevArg == 2) {
                        tipTv.setText("设备已关闭");
                    }
                    break;
                case TG661JFrontAPI.WAIT_DIALOG:
                    int type = msg.arg1;
                    if (type == 0) {
                        String tipStr = (String) msg.obj;
                        waitDialog = AlertDialogUtil.Instance()
                                .showWaitDialog(FrontActivity.this, tipStr);
                    } else if (type == 1) {
                        if (waitDialog != null && waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                    }
                    break;
                case TG661JFrontAPI.DEV_STATUS:
                    /*
                     * 设备状态：
                     *  1：设备状态：已连接
                     *  -1：已断开,连接中...
                     */
                    int devStatusArg = msg.arg1;
                    Log.d("===TAG", " 接收到的设备状态：" + devStatusArg);
                    if (devStatusArg >= 0) {
                        if (tipTv.getText().toString().contains("断开")) {
                            tipTv.setText("设备状态：已连接");
                        }
                        dev_StatusTv.setText("设备状态:已连接");
                        devStatusTv.setText("设备状态:已连接");
                    } else if (devStatusArg == -1) {
                        tipTv.setText("设备状态：未连接");
                        dev_StatusTv.setText("设备状态:未连接");
//                        registerBtnBehind.setText("注册");
//                        isGetImg = false;
                    } else if (devStatusArg == -2) {
                        tipTv.setText("设备状态：未连接/已断开");
                        dev_StatusTv.setText("设备状态:已断开,重新连接中...");
//                        registerBtnBehind.setText("注册");
//                        isGetImg = false;
                    }
                    break;
                case TG661JFrontAPI.SET_DEV_MODEL:
                    /*
                     * 设置设备工作模式
                     * 返回值：setDevModelArg
                     * 1:设备工作模式设置成功
                     * 2:设置失败，该设备不支持6特征模板注册
                     * 3:请先删除设备中的3模板
                     * 4:请先删除设备中的6模板
                     * -1:设备工作模式设置失败
                     * -2:入参错误
                     */
                    int setDevModelArg = msg.arg1;
                    if (setDevModelArg == 1) {
                        tg661JFrontAPI.getDevWorkModel(handler);
                        tipTv.setText("设备工作模式设置成功");
                        consoleTipAdapter.addData("设备工作模式设置成功");
                        devTempls = 0;
                        tg661JFrontAPI.getDevTemplNum(handler, devTempls);
                    } else if (setDevModelArg == 2) {
                        tipTv.setText("设置失败，该设备不支持6特征模板注册");
                        consoleTipAdapter.addData("设置失败，该设备不支持6特征模板注册");
                    } else if (setDevModelArg == 3) {
                        tipTv.setText("请先删除设备中的3模板");
                        consoleTipAdapter.addData("请先删除设备中的3模板");
                    } else if (setDevModelArg == 4) {
                        tipTv.setText("请先删除设备中的6模板");
                        consoleTipAdapter.addData("请先删除设备中的6模板");
                    } else if (setDevModelArg == -1) {
                        tipTv.setText("设置失败");
                        consoleTipAdapter.addData("设置失败");
                    } else if (setDevModelArg == -2) {
                        tipTv.setText("入参错误");
                        consoleTipAdapter.addData("入参错误");
                    }
                    break;
                case TG661JFrontAPI.DEV_WORK_MODEL:
                    /*
                     * 获取工作模式：
                     * 返回值：devWorkModelArg
                     * -1:获取工作模式失败
                     * 1:前比3模板
                     * 2:后比
                     * 3:前比6模板
                     */
                    int devWorkModelArg = msg.arg1;
                    if (devWorkModelArg == 1) {
                        devWorkModel.setText(String.format("设备的工作模式:%s", "前比3模板"));
                    } else if (devWorkModelArg == 2) {
                        devWorkModel.setText(String.format("设备的工作模式:%s", "后比"));
                    } else if (devWorkModelArg == 3) {
                        devWorkModel.setText(String.format("设备的工作模式:%s", "前比6模板"));
                    } else if (devWorkModelArg == -1) {
                        devWorkModel.setText("获取工作模式失败");
                    }
                    break;
                case TG661JFrontAPI.DEV_TEMPL_CLEAR:
                    /*
                     * 清空设备中的模板
                     * 返回值：devClearTemplArg
                     * 1:设备中模板清空成功
                     * 2:设备中模板不存在
                     * -1:设备中模板清空失败
                     */
                    int devClearTemplArg = msg.arg1;
                    if (devClearTemplArg == 1) {
                        //获取设备中得模板列表，更新界面
                        devTempls = 0;
                        tg661JFrontAPI.getDevTemplNum(handler, devTempls);
                        tipTv.setText("设备中模板清空成功");
                        consoleTipAdapter.addData("设备中模板清空成功");
                        templAdapter.clearData();
                    } else if (devClearTemplArg == 2) {
                        tipTv.setText("设备中模板不存在");
                        consoleTipAdapter.addData("设备中模板不存在");
                    } else if (devClearTemplArg == -1) {
                        tipTv.setText("设备中模板清空失败");
                        consoleTipAdapter.addData("设备中模板清空失败");
                    }
                    break;
                case TG661JFrontAPI.DEV_VOICE:
                    /*
                     * 调节设备音量
                     * 返回值:
                     * 1: 设备中模板清空失败
                     * -1: 设备音量调节失败
                     */
                    int devVoiceArg = msg.arg1;
                    if (devVoiceArg == 1) {
                        String voiceValue = (String) msg.obj;
                        voiceTv.setText(voiceValue);
                        tipTv.setText("设备音量调节成功");
                    } else if (devVoiceArg == 2) {
                        tipTv.setText("已是最小音量");
                    } else if (devVoiceArg == 3) {
                        tipTv.setText("已是最大音量");
                    } else if (devVoiceArg == -1) {
                        tipTv.setText("设备音量调节失败");
                    }
                    break;
                case TG661JFrontAPI.DEV_REGISTER:
                    /*
                     * 设备注册：
                     * 返回值：devRegisterArg
                     * 0:登记成功
                     * -1:登记失败
                     */
                    int devRegisterArg = msg.arg1;
                    if (devRegisterArg == -1 || devRegisterArg == 2) {
                        tipTv.setText("登记失败");
                        consoleTipAdapter.addData("登记失败");
                    } else if (devRegisterArg == 1) {
                        //获取设备中得模板列表，更新界面
                        devTempls = 0;
                        tg661JFrontAPI.getDevTemplNum(handler, devTempls);
                        tipTv.setText("登记成功");
                        consoleTipAdapter.addData("登记成功");
                    } else if (devRegisterArg == -9) {
                        tipTv.setText("注册名长度不可超过49位");
                        consoleTipAdapter.addData("注册名长度不可超过49位");
                    }
                    break;
                case TG661JFrontAPI.CONTINUE_VERIFY:
                    /*
                     * 连续验证:
                     * 返回值:continueVerifyArg
                     * 1:连续验证调用成功
                     * -1:连续验证调用失败
                     */
                    int continueVerifyArg = msg.arg1;
                    if (continueVerifyArg == 1) {
                        consoleTipAdapter.addData("连续验证调用成功");
                    } else if (continueVerifyArg == -1) {
                        consoleTipAdapter.addData("连续验证调用失败");
                    }
                    break;
                case TG661JFrontAPI.DEV_VERIFY_TEMPL:
                    /*
                     * 设备1:N验证模板
                     * 返回值：devVerify1_NArg
                     * -1:调用设备1:N验证接口失败
                     * 1:1:N验证成功
                     * 2:1:N验证失败
                     */
                    //前比1:N验证
                    int devVerify1_NArg = msg.arg1;
                    if (devVerify1_NArg == -1) {
                        tipTv.setText("调用设备1:N验证接口失败");
                        consoleTipAdapter.addData("调用设备1:N验证接口失败");
                    } else if (devVerify1_NArg == 1) {
                        tipTv.setText("1:N验证成功");
                        consoleTipAdapter.addData("1:N验证成功");
                    } else if (devVerify1_NArg == 2) {
                        tipTv.setText("1:N验证失败");
                        consoleTipAdapter.addData("1:N验证失败");
                    } else if (devVerify1_NArg == -2) {
                        tipTv.setText("入参错误");
                        consoleTipAdapter.addData("入参错误");
                    }
                    break;
                case TG661JFrontAPI.DEV_VERIFY1_1:
                    /*
                     * 设备1:1验证失败：
                     * 返回值：devVerify1_1Arg
                     * -1:调用设备1:1验证接口失败
                     * 1:1:1验证成功
                     * 2:1:1验证失败
                     * 3:比对的模板不存在
                     */
                    int devVerify1_1Arg = msg.arg1;
                    Log.d("===LOG", "   1：1验证结果：" + devVerify1_1Arg);
                    if (devVerify1_1Arg == -1) {
                        consoleTipAdapter.addData("调用设备1:1验证接口失败");
                    } else if (devVerify1_1Arg == 1) {
                        tipTv.setText("1:1验证成功");
                        consoleTipAdapter.addData("1:1验证成功");
                    } else if (devVerify1_1Arg == 2) {
                        tipTv.setText("1:1验证失败");
                        consoleTipAdapter.addData("1:1验证失败");
                    } else if (devVerify1_1Arg == 3) {
                        tipTv.setText("比对的模板不存在");
                        consoleTipAdapter.addData("比对的模板不存在");
                    } else if (devVerify1_1Arg == -2) {
                        tipTv.setText("入参错误");
                        consoleTipAdapter.addData("入参错误");
                    }
                    break;
                case TG661JFrontAPI.CANCEL_VERIFY:
                    /*
                     * 取消验证或注册：
                     * 返回值：cancelRegisterArg
                     * 0:取消成功
                     * -1:取消失败
                     */
                    int cancelRegisterArg = msg.arg1;
                    if (cancelRegisterArg == 1) {
                        tipTv.setText("取消成功");
                    } else if (cancelRegisterArg == -1) {
                        tipTv.setText("取消失败");
                    }
                    break;
                case TG661JFrontAPI.WRITE_DEV_INFO:
                    /*
                     * 往设备中写入信息
                     * 返回值:writeDevInfoArg
                     * 1:设备写入信息成功
                     * -1:设备写入数据超时
                     * -2:入参错误
                     */
                    int writeDevInfoArg = msg.arg1;
                    if (writeDevInfoArg == 1) {
                        tipTv.setText("设备写入信息成功");
                        consoleTipAdapter.addData("设备写入信息成功");
                    } else if (writeDevInfoArg == -1) {
                        tipTv.setText("设备写入数据超时");
                        consoleTipAdapter.addData("设备写入数据超时");
                    } else if (writeDevInfoArg == -2) {
                        tipTv.setText("入参错误");
                        consoleTipAdapter.addData("入参错误");
                    }
                    break;
                case TG661JFrontAPI.READ_DEV_INFO:
                    /*
                     * 读取设备信息
                     * 返回值:readDevInfoArg
                     * 1:设备读取信息成功
                     * -1:设备写入数据超时
                     * -2:入参错误
                     */
                    int readDevInfoArg = msg.arg1;
                    if (readDevInfoArg == 1) {
                        String devInfo = (String) msg.obj;//设备信息
                        if (TextUtils.isEmpty(devInfo)) {
                            ToastUtil.toast(FrontActivity.this, "暂无设备信息");
                        } else {
                            AlertDialogUtil.Instance().showResultDialog(FrontActivity.this,
                                    devInfo, true);
                        }
                        tipTv.setText(MessageFormat.format("设备信息:{0}", devInfo));
                        consoleTipAdapter.addData("设备读取信息成功");
                    } else if (readDevInfoArg == -1) {
                        tipTv.setText("设备写入数据超时");
                        consoleTipAdapter.addData("设备读取数据超时");
                    } else if (readDevInfoArg == -2) {
                        tipTv.setText("设备写入数据超时");
                        consoleTipAdapter.addData("入参错误");
                    }
                    break;
                case TG661JFrontAPI.UP_TEMPL_HOST:
                    /*
                     * 上传的那个模板到主机
                     * 返回值：upTemplHostArg
                     * -1:上传超时
                     * 0:上传主机成功，写入主机成功
                     * 1:设备中不存在待上传的模板
                     * 2:上传主机失败
                     */
                    int upTemplHostArg = msg.arg1;
                    if (upTemplHostArg == -1) {
                        tipTv.setText("上传超时");
                        consoleTipAdapter.addData("上传超时");
                    } else if (upTemplHostArg == 1) {
                        tipTv.setText("上传主机成功，写入主机成功");
                        consoleTipAdapter.addData("上传主机成功，写入主机成功");
                        boolean writeFile = (boolean) msg.obj;
                        if (writeFile) {
                            ToastUtil.toast(FrontActivity.this, "模板上传到主机成功");
                            //这里明确了是前比
                            getAimDirListToAdapter();
                        }
                    } else if (upTemplHostArg == 2) {
                        tipTv.setText("上传主机失败");
                        consoleTipAdapter.addData("上传主机失败");
                    } else if (upTemplHostArg == 3) {
                        tipTv.setText("设备中不存在待上传的模板");
                        consoleTipAdapter.addData("设备中不存在待上传的模板");
                    }
                    break;
                case TG661JFrontAPI.DOWN_TEMPL_DEV:
                    /*
                     * 下载单个模板到设备:
                     * 返回值:downTemplArg
                     * 1:设备下载模板成功
                     * -1:设备下载模板超时
                     * -2:模板错误
                     * -3:设备可容纳模板数已满
                     */
                    int downTemplArg = msg.arg1;
                    if (downTemplArg == 1) {
                        //获取设备中得模板列表，更新界面
                        tg661JFrontAPI.getDevTemplNum(handler, 0);
                        tipTv.setText("设备下载模板成功");
                        consoleTipAdapter.addData("设备下载模板成功");
                    } else if (downTemplArg == -1) {
                        tipTv.setText("设备下载模板超时");
                        consoleTipAdapter.addData("设备下载模板超时");
                    } else if (downTemplArg == -2) {
                        tipTv.setText("模板错误");
                        consoleTipAdapter.addData("模板错误");
                    } else if (downTemplArg == -3) {
                        tipTv.setText("设备可容纳模板数已满");
                        consoleTipAdapter.addData("设备可容纳模板数已满");
                    }
                    break;
                case TG661JFrontAPI.UP_TEMPL_PAC_HOST:
                    /*
                     * 上传模板包到主机:
                     * 返回值:upTemplPacArg
                     * -1:获取模板数量超时
                     * 1:上传模板包到主机成功
                     * 2:设备中不存在模板
                     * 3:设备上传模板包超时
                     * 4:写入主机成功
                     */
                    int upTemplPacArg = msg.arg1;
                    if (upTemplPacArg == -1) {
                        tipTv.setText("获取模板数量超时");
                        consoleTipAdapter.addData("获取模板数量超时");
                    } else if (upTemplPacArg == 1) {
                        tipTv.setText("上传模板包到主机成功");
                        consoleTipAdapter.addData("上传模板包到主机成功");
                        ArrayList<String> hostTemplPacList;
                        if (templType == TG661JFrontAPI.TEMPL_MODEL_3) {
                            String frontHost3TemplPath = tg661JFrontAPI.getFrontHost3TemplPath();
                            hostTemplPacList = tg661JFrontAPI.scanAimDirFileName(frontHost3TemplPath);
                            hostTemplAdapter.clearData();
                            hostTemplAdapter.addData(hostTemplPacList);
                        } else if (templType == TG661JFrontAPI.TEMPL_MODEL_6) {
                            String frontHost6TemplPath = tg661JFrontAPI.getFrontHost6TemplPath();
                            hostTemplPacList = tg661JFrontAPI.scanAimDirFileName(frontHost6TemplPath);
                            hostTemplAdapter.clearData();
                            hostTemplAdapter.addData(hostTemplPacList);
                        }
                    } else if (upTemplPacArg == 2) {
                        tipTv.setText("设备中不存在模板");
                        consoleTipAdapter.addData("设备中不存在模板");
                    } else if (upTemplPacArg == 3) {
                        tipTv.setText("设备上传模板包超时");
                        consoleTipAdapter.addData("设备上传模板包超时");
                    } else if (upTemplPacArg == 4) {
                        tipTv.setText("模板包写入主机成功");
                        consoleTipAdapter.addData("模板包写入主机成功");
                    }
                    break;
                case TG661JFrontAPI.DOWN_TEMPL_PAC_DEV:
                    /*
                     * 下载模板包到设备:
                     * 返回值:downTemplPagArg
                     * 1:设备下载模板包成功
                     * -1:设备下载模板包超时
                     * -2:带下载的模板包错误
                     * -3:设备中可存储的指静脉模板已满
                     */
                    int downTemplPagArg = msg.arg1;
                    if (downTemplPagArg == 1) {
                        devTempls = 0;
                        tg661JFrontAPI.getDevTemplNum(handler, devTempls);
                        tipTv.setText("设备下载模板包成功");
                        consoleTipAdapter.addData("设备下载模板包成功");
                    } else if (downTemplPagArg == -1) {
                        tipTv.setText("设备下载模板包超时");
                        consoleTipAdapter.addData("设备下载模板包超时");
                    } else if (downTemplPagArg == -2) {
                        tipTv.setText("带下载的模板包错误");
                        consoleTipAdapter.addData("带下载的模板包错误");
                    } else if (downTemplPagArg == -3) {
                        tipTv.setText("设备中可存储的指静脉模板已满");
                        consoleTipAdapter.addData("设备中可存储的指静脉模板已满");
                    }
                    break;
                case TG661JFrontAPI.DEV_TEMPL_NUM:
                    /*
                     * 获取设备中模板的数量：
                     * 返回值:devTemplNumArg
                     * -1:获取设备模板数接口超时
                     * >=0:设备已注册的模板/或设备中可注册的最大模板数量
                     */
                    int devTemplNumArg = msg.arg1;
                    if (devTemplNumArg >= 0) {
                        //获取的是设备中模板的类型，0标识设备中已注册的模板，1标识设备中可注的模板
                        devTempls = ((int) msg.obj);
                        if (devTempls == 0) {
                            if (TextUtils.isEmpty(keRegTempNum.getText().toString())) {
                                get6CanDevTemplNum();
//                                devTempls = 1;
//                                TG661JFrontAPI.getDevTemplNum(handler, devTempls);
                            }
                            //设备中模板的数量
                            yiRegTempNum.setText(MessageFormat.format("设备中已注册模板的数量:{0}",
                                    devTemplNumArg));
                            consoleTipAdapter.addData(MessageFormat.format("设备中已注册模板数量:{0}",
                                    devTemplNumArg));
                        } else if (devTempls == 1) {
                            if (TextUtils.isEmpty(yiRegTempNum.getText().toString())) {
                                devTempls = 0;
                                tg661JFrontAPI.getDevTemplNum(handler, devTempls);
                            }
                            //设备中模板的数量
                            keRegTempNum.setText(MessageFormat.format("设备中可注册最大模板数量:{0}",
                                    devTemplNumArg));
                            consoleTipAdapter.addData(MessageFormat.format("设备中可注册最大模板数量:{0}",
                                    devTemplNumArg));
                        }
                    } else {
                        tipTv.setText("获取设备模板数接口超时");
                        consoleTipAdapter.addData("获取设备模板数接口超时");
                    }
                    break;
                case TG661JFrontAPI.DEV_TEMPL_LIST:
                    /*
                     * 获取设备中的模板名称列表：
                     * 返回值:devTemplListArg
                     * -1:调用获取设备端模板信息列表接口超时
                     * 1:获取成功
                     * 2:获取设备端模板信息列表数量为:0
                     */
                    int devTemplListArg = msg.arg1;
                    if (devTemplListArg == 1) {
                        Bundle data = msg.getData();
                        ArrayList<String> templList = data.getStringArrayList(TG661JFrontAPI.TEMP_LIST);
                        if (templList != null && templList.size() > 0) {
                            templAdapter.clearData();
                            templAdapter.addData(templList);
                        }
                    } else if (devTemplListArg == 2) {
                        templAdapter.clearData();
                        tipTv.setText("获取设备端模板信息列表数量为:0");
                        consoleTipAdapter.addData("获取设备端模板信息列表数量为:0");
                    } else if (devTemplListArg == -1) {
                        tipTv.setText("调用获取设备端模板信息列表接口超时");
                        consoleTipAdapter.addData("调用获取设备端模板信息列表接口超时");
                    }
                    break;
                case TG661JFrontAPI.DEV_DEL_ID_TEMPL:
                    /*
                     * 从设备中删除指定ID的模板:
                     * 返回值:delDevIdTemplArg
                     * -1:删除模板超时
                     * 1:模板从设备中删除成功
                     * 2:设备中不存在该模板
                     */
                    int delDevIdTemplArg = msg.arg1;
                    if (delDevIdTemplArg == -1) {
                        tipTv.setText("删除模板超时");
                        consoleTipAdapter.addData("删除模板超时");
                    } else if (delDevIdTemplArg == 1) {
                        templAdapter.removeData(datFileName);
                        //获取设备中得模板列表，更新界面
                        devTempls = 0;
                        tg661JFrontAPI.getDevTemplNum(handler, devTempls);
                        Log.d("===LOG", "  设备删除模板的结果码：" + delDevIdTemplArg);

                        tipTv.setText("模板从设备中删除成功");
                        consoleTipAdapter.addData("模板从设备中删除成功");
                    } else if (delDevIdTemplArg == 2) {
                        tipTv.setText("设备中不存在该模板");
                        consoleTipAdapter.addData("设备中不存在该模板");
                    }
                    break;
                case TG661JFrontAPI.DELETE_HOST_ALL_TEMPL:
                    /*
                     * 删除主机中的所有模板:
                     * 返回值:deleteHostTemplArg
                     * -1:删除失败
                     * 0:删除成功
                     */
                    int deleteHostTemplArg = msg.arg1;
                    if (deleteHostTemplArg == 1) {
                        ArrayList<String> hostFileList = tg661JFrontAPI.getAimFileList();
                        hostTemplAdapter.clearData();
                        hostTemplAdapter.addData(hostFileList);
                        tipTv.setText("删除成功");
                        LogUtils.d("删除成功");
                    } else if (deleteHostTemplArg == -1) {
                        tipTv.setText("删除失败");
                        LogUtils.d("删除失败");
                    }
                    break;
                case TG661JFrontAPI.DELETE_HOST_ID_TEMPL:
                    int deleteHostAllArg = msg.arg1;
                    if (deleteHostAllArg == 1) {
                        hostTemplAdapter.removeData(delHostDatFileName);
                        tipTv.setText("删除成功");
                        tg661JFrontAPI.getAP().play_deleteSuccess();
                    } else if (deleteHostAllArg == -1) {
                        tipTv.setText("删除失败");
                        tg661JFrontAPI.getAP().play_deleteFail();
                    }
                    break;
                case TG661JFrontAPI.DEV_FW:
                    int devFWArg = msg.arg1;
                    if (devFWArg == -1) {
                        tipTv.setText("获取设备固件号失败");
                        consoleTipAdapter.addData("获取设备固件号失败");
                    } else if (devFWArg == 1) {
                        String fw = (String) msg.obj;
                        devFW.setText(String.format("设备固件号:%s", fw));
                        tipTv.setText("获取设备固件号成功");
                        consoleTipAdapter.addData("获取设备固件号成功");
                    }
                    break;
                case TG661JFrontAPI.DEV_SN:
                    int devSNArg = msg.arg1;
                    if (devSNArg == 1) {
                        String sn = (String) msg.obj;
                        devSN.setText(String.format("设备序列号:%s", sn));
                        tipTv.setText("获取设备序列号成功");
                        consoleTipAdapter.addData("获取设备序列号成功");
                    } else if (devSNArg == -1) {
                        tipTv.setText("获取设备序列号失败");
                        consoleTipAdapter.addData("获取设备序列号失败");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        Button closeDevBtn = findViewById(R.id.closeDevBtn);
        Button openDevBtn = findViewById(R.id.openDevBtn);

        dev_StatusTv = findViewById(R.id.devStatus);
        devStatusTv = findViewById(R.id.devStatusTv);
        Button clearAllDevBtn = findViewById(R.id.clearAllDevBtn);
        Button registerBtn = findViewById(R.id.registerBtn);
        Button verifyBtn = findViewById(R.id.verifyBtn);
        Button upTemplPacBtn = findViewById(R.id.upTemplPacBtn);
        Button downTemplPacBtn = findViewById(R.id.downTemplPacBtn);
        Button getDevInfo = findViewById(R.id.getDevInfo);
        Button setDevInfo = findViewById(R.id.setDevInfo);
        Button VerifyBtn1_1 = findViewById(R.id.VerifyBtn1_1);
        RadioGroup RG = findViewById(R.id.RG);
        final RadioButton rb3 = findViewById(R.id.rb3);
        final RadioButton rb6 = findViewById(R.id.rb6);
        Button addVoice = findViewById(R.id.addVoice);
        Button loseVoice = findViewById(R.id.loseVoice);
        Button getSDKVersion = findViewById(R.id.getSDKVersion);
        Button clearAllHostBtn = findViewById(R.id.clearAllHostBtn);
        openContinueVerifyCb = findViewById(R.id.openContinueVerifyCb);
        Button cancelVerifyBtn = findViewById(R.id.cancelVerifyBtn);
        ImageView consoleClearIcon = findViewById(R.id.consoleClearIcon);
        userNameEt = findViewById(R.id.UserNameEt);
        voiceTv = findViewById(R.id.voiceValue);
        keRegTempNum = findViewById(R.id.keRegTempNum);
        yiRegTempNum = findViewById(R.id.yiRegTempNum);
        devFW = findViewById(R.id.devFW);
        devSN = findViewById(R.id.devSN);
        devWorkModel = findViewById(R.id.devWorkModel);
        tipTv = findViewById(R.id.tipTv);
        clearEt = findViewById(R.id.clearEt);
        RecyclerView datFileRv = findViewById(R.id.datFileRv);
        RecyclerView consoleRv = findViewById(R.id.consoleRv);
        RecyclerView hostDatFileRv = findViewById(R.id.hostDatFileRv);

        //默认的初始化音量是4
        voiceTv.setText("4");

        //日志
        consoleRv.setLayoutManager(new LinearLayoutManager(FrontActivity.this,
                OrientationHelper.VERTICAL, false));
        consoleTipAdapter = new ConsoleTipAdapter(FrontActivity.this);
        consoleRv.setAdapter(consoleTipAdapter);
        //设备模板
        datFileRv.setLayoutManager(new LinearLayoutManager(FrontActivity.this,
                OrientationHelper.VERTICAL, false));
        templAdapter = new TemplAdapter(FrontActivity.this);
        templAdapter.setItemClick(this);
        datFileRv.setAdapter(templAdapter);
        //主机设备模板
        hostDatFileRv.setLayoutManager(new LinearLayoutManager(FrontActivity.this,
                OrientationHelper.VERTICAL, false));
        hostTemplAdapter = new HostTemplAdapter(FrontActivity.this);
        hostTemplAdapter.setItemClick(this);
        hostDatFileRv.setAdapter(hostTemplAdapter);

        openDevBtn.setOnClickListener(this);
        closeDevBtn.setOnClickListener(this);

        openContinueVerifyCb.setOnCheckedChangeListener(this);
        clearAllDevBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        verifyBtn.setOnClickListener(this);
        consoleClearIcon.setOnClickListener(this);
        upTemplPacBtn.setOnClickListener(this);
        downTemplPacBtn.setOnClickListener(this);
        addVoice.setOnClickListener(this);
        loseVoice.setOnClickListener(this);
        cancelVerifyBtn.setOnClickListener(this);
        getDevInfo.setOnClickListener(this);
        setDevInfo.setOnClickListener(this);
        VerifyBtn1_1.setOnClickListener(this);
        clearAllHostBtn.setOnClickListener(this);
        getSDKVersion.setOnClickListener(this);
        clearEt.setOnClickListener(this);

        userNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (userNameEt.getText().toString().trim().length() > 0) {
                    clearEt.setVisibility(View.VISIBLE);
                } else {
                    clearEt.setVisibility(View.GONE);
                }
            }
        });

        rb3.setChecked(true);
        RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                devStatus = checkDevStatus();
                if (devStatus) {
                    if (i == rb3.getId()) {
                        devTempls = 0;
                        templType = TG661JFrontAPI.TEMPL_MODEL_3;
                        tg661JFrontAPI.setDevWorkModel(handler, workType, templType);
                        getAimDirListToAdapter();
                    } else if (i == rb6.getId()) {
                        devTempls = 1;
                        templType = TG661JFrontAPI.TEMPL_MODEL_6;
                        tg661JFrontAPI.setDevWorkModel(handler, workType, templType);
                        getAimDirListToAdapter();
                    }
                }
                get6CanDevTemplNum();
            }
        });
        //打开设备
        openDev();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tg661JFrontAPI.isDevOpen()) {
            openContinueVerifyCb.setChecked(false);
            closeDev();
        }
    }

    //获取6特征模板状态下得设备可注册得模板数量
    private void get6CanDevTemplNum() {
        try {
            Thread.sleep(300);
            devTempls = 1;
            tg661JFrontAPI.getDevTemplNum(handler, devTempls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int workType = TG661JFrontAPI.WORK_FRONT;//设备工作模式--》前比

    //开启设备
    private void openDev() {
        devOpen = tg661JFrontAPI.isDevOpen();
        if (!devOpen) {
            //默认为3模板模式
            int templModelType = TG661JFrontAPI.TEMPL_MODEL_3;
            tg661JFrontAPI.openDev(handler, FrontActivity.this, templModelType);
        } else {
            ToastUtil.toast(FrontActivity.this, "设备已经开启");
        }
    }

    //关闭设备
    private void closeDev() {
        devOpen = tg661JFrontAPI.isDevOpen();
        if (devOpen) {
            tg661JFrontAPI.closeDev(handler);
        } else {
            ToastUtil.toast(FrontActivity.this, "设备已经关闭");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openDevBtn:
                openDev();
                break;
            case R.id.closeDevBtn:
                closeDev();
                break;
            case R.id.clearAllDevBtn:
                devStatus = checkDevStatus();
                if (devStatus)
                    tg661JFrontAPI.clearDevTempl(handler);
                break;
            case R.id.registerBtn:
                devStatus = checkDevStatus();
                if (!devStatus) {
                    ToastUtil.toast(FrontActivity.this, "请先开启设备");
                } else {
                    String userN = userNameEt.getText().toString();
                    if (TextUtils.isEmpty(userN)) {
                        consoleTipAdapter.addData("请填写模板注册ID");
                        tipTv.setText(R.string.user_id_tip);
                    } else {
                        //检测注册名只包含字母或数字或中文
                        boolean b = RegularUtil.strContainsNumOrAlpOrChin(userN);
                        if (b) {
                            if (userNameEt.length() > 49) {
                                ToastUtil.toast(FrontActivity.this,
                                        "注册模板的名字太长，请重新填写待注册模板的名字");
                                userNameEt.getText().clear();
                            } else {
                                tg661JFrontAPI.registerDev(handler, userN);
                                tipTv.setText("指静脉注册");
                            }
                        } else {
                            ToastUtil.toast(FrontActivity.this, "注册的模板名称不可包含数字/字母/中文以外的字符");
                        }
                    }
                }
                break;
            case R.id.verifyBtn:
                devStatus = checkDevStatus();
                if (devStatus) {
                    tg661JFrontAPI.devModelVerify(handler);
                    tipTv.setText("1:N验证");
                }
                break;
            case R.id.consoleClearIcon:
                consoleTipAdapter.clearData();
                tipTv.setText("清除日志");
                break;
            case R.id.upTemplPacBtn:
                devStatus = checkDevStatus();
                if (devStatus)
                    AlertDialogUtil.Instance().showActDialog(FrontActivity.this,
                            "确定要从设备上传模板包到主机吗?", this, 3);
                break;
            case R.id.downTemplPacBtn:
                devStatus = checkDevStatus();
                if (devStatus)
                    AlertDialogUtil.Instance().showActDialog(FrontActivity.this,
                            "确定要从主机下载模板包到设备吗?", this, 4);
                break;
            case R.id.cancelVerifyBtn:
                devStatus = checkDevStatus();
                if (devStatus) {
                    tg661JFrontAPI.cancelVerify(handler);
                    openContinueVerifyCb.setChecked(false);
                }
                break;
            case R.id.addVoice:
                devStatus = checkDevStatus();
                if (devStatus)
                    tg661JFrontAPI.setDevVoice(handler, 1);
                break;
            case R.id.loseVoice:
                devStatus = checkDevStatus();
                if (devStatus)
                    tg661JFrontAPI.setDevVoice(handler, 2);
                break;
            case R.id.getDevInfo:
                devStatus = checkDevStatus();
                if (devStatus)
                    tg661JFrontAPI.readDevInfo(handler);
                break;
            case R.id.setDevInfo:
                devStatus = checkDevStatus();
                if (devStatus)
                    AlertDialogUtil.Instance().showGetTipDialog(FrontActivity.this,
                            this, 1);
                break;
            case R.id.VerifyBtn1_1:
                devStatus = checkDevStatus();
                if (devStatus) {
                    String userN = userNameEt.getText().toString().trim();
                    if (TextUtils.isEmpty(userN)) {
                        consoleTipAdapter.addData("请填写模板注册ID");
                        tipTv.setText(R.string.user_id_tip);
                    } else {
                        if (userNameEt.length() > 49) {
                            ToastUtil.toast(FrontActivity.this,
                                    "注册模板的名字太长，请重新填写待注册模板的名字");
                            userNameEt.getText().clear();
                        } else {
                            tg661JFrontAPI.verifyDev1_1(handler, userN);
                            tipTv.setText("1:1验证");
                        }
                    }
                }
                break;
            case R.id.clearAllHostBtn:
                tg661JFrontAPI.deleteHostAllTempl(handler);
                tipTv.setText("清除主机中的模板");
                break;
            case R.id.clearEt:
                userNameEt.getText().clear();
                break;
            case R.id.getSDKVersion:
                //获取SDK版本号
                String sdkVersion = tg661JFrontAPI.getSDKVersion();
                tipTv.setText(MessageFormat.format("SDK版本号：{0}", sdkVersion));
                break;

        }
    }

    private boolean checkDevStatus() {
        devOpen = tg661JFrontAPI.isDevOpen();
        if (!devOpen) {
            ToastUtil.toast(FrontActivity.this, "请先开启设备");
            return false;
        } else {
            return true;
        }
    }

    public void consoleAddData(String tip) {
        tipTv.setText(tip);
        consoleTipAdapter.addData(tip);
    }

    //扫描主机目标文件夹更新adapter得数据
    public void getAimDirListToAdapter() {
        ArrayList<String> hostDatFileNameList = tg661JFrontAPI.scanHostAimDir();
        hostTemplAdapter.clearData();
        hostTemplAdapter.addData(hostDatFileNameList);
    }

    private String datFileName;

    @Override
    public void itemSelectFile(String datFileName) {
        this.datFileName = datFileName;
        AlertDialogUtil.Instance().showActDialog(FrontActivity.this,
                "确定要上传该模板到主机吗?", this, 1);
    }

    @Override
    public void delTempl(String datFileName) {
        if (!TextUtils.isEmpty(datFileName)) {
            tg661JFrontAPI.delIDTemplDev(handler, datFileName, templType);
        }
    }

    @Override
    public void hostItemSelectFile(String datFileName) {
        this.datFileName = datFileName;
        AlertDialogUtil.Instance().showActDialog(FrontActivity.this,
                "确定要下载该模板到设备吗?", this, 2);
    }

    @Override
    public void hostDelTempl(String datFileName) {
        delHostDatFileName = datFileName;
        tg661JFrontAPI.deleteHostIdTempl(handler, datFileName);
    }

    @Override
    public void actListener(int flag) {
        switch (flag) {
            case 1:
                if (TextUtils.isEmpty(datFileName)) {
                    ToastUtil.toast(FrontActivity.this, "所选待上传模板的ID为空，请检查");
                } else {
                    tg661JFrontAPI.upTemplHost(handler, datFileName, templType);
                }
                break;
            case 2:
                if (TextUtils.isEmpty(datFileName)) {
                    ToastUtil.toast(FrontActivity.this, "所选待下载模板的ID为空，请检查");
                } else {
                    tg661JFrontAPI.downTemplDev(handler, datFileName, templType);
                }
                break;
            case 3:
                tg661JFrontAPI.upTemplPacHost(handler, templType);
                break;
            case 4:
                tg661JFrontAPI.downTemplPacDev(handler, templType);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        devOpen = tg661JFrontAPI.isDevOpen();
        if (!devOpen) {
            openContinueVerifyCb.setChecked(false);
            ToastUtil.toast(FrontActivity.this, "请先开启设备");
            return;
        }
        if (b) {
            tg661JFrontAPI.continueVerify(handler, 0);
        } else {
            tg661JFrontAPI.cancelVerify(handler);
        }
    }

    @Override
    public void commiteInfo(String info, int flag) {
        switch (flag) {
            case 1:
                byte[] infoBytes = info.getBytes();
                if (infoBytes.length > 1024) {
                    ToastUtil.toast(FrontActivity.this, "输入的设备信息超出长度");
                    tipTv.setText("输入的设备信息超出长度");
                } else {
                    tg661JFrontAPI.writeDevInfo(handler, infoBytes);
                }
                break;
        }
    }

    @Override
    public void showTip(String tip) {
        ToastUtil.toast(FrontActivity.this, tip);
    }
}
