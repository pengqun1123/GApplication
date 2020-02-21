package com.sd.tgfinger.tgApi.bigFeature;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

/**
 * 大特征算法
 * 
 */
public interface TG_Big_FV extends Library {

    TG_Big_FV TGFV_INSTANCE = Native.loadLibrary("TG_FV", TG_Big_FV.class);

    /**
     * Function:
     * 获取算法版本号：
     * （1）获取当前使用的算法版本号
     * Input：
     * <p>
     * Output：
     * （1）char *verInfo：获取到的算法版本号缓存区指针（缓存区 4 Bytes）
     * Return：
     * （1） 0：获取成功，Output数据有效
     * （2）-1：获取失败，因参数不合法，Output数据无效
     * Others：
     * （1）如果版本为1.0.0.1，则算法版本号为verInfo[0] = 1, verInfo[1] = 0, verInfo[2] = 0, verInfo[3] = 1;
     */
    int TGGetFVAPIVer(byte[] verInfo);

    /**
     * const char * 对应的Java数据类型是String
     * Function:
     * 初始化：
     * （1）传入许可证路径，初始化算法接口
     * Input：
     * （1）const char *licenseDatPath：许可证文件路径
     * Output：
     * <p>
     * Return：
     * （1）0：初始化成功,算法接口有效
     * （2）1: 初始化失败,因证书路径错误,算法接口无效
     * （3）2: 初始化失败,因证书内容无效,算法接口无效
     * （4）3: 初始化失败,因证书内容过期,算法接口无效
     * Others：
     * （1）该接口的使用必须传入许可证文件数据，圣点科技已提供限时使用的免费试用版许可证，
     * 其他版本的许可证请咨询圣点科技。
     * （2）该接口一般调用一次，即可初始化算法接口
     */
    int TGInitFVProcess(String licenseDatPath);

    /**
     * Function:
     * 特征提取[注册使用]：
     * （1）注册时,从加密指静脉"图像"中提取指静脉"特征"
     * Input：
     * （1）unsigned char *encryptImg：加密的指静脉"图像"数据缓存区指针(缓存区 imgWidth*imgHeight Bytes)
     * （2）int imgWidth：加密的指静脉"图像"宽度（像素）,默认值500
     * （3）int imgHeight：加密的指静脉"图像"高度（像素）,默认值200
     * Output：
     * （1）unsigned char *feature：从加密的指静脉"图像"中提取到的指静脉"特征"数据缓存区指针（缓存区 6016 Bytes）
     * Return：
     * （1） 0：特征提取成功,Output数据有效
     * （2） 1: 特征提取失败,因证书路径错误,Output数据无效
     * （3） 2: 特征提取失败,因证书内容无效,Output数据无效
     * （4） 3: 特征提取失败,因证书内容过期,Output数据无效
     * （5） 4：特征提取失败,因"图像"数据无效,Output数据无效
     * （6） 5：特征提取失败,因"图像"质量较差,Output数据无效
     * （7）-1: 特征提取失败,因参数不合法,Output数据无效
     * Others：
     * （1）加密的指静脉"图像"是指指静脉"图像"是加密过的。
     * （2）一般在录入手指的时候调用六次该函数，提取同一根手指的六幅"图像"的指静脉"特征"，
     * 提取的六个指静脉"特征"用于特征融合。
     * （3）一般在验证手指的时候调用一次该函数，提取一个指静脉"特征"，用于特征比对。
     * （4）设备型号UD670图像imgWidth值500,imgHeight值200；
     * 设备型号UD650,UP650,UE860图像imgWidth值320,imgHeight值140。
     */
    int TGImgExtractFeatureRegister(byte[] encryptImg, int imgWidth, int imgHeight, byte[] feature);

    /**
     * Function:
     * 特征提取[验证使用]：
     * （1）验证时,从加密指静脉"图像"中提取指静脉"特征"
     * Input：
     * （1）unsigned char *encryptImg：加密的指静脉"图像"数据缓存区指针(缓存区 208+imgWidth*imgHeight Bytes)
     * （2）int imgWidth：加密的指静脉"图像"宽度（像素）
     * （3）int imgHeight：加密的指静脉"图像"高度（像素）
     * Output：
     * （1）unsigned char *feature：从加密的指静脉"图像"中提取到的指静脉"特征"数据缓存区指针（缓存区 6016 Bytes）
     * Return：
     * （1） 0：特征提取成功,Output数据有效
     * （2） 1: 特征提取失败,因证书路径错误,Output数据无效
     * （3） 2: 特征提取失败,因证书内容无效,Output数据无效
     * （4） 3: 特征提取失败,因证书内容过期,Output数据无效
     * （5） 4：特征提取失败,因"图像"数据无效,Output数据无效
     * （6） 5：特征提取失败,因"图像"质量较差,Output数据无效
     * （7）-1: 特征提取失败,因参数不合法,Output数据无效
     * Others：
     * （1）加密的指静脉"图像"是指指静脉"图像"是加密过的。
     * （2）一般在验证手指的时候调用一次该函数，提取一个指静脉"特征"，用于特征比对。
     * （3）设备型号UD670图像imgWidth值500,imgHeight值200；
     * 设备型号UD650,UP650,UE860图像imgWidth值320,imgHeight值140。
     */
    int TGImgExtractFeatureVerify(byte[] encryptImg, int imgWidth, int imgHeight, byte[] feature);

