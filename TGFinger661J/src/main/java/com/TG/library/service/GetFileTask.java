package com.TG.library.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.TG.library.api.TG661JBAPI;
import com.TG.library.pojos.MatchN;
import com.TG.library.utils.FileUtil;
import com.sun.jna.ptr.IntByReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created By pq
 * on 2019/5/20
 */
public class GetFileTask implements Callable<MatchN> {

    private File[] myFile;
    private int templModelType;
    private List<byte[]> myList;

    private byte[] match1_NImgData;//获取的图片数据
    private int tgGetDevImageMatchNRes;//获取到的图片长度
    private byte[] match1_NFeature;//可比对的图片特征


    private String aimPath;
    //    private Handler handler;
    private Message myMessage;
    private Bundle myBundle;
    private boolean sImg = false;

    public GetFileTask(File[] files, int modelType, String aimPath, /*Handler handler,*/ Message message,
                       Bundle bundle, byte[] match1_NImgData, int tgGetDevImageMatchNRes
            , byte[] match1_NFeature) {
        this.myFile = files;
        this.templModelType = modelType;
        this.aimPath = aimPath;
//        this.handler = handler;
        this.myMessage = message;
        this.myBundle = bundle;
        this.match1_NImgData = match1_NImgData;
        this.tgGetDevImageMatchNRes = tgGetDevImageMatchNRes;
        this.match1_NFeature = match1_NFeature;
    }

    private byte[] cellFingerDatas;
    private byte[] allNMatchData;
    private int countN;
    private int imgLength;
    private byte[] mImgData;
    private byte[] updateTempl;
    private int count;

    public GetFileTask(byte[] newFingerData, byte[] allNMatchData, int N, String aimPath,
                       int imgLength, byte[] imgData, byte[] updateTempl,
            /*Handler handler,*/ Message message,
                       Bundle bundle, boolean imgSave, int count) {
        this.cellFingerDatas = newFingerData;
        this.allNMatchData = allNMatchData;
        this.countN = N;
        this.aimPath = aimPath;
        this.imgLength = imgLength;
        this.mImgData = imgData;
        this.updateTempl = updateTempl;
//        this.handler = handler;
        this.myMessage = message;
        this.myBundle = bundle;
        this.sImg = imgSave;
        this.count = count;
    }

    @Override
    public MatchN call() {
        MatchN matchN = matchFeature2(cellFingerDatas, allNMatchData, countN,
                aimPath, imgLength, mImgData, updateTempl, count);
//            finger1N(myList, aimPath, handler, myMessage, myBundle);
        return matchN;
    }


//    private MatchN match1Method() {
//        Log.d("===HHH", "   一回合文件的数量：   " + myFile.length);
//        long startTime = System.currentTimeMillis();
//        Log.d("===HHH", "  一回合的起始时间： " + startTime);
//        MatchN matchN = null;
//        if (myFile != null && myFile.length > 0) {
//            myList = new ArrayList<>();
//            byte[] bytes = null;
//            for (File file : myFile) {
//
//                if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                    bytes = new byte[TG661JBehindAPI.PERFECT_FEATURE_3];
//                } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                    bytes = new byte[TG661JBehindAPI.PERFECT_FEATURE_6];
//                }
////                FileUtil.readFileToArray(file, bytes);
//                FileUtil.readFile(file, bytes);
//                myList.add(bytes);
//            }
//            Log.d("===HHH", "   一个模板的大小：" + bytes.length);
//
//            long endTime = System.currentTimeMillis();
//            Log.d("===HHH", "  一回合的结束时间： " + endTime);
//
//            Log.d("===HHH", "   一回合完成的时间差  ：" + (endTime - startTime));
//            //得到了原始的模板列表-》可比对的模板数据
//            byte[] waitMatchTemplData = transformMatchTempl(myList);
//            //将已有的特征与模板进行比对
//            matchN = matchFeature1(match1_NFeature, waitMatchTemplData, myList, aimPath,
//                    myMessage, tgGetDevImageMatchNRes, match1_NImgData);
//            long matchCompleteTime = System.currentTimeMillis();
//            Log.d("===HHH", "   文件读取完成后，比对所化的时间：" + (matchCompleteTime - endTime));
//            updateTempl = null;
//        }
//        return matchN;
//    }


