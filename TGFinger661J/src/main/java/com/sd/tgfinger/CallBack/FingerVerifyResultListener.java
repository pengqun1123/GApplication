package com.sd.tgfinger.CallBack;

/**
 * Created By pq
 * on 2019/10/14
 */
public interface FingerVerifyResultListener {
    void fingerVerifyResult(int res, String msg, int score,
                            int index, Long fingerId,
                            byte[] updateFinger);
}
