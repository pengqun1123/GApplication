package com.example.administrator.gapplication;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.TG.library.CallBack.CommitCallBack;
import com.TG.library.api.TG661JAPI;
import com.TG.library.utils.AlertDialogUtil;
import com.TG.library.utils.LogUtils;
import com.TG.library.utils.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CommitCallBack, TemplAdapter.ItemClick {


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TG661JAPI.OPEN_DEV:
                    /**
                     * 返回值：openDevRes
                     * 0:设备打开成功
                     * -1:设备打开失败
                     */
                    int openDevRes = msg.arg1;
                    if (openDevRes >= 0) {
                        LogUtils.d("设备打开成功");
                        tipTv.setText("设备打开成功");
//                        String templPath = TG661JAPI.getTG661JAPI().get6TemplPath();
                        TG661JAPI.getTG661JAPI().getAimFileList();
                    } else if (openDevRes == -1) {
                        LogUtils.d("设备打开失败");
                        tipTv.setText("设备打开失败");
                    }
                    break;
                case TG661JAPI.CLOSE_DEV:
                    /**
                     * 返回值:closeRes:
                     * 2:设备已经关闭
                     * 0:设备关闭成功
                     */
                    int closeRes = msg.arg1;
                    if (closeRes == 2) {
                        LogUtils.d("设备已经关闭");
                        tipTv.setText("设备已经关闭");
                    } else if (closeRes == 0) {
                        LogUtils.d("设备关闭成功");
                        tipTv.setText("设备关闭成功");
                    }
                    break;
                case TG661JAPI.DEV_FW:
                    /**
                     * 返回值：fwArg
                     * -1: 获取设备服务号超时
                     * 0: 设备服务号
                     */
                    int fwArg = msg.arg1;
                    if (fwArg == -1) {
                        LogUtils.d("获取设备服务号超时");
                        tipTv.setText("获取设备服务号超时");
                    } else if (fwArg == 0) {
                        String fw = (String) msg.obj;
                        LogUtils.d("设备服务号:" + fw);
                        tipTv.setText(MessageFormat.format("设备服务号:{0}", fw));
                    }
                    break;
                case TG661JAPI.DEV_SN:
                    /**
                     * 返回值：snArg
                     * -1：获取设备服务号超时(调用接口失败)
                     * 0:获取服务号成功
                     */
                    int snArg = msg.arg1;
                    if (snArg == -1) {
                        LogUtils.d("获取设备序列号超时");
                        tipTv.setText("获取设备序列号超时");
                    } else if (snArg == 0) {
                        String sn = (String) msg.obj;
                        LogUtils.d("设备序列号:" + sn);
                        tipTv.setText("设备序列号:" + sn);
                    }
                    break;
                case TG661JAPI.DEV_VOICE:
                    /**
                     * 返回值：voiceArg
                     * -1:调节音量失败
                     * 1:调节音量成功
                     * 2:设备音量已经是最小值
                     * 3:设备音量已经是最大值
                     */
                    int voiceArg = msg.arg1;
                    if (voiceArg == -1) {
                        LogUtils.d("调节音量失败");
                        tipTv.setText("调节音量失败");
                    } else if (voiceArg == 1) {
                        String voiceValue = (String) msg.obj;
                        tipTv.setText(voiceValue);
                        LogUtils.d("调节音量成功");
                        tipTv.setText("调节音量成功");
                        voiceTt.setText(voiceValue);
                    } else if (voiceArg == 2) {
                        LogUtils.d("设备音量已经是最小值");
                        tipTv.setText("设备音量已经是最小值");
                    } else if (voiceArg == 3) {
                        LogUtils.d("设备音量已经是最大值");
                        tipTv.setText("设备音量已经是最大值");
                    }
                    break;
                case TG661JAPI.DEV_REGISTER:
                    /**
                     * 返回值：
                     * -1:调用注册接口超时
                     * 1:模板名字过长，超过49字节，请重写命名模板
                     * 2:注册成功
                     */
                    int registerArg = msg.arg1;
                    if (registerArg == -1) {
                        LogUtils.d("调用注册接口超时");
                        tipTv.setText("调用注册接口超时");
                    } else if (registerArg == 1) {
                        LogUtils.d("模板名字过长");
                        tipTv.setText("模板名字过长");
                    } else if (registerArg == 2) {
                        LogUtils.d("注册成功");
                        tipTv.setText("注册成功");
                    }
                    break;
                case TG661JAPI.WRITE_DEV_INFO:
                    /**
                     * 返回值：writeArg
                     *   * >0：写入成功,实际写入长度
                     *      * -1：超时
                     *      * -2：入参错误
                     *      -3：信息字节长度过长（应该小于1024K）
                     */
                    int writeArg = msg.arg1;
                    if (writeArg >= 0) {
                        LogUtils.d("写入成功");
                        tipTv.setText("写入成功");
                    } else if (writeArg == -1) {
                        LogUtils.d("超时");
                        tipTv.setText("超时");
                    } else if (writeArg == -2) {
                        LogUtils.d("入参错误");
                        tipTv.setText("入参错误");
                    } else if (writeArg == -3) {
                        LogUtils.d("输入的设备信息超出长度");
                        tipTv.setText("输入的设备信息超出长度");
                    }
                    break;
                case TG661JAPI.READ_DEV_INFO:
                    /**
                     * 返回值readArg:
                     *  >=0：成功,实际读取长度
                     *  -1：超时
                     *  -2：入参错误
                     */
                    int readArg = msg.arg1;
                    if (readArg >= 0) {
                        LogUtils.d("成功");
                        tipTv.setText("成功");
                    } else if (readArg == -1) {
                        LogUtils.d("超时");
                        tipTv.setText("超时");
                    } else if (readArg == -2) {
                        LogUtils.d("入参错误");
                        tipTv.setText("入参错误");
                    }
                    break;
                case TG661JAPI.CANCEL_VERIFY:
                    /**
                     *    * Return：
                     *      * 0：请求成功
                     *      * else：请求失败
                     */
                    int cancelArg = msg.arg1;
                    if (cancelArg == 0) {
                        LogUtils.d("请求成功");
                        tipTv.setText("请求成功");
                    } else {
                        LogUtils.d("请求失败");
                        tipTv.setText("请求失败");
                    }
                    break;
                case TG661JAPI.DEV_WORK_MODEL:
                    /**
                     * 返回值devModelArg:
                     * -1:获取设备的工作模式接口超时
                     * 0:前比3模板
                     * 1:后比
                     * 2:前比6模板
                     */
                    int devModelArg = msg.arg1;
                    if (devModelArg == -1) {
                        LogUtils.d("获取设备的工作模式接口超时");
                        tipTv.setText("获取设备的工作模式接口超时");
                    } else if (devModelArg == 0) {
                        LogUtils.d("前比3模板");
                        tipTv.setText("前比3模板");
                    } else if (devModelArg == 1) {
                        LogUtils.d("后比");
                        tipTv.setText("后比");
                    } else if (devModelArg == 2) {
                        LogUtils.d("前比6模板");
                        tipTv.setText("前比6模板");
                    }
                    break;
                case TG661JAPI.DEV_TEMPL_NUM:
                    /**
                     * 返回值devTemplNum：
                     * （1）>=0：模板数
                     *      * （2）-1:超时
                     */
                    if (templType == 0) {
                        //设备中已注册的最大模板数
                        int devTemplNum = msg.arg1;
                        if (devTemplNum >= 0) {
                            LogUtils.d("设备中已注册的最大模板数:" + devTemplNum);
                            tipTv.setText("设备中已注册的最大模板数:" + devTemplNum);
                        } else if (devTemplNum == -1) {
                            LogUtils.d("超时");
                            tipTv.setText("超时");
                        }
                    } else if (templType == 1) {
                        //设备中可以注册的最大模板数
                        int devTemplNum = msg.arg1;
                        if (devTemplNum >= 0) {
                            LogUtils.d("设备中可以注册的最大模板数:" + devTemplNum);
                            tipTv.setText("设备中可以注册的最大模板数:" + devTemplNum);
                        } else if (devTemplNum == -1) {
                            LogUtils.d("超时");
                            tipTv.setText("超时");
                        }
                    }
                    break;
                case TG661JAPI.DEV_TEMPL_LIST:
                    /**
                     * 返回值devTemlpListArg:
                     * -1：调用获取设备端模板信息列表接口超时
                     * 1：获取成功
                     */
                    int devTemlpListArg = msg.arg1;
                    if (devTemlpListArg == -1) {
                        LogUtils.d("调用获取设备端模板信息列表接口超时");
                        tipTv.setText("调用获取设备端模板信息列表接口超时");
                    } else if (devTemlpListArg == 1) {
                        ArrayList<String> devTemlpList = (ArrayList<String>) msg.obj;
                        if (devTemlpList.size() > 0) {
                            for (String s : devTemlpList) {
                                LogUtils.d("设备中的模板名称:" + s);
                            }
                        }
                    }
                    break;
                case TG661JAPI.DEV_DEL_ID_TEMPL:
                    /**
                     * （1）0：删除成功
                     * （2）1：设备中不存在待删除的 ID
                     * （3）-1：超时
                     */
                    int delTemplArg = msg.arg1;
                    if (delTemplArg == 0) {
                        LogUtils.d("删除成功");
                        tipTv.setText("删除成功");
                    } else if (delTemplArg == 1) {
                        LogUtils.d("设备中不存在待删除的 ID");
                        tipTv.setText("设备中不存在待删除的 ID");
                    } else if (delTemplArg == -1) {
                        LogUtils.d("超时");
                        tipTv.setText("超时");
                    }
                    break;
                case TG661JAPI.DEV_IMG_REGISTER:
                    /**
                     * 后比注册
                     * 返回值 imgArg
                     * 0:设备获取图像成功，特征提取成功，特征融合成功，模板存储成功-->登记成功
                     * 1:特征融合失败，因"特征"数据一致性差，Output数据无效
                     * 2:特征融合失败，因参数不合法,Output数据无效
                     * 3:特征提取失败,因证书路径错误,Output数据无效
                     * 4:特征提取失败,因证书内容无效,Output数据无效
                     * 5:特征提取失败,因证书内容过期,Output数据无效
                     * 6:特征提取失败,因"图像"数据无效,Output数据无效
                     * 7:特征提取失败,因"图像"质量较差,Output数据无效
                     * 8:特征提取失败,因参数不合法,Output数据无效
                     * -1:抓图超时
                     * -2:设备断开
                     * -3:操作取消
                     * -4:入参错误
                     */
                    int imgArg = msg.arg1;
                    if (imgArg == 0) {
//                        byte[] imgData = (byte[]) msg.obj;
//                        String templPath = TG661JAPI.getTG661JAPI().get6TemplPath();
                        TG661JAPI.getTG661JAPI().getAimFileList();
//                        Glide.with(MainActivity.this).load(obj).into(iv);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(obj, 0, obj.length);
                        LogUtils.d("设备获取图像成功");
                        tipTv.setText("设备获取图像成功");
                    } else if (imgArg == -1) {
                        LogUtils.d("抓图超时");
                        tipTv.setText("抓图超时");
                    } else if (imgArg == -2) {
                        LogUtils.d("设备断开");
                        tipTv.setText("设备断开");
                    } else if (imgArg == -3) {
                        LogUtils.d("操作取消");
                        tipTv.setText("操作取消");
                    } else if (imgArg == -4) {
                        LogUtils.d("入参错误");
                        tipTv.setText("入参错误");
                    }
                    break;
                case TG661JAPI.CANCEL_DEV_IMG:
                    /**
                     * 返回值 cancelImgArg
                     * 0:
                     * -1:
                     */
                    int cancelImgArg = msg.arg1;
                    if (cancelImgArg == 0) {
                        LogUtils.d("取消抓取图像成功");
                        tipTv.setText("取消抓取图像成功");
                    } else if (cancelImgArg == -1) {
                        LogUtils.d("取消抓取图像失败");
                        tipTv.setText("取消抓取图像失败");
                    }
                    break;
                case TG661JAPI.INIT_FV:
                    /**
                     *  （1）0：初始化成功,算法接口有效
                     *  （2）1: 初始化失败,因证书路径错误,算法接口无效
                     *  （3）2: 初始化失败,因证书内容无效,算法接口无效
                     *  （4）3: 初始化失败,因证书内容过期,算法接口无效
                     */
                    int initFvArg = msg.arg1;
                    if (initFvArg == 0) {
                        LogUtils.d("初始化成功,算法接口有效");
                        tipTv.setText("初始化成功,算法接口有效");
                    } else if (initFvArg == 1) {
                        LogUtils.d("初始化失败,因证书路径错误,算法接口无效");
                        tipTv.setText("初始化失败,因证书路径错误,算法接口无效");
                    } else if (initFvArg == 2) {
                        LogUtils.d("初始化失败,因证书内容无效,算法接口无效");
                        tipTv.setText("初始化失败,因证书内容无效,算法接口无效");
                    } else if (initFvArg == 3) {
                        LogUtils.d("初始化失败,因证书内容过期,算法接口无效");
                        tipTv.setText("初始化失败,因证书内容过期,算法接口无效");
                    }
                    break;
                case TG661JAPI.EXTRACT_FEATURE_REGISTER:
                    /**
                     * 返回值
                     *  * （1） 0：特征提取成功,Output数据有效
                     *      * （2） 1: 特征提取失败,因证书路径错误,Output数据无效
                     *      * （3） 2: 特征提取失败,因证书内容无效,Output数据无效
                     *      * （4） 3: 特征提取失败,因证书内容过期,Output数据无效
                     *      * （5） 4：特征提取失败,因"图像"数据无效,Output数据无效
                     *      * （6） 5：特征提取失败,因"图像"质量较差,Output数据无效
                     *      * （7）-1: 特征提取失败,因参数不合法,Output数据无效
                     */

                    break;
                case TG661JAPI.EXTRACT_FEATURE_VERIFY:

                    break;
                case TG661JAPI.GET_AIM_FILE_LIST:
                    //扫描获取目标文件列表
                    ArrayList<String> aimFileList = (ArrayList<String>) msg.obj;
                    templAdapter.clearData();
                    templAdapter.addData(aimFileList);
                    break;
                case TG661JAPI.FEATURE_COMPARE1_1:
                    /**
                     *
                     */
                    int match1Arg1 = msg.arg1;
                    if (match1Arg1 == 0) {
                        Bundle data = msg.getData();
                        byte[] updateTemplData = data.getByteArray(TG661JAPI.COMPARE_N_TEMPL);//可更新的模板
//                        int templIndex = data.getInt(TG661JAPI.COMPARE_N_INDEX);
                        String templName = data.getString(TG661JAPI.COMPARE_NAME);
                        int templScore = data.getInt(TG661JAPI.COMPARE_N_SCORE);
                        if (autoUpdateStatus) {
                            TG661JAPI.getTG661JAPI().updateHostTempl(updateTemplData,
                                    /*, templIndex*/handler,templName);
                        }
                        tipTv.setText(MessageFormat.format("验证成功,验证分数：{0}", templScore));
                        LogUtils.d("验证成功");
                    }
                    break;
                case TG661JAPI.FEATURE_COMPARE1_N:
                    int matchNArg = msg.arg1;
                    if (matchNArg == 0) {
                        Bundle data = msg.getData();
                        byte[] updateTemplData = data.getByteArray(TG661JAPI.COMPARE_N_TEMPL);//可更新的模板
//                        int templIndex = data.getInt(TG661JAPI.COMPARE_N_INDEX);
                        String templName = data.getString(TG661JAPI.COMPARE_NAME);
                        int templScore = data.getInt(TG661JAPI.COMPARE_N_SCORE);
                        if (autoUpdateStatus) {
                            TG661JAPI.getTG661JAPI().updateHostTempl(updateTemplData,
                                    /*, templIndex*/handler,templName);
                        }
                        tipTv.setText(MessageFormat.format("验证成功,验证分数：{0}", templScore));
                        LogUtils.d("验证成功");
                    }
                    break;
                case TG661JAPI.DELETE_HOST_ALL_TEMPL:
                    int deleteHostIDArg = msg.arg1;
                    if (deleteHostIDArg == 0) {
//                        String templPath = TG661JAPI.getTG661JAPI().get6TemplPath();
                        TG661JAPI.getTG661JAPI().getAimFileList();
                        tipTv.setText("删除成功");
                        LogUtils.d("删除成功");
                    } else if (deleteHostIDArg == -1) {
                        tipTv.setText("删除失败");
                        LogUtils.d("删除失败");
                    }
                    break;
                case TG661JAPI.DELETE_HOST_ID_TEMPL:
                    int deleteHostAllArg = msg.arg1;
                    if (deleteHostAllArg == 0) {
//                        String templPath = TG661JAPI.getTG661JAPI().get6TemplPath();
                        TG661JAPI.getTG661JAPI().getAimFileList();
                        tipTv.setText("删除成功");
                        LogUtils.d("删除成功");
                    } else if (deleteHostAllArg == -1) {
                        tipTv.setText("删除失败");
                        LogUtils.d("删除失败");
                    }
                    break;
                case TG661JAPI.UPDATE_HOST_TEMPL:
                    //更新主机模板的结果
                    boolean updateStatus = (boolean) msg.obj;
                    if (updateStatus) {
                        tipTv.setText("模板更新成功");
                        LogUtils.d("模板更新成功");
                    } else {
                        tipTv.setText("模板更新失败");
                        LogUtils.d("模板更新失败");
                    }
                    break;
                case TG661JAPI.TEMPL_SN:
                    int templSnArg = msg.arg1;
                    if (templSnArg == 0) {
                        byte[] snData = (byte[]) msg.obj;
                        try {
                            String snStr = new String(snData, "UTF-8");
                            tipTv.setText(snStr);
                            LogUtils.d(" 模板的序列号 :" + snStr);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else if (templSnArg == -1) {
                        tipTv.setText("获取失败，参数错误");
                        LogUtils.d("获取失败，参数错误");
                    } else if (templSnArg == -2) {
                        tipTv.setText("模板名称为空，请检查");
                        LogUtils.d("模板名称为空，请检查");
                    }
                    break;
                case TG661JAPI.TEMPL_TIME:
                    int templTimeArg = msg.arg1;
                    if (templTimeArg == 0) {
                        byte[] timeData = (byte[]) msg.obj;
                        try {
                            String timeStr = new String(timeData, "UTF-8");
                            tipTv.setText(timeStr);
                            LogUtils.d(" 模板的序列号 :" + timeStr);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else if (templTimeArg == -1) {
                        tipTv.setText("获取失败，参数错误");
                        LogUtils.d("获取失败，参数错误");
                    } else if (templTimeArg == -2) {
                        tipTv.setText("模板名称为空，请检查");
                        LogUtils.d("模板名称为空，请检查");
                    }
                    break;
                case TG661JAPI.TEMPL_FV_VERSION:
                    int templVersionArg = msg.arg1;
                    if (templVersionArg == 0) {
                        byte[] versionData = (byte[]) msg.obj;
                        try {
                            String versionStr = new String(versionData, "UTF-8");
                            tipTv.setText(versionStr);
                            LogUtils.d(" 模板的序列号 :" + versionStr);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else if (templVersionArg == -1) {
                        tipTv.setText("获取失败，参数错误");
                        LogUtils.d("获取失败，参数错误");
                    } else if (templVersionArg == -2) {
                        tipTv.setText("模板名称为空，请检查");
                        LogUtils.d("模板名称为空，请检查");
                    }
                    break;
            }
        }
    };
    private TextView tipTv, voiceTt;
    private ImageView iv;
    private EditText templID;
    private boolean devOpen;
    private EditText templIDBehind;
    private int templModelType = TG661JAPI.TEMPL_MODEL_6;//默认为6模板模式
    private TemplAdapter templAdapter;
    private boolean autoUpdateStatus = false;//自动更新模板
    private boolean continueVerifyStatus = false;//连续验证模板
    private TextView volumeTt;
    private String datFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tipTv = findViewById(R.id.tipTv);
        iv = findViewById(R.id.iv);
        Button openBtn = findViewById(R.id.openBtn);
        Button closeBtn = findViewById(R.id.closeBtn);
        Button fwBtn = findViewById(R.id.fwBtn);
        Button snBtn = findViewById(R.id.snBtn);
        Button devStatusBtn = findViewById(R.id.devStatusBtn);
        Button clearTmplBtn = findViewById(R.id.clearTmplBtn);
        Button voiceAddBtn = findViewById(R.id.voiceAddBtn);
        Button voiceLoseBtn = findViewById(R.id.voiceLoseBtn);
        voiceTt = findViewById(R.id.voiceTt);
        templID = findViewById(R.id.templID);
        Button registerBtn = findViewById(R.id.registerBtn);
        Button verify1_1Btn = findViewById(R.id.verify1_1Btn);
        Button verify1_NBtn = findViewById(R.id.verify1_NBtn);
        CheckBox continueVerify = findViewById(R.id.continueVerify);
        Button setDevInfoBtn = findViewById(R.id.setDevInfoBtn);
        Button getDevInfoBtn = findViewById(R.id.getDevInfoBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);
        Button upTmpBtn = findViewById(R.id.upTmpBtn);
        Button upTmpPgBtn = findViewById(R.id.upTmpPgBtn);
        Button downTmpBtn = findViewById(R.id.downTmpBtn);
        Button downTmpPgBtn = findViewById(R.id.downTmpPgBtn);
        Button delDevTmpBtn = findViewById(R.id.delDevTmpBtn);
        Button delHostTmpBtn = findViewById(R.id.delHostTmpBtn);
        Button getHostAllTmpBtn = findViewById(R.id.getHostAllTmpBtn);
        Button getDevSaveTmpNumBtn = findViewById(R.id.getDevSaveTmpNumBtn);
        Button devSavedTmpNumBtn = findViewById(R.id.devSavedTmpNumBtn);
        Button getDevSaveTmpListBtn = findViewById(R.id.getDevSaveTmpListBtn);
        Button getDevWorkModel = findViewById(R.id.getDevWorkModel);

        //后比
        templIDBehind = findViewById(R.id.templIDBehind);
        Button registerBtnBehind = findViewById(R.id.registerBtnBehind);
        RadioGroup templSumModel = findViewById(R.id.templSumModel);
        final RadioButton templ3Rb = findViewById(R.id.templ3Rb);
        final RadioButton templ6Rb = findViewById(R.id.templ6Rb);
        Button ver1_1Btn = findViewById(R.id.ver1_1Btn);
        Button ver1_NBtn = findViewById(R.id.ver1_NBtn);
        Button getTemplFW = findViewById(R.id.getTemplFW);
        Button getTemplSN = findViewById(R.id.getTemplSN);
        Button templTimeBtn = findViewById(R.id.templTimeBtn);
        Button voiceIncreaceBtn = findViewById(R.id.voiceIncreaceBtn);
        volumeTt = findViewById(R.id.volumeTt);
        Button voiceDecreaceBtn = findViewById(R.id.voiceDecreaceBtn);
        Button getTemplAlgorVersionBtn = findViewById(R.id.getTemplAlgorVersionBtn);
        CheckBox autoUpdateTempl = findViewById(R.id.autoUpdateTempl);
        RecyclerView templFileRv = findViewById(R.id.templFileRv);

        templFileRv.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                OrientationHelper.VERTICAL, false));
        templAdapter = new TemplAdapter(MainActivity.this);
        templAdapter.setItemClick(this);
        templFileRv.setAdapter(templAdapter);


        TG661JAPI.getTG661JAPI().init(MainActivity.this);
        //默认的初始化音量是4
        voiceTt.setText("4");


        //获取当前音量
        String currentVolume = TG661JAPI.getTG661JAPI().getCurrentVolume(handler);
        volumeTt.setText(currentVolume);

        openBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        fwBtn.setOnClickListener(this);
        snBtn.setOnClickListener(this);
        devStatusBtn.setOnClickListener(this);
        clearTmplBtn.setOnClickListener(this);
        voiceAddBtn.setOnClickListener(this);
        voiceLoseBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        verify1_1Btn.setOnClickListener(this);
        verify1_NBtn.setOnClickListener(this);
        setDevInfoBtn.setOnClickListener(this);
        getDevInfoBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        upTmpBtn.setOnClickListener(this);
        upTmpPgBtn.setOnClickListener(this);
        downTmpBtn.setOnClickListener(this);
        downTmpPgBtn.setOnClickListener(this);
        delDevTmpBtn.setOnClickListener(this);
        delHostTmpBtn.setOnClickListener(this);
        getHostAllTmpBtn.setOnClickListener(this);
        getDevSaveTmpNumBtn.setOnClickListener(this);
        devSavedTmpNumBtn.setOnClickListener(this);
        getDevSaveTmpListBtn.setOnClickListener(this);
        getDevWorkModel.setOnClickListener(this);

        registerBtnBehind.setOnClickListener(this);
        ver1_1Btn.setOnClickListener(this);
        ver1_NBtn.setOnClickListener(this);
        getTemplAlgorVersionBtn.setOnClickListener(this);
        getTemplFW.setOnClickListener(this);
        getTemplSN.setOnClickListener(this);
        templTimeBtn.setOnClickListener(this);
        voiceIncreaceBtn.setOnClickListener(this);
        voiceDecreaceBtn.setOnClickListener(this);

        continueVerifyStatus = continueVerify.isChecked();
        //设备连续验证
        continueVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                continueVerifyStatus = b;
            }
        });
        //3特征模板和6特征模板切换
        templSumModel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == templ3Rb.getId()) {
                    templModelType = TG661JAPI.TEMPL_MODEL_3;
                    TG661JAPI.getTG661JAPI().setTemplModelType(TG661JAPI.TEMPL_MODEL_3);
                } else if (i == templ6Rb.getId()) {
                    templModelType = TG661JAPI.TEMPL_MODEL_6;
                    TG661JAPI.getTG661JAPI().setTemplModelType(TG661JAPI.TEMPL_MODEL_6);
                }
                TG661JAPI.getTG661JAPI().getAimFileList();
            }
        });
        autoUpdateStatus = autoUpdateTempl.isChecked();
        //自动更新
        autoUpdateTempl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                autoUpdateStatus = b;
            }
        });
    }

    private int templType = -1;
    private int wokeType = TG661JAPI.WORK_BEHIND;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openBtn:
//                TG661JAPI.getTG661JAPI().openDev(handler, /*MainActivity.this,*/
//                        MainActivity.this, wokeType);
                break;
            case R.id.closeBtn:
                TG661JAPI.getTG661JAPI().closeDev(handler);
                break;
            case R.id.fwBtn:
                TG661JAPI.getTG661JAPI().getDevFW(handler);
                break;
            case R.id.snBtn:
                TG661JAPI.getTG661JAPI().getDevSN(handler);
                break;
            case R.id.devStatusBtn:
                TG661JAPI.getTG661JAPI().getDevStatus(handler);
                break;
            case R.id.clearTmplBtn:
                TG661JAPI.getTG661JAPI().clearDevTempl(handler);
                break;
            case R.id.voiceAddBtn:
                TG661JAPI.getTG661JAPI().setDevVoice(handler, 1);
                break;
            case R.id.voiceLoseBtn:
                TG661JAPI.getTG661JAPI().setDevVoice(handler, 2);
                break;
            case R.id.registerBtn:
                devOpen = TG661JAPI.getTG661JAPI().isDevOpen();
                if (devOpen) {
                    String templId = templID.getText().toString().trim();
                    if (TextUtils.isEmpty(templId)) {
                        ToastUtil.toast(MainActivity.this, "请填写模板名称");
                    } else {
                        TG661JAPI.getTG661JAPI().registerDev(handler, templId);
                    }
                } else {
                    ToastUtil.toast(MainActivity.this, "请开启设备");
                }
                break;
            case R.id.verify1_1Btn:
                String templId = templID.getText().toString().trim();
                if (TextUtils.isEmpty(templId)) {
                    ToastUtil.toast(MainActivity.this, "请填写模板名称");
                } else {
                    TG661JAPI.getTG661JAPI().verifyDev1_1(handler, templId);
                }
                break;
            case R.id.verify1_NBtn:
                TG661JAPI.getTG661JAPI().devModelVerify(handler);
                break;
            case R.id.setDevInfoBtn:
                devOpen = TG661JAPI.getTG661JAPI().isDevOpen();
                if (!devOpen) {
                    ToastUtil.toast(MainActivity.this, "请先开启设备");
                    return;
                }
                AlertDialogUtil.Instance().showGetTipDialog(MainActivity.this,
                        this, 1);
                break;
            case R.id.getDevInfoBtn:
                TG661JAPI.getTG661JAPI().readDevInfo(handler);
                break;
            case R.id.cancelBtn:
                TG661JAPI.getTG661JAPI().cancelVerify(handler);
                break;
            case R.id.upTmpBtn:
                datFileName = templID.getText().toString().trim();
                if (TextUtils.isEmpty(datFileName)) {
                    ToastUtil.toast(MainActivity.this, "所选待上传模板的ID为空，请检查");
                } else {
                    LogUtils.d("====  文件的ID：" + datFileName);
//                    templId = datFileName.substring(0, datFileName.indexOf(".dat"));
                    TG661JAPI.getTG661JAPI().upTemplHost(handler, datFileName, templModelType);
                }
                break;
            case R.id.upTmpPgBtn:
                datFileName = templID.getText().toString().trim();
                if (TextUtils.isEmpty(datFileName)) {
                    ToastUtil.toast(MainActivity.this, "所选待上传模板的ID为空，请检查");
                } else {
                    LogUtils.d("====  文件的ID：" + datFileName);
//                    templId = datFileName.substring(0, datFileName.indexOf(".dat"));
                    TG661JAPI.getTG661JAPI().upTemplPacHost(handler, templModelType);
                }
                break;
            case R.id.downTmpBtn:
                datFileName = templID.getText().toString().trim();
                if (TextUtils.isEmpty(datFileName)) {
                    ToastUtil.toast(MainActivity.this, "所选待上传模板的ID为空，请检查");
                } else {
//                    TG661JAPI.getTG661JAPI().downTemplDev(handler, datFileName.getBytes(), templModelType);
                }
                break;
            case R.id.downTmpPgBtn:
                datFileName = templID.getText().toString().trim();
                if (TextUtils.isEmpty(datFileName)) {
                    ToastUtil.toast(MainActivity.this, "所选待上传模板的ID为空，请检查");
                } else {
                    LogUtils.d("====  文件的ID：" + datFileName);
                    TG661JAPI.getTG661JAPI().downTemplPacDev(handler, templModelType);
                }
                break;
            case R.id.delDevTmpBtn:
                String templ_ID = templID.getText().toString().trim();
                if (TextUtils.isEmpty(templ_ID)) {
                    ToastUtil.toast(MainActivity.this, "请填写模板名称");
                } else {
                    TG661JAPI.getTG661JAPI().delIDTemplDev(handler, templ_ID, templModelType);
                }
                break;
            case R.id.delHostTmpBtn:

                break;
            case R.id.getHostAllTmpBtn:

                break;
            case R.id.getDevSaveTmpNumBtn:
                templType = 1;
                TG661JAPI.getTG661JAPI().getDevTemplNum(handler, templType);
                break;
            case R.id.devSavedTmpNumBtn:
                /**
                 * 0：设备中已注册的模板数；1：设备中可注册的最大模板数
                 */
                templType = 0;
                TG661JAPI.getTG661JAPI().getDevTemplNum(handler, templType);
                break;
            case R.id.getDevSaveTmpListBtn:
                TG661JAPI.getTG661JAPI().getDevTemplList(handler);
                break;
            case R.id.getDevWorkModel:
                TG661JAPI.getTG661JAPI().getDevWorkModel(handler);
                break;
            ////后比
            case R.id.registerBtnBehind:
                //注册
                String templID = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(templID)) {
                    ToastUtil.toast(MainActivity.this, "注册的模板ID不能为空");
                } else {
                    TG661JAPI.getTG661JAPI().extractFeatureRegister(handler, templModelType, templID);
                }
                break;
            case R.id.ver1_1Btn:
                //1:1验证
                String templName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(templName)) {
                    ToastUtil.toast(MainActivity.this, "请选择要比对的模板文件");
                } else {
                    TG661JAPI.getTG661JAPI().featureCompare1_1(handler, templName);
                }
                break;
            case R.id.ver1_NBtn:
                //1:N验证
                TG661JAPI.getTG661JAPI().featureCompare1_N(handler);
                break;
            case R.id.getTemplSN:
                String snTemplName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(snTemplName)) {
                    ToastUtil.toast(MainActivity.this, "模板名字不能为空");
                } else {
                    TG661JAPI.getTG661JAPI().getTemplSN(handler, snTemplName);
                }
                break;
            case R.id.getTemplFW:
                String fwTemplName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(fwTemplName)) {
                    ToastUtil.toast(MainActivity.this, "模板名字不能为空");
                } else {
                    TG661JAPI.getTG661JAPI().getTemplFW(handler, fwTemplName);
                }
                break;
            case R.id.templTimeBtn:
                String timeTemplName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(timeTemplName)) {
                    ToastUtil.toast(MainActivity.this, "模板名字不能为空");
                } else {
                    TG661JAPI.getTG661JAPI().getTemplTime(handler, timeTemplName);
                }
                break;
            case R.id.getTemplAlgorVersionBtn:
                String fvVersionTemplName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(fvVersionTemplName)) {
                    ToastUtil.toast(MainActivity.this, "模板名字不能为空");
                } else {
                    TG661JAPI.getTG661JAPI().getTemplVersion(handler, fvVersionTemplName);
                }
                break;
            case R.id.voiceIncreaceBtn:
                //音量加
                boolean increaseVolume = TG661JAPI.getTG661JAPI().increaseVolume(handler);
                if (increaseVolume) {
                    String currentVolume = TG661JAPI.getTG661JAPI().getCurrentVolume(handler);
                    volumeTt.setText(currentVolume);
                    tipTv.setText("音量增大成功");
                    LogUtils.d("音量增大成功");
                } else {
                    tipTv.setText("已经是最大音量");
                    LogUtils.d("已经是最大音量");
                }
                break;
            case R.id.voiceDecreaceBtn:
                //音量减
                boolean descreaseVolume = TG661JAPI.getTG661JAPI().descreaseVolume(handler);
                if (descreaseVolume) {
                    String currentVolume = TG661JAPI.getTG661JAPI().getCurrentVolume(handler);
                    volumeTt.setText(currentVolume);
                    tipTv.setText("音量减小成功");
                    LogUtils.d("音量减小成功");
                } else {
                    tipTv.setText("已经是最小音量");
                    LogUtils.d("已经是最小音量");
                }
                break;


        }
    }

    @Override
    public void commiteInfo(String info, int flag) {
        switch (flag) {
            case 1:
                TG661JAPI.getTG661JAPI().writeDevInfo(handler, info.getBytes());
                break;
        }
    }

    @Override
    public void showTip(String tip) {

    }

    @Override
    public void itemSelectFile(String datFileName) {
        templIDBehind.setText(datFileName);
    }

    @Override
    public void delTempl(String datFileName) {
        TG661JAPI.getTG661JAPI().deleteHostIdTempl(handler,datFileName);
//        templIDBehind.setText(datFileName);
    }
}