    //将普通模板转换成比对模板
//    private byte[] transformMatchTempl(List<byte[]> allTemplByteList) {
//        byte[] allWaitTempl = null;
//        ArrayList<byte[]> allMatchTemplList = null;
//        //转成比对模板
//        if (allTemplByteList != null && allTemplByteList.size() > 0) {
//            allMatchTemplList = new ArrayList<>();
//            for (byte[] bytes : allTemplByteList) {
//                byte[] matchTempll_N = null;
//
//                if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                    matchTempll_N = new byte[TG661JBehindAPI.WAIT_COMPARE_FEATURE_3];
//                } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                    matchTempll_N = new byte[TG661JBehindAPI.WAIT_COMPARE_FEATURE_6];
//                }
//                int tgTmplToMatchTmpl1_NRes = TG661JBehindAPI.getTG661JBehindAPI().getTGFV().
//                        TGTmplToMatchTmpl(bytes, matchTempll_N);
//                if (tgTmplToMatchTmpl1_NRes == 0) {
//                    allMatchTemplList.add(matchTempll_N);
//                } else if (tgTmplToMatchTmpl1_NRes == -1) {
//                    int k = 0;
//                    boolean continueMatch = true;
//                    while (continueMatch) {
//                        if (k < 3) {
//                            tgTmplToMatchTmpl1_NRes = TG661JBehindAPI.getTG661JBehindAPI().getTGFV().
//                                    TGTmplToMatchTmpl(bytes, matchTempll_N);
//                            k++;
//                            if (tgTmplToMatchTmpl1_NRes == 0) {
//                                allMatchTemplList.add(matchTempll_N);
//                                k = 0;
//                                continueMatch = false;
//                            }
//                        }
//                    }
//                }
//            }
//            if (allMatchTemplList.size() > 0) {
//                if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                    allWaitTempl = new byte[TG661JBehindAPI.WAIT_COMPARE_FEATURE_3
//                            * allMatchTemplList.size()];
//                } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                    allWaitTempl = new byte[TG661JBehindAPI.WAIT_COMPARE_FEATURE_6
//                            * allMatchTemplList.size()];
//                }
//                for (int i = 0; i < allMatchTemplList.size(); i++) {
//                    int i1 = 0;
//                    if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                        i1 = TG661JBehindAPI.WAIT_COMPARE_FEATURE_3 * i;
//                    } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                        i1 = TG661JBehindAPI.WAIT_COMPARE_FEATURE_6 * i;
//                    }
//                    byte[] bytes = allMatchTemplList.get(i);
//                    System.arraycopy(bytes, 0, allWaitTempl, i1, bytes.length);
//                }
//            }
//        }
//        return allWaitTempl;
//    }