    /**
     * Function:
     * 特征融合：
     * （1）将同一根手指的六个"特征"数据，融合成该手指的"模板"数据
     * Input：
     * （1）unsigned char *feature：待融合的"特征"数据缓存区指针（缓存区 6016 * featureSize Bytes）
     * （2）int featureSize：待融合的"特征"数据个数,默认值6
     * Output：
     * （1）unsigned char *tmpl："特征"融合成的"模板"数据缓存区指针（缓存区35008 Bytes）
     * Return：
     * （1） 0：特征融合成功，Output数据有效
     * （2） 6：特征融合失败，因"特征"数据一致性差，Output数据无效
     * （3）-1: 特征融合失败，因参数不合法,Output数据无效
     * Others：
     * （1）一般在录入手指的时候调用一次该函数，将指定的指静脉"特征"融合成"模板"，一般存放于数据库中。
     * （2）融合的"模板"需解析成"比对模板"，用于特征比对
     */
    int TGFeaturesFusionTmpl(byte[] features, int featureSize, byte[] tmpl);

    /**
     * Function:
     * 模板解析为比对模板：
     * （1）将"模板"解析为"比对模板"
     * Input：
     * （1）unsigned char* tmpl："模板"缓存区指针（缓存区 35008 Bytes）/3特征 17408
     * Output：
     * （1）unsigned char* matchTmpl："比对模板"缓存区指针（缓存区34784 Bytes）
     * Return：
     * （1） 0：模板解析成功， Output数据有效
     * （2）-1：模板解析失败，因参数不合法，Output数据无效
     * Others：
     * （1）将"模板"解析成"比对模板"，"比对模板"用于特征比对。
     */
    int TGTmplToMatchTmpl(byte[] tmpl, byte[] matchTmpl);

    /**
     * Function:
     * 特征比对（1:1）：
     * （1）将一个"特征"与一个"比对模板"进行比对
     * Input：
     * （1）unsigned char *feature：待比对的"特征"缓存区指针（缓存区 6016 Bytes）
     * （2）unsigned char* matchTmpl：待比对的"比对模板"缓存区指针（缓存区 34784 Bytes）
     * Output：
     * （1）unsigned char* updateTmpl：比对成功后，与之相匹配的模板的更新"模板"数据（缓存区 35008 Bytes）
     * （2）int* matchScore: 比对结束后，比对的分数(缓存区 1*sizeof(int) Bytes)
     * Return：
     * （1） 0：特征比对（1:1）成功，Output数据有效
     * （2） 7: 特征比对（1:1）失败，因比对失败,仅Output的matchScore数据有效
     * （3）-1: 特征比对（1:1）失败，因参数不合法,Output数据无效
     * Others：
     * （1）一般该接口适合1:1的特征比对，不建议循环调用该接口来实现1：N的特征比对
     * （2）unsigned char *updateTmpl中的数据，一般在比对成功后，覆盖数据库中原来的"模板"数据
     * （3）int* matchScore，默认判决分数阈值为60，当分数大于等于60，小于等于100时，比对成功；
     * 当分数大于等于0，小于60时，比对失败；另外，该参数仅作参考
     */
    int TGFeatureMatchTmpl11(byte[] feature, byte[] matchTmpl, byte[] updateTmpl, IntByReference matchScore);

