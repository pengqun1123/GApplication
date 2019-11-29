package com.sd.tgfinger.utils;

import com.sd.tgfinger.pojo.Finger6;

import java.util.ArrayList;

/**
 * Created by wangyu on 2019/10/18.
 */

public class FingerListManager {

    private static ArrayList<Finger6> fingerList;

    public static FingerListManager getInstance() {
        return Holder.INSTANCE;
    }

    private FingerListManager() {
        fingerList = new ArrayList<>();
    }

    private static class Holder {
        private static final FingerListManager INSTANCE = new FingerListManager();
    }

    public ArrayList<Finger6> getFingerData() {
        return fingerList;
    }

    public void addFingerDataList(ArrayList<Finger6> newFingerList) {
        if (newFingerList != null && newFingerList.size() > 0)
            fingerList.addAll(newFingerList);
    }

    public void addFingerData(Finger6 finger6) {
        fingerList.add(finger6);
    }

    public void removeFingerData(int position) {
        fingerList.remove(position);
    }

    /**
     * Finger完全相等的时候调用
     *
     * @param finger6 指静脉数据
     */
    public void removeFinger(Finger6 finger6) {
        if (fingerList.size() > 0) {
            fingerList.remove(finger6);
        }
    }

    /**
     * 指静脉Id相等的时候调用
     *
     * @param finger 指静脉
     */
    public boolean removeFingerById(Finger6 finger) {
        boolean isEq = false;
        Finger6 removeFinger = null;
        for (Finger6 finger6 : fingerList) {
            Long uId = finger6.getUId();
            if (uId.equals(finger.getUId())) {
                isEq = true;
                removeFinger = finger6;
                break;
            }
        }
        if (isEq)
            fingerList.remove(removeFinger);
        return isEq;
    }

    /**
     * 覆盖指静脉ID相等的数据
     *
     * @param finger 指静脉数据
     */
    public boolean coverFinger(Finger6 finger) {
        boolean b = removeFingerById(finger);
        if (b)
            addFingerData(finger);
        return b;
    }

    public void clearFingerData() {
        fingerList.clear();
    }


}
