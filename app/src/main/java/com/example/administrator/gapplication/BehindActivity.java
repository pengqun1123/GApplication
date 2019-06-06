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
import android.view.LayoutInflater;
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
import com.TG.library.api.TG661JBAPI;
import com.TG.library.api.TG661JBehindAPI;
import com.TG.library.utils.AlertDialogUtil;
import com.TG.library.utils.AudioProvider;
import com.TG.library.utils.RegularUtil;
import com.TG.library.utils.ToastUtil;
import com.bumptech.glide.Glide;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 后比Activity
 */
public class BehindActivity extends AppCompatActivity implements View.OnClickListener,
        CommitCallBack, TemplAdapter.ItemClick {

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
    private ImageView clearEt;
    private boolean devStatu;

    private AlertDialog waitDialog;
    private EditText templIDBehind, userFingerName;
    private TextView volumeTt, tipTv, devStatus, devModelTv;
    private byte[] imgData;
    private List<byte[]> imgDatas = new ArrayList<>();

    private int templModelType = TG661JBAPI.TEMPL_MODEL_6;//默认为6模板模式
    private boolean autoUpdateStatus = false;//自动更新模板
    private ImageView iv;

    public void setDevStatus(String status) {
        devStatus.setText(status);
        tipTv.setText(status);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TG661JBAPI.DEV_STATUS:
                    /*
                     * 设备状态：
                     *  1：设备状态：已连接
                     *  -1：已断开,连接中...
                     */
                    int devStatusArg = msg.arg1;
                    if (devStatusArg >= 0) {
                        if (tipTv.getText().toString().contains("断开")) {
                            tipTv.setText("设备状态：已连接");
                        }
                        devStatus.setText("设备状态:已连接");
                    } else if (devStatusArg == -1) {
                        tipTv.setText("设备状态：未连接");
                        devStatus.setText("设备状态:未连接");
                        registerBtnBehind.setText("注册");
                        isGetImg = false;
                    } else if (devStatusArg == -2) {
                        tipTv.setText("设备状态：未连接/已断开");
                        devStatus.setText("设备状态:已断开,重新连接中...");
                        registerBtnBehind.setText("注册");
                        isGetImg = false;
                    }
                    registerBtnBehind.setClickable(true);
                    break;
//                case TG661JBAPI.DEV_IMG:
//                    /*
//                     * 后比注册
//                     * 返回值 imgArg
//                     * 1:设备获取图像成功，特征提取成功，特征融合成功，模板存储成功-->登记成功
//                     * 2:特征融合失败，因"特征"数据一致性差，Output数据无效
//                     * 3:特征融合失败，因参数不合法,Output数据无效
//                     * 4:特征提取失败,因证书路径错误,Output数据无效
//                     * 5:特征提取失败,因证书内容无效,Output数据无效
//                     * 6:特征提取失败,因证书内容过期,Output数据无效
//                     * 7:特征提取失败,因"图像"数据无效,Output数据无效
//                     * 8:特征提取失败,因"图像"质量较差,Output数据无效
//                     * 9:特征提取失败,因参数不合法,Output数据无效
//                     * -1:抓图超时
//                     * -2:设备断开
//                     * -3:操作取消
//                     * -4:入参错误
//                     * -5:该指静脉已经注册或模板名字已存在
//                     */
//                    int imgArg = msg.arg1;
//                    if (imgArg == 1) {
//                        int imgLength = msg.arg2;
//                        imgData = (byte[]) msg.obj;
//                        imgDatas.add(imgData);
////                        getTemplList();
//                        tipTv.setText("设备获取图像成功");
//
//                    } else if (imgArg == -1) {
//                        tipTv.setText("抓图超时");
//                    } else if (imgArg == -2) {
//                        tipTv.setText("设备断开");
//                    } else if (imgArg == -3) {
//                        tipTv.setText("操作取消");
//                    } else if (imgArg == -4) {
//                        tipTv.setText("入参错误");
//                    } else if (imgArg == -5) {
//                        tipTv.setText("该指静脉已经注册或模板名字已存在");
//                    } else if (imgArg == 2) {
//                        tipTv.setText("特征融合失败，因\"特征\"数据一致性差，Output数据无效");
//                    } else if (imgArg == 3) {
//                        tipTv.setText("特征融合失败，因参数不合法,Output数据无效");
//                    } else if (imgArg == 4) {
//                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
//                    } else if (imgArg == 5) {
//                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
//                    } else if (imgArg == 6) {
//                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
//                    } else if (imgArg == 7) {
//                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
//                    } else if (imgArg == 8) {
//                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
//                    } else if (imgArg == 9) {
//                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
//                    }
//                    break;
                case TG661JBAPI.CANCEL_REGISTER:
                    /*
                     * 取消设备抓图
                     * 返回值 cancelImgArg
                     * 1:取消抓取图像成功
                     * -1:取消抓取图像失败
                     */
                    int cancelImgArg = msg.arg1;
                    if (cancelImgArg == 1) {
                        tipTv.setText("取消注册成功");
                    } else if (cancelImgArg == -1) {
                        tipTv.setText("取消注册失败");
                    }
                    registerBtnBehind.setClickable(true);
                    break;
                case TG661JBAPI.FEATURE_FUSION:
                    int fusionArg = msg.arg1;
//                    if (fusionArg == 1) {
//
//                    } else if (fusionArg == 2) {
//
//                    } else if (fusionArg == -1) {
//
//                    } else if (fusionArg == -2) {
//
//                    } else if (fusionArg == -3) {
//
//                    }
                    break;
                case TG661JBAPI.INIT_FV:
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
                case TG661JBAPI.DEV_WORK_MODEL:
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
                        devModelTv.setText("前比3");
                    } else if (devWorkModelArg == 2) {
                        devModelTv.setText("后比");
                    } else if (devWorkModelArg == 3) {
                        devModelTv.setText("前比6");
                    } else if (devWorkModelArg == -1) {
                        devModelTv.setText("获取工作模式失败");
                    }
                    break;
                case TG661JBAPI.EXTRACT_FEATURE_REGISTER:
                    /*
                     * 从图片中提取特征(注册的时候专用)
                     * 返回值:extractFeatureRegisterArg
                     *   1：登记成功(标识外部存储，登记成功后会返回融合后的模板数据)
                     *   2: 特征融合失败，因"特征"数据一致性差，Output数据无效
                     *   3: 特征融合失败，因参数不合法,Output数据无效
                     *   4: 特征提取失败,因证书路径错误,Output数据无效
                     *   5：特征提取失败,因证书内容无效,Output数据无效
                     *   6：特征提取失败,因证书内容过期,Output数据无效
                     *   7：特征提取失败,因"图像"数据无效,Output数据无效
                     *   8：特征提取失败,因"图像"质量较差,Output数据无效
                     *   9：特征提取失败,因参数不合法,Output数据无效
                     *   10: 特征提取成功,Output数据有效
                     *   11:抓取图片成功
                     *   -1: 抓图超时
                     *   -2:设备断开
                     *   -3:操作取消
                     *   -4:入参错误
                     *   -5:已存在相同模板
                     *   -6:注册的模板ID不能为空
                     *   -7:注册的模板名称不可包含数字/字母/中文以外的字符
                     *   -8:已存在相同模板名称
                     *   -9:抓取的图片数据为null，请检查
                     */
                    int extractFeatureRegisterArg = msg.arg1;
                    //显示图片
                    getImgData(msg);
                    if (extractFeatureRegisterArg == 10) {
                        tipTv.setText("特征提取成功");
                    } else if (extractFeatureRegisterArg == 1) {
                        getTemplList();
                        tipTv.setText("登记成功");
                        Bundle data = msg.getData();
                        //指静脉模板数据
                        byte[] fingerData = data.getByteArray(TG661JBAPI.FINGER_DATA);
                        Log.d("===AAA", "   模板数据返回成功 : "+fingerData);
                    } else if (extractFeatureRegisterArg == 2) {
                        tipTv.setText("特征融合失败，因\"特征\"数据一致性差，Output数据无效");
                    } else if (extractFeatureRegisterArg == 3) {
                        tipTv.setText("特征融合失败，因参数不合法,Output数据无效");
                    } else if (extractFeatureRegisterArg == 4) {
                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
                    } else if (extractFeatureRegisterArg == 5) {
                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
                    } else if (extractFeatureRegisterArg == 6) {
                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
                    } else if (extractFeatureRegisterArg == 7) {
                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
                    } else if (extractFeatureRegisterArg == 8) {
                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
                    } else if (extractFeatureRegisterArg == 9) {
                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
                    } else if (extractFeatureRegisterArg == -1) {
                        tipTv.setText("抓图超时");
                    } else if (extractFeatureRegisterArg == -2) {
                        tipTv.setText("设备断开");
                    } else if (extractFeatureRegisterArg == -3) {
                        tipTv.setText("操作取消");
                    } else if (extractFeatureRegisterArg == -4) {
                        tipTv.setText("入参错误");
                    } else if (extractFeatureRegisterArg == -5) {
                        tipTv.setText("已存在相同模板");
                    } else if (extractFeatureRegisterArg == -6) {
                        tipTv.setText("注册的模板ID不能为空");
                        ToastUtil.toast(BehindActivity.this, "注册的模板ID不能为空");
                    } else if (extractFeatureRegisterArg == -7) {
                        tipTv.setText("注册的模板名称不可包含数字/字母/中文以外的字符");
                        ToastUtil.toast(BehindActivity.this,
                                "注册的模板名称不可包含数字/字母/中文以外的字符");
                    } else if (extractFeatureRegisterArg == -8) {
                        tipTv.setText("已存在相同模板名称");
                    }
                    registerBtnBehind.setClickable(true);
                    break;
                case TG661JBAPI.EXTRACT_FEATURE_VERIFY:
                    /*
                     * 图片提取特征(验证专用)
                     * 返回值：extractFeatureVerifyArg
                     * 1: 特征提取成功,Output数据有效
                     * 2:特征提取失败,因证书路径错误,Output数据无效
                     * 3:特征提取失败,因证书内容无效,Output数据无效
                     * 4:特征提取失败,因证书内容过期,Output数据无效
                     * 5:特征提取失败,因"图像"数据无效,Output数据无效
                     * 6:特征提取失败,因"图像"质量较差,Output数据无效
                     * -1:特征提取失败,因参数不合法,Output数据无效
                     */
                    int extractFeatureVerifyArg = msg.arg1;
                    if (extractFeatureVerifyArg == 1) {
                        byte[] feature = (byte[]) msg.obj;//验证时返回的特征
                        tipTv.setText("特征提取成功");
                    } else if (extractFeatureVerifyArg == 2) {
                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
                    } else if (extractFeatureVerifyArg == 3) {
                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
                    } else if (extractFeatureVerifyArg == 4) {
                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
                    } else if (extractFeatureVerifyArg == 5) {
                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
                    } else if (extractFeatureVerifyArg == 6) {
                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
                    } else if (extractFeatureVerifyArg == -1) {
                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
                    }
                    break;
                case TG661JBAPI.RESOLVE_COMPARE_TEMPL:
                    /*
                     * 将模板解析成可比对模板
                     * 返回值:resolveCompareMsg
                     * 1:模板解析成功， Output数据有效
                     * -1:模板解析失败，因参数不合法，Output数据无效
                     * -2:待解析的模板数据为null
                     */
                    int resolveCompareMsg = msg.arg1;
                    if (resolveCompareMsg == 1) {
                        byte[] matchTmplData = (byte[]) msg.obj;//解析后的模板数据
                        tipTv.setText("模板解析成功");
                    } else if (resolveCompareMsg == -1) {
                        tipTv.setText("模板解析失败，因参数不合法，Output数据无效");
                    } else if (resolveCompareMsg == -2) {
                        tipTv.setText("待解析的模板数据为null");
                    }
                    break;
                case TG661JBAPI.DEV_IMG:
                    /*
                    设备抓取图片:
                     返回值：
                     0：图片抓取成功
                     -1：抓图超时
                     -2:设备断开
                     -3:操作取消
                     -4:入参错误
                     */
                    int devImgArg = msg.arg1;
                    if (devImgArg == 0) {
                        tipTv.setText("图片抓取成功");
                    } else if (devImgArg == -1) {
                        tipTv.setText("抓图超时");
                    } else if (devImgArg == -2) {
                        tipTv.setText("设备断开");
                    } else if (devImgArg == -3) {
                        tipTv.setText("操作取消");
                    } else if (devImgArg == -4) {
                        tipTv.setText("入参错误");
                    }
                    break;
                case TG661JBAPI.FEATURE_COMPARE1_1:
                    /*
                     * 特征模板1:1验证
                     * 返回值:match1Arg1
                     * 1:特征比对（1:1）成功，Output数据有效
                     * 2:特征比对（1:1）失败，因比对失败,仅Output的matchScore数据有效
                     * 3:特征比对（1:1）失败，因参数不合法,Output数据无效
                     * 4:特征提取失败,因证书路径错误,Output数据无效
                     * 5:特征提取失败,因证书内容无效,Output数据无效
                     * 6:特征提取失败,因证书内容过期,Output数据无效
                     * 7:特征提取失败,因"图像"数据无效,Output数据无效
                     * 8:特征提取失败,因"图像"质量较差,Output数据无效
                     * 9:特征提取失败,因参数不合法,Output数据无效
                     * 10:抓图超时
                     * 11:设备断开
                     * 12:操作取消
                     * 13:入参错误
                     * -1:模板解析失败，因参数不合法，Output数据无
                     * -2:模板数据可能为null，请检查
                     */
                    int match1Arg1 = msg.arg1;
                    if (match1Arg1 == 1) {
                        Bundle data = msg.getData();
                        byte[] updateTemplData = data.getByteArray(TG661JBAPI.COMPARE_N_TEMPL);//可更新的模板
                        String templName = data.getString(TG661JBAPI.COMPARE_NAME);
                        int templScore = data.getInt(TG661JBAPI.COMPARE_N_SCORE);
                        //显示图片
                        getImgData(msg);
                        if (autoUpdateStatus) {
                            tg661JBAPI.updateHostTempl(updateTemplData,
                                    handler, templName);
                        }
                        tipTv.setText(MessageFormat.format("验证成功,验证分数：{0}", templScore));
                    } else if (match1Arg1 == 2) {
                        Bundle data = msg.getData();
                        int match1Score = data.getInt(TG661JBAPI.COMPARE_N_SCORE);
                        tipTv.setText(MessageFormat.format("特征比对（1:1）失败，" +
                                "因比对失败,仅Output的matchScore数据有效,分数：{0}", match1Score));
                        //显示图片
                        getImgData(msg);
                    } else if (match1Arg1 == 3) {
                        tipTv.setText("特征比对（1:1）失败，因参数不合法,Output数据无效");
                        //显示图片
                        getImgData(msg);
                    } else if (match1Arg1 == 4) {
                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (match1Arg1 == 5) {
                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (match1Arg1 == 6) {
                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (match1Arg1 == 7) {
                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (match1Arg1 == 8) {
                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (match1Arg1 == 9) {
                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (match1Arg1 == 10) {
                        tipTv.setText("抓图超时");
                    } else if (match1Arg1 == 11) {
                        tipTv.setText("设备断开");
                    } else if (match1Arg1 == 12) {
                        tipTv.setText("操作取消");
                    } else if (match1Arg1 == 13) {
                        tipTv.setText("入参错误");
                    } else if (match1Arg1 == -1) {
                        tipTv.setText("模板解析失败，因参数不合法，Output数据无效");
                    }
                    ver1_1Btn.setClickable(true);
                    break;
                case TG661JBAPI.FEATURE_COMPARE1_N:
                    /*
                     * 返回值：matchNArg
                     * 1:特征比对（1：N）成功，Output数据有效
                     * 2:特征比对（1：N）失败，仅Output的matchScore数据有效
                     * 3:特征比对（1：N）失败，因参数不合法，Output数据无效
                     * 4:特征提取失败,因证书路径错误,Output数据无效
                     * 5:特征提取失败,因证书内容无效,Output数据无效
                     * 6:特征提取失败,因证书内容过期,Output数据无效
                     * 7:特征提取失败,因"图像"数据无效,Output数据无效
                     * 8:特征提取失败,因"图像"质量较差,Output数据无效
                     * 9:特征提取失败,因参数不合法,Output数据无效
                     * -1:抓图超时
                     * -2:设备断开
                     * -3:操作取消
                     * -4:入参错误
                     * -5:该指静脉已经注册或模板名字已经注册
                     */
                    int matchNArg = msg.arg1;
                    if (matchNArg == 1) {
                        Bundle data = msg.getData();
                        byte[] updateTemplData = data.getByteArray(TG661JBAPI.COMPARE_N_TEMPL);//可更新的模板
                        int templScore = data.getInt(TG661JBAPI.COMPARE_N_SCORE);
                        //非主机文件存储时候才返回这个模板索引
                        int index = data.getInt(TG661JBAPI.INDEX);
                        String tip = data.getString("tip");
                        if (!TextUtils.isEmpty(tip))
                            ToastUtil.toast(BehindActivity.this, tip);
                        //主机文件夹下存储时才返回模板名称
                        String templName = data.getString(TG661JBAPI.COMPARE_NAME);
                        if (autoUpdateStatus) {
                            tg661JBAPI.updateHostTempl(updateTemplData, handler, templName);
                        }
                        tipTv.setText(MessageFormat.format("验证成功,验证分数：{0}", templScore));
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == 2) {
                        Bundle data = msg.getData();
                        int templScore = data.getInt(TG661JBAPI.COMPARE_N_SCORE);
                        tipTv.setText("特征比对（1：N）失败，仅Output的matchScore数据有效,分数：" + templScore);
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == 3) {
                        tipTv.setText("特征比对（1：N）失败，因参数不合法，Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == 4) {
                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == 5) {
                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == 6) {
                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == 7) {
                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == 8) {
                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == 9) {
                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
                        //显示图片
                        getImgData(msg);

                    } else if (matchNArg == -1) {
                        tipTv.setText("抓图超时");
                    } else if (matchNArg == -2) {
                        tipTv.setText("设备断开");
                    } else if (matchNArg == -3) {
                        tipTv.setText("操作取消");
                    } else if (matchNArg == -4) {
                        tipTv.setText("入参错误");
                    } else if (matchNArg == -5) {
                        tipTv.setText("该指静脉已经注册或模板名字已经注册");
                    }
                    ver1_nBtn.setClickable(true);
                    break;
                case TG661JBAPI.DELETE_HOST_ALL_TEMPL:
                    int deleteHostIDArg = msg.arg1;
                    if (deleteHostIDArg == 1) {
                        getTemplList();
                        tipTv.setText("删除成功");
                        templIDBehind.getText().clear();
                        tg661JBAPI.setTemplModelType(templModelType);
                        getAp().play_deleteSuccess();
                    } else if (deleteHostIDArg == -1) {
                        tipTv.setText("删除失败");
                        getAp().play_deleteFail();
                    }
                    delAllHostTemplBtn.setClickable(true);
                    break;
                case TG661JBAPI.DELETE_HOST_ID_TEMPL:
                    int deleteHostAllArg = msg.arg1;
                    if (deleteHostAllArg == 1) {
                        getTemplList();
                        templIDBehind.getText().clear();
                        tg661JBAPI.setTemplModelType(templModelType);
                        tipTv.setText("删除成功");
                        getAp().play_deleteSuccess();
                    } else if (deleteHostAllArg == -1) {
                        tipTv.setText("删除失败");
                        getAp().play_deleteFail();
                    }
                    delIDHostTemplBtn.setClickable(true);
                    break;
                case TG661JBAPI.UPDATE_HOST_TEMPL:
                    //更新主机模板的结果
                    boolean updateStatus = (boolean) msg.obj;
                    if (updateStatus) {
                        tipTv.setText("模板更新成功");
                    } else {
                        tipTv.setText("模板更新失败");
                    }
                    break;
                case TG661JBAPI.TEMPL_SN:
                    int templSnArg = msg.arg1;
                    if (templSnArg == 1) {
                        String sn = (String) msg.obj;
                        tipTv.setText(MessageFormat.format("模板的序列号:{0}", sn));
                    } else if (templSnArg == -1) {
                        tipTv.setText("获取失败，参数错误，Output数据无效");
                    } else if (templSnArg == -2) {
                        tipTv.setText("模板名称为空/不存在，请检查");
                    }
                    getTemplSN.setClickable(true);
                    break;
                case TG661JBAPI.TEMPL_FW:
                    int fwArg = msg.arg1;
                    if (fwArg == 1) {
                        String fw = (String) msg.obj;
                        tipTv.setText(MessageFormat.format("模板的固件号:{0}", fw));
                    } else if (fwArg == -1) {
                        tipTv.setText("获取失败，参数错误，Output数据无效");
                    } else if (fwArg == -2) {
                        tipTv.setText("模板名称为空/不存在，请检查");
                    }
                    getTemplFW.setClickable(true);
                    break;
                case TG661JBAPI.TEMPL_TIME:
                    int templTimeArg = msg.arg1;
                    if (templTimeArg == 1) {
                        String time = (String) msg.obj;
                        tipTv.setText(MessageFormat.format("模板的注册时间:{0}", time));
                    } else if (templTimeArg == -1) {
                        tipTv.setText("获取失败，参数错误");
                    } else if (templTimeArg == -2) {
                        tipTv.setText("模板名称为空，请检查");
                    }
                    templTimeBtn.setClickable(true);
                    break;
                case TG661JBAPI.TEMPL_FV_VERSION:
                    int templVersionArg = msg.arg1;
                    if (templVersionArg == 1) {
                        String snVersion = (String) msg.obj;
                        tipTv.setText(MessageFormat.format("模板的算法版本号:{0}", snVersion));
                    } else if (templVersionArg == -1) {
                        tipTv.setText("获取失败，参数错误");
                    } else if (templVersionArg == -2) {
                        tipTv.setText("模板名称为空，请检查");
                    }
                    getTemplAlgorVersionBtn.setClickable(true);
                    break;
                case TG661JBehindAPI.WAIT_DIALOG:
                    int typeDialog = msg.arg1;
                    if (typeDialog == 1) {
                        String tipStr = (String) msg.obj;
                        waitDialog = AlertDialogUtil.Instance()
                                .showWaitDialog(BehindActivity.this, tipStr);
                    } else if (typeDialog == -1) {
                        if (waitDialog != null && waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                    }
                    break;
                case TG661JBehindAPI.OPEN_DEV:
                    int openDevArg = msg.arg1;
                    if (openDevArg == 1) {
                        //初始化获取主机模板列表
                        getTemplList();
                        tipTv.setText("设备打开成功,工作模式设置成功");
                    } else if (openDevArg == -1) {
                        tipTv.setText("设备打开失败");
                    }
                    break;
                case TG661JBehindAPI.CLOSE_DEV:
                    int closeDevArg = msg.arg1;
                    if (closeDevArg == -1) {
                        tipTv.setText("设备状态:设备关闭失败");
                    } else if (closeDevArg == 1) {
                        tipTv.setText("设备状态:设备关闭成功");
                    } else if (closeDevArg == 2) {
                        tipTv.setText("设备状态:设备已关闭");
                    }
                    break;
                case TG661JBehindAPI.WRITE_FILE:
                    //往主机中写入文件
                    int writeHostArg = msg.arg1;
                    if (writeHostArg == -1) {
                        tipTv.setText("写入失败");
                    } else if (writeHostArg == -9) {
                        tipTv.setText("传进来待写入的文件数据或文件名称为null，请检查");
                    } else if (writeHostArg == 1) {
                        tipTv.setText("写入成功");
                    }
                    break;
                case TG661JBAPI.DEV_IMG_LISTENER:
                    //设备是否有图像返回
                    int devImgArg1 = msg.arg1;
                    int devImgLength = msg.arg2;
                    byte[] devImgData = (byte[]) msg.obj;
                    if (devImgArg1 == 1 && devImgData != null) {
//                        tipTv.setText("检测到有指静脉");
                        Log.d("===HHH", "   指静脉数据长度：" + devImgLength + "  指静脉数据 ：" + devImgData);
                    } else {
//                        tipTv.setText("没检测到指静脉");
                        Log.d("===HHH", "   指静脉数据长度：" + devImgLength + "  指静脉数据 ：" + devImgData);
                    }
                    break;
            }
        }
    };

    boolean isGetImg = false;
    //小特征 6K
    private TG661JBAPI tg661JBAPI = TG661JBAPI.getTg661JBAPI();

    //大特征 32K
//    private TG661JBehindAPI tg661JBAPI = TG661JBehindAPI.getTG661JBehindAPI();

    private AudioProvider getAp() {
        return tg661JBAPI.getAP(BehindActivity.this);
//       return tg661JBAPI.getAP();
    }

    private boolean devOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behind);
        //后比
        initView();
        init();
        //开启设备
        openDev();
        //显示图片
//        tg661JBAPI.setsImg(true);
        //监听设备是否有指静脉图像返回
//        devImgListener();
    }

    private void initView() {
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
        userFingerName = findViewById(R.id.userFingerName);

        getTemplAlgorVersionBtn = findViewById(R.id.getTemplAlgorVersionBtn);
        CheckBox autoUpdateTempl = findViewById(R.id.autoUpdateTempl);
        RecyclerView templFileRv = findViewById(R.id.templFileRv);

        templFileRv.setLayoutManager(new LinearLayoutManager(BehindActivity.this,
                OrientationHelper.VERTICAL, false));
        templAdapter = new TemplAdapter(BehindActivity.this);
        templAdapter.setItemClick(this);
        templFileRv.setAdapter(templAdapter);

        //初始化算法----->将算法的数据流传进去
        tg661JBAPI.initFV(handler, BehindActivity.this, null, false);

        //获取当前音量
        String currentVolume = tg661JBAPI.getCurrentVolume(handler);
        volumeTt.setText(currentVolume);
        //初始化获取当前特征模式下的模板列表
        getTemplList();

        openDevBtn.setOnClickListener(this);
        closeDevBtn.setOnClickListener(this);
        getDevModel.setOnClickListener(this);
        getSDKVersion.setOnClickListener(this);

        registerBtnBehind.setOnClickListener(this);
        cancelRegisterBtnBehind.setOnClickListener(this);
        ver1_1Btn.setOnClickListener(this);
        ver1_nBtn.setOnClickListener(this);
        getTemplAlgorVersionBtn.setOnClickListener(this);
        getTemplFW.setOnClickListener(this);
        getTemplSN.setOnClickListener(this);
        templTimeBtn.setOnClickListener(this);
        voiceIncreaceBtn.setOnClickListener(this);
        voiceDecreaceBtn.setOnClickListener(this);
        delIDHostTemplBtn.setOnClickListener(this);
        delAllHostTemplBtn.setOnClickListener(this);

        getImg.setOnClickListener(this);
        getImgFeature.setOnClickListener(this);
        getFeatureTempl.setOnClickListener(this);
        getMatchTempl.setOnClickListener(this);
        get1_1.setOnClickListener(this);
        get1_N.setOnClickListener(this);
        clearEt.setOnClickListener(this);

        templIDBehind.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (templIDBehind.getText().toString().trim().length() > 0) {
                    clearEt.setVisibility(View.VISIBLE);
                } else {
                    clearEt.setVisibility(View.GONE);
                }
            }
        });

        //3特征模板和6特征模板切换
        templSumModel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                devStatu = checkDevStatus();
                if (devStatu) {
                    templIDBehind.getText().clear();
                    if (i == templ3Rb.getId()) {
                        templModelType = TG661JBAPI.TEMPL_MODEL_3;
                        tg661JBAPI.setTemplModelType(TG661JBAPI.TEMPL_MODEL_3);
                    } else if (i == templ6Rb.getId()) {
                        templModelType = TG661JBAPI.TEMPL_MODEL_6;
                        tg661JBAPI.setTemplModelType(TG661JBAPI.TEMPL_MODEL_6);
                    }
                    getTemplList();
                }
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

    private void init() {
        //检查权限，如果同意权限后会初始化一次算法；如果不需要权限的，则需要另行初始化算法
        tg661JBAPI.checkPermissions(handler, 0, this);
//        if (!tg661JBAPI.getisInitFV())
        //初始化算法
        tg661JBAPI.initFV(handler, this, null, false);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
        if (tg661JBAPI.isDevOpen()) {
            closeDev();
        }
    }

    public void getTemplList() {
        ArrayList<String> aimFileList = tg661JBAPI.getAimFileList();
        templAdapter.clearData();
        templAdapter.addData(aimFileList);
    }

    private boolean checkDevStatus() {
        boolean devOpen = tg661JBAPI.isDevOpen();
        if (!devOpen) {
            ToastUtil.toast(BehindActivity.this, "请先开启设备");
            return false;
        } else {
            return true;
        }
    }

    //打开设备
    private void openDev() {
        devOpen = tg661JBAPI.isDevOpen();
        if (!devOpen) {
            //设备工作模式--》后比
            int workType = TG661JBAPI.WORK_BEHIND;
            tg661JBAPI.openDev(handler, BehindActivity.this, workType,
                    templModelType, TG661JBAPI.EXTERNAL_TEMPL_SOURCES);
        } else {
            ToastUtil.toast(BehindActivity.this, "设备已经开启");
        }
    }

    //关闭设备
    private void closeDev() {
        devOpen = tg661JBAPI.isDevOpen();
        if (devOpen) {
            tg661JBAPI.closeDev(handler);
        } else {
            ToastUtil.toast(BehindActivity.this, "设备已经关闭");
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
            case R.id.getDevModel:
                //获取设备的工作模式
                tg661JBAPI.getDevWorkModel(handler);
                break;
            case R.id.getSDKVersion:
                //获取SDK的版本号
                String sdkVersion = tg661JBAPI.getSDKVersion();
                tipTv.setText(String.format("SDK版本号：%s", sdkVersion));
                break;
            ////后比
            case R.id.registerBtnBehind:
                //注册
                String templID = templIDBehind.getText().toString().trim();
//                if (TextUtils.isEmpty(templID)) {
//                    ToastUtil.toast(BehindActivity.this, "注册的模板ID不能为空");
//                } else {
//                    //检测注册名只包含字母或数字或中文
//                    boolean b = RegularUtil.strContainsNumOrAlpOrChin(templID);
//                    if (b) {
                        devStatu = checkDevStatus();
                        if (devStatu) {
                            tg661JBAPI.extractFeatureRegister(handler,
                                    templModelType, templID);
                            registerBtnBehind.setClickable(false);
                        }
//                    } else {
//                        ToastUtil.toast(BehindActivity.this,
//                                "注册的模板名称不可包含数字/字母/中文以外的字符");
//                    }
//                }
                break;
            case R.id.cancelRegisterBtnBehind:
                //取消注册：调用取消抓图接口并重置上一次抓图已经存在的数据
                devStatu = checkDevStatus();
                if (devStatu) {
                    tg661JBAPI.cancelRegister(handler, templModelType);
                }
                break;
            case R.id.ver1_1Btn:
                //1:1验证
                String templName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(templName)) {
                    ToastUtil.toast(BehindActivity.this, "请选择要比对的模板文件");
                } else {
                    devStatu = checkDevStatus();
                    if (devStatu) {
                        tg661JBAPI.featureCompare1_1(handler, templName, null, true);
                        ver1_1Btn.setClickable(false);
                    }
                }
                break;
            case R.id.ver1_NBtn:
                //1:N验证
//                String userFinngerName = userFingerName.getText().toString().trim();
//                if (TextUtils.isEmpty(userFinngerName)) {
//                    ToastUtil.toast(this, "用户的验证手指名称不可为空");
//                    return;
//                }
                devStatu = checkDevStatus();
                if (devStatu) {
                    tg661JBAPI.featureCompare1_N(handler, true);
                    ver1_nBtn.setClickable(false);
                }
                break;
            case R.id.getTemplSN:
                String snTemplName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(snTemplName)) {
                    ToastUtil.toast(BehindActivity.this, "模板名字不能为空");
                } else {
                    devStatu = checkDevStatus();
                    if (devStatu) {
                        tg661JBAPI.getTemplSN(handler, snTemplName);
                        getTemplSN.setClickable(false);
                    }
                }
                break;
            case R.id.getTemplFW:
                String fwTemplName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(fwTemplName)) {
                    ToastUtil.toast(BehindActivity.this, "模板名字不能为空");
                } else {
                    devStatu = checkDevStatus();
                    if (devStatu) {
                        tg661JBAPI.getTemplFW(handler, fwTemplName);
                        getTemplFW.setClickable(false);
                    }
                }
                break;
            case R.id.templTimeBtn:
                String timeTemplName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(timeTemplName)) {
                    ToastUtil.toast(BehindActivity.this, "模板名字不能为空");
                } else {
                    devStatu = checkDevStatus();
                    if (devStatu) {
                        tg661JBAPI.getTemplTime(handler, timeTemplName);
                        templTimeBtn.setClickable(false);
                    }
                }
                break;
            case R.id.getTemplAlgorVersionBtn:
                String fvVersionTemplName = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(fvVersionTemplName)) {
                    ToastUtil.toast(BehindActivity.this, "模板名字不能为空");
                } else {
                    devStatu = checkDevStatus();
                    if (devStatu) {
                        tg661JBAPI.getTemplVersion(handler, fvVersionTemplName);
                        getTemplAlgorVersionBtn.setClickable(false);
                    }
                }
                break;
            case R.id.voiceIncreaceBtn:
                //音量加
                boolean increaseVolume = tg661JBAPI.increaseVolume(handler);
                voiceIncreaceBtn.setClickable(false);
                if (increaseVolume) {
                    String currentVolume = tg661JBAPI.getCurrentVolume(handler);
                    volumeTt.setText(currentVolume);
                    tipTv.setText("音量增大成功");
                } else {
                    tipTv.setText("已经是最大音量");
                }
                voiceIncreaceBtn.setClickable(true);
                break;
            case R.id.voiceDecreaceBtn:
                //音量减
                boolean descreaseVolume = tg661JBAPI.descreaseVolume(handler);
                voiceDecreaceBtn.setClickable(false);
                if (descreaseVolume) {
                    String currentVolume = tg661JBAPI.getCurrentVolume(handler);
                    volumeTt.setText(currentVolume);
                    tipTv.setText("音量减小成功");
                } else {
                    tipTv.setText("已经是最小音量");
                }
                voiceDecreaceBtn.setClickable(true);
                break;
            case R.id.delIDHostTemplBtn:
                //删除指定ID的模板
                String templNameID = templIDBehind.getText().toString().trim();
                if (TextUtils.isEmpty(templNameID)) {
                    ToastUtil.toast(BehindActivity.this, "模板名字不能为空");
                } else {
                    devStatu = checkDevStatus();
                    if (devStatu) {
                        tg661JBAPI.deleteHostIdTempl(handler, templNameID);
                        delIDHostTemplBtn.setClickable(false);
                    }
                }
                break;
            case R.id.delAllHostTemplBtn:
                //删除主机中的所有模板
                devStatu = checkDevStatus();
                if (devStatu) {
                    tg661JBAPI.deleteHostAllTempl(handler);
                    delAllHostTemplBtn.setClickable(false);
                }
                break;
            case R.id.getImgFeature://从图像提取特征
                if (imgData != null) {
                    if (imgDatas != null && imgDatas.size() == 2) {
                        tg661JBAPI.fusionFeature(handler, imgDatas);
                    }
                }
                break;
            case R.id.clearEt:
                templIDBehind.getText().clear();
                break;
        }
    }

    @Override
    public void commiteInfo(String info, int flag) {

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
        tg661JBAPI.deleteHostIdTempl(handler, datFileName);
    }

    /**
     * 界面显示图片
     */
    //提取图片数据
    public void getImgData(Message msg) {
        Bundle data = msg.getData();
        int imgLength = data.getInt("imgLength");
        byte[] imgData = data.getByteArray("imgData");
        if (imgData != null && imgLength > 0) {
            Log.i("===AAA", "  imgLength:" + imgLength);
            byte[] jpegData = new byte[imgLength];
            System.arraycopy(imgData, 1024 * 256, jpegData, 0, imgLength);
            Glide.with(BehindActivity.this).load(jpegData).into(iv);
        }
    }


}
