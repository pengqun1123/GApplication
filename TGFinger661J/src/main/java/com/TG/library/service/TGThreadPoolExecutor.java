package com.TG.library.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created By pq
 * on 2019/5/28
 * 自定义线程池
 */
public class TGThreadPoolExecutor extends ThreadPoolExecutor {

    public TGThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                long keepAliveTime, TimeUnit unit,
                                BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * 线程任务执行前执行该方法
     *
     * @param t
     * @param r
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);

    }

    /**
     * 线程任务执行后执行该方法
     *
     * @param r
     * @param t
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

    }

    /**
     * 线程池关闭后执行该方法
     */
    @Override
    protected void terminated() {
        super.terminated();

    }
}