    /**
     * Function:
     * 特征比对（1：N）：算法V3.0.0.5去掉了UUID这个参数
     * （1）将一个"特征"与地址连续的多个"比对模板"进行比对，从而在地址连续的多个"比对模板"中，找到与之相匹配的"比对模板"
     * Input：
     * （1）unsigned char *feature：待比对的"特征"数据缓存区指针（缓存区 6016 Bytes）
     * （2）unsigned char* matchTmplStart：地址连续的的多个"比对模板"数据缓存区指针（缓存区 tmplNum*34784 Bytes）
     * （3）int matchTmplNum：地址连续的多个"比对模板"的个数
     * Output：
     * （1）int *matchIndex：在特征比对中，找到与之相匹配的"比对模板"，其对应位置的变量指针(缓存区 1*sizeof(int) Bytes)
     * （2）unsigned char* matchUUID:在特征比对中，找到与之相匹配的"比对模板"，其对应UUID的变量指针(缓存区 33*sizeof(int) Bytes)
     * （3）int* matchScore: 在特征比对中，找到与之相匹配的"比对模板"，其对应比对分数的变量指针(缓存区 1*sizeof(int) Bytes)
     * （4）unsigned char* updateTmpl：在特征比对中，找到与之相匹配的"比对模板"，其对应更新"模板"数据的变量指针（缓存区 35008 Bytes）
     * Return：
     * （1） 0：特征比对（1：N）成功，Output数据有效
     * （2） 8：特征比对（1：N）失败，仅Output的matchScore数据有效
     * （3）-1：特征比对（1：N）失败，因参数不合法，Output数据无效
     * Others：
     * （1）一般在验证手指的时候调用一次该函数，若比对成功，则查看 int *matchIndex
     * （2）地址连续的多个"比对模板"数据是指将数据库中解析的所有"比对模板"数据连接在一起，是在一块连续的
     * 地址中连续存放"比对模板"1数据_"比对模板"数据...
     * （3）如果匹配到的是"比对模板"1，则*matchIndex为1；
     * 如果匹配到的是"比对模板"2，则*matchIndex为2，以此类推
     * （4）unsigned char* matchUUID:前32数据对应UUID的32个字符
     * （5）int* matchScore:默认判决分数阈值为60，当分数大于等于60，小于等于100时，比对成功；
     * 当分数大于等于0，小于60时，比对失败；另外，该参数仅作参考
     * （6）unsigned char *updateTmpl:一般在比对成功后，覆盖数据库中原来的"模板"数据
     */
    int TGFeatureMatchTmpl1N(byte[] feature, byte[] matchTmplStart, int matchTmplNum,
                             IntByReference matchIndex, byte[] matchUUID,
                             IntByReference matchScore, byte[] updateTmpl);

    /**
     * Function:
     * 获取模板的算法版本号：
     * （1）从"模板"里面获取该"模板"生成时对应的算法版本号
     * Input：
     * （1）unsigned char* tmpl："模板"数据缓存区指针(缓存区 17632 Bytes)
     * Output：
     * （1）char * verInfo：从"模板"获取到的算法版本号缓存区指针（缓存区 5 Bytes）
     * Return：
     * （1） 0：获取成功， Output数据有效
     * （2）-1：获取失败，参数错误，Output数据无效
     * Others：
     * （1）如果版本为VA.B.C.D，则获取"ABCD"，算法版本号为字符串形式，共4个有效字符
     */
    int TGGetAPIVerFromTmpl(byte[] tmpl, byte[] verInfo);

    /**
     * Function:
     * 获取模板的SN号：
     * （1）从"模板"里面获取该"模板"生成时对应的设备SN号
     * Input：
     * （1）unsigned char* tmpl："模板"数据缓存区指针(缓存区 17632 Bytes)
     * Output：
     * （1）char * SNInfo：从"模板"获取到的设备SN号缓存区指针（缓存区 17 Bytes）
     * Return：
     * （1） 0：获取成功， Output数据有效
     * （2）-1：获取失败，参数错误，Output数据无效
     * Others：
     * （1）如果版本为XXXXXXXXXXXXXXXX，则获取"XXXXXXXXXXXXXXXX"，设备SN号为字符串形式，共16个有效字符
     */
    int TGGetSNFromTmpl(byte[] tmpl, byte[] SNInfo);

    /**
     * Function:
     * 获取模板的FW号：
     * （1）从"模板"里面获取该"模板"生成时对应的设备FW号
     * Input：
     * （1）unsigned char* tmpl："模板"数据缓存区指针(缓存区 17632 Bytes)
     * Output：
     * （1）char * FWInfo：从"模板"获取到的设备FW号缓存区指针（缓存区 17 Bytes）
     * Return：
     * （1） 0：获取成功， Output数据有效
     * （2）-1：获取失败，参数错误，Output数据无效
     * Others：
     * （1）如果版本为XXXXXXXXXXXXXXXX，则获取"XXXXXXXXXXXXXXXX"，设备FW号为字符串形式，共16个有效字符
     */
    int TGGetFWFromTmpl(byte[] tmpl, byte[] FWInfo);

    /**
     * Function:
     * 获取模板的时间：
     * （1）从"模板"里面获取该"模板"生成时对应的时间
     * Input：
     * （1）unsigned char* tmpl："模板"数据缓存区指针(缓存区 17632 Bytes)
     * Output：
     * （1）char * timeInfo：从"模板"获取到的时间数据缓存区指针（缓存区 15 Bytes）
     * Return：
     * （1） 0：获取成功， Output数据有效
     * （2）-1：获取失败，参数错误，Output数据无效
     * Others：
     * （1）如果时间为2018-04-19 15:20:32，则获取"20180419152032"，时间为字符串形式，共14个有效字符
     */
    int TGGetTimeFromTmpl(byte[] tmpl, byte[] timeInfo);
}
