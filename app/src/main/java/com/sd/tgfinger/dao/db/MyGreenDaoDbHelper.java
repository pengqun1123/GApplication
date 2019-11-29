package com.sd.tgfinger.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.database.Database;

/**
 * Created By pq
 * on 2019/9/17
 */
public class MyGreenDaoDbHelper extends DaoMaster.DevOpenHelper {

    public MyGreenDaoDbHelper(Context context, String name) {
        super(context, name);
    }

    public MyGreenDaoDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    @SuppressWarnings("all")
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        Logger.e("MyGreenDaoDbHelper", "----"
                + oldVersion + "---先前和更新之后的版本---" + newVersion + "----");
        //在子线程中执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (oldVersion < newVersion) {
                    Logger.e("MyGreenDaoDbHelper", "进行数据库升级");
                    new GreenDaoCompatibleUpdateHelper()
                            .setCallBack(
                                    new GreenDaoCompatibleUpdateHelper.GreenDaoCompatibleUpdateCallBack() {
                                        @Override
                                        public void onFinalSuccess() {
                                            Logger.e("MyGreenDaoDbHelper", "进行数据库升级 ===> 成功");
                                        }

                                        @Override
                                        public void onFailedLog(String errorMsg) {
                                            Logger.e("MyGreenDaoDbHelper", "升级失败日志 ===> " + errorMsg);
                                        }
                                    }
                            )
                            .compatibleUpdate(
                                    db,
                                    //这里写要升级的所有Dao类
                                    Finger6Dao.class
                            );
                    Logger.e("MyGreenDaoDbHelper", "进行数据库升级--完成");
                }
            }
        }).start();
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // 不要调用父类的，它默认是先删除全部表再创建
        // super.onUpgrade(db, oldVersion, newVersion);

    }


}
