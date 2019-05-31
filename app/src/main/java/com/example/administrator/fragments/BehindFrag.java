package com.example.administrator.fragments;


/**
 * A simple {@link Fragment} subclass.
 */
//public class BehindFrag extends Fragment implements View.OnClickListener,
//        CommitCallBack, TemplAdapter.ItemClick {
//
//
//    private TemplAdapter templAdapter;
//    private Button registerBtnBehind;
//    private Button ver1_1Btn;
//    private Button ver1_nBtn;
//    private Button getTemplFW;
//    private Button getTemplSN;
//    private Button templTimeBtn;
//    private Button voiceIncreaceBtn;
//    private Button delIDHostTemplBtn;
//    private Button delAllHostTemplBtn;
//    private Button voiceDecreaceBtn;
//    private Button getTemplAlgorVersionBtn;
//    private Button getImg;
//    private Button getImgFeature;
//    private Button getFeatureTempl;
//    private Button getMatchTempl;
//    private Button get1_1;
//    private Button get1_N;
//    private ImageView iv;
//    private ImageView clearEt;
//    private boolean devStatu;
//
//    public BehindFrag() {
//        // Required empty public constructor
//    }
//
//    public void setTip(String str) {
//        tipTv.setText(str);
//    }
//
//    public static BehindFrag instance() {
//        BehindFrag behindFrag = new BehindFrag();
//        Bundle bundle = new Bundle();
//        behindFrag.setArguments(bundle);
//        return behindFrag;
//    }
//
//    //提取图片数据
//    public void getImgData(Message msg) {
//        Bundle data = msg.getData();
//        int imgLength = data.getInt("imgLength");
//        byte[] imgData = data.getByteArray("imgData");
//        if (imgData != null) {
//            byte[] jpegData = new byte[imgLength];
//            System.arraycopy(imgData, 1024 * 256, jpegData, 0, imgLength);
//            Glide.with(getActivity()).load(jpegData).into(iv);
//        }
//    }
//
//
//    private boolean bb = false;
//    private byte[] imgData;
//    private List<byte[]> imgDatas = new ArrayList<>();
//
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case TG661JAPI.DEV_STATUS:
//                    /**
//                     * 设备状态：
//                     *  1：设备状态：已连接
//                     *  -1：已断开,连接中...
//                     */
//                    int devStatusArg = msg.arg1;
//                    if (devStatusArg >= 0) {
//                        if (tipTv.getText().toString().contains("断开")) {
//                            tipTv.setText("设备状态：已连接");
//                        }
//                        devStatus.setText("设备状态:已连接");
//                    } else if (devStatusArg == -1) {
//                        tipTv.setText("设备状态：未连接");
//                        devStatus.setText("设备状态:未连接");
//                        registerBtnBehind.setText("注册");
//                        isGetImg = false;
//                    } else if (devStatusArg == -2) {
//                        tipTv.setText("设备状态：未连接/已断开");
//                        devStatus.setText("设备状态:已断开,重新连接中...");
//                        registerBtnBehind.setText("注册");
//                        isGetImg = false;
//                    }
//                    break;
//                case TG661JAPI.DEV_IMG_REGISTER:
//                    /**
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
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == -1) {
//                        tipTv.setText("抓图超时");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == -2) {
//                        tipTv.setText("设备断开");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == -3) {
//                        tipTv.setText("操作取消");
//                    } else if (imgArg == -4) {
//                        tipTv.setText("入参错误");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == -5) {
//                        tipTv.setText("该指静脉已经注册或模板名字已存在");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == 2) {
//                        tipTv.setText("特征融合失败，因\"特征\"数据一致性差，Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == 3) {
//                        tipTv.setText("特征融合失败，因参数不合法,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == 4) {
//                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == 5) {
//                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == 6) {
//                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == 7) {
//                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == 8) {
//                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (imgArg == 9) {
//                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    }
//                    break;
////                case TG661JAPI.CANCEL_DEV_IMG:
////                    /**
////                     * 取消设备抓图
////                     * 返回值 cancelImgArg
////                     * 1:取消抓取图像成功
////                     * -1:取消抓取图像失败
////                     */
////                    int cancelImgArg = msg.arg1;
////                    if (cancelImgArg == 1) {
////                        tipTv.setText("取消注册成功");
////                    } else if (cancelImgArg == -1) {
////                        tipTv.setText("取消注册失败");
////                    }
////                    break;
//                case TG661JAPI.FEATURE_FUSION:
//                    int fusionArg = msg.arg1;
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
//                    break;
//                case TG661JAPI.INIT_FV:
//                    /**
//                     * 初始化算法接口
//                     * 返回值:initFvArg
//                     *  （1）1：初始化成功,算法接口有效
//                     *  （2）2: 初始化失败,因证书路径错误,算法接口无效
//                     *  （3）3: 初始化失败,因证书内容无效,算法接口无效
//                     *  （4）4: 初始化失败,因证书内容过期,算法接口无效
//                     */
//                    int initFvArg = msg.arg1;
//                    if (initFvArg == 1) {
//                        tipTv.setText("初始化成功,算法接口有效");
//                    } else if (initFvArg == 2) {
//                        tipTv.setText("初始化失败,因证书路径错误,算法接口无效");
//                    } else if (initFvArg == 3) {
//                        tipTv.setText("初始化失败,因证书内容无效,算法接口无效");
//                    } else if (initFvArg == 4) {
//                        tipTv.setText("初始化失败,因证书内容过期,算法接口无效");
//                    }
//                    break;
//                case TG661JAPI.EXTRACT_FEATURE_REGISTER:
//                    /**
//                     * 从图片中提取特征(注册的时候专用)
//                     * 返回值:extractFeatureRegisterArg
//                     *   1：登记成功
//                     *   2: 特征融合失败，因"特征"数据一致性差，Output数据无效
//                     *   3: 特征融合失败，因参数不合法,Output数据无效
//                     *   4: 特征提取失败,因证书路径错误,Output数据无效
//                     *   5：特征提取失败,因证书内容无效,Output数据无效
//                     *   6：特征提取失败,因证书内容过期,Output数据无效
//                     *   7：特征提取失败,因"图像"数据无效,Output数据无效
//                     *   8：特征提取失败,因"图像"质量较差,Output数据无效
//                     *   9：特征提取失败,因参数不合法,Output数据无效
//                     *   10: 特征提取成功,Output数据有效
//                     *   -1: 抓图超时
//                     *   -2:设备断开
//                     *   -3:操作取消
//                     *   -4:入参错误
//                     *   -5:已存在相同模板名称
//                     */
//                    int extractFeatureRegisterArg = msg.arg1;
//                    if (extractFeatureRegisterArg == 10) {
//                        tipTv.setText("特征提取成功");
//                    } else if (extractFeatureRegisterArg == 1) {
//                        getTemplList();
//                        tipTv.setText("登记成功");
//                        //显示图片
//                        getImgData(msg);
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == 2) {
//                        tipTv.setText("特征融合失败，因\"特征\"数据一致性差，Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == 3) {
//                        tipTv.setText("特征融合失败，因参数不合法,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == 4) {
//                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == 5) {
//                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == 6) {
//                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == 7) {
//                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == 8) {
//                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == 9) {
//                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == -1) {
//                        tipTv.setText("抓图超时");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == -2) {
//                        tipTv.setText("设备断开");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == -3) {
//                        tipTv.setText("操作取消");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == -4) {
//                        tipTv.setText("入参错误");
////                        registerBtnBehind.setClickable(true);
//                    } else if (extractFeatureRegisterArg == -5) {
//                        tipTv.setText("该指静脉已经注册或模板名字已存在");
////                        registerBtnBehind.setClickable(true);
//                    }
//                    break;
//                case TG661JAPI.EXTRACT_FEATURE_VERIFY:
//                    /**
//                     * 图片提取特征(验证专用)
//                     * 返回值：extractFeatureVerifyArg
//                     * 1: 特征提取成功,Output数据有效
//                     * 2:特征提取失败,因证书路径错误,Output数据无效
//                     * 3:特征提取失败,因证书内容无效,Output数据无效
//                     * 4:特征提取失败,因证书内容过期,Output数据无效
//                     * 5:特征提取失败,因"图像"数据无效,Output数据无效
//                     * 6:特征提取失败,因"图像"质量较差,Output数据无效
//                     * -1:特征提取失败,因参数不合法,Output数据无效
//                     */
//                    int extractFeatureVerifyArg = msg.arg1;
//                    if (extractFeatureVerifyArg == 1) {
//                        byte[] feature = (byte[]) msg.obj;//验证时返回的特征
//                        tipTv.setText("特征提取成功");
//                    } else if (extractFeatureVerifyArg == 2) {
//                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
//                    } else if (extractFeatureVerifyArg == 3) {
//                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
//                    } else if (extractFeatureVerifyArg == 4) {
//                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
//                    } else if (extractFeatureVerifyArg == 5) {
//                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
//                    } else if (extractFeatureVerifyArg == 6) {
//                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
//                    } else if (extractFeatureVerifyArg == -1) {
//                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
//                    }
//                    break;
//                case TG661JAPI.RESOLVE_COMPARE_TEMPL:
//                    /**
//                     * 将模板解析成可比对模板
//                     * 返回值:resolveCompareMsg
//                     * 1:模板解析成功， Output数据有效
//                     * -1:模板解析失败，因参数不合法，Output数据无效
//                     * -2:待解析的模板数据为null
//                     */
//                    int resolveCompareMsg = msg.arg1;
//                    if (resolveCompareMsg == 1) {
//                        byte[] matchTmplData = (byte[]) msg.obj;//解析后的模板数据
//                        tipTv.setText("模板解析成功");
//                    } else if (resolveCompareMsg == -1) {
//                        tipTv.setText("模板解析失败，因参数不合法，Output数据无效");
//                    } else if (resolveCompareMsg == -2) {
//                        tipTv.setText("待解析的模板数据为null");
//                    }
//                    break;
//                case TG661JAPI.FEATURE_COMPARE1_1:
//                    /**
//                     * 特征模板1:1验证
//                     * 返回值:match1Arg1
//                     * 1:特征比对（1:1）成功，Output数据有效
//                     * 2:特征比对（1:1）失败，因比对失败,仅Output的matchScore数据有效
//                     * 3:特征比对（1:1）失败，因参数不合法,Output数据无效
//                     * 4:特征提取失败,因证书路径错误,Output数据无效
//                     * 5:特征提取失败,因证书内容无效,Output数据无效
//                     * 6:特征提取失败,因证书内容过期,Output数据无效
//                     * 7:特征提取失败,因"图像"数据无效,Output数据无效
//                     * 8:特征提取失败,因"图像"质量较差,Output数据无效
//                     * 9:特征提取失败,因参数不合法,Output数据无效
//                     * 10:抓图超时
//                     * 11:设备断开
//                     * 12:操作取消
//                     * 13:入参错误
//                     * -1:模板解析失败，因参数不合法，Output数据无效
//                     */
//                    int match1Arg1 = msg.arg1;
//                    if (match1Arg1 == 1) {
//                        Bundle data = msg.getData();
//                        byte[] updateTemplData = data.getByteArray(TG661JAPI.COMPARE_N_TEMPL);//可更新的模板
//                        String templName = data.getString(TG661JAPI.COMPARE_NAME);
//                        int templScore = data.getInt(TG661JAPI.COMPARE_N_SCORE);
//                        //显示图片
//                        int imgLength = data.getInt("imgLength");
////                        byte[] imgData = data.getByteArray("imgData");
////                        if (imgData != null) {
////                            byte[] jpegData = new byte[imgLength];
////                            System.arraycopy(imgData, 1024 * 256, jpegData, 0, imgLength);
////                            Glide.with(getActivity()).load(jpegData).into(iv);
////                        }
//                        if (autoUpdateStatus) {
//                            TG661JAPI.getTG661JAPI().updateHostTempl(updateTemplData,
//                                    handler, templName);
//                        }
//                        tipTv.setText(MessageFormat.format("验证成功,验证分数：{0}", templScore));
//                    } else if (match1Arg1 == 2) {
//                        Integer match1Score = (Integer) msg.obj;
//                        tipTv.setText("特征比对（1:1）失败，因比对失败,仅Output的matchScore数据有效,分数："
//                                + match1Score);
//                        //显示图片
////                        getImgData(msg);
//                    } else if (match1Arg1 == 3) {
//                        tipTv.setText("特征比对（1:1）失败，因参数不合法,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//                    } else if (match1Arg1 == 4) {
//                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (match1Arg1 == 5) {
//                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (match1Arg1 == 6) {
//                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (match1Arg1 == 7) {
//                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (match1Arg1 == 8) {
//                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (match1Arg1 == 9) {
//                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (match1Arg1 == 10) {
//                        tipTv.setText("抓图超时");
//                    } else if (match1Arg1 == 11) {
//                        tipTv.setText("设备断开");
//                    } else if (match1Arg1 == 12) {
//                        tipTv.setText("操作取消");
//                    } else if (match1Arg1 == 13) {
//                        tipTv.setText("入参错误");
//                    } else if (match1Arg1 == -1) {
//                        tipTv.setText("模板解析失败，因参数不合法，Output数据无效");
//                    }
//                    ver1_1Btn.setClickable(true);
//                    break;
//                case TG661JAPI.FEATURE_COMPARE1_N:
//                    /**
//                     * 返回值：matchNArg
//                     * 1:特征比对（1：N）成功，Output数据有效
//                     * 2:特征比对（1：N）失败，仅Output的matchScore数据有效
//                     * 3:特征比对（1：N）失败，因参数不合法，Output数据无效
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
//                     * -5:该指静脉已经注册或模板名字已经注册
//                     */
//                    int matchNArg = msg.arg1;
//                    if (matchNArg == 1) {
//                        Bundle data = msg.getData();
//                        byte[] updateTemplData = data.getByteArray(TG661JAPI.COMPARE_N_TEMPL);//可更新的模板
//                        String templName = data.getString(TG661JAPI.COMPARE_NAME);
//                        int templScore = data.getInt(TG661JAPI.COMPARE_N_SCORE);
//                        if (autoUpdateStatus) {
//                            TG661JAPI.getTG661JAPI().updateHostTempl(updateTemplData, handler, templName);
//                        }
//                        tipTv.setText(MessageFormat.format("验证成功,验证分数：{0}", templScore));
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == 2) {
//
//                        Integer templScore = (Integer) msg.obj;
//                        tipTv.setText("特征比对（1：N）失败，仅Output的matchScore数据有效,分数：" + templScore);
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == 3) {
//                        tipTv.setText("特征比对（1：N）失败，因参数不合法，Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == 4) {
//                        tipTv.setText("特征提取失败,因证书路径错误,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == 5) {
//                        tipTv.setText("特征提取失败,因证书内容无效,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == 6) {
//                        tipTv.setText("特征提取失败,因证书内容过期,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == 7) {
//                        tipTv.setText("特征提取失败,因\"图像\"数据无效,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == 8) {
//                        tipTv.setText("特征提取失败,因\"图像\"质量较差,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == 9) {
//                        tipTv.setText("特征提取失败,因参数不合法,Output数据无效");
//                        //显示图片
////                        getImgData(msg);
//
//                    } else if (matchNArg == -1) {
//                        tipTv.setText("抓图超时");
//                    } else if (matchNArg == -2) {
//                        tipTv.setText("设备断开");
//                    } else if (matchNArg == -3) {
//                        tipTv.setText("操作取消");
//                    } else if (matchNArg == -4) {
//                        tipTv.setText("入参错误");
//                    } else if (matchNArg == -5) {
//                        tipTv.setText("该指静脉已经注册或模板名字已经注册");
//                    }
//                    ver1_nBtn.setClickable(true);
//                    break;
//                case TG661JAPI.DELETE_HOST_ALL_TEMPL:
//                    int deleteHostIDArg = msg.arg1;
//                    if (deleteHostIDArg == 1) {
//                        getTemplList();
//                        tipTv.setText("删除成功");
//                        templIDBehind.getText().clear();
//                        TG661JAPI.getTG661JAPI().setTemplModelType(templModelType);
//                        TG661JAPI.getTG661JAPI().getAP().play_deleteSuccess();
//                    } else if (deleteHostIDArg == -1) {
//                        tipTv.setText("删除失败");
//                        TG661JAPI.getTG661JAPI().getAP().play_deleteFail();
//                    }
//                    delAllHostTemplBtn.setClickable(true);
//                    break;
//                case TG661JAPI.DELETE_HOST_ID_TEMPL:
//                    int deleteHostAllArg = msg.arg1;
//                    if (deleteHostAllArg == 1) {
//                        getTemplList();
//                        templIDBehind.getText().clear();
//                        TG661JAPI.getTG661JAPI().setTemplModelType(templModelType);
//                        tipTv.setText("删除成功");
//                        TG661JAPI.getTG661JAPI().getAP().play_deleteSuccess();
//                    } else if (deleteHostAllArg == -1) {
//                        tipTv.setText("删除失败");
//                        TG661JAPI.getTG661JAPI().getAP().play_deleteFail();
//                    }
//                    delIDHostTemplBtn.setClickable(true);
//                    break;
//                case TG661JAPI.UPDATE_HOST_TEMPL:
//                    //更新主机模板的结果
//                    boolean updateStatus = (boolean) msg.obj;
//                    if (updateStatus) {
//                        tipTv.setText("模板更新成功");
//                    } else {
//                        tipTv.setText("模板更新失败");
//                    }
//                    break;
//                case TG661JAPI.TEMPL_SN:
//                    int templSnArg = msg.arg1;
//                    if (templSnArg == 1) {
//                        String sn = (String) msg.obj;
//                        tipTv.setText(MessageFormat.format("模板的序列号:{0}", sn));
//                    } else if (templSnArg == -1) {
//                        tipTv.setText("获取失败，参数错误，Output数据无效");
//                    } else if (templSnArg == -2) {
//                        tipTv.setText("模板名称为空/不存在，请检查");
//                    }
//                    getTemplSN.setClickable(true);
//                    break;
//                case TG661JAPI.TEMPL_FW:
//                    int fwArg = msg.arg1;
//                    if (fwArg == 1) {
//                        String fw = (String) msg.obj;
//                        tipTv.setText(MessageFormat.format("模板的固件号:{0}", fw));
//                    } else if (fwArg == -1) {
//                        tipTv.setText("获取失败，参数错误，Output数据无效");
//                    } else if (fwArg == -2) {
//                        tipTv.setText("模板名称为空/不存在，请检查");
//                    }
//                    getTemplFW.setClickable(true);
//                    break;
//                case TG661JAPI.TEMPL_TIME:
//                    int templTimeArg = msg.arg1;
//                    if (templTimeArg == 1) {
//                        String time = (String) msg.obj;
//                        tipTv.setText(MessageFormat.format("模板的注册时间:{0}", time));
//                    } else if (templTimeArg == -1) {
//                        tipTv.setText("获取失败，参数错误");
//                    } else if (templTimeArg == -2) {
//                        tipTv.setText("模板名称为空，请检查");
//                    }
//                    templTimeBtn.setClickable(true);
//                    break;
//                case TG661JAPI.TEMPL_FV_VERSION:
//                    int templVersionArg = msg.arg1;
//                    if (templVersionArg == 1) {
//                        String snVersion = (String) msg.obj;
//                        tipTv.setText(MessageFormat.format("模板的算法版本号:{0}", snVersion));
//                    } else if (templVersionArg == -1) {
//                        tipTv.setText("获取失败，参数错误");
//                    } else if (templVersionArg == -2) {
//                        tipTv.setText("模板名称为空，请检查");
//                    }
//                    getTemplAlgorVersionBtn.setClickable(true);
//                    break;
//                case TG661JAPI.WAIT_DIALOG:
//                    int typeDialog = msg.arg1;
//                    if (typeDialog == 1) {
//                        String tipStr = (String) msg.obj;
//                        waitDialog = AlertDialogUtil.Instance()
//                                .showWaitDialog(getActivity(), tipStr);
//                    } else if (typeDialog == -1) {
//                        if (waitDialog != null && waitDialog.isShowing()) {
//                            waitDialog.dismiss();
//                        }
//                    }
//                    break;
//                case TG661JAPI.OPEN_DEV:
//                    int openDevArg = msg.arg1;
//                    if (openDevArg == 1) {
//                        //初始化获取主机模板列表
//                        getTemplList();
//                        tipTv.setText("设备打开成功,工作模式设置成功");
//                    } else if (openDevArg == -1) {
//                        tipTv.setText("设备打开失败");
//                    }
//                    break;
//                case TG661JAPI.CLOSE_DEV:
//                    int closeDevArg = msg.arg1;
//                    if (closeDevArg == -1) {
//                        tipTv.setText("设备状态:设备关闭失败");
//                    } else if (closeDevArg == 1) {
//                        tipTv.setText("设备状态:设备关闭成功");
//                    } else if (closeDevArg == 2) {
//                        tipTv.setText("设备状态:设备已关闭");
//                    }
//                    break;
//            }
//        }
//    };
//
//    private AlertDialog waitDialog;
//    private EditText templIDBehind;
//    private TextView volumeTt, tipTv, devStatus;
//
//    private int templModelType = TG661JAPI.TEMPL_MODEL_6;//默认为6模板模式
//    private boolean autoUpdateStatus = false;//自动更新模板
//
//    public void setDevStatus(String status) {
//        devStatus.setText(status);
//        tipTv.setText(status);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_behind, container, false);
//        //后比
//        Button closeDevBtn = view.findViewById(R.id.closeDevBtn);
//        Button openDevBtn = view.findViewById(R.id.openDevBtn);
//
//        templIDBehind = view.findViewById(R.id.templIDBehind);
//        tipTv = view.findViewById(R.id.tipTv);
//        devStatus = view.findViewById(R.id.devStatus);
//        registerBtnBehind = view.findViewById(R.id.registerBtnBehind);
//        RadioGroup templSumModel = view.findViewById(R.id.templSumModel);
//        final RadioButton templ3Rb = view.findViewById(R.id.templ3Rb);
//        final RadioButton templ6Rb = view.findViewById(R.id.templ6Rb);
//        ver1_1Btn = view.findViewById(R.id.ver1_1Btn);
//        ver1_nBtn = view.findViewById(R.id.ver1_NBtn);
//        getTemplFW = view.findViewById(R.id.getTemplFW);
//        getTemplSN = view.findViewById(R.id.getTemplSN);
//        templTimeBtn = view.findViewById(R.id.templTimeBtn);
//        voiceIncreaceBtn = view.findViewById(R.id.voiceIncreaceBtn);
//        delIDHostTemplBtn = view.findViewById(R.id.delIDHostTemplBtn);
//        delAllHostTemplBtn = view.findViewById(R.id.delAllHostTemplBtn);
//        volumeTt = view.findViewById(R.id.volumeTt);
//        voiceDecreaceBtn = view.findViewById(R.id.voiceDecreaceBtn);
//        //测试
//        iv = view.findViewById(R.id.iv);
//        getImg = view.findViewById(R.id.getImg);
//        getImgFeature = view.findViewById(R.id.getImgFeature);
//        getFeatureTempl = view.findViewById(R.id.getFeatureTempl);
//        getMatchTempl = view.findViewById(R.id.getMatchTempl);
//        get1_1 = view.findViewById(R.id.get1_1);
//        get1_N = view.findViewById(R.id.get1_N);
//        clearEt = view.findViewById(R.id.clearEt);
//
//        getTemplAlgorVersionBtn = view.findViewById(R.id.getTemplAlgorVersionBtn);
//        CheckBox autoUpdateTempl = view.findViewById(R.id.autoUpdateTempl);
//        RecyclerView templFileRv = view.findViewById(R.id.templFileRv);
//
//        templFileRv.setLayoutManager(new LinearLayoutManager(getActivity(),
//                OrientationHelper.VERTICAL, false));
//        templAdapter = new TemplAdapter(getActivity());
//        templAdapter.setItemClick(this);
//        templFileRv.setAdapter(templAdapter);
//
//        //获取当前音量
//        String currentVolume = TG661JAPI.getTG661JAPI().getCurrentVolume(handler);
//        volumeTt.setText(currentVolume);
//        //初始化获取当前特征模式下的模板列表
//        getTemplList();
//
//        openDevBtn.setOnClickListener(this);
//        closeDevBtn.setOnClickListener(this);
//
//        registerBtnBehind.setOnClickListener(this);
//        ver1_1Btn.setOnClickListener(this);
//        ver1_nBtn.setOnClickListener(this);
//        getTemplAlgorVersionBtn.setOnClickListener(this);
//        getTemplFW.setOnClickListener(this);
//        getTemplSN.setOnClickListener(this);
//        templTimeBtn.setOnClickListener(this);
//        voiceIncreaceBtn.setOnClickListener(this);
//        voiceDecreaceBtn.setOnClickListener(this);
//        delIDHostTemplBtn.setOnClickListener(this);
//        delAllHostTemplBtn.setOnClickListener(this);
//
//        getImg.setOnClickListener(this);
//        getImgFeature.setOnClickListener(this);
//        getFeatureTempl.setOnClickListener(this);
//        getMatchTempl.setOnClickListener(this);
//        get1_1.setOnClickListener(this);
//        get1_N.setOnClickListener(this);
//        clearEt.setOnClickListener(this);
//
//        templIDBehind.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (templIDBehind.getText().toString().trim().length() > 0) {
//                    clearEt.setVisibility(View.VISIBLE);
//                } else {
//                    clearEt.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        //3特征模板和6特征模板切换
//        templSumModel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                devStatu = checkDevStatus();
//                if (devStatu) {
//                    templIDBehind.getText().clear();
//                    if (i == templ3Rb.getId()) {
//                        templModelType = TG661JAPI.TEMPL_MODEL_3;
//                        TG661JAPI.getTG661JAPI().setTemplModelType(TG661JAPI.TEMPL_MODEL_3);
//                    } else if (i == templ6Rb.getId()) {
//                        templModelType = TG661JAPI.TEMPL_MODEL_6;
//                        TG661JAPI.getTG661JAPI().setTemplModelType(TG661JAPI.TEMPL_MODEL_6);
//                    }
//                    getTemplList();
//                }
//            }
//        });
//        autoUpdateStatus = autoUpdateTempl.isChecked();
//        //自动更新
//        autoUpdateTempl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                autoUpdateStatus = b;
//            }
//        });
//
//        return view;
//    }
//
//    public void getTemplList() {
//        ArrayList<String> aimFileList = TG661JAPI.getTG661JAPI().getAimFileList();
//        templAdapter.clearData();
//        templAdapter.addData(aimFileList);
//    }
//
//    private boolean checkDevStatus() {
//        boolean devOpen = TG661JAPI.getTG661JAPI().isDevOpen();
//        if (!devOpen) {
//            ToastUtil.toast(getActivity(), "请先开启设备");
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    boolean isGetImg = false;
//    private TG661JAPI tg661JAPI = TG661JAPI.getTG661JAPI();
//    private int workType = TG661JAPI.WORK_BEHIND;//设备工作模式--》前比
//    private boolean devOpen;
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.openDevBtn:
//                devOpen = tg661JAPI.isDevOpen();
//                if (!devOpen) {
//                    tg661JAPI.openDev(handler, getActivity(), workType, templModelType);
//                } else {
//                    ToastUtil.toast(getActivity(), "设备已经开启");
//                }
//                break;
//            case R.id.closeDevBtn:
//                devOpen = tg661JAPI.isDevOpen();
//                if (devOpen) {
//                    tg661JAPI.closeDev(handler);
//                } else {
//                    ToastUtil.toast(getActivity(), "设备已经关闭");
//                }
//                break;
//            ////后比
//            case R.id.registerBtnBehind:
//                //注册
//                String templID = templIDBehind.getText().toString().trim();
//                if (TextUtils.isEmpty(templID)) {
//                    ToastUtil.toast(getActivity(), "注册的模板ID不能为空");
//                } else {
//                    //检测注册名只包含字母或数字或中文
//                    boolean b = RegularUtil.strContainsNumOrAlpOrChin(templID);
//                    if (b) {
//                        devStatu = checkDevStatus();
//                        if (devStatu) {
////                        if (!isGetImg) {
//                            TG661JAPI.getTG661JAPI().extractFeatureRegister(handler, templModelType, templID);
////                            registerBtnBehind.setClickable(false);
//                            registerBtnBehind.setText("取消注册");
//                            isGetImg = true;
////                        } else {
////                            TG661JAPI.getTG661JAPI().behindCancelRegister(handler);
////                            TG661JAPI.getTG661JAPI().cancelDevImg(handler);
////                            registerBtnBehind.setText("注册");
////                            isGetImg = false;
////                        }
//                        }
//                    } else {
//                        ToastUtil.toast(getActivity(), "注册的模板名称不可包含数字/字母/中文以外的字符");
//                    }
//                }
//                break;
//            case R.id.ver1_1Btn:
//                //1:1验证
//                String templName = templIDBehind.getText().toString().trim();
//                if (TextUtils.isEmpty(templName)) {
//                    ToastUtil.toast(getActivity(), "请选择要比对的模板文件");
//                } else {
//                    devStatu = checkDevStatus();
//                    if (devStatu) {
//                        TG661JAPI.getTG661JAPI().featureCompare1_1(handler, templName);
//                        ver1_1Btn.setClickable(false);
//                    }
//                }
//                break;
//            case R.id.ver1_NBtn:
//                //1:N验证
//                devStatu = checkDevStatus();
//                if (devStatu) {
//                    TG661JAPI.getTG661JAPI().featureCompare1_N(handler);
//                    ver1_nBtn.setClickable(false);
//                }
//                break;
//            case R.id.getTemplSN:
//                String snTemplName = templIDBehind.getText().toString().trim();
//                if (TextUtils.isEmpty(snTemplName)) {
//                    ToastUtil.toast(getActivity(), "模板名字不能为空");
//                } else {
//                    devStatu = checkDevStatus();
//                    if (devStatu) {
//                        TG661JAPI.getTG661JAPI().getTemplSN(handler, snTemplName);
//                        getTemplSN.setClickable(false);
//                    }
//                }
//                break;
//            case R.id.getTemplFW:
//                String fwTemplName = templIDBehind.getText().toString().trim();
//                if (TextUtils.isEmpty(fwTemplName)) {
//                    ToastUtil.toast(getActivity(), "模板名字不能为空");
//                } else {
//                    devStatu = checkDevStatus();
//                    if (devStatu) {
//                        TG661JAPI.getTG661JAPI().getTemplFW(handler, fwTemplName);
//                        getTemplFW.setClickable(false);
//                    }
//                }
//                break;
//            case R.id.templTimeBtn:
//                String timeTemplName = templIDBehind.getText().toString().trim();
//                if (TextUtils.isEmpty(timeTemplName)) {
//                    ToastUtil.toast(getActivity(), "模板名字不能为空");
//                } else {
//                    devStatu = checkDevStatus();
//                    if (devStatu) {
//                        TG661JAPI.getTG661JAPI().getTemplTime(handler, timeTemplName);
//                        templTimeBtn.setClickable(false);
//                    }
//                }
//                break;
//            case R.id.getTemplAlgorVersionBtn:
//                String fvVersionTemplName = templIDBehind.getText().toString().trim();
//                if (TextUtils.isEmpty(fvVersionTemplName)) {
//                    ToastUtil.toast(getActivity(), "模板名字不能为空");
//                } else {
//                    devStatu = checkDevStatus();
//                    if (devStatu) {
//                        TG661JAPI.getTG661JAPI().getTemplVersion(handler, fvVersionTemplName);
//                        getTemplAlgorVersionBtn.setClickable(false);
//                    }
//                }
//                break;
//            case R.id.voiceIncreaceBtn:
//                //音量加
//                boolean increaseVolume = TG661JAPI.getTG661JAPI().increaseVolume(handler);
//                voiceIncreaceBtn.setClickable(false);
//                if (increaseVolume) {
//                    String currentVolume = TG661JAPI.getTG661JAPI().getCurrentVolume(handler);
//                    volumeTt.setText(currentVolume);
//                    tipTv.setText("音量增大成功");
//                } else {
//                    tipTv.setText("已经是最大音量");
//                }
//                voiceIncreaceBtn.setClickable(true);
//                break;
//            case R.id.voiceDecreaceBtn:
//                //音量减
//                boolean descreaseVolume = TG661JAPI.getTG661JAPI().descreaseVolume(handler);
//                voiceDecreaceBtn.setClickable(false);
//                if (descreaseVolume) {
//                    String currentVolume = TG661JAPI.getTG661JAPI().getCurrentVolume(handler);
//                    volumeTt.setText(currentVolume);
//                    tipTv.setText("音量减小成功");
//                } else {
//                    tipTv.setText("已经是最小音量");
//                }
//                voiceDecreaceBtn.setClickable(true);
//                break;
//            case R.id.delIDHostTemplBtn:
//                //删除指定ID的模板
//                String templNameID = templIDBehind.getText().toString().trim();
//                if (TextUtils.isEmpty(templNameID)) {
//                    ToastUtil.toast(getActivity(), "模板名字不能为空");
//                } else {
//                    devStatu = checkDevStatus();
//                    if (devStatu) {
//                        TG661JAPI.getTG661JAPI().deleteHostIdTempl(handler, templNameID);
//                        delIDHostTemplBtn.setClickable(false);
//                    }
//                }
//                break;
//            case R.id.delAllHostTemplBtn:
//                //删除主机中的所有模板
//                devStatu = checkDevStatus();
//                if (devStatu) {
//                    TG661JAPI.getTG661JAPI().deleteHostAllTempl(handler);
//                    delAllHostTemplBtn.setClickable(false);
//                }
//                break;
//
//            //测试
//            case R.id.getImg://抓图
//                if (!isGetImg) {
//                    devStatu = checkDevStatus();
//                    if (devStatu) {
//                        TG661JAPI.getTG661JAPI().getDevImg(handler);
//                        getImg.setText("取消抓图");
//                        isGetImg = true;
//                    }
//                } else {
//                    devStatu = checkDevStatus();
//                    if (devStatu) {
//                        TG661JAPI.getTG661JAPI().cancelDevImg(handler);
//                        getImg.setText("抓图");
//                        isGetImg = false;
//                    }
//                }
//                break;
//            case R.id.getImgFeature://从图像提取特征
//                if (imgData != null) {
//                    if (imgDatas != null && imgDatas.size() == 2) {
//                        TG661JAPI.getTG661JAPI().fusionFeature(handler, imgDatas);
//                    }
//                }
//                break;
//            case R.id.clearEt:
//                templIDBehind.getText().clear();
//                break;
//            case R.id.getFeatureTempl://特征融合成为模板
//
//                break;
//            case R.id.getMatchTempl://特征模板转成比对模板
//
//                break;
//            case R.id.get1_1://1:1
//
//                break;
//            case R.id.get1_N://1:N
//
//                break;
//        }
//    }
//
//    @Override
//    public void commiteInfo(String info, int flag) {
//
//    }
//
//    @Override
//    public void showTip(String tip) {
//
//    }
//
//    @Override
//    public void itemSelectFile(String datFileName) {
//        templIDBehind.setText(datFileName);
//    }
//
//    @Override
//    public void delTempl(String datFileName) {
//        TG661JAPI.getTG661JAPI().deleteHostIdTempl(handler, datFileName);
//    }
//}
