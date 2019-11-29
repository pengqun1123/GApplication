package com.sd.tgfinger.dao.db;

import java.util.List;

/**
 * Created By pq
 * on 2019/9/17
 */
public interface DbCallBack<T> {

    void onSuccess(T result);

    void onSuccess(List<T> result);

    void onFailed();

    void onNotification(boolean result);
}
