package com.sd.tgfinger.tgApi.bigFeature;

import android.os.Environment;

import java.io.File;

public final class TGB2Constant {

    //启动目标service的Action
    public static final String DEV_SERVICE_ACTION = "com.example.mylibrary.DevService.action";
    public static final String STATUS = "status";
    public static final String EXE_CMD = "exe_cmd";
    public static final String FINGER_DATA = "finger_data";//模板数据
    //存储数据的根文件夹路径
    public static final String TG_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + (File.separator + "TG_TEMPLATE");
    //后比模板的文件夹
    public static final String BEHIND_DAT_DIR = TG_DIR_PATH + File.separator + "BehindTemplate";
    //后比的3，6模板路径
    public static final String BEHIND_TEMPL_3_PATH = BEHIND_DAT_DIR + File.separator + "TEMPL_3";
    public static final String BEHIND_TEMPL_6_PATH = BEHIND_DAT_DIR + File.separator + "TEMPL_6";
    //证书所在的文件夹路径
    public static final String LICENSE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "TG_TEMPLATE" + File.separator + "TG_VEIN";
    //证书所在的路径
    public static final String LICENSE_PATH = LICENSE_DIR + File.separator + "license.dat";
    //日志的路径
    public static final String LOG_DIR = TG_DIR_PATH + File.separator + "Log";
    //图片存储的路径
    public static final String IMG_PATH = TG_DIR_PATH + File.separator + "IMGS";
    //模拟外部数据的存储路径---》后续应该用数据库替代
    public static final String MONI_EXTER_3_PATH = BEHIND_DAT_DIR + File.separator + "EX3";
    public static final String MONI_EXTER_6_PATH = BEHIND_DAT_DIR + File.separator + "EX6";
    //3模板
    public static final int TEMPL_MODEL_3 = 3;
    //6模板
    public static final int TEMPL_MODEL_6 = 6;
    //后比模式
    public static final int WORK_BEHIND = 1;

    public static final int RECEIVE_MESSAGE_CODE = 0x0002;
    public static final int SEND_MESSAGE_CODE = 0x0001;
    public static final int IMG_SIZE = 500 * 200 + 208;
    public static final int IMG_W = 500;
    public static final int IMG_H = 200;
    public static final int FEATURE_SIZE =
            6016;//这个是大特征的大小
    //UUID的byte占位大小
    public static final int UUID_SIZE = 33;
    //算法版本
    public static final int FV_VERSION = 4;
    //时间值的占位大小
    public static final int TIME_SIZE = 15;

    //临时加的图片大小
    public static final int T_SIZE = 1024 * 500;
    public static final int GET_IMG_OUT_TIME = 10;//默认设置抓图超时的时间为10S
    //特征模板
    public static final int PERFECT_FEATURE_3 =
            17632;//大特征的大小
    public static final int PERFECT_FEATURE_6 =
            35008;//大特征的大小

    //可比对的特征大小
    public static final int WAIT_COMPARE_FEATURE_6 = 34784;//6特征
    public static final int WAIT_COMPARE_FEATURE_3 = 17408;//3特征

    public static final String TG661JB = "TG661JB";
    public static final String TG661JBehind = "TG661JBehind";
    public static final String TG661JFront = "TG661JFront";
}
