package com.sd.tgfinger.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created By pq
 * on 2019/6/6
 */
public class ImgExechangeBMP {
    private int imageWidth = 500;
    private int imageHeight = 200;

    private static ImgExechangeBMP imgExechangeBMP = null;

    public static ImgExechangeBMP instance() {
        if (imgExechangeBMP == null) {
            synchronized (ImgExechangeBMP.class) {
                if (imgExechangeBMP == null)
                    imgExechangeBMP = new ImgExechangeBMP();
            }
        }
        return imgExechangeBMP;
    }

    public byte[] imgExechangeBMP(byte[] imgSource, int imgLength, /*int flag,*/ String imgSavePath) {
        decoderBMP(imgSource, imgLength, 1/*flag*/);
        byte[] showImgData = DataToSimpleBMP(imgSource, imgSavePath);
        return showImgData;
    }

    /**
     * BMP位图的文件头
     *
     * @param imgSource 去掉自定义头的源数据
     * @param imgLength 图片的长度
     * @param flag      标记  默认传1
     */
    private void decoderBMP(byte[] imgSource, int imgLength, int flag) {
        int i, j, temp1, temp2;
        byte[] key = {
                0x3F, (byte) 0xE2, 0x58, 0x26, (byte) 0xD9, 0x74, 0x40, 0x5B, (byte) 0xFE,
                (byte) 0x9F, 0x43, (byte) 0xE3, (byte) 0xF9, (byte) 0xA5, (byte) 0xDF, 0x07,
                (byte) 0xEF, (byte) 0x93, 0x2D, (byte) 0xD0, 0x05, (byte) 0xE3, (byte) 0x89,
                0x37, 0x7B, (byte) 0xD4, 0x2B, 0x54, 0x2D, (byte) 0xD4, (byte) 0xCD, (byte) 0xEA,
                (byte) 0x9A, 0x3F, 0x0D, 0x43, (byte) 0x95, 0x0C, 0x26, 0x60, (byte) 0xCF,
                (byte) 0xC0, 0x3B, (byte) 0xD6, 0x08, 0x64, 0x1C, (byte) 0xE3, (byte) 0x8E,
                0x6F, (byte) 0xFD, 0x59, 0x20, 0x43, 0x59, (byte) 0xCE, 0x42, 0x18, 0x21,
                0x3A, 0x5A, 0x64, 0x19, (byte) 0xB5, (byte) 0x9D, (byte) 0xA9, 0x1B,
                (byte) 0xF8, (byte) 0x8E, (byte) 0xFF, (byte) 0xAA, (byte) 0xB0, (byte) 0xEF,
                0x5E, (byte) 0xCC, (byte) 0x8F, 0x05, (byte) 0xB8, 0x4F, 0x6D, (byte) 0x9A,
                (byte) 0x8E, (byte) 0xCC, 0x1F, 0x5B, (byte) 0x84, 0x13, (byte) 0x8B, (byte) 0xC2,
                (byte) 0xD6, (byte) 0xB2, (byte) 0xA3, (byte) 0xD4, 0x6A, 0x60, 0x0B,
                0x4E, (byte) 0x8D, (byte) 0xE5, 0x09, (byte) 0x99, (byte) 0xC8, 0x11,
                (byte) 0x96, 0x20, (byte) 0xB7, (byte) 0x9C, (byte) 0xB9, 0x73, 0x40,
                (byte) 0xCE, (byte) 0xEC, 0x41, 0x2C, (byte) 0xC0, 0x1C, 0x3A, 0x2E,
                (byte) 0xCE, 0x6E, (byte) 0x8E, 0x11, (byte) 0xA9, (byte) 0xFD, 0x4A,
                0x00, (byte) 0xC6, 0x65
        };
        if (1 == flag) {
            temp1 = imgLength / 128;
            temp2 = imgLength % 128;
            for (i = 0; i < temp1; i++) {
                for (j = 0; j < 128; j++) {
                    imgSource[i * 128 + j] ^= key[j];
                }
            }
            for (j = 0; j < temp2; j++) {
                imgSource[i * 128 + j] ^= key[j];
            }
        }
    }

    private byte[] DataToSimpleBMP(byte[] imgData, String imgSavePath) {
        int i;
        byte color = 0;
        //byte[] end = {0, 0};
        byte[] patte = new byte[1024];
        patte[0] = 0;
        int pos = 0;
        byte[] header = {
                0x42, 0x4d, 0x30, 0x0C, 0x01, 0, 0, 0, 0, 0, 0x36,
                4, 0, 0, 0x28, 0, 0, 0, (byte) 0xF5, 0, 0, 0, 0x46, 0, 0,
                0, 0x01, 0, 8, 0, 0, 0, 0, 0, (byte) 0xF8, 0x0b, 0x01, 0,
                0x12, 0x0b, 0, 0, 0x12, 0x0b, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0
        };

        int size = imageWidth * imageHeight;
        int allsize = size + 1078;
        if (imgData == null || TextUtils.isEmpty(imgSavePath)) {
            return null;
        }

        header[2] = (byte) (allsize & 0xFF);
        header[3] = (byte) ((allsize >> 8) & 0xFF);
        header[4] = (byte) ((allsize >> 16) & 0xFF);
        header[5] = (byte) ((allsize >> 24) & 0xFF);

        header[18] = (byte) (imageWidth & 0xFF);
        header[19] = (byte) ((imageWidth >> 8) & 0xFF);
        header[20] = (byte) ((imageWidth >> 16) & 0xFF);
        header[21] = (byte) ((imageWidth >> 24) & 0xFF);

        header[22] = (byte) (imageHeight & 0xFF);
        header[23] = (byte) ((imageHeight >> 8) & 0xFF);
        header[24] = (byte) ((imageHeight >> 16) & 0xFF);
        header[25] = (byte) ((imageHeight >> 24) & 0xFF);
        //heard[22] = (-imageHeight) & 0xFF;    
        //heard[23] = (-imageHeight >> 8) & 0xFF;
        //heard[24] = (-imageHeight >> 16) & 0xFF;
        //heard[25] = (-imageHeight >> 24) & 0xFF;

        allsize -= 1078;
        header[34] = (byte) (allsize & 0xFF);
        header[35] = (byte) ((allsize >> 8) & 0xFF);
        header[36] = (byte) ((allsize >> 16) & 0xFF);
        header[37] = (byte) ((allsize >> 24) & 0xFF);

        for (i = 0; i < 1024; i += 4) {
            patte[pos++] = color;
            patte[pos++] = color;
            patte[pos++] = color;
            patte[pos++] = 0;
            color++;
        }

        byte[] imgDatas = new byte[1078 + imageWidth * imageHeight];
        System.arraycopy(header, 0, imgDatas, 0, header.length);
        System.arraycopy(patte, 0, imgDatas, 54, patte.length);
        System.arraycopy(imgData, 0, imgDatas, 1078, imgData.length);
        boolean imgSave = FileUtil.writeFile(imgDatas, imgSavePath);
        Log.i("===BMP","   BMP存储路径："+imgSavePath);
        return imgDatas;
    }
}