    //将已有特征与模板比对
//    private MatchN matchFeature1(byte[] match1_NFeature, byte[] allWaitTempl, List<byte[]> allMatchTemplList
//            , String templsPath, /*Handler handler, */Message matchNMsg/*, Bundle matchNBundle*/
//            , int tgGetDevImageMatchNRes, byte[] match1_NImgData) {
//        MatchN matchN = null;
//        if (allWaitTempl != null) {
//            IntByReference intB1 = new IntByReference();
//            IntByReference intB2 = new IntByReference();
//            byte[] uuId = new byte[33];
//
//            if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                updateTempl = new byte[TG661JBehindAPI.PERFECT_FEATURE_3];
//            } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                updateTempl = new byte[TG661JBehindAPI.PERFECT_FEATURE_6];
//            }
//            int tgFeatureMatchTmpl1NRes = TG661JBehindAPI.getTG661JBehindAPI()
//                    .getTGFV().TGFeatureMatchTmpl1N(match1_NFeature,
//                            allWaitTempl, allMatchTemplList.size(), intB1, uuId,
//                            intB2, updateTempl);
//            if (tgFeatureMatchTmpl1NRes == 0) {
//
//                int templIndex = intB1.getValue();//模板的指针位置
//                int templScore = intB2.getValue();//验证的分数
//                //根据返回的指针获取主机中模板文件的名字
//                String fileName = FileUtil.getFileName(templsPath, templIndex - 1);
////                matchNMsg.arg1 = 1;
////                matchNBundle.putByteArray(TG661JBehindAPI.COMPARE_N_TEMPL, updateTempl);
////                matchNBundle.putString(TG661JBehindAPI.COMPARE_NAME, fileName);
////                matchNBundle.putInt(TG661JBehindAPI.COMPARE_N_SCORE, templScore);
//
//                //将数据结果存储到对象
//                matchN = new MatchN(1, templIndex, templScore, fileName, updateTempl);
//
//                //传出抓取图片的数据
//                if (sImg) {
////                    matchNBundle.putInt("imgLength", tgGetDevImageMatchNRes);
////                    matchNBundle.putByteArray("imgData", match1_NImgData);
////                                            img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
//                    //存储图片
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(fileName, match1_NImgData,
//                            tgGetDevImageMatchNRes);
//                }
////                matchNMsg.setData(matchNBundle);
//
//            } else if (tgFeatureMatchTmpl1NRes == 8) {
//                int templIndex = intB1.getValue();//模板的指针位置
//                int templScore = intB2.getValue();//验证的分数
//
////                matchNMsg.arg1 = 2;
////                matchNBundle.putInt(TG661JBehindAPI.COMPARE_N_SCORE, templScore);
//
//                //将数据结果存储到对象
//                matchN = new MatchN(2, templIndex, templScore, null, null);
//
//                //传出抓取图片的数据
//                if (sImg) {
////                    matchNBundle.putInt("imgLength", tgGetDevImageMatchNRes);
////                    matchNBundle.putByteArray("imgData", match1_NImgData);
////                                            img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
////                matchNMsg.setData(matchNBundle);
//            } else if (tgFeatureMatchTmpl1NRes == -1) {
//
////                matchNMsg.arg1 = 3;
//
//                //将数据结果存储到对象
//                matchN = new MatchN(3, -5, -5, null, null);
//
//                //传出抓取图片的数据
//                if (sImg) {
//                    TG661JBehindAPI.getTG661JBehindAPI().img(matchNMsg, match1_NImgData,
//                            tgGetDevImageMatchNRes);
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
//            }
//
//        }
//        return matchN;
//    }

//    private MatchN match2(){
//
//    }

    private int c = 0;

    //将已有特征与模板比对
    private MatchN matchFeature2(byte[] match1_NFeature, byte[] allWaitTempl, int countN
            , String templsPath, int tgGetDevImageMatchNRes, byte[] match1_NImgData,
                                 byte[] updateTempl, int count) {
        MatchN matchN = null;
        if (allWaitTempl != null) {
            c++;
            IntByReference intB1 = new IntByReference();
            IntByReference intB2 = new IntByReference();
            byte[] uuId = new byte[33];
            int tgFeatureMatchTmpl1NRes = TG661JBAPI.getTg661JBAPI()
                    .getTGFV().TGFeatureMatchTmpl1N(match1_NFeature,
                            allWaitTempl, countN, intB1, uuId,
                            intB2, updateTempl);
            Log.d("===LLL", "  验证的结果：" + tgFeatureMatchTmpl1NRes);
            if (tgFeatureMatchTmpl1NRes == 0) {
                int templIndex = intB1.getValue();//模板的指针位置
                int templScore = intB2.getValue();//验证的分数
                //根据返回的指针获取主机中模板文件的名字
                String fileName = FileUtil.getFileName(templsPath, templIndex - 1);
                //将数据结果存储到对象
                matchN = new MatchN(1, templIndex, templScore, fileName, updateTempl);
                //传出抓取图片的数据
                TG661JBAPI.getTg661JBAPI().tgSaveImg(myMessage, myBundle, fileName,
                        match1_NImgData, tgGetDevImageMatchNRes);
            } else if (tgFeatureMatchTmpl1NRes == 8) {
                if (c == count) {
                    int templIndex = intB1.getValue();//模板的指针位置
                    int templScore = intB2.getValue();//验证的分数
                    //将数据结果存储到对象
                    matchN = new MatchN(2, templIndex, templScore, null, null);
                }
                //传出抓取图片的数据
//                if (sImg) {
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
            } else if (tgFeatureMatchTmpl1NRes == -1) {
                if (c == count) {
                    //将数据结果存储到对象
                    matchN = new MatchN(3, -5,
                            -5, null, null);
                }
                //传出抓取图片的数据
//                if (sImg) {
//                    TG661JBehindAPI.getTG661JBehindAPI().img(matchNMsg, match1_NImgData,
//                            tgGetDevImageMatchNRes);
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
            }

        }
        return matchN;
    }

//    public void finger1N(List<byte[]> allTemplByteList, String templsPath, Handler handler,
//                         Message matchNMsg, Bundle matchNBundle) {
//        //获取模板的所有地址
////        String templsPath = getAimPath();
////        Message matchNMsg = handler.obtainMessage();
////        matchNMsg.what = FEATURE_COMPARE1_N;
////        Bundle matchNBundle = new Bundle();
////        //读取所有文件模板
////        ArrayList<byte[]> allTemplByteList = readAllTempl(templsPath);
//
//        byte[] allWaitTempl = null;
//        ArrayList<byte[]> allMatchTemplList = null;
//        //转成比对模板
//        if (allTemplByteList != null && allTemplByteList.size() > 0) {
//            allMatchTemplList = new ArrayList<>();
//            for (byte[] bytes : allTemplByteList) {
//                byte[] matchTempll_N = null;
//                if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                    matchTempll_N = new byte[TG661JBehindAPI.WAIT_COMPARE_FEATURE_3];
//                } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                    matchTempll_N = new byte[TG661JBehindAPI.WAIT_COMPARE_FEATURE_6];
//                }
//                int tgTmplToMatchTmpl1_NRes = TG661JBehindAPI.getTG661JBehindAPI().getTGFV().
//                        TGTmplToMatchTmpl(bytes, matchTempll_N);
//                if (tgTmplToMatchTmpl1_NRes == 0) {
//                    allMatchTemplList.add(matchTempll_N);
//                } else if (tgTmplToMatchTmpl1_NRes == -1) {
//                    int k = 0;
//                    boolean continueMatch = true;
//                    while (continueMatch) {
//                        if (k < 3) {
//                            tgTmplToMatchTmpl1_NRes = TG661JBehindAPI.getTG661JBehindAPI().getTGFV().
//                                    TGTmplToMatchTmpl(bytes, matchTempll_N);
//                            k++;
//                            if (tgTmplToMatchTmpl1_NRes == 0) {
//                                allMatchTemplList.add(matchTempll_N);
//                                k = 0;
//                                continueMatch = false;
//                            }
//                        }
//                    }
//                }
//            }
//            if (allMatchTemplList.size() > 0) {
//                if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                    allWaitTempl = new byte[TG661JBehindAPI.WAIT_COMPARE_FEATURE_3
//                            * allMatchTemplList.size()];
//                } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                    allWaitTempl = new byte[TG661JBehindAPI.WAIT_COMPARE_FEATURE_6
//                            * allMatchTemplList.size()];
//                }
//                for (int i = 0; i < allMatchTemplList.size(); i++) {
//                    int i1 = 0;
//                    if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                        i1 = TG661JBehindAPI.WAIT_COMPARE_FEATURE_3 * i;
//                    } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                        i1 = TG661JBehindAPI.WAIT_COMPARE_FEATURE_6 * i;
//                    }
//                    byte[] bytes = allMatchTemplList.get(i);
//                    System.arraycopy(bytes, 0, allWaitTempl, i1, bytes.length);
//                }
//            }
//        }
//        //用转换好的模板等待比对，--》抓取图片
//        TG661JBehindAPI.getTG661JBehindAPI().getAP().play_inputDownGently();
////                        byte[] match1_NImgData = new byte[IMG_SIZE];
//        byte[] match1_NImgData = new byte[TG661JBehindAPI.IMG_SIZE + TG661JBehindAPI.T_SIZE];
//        match1_NImgData[0] = ((byte) 0xfe);
//        int tgGetDevImageMatchNRes = TG661JBehindAPI.getTG661JBehindAPI().getTG661()
//                .TGGetDevImage(match1_NImgData, TG661JBehindAPI.GET_IMG_OUT_TIME);
//        if (tgGetDevImageMatchNRes >= 0) {
//            //提取特征
//            byte[] match1_NFeature = new byte[TG661JBehindAPI.FEATURE_SIZE];
//            int tgImgExtractFeatureVerifyNRes = TG661JBehindAPI.getTG661JBehindAPI()
//                    .getTGFV().TGImgExtractFeatureVerify(match1_NImgData, 500,
//                            200, match1_NFeature);
//            if (tgImgExtractFeatureVerifyNRes == 0) {
//                if (allWaitTempl != null) {
//                    IntByReference intB1 = new IntByReference();
//                    IntByReference intB2 = new IntByReference();
//                    byte[] uuId = new byte[33];
//                    byte[] updateTempl = null;
//                    if (templModelType == TG661JBehindAPI.TEMPL_MODEL_3) {
//                        updateTempl = new byte[TG661JBehindAPI.PERFECT_FEATURE_3];
//                    } else if (templModelType == TG661JBehindAPI.TEMPL_MODEL_6) {
//                        updateTempl = new byte[TG661JBehindAPI.PERFECT_FEATURE_6];
//                    }
//                    int tgFeatureMatchTmpl1NRes = TG661JBehindAPI.getTG661JBehindAPI()
//                            .getTGFV().TGFeatureMatchTmpl1N(match1_NFeature,
//                                    allWaitTempl, allMatchTemplList.size(), intB1, uuId,
//                                    intB2, updateTempl);
//                    if (tgFeatureMatchTmpl1NRes == 0) {
//                        TG661JBehindAPI.getTG661JBehindAPI()
//                                .getAP().play_verifySuccess();
//                        int templIndex = intB1.getValue();//模板的指针位置
//                        int templScore = intB2.getValue();//验证的分数
//                        //根据返回的指针获取主机中模板文件的名字
//                        String fileName = FileUtil.getFileName(templsPath, templIndex - 1);
//                        matchNMsg.arg1 = 1;
//                        matchNBundle.putByteArray(TG661JBehindAPI.COMPARE_N_TEMPL, updateTempl);
//                        matchNBundle.putString(TG661JBehindAPI.COMPARE_NAME, fileName);
//                        matchNBundle.putInt(TG661JBehindAPI.COMPARE_N_SCORE, templScore);
//                        //传出抓取图片的数据
//                        if (sImg) {
//                            matchNBundle.putInt("imgLength", tgGetDevImageMatchNRes);
//                            matchNBundle.putByteArray("imgData", match1_NImgData);
////                                            img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
//                            //存储图片
//                            TG661JBehindAPI.getTG661JBehindAPI().saveImg(fileName, match1_NImgData,
//                                    tgGetDevImageMatchNRes);
//                        }
//                        matchNMsg.setData(matchNBundle);
//                    } else if (tgFeatureMatchTmpl1NRes == 8) {
//                        int templIndex = intB1.getValue();//模板的指针位置
//                        int templScore = intB2.getValue();//验证的分数
//                        TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//                        matchNMsg.arg1 = 2;
//                        matchNBundle.putInt(TG661JBehindAPI.COMPARE_N_SCORE, templScore);
//                        //传出抓取图片的数据
//                        if (sImg) {
//                            matchNBundle.putInt("imgLength", tgGetDevImageMatchNRes);
//                            matchNBundle.putByteArray("imgData", match1_NImgData);
////                                            img(matchNMsg, match1_NImgData, tgGetDevImageMatchNRes);
//                            //存储图片
//                            long l = System.currentTimeMillis();
//                            TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                                    match1_NImgData, tgGetDevImageMatchNRes);
//                        }
//                        matchNMsg.setData(matchNBundle);
//                    } else if (tgFeatureMatchTmpl1NRes == -1) {
//                        TG661JBehindAPI.getTG661JBehindAPI().getAP().play_time_out();
//                        matchNMsg.arg1 = 3;
//                        //传出抓取图片的数据
//                        if (sImg) {
//                            TG661JBehindAPI.getTG661JBehindAPI().img(matchNMsg, match1_NImgData,
//                                    tgGetDevImageMatchNRes);
//                            //存储图片
//                            long l = System.currentTimeMillis();
//                            TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                                    match1_NImgData, tgGetDevImageMatchNRes);
//                        }
//                    }
//                }
//            } else if (tgImgExtractFeatureVerifyNRes == 1) {
//                TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//                matchNMsg.arg1 = 4;
//                //传出抓取图片的数据
//                if (sImg) {
//                    TG661JBehindAPI.getTG661JBehindAPI().img(matchNMsg, match1_NImgData,
//                            tgGetDevImageMatchNRes);
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
//            } else if (tgImgExtractFeatureVerifyNRes == 2) {
//                TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//                matchNMsg.arg1 = 5;
//                //传出抓取图片的数据
//                if (sImg) {
//                    TG661JBehindAPI.getTG661JBehindAPI().img(matchNMsg, match1_NImgData,
//                            tgGetDevImageMatchNRes);
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
//            } else if (tgImgExtractFeatureVerifyNRes == 3) {
//                TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//                matchNMsg.arg1 = 6;
//                //传出抓取图片的数据
//                if (sImg) {
//                    TG661JBehindAPI.getTG661JBehindAPI().img(matchNMsg,
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
//            } else if (tgImgExtractFeatureVerifyNRes == 4) {
//                TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//                matchNMsg.arg1 = 7;
//            } else if (tgImgExtractFeatureVerifyNRes == 5) {
//                TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//                matchNMsg.arg1 = 8;
//                //传出抓取图片的数据
//                if (sImg) {
//                    TG661JBehindAPI.getTG661JBehindAPI().img(matchNMsg, match1_NImgData,
//                            tgGetDevImageMatchNRes);
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
//            } else if (tgImgExtractFeatureVerifyNRes == -1) {
//                TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//                matchNMsg.arg1 = 9;
//                //传出抓取图片的数据
//                if (sImg) {
//                    TG661JBehindAPI.getTG661JBehindAPI().img(matchNMsg, match1_NImgData,
//                            tgGetDevImageMatchNRes);
//                    //存储图片
//                    long l = System.currentTimeMillis();
//                    TG661JBehindAPI.getTG661JBehindAPI().saveImg(String.valueOf(l),
//                            match1_NImgData, tgGetDevImageMatchNRes);
//                }
//            }
//        } else if (tgGetDevImageMatchNRes == -1) {
//            TG661JBehindAPI.getTG661JBehindAPI().getAP().play_time_out();
//            matchNMsg.arg1 = -1;
//        } else if (tgGetDevImageMatchNRes == -2) {
//            TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//            matchNMsg.arg1 = -2;
//        } else if (tgGetDevImageMatchNRes == -3) {
//            TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//            matchNMsg.arg1 = -3;
//        } else if (tgGetDevImageMatchNRes == -4) {
//            TG661JBehindAPI.getTG661JBehindAPI().getAP().play_verifyFail();
//            matchNMsg.arg1 = -4;
//        }
//        handler.sendMessage(matchNMsg);
//    }
}
